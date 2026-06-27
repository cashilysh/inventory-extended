#!/usr/bin/env bash
set -uo pipefail

export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-25-openjdk}"

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

LOG_FILE="$SCRIPT_DIR/test_results.log"
TEMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TEMP_DIR"' EXIT

GRADLE_EXTRA_ARGS=""
for arg in "$@"; do
    case "$arg" in
        -P*) GRADLE_EXTRA_ARGS="$GRADLE_EXTRA_ARGS $arg" ;;
    esac
done

MODE="${1:-all}"
# strip mode from args if it's not a -P flag
if [[ "$MODE" != -* ]]; then
    shift 2>/dev/null || true
fi

GRADLE_CMD="./gradlew $GRADLE_EXTRA_ARGS --no-daemon"

> "$LOG_FILE"

log() {
    echo "$@"
    echo "$@" >> "$LOG_FILE"
}

log_separator() {
    local msg="════════════════════════════════════════════════════════════════"
    echo ""
    echo "  $*"
    echo "  $msg"
    echo ""
    {
        echo ""
        echo "  $*"
        echo "  $msg"
        echo ""
    } >> "$LOG_FILE"
}

log_test_result() {
    local status="$1" name="$2" extra="$3"
    if [ "$status" = "PASSED" ]; then
        printf "  [\033[32mPASSED\033[0m]   %s\n" "$name"
        printf "  [PASSED]   %s\n" "$name" >> "$LOG_FILE"
    else
        printf "  [\033[31mFAILED\033[0m]   %s\n" "$name"
        printf "  [FAILED]   %s\n" "$name" >> "$LOG_FILE"
        if [ -n "$extra" ]; then
            printf "           \033[31m→ %s\033[0m\n" "$extra"
            printf "           → %s\n" "$extra" >> "$LOG_FILE"
        fi
    fi
}

log_section_header() {
    local title="$*"
    echo ""
    printf "╔══════════════════════════════════════════════════════════════╗\n"
    printf "║  %-58s ║\n" "$title"
    printf "╚══════════════════════════════════════════════════════════════╝\n"
    echo ""
    {
        echo ""
        printf "╔══════════════════════════════════════════════════════════════╗\n"
        printf "║  %-58s ║\n" "$title"
        printf "╚══════════════════════════════════════════════════════════════╝\n"
        echo ""
    } >> "$LOG_FILE"
}

# ──────────────────────────────────────────────────────────────────
# UNIT TESTS
# ──────────────────────────────────────────────────────────────────
run_unit_tests() {
    log_section_header "STEP 1: Unit Tests (Fabric Loader JUnit)"

    local unit_status="PASSED"

    $GRADLE_CMD test 2>"$TEMP_DIR/unit-raw.log" >"$TEMP_DIR/unit-stdout.log"
    local gradle_rc=$?

    {
        echo "  Gradle output (last 10 lines):"
        tail -10 "$TEMP_DIR/unit-stdout.log" | sed 's/^/    /'
    }
    {
        echo "  Gradle output (last 10 lines):"
        tail -10 "$TEMP_DIR/unit-stdout.log" | sed 's/^/    /'
    } >> "$LOG_FILE"

    local xml_dir="build/test-results/test"
    if [ -d "$xml_dir" ]; then
        for xml in "$xml_dir"/TEST-*.xml; do
            [ -f "$xml" ] || continue

            python3 -c "
import xml.etree.ElementTree as ET
import sys
try:
    tree = ET.parse('$xml')
    root = tree.getroot()
    suite_name = root.get('name', 'Unknown')
    for tc in root.findall('testcase'):
        name = tc.get('name', '?')
        cls = tc.get('classname', '?')
        full = cls + '.' + name
        failure = tc.find('failure')
        error = tc.find('error')
        if failure is not None:
            msg = (failure.get('message','') or failure.text or '')[:120].strip()
            print(f'FAILED|{full}|{msg}')
        elif error is not None:
            msg = (error.get('message','') or error.text or '')[:120].strip()
            print(f'FAILED|{full}|{msg}')
        else:
            print(f'PASSED|{full}|')
except Exception as e:
    print(f'PARSE_ERROR|{e}|', file=sys.stderr)
" 2>/dev/null | while IFS='|' read -r status name extra; do
                if [ "$status" = "FAILED" ]; then
                    unit_status="FAILED"
                fi
                log_test_result "$status" "$name" "$extra"
            done
        done
    else
        log_test_result "FAILED" "Unit tests (no XML report found)" "Gradle exit code: $gradle_rc"
        unit_status="FAILED"
    fi

    if [ "$unit_status" = "PASSED" ]; then
        echo ""
        printf "  \033[32mAll unit tests passed\033[0m\n"
        { echo ""; echo "  All unit tests passed"; } >> "$LOG_FILE"
    else
        echo ""
        printf "  \033[31mUnit tests FAILED\033[0m\n"
        { echo ""; echo "  Unit tests FAILED"; } >> "$LOG_FILE"
    fi

    [ "$unit_status" = "PASSED" ]
    return $?
}

