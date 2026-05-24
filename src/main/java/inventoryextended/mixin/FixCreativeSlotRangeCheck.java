package inventoryextended.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/*
 * The server's handleSetCreativeModeSlot checks slotNum >= 1 && slotNum <= 45.
 * Vanilla InventoriesMenu has 46 slots (0-45), so 45 = max index.
 * Modded InventoryMenu has 73 slots (0-72), so 72 is the max index.
 */
@Mixin(net.minecraft.server.network.ServerGamePacketListenerImpl.class)
public abstract class FixCreativeSlotRangeCheck {

    @ModifyConstant(
            method = "handleSetCreativeModeSlot",
            constant = @Constant(intValue = 45),
            require = 1
    )
    private int fixCreativeSlotRange(int original) {
        return 72;
    }
}
