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
}