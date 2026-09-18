package inventoryextended.mixin.quickmove;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(MerchantMenu.class)
public abstract class MerchantMenuTradeQuickMove {

    @Shadow
    private Merchant trader;

    @Shadow
    private MerchantContainer tradeContainer;

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void inventoryextended$quickMoveToPayment(Player player, int index,
            CallbackInfoReturnable<ItemStack> cir) {
        if (index < 3) {
            return;
        }

        Slot slot = inventoryextended$slots().get(index);
        if (slot == null || !slot.hasItem()) {
            return;
        }

        ItemStack stack = slot.getItem();
        MerchantOffers offers = this.trader.getOffers();
        if (offers == null || offers.isEmpty()) {
            return;
        }

        ItemStack original = stack.copy();
        for (int paymentSlot = 0; paymentSlot < 2; paymentSlot++) {
            for (MerchantOffer offer : offers) {
                ItemCost cost = paymentSlot == 0
                        ? offer.getItemCostA()
                        : offer.getItemCostB().orElse(null);
                if (cost == null || !cost.test(stack)) {
                    continue;
                }
                if (!inventoryextended$moveIntoPaymentSlot(stack, paymentSlot)) {
                    continue;
                }

                if (stack.isEmpty()) {
                    slot.setByPlayer(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
                cir.setReturnValue(original);
                return;
            }
        }
    }

    private boolean inventoryextended$moveIntoPaymentSlot(ItemStack stack, int paymentSlot) {
        Slot target = inventoryextended$slots().get(paymentSlot);
        ItemStack existing = target.getItem();

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                return false;
            }
            int max = target.getMaxStackSize(existing);
            if (existing.getCount() >= max) {
                return false;
            }
            int total = existing.getCount() + stack.getCount();
            if (total <= max) {
                stack.setCount(0);
                existing.setCount(total);
            } else {
                stack.shrink(max - existing.getCount());
                existing.setCount(max);
            }
            target.setChanged();
            return true;
        }

        if (!target.mayPlace(stack)) {
            return false;
        }
        int max = target.getMaxStackSize(stack);
        ItemStack placed = stack.split(Math.min(stack.getCount(), max));
        target.setByPlayer(placed);
        target.setChanged();
        return true;
    }

    private net.minecraft.core.NonNullList<Slot> inventoryextended$slots() {
        return ((AbstractContainerMenu) (Object) this).slots;
    }
}
