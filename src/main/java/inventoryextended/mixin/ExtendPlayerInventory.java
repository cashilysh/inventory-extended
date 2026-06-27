package inventoryextended.mixin;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(PlayerInventory.class)
public abstract class ExtendPlayerInventory {

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 36))
	private int modifyInitListSize(int original) {
		return original + 27;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36))
	private static int modifyClinit36(int original) {
		return original + 27;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 40))
	private static int modifyClinitOffhand(int original) {
		return original + 27;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 41), require = 0)
	private static int modifyClinitBody(int original) {
		return original + 27;
	}

	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 42), require = 0)
	private static int modifyClinitSaddle(int original) {
		return original + 27;
	}

	@ModifyConstant(method = "getOccupiedSlotWithRoomForStack", constant = @Constant(intValue = 40), require = 0)
	private int modifyOffhandSlotCheck(int original) {
		return original + 27;
	}
}