package inventoryextended.mixin;

import net.minecraft.screen.BrewingStandScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(BrewingStandScreenHandler.class)
public abstract class FixBrewingStandMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 32))
    private int modifyBrewingUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 41))
    private int modifyBrewingSlotCount(int original) {
        return original + 27;
    }
}
