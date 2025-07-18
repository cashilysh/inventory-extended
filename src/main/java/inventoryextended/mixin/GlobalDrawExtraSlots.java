package inventoryextended.mixin;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(ScreenHandler.class)
public abstract class GlobalDrawExtraSlots {
	
	@Shadow
    protected abstract Slot addSlot(Slot slot); // Shadow the addSlot method

/*
    @Inject(method = "onSlotClick", at = @At("HEAD"))
    private void onSlotClick(int slot, int button, SlotActionType actionType, PlayerEntity player) {
            System.out.println("Slot clicked: " + slot);
    }
	*/
	//Draw 3 more rows of inventory slots
    @ModifyConstant(
        method = "addPlayerInventorySlots",
        constant = @Constant(intValue = 3)
    )
    private int modifyInventoryRows(int original) {
        return 6; // Change from 3 rows to 6 rows
    }
	
	
	
	//Hotbar Y-Offset
	 @ModifyConstant(
        method = "addPlayerSlots",
        constant = @Constant(intValue = 58)
    )
    private static int modifyHotbarOffset(int original) {
        return 112; // Replace 58, 112 works good!
    }
	
	
}