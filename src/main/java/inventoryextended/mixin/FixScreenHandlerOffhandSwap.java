package inventoryextended.mixin;

import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(ScreenHandler.class)
public abstract class FixScreenHandlerOffhandSwap {

    @ModifyConstant(
            method = "onSlotClick",
            constant = @Constant(intValue = 40),
            require = 0
    )
    private int modifyOffhandSwapSlot(int original) {
        return 67;
    }
}
