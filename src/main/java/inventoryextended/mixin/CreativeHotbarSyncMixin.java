package inventoryextended.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*
 * Remaps hotbar slot indices when ItemPickerMenu syncs to the server.
 *
 * ItemPickerMenu places hotbar slots at menu indices 45-53 (after the 45
 * creative grid slots).  The mod shifts the real InventoryMenu hotbar to
 * 63-71, so the packet sent to the server must use 63-71.
 *
 * This redirect intercepts ContainerSynchronizer.sendSlotChange inside
 * synchronizeSlotToRemote and adjusts the slot index only when the
 * container is the creative ItemPickerMenu.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class CreativeHotbarSyncMixin {

    @Redirect(
            method = "synchronizeSlotToRemote",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ContainerSynchronizer;sendSlotChange(Lnet/minecraft/world/inventory/AbstractContainerMenu;ILnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void remapCreativeHotbarSync(
            ContainerSynchronizer sync,
            AbstractContainerMenu menu,
            int slot,
            ItemStack stack
    ) {
        if ((Object) this instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
            int size = ((AbstractContainerMenu) (Object) this).slots.size();
            if (slot >= size - 9 && slot < size) {
                slot = slot - (size - 9) + 63;
            }
        }
        sync.sendSlotChange(menu, slot, stack);
    }
}
