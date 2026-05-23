package inventoryextended.mixin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Repositions the creative screen category-tab hotbar slots
 * from menu indices 45-53 to 63-71 so they match the modded
 * InventoryMenu layout.
 *
 * The ItemPickerMenu constructor creates:
 *   [0..44]  Creative grid (CustomCreativeSlot)
 *   [45..53] Hotbar (9 regular Slots added by addInventoryHotbarSlots)
 *
 * After this mixin:
 *   [0..44]  Creative grid (unchanged)
 *   [45..62] 18 invisible spacer slots - reference playerInventory
 *            at the matching container indices so they sync actual
 *            inventory items instead of clearing inventory slots.
 *   [63..71] Hotbar (moved from 45-53)
 */
@Mixin(CreativeModeInventoryScreen.ItemPickerMenu.class)
public abstract class CreativeScreenHandlerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void fixHotbarSlotPositions(Player player, CallbackInfo ci) {
        CreativeModeInventoryScreen.ItemPickerMenu menu =
                (CreativeModeInventoryScreen.ItemPickerMenu) (Object) this;
        NonNullList<Slot> slots = menu.slots;
        Inventory inv = player.getInventory();

        int originalSize = slots.size();
        System.out.println("[InventoryExtended] ItemPickerMenu.<init> TAIL: slots.size="
                + originalSize + "  (0.." + (originalSize - 1) + ")");

        int hotbarCount = 9;
        if (originalSize <= 45 + hotbarCount) {
            List<Slot> hotbar = new ArrayList<>();
            for (int i = 0; i < hotbarCount; i++) {
                hotbar.add(slots.remove(slots.size() - 1));
            }
            System.out.println("[InventoryExtended]   removed " + hotbar.size()
                    + " hotbar slots, now size=" + slots.size());

            int spacersNeeded = 63 - slots.size();
            System.out.println("[InventoryExtended]   adding " + spacersNeeded
                    + " spacer slots for indices " + slots.size()
                    + ".." + (slots.size() + spacersNeeded - 1));
            for (int i = 0; i < spacersNeeded; i++) {
                int containerIndex = 45 + i;
                slots.add(new SpacerSlot(inv, containerIndex));
            }

            for (Slot s : hotbar) {
                slots.add(s);
            }
            System.out.println("[InventoryExtended]   final slots.size=" + slots.size()
                    + "  hotbar now at ["
                    + (slots.size() - hotbarCount) + ".."
                    + (slots.size() - 1) + "]");
        } else {
            System.out.println("[InventoryExtended]   WARNING: unexpected slot count "
                    + originalSize + ", skipping fix");
        }
    }

    /*
     * Invisible spacer that references the player's inventory at the
     * correct container index so broadcastChanges sends the real item
     * instead of clearing inventory slots on the server.
     *
     * Positioned off-screen and rejects all clicks.
     */
    private static class SpacerSlot extends Slot {
        SpacerSlot(Inventory inventory, int containerIndex) {
            super(inventory, containerIndex, -2000, -2000);
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }

        @Override
        public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }
}
