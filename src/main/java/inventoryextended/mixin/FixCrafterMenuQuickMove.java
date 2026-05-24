package inventoryextended.mixin;

import net.minecraft.world.inventory.CrafterMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(CrafterMenu.class)
public abstract class FixCrafterMenuQuickMove {

    @ModifyConstant(
            method = "quickMoveStack",
            constant = @Constant(intValue = 45),
            require = 1
    )
    private int modifyCrafterSlotCount(int original) {
        return original + 27;
    }
}
