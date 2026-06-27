package inventoryextended.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(net.minecraft.client.gui.screen.ingame.BookEditScreen.class)
public abstract class FixBookEditScreen {

    @ModifyConstant(
            method = "finalizeBook",
            constant = @Constant(intValue = 40),
            require = 1
    )
    private int fixEditBookOffhandSlot(int original) {
        return original + 27;
    }
}
