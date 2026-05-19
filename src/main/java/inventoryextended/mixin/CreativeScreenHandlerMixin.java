package inventoryextended.mixin;


import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;


import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;


@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public class CreativeScreenHandlerMixin {




    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 5))
    private int modifyCreativeRows(int original) {
        return original + 0; // Expand creative grid from 5x9 to 8x9 to match your inventory expansion
    }

    //Add ghost slots upto slot ID 63 then proceed with horbar

/*
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen$CreativeScreenHandler;addPlayerHotbarSlots(Lnet/minecraft/inventory/Inventory;II)V"))
    private void fixHotbarSlotAlignment(CreativeInventoryScreen.CreativeScreenHandler handler, Inventory playerInventory, int left, int y) {
        // Add dummy slots to reach the correct index (63)
        // Using a simple inventory for dummy slots since they're hidden
        SimpleInventory dummyInventory = new SimpleInventory(27);

        while(handler.slots.size() < 63) {
            handler.addSlot(new Slot(dummyInventory, 0, -2000, -2000)); // Hidden dummy slots
        }

        // Now add hotbar at the correct indices (63-71)
        for(int i = 0; i < 9; ++i) {
            handler.addSlot(new Slot(playerInventory, i, left + i * 18, y));
        }
    }

    // Update the hotbar Y position constant
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 112))
    private int modifyHotbarY(int original) {
        return 166; // Match your expanded inventory hotbar position
    }

 */
}