# ──────────────────────────────────────────────────────────────────
# SERVER GAME TESTS
# ──────────────────────────────────────────────────────────────────

extract_gametest_names() {
    python3 -c "
import json, os, re

try:
    with open('src/gametest/resources/fabric.mod.json') as f:
        data = json.load(f)
    classes = data.get('entrypoints',{}).get('fabric-gametest',[])
except Exception:
    classes = []

for fqcn in classes:
    rel = fqcn.replace('.','/') + '.java'
    path = 'src/gametest/java/' + rel
    if not os.path.isfile(path):
        continue
    with open(path) as f:
        content = f.read()
    for m in re.finditer(r'@GameTest\b[^)]*?\n\s*public\s+void\s+(\w+)\s*\(', content, re.DOTALL):
        method = m.group(1)
        short_class = fqcn.split('.')[-1]
        print(f'{short_class}::{method}')
" 2>/dev/null
}

run_server_tests() {
    log_section_header "STEP 2: Server Game Tests (Minecraft Gametest)"

    local server_status="PASSED"

    local test_names_file="$TEMP_DIR/server-test-names.txt"
    extract_gametest_names > "$test_names_file"
    mapfile -t TEST_NAMES < "$test_names_file"

    $GRADLE_CMD runGameTest 2>"$TEMP_DIR/server-raw.log" >"$TEMP_DIR/server-stdout.log"
    local gradle_rc=$?

    local output
    output=$(cat "$TEMP_DIR/server-stdout.log")

    local test_count=0
    local grid=""
    local failures=""

    test_count=$(echo "$output" | grep -oP 'Running test.*batch 0 \(\K\d+' | tail -1)
    grid=$(echo "$output" | grep -oP '\[[+X.]+\]' | tail -1 | tr -d '[]')
    local failure_lines
    mapfile -t failure_lines < <(echo "$output" | grep -oP '^\s*-\s+\S+:\s+\S+:\s+.*$' | sed 's/^\s*- //')

    log "  Gradle exit code: $gradle_rc"
    log ""

    if [ -z "$grid" ] && [ "$test_count" -gt 0 ]; then
        log_test_result "FAILED" "Server game tests (no grid found)" "Check server-gametest-output.log"
        server_status="FAILED"
        return 1
    fi

    if [ -z "$test_count" ] || [ "$test_count" -eq 0 ]; then
        log_test_result "FAILED" "Server game tests (0 tests detected)" "Check server-gametest-output.log"
        server_status="FAILED"
        return 1
    fi

    local i=0
    while [ $i -lt "${#grid}" ]; do
        ch="${grid:$i:1}"

        local name="Server-test[$i]"
        if [ $i -lt "${#TEST_NAMES[@]}" ]; then
            name="${TEST_NAMES[$i]}"
        fi

        if [ "$ch" = "X" ]; then
            server_status="FAILED"
            local fail_msg=""
            if [ $i -lt "${#failure_lines[@]}" ]; then
                fail_msg="${failure_lines[$i]}"
            fi
            if [ -z "$fail_msg" ]; then
                fail_msg="[no error detail found]"
            fi
            log_test_result "FAILED" "$name" "$fail_msg"
        elif [ "$ch" = "+" ]; then
            log_test_result "PASSED" "$name" ""
        else
            log_test_result "FAILED" "$name" "unexpected result char '$ch'"
            server_status="FAILED"
        fi
        i=$((i + 1))
    done

    if [ "$server_status" = "PASSED" ]; then
        echo ""
        printf "  \033[32mAll %s server game tests passed\033[0m\n" "$test_count"
        { echo ""; echo "  All $test_count server game tests passed"; } >> "$LOG_FILE"
    else
        echo ""
        printf "  \033[31mServer game tests FAILED\033[0m\n"
        { echo ""; echo "  Server game tests FAILED"; } >> "$LOG_FILE"
    fi

    [ "$server_status" = "PASSED" ]
    return $?
}

