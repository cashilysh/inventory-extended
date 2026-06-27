package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.InventoryMenu;

import java.lang.reflect.Field;

@SuppressWarnings("UnstableApiUsage")
public class ContainerBackgroundGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            context.computeOnClient(mc -> {
                if (mc.player == null) {
                    throw new AssertionError("Player is null on client");
                }

                var inv = mc.player.getInventory();
                int containerSize = inv.getContainerSize();
                if (containerSize < 63) {
                    throw new AssertionError(
                        "Client inventory containerSize = " + containerSize
                        + " (expected >= 63)");
                }

                InventoryMenu menu = mc.player.inventoryMenu;
                if (menu.slots.size() < 73) {
                    throw new AssertionError(
                        "Client InventoryMenu slots = " + menu.slots.size()
                        + " (expected >= 73, RemapPlayerSlots mixin may not have applied)");
                }

                return null;
            });

            context.computeOnClient(mc -> {
                try {
                    Field f = AbstractContainerScreen.class
                            .getDeclaredField("imageHeight");
                    f.setAccessible(true);

                    InventoryScreen screen = new InventoryScreen(mc.player);
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

            context.takeScreenshot("inventoryextended-container-bg-check");
        }
    }
}
