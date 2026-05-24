package inventoryextended.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(AbstractContainerMenu.class)
public abstract class GlobalDrawExtraSlots {

    @Shadow
    protected abstract Slot addSlot(Slot slot);

    // 3 inventory rows → 6
    @ModifyConstant(method = "addInventoryExtendedSlots", constant = @Constant(intValue = 3))
    private int modifyInventoryRows(int original) {
        return 6;
    }

    // Hotbar Y-offset: 58 → 112
    @ModifyConstant(method = "addStandardInventorySlots", constant = @Constant(intValue = 58))
    private int modifyHotbarOffset(int original) {
        return 112;
    }

    // doClick SWAP handler: offhand hotkey buttonNum == 40 → 67
    @ModifyConstant(method = "doClick", constant = @Constant(intValue = 40))
    private int modifyOffhandSwapKey(int original) {
        return original + 27;
    }
}