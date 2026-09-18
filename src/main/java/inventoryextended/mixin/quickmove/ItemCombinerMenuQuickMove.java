package inventoryextended.mixin.quickmove;

import net.minecraft.world.inventory.ItemCombinerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMenuQuickMove {

    @ModifyConstant(
            method = "getInventorySlotEnd",
            constant = @Constant(intValue = 27)
    )
    private int modifyItemCombinerInventorySlotEnd(int original) {
        return original + 27;
    }
}
