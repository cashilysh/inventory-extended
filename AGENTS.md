# AGENTS.md

This workspace contains two independent Minecraft Fabric mods:
- `inventory-extended-legacy/` — targets MC 1.21.x (obfuscated, Yarn mappings)
- `inventory-extended-neo/` — targets MC 26.x (unobfuscated, Mojang mappings)

**Do not assume commands or conventions apply across both projects.** Each has its own Gradle wrapper, Java requirement, Loom plugin version, and build quirks. Read the respective AGENTS.md / build.gradle before touching either.

## Quick comparison

| Aspect | Legacy | Neo |
|--------|--------|-----|
| MC versions | 1.21.6–1.21.11 | 26.x |
| Java | **21** | **25** |
| Gradle | 9.3 | 9.5.1 |
| Loom | 1.15-SNAPSHOT | 1.16-SNAPSHOT |
| Mappings | **Yarn** (obfuscated) | **Mojang** (unobfuscated) |
| Output task | `remapJar` | `jar` |
| Mixin count | **31** | 30 |
| Deps source | `version_mappings.json` (Linkie API) | `gradle.properties` (Modrinth slugs) |
| AGENTS.md | This file | Comprehensive (read it) |

## Project-specific instructions

### inventory-extended-neo

See `inventory-extended-neo/AGENTS.md` for full build commands, architecture, mixin catalog, and creative inventory troubleshooting.

Notable:
- `fabric.mod.json` is auto-generated — do not edit manually
- Shell scripts (`BUILD ALL.sh`, etc.) have `read -p` prompts and hang unattended
- No tests exist

### inventory-extended-legacy

## Build

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew build -PtargetVersion=1.21.11
./gradlew runclient -PtargetVersion=1.21.11
```

- **Java 21 required** (hard check in build.gradle). Java 25 will be rejected.
- `version_mappings.json` auto-downloads from Linkie API if missing at build time.
- `./gradlew updateversionmappings` refreshes it manually.
- `fabric.mod.json` is mutated during build (depends section rewritten) then restored by `buildFinished`.
- `editFabricModJson` task runs at config time and on build finish — do not edit `fabric.mod.json` manually.
- Version-specific overrides exist at `src/version_specific/` (currently empty). Infrastructure in `build.gradle` handles this.
- Shell scripts (`BUILD ALL.sh`, etc.) have `read -p` prompts — don't run unattended.
- No tests, no CI, no lint/typecheck.

## Decompiling Minecraft sources for verification

```bash
# Requires Java 21 and ~8GB heap
JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  GRADLE_OPTS="-Xmx8192m" \
  ./gradlew genSources -PtargetVersion=1.21.11 --no-daemon
```

Generated sources JAR lives at:
```
.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-<hash>/
  1.21.11-net.fabricmc.yarn.1_21_11.1.21.11+build.4-v2/
  minecraft-merged-<hash>-1.21.11-net.fabricmc.yarn.1_21_11.1.21.11+build.4-v2-sources.jar
