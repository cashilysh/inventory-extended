# AGENTS.md

## Build

```
./gradlew build                                  # unit + server game tests
./gradlew clean build
./gradlew build -PtargetVersion=26.2             # target a specific MC version
./gradlew runclient                              # launch client after build
```

Convenience test runner (colored output + clean log):
```
./RUN TESTS.sh         # all tests
./RUN TESTS.sh unit    # unit tests only
./RUN TESTS.sh server  # server game tests only
./RUN TESTS.sh client  # client game tests only
```
Supports `-PtargetVersion=` forwarding:
```
./RUN TESTS.sh all -PtargetVersion=26.2
```

Direct Gradle test tasks:
```
./gradlew test             # unit tests (Fabric Loader JUnit)
./gradlew runGameTest      # server game tests
./gradlew runClientGameTest # client game tests
```

- **Java 25 required** (build fails otherwise). Gradle 9.5.1 via wrapper.
- Dependencies are resolved **at config-time from live APIs** (meta.fabricmc.net, api.modrinth.com). The build needs internet.
- Default target version: `26.1.2` (set in `gradle.properties`). Built `fabric.mod.json` may differ due to `editFabricModJson` task.
- Tests require `JAVA_HOME` set (e.g. `export JAVA_HOME=/usr/lib/jvm/java-25-openjdk`). `RUN TESTS.sh` defaults to that path if unset.
- MC 26.1.2 and 26.2 are both supported. Only known API break: `EntityType.HORSE` → `EntityTypes.HORSE` (handle via reflection in `QuickMoveOtherMenusGameTest`).

## Architecture

- **Fabric mod** using Fabric Loom (`1.16-SNAPSHOT`). Entrypoint: `inventoryextended.InventoryExtended` (implements `ModInitializer`).
- **All logic is in mixins** (30 total in `src/main/java/inventoryextended/mixin/`). The entrypoint class just logs init. Mixin config at `src/main/resources/inventoryextended.mixins.json`.
- Expands inventory from 36 to 63 slots by modifying constants/slot indices via Mixin (+27 offset throughout).

## Modded Inventory Layout

| Inventory index | Content |
|----------------|---------|
| 0–8 | Hotbar |
| 9–62 | Main inventory (6 rows x 9) |
| 63–66 | Armor |
| 67 | Offhand |

### InventoryMenu (survival screen)
| Menu index | Content |
|-----------|---------|
| 0 | Crafting result |
| 1–4 | Crafting grid |
| 5–8 | Armor (inv indices 63–66) |
| 9–62 | Main inventory (inv indices 9–62) |
| 63–71 | Hotbar (inv indices 0–8) |
| 72 | Offhand (inv index 67) |

### Creative screen — category tab (ItemPickerMenu)
| Menu index | Content |
|-----------|---------|
| 0–44 | Creative grid (CustomCreativeSlot) |
| 45–53 | Hotbar (9 Slots, inv indices 0–8) |

### Creative screen — inventory tab (player model)
Uses `SlotWrapper` instances wrapping `InventoryMenu` slots — indices match `InventoryMenu` layout above.

### Creative screen — inventory tab positions (after mod reposition)
| Slot | Position | Content |
|------|---------|---------|
| 0–4 | (-2000,-2000) | Crafting (hidden) |
| 5–8 | armor layout | Armor |
| 9–62 | (9+col·18, 54+row·18) | 6 inventory rows |
| 63–71 | (9+col·18, 166) | Hotbar |
| 72 | (35, 20) | Offhand (vanilla creative position) |
| 73 | (173, 166) | Destroy item slot (vanilla 112 + 3 rows) |

## Key gotchas

- **`fabric.mod.json` is auto-generated** at build time by `editFabricModJson` task. Do not manually edit version/depends fields; they get overwritten from `gradle.properties` and live API results.
- **Version-specific overrides**: `src/version_specific/` can hold Java sources that override `src/main/java/` files for specific MC version ranges. The `applyVersionSpecificOverrides` task copies them in before compile and the `buildFinished` hook restores originals. Currently empty — infrastructure exists but no overrides are active.
- **MC 26.x is unobfuscated** — the output task is `jar`, not `remapJar` (Loom skips remapping).
- Dependencies are declared as **Modrinth slugs** in `gradle.properties` under `deps.implementation` / `deps.compileOnly` / `deps.localRuntime`, not as standard Maven coordinates.
- 79 tests total: 15 unit + 59 server game + 5 client game.

