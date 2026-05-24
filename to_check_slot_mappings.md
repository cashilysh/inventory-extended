# Slot Mappings Still to Fix

Every hardcoded slot-index constant that needs changing after the 36→63 inventory expansion,
verified against MC 26.1.2 bytecode.

---

## 🔴 Priority 1 — Offhand slot (40 → 67), breaks real functionality

| # | Class | Method | Constant | Fix | Why |
|---|-------|--------|----------|-----|-----|
| 1 | `ServerGamePacketListenerImpl` | `handleEditBook` | `bipush 40` | `40→67` | Server rejects book edits in offhand slot |
| 2 | `BookEditScreen` (client) | `saveChanges()` | `bipush 40` | `40→67` | Client sends wrong slot in `ServerboundEditBookPacket` |

---

## 🟡 Priority 2 — Menu `quickMoveStack` ranges

Every menu that calls `addStandardInventorySlots` gets 6 inventory rows (54 slots) instead of 3 (27).
Their `quickMoveStack` still uses the old 27+9 bounds, so shift-click only fills 36 of 63 player slots.

| # | Class | Block slots | Old USE_ROW_START | Old SLOT_COUNT | New USE_ROW_START | New SLOT_COUNT |
|---|-------|:-:|:-:|:-:|:-:|:-:|
| 3 | `CraftingMenu` | 10 | 37 | 46 | **64** | **73** |
| 4 | `AbstractFurnaceMenu` | 3 | 30 | 39 | **57** | **66** |
| 5 | `BeaconMenu` | 1 | 28 | 37 | **55** | **64** |
| 6 | `BrewingStandMenu` | 5 | 32 | 41 | **59** | **68** |
| 7 | `MerchantMenu` | 3 | 30 | 39 | **57** | **66** |
| 8 | `StonecutterMenu` | 2 | 29 | 38 | **56** | **65** |
| 9 | `LoomMenu` | 4 | 31 | 40 | **58** | **67** |
| 10 | `EnchantmentMenu` | 2 | — | 38 | — | **65** |
| 11 | `CartographyTableMenu` | 3 | 30 | 39 | **57** | **66** |
| 12 | `GrindstoneMenu` | 3 | 30 | 39 | **57** | **66** |
| 13 | `DispenserMenu` | 9 | — | 45 | — | **72** |
| 14 | `CrafterMenu` | 9 | — | 45 | — | **72** |
| 15 | `AbstractMountInventoryMenu` | *dynamic* | — | uses `bipush 27` | — | **27→54** |

`AbstractMountInventoryMenu` is the parent of `HorseInventoryMenu` and `NautilusInventoryMenu`.

---

## 🟢 Already fixed (existing mixins — verified correct)

| Mixin | Target | Fixes |
|-------|--------|-------|
| `ExtendPlayerInventory` | `Inventory` | `<init>` 36→63, `<clinit>` 36/40/41/42 +27, `getSlotWithRemainingSpace` 40→67 |
| `RemapPlayerSlots` | `InventoryMenu` | 36→63, 39→66, 40→67, 45→72, 46→73 in `<clinit>`, `isHotbarSlot`, `quickMoveStack`, `<init>` |
| `GlobalDrawExtraSlots` | `AbstractContainerMenu` | rows 3→6, hotbar Y 58→112, `doClick` 40→67 |
| `FixSwapOffhandContainer` | `AbstractContainerScreen` | `checkHotbarKeyPressed` 40→67 |
| `FixPlayerInventoryStorageImpl` | `PlayerInventoryStorageImpl` | 40→67, 36→63 |
| `FixCreativeSlotRangeCheck` | `ServerGamePacketListenerImpl` | `handleSetCreativeModeSlot` 45→72 |
| `CreativeInventoryMixin` | `CreativeModeInventoryScreen` | 36/45 offsets + hotbar sync + inventory tab reposition |

## ✅ Verified not affected (dynamic ranges)

- **`ChestMenu`** — uses `containerRows * 9` dynamically
- **`HopperMenu`** — uses `hopper.getContainerSize()` dynamically
- **`ShulkerBoxMenu`** — uses `container.getContainerSize()` dynamically
- **`SmithingMenu`** — inherits `ItemCombinerMenu.quickMoveStack` with virtual dispatch (no override)
- **`Inventory.isHotbarSlot()`** — checks `0 <= slot < 9`, still correct
- **All pixel/screen coordinates** in screen classes — not slot indices

---

*Source: decompiled via `javap -c -p` from loom-cache merged jar `minecraft-merged-52430b475d-26.1.2.jar`.*
