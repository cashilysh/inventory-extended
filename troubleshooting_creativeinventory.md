# Creative Inventory Hotbar Sync — Full Analysis & Fix

## Summary

The mod expands the player inventory by 27 slots (hotbar shifts from 36–44 to 63–71 in `InventoryMenu`). The creative screen (`CreativeModeInventoryScreen`) has two modes:

1. **Category tab** — The creative item grid + hotbar. Hotbar slots are at `ItemPickerMenu.slots[45..53]` (created by `addInventoryHotbarSlots` after the 45 creative grid slots at 0–44).
2. **Inventory tab** — Shows the player model, replaces all slots with `SlotWrapper` instances wrapping `InventoryMenu` slots. These already have correct indices (63–71 for hotbar).

**Bug:** Clicking a creative item and placing it in a hotbar slot in the **category tab** sends the wrong slot index to the server (45 instead of 63). The server applies the click to `InventoryMenu.slots[45]` which is an inventory slot, not a hotbar slot.

---

## The REAL sync path (discovered during investigation)

### Previous (wrong) assumption
`broadcastChanges()` → `synchronizeSlotToRemote()` → `sendSlotChange()` → `ServerboundContainerSlotPacket`

### Actual sync path
`AbstractContainerScreen` → `MultiPlayerGameMode.handleContainerInput(containerId, slotId, button, action)` →
1. Copies all slot items (before state)
2. `menu.clicked(slotId, ...)` → `doClick(slotId, ...)` — modifies the actual containers
3. Compares before/after to build `changedItems` map
4. Sends `ServerboundContainerClickPacket(containerId, stateId, (short)slotId, ...)`

The server receives this packet and processes the click on the slot identified by `slotId` within the player's `InventoryMenu` (containerId=0).

### Why the old fix didn't work
The first `CreativeHotbarSyncMixin` redirected `ContainerSynchronizer.sendSlotChange` inside `synchronizeSlotToRemote`. But `broadcastChanges` is only called on `player.inventoryMenu` (the `InventoryMenu`), **not** on the `ItemPickerMenu`. The `instanceof ItemPickerMenu` check in the redirect handler always failed because `this` was an `InventoryMenu` instance.

---

## The Fix

**New mixin:** `CreativeHotbarSyncMixin` (replaces the old one)

**Target:** `MultiPlayerGameMode.handleContainerInput`

**Mechanism:** `@Redirect` on the `ServerboundContainerClickPacket` constructor call (`@At(value = "NEW")`). Intercepts the construction and remaps:
- `slot` parameter: `45..53 → 63..71`
- `changedItems` map keys: `45..53 → 63..71`

**Safety check:** The handler checks if the clicked slot's class name is `"SlotWrapper"`. In the inventory tab, all visible slots are `SlotWrapper` instances (already have correct indices). In the category tab, hotbar slots are plain `Slot` instances. Remapping only happens when NOT a `SlotWrapper` AND the slot index is in the hotbar range (`[size-9, size)`).

---

## All Creative-Related Mixins

| Mixin file | Target | What it does | Status |
|-----------|--------|-------------|--------|
| `CreativeInventoryMixin` | `CreativeModeInventoryScreen` | (a) `handleHotbarLoadOrSave`: +27 to `handleCreativeModeItemAdd` slot — handles hotbar save/load. (b) `selectTab` TAIL: repositions extended slots in inventory tab. | ✅ Fixed |
| `CreativeHotbarSyncMixin` | `MultiPlayerGameMode` | Redirects `ServerboundContainerClickPacket` constructor to remap slot indices 45→63 for category tab hotbar clicks. **This is the main fix.** | ✅ Fixed |
| `CreativeScreenHandlerMixin` | `ItemPickerMenu` | `@ModifyConstant` for `5` in constructor: returns `5 + 0` (no-op). Was meant to expand creative grid rows. | ⚠️ No-op placeholder |

---

## Debug Output

When enabled, the `CreativeHotbarSyncMixin` prints lines like:

```
[InventoryExtended] handleContainerInput: slot=45 size=54 hotbarRange=[45,53] slotClass=Slot isSlotWrapper=false needRemap=true action=PICKUP
[InventoryExtended]   REMAP slot 45 -> 63
[InventoryExtended]   REMAP changedItems key 45 -> 63
```

On the **inventory tab** (SlotWrapper slots — should NOT remap):
```
[InventoryExtended] handleContainerInput: slot=63 size=74 hotbarRange=[65,73] slotClass=SlotWrapper isSlotWrapper=true needRemap=false action=PICKUP
```

---

## Key Constants

| Constant | Vanilla | Modded | Meaning |
|----------|---------|--------|---------|
| Main inventory size | 36 | 63 | `Inventory` main items count |
| InventoryMenu hotbar start | 36 | 63 | `HOTBAR_SLOT_START` |
| InventoryMenu hotbar end | 45 | 72 | `HOTBAR_SLOT_END` (exclusive) |
| InventoryMenu offhand | 45 | 72 | Position in menu slots list |
| ItemPickerMenu hotbar start | 45 | 45 | Position in creative menu (unchanged) |
| ItemPickerMenu hotbar end | 54 | 54 | 45 creative + 9 hotbar (unchanged) |
| ItemPickerMenu size (category) | 54 | 54 | 45 creative + 9 hotbar |
| ItemPickerMenu size (inventory tab) | 46 + 1 | 73 + 1 | SlotWrappers + destroy slot |
| InventoryMenu total slots | 46 | 73 | 0-craft + 1-4-crafting + 5-8-armor + 9-62-inv + 63-71-hotbar + 72-offhand |

---

## Slot Index Mapping Table

### Category tab (creative grid + hotbar)
| ItemPickerMenu index | Content | Container | Container index |
|---------------------|---------|-----------|-----------------|
| 0–44 | Creative grid (CustomCreativeSlot) | `CONTAINER` (SimpleContainer) | 0–44 |
| **45–53** | **Hotbar (9 regular Slots)** | **playerInventory** | **0–8** |

→ Hotbar slots reference `playerInventory.items[0..8]` (correct).
→ **But menu index 45–53 expects server InventoryMenu index 63–71.**

### Inventory tab (player model view)
| ItemPickerMenu index | Content | Wraps InventoryMenu slot |
|---------------------|---------|-------------------------|
| 0–4 | Hidden (crafting) | InventoryMenu.slots[0–4] |
| 5–8 | Armor (SlotWrapper) | InventoryMenu.slots[5–8] |
| 9–62 | Inventory (SlotWrapper) | InventoryMenu.slots[9–62] |
| **63–71** | **Hotbar (SlotWrapper)** | **InventoryMenu.slots[63–71]** |
| 72 | Offhand (SlotWrapper) | InventoryMenu.slots[72] |
| 73 | Destroy item slot | CONTAINER |

→ SlotWrapper indices already match InventoryMenu. No remapping needed.

---

## Files Modified

1. **NEW/REPLACED:** `src/main/java/inventoryextended/mixin/CreativeHotbarSyncMixin.java`
   - `@Mixin(MultiPlayerGameMode.class)`
   - `@Redirect` on `ServerboundContainerClickPacket` constructor in `handleContainerInput`
   - Remaps slot index and changedItems map keys for category tab hotbar clicks
   - Includes `System.out.println` debug logging

2. **MODIFIED:** `src/main/resources/inventoryextended.mixins.json`
   - Added `"CreativeHotbarSyncMixin"` entry