## Tests

79 tests total, split across three test types:

| Type | Qty | Location | Framework |
|------|-----|----------|-----------|
| Unit | 15 | `src/test/java/inventoryextended/test/` | Fabric Loader JUnit |
| Server game | 59 | `src/gametest/java/inventoryextended/gametest/` | Fabric GameTest |
| Client game | 5 | `src/gametest/java/inventoryextended/gametest/` | Fabric ClientGameTest |

### Unit tests

| File | Tests | Coverage |
|------|-------|----------|
| `InventorySlotConstantsTest.java` | 4 | isHotbarSlot boundaries, EQUIPMENT_SLOT_MAPPING keys shifted, old keys absent, registry bootstrap |
| `ConstantBoundaryGameTest.java` | 3 | isHotbarSlot full range (0–79), EQUIPMENT_SLOT_MAPPING full key range, inventory constructor accessible |
| `ReflectionFieldGameTest.java` | 4 | AbstractContainerScreen fields (imageHeight, topPos), Slot fields (x, y), registry reflection, isHotbarSlot method |
| `EntityTypeCompatibilityGameTest.java` | 4 | Horse entity type resolvable, all 5 horse variants exist, version API constants, AbstractHorse class |

### Server game tests

| File | Tests | Coverage |
|------|-------|----------|
| `InventorySizeGameTest` | 3 | Container size ≥63, place/retrieve all 63 slots, hotbar range |
| `QuickMoveGameTest` | 4 | Furnace fuel/ingredient/hotbar quick-move, crafting quick-move |
| `QuickMoveOtherMenusGameTest` | 11 | Beacon, brewing, stonecutter, loom, enchantment, cartography, grindstone, merchant, dispenser, crafter, horse |
| `ArmorOffhandGameTest` | 4 | Armor slots 63–66, offhand 67, equipping, equipment mapping |
| `PersistenceGameTest` | 2 | Save/load roundtrip for all 63 slots, extended slots (36, 62) survive |
| `CreativeExtendedSlotGameTest` | 3 | All 63 main slots writable/readable in creative, InventoryMenu has 73 slots, isHotbarSlot correct range |
| `OffhandSwapGameTest` | 4 | Offhand slot 67 index, offhand item read, offhand clear/persist, swap constant in menu |
| `BookEditingGameTest` | 3 | Offhand slot 67 accessible for books, book edit offhand index, written book survives |
| `QuickMoveEdgeCaseGameTest` | 7 | Partial stack merge (63 coal), extended slot 62→furnace, extended slot→crafting, full inventory, boundary slots (9, 62), hotbar slot 0→57, hotbar slot 8→65 |
| `PersistenceEdgeCaseGameTest` | 3 | Sparse save/load (slots 0, 30, 62), stack of 64 in slot 9, armor/offhand items readable |
| `DeathDropGameTest` | 3 | Extended slots (9–62) all accessible, hotbar (0–8) items accessible, armor/offhand slots readable |
| `CreativeSurvivalTransitionGameTest` | 3 | All 63 slots accessible in creative, all 63 slots in survival, sequential diamond counts |
| `MixinApplicationGameTest` | 6 | Container size 63, menu slot count 73, isHotbarSlot, EQUIPMENT_SLOT_MAPPING, quick-move, all menu types quick-move |
| `QuickMoveCoverageGameTest` | 3 | All menu types registered, quick-move doesn't crash, known menus have fix |

Plus 1 test from Fabric API itself.

### Client game tests

| File | Tests | Coverage |
|------|-------|----------|
| `CreativeHotbarGameTest` | 1 | Creative screen hotbar constant correctness |
| `InventoryScreenshotTest` | 1 | Screenshot-based inventory visual verification |
| `CreativeInventoryTabGameTest` | 1 | Client-side container size, menu slots, isHotbarSlot, screenshot |
| `RecipeBookButtonGameTest` | 1 | Client-side container size verify, recipe book button screenshot |
| `ContainerBackgroundGameTest` | 1 | Client-side container size, extra background screenshot |

### Key test implementation details

