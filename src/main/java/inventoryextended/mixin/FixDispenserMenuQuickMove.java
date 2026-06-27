package inventoryextended.mixin;

import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(Generic3x3ContainerScreenHandler.class)
public abstract class FixDispenserMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 45), require = 1)
    private int modifyDispenserSlotCount(int original) {
        return original + 27;
    }
}
