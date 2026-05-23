package inventoryextended.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryMixin {

    /*
     * Fix creative hotbar save/load sync.
     * Vanilla sends handleCreativeModeItemAdd(stack, 36 + i).
     * Modded hotbar is at 63, so shift by +27.
     */
    @ModifyArg(
            method = "handleHotbarLoadOrSave",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"
            ),
            index = 1
    )
    private static int fixCreativeHotbarSync(int slot) {
        int remapped = slot + 27;
        System.out.println("[InventoryExtended] handleHotbarLoadOrSave: slot " + slot
                + " -> " + remapped);
        return remapped;
    }

    /*
     * Reposition extended inventory slots in the inventory tab
     * (the tab with the player model).  Runs after the vanilla
     * selectTab loop finishes creating SlotWrappers.
     */
    @Inject(
            method = "selectTab",
            at = @At("TAIL")
    )
    private void repositionExtendedSlots(
            net.minecraft.world.item.CreativeModeTab tab,
            CallbackInfo ci
    ) {
        CreativeModeInventoryScreen self =
                (CreativeModeInventoryScreen) (Object) this;

        boolean isInventory = self.isInventoryOpen();
        String tabName = tab != null
                ? tab.getDisplayName().getString()
                : "null";

        System.out.println("[InventoryExtended] selectTab TAIL: tab=" + tabName
                + " isInventoryOpen=" + isInventory);

        if (!isInventory) {
            ItemPickerMenu menu = (ItemPickerMenu) self.getMenu();
            System.out.println("[InventoryExtended]   menu.slots.size=" + menu.slots.size()
                    + "  (not inventory tab, skipping reposition)");
            return;
        }

        ItemPickerMenu menu = (ItemPickerMenu) self.getMenu();
        System.out.println("[InventoryExtended]   menu.slots.size=" + menu.slots.size());

        int repositioned = 0;
        for (Slot slot : menu.slots) {
            int i = slot.index;
            SlotAccessor accessor = (SlotAccessor) (Object) slot;

            int oldX = accessor.getX();
            int oldY = accessor.getY();

            if (i >= 9 && i < 63) {
                int pos = i - 9;
                int row = pos / 9;
                int col = pos % 9;
                accessor.setX(9 + col * 18);
                accessor.setY(54 + row * 18);
                repositioned++;
                System.out.println("[InventoryExtended]   REPOSITION inventory slot " + i
                        + " row=" + row + " col=" + col
                        + " (" + oldX + "," + oldY + ") -> ("
                        + accessor.getX() + "," + accessor.getY() + ")");
            } else if (i >= 63 && i < 72) {
                int col = i - 63;
                accessor.setX(9 + col * 18);
                accessor.setY(166);
                repositioned++;
                System.out.println("[InventoryExtended]   REPOSITION hotbar slot " + i
                        + " col=" + col
                        + " (" + oldX + "," + oldY + ") -> ("
                        + accessor.getX() + "," + accessor.getY() + ")");
            } else if (i == 72) {
                accessor.setX(173);
                accessor.setY(166);
                repositioned++;
                System.out.println("[InventoryExtended]   REPOSITION offhand slot " + i
                        + " (" + oldX + "," + oldY + ") -> ("
                        + accessor.getX() + "," + accessor.getY() + ")");
            }
        }
        System.out.println("[InventoryExtended]   total repositioned: " + repositioned);
    }
}