- Mock players created with `makeMockPlayer(GameType.SURVIVAL)` (MC 26.x API; `makeMockServerPlayerInLevel` is deprecated).
- `net.minecraft.server.Bootstrap.bootStrap()` — correct bootstrap class for MC 26.x.
- `TestSingleplayerContext` provides `getClientLevel()` (MC 26.1.2+ Fabric API).
- Static constant fields (`InventoryMenu.INV_SLOT_END`, etc.) cannot be tested directly — `@ModifyConstant` on `<clinit>` doesn't change compile-time constants. Test `isHotbarSlot()` behavior instead.
- `getContainerSize()` returns 70 (63 main + 4 armor + 1 offhand + 1 body + 1 saddle) — wider than just main inventory.
- Quick-move tests for restrictive menus use relaxed assertions (verify range doesn't throw, don't check exact destination).
- `EntityType.HORSE` → `EntityTypes.HORSE` between 26.1.2 and 26.2: handle via reflection (try `EntityTypes` first, fall back to `EntityType`). Spawn returns wildcard `Entity` — cast to `AbstractHorse`.

### Known test limitations

Some mixins cannot be tested directly due to framework constraints. Their coverage is indirect (slot constants, container size, etc.).

| Mixin | Limitation |
|-------|-----------|
| `FixCreativeSlotRangeCheck` (`45`→`72`) | `makeMockPlayer()` returns a mock, not `ServerPlayer`. No `ServerGamePacketListenerImpl` available to call `handleSetCreativeModeSlot`. Verified indirectly via menu slot count ≥73. |
| `FixSwapOffhandContainer` (`40`→`67`) | Targets `AbstractContainerScreen` (client-only). Server game tests log a "target not found" warning. Covered indirectly by offhand slot index tests. |
| `FixBookEditScreen` (`40`→`67`) | Targets `BookEditScreen` (client-only). Same as above. Covered indirectly by book slot index tests. |
| Armor/offhand save/load | `inv.save()` serializes `items`, `armor`, and `offhand` as separate NBT arrays. The `ValueOutput.TypedOutputList` approach only captures the `items` list (slots 0–62). Armor (63–66) and offhand (67) items persist correctly through the game's standard NBT serialization (`Inventory`, `ArmorItem`, `OffhandItem` tags) — just not through the `TypedOutputList` test path. Tests verify read/write accessibility instead. |

### Test infrastructure

- `build.gradle` configures `fabricApi { configureTests { ... } }` block for server and client game tests.
- `src/gametest/resources/fabric.mod.json` registers gametest entrypoints.
- `RUN TESTS.sh` produces ANSI-colored terminal output and a plain-text `test_results.log` with per-test PASSED/FAILED + failure details.
- Test names are extracted from `@GameTest` annotations; the script's parser handles `@GameTest(template = "foo")` patterns.

## All Mixins (30 total)

### Core inventory expansion
| File | Target | Purpose |
|------|--------|---------|
| `ExtendPlayerInventory` | `Inventory` | +27 to main size, offhand, armor indices |
| `RemapPlayerSlots` | `InventoryMenu` | +27 to hotbar/offhand/armor slot constants |
| `GlobalDrawExtraSlots` | `AbstractContainerMenu` | 3→6 inventory rows, hotbar Y 58→112 |
| `IncreaseGlobalBackgroundHeight` | `AbstractContainerScreen` | 166→226 bg height, label Y adjust |
| `SlotAccessor` | `Slot` | Accessors for x/y get/set (with `@Mutable`) |

### Creative screen
| File | Target | Purpose |
|------|--------|---------|
| `CreativeInventoryMixin` | `CreativeModeInventoryScreen` | Hotbar save/load +27, quick-craft save `36`→`63`, click→server sync, inventory tab reposition, debug logging |
| `FixCreativeSlotRangeCheck` | `ServerGamePacketListenerImpl` | `45`→`72` creative slot range on server |

### Recipe book button positions
| File | Target | Purpose |
|------|--------|---------|
| `PlayerInventoryRecipeButton` | `InventoryScreen` | Recipe book button Y +30 |
| `CraftingScreenRecipeBookButton` | `CraftingScreen` | Recipe book button Y +30 |
| `FurnaceScreenRecipeBookButton` | `AbstractFurnaceScreen` | Recipe book button Y +30 |

### Container background drawing
| File | Target | Purpose |
|------|--------|---------|
| `ChestsDrawExtraBackground` | `ContainerScreen` | Extra inventory bg for chests |
| `BeaconDrawExtraBackground` | `BeaconScreen` | Extra inventory bg for beacon |
| `HopperDrawExtraBackground` | `HopperScreen` | Extra inventory bg for hopper |

