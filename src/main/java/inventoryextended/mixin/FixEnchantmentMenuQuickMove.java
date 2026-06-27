package inventoryextended.mixin;

import net.minecraft.screen.EnchantmentScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(EnchantmentScreenHandler.class)
public abstract class FixEnchantmentMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 38), require = 1)
    private int modifyEnchantmentSlotCount(int original) {
        return original + 27;
    }
}
