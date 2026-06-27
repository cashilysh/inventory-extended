package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.InventoryMenu;

import java.lang.reflect.Field;

@SuppressWarnings("UnstableApiUsage")
public class RecipeBookButtonGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            context.computeOnClient(mc -> {
                var inv = mc.player.getInventory();
                int containerSize = inv.getContainerSize();

                if (containerSize < 63) {
                    throw new AssertionError(
                        "Client inventory containerSize = " + containerSize
                        + " (expected >= 63, mixin may not have applied)");
                }

                InventoryMenu menu = mc.player.inventoryMenu;
                if (menu.slots.size() < 73) {
                    throw new AssertionError(
                        "Client InventoryMenu slots = " + menu.slots.size()
                        + " (expected >= 73)");
                }

                return null;
            });

            context.computeOnClient(mc -> {
                InventoryScreen screen = new InventoryScreen(mc.player);

                try {
                    Field f = AbstractContainerScreen.class
                            .getDeclaredField("imageHeight");
                    f.setAccessible(true);
                    int height = (int) f.get(screen);
                    if (height != 226) {
                        throw new AssertionError(
                            "IncreaseGlobalBackgroundHeight: InventoryScreen imageHeight should be 226, got "
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

            context.takeScreenshot("inventoryextended-recipebook-check");
        }
    }
}
