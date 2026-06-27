package inventoryextended.mixin;

import net.minecraft.screen.LoomScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(LoomScreenHandler.class)
public abstract class FixLoomMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 31))
    private int modifyLoomUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 40))
    private int modifyLoomSlotCount(int original) {
        return original + 27;
    }
}
