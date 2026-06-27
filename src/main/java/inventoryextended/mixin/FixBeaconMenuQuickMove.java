package inventoryextended.mixin;

import net.minecraft.screen.BeaconScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(BeaconScreenHandler.class)
public abstract class FixBeaconMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 28))
    private int modifyBeaconUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 37))
    private int modifyBeaconSlotCount(int original) {
        return original + 27;
    }
}
