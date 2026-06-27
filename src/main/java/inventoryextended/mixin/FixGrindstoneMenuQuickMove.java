package inventoryextended.mixin;

import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(GrindstoneScreenHandler.class)
public abstract class FixGrindstoneMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 30))
    private int modifyGrindstoneUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 39))
    private int modifyGrindstoneSlotCount(int original) {
        return original + 27;
    }
}
