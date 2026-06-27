package inventoryextended.mixin;

import net.minecraft.screen.AbstractFurnaceScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class FixFurnaceMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 30))
    private int modifyFurnaceUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 39))
    private int modifyFurnaceSlotCount(int original) {
        return original + 27;
    }
}
