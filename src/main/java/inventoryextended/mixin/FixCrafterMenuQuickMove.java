package inventoryextended.mixin;

import net.minecraft.screen.CrafterScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(CrafterScreenHandler.class)
public abstract class FixCrafterMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 45), require = 1)
    private int modifyCrafterSlotCount(int original) {
        return original + 27;
    }
}