### QuickMove fixes (+27 offset in `quickMoveStack` constants)
| File | Target | Purpose |
|------|--------|---------|
| `FixCraftingMenuQuickMove` | `CraftingMenu` | `37`→`64`, `46`→`73` |
| `FixFurnaceMenuQuickMove` | `AbstractFurnaceMenu` | Index adjustments |
| `FixBeaconMenuQuickMove` | `BeaconMenu` | Index adjustments |
| `FixBrewingStandMenuQuickMove` | `BrewingStandMenu` | Index adjustments |
| `FixMerchantMenuQuickMove` | `MerchantMenu` | Index adjustments |
| `FixStonecutterMenuQuickMove` | `StonecutterMenu` | Index adjustments |
| `FixLoomMenuQuickMove` | `LoomMenu` | Index adjustments |
| `FixEnchantmentMenuQuickMove` | `EnchantmentMenu` | Index adjustments |
| `FixCartographyMenuQuickMove` | `CartographyMenu` | Index adjustments |
| `FixGrindstoneMenuQuickMove` | `GrindstoneMenu` | Index adjustments |
| `FixDispenserMenuQuickMove` | `DispenserMenu` | Index adjustments |
| `FixCrafterMenuQuickMove` | `CrafterMenu` | Index adjustments |
| `FixMountMenuQuickMove` | `AbstractMountInventoryMenu` | `27`→`54` |

### Other fixes
| File | Target | Purpose |
|------|--------|---------|
| `FixPlayerInventoryStorageImpl` | `PlayerInventoryStorageImpl` | +27 to fabric transfer API indices |
| `FixSwapOffhandContainer` | `AbstractContainerScreen` | `checkHotbarKeyPressed`: `40`→`67` (offhand swap) |
| `FixHandleEditBook` | `ServerGamePacketListenerImpl` | `handleEditBook`: `40`→`67` (offhand slot) |
| `FixBookEditScreen` | `BookEditScreen` | `saveChanges`: `40`→`67` (offhand slot) |

## Creative inventory — full troubleshooting reference

The creative screen (`CreativeModeInventoryScreen`) uses `ItemPickerMenu` as its container, NOT `InventoryMenu`. `ItemPickerMenu` has `containerId=0` and replaces `InventoryMenu` as the player's active container when creative is open. This difference from survival inventory is the source of most bugs.

### Bug A: Items placed in creative hotbar invisible to server

**Symptom**: Item appears in creative hotbar client-side, but placing blocks uses old item, and relogging restores old item.

**Root cause — three independent failures**:

1. **`ServerGamePacketListenerImpl.handleSetCreativeModeSlot` has hardcoded `slotNum <= 45`**. The vanilla `InventoryMenu` has 46 slots (max index 45). The modded `InventoryMenu` has 73 slots (max index 72, hotbar at 63–71). Any `ServerboundSetCreativeModeSlotPacket` with a slot index > 45 is silently dropped by the server. **Fix**: `FixCreativeSlotRangeCheck`.

2. **Click sync relied on broken `broadcastChanges` path**. When you click a hotbar slot in the category tab:
   - `slotClicked` calls `ItemPickerMenu.clicked(slot.index, ...)` to process the click client-side (modifying `playerInventory`)
   - Then `player.inventoryMenu.broadcastChanges()` runs — sends `ServerboundContainerSlotPacket(containerId=0, slot=63)` to the server
   - The server's active container during creative is `ItemPickerMenu` (only 54 vanilla slots), not `InventoryMenu` — `getSlot(63)` returns null, change is lost
   - Then `this.menu.broadcastChanges()` (ItemPickerMenu) runs — but the client-side `ItemPickerMenu` has NO `ContainerSynchronizer` (only server-side menus get one) — nothing is sent
   - **Fix**: `syncCreativeHotbarSlot` TAIL inject calls `handleCreativeModeItemAdd(stack, 63+containerSlot)` directly, sending `ServerboundSetCreativeModeSlotPacket` and bypassing both broken broadcast paths

3. **`handleHotbarLoadOrSave` and `slotClicked` quick-craft hotbar save use `36+i`**. Both need the +27 offset to use `63+i` instead. **Fix**: `fixCreativeHotbarSync` (for `handleHotbarLoadOrSave`) and `fixSlotClickedHotbarSave` (for the `36` constant in `slotClicked`).

