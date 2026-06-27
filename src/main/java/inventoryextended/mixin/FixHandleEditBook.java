package inventoryextended.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class FixHandleEditBook {

    @ModifyConstant(
            method = "onBookUpdate",
            constant = @Constant(intValue = 40),
            require = 1
    )
    private int fixEditBookOffhandSlot(int original) {
        return original + 27;
    }
}
