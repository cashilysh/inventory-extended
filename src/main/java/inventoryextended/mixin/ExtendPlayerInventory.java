package inventoryextended.mixin;


import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.*;

import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(PlayerInventory.class)
//@Environment(EnvType.CLIENT)
public abstract class ExtendPlayerInventory {
	
	
	@ModifyConstant(method = "*", constant = @Constant(intValue = 36), require = 1)
	private static int modifyMainSize(int original) {
    return original + 27; // 36 → 63
	}

	@ModifyConstant(method = "*", constant = @Constant(intValue = 40), require = 1)
	private static int modifyOffHandSlotConstInit(int original) {
    return original + 27; // 40 → 67
	}
	

}