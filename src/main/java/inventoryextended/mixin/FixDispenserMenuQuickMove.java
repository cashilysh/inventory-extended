package inventoryextended.mixin;

import net.minecraft.world.inventory.DispenserMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(DispenserMenu.class)
public abstract class FixDispenserMenuQuickMove {

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 45),
            require = 1
    )
    private int modifyDispenserSlotCount(int original) {
        return original + 27;
    }
}
