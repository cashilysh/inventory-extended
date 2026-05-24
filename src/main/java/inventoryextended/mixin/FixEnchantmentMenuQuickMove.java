package inventoryextended.mixin;

import net.minecraft.world.inventory.EnchantmentMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(EnchantmentMenu.class)
public abstract class FixEnchantmentMenuQuickMove {

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 38),
            require = 1
    )
    private int modifyEnchantmentSlotCount(int original) {
        return original + 27;
    }
}
