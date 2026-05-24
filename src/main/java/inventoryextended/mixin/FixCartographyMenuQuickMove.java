package inventoryextended.mixin;

import net.minecraft.world.inventory.CartographyTableMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(CartographyTableMenu.class)
public abstract class FixCartographyMenuQuickMove {

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 30)
    )
    private int modifyCartographyUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 39)
    )
    private int modifyCartographySlotCount(int original) {
        return original + 27;
    }
}
