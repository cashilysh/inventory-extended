package inventoryextended.mixin;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryMixin {

    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void debugMouseClick(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        if (slot != null) {
            System.out.println("Slot clicked: " + slot.index + ", Index: " + slot.getContainerSlot() + ", Type: " + containerInput);
        }
    }

    @ModifyConstant(method = "selectTab", constant = @Constant(intValue = 45))
    private int modify45(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "selectTab", constant = @Constant(intValue = 36))
    private int modify36(int original) {
        return original + 27;
    }

    // Creative inventory hot bar Y-Position
    @ModifyConstant(method = "selectTab", constant = @Constant(intValue = 112))
    private int modify112(int original) {
        return original + 60;
    }

    // Note: "onMouseClick" method doesn't exist in the new version
    // These might need to be removed or updated to target different methods
    /*
    @ModifyConstant(method = "onMouseClick", constant = @Constant(intValue = 36))
    private int modify36again(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "onMouseClick", constant = @Constant(intValue = 45))
    private int modify45again(int original) {
        return original + 27;
    }
    */

    @ModifyConstant(method = "handleHotbarLoadOrSave", constant = @Constant(intValue = 36))
    private static int modify36again2(int original) {
        return original + 27;
    }
}