package inventoryextended.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(HandledScreen.class)
public abstract class FixSwapOffhandContainer {

    @ModifyConstant(
        method = "handleHotbarKeyPressed",
        constant = @Constant(intValue = 40)
    )
    private int modifySwapOffhandAction(int original) {
        return original + 27;
    }
}
