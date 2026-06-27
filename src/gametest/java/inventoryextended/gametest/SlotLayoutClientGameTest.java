package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import inventoryextended.mixin.SlotAccessor;

import java.lang.reflect.Field;

@SuppressWarnings("UnstableApiUsage")
public class SlotLayoutClientGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            context.computeOnClient(mc -> {
                var player = mc.player;
                InventoryMenu menu = player.inventoryMenu;

                if (menu.slots.size() < 73) {
                    throw new AssertionError(
                        "InventoryMenu slots = " + menu.slots.size()
                        + " (expected >= 73)");
                }

                Slot firstInvSlot = menu.getSlot(9);
                Slot firstHotbarSlot = menu.getSlot(63);

                int invY = firstInvSlot.y;
                int hotbarY = firstHotbarSlot.y;

                if (hotbarY <= invY) {
                    throw new AssertionError(
                        "GlobalDrawExtraSlots: hotbar Y (" + hotbarY
                        + ") should be greater than inventory Y (" + invY + ")");
                }

                int gap = hotbarY - invY;
                if (gap < 100) {
                    throw new AssertionError(
                        "GlobalDrawExtraSlots: hotbar gap should be >= 112, got "
                        + gap + " (invY=" + invY + ", hotbarY=" + hotbarY + ")");
                }

                Slot lastInvSlot = menu.getSlot(62);
                int lastInvY = lastInvSlot.y;
                if (lastInvY >= hotbarY) {
                    throw new AssertionError(
                        "GlobalDrawExtraSlots: last inventory slot Y (" + lastInvY
                        + ") should be below hotbar Y (" + hotbarY + ")");
                }

                return null;
            });

            context.computeOnClient(mc -> {
                try {
                    Field f = AbstractContainerScreen.class
                            .getDeclaredField("imageHeight");
                    f.setAccessible(true);

                    var screen = new net.minecraft.client.gui.screens.inventory
                            .InventoryScreen(mc.player);
                    int height = (int) f.get(screen);

                    if (height != 226) {
                        throw new AssertionError(
                            "IncreaseGlobalBackgroundHeight: imageHeight should be 226, got "
                            + height);
                    }
                } catch (NoSuchFieldException e) {
                    throw new AssertionError(
                        "AbstractContainerScreen.imageHeight field not found: "
                        + e.getMessage());
                } catch (IllegalAccessException e) {
                    throw new AssertionError(
                        "Cannot access imageHeight field: " + e.getMessage());
                }
                return null;
            });

            context.computeOnClient(mc -> {
                InventoryMenu menu = mc.player.inventoryMenu;
                Slot slot = menu.getSlot(0);

                SlotAccessor accessor = (SlotAccessor) slot;

                int origX = accessor.getX();
                int origY = accessor.getY();

                accessor.setX(origX + 50);
                accessor.setY(origY + 50);

                if (accessor.getX() != origX + 50) {
                    throw new AssertionError(
                        "SlotAccessor: getX() after setX() should be " + (origX + 50)
                        + ", got " + accessor.getX());
                }
                if (accessor.getY() != origY + 50) {
                    throw new AssertionError(
                        "SlotAccessor: getY() after setY() should be " + (origY + 50)
                        + ", got " + accessor.getY());
                }

                accessor.setX(origX);
                accessor.setY(origY);

                if (accessor.getX() != origX || accessor.getY() != origY) {
                    throw new AssertionError(
                        "SlotAccessor: position not restored correctly");
                }

                return null;
            });

            context.takeScreenshot("inventoryextended-layout-check");
        }
    }
}
