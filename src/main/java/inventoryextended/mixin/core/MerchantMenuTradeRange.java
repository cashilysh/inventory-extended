package inventoryextended.mixin.core;

import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(MerchantMenu.class)
public abstract class MerchantMenuTradeRange {

    @ModifyConstant(
            method = "tryMoveItems",
            constant = @Constant(intValue = 39)
    )
    private int modifyMerchantTryMoveRange(int original) {
        return original + 27;
    }

    @ModifyConstant(
            method = "moveFromInventoryToPaymentSlot",
            constant = @Constant(intValue = 39)
    )
    private int modifyMerchantPaymentRange(int original) {
        return original + 27;
    }
}