### Bug B: Creative inventory tab shows only 27 slots, extra slots overlap hotbar

**Symptom**: Switching to creative screen's "inventory" tab (player model view) shows only 27 vanilla inventory slots. Extended slots (36–62) overlap the hotbar at y=112. Hotbar not moved down. Bottom creative tabs not moved down.

**Root cause — four independent failures**:

1. **Creative screen uses `imageHeight=136`, not `166`**. The creative screen constructor calls `super(menu, inv, title, 195, 136)`, NOT the default `176`×`166` constructor. The existing `IncreaseGlobalBackgroundHeight` only modifies the `166` in `AbstractContainerScreen`'s convenience constructor, which the creative screen never uses. Bottom tabs render at `topPos + imageHeight - 4`, so tabs never moved down with the default 136.

2. **Vanilla `selectTab` hardcodes `i >= 36` for hotbar Y (112)**. In the modded layout, `i=36..62` are inventory slots, not hotbar. The vanilla code positions them all at hotbar Y=112, causing overlap. Also positions hotbar slots at `x = 3 + (i - 36) * 18` — for the modded hotbar (i=63..71), this gives x=489..633 (off-screen).

3. **Vanilla `selectTab` adds SlotWrappers via `menu.slots.add()`, NOT `addSlot()`**. `addSlot()` is the method that sets `slot.index = menuPosition`. Since `addSlot()` is never called, ALL SlotWrapper instances have `slot.index = 0` (field default). Any reposition logic relying on `slot.index` silently matches nothing.

4. **`Slot.index` is `public` but `Slot.x`/`Slot.y` are `public final`**. Modifying `x`/`y` at runtime requires removing the `final` modifier. The `SlotAccessor` mixin with `@Mutable` handles this at bytecode level. Direct reflection with `Field.modifiers` hack may fail on Java 25.

**Fix**: `CreativeInventoryMixin.repositionExtendedSlots` TAIL inject on `selectTab`:
- Uses a **loop counter** (`int j`) instead of `slot.index` to identify each slot's InventoryMenu position
- Dynamically adjusts `imageHeight` + `topPos` via reflection: 194 for inventory tab (tabs move down), 136 for all other tabs (tabs stay at vanilla position)
- Repositions all SlotWrapper instances using `SlotAccessor` with proper 6-row inventory layout, correct hotbar Y (166), offhand at vanilla creative position (35, 20), and destroy item slot moved down (173, 166)

### What didn't work (for reference)

| Approach | Why it failed |
|----------|--------------|
| `@Mixin(MultiPlayerGameMode.class)` — intercept `handleContainerInput` / `ServerboundContainerClickPacket` | `MixinProcessor` crashes in this MC version. Constructor redirects on `MultiPlayerGameMode` are unreliable. |
| Spacer slots in `ItemPickerMenu` (move hotbar 45→63) | Vanilla `CreativeModeInventoryScreen.slotClicked` has hardcoded range checks expecting hotbar at 45–53. Cursor items wouldn't release. |
| `@ModifyConstant` on `<init>` with `intValue = 136` | Modifies bytecode BEFORE `super()` call — `this` uninitialized → `VerifyError` at class load. |
| `@Shadow private int imageHeight` in `CreativeInventoryMixin` | Field is in parent class `AbstractContainerScreen`, not in the target class. Mixin can't resolve without a refMap. |
| Using `slot.index` for SlotWrapper slot identification | Vanilla `selectTab` adds SlotWrappers with `menu.slots.add()`, not `addSlot()` — `slot.index` stays at 0 for all. |
| `@ModifyConstant` on `extractTabButton` to change tab gap globally | The `4` is correct for all non-inventory tabs. Only the inventory tab needs a larger gap. Fixed by adjusting `HEIGHT_EXTENDED` instead. |

## Conventions

- Mixin class names are descriptive CamelCase (e.g. `ExtendPlayerInventory`, `RemapPlayerSlots`).
- Shell scripts (`BUILD ALL.sh`, `RUN TESTS.sh`, etc.) — `BUILD ALL.sh` contains `read -p` prompts and will hang waiting for Enter; don't run it unattended.
- `./gradlew clean` also triggers file restoration of version-specific overrides (via `buildFinished`).
- All QuickMove mixins follow the same pattern: `@ModifyConstant` replacing hardcoded inventory index constants with `original + 27`.
