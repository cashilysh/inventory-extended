package inventoryextended.mixin;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(Inventory.class)
public abstract class ExtendPlayerInventory {

	// ── <init> ────────────────────────────────────────────────────────────────
	// NonNullList.withSize(36, ItemStack.EMPTY) → 63
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 36))
	private int modifyInitListSize(int original) {
		return original + 27;
	}

	// ── <clinit> ──────────────────────────────────────────────────────────────
	// EquipmentSlot.FEET.getIndex(36)   ordinal 0
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 0))
	private static int modifyClinitFeetIndex(int original) {
		return original + 27;
	}

	// EquipmentSlot.LEGS.getIndex(36)   ordinal 1
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 1))
	private static int modifyClinitLegsIndex(int original) {
		return original + 27;
	}

	// EquipmentSlot.CHEST.getIndex(36)  ordinal 2
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 2))
	private static int modifyClinitChestIndex(int original) {
		return original + 27;
	}

	// EquipmentSlot.HEAD.getIndex(36)   ordinal 3
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 36, ordinal = 3))
	private static int modifyClinitHeadIndex(int original) {
		return original + 27;
	}

	// map key 40 → 67 (OFFHAND)
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 40))
	private static int modifyClinitOffhand(int original) {
		return original + 27;
	}

	// map key 41 → 68 (BODY)
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 41))
	private static int modifyClinitBody(int original) {
		return original + 27;
	}

	// map key 42 → 69 (SADDLE)
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 42))
	private static int modifyClinitSaddle(int original) {
		return original + 27;
	}

	// ── getSlotWithRemainingSpace ─────────────────────────────────────────────
	// explicit getItem(40) offhand check → getItem(67)
	@ModifyConstant(method = "getSlotWithRemainingSpace", constant = @Constant(intValue = 40))
	private int modifyOffhandSlotCheck(int original) {
		return original + 27;
	}
}