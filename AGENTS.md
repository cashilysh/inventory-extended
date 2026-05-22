# AGENTS.md

## Build

```
./gradlew build
./gradlew clean build
./gradlew build -PtargetVersion=26.1            # target a specific MC version
./gradlew runclient                              # launch client after build
```

- **Java 25 required** (build fails otherwise). Gradle 9.5.1 via wrapper.
- Dependencies are resolved **at config-time from live APIs** (meta.fabricmc.net, api.modrinth.com). The build needs internet.

## Architecture

- **Fabric mod** using Fabric Loom (`1.16-SNAPSHOT`). Entrypoint: `inventoryextended.InventoryExtended` (implements `ModInitializer`).
- **All logic is in mixins** (13 total in `src/main/java/inventoryextended/mixin/`). The entrypoint class just logs init. Mixin config at `src/main/resources/inventoryextended.mixins.json`.
- Expands inventory from 36 to 63 slots by modifying constants/slot indices via Mixin.

## Key gotchas

- **`fabric.mod.json` is auto-generated** at build time by `editFabricModJson` task. Do not manually edit version/depends fields; they get overwritten from `gradle.properties` and live API results.
- **Version-specific overrides**: `src/version_specific/` contains Java sources that override `src/main/java/` files for specific MC version ranges (folders like `26.1_26.1.2`). The `applyVersionSpecificOverrides` task copies them in before compile and the `buildFinished` hook restores originals. Files in `src/main/java/` are temporarily mutated during build.
- **MC 26.x is unobfuscated** — the output task is `jar`, not `remapJar` (Loom skips remapping).
- Dependencies are declared as **Modrinth slugs** in `gradle.properties` under `deps.implementation` / `deps.compileOnly`, not as standard Maven coordinates.
- No tests exist in this project.

## Conventions

- Mixin class names are descriptive CamelCase (e.g. `ExtendPlayerInventory`, `RemapPlayerSlots`).
- Shell scripts (`BUILD ALL.sh`, etc.) contain `read -p` prompts and will hang waiting for Enter.
- `./gradlew clean` also triggers file restoration of version-specific overrides (via `buildFinished`).
