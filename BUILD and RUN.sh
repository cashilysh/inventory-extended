#!/bin/bash
#USER

# Set the window title
export FOLDER_NAME=${PWD##*/}
echo -ne "\033]0;$FOLDER_NAME\007"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_ALL=$(find "$SCRIPT_DIR" -maxdepth 1 -iname "*build*all*.sh" | head -1)

# Parse versions from BUILD_ALL.sh (lines matching: build_version "...")
mapfile -t VERSIONS < <(grep -oP 'build_version\s+"\K[^"]+' "$BUILD_ALL")

if [ -z "$BUILD_ALL" ] || [ ${#VERSIONS[@]} -eq 0 ]; then
    echo "No versions found in BUILD_ALL.sh, falling back to latest."
    SELECTED=""
else
    # The last version in the list is treated as "latest"
    LATEST="${VERSIONS[-1]}"
    SELECTED="$LATEST"

    # Terminal interaction setup
    tput_available=false
    command -v tput &>/dev/null && tput_available=true

    hide_cursor()  { $tput_available && tput civis; }
    show_cursor()  { $tput_available && tput cnorm; }
    clear_menu()   {
        # Move up (VERSIONS + 2 header lines + 1 timer line) and clear
        local lines=$(( ${#VERSIONS[@]} + 3 ))
        for ((i=0; i<lines; i++)); do
            tput cuu1 2>/dev/null && tput el 2>/dev/null
        done
    }

    draw_menu() {
        local current=$1
        local remaining=$2
        echo
        echo "Select Minecraft version (↑/↓, Enter to confirm):"
        for i in "${!VERSIONS[@]}"; do
            if [ "$i" -eq "$current" ]; then
                printf "  \033[1;32m▶ %s\033[0m\n" "${VERSIONS[$i]}"
            else
                printf "    %s\n" "${VERSIONS[$i]}"
            fi
        done
        if [ -n "$remaining" ]; then
            printf "  \033[2mContinuing with \033[0;32m%s\033[2m in %d s (any key cancels timer)...\033[0m\n" "$LATEST" "$remaining"
        else
            printf "  \033[2m(timer cancelled)\033[0m\n"
        fi
    }

    # Start with the last (latest) version highlighted
    current=$(( ${#VERSIONS[@]} - 1 ))
    timer_active=true
    remaining=2

    hide_cursor
    # Trap to restore cursor on exit
    trap 'show_cursor; stty echo icanon 2>/dev/null' EXIT INT TERM

    # Raw input mode
    stty -echo -icanon min 0 time 0 2>/dev/null

    draw_menu "$current" "$remaining"

    TICK=0.1          # poll interval in seconds
    ticks_per_sec=10
    tick_count=0

    while true; do
        # Read up to 3 bytes (handles escape sequences)
        IFS= read -r -s -N1 -t "$TICK" key 2>/dev/null
        got_key=$?

        if [ $got_key -eq 0 ]; then
            # Got a character — read rest of escape sequence if any
            if [ "$key" = $'\x1b' ]; then
                IFS= read -r -s -N1 -t 0.05 k2 2>/dev/null
                IFS= read -r -s -N1 -t 0.05 k3 2>/dev/null
                key="${key}${k2}${k3}"
            fi

            case "$key" in
                $'\x1b[A'|$'\x1b[D')   # Up / Left
                    timer_active=false
                    (( current > 0 )) && (( current-- ))
                    ;;
                $'\x1b[B'|$'\x1b[C')   # Down / Right
                    timer_active=false
                    (( current < ${#VERSIONS[@]} - 1 )) && (( current++ ))
                    ;;
                $'\x0a'|$'\x0d'|'')    # Enter
                    SELECTED="${VERSIONS[$current]}"
                    clear_menu
                    break
                    ;;
                $'\x03')               # Ctrl-C
                    show_cursor
                    stty echo icanon 2>/dev/null
                    echo
                    exit 1
                    ;;
                *)
                    # Any other key cancels the timer
                    timer_active=false
                    ;;
            esac

            clear_menu
            if $timer_active; then
                draw_menu "$current" "$remaining"
            else
                draw_menu "$current" ""
            fi
            tick_count=0

        else
            # Timeout — advance timer if active
            if $timer_active; then
                (( tick_count++ ))
                if (( tick_count >= ticks_per_sec )); then
                    tick_count=0
                    (( remaining-- ))
                    clear_menu
                    if (( remaining <= 0 )); then
                        SELECTED="$LATEST"
                        draw_menu "$current" ""
                        break
                    fi
                    draw_menu "$current" "$remaining"
                fi
            fi
        fi
    done

    show_cursor
    stty echo icanon 2>/dev/null
fi

echo
echo "Starting build for Minecraft ${SELECTED:-latest}..."
if [ -n "$SELECTED" ]; then
    ./gradlew build -PtargetVersion="$SELECTED"
else
    ./gradlew build
fi

if [ $? -ne 0 ]; then
    echo
    echo "Build failed! Check the error messages above."
    read -p "Press Enter to continue..."
    exit 1
fi

./gradlew runclient
