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
     * ============================================================
     * Fix creative hotbar sync
     * ============================================================
     */

    @ModifyArg(
            method = "handleHotbarLoadOrSave",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"
            ),
            index = 1
    )
    private static int inventoryextended$fixCreativeHotbarSync(int slot) {
        return slot + 27;
    }

    /*
     * ============================================================
     * Reposition extended inventory slots
     * ============================================================
     */

    @Inject(
            method = "selectTab",
            at = @At("TAIL")
    )
    private void inventoryextended$repositionExtendedSlots(
            net.minecraft.world.item.CreativeModeTab tab,
            CallbackInfo ci
    ) {
        CreativeModeInventoryScreen self =
                (CreativeModeInventoryScreen)(Object)this;

        if (!self.isInventoryOpen()) {
            return;
        }

        ItemPickerMenu menu = (ItemPickerMenu)self.getMenu();

        for (Slot slot : menu.slots) {

            int i = slot.index;

            SlotAccessor accessor =
                    (SlotAccessor)(Object)slot;

            /*
             * Vanilla:
             *
             * 0-4   crafting
             * 5-8   armor
             * 9-35  inventory
             * 36-44 hotbar
             * 45    offhand
             *
             * Extended:
             *
             * 9-62  inventory
             * 63-71 hotbar
             * 72    offhand
             */

            // Main inventory
            if (i >= 9 && i < 63) {

                int pos = i - 9;

                int row = pos / 9;
                int col = pos % 9;

                accessor.setX(9 + col * 18);
                accessor.setY(54 + row * 18);
            }

            // Hotbar
            else if (i >= 63 && i < 72) {

                int col = i - 63;

                accessor.setX(9 + col * 18);
                accessor.setY(166);
            }

            // Offhand
            else if (i == 72) {

                accessor.setX(173);
                accessor.setY(166);
            }
        }
    }
}