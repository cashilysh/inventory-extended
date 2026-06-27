package inventoryextended.mixin;

import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(CraftingScreenHandler.class)
public abstract class FixCraftingMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 37))
    private int modifyCraftingUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 46))
    private int modifyCraftingSlotCount(int original) {
        return original + 27;
    }
}
