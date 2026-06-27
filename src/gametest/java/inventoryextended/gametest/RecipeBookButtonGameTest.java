package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.FurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;

import java.lang.reflect.Field;

@SuppressWarnings("UnstableApiUsage")
public class RecipeBookButtonGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            context.computeOnClient(mc -> {
                Inventory inv = mc.player.getInventory();
                int containerSize = inv.getContainerSize();

                if (containerSize < 63) {
                    throw new AssertionError(
                        "Client inventory containerSize = " + containerSize
                        + " (expected >= 63, mixin may not have applied)");
                }
                return null;
            });

            context.takeScreenshot("inventoryextended-recipebook-check");
        }
    }
}
