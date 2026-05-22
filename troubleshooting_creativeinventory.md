# Troubleshooting: Creative Inventory Screen Slot Mapping

## Overview

Inventory slot indices are shifted by +27 everywhere in the mod (main inventory 9→62, hotbar 63→71, offhand 72). The creative screen (`CreativeModeInventoryScreen` + `ItemPickerMenu`) has hardcoded assumptions about slot layout that break with this shift.

---

## Key Classes

### `CreativeModeInventoryScreen`
- Screen class (client-only, `containerId = 0`)
- Inner classes: `ItemPickerMenu`, `SlotWrapper`, `CustomCreativeSlot`
- Implements `FabricCreativeModeInventoryScreen`

### `ItemPickerMenu` (inner class)
- Client-only container; `containerId = 0` → synced to server on the player's `InventoryMenu`
- Constructor (`<init>`): sets `inventoryMenu = player.inventoryMenu`, creates 5×9 creative grid slots + 9 hotbar slots via `addInventoryHotbarSlots(inventory, 9, 112)`, then calls `scrollTo(0)`
- `getCarried()` / `setCarried()` delegate to `inventoryMenu` (player's `InventoryMenu`)
- `quickMoveStack()`: shift-click in last 9 slots → clears the item (single-player only)
- `calculateRowCount()`: `Mth.positiveCeilDiv(items.size(), 9) - 5`
- `canScroll()`: `items.size() > 45`

### `SlotWrapper` (inner class)
- Wraps an `InventoryMenu` slot for display in the **inventory tab** of the creative screen
- Constructor: `SlotWrapper(Slot target, int slotIndex, int x, int y)`
  - `this.index = slotIndex` (set by `Slot` super constructor)
  - `this.target = target` (the original slot)
- All getter/setter/query methods delegate to `this.target`
- Used in `selectTab()` when showing the INVENTORY tab (the one with the player model)

### `CustomCreativeSlot` (inner class)
- Simple slot used for the 5×9 creative grid in category tabs
- Only overrides `mayPickup(player)` → checks creative mode

---

## Slot Layouts

### Vanilla `InventoryMenu` (after constructor)
| Index range | Content | Inventory container index |
|------------|---------|--------------------------|
| 0 | Crafting result | - |
| 1–4 | Crafting grid | - |
| 5–8 | Armor (feet→head) | 36→39 |
| 9–35 | Main inventory (3 rows) | 9→35 |
| 36–44 | Hotbar (9 slots) | 0→8 |
| 45 | Offhand | 40 |

### Modded `InventoryMenu` (after mixins)
| Index range | Content | Inventory container index |
|------------|---------|--------------------------|
| 0 | Crafting result | - |
| 1–4 | Crafting grid | - |
| 5–8 | Armor (feet→head) | 63→66 (shifted +27) |
| **9–62** | **Main inventory (6 rows)** | **9→62** |
| **63–71** | **Hotbar (9 slots)** | **0→8** |
| **72** | **Offhand** | **67** |

### `ItemPickerMenu` after constructor (category tab)
| Index range | Content | Container reference |
|------------|---------|-------------------|
| 0–44 | Creative grid (5×9 CustomCreativeSlots) | `CONTAINER` (index 0→44) |
| **45–53** | **Hotbar (9 regular Slots)** | **player.inventory** (index 0→8) |

The hotbar slots at indices 45–53 reference `player.inventory.getItem(0)` through `player.inventory.getItem(8)` — this is **correct for the Inventory's hotbar indices (0–8)**. But the **menu slot index** (45–53) is what gets sent to the server.

---

## How Creative Screen Slot Clicks Work

### Category tab (not inventory tab)

When you click on a creative grid item and place it in a hotbar slot:

1. `doClick(slotId=45..53, ...)` processes the click
2. `slot.setByPlayer(item)` or `slot.set(item)` updates the player's `Inventory.getItem(0..8)` directly (correct)
3. Caller runs `menu.broadcastChanges()` after `doClick`
4. `broadcastChanges()` iterates `this.slots` (the `ItemPickerMenu`'s slot list)
5. For each slot: `synchronizeSlotToRemote(i, itemStack, supplier)` where `i` is the **menu index**
6. Inside `synchronizeSlotToRemote`:
   ```java
   // i = 45..53 for hotbar
   this.remoteSlots.get(i)  // tracks previous state
   this.synchronizer.sendSlotChange(this, i, copy)
   ```
7. `ContainerSynchronizer.sendSlotChange()` sends `ServerboundContainerSlotPacket(containerId, stateId, slotIndex, stack)` to the server
8. `containerId = 0` → server routes to player's open container (which is `InventoryMenu`)
9. Server applies the change to `InventoryMenu.slots[slotIndex]`

**THE BUG: slotIndex = 45..53 is sent, but InventoryMenu hotbar is at 63..71.**

### Inventory tab (player model visible)

When you are on the **INVENTORY tab** (`selectedTab.getType() == INVENTORY`):

1. `selectTab(CreativeModeTab)` is called
2. The entire slot list is replaced: `menu.slots.clear()` followed by a loop creating `SlotWrapper`s for EVERY slot in `player.inventoryMenu`
3. Each `SlotWrapper` has `index = i` (matching the `InventoryMenu` slot index: 0..45 in vanilla, 0..72 modded)
4. After the loop, a destroy item slot is appended
5. Then the mod's `@Inject(method="selectTab", at=@At("TAIL"))` repositions extended slots

Since `SlotWrapper.index = i` where `i` matches `InventoryMenu.slots` index, **slot click indices now DO match the server**. The clicks in the inventory tab work correctly because the indices align.

**The inventory tab works fine. The bug is in the CATEGORY tab.**

---

## Bugs Found

### BUG 1 (Critical): Creative category tab hotbar sync

**Location:** `AbstractContainerMenu.synchronizeSlotToRemote()` → `synchronizer.sendSlotChange(this, slot, copy)`

**What happens:**
- `broadcastChanges()` iterates `ItemPickerMenu.slots` (size = 54: 45 creative + 9 hotbar)
- Hotbar slots at menu indices **45–53** get synced to the server
- Server applies to `InventoryMenu.slots[45..53]` which are **inventory slots** (not hotbar)
- Item goes to wrong Inventory slot

**Affected interaction:** Any normal click that places a creative grid item into a hotbar slot while on a category tab.

**Fix:** Remap slot index 45→63 when sending `sendSlotChange` from an `ItemPickerMenu` context. The `remoteSlots` tracking remains correct (still uses 45–53 for the local menu), but the packet to the server uses 63–71.

```java
@Mixin(AbstractContainerMenu.class)
public class CreativeHotbarSyncMixin {
    @Redirect(
        method = "synchronizeSlotToRemote",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ContainerSynchronizer;sendSlotChange(Lnet/minecraft/world/inventory/AbstractContainerMenu;ILnet/minecraft/world/item/ItemStack;)V")
    )
    private void remapCreativeHotbarSync(ContainerSynchronizer sync, AbstractContainerMenu menu,
                                          int slot, ItemStack stack) {
        if ((Object)this instanceof ItemPickerMenu) {
            int size = ((AbstractContainerMenu)(Object)this).slots.size();
            if (slot >= size - 9 && slot < size) {
                slot = slot - (size - 9) + 63;
            }
        }
        sync.sendSlotChange(menu, slot, stack);
    }
}
```

---

### BUG 2 (Already Fixed): `handleHotbarLoadOrSave`

**Location:** `CreativeModeInventoryScreen.handleHotbarLoadOrSave(Minecraft, int, boolean, boolean)`

**Vanilla code (lines 80–90):**
```java
minecraft.gameMode.handleCreativeModeItemAdd(stack, 36 + slot);
```
This sends the hotbar index as **36 + slotIndex** (0..8 → 36..44).

**Fix:** `CreativeInventoryMixin.inventoryextended$fixCreativeHotbarSync` modifies the slot argument: `return slot + 27;`
→ `handleCreativeModeItemAdd` receives **63 + slotIndex** (63..71). ✅

---

### BUG 3 (Minor — Visual Only): `selectTab` offhand slot check

**Location:** `CreativeModeInventoryScreen.selectTab()` — the loop creating `SlotWrapper`s

**Vanilla code (bytecode offsets 451–466):**
```java
if (i == 45) {
    x = 35; y = 20;  // offhand
}
```

**Problem:** With the mod, slot 45 is a regular inventory slot (not offhand). The offhand is at slot 72.

**Effect:** The vanilla loop creates a `SlotWrapper` for i=45 with position (35, 20). Then the mod's `@Inject(method="selectTab", at=@At("TAIL"))` repositions slot 45 as an inventory slot (x=9+col*18, y=54+row*18). Visual is corrected. ✅

(Less of a bug, more of a double-write.)

---

### BUG 4 (Minor — No-Op): Creative grid row count

**Location:** `CreativeScreenHandlerMixin.modifyCreativeRows(int original)`

```java
@ModifyConstant(method = "<init>", constant = @Constant(intValue = 5))
private int modifyCreativeRows(int original) {
    return original + 0; // placeholder
}
```

**Effect:** No change. The creative grid stays at 5 rows. The comment says "Expand creative grid from 5×9 to 8×9" but the code is `original + 0`. If the goal is to show 6 inventory rows worth of content in the creative grid, this should be changed from `original + 0` to `original + 3` (or modify the constructor constant directly). This is a separate feature issue, not related to the hotbar bug.

---

## How the Creative Screen Sync Works (Full Flow)

```
Client (ItemPickerMenu, containerId=0)
  │
  │  doClick(slotId, ...)          ← slot index in ItemPickerMenu.slots
  │  broadcastChanges()
  │    synchronizeSlotToRemote(slotId, stack, ...)
  │      ├── remoteSlots.get(slotId)        ← local tracking, uses slotId
  │      ├── remoteSlots.set(slotId, ...)   ← local tracking, uses slotId
  │      └── sendSlotChange(this, slotId, copy)  ← packet to server
  │
  ├─── ServerboundContainerSlotPacket ───► Server
  │       { containerId=0, slot=slotId, stack=... }
  │
  ▼
Server (InventoryMenu, containerId=0)
  │
  │  handleContainerSlot(packet)
  │    menu = player.containerMenu   ← InventoryMenu (because containerId == 0)
  │    menu.slots[packet.slot].set(packet.stack)
  │
  ▼
  Vanilla:   menu.slots[45..53] → offhand + slot 46..53 don't exist! (handled by server differently)
  Modded:    menu.slots[45..53] → inventory slots (wrong!)
  Should be: menu.slots[63..71] → hotbar slots (correct)
```

---

## Summary of Needed Changes

| Priority | What | Status |
|----------|------|--------|
| **P0** | Add `CreativeHotbarSyncMixin` — remap `sendSlotChange` slot indices for `ItemPickerMenu` hotbar | ❌ Not implemented |
| P1 | `CreativeScreenHandlerMixin.modifyCreativeRows` — currently `+0` (no-op) | ⚠️ Placeholder |
| P2 | Review if creative grid should be expanded to 6+ rows to match inventory | 🤔 Future |

### Files to create/modify

1. **NEW:** `src/main/java/inventoryextended/mixin/CreativeHotbarSyncMixin.java`
2. **MODIFY:** `src/main/resources/inventoryextended.mixins.json` (add the new mixin reference)
