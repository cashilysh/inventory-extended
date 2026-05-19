package inventoryextended.mixin;


import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerMenu.class)
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
        method = "addInventoryExtendedSlots",
        constant = @Constant(intValue = 3)
    )
    private int modifyInventoryRows(int original) {
        return 6; // Change from 3 rows to 6 rows
    }
	
	
	
	//Hotbar Y-Offset
	 @ModifyConstant(
        method = "addStandardInventorySlots",
        constant = @Constant(intValue = 58)
    )
    private static int modifyHotbarOffset(int original) {
        return 112; // Replace 58, 112 works good!
    }
	
	
}