# ──────────────────────────────────────────────────────────────────
# CLIENT GAME TESTS
# ──────────────────────────────────────────────────────────────────
run_client_tests() {
    log_section_header "STEP 3: Client Game Tests (Fabric Client Gametest)"

    local client_status="PASSED"

    $GRADLE_CMD runClientGameTest 2>"$TEMP_DIR/client-raw.log" >"$TEMP_DIR/client-stdout.log"
    local gradle_rc=$?

    local output
    output=$(cat "$TEMP_DIR/client-stdout.log")

    log "  Gradle exit code: $gradle_rc"

    if [ "$gradle_rc" -eq 0 ]; then
        local client_names
        client_names=$(echo "$output" | grep -oP '\[fabric-client-gametest-api-v1\].*?Running test.*?: \K.*' | tail -20)

        if [ -z "$client_names" ]; then
            client_names=$(echo "$output" | grep -i "inventoryextended-" | grep -oP '\S+' | head -10)
        fi

        local test_list
        test_list=$(python3 -c "
import json, sys
try:
    with open('src/gametest/resources/fabric.mod.json') as f:
        data = json.load(f)
    for t in data.get('entrypoints',{}).get('fabric-client-gametest',[]):
        print(t.split('.')[-1])
except Exception as e:
    print(f'PARSE_ERROR|{e}', file=sys.stderr)
" 2>/dev/null)

        if [ -n "$test_list" ]; then
            while IFS= read -r tname; do
                [ -z "$tname" ] && continue
                log_test_result "PASSED" "$tname" ""
            done <<< "$test_list"
        else
            log_test_result "PASSED" "Client game test" ""
        fi
    else
        log_test_result "FAILED" "Client game tests" "Gradle exited with code $gradle_rc"
        client_status="FAILED"
    fi

    log ""
    if [ "$client_status" = "PASSED" ]; then
        log "  Client game tests passed"
    else
        printf "  \033[31mClient game tests FAILED\033[0m\n"
        echo "  Client game tests FAILED" >> "$LOG_FILE"
        echo "$output" | grep -iE "error|fail|crash" | head -15 | while read -r line; do
            echo "    $line"
            echo "    $line" >> "$LOG_FILE"
        done
    fi

    [ "$client_status" = "PASSED" ]
    return $?
}

# ──────────────────────────────────────────────────────────────────
# MAIN
# ──────────────────────────────────────────────────────────────────

OVERALL_EXIT=0

init_log() {
    {
        echo "INVENTORY-EXTENDED TEST SUITE — $(date '+%Y-%m-%d %H:%M:%S')"
        echo "JAVA_HOME=${JAVA_HOME}"
        echo ""
    } > "$LOG_FILE"
}

init_once() {
    [ -s "$LOG_FILE" ] || init_log
}

do_unit() {
    init_once
    run_unit_tests || OVERALL_EXIT=1
}

do_server() {
    init_once
    run_server_tests || OVERALL_EXIT=1
}

do_client() {
    init_once
    run_client_tests || OVERALL_EXIT=1
}

case "$MODE" in
    unit)   do_unit ;;
    server) do_server ;;
    client) do_client ;;
    all)    do_unit; do_server; do_client ;;
    *)
        echo "Usage: $0 [unit|server|client|all]"
        echo ""
        echo "  unit    - Run unit tests (Fabric Loader JUnit)"
        echo "  server  - Run server game tests"
        echo "  client  - Run client game tests"
        echo "  all     - Run all tests (default)"
        exit 1
        ;;
esac

echo ""
echo "================================================================================"
if [ "$OVERALL_EXIT" -eq 0 ]; then
    printf "  \033[32mALL TESTS PASSED\033[0m\n"
else
    printf "  \033[31mSOME TESTS FAILED — check test_results.log for details\033[0m\n"
fi
echo "  Log saved to: $LOG_FILE"
echo "================================================================================"

{
    echo ""
    echo "================================================================================"
    if [ "$OVERALL_EXIT" -eq 0 ]; then
        echo "  ALL TESTS PASSED"
    else
        echo "  SOME TESTS FAILED — check test_results.log for details"
    fi
    echo "  Log saved to: $LOG_FILE"
    echo "================================================================================"
} >> "$LOG_FILE"

exit "$OVERALL_EXIT"
