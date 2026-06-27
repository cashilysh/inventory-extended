package inventoryextended.mixin;

import inventoryextended.InventoryExtended;
import java.lang.reflect.Field;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryMixin {

    private static final int HEIGHT_EXTENDED = 194;
    private static final int HEIGHT_VANILLA = 136;
    private static Field backgroundHeightField;
    private static Field topPosField;
    private static Field deleteItemSlotField;

    static {
        try {
            backgroundHeightField = net.minecraft.client.gui.screen.ingame
                    .HandledScreen.class.getDeclaredField("backgroundHeight");
            backgroundHeightField.setAccessible(true);
            topPosField = net.minecraft.client.gui.screen.ingame
                    .HandledScreen.class.getDeclaredField("y");
            topPosField.setAccessible(true);
            deleteItemSlotField = net.minecraft.client.gui.screen.ingame
                    .CreativeInventoryScreen.class.getDeclaredField("deleteItemSlot");
            deleteItemSlotField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ModifyArg(
            method = "onHotbarKeyPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;clickCreativeStack(Lnet/minecraft/item/ItemStack;I)V"
            ),
            index = 1
    )
    private static int fixCreativeHotbarSync(int slot) {
        return slot + 27;
    }

    @ModifyConstant(
            method = "onMouseClick",
            constant = @Constant(intValue = 36),
            require = 1
    )
    private int fixSlotClickedHotbarSave(int original) {
        return original + 27;
    }

    @Inject(
            method = "onMouseClick",
            at = @At("TAIL")
    )
    private void syncCreativeHotbarSlot(
            Slot slot,
            int slotId,
            int button,
            net.minecraft.screen.slot.SlotActionType actionType,
            CallbackInfo ci
    ) {
        CreativeInventoryScreen self =
                (CreativeInventoryScreen) (Object) this;
        CreativeScreenHandler handler = (CreativeScreenHandler) self.getScreenHandler();
        if (handler.slots.size() > 54) return;
        if (slot == null) return;

        int containerSlot = slot.id;
        if (containerSlot >= 0 && containerSlot < 9) {
            ItemStack stack = slot.getStack().copy();
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.interactionManager.clickCreativeStack(
                    stack, 63 + containerSlot);
        }
    }

    @Inject(
            method = "setSelectedTab",
            at = @At("TAIL")
    )
    private void repositionExtendedSlots(
            ItemGroup tab,
            CallbackInfo ci
    ) {
        CreativeInventoryScreen self =
                (CreativeInventoryScreen) (Object) this;

        CreativeScreenHandler screenHandler =
                (CreativeScreenHandler) self.getScreenHandler();
        boolean isInventory = screenHandler.slots.size() > 54;

        try {
            if (isInventory) {
                backgroundHeightField.setInt(this, HEIGHT_EXTENDED);
                topPosField.setInt(this,
                        (self.height - HEIGHT_EXTENDED) / 2);
            } else {
                backgroundHeightField.setInt(this, HEIGHT_VANILLA);
                topPosField.setInt(this,
                        (self.height - HEIGHT_VANILLA) / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isInventory) return;

        if (InventoryExtended.debug) {
            System.out.println("[InventoryExtended] setSelectedTab inventory reposition: "
                    + screenHandler.slots.size() + " slots");
        }

        java.util.List<Slot> slotList = screenHandler.slots;
        int j = 0;
        for (Slot slot : slotList) {
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

        try {
            if (deleteItemSlotField != null) {
                Slot destroyItemSlot = (Slot) deleteItemSlotField.get(this);
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
        } catch (Exception e) {
            if (InventoryExtended.debug) {
                System.out.println("[InventoryExtended] Could not reposition destroy slot: " + e);
            }
        }
    }
}