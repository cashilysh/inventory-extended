package inventoryextended.mixin;

import inventoryextended.InventoryExtended;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeInventoryMixin {

    @Shadow
    private Slot destroyItemSlot;

    private static final int HEIGHT_EXTENDED = 194;
    private static final int HEIGHT_VANILLA = 136;
    private static Field imageHeightField;
    private static Field topPosField;

    static {
        try {
            imageHeightField = net.minecraft.client.gui.screens.inventory
                    .AbstractContainerScreen.class.getDeclaredField("imageHeight");
            imageHeightField.setAccessible(true);
            topPosField = net.minecraft.client.gui.screens.inventory
                    .AbstractContainerScreen.class.getDeclaredField("topPos");
            topPosField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ModifyArg(
            method = "handleHotbarLoadOrSave",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"
            ),
            index = 1
    )
    private static int fixCreativeHotbarSync(int slot) {
        return slot + 27;
    }

    @ModifyConstant(
            method = "slotClicked",
            constant = @Constant(intValue = 36),
            require = 1
    )
    private int fixSlotClickedHotbarSave(int original) {
        return original + 27;
    }

    @Inject(
            method = "slotClicked",
            at = @At("TAIL")
    )
    private void syncCreativeHotbarSlot(
            Slot slot,
            int slotIndex,
            int button,
            net.minecraft.world.inventory.ContainerInput clickType,
            CallbackInfo ci
    ) {
        CreativeModeInventoryScreen self =
                (CreativeModeInventoryScreen) (Object) this;
        if (self.isInventoryOpen()) return;
        if (slot == null) return;

        int containerSlot = slot.getContainerSlot();
        if (containerSlot >= 0 && containerSlot < 9) {
            ItemStack stack = slot.getItem().copy();
            Minecraft mc = Minecraft.getInstance();
            mc.gameMode.handleCreativeModeItemAdd(
                    stack, 63 + containerSlot);
        }
    }

    @Inject(
            method = "slotClicked",
            at = @At("HEAD")
    )
    private void debugSlotClicked(
            Slot slot,
            int slotIndex,
            int button,
            net.minecraft.world.inventory.ContainerInput clickType,
            CallbackInfo ci
    ) {
        CreativeModeInventoryScreen self =
                (CreativeModeInventoryScreen) (Object) this;
        if (self.isInventoryOpen()) return;

        String slotClass = slot != null
                ? slot.getClass().getSimpleName()
                : "null";
        boolean hasCarried = !self.getMenu().getCarried().isEmpty();

        if (InventoryExtended.debug) {
            System.out.println("[InventoryExtended] slotClicked: slotIndex=" + slotIndex
                    + " slotClass=" + slotClass
                    + " action=" + clickType
                    + " carried=" + hasCarried);
        }
    }

    @Inject(
            method = "selectTab",
            at = @At("TAIL")
    )
    private void repositionExtendedSlots(
            net.minecraft.world.item.CreativeModeTab tab,
            CallbackInfo ci
    ) {
        CreativeModeInventoryScreen self =
                (CreativeModeInventoryScreen) (Object) this;

        boolean isInventory = self.isInventoryOpen();

        try {
            if (isInventory) {
                imageHeightField.setInt(this, HEIGHT_EXTENDED);
                topPosField.setInt(this,
                        (self.height - HEIGHT_EXTENDED) / 2);
            } else {
                imageHeightField.setInt(this, HEIGHT_VANILLA);
                topPosField.setInt(this,
                        (self.height - HEIGHT_VANILLA) / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isInventory) return;

        ItemPickerMenu menu = (ItemPickerMenu) self.getMenu();

        if (InventoryExtended.debug) {
            System.out.println("[InventoryExtended] selectTab inventory reposition: "
                    + menu.slots.size() + " slots");
        }

        int j = 0;
        for (Slot slot : menu.slots) {
            SlotAccessor a = (SlotAccessor) (Object) slot;
            int oldX = a.getX();
            int oldY = a.getY();

            if (j >= 9 && j < 63) {
                int pos = j - 9;
                int row = pos / 9;
                int col = pos % 9;
                a.setX(9 + col * 18);
                a.setY(54 + row * 18);
                if (InventoryExtended.debug) {
                    System.out.println("[InventoryExtended]   INV " + j
                            + " r=" + row + " c=" + col
                            + " (" + oldX + "," + oldY
                            + ") -> (" + a.getX() + "," + a.getY() + ")");
                }
            } else if (j >= 63 && j < 72) {
                int col = j - 63;
                a.setX(9 + col * 18);
                a.setY(166);
                if (InventoryExtended.debug) {
                    System.out.println("[InventoryExtended]   HOTBAR " + j
                            + " c=" + col
                            + " (" + oldX + "," + oldY
                            + ") -> (" + a.getX() + "," + a.getY() + ")");
                }
            } else if (j == 72) {
                a.setX(35);
                a.setY(20);
                if (InventoryExtended.debug) {
                    System.out.println("[InventoryExtended]   OFFHAND "
                            + " (" + oldX + "," + oldY
                            + ") -> (" + a.getX() + "," + a.getY() + ")");
                }
            }
            j++;
        }

        if (destroyItemSlot != null) {
            SlotAccessor a = (SlotAccessor) (Object) destroyItemSlot;
            a.setX(173);
            a.setY(166);
            if (InventoryExtended.debug) {
                System.out.println("[InventoryExtended]   TRASH -> ("
                        + a.getX() + "," + a.getY() + ")");
            }
        }
    }
}
