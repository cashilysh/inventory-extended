package inventoryextended.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(Inventory.class)
public abstract class ExtendPlayerInventory {


	@ModifyConstant(method = "*", constant = @Constant(intValue = 36), require = 1)
	private static int modifyMainSize(int original) {
		return original + 27; // 36 → 63
	}

	@ModifyConstant(method = "*", constant = @Constant(intValue = 40), require = 1)
	private static int modifyOffHandSlotConstInit(int original) {
		return original + 27; // 40 → 67
	}



	// Shift body slot (41 → 68)
	@ModifyConstant(method = "*", constant = @Constant(intValue = 41), require = 1)
	private static int modifyBodySlot(int original) {
		return original + 27;
	}

	// Shift saddle slot (42 → 69)
	@ModifyConstant(method = "*", constant = @Constant(intValue = 42), require = 1)
	private static int modifySaddleSlot(int original) {
		return original + 27;
	}
}