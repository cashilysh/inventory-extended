package inventoryextended.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(net.minecraft.server.network.ServerPlayNetworkHandler.class)
public abstract class FixCreativeSlotRangeCheck {

    @ModifyConstant(
            method = "onCreativeInventoryAction",
            constant = @Constant(intValue = 45),
            require = 1
    )
    private int fixCreativeSlotRange(int original) {
        return 72;
    }
}
