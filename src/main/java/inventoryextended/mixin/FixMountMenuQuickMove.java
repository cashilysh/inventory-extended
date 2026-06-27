package inventoryextended.mixin;

import net.minecraft.screen.MountScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(MountScreenHandler.class)
public abstract class FixMountMenuQuickMove {

    @ModifyConstant(method = "quickMove", constant = @Constant(intValue = 27), require = 1)
    private int modifyMountInventoryRows(int original) {
        return original + 27;
    }
}
