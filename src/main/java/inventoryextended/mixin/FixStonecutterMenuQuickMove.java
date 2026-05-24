package inventoryextended.mixin;

import net.minecraft.world.inventory.StonecutterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(StonecutterMenu.class)
public abstract class FixStonecutterMenuQuickMove {

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 29)
    )
    private int modifyStonecutterUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 38)
    )
    private int modifyStonecutterSlotCount(int original) {
        return original + 27;
    }
}
