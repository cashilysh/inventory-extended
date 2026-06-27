package inventoryextended.mixin;

import net.minecraft.screen.StonecutterScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(StonecutterScreenHandler.class)
public abstract class FixStonecutterMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 29))
    private int modifyStonecutterUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 38))
    private int modifyStonecutterSlotCount(int original) {
        return original + 27;
    }
}