```

Extract and search:
```bash
jar xf <sources.jar> net/minecraft/screen/CraftingScreenHandler.java
grep -A 50 "quickMove" net/minecraft/screen/CraftingScreenHandler.java
```

## Architecture

- **Fabric mod** using Fabric Loom 1.15-SNAPSHOT. Entrypoint: `inventoryextended.InventoryExtended`.
- **All logic in mixins** (31 total in `src/main/java/inventoryextended/mixin/`). Mixin config at `src/main/resources/inventoryextended.mixins.json`.
- Expands inventory from 36 to 63 slots (+27 offset throughout) by modifying constants/slot indices via Mixin.

## Porting between Neo and Legacy — critical gotchas

### 1. Yarn vs Mojang class/method names

Never copy mixins verbatim. These class names differ:

| Mojang (Neo) | Yarn (Legacy) |
|-------------|---------------|
| `Inventory` (player) | `PlayerInventory` |
| `InventoryMenu` | `PlayerScreenHandler` |
| `AbstractContainerMenu` | `ScreenHandler` |
| `AbstractContainerScreen` | `HandledScreen` |
| `ContainerScreen` | `GenericContainerScreen` |
| `ChestMenu` | `GenericContainerScreenHandler` / `ShulkerBoxScreenHandler` |
| `BeaconMenu` | `BeaconScreenHandler` |
| `HopperMenu` | `HopperScreenHandler` |
| `CreativeModeInventoryScreen` | `CreativeInventoryScreen` |
| `ItemPickerMenu` | `CreativeInventoryScreen.CreativeScreenHandler` |
| `AbstractMountInventoryMenu` | `MountScreenHandler` |
| `DispenserMenu` | `Generic3x3ContainerScreenHandler` |

These method names differ:

| Mojang (Neo) | Yarn (Legacy) |
|-------------|---------------|
| `quickMoveStack` | `quickMove` |
| `isHotbarSlot` | `isInHotbar` |
| `doClick` | `onSlotClick` |
| `handleSetCreativeModeSlot` | `onCreativeInventoryAction` |
| `handleEditBook` | `onBookUpdate` |
| `checkHotbarKeyPressed` | `handleHotbarKeyPressed` |
| `saveChanges` (book) | `finalizeBook` |
| `selectTab` (creative) | `setSelectedTab` |

### 2. Always verify against decompiled sources

QuickMove constants are identical between Yarn and Mojang (same bytecode), but **class and method names must match**. Always run `genSources` and check the actual source before writing `@ModifyConstant`.

### 3. Use `require = 0` for uncertain targets

When unsure if a method/constant exists in Yarn (e.g., `getOccupiedSlotWithRoomForStack`, body/saddle slot constants), use `require = 0` to make the injection non-fatal. Silently skipped is better than crashing.

### 4. ExtendPlayerInventory needs BOTH `<init>` and `<clinit>`

The 36 constant appears in:
- `<init>`: `DefaultedList.ofSize(36, ...)` — main inventory size
- `<clinit>`: `MAIN_SIZE = 36` + 4× `getOffsetEntitySlotId(36)` in EQUIPMENT_SLOTS map

Without `<clinit>` modification, the `EQUIPMENT_SLOTS` map keeps vanilla indices (36-42) while PlayerScreenHandler routes items to modded positions (63-69) — armor/offhand placement silently fails.

### 5. ScreenHandler.onSlotClick has a separate 40 constant

The offhand swap path has TWO constants that both need modification:
- `HandledScreen.handleHotbarKeyPressed` → passes button=40 (fixed by `FixSwapOffhandContainer`)
- `ScreenHandler.onSlotClick` → checks `button == 40` (fixed by `FixScreenHandlerOffhandSwap`)

If only the first is patched, the `onSlotClick` SWAP handler never matches because it receives 67 but expects 40.

### 6. No refMap means Mixin must work without it

The legacy project has no `refMap` in mixins.json. Mixin uses fallback method resolution. Some method names that work with a refMap may silently fail without one. Add `require = 0` to injections if unsure.

## All Mixins (31 total)

### Core inventory expansion
| File | Target | Purpose |
|------|--------|---------|
| `ExtendPlayerInventory` | `PlayerInventory` | `<init>` 36→63, `<clinit>` 36→63 (×5), 40→67, 41→68, 42→69, `getOccupiedSlotWithRoomForStack` 40→67 |
| `RemapPlayerSlots` | `PlayerScreenHandler` | `<clinit>/isInHotbar/quickMove`: 36→63, 45→72, 46→73; `<init>`: 39→66, 40→67 |
| `GlobalDrawExtraSlots` | `ScreenHandler` | 3→6 inventory rows, hotbar Y 58→112 |
| `IncreaseGlobalBackgroundHeight` | `HandledScreen` | 166→226 bg height, label Y adjust |
| `SlotAccessor` | `Slot` | Accessors for x/y get/set (with `@Mutable`) |

### Creative screen
| File | Target | Purpose |
|------|--------|---------|
| `CreativeInventoryMixin` | `CreativeInventoryScreen` | Hotbar save/load +27 (`@ModifyArg`), click→server sync (TAIL inject bypassing broadcast), inventory tab reposition via reflection, debug logging |
| `FixCreativeSlotRangeCheck` | `ServerPlayNetworkHandler` | `onCreativeInventoryAction`: 45→72 slot range |
| `CreativeScreenHandlerMixin` | `CreativeInventoryScreen.CreativeScreenHandler` | Legacy no-op (exists but adds 0) |

### Offhand / book / swap fixes
| File | Target | Purpose |
|------|--------|---------|
| `FixSwapOffhandContainer` | `HandledScreen` | `handleHotbarKeyPressed`: 40→67 |
| `FixScreenHandlerOffhandSwap` | `ScreenHandler` | `onSlotClick` SWAP: 40→67 |
| `FixHandleEditBook` | `ServerPlayNetworkHandler` | `onBookUpdate`: 40→67 |
| `FixBookEditScreen` | `BookEditScreen` | `finalizeBook`: 40→67 |

### Recipe book button positions
| File | Target | Purpose |
|------|--------|---------|
| `PlayerInventoryRecipeButton` | `InventoryScreen` | Recipe book button Y +30 |
| `CraftingScreenRecipeBookButton` | `CraftingScreen` | Recipe book button Y +30 |
| `FurnaceScreenRecipeBookButton` | `AbstractFurnaceScreen` | Recipe book button Y +30 |

### Container background drawing
| File | Target | Purpose |
|------|--------|---------|
| `ChestsDrawExtraBackground` | `GenericContainerScreen` | Extra inventory bg for chests |
| `BeaconDrawExtraBackground` | `BeaconScreen` | Extra inventory bg for beacon |
| `HopperDrawExtraBackground` | `HopperScreen` | Extra inventory bg for hopper |

### QuickMove fixes (+27 offset)
| File | Target | Vanilla constants → Modded |
|------|--------|---------------------------|
| `FixCraftingMenuQuickMove` | `CraftingScreenHandler` | 37→64, 46→73 |
| `FixFurnaceMenuQuickMove` | `AbstractFurnaceScreenHandler` | 30→57, 39→66 |
| `FixBeaconMenuQuickMove` | `BeaconScreenHandler` | 28→55, 37→64 |
| `FixBrewingStandMenuQuickMove` | `BrewingStandScreenHandler` | 32→59, 41→68 |
| `FixMerchantMenuQuickMove` | `MerchantScreenHandler` | 30→57, 39→66 |
| `FixStonecutterMenuQuickMove` | `StonecutterScreenHandler` | 29→56, 38→65 |
| `FixLoomMenuQuickMove` | `LoomScreenHandler` | 31→58, 40→67 |
| `FixEnchantmentMenuQuickMove` | `EnchantmentScreenHandler` | 38→65 |
| `FixCartographyMenuQuickMove` | `CartographyTableScreenHandler` | 30→57, 39→66 |
| `FixGrindstoneMenuQuickMove` | `GrindstoneScreenHandler` | 30→57, 39→66 |
| `FixDispenserMenuQuickMove` | `Generic3x3ContainerScreenHandler` | 45→72 |
| `FixCrafterMenuQuickMove` | `CrafterScreenHandler` | 45→72 |
| `FixMountMenuQuickMove` | `MountScreenHandler` | 27→54 |

### Other
| File | Target | Purpose |
|------|--------|---------|
| `FixPlayerInventoryStorageImpl` | `PlayerInventoryStorageImpl` (Fabric API) | +27 to fabric transfer API indices |

## QuickMove constant verification process

When adding/modifying QuickMove mixins:
1. Run `genSources` (see above) to get decompiled Yarn-named `.java` files
2. Extract the target handler class (e.g. `CraftingScreenHandler.java`)
3. Find the `quickMove` method and identify all int constants
4. The pattern is always `original + 27`: vanilla hotbar start → modded hotbar start
5. Verify the constant value exists in the modded layout:
   - Inventory start: class-specific (varies by container)
   - Hotbar start: inventory start + 54
   - Total slots: inventory start + 63 (54 main + 9 hotbar)

## Conventions

- Mixin class names are descriptive CamelCase (e.g. `ExtendPlayerInventory`, `FixCraftingMenuQuickMove`).
- Shell scripts (`BUILD ALL.sh`, etc.) contain `read -p` prompts and will hang waiting for Enter — don't run them unattended.
- `./gradlew clean` also triggers file restoration of version-specific overrides (via `buildFinished`).
- All QuickMove mixins follow the same pattern: `@ModifyConstant` replacing hardcoded inventory index constants with `original + 27`.
- `FixMountMenuQuickMove` targets `MountScreenHandler` (not `HorseScreenHandler`) — the Yarn parent class for all mount inventory screens.
