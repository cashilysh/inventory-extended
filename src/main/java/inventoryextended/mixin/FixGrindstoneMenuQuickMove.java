package inventoryextended.mixin;

import net.minecraft.world.inventory.GrindstoneMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(GrindstoneMenu.class)
public abstract class FixGrindstoneMenuQuickMove {

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 30)
    )
    private int modifyGrindstoneUseRowStart(int original) {
        return original + 27;
    }

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 39)
    )
    private int modifyGrindstoneSlotCount(int original) {
        return original + 27;
    }
}
