package inventoryextended.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.fabricmc.fabric.impl.transfer.item.PlayerInventoryStorageImpl")
abstract class FixPlayerInventoryStorageImpl {

    @ModifyConstant(
            method = "getHandSlot",
            constant = @Constant(intValue = 40),
            require = 1
    )
    private int fixOffhandSlot(int original) {
        return original + 27;
    }

    @ModifyConstant(
            method = "offer",
            constant = @Constant(intValue = 36),
            require = 1
    )
    private int fixInventorySize(int original) {
        return original + 27;
    }
}