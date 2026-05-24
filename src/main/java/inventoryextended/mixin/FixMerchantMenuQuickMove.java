package inventoryextended.mixin;

import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(MerchantMenu.class)
public abstract class FixMerchantMenuQuickMove {

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 30)
    )
    private int modifyMerchantUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 39)
    )
    private int modifyMerchantSlotCount(int original) {
        return original + 27;
    }
}
