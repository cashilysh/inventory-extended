package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.Minecraft;

import inventoryextended.mixin.SlotAccessor;

@SuppressWarnings("UnstableApiUsage")
public class CreativeInventoryTabGameTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientLevel().waitForChunksRender();

            context.computeOnClient(mc -> {
                var player = mc.player;
                Inventory inv = player.getInventory();
                InventoryMenu menu = player.inventoryMenu;

                int containerSize = inv.getContainerSize();
                if (containerSize < 63) {
                    throw new AssertionError(
                        "Client inventory containerSize = " + containerSize
                        + " (expected >= 63)");
                }

                int menuSlots = menu.slots.size();
                if (menuSlots < 73) {
                    throw new AssertionError(
                        "Client InventoryMenu slots = " + menuSlots
                        + " (expected >= 73)");
                }

                for (int i = 0; i < 9; i++) {
                    boolean hotbar = InventoryMenu.isHotbarSlot(63 + i);
                    if (!hotbar) {
                        throw new AssertionError(
                            "isHotbarSlot(" + (63 + i) + ") should be true");
                    }
                }

                if (!InventoryMenu.isHotbarSlot(72)) {
                    throw new AssertionError(
                        "isHotbarSlot(72) should be true (offhand)");
                }

                if (InventoryMenu.isHotbarSlot(62)) {
                    throw new AssertionError(
                        "isHotbarSlot(62) should be false (last inventory slot)");
                }

                return null;
            });

            context.computeOnClient(mc -> {
                for (int i = 0; i < 9; i++) {
                    if (mc.player == null) break;
                    Inventory inv = mc.player.getInventory();
                    inv.setItem(i, new net.minecraft.world.item.ItemStack(
                            net.minecraft.world.item.Items.STONE, i + 1));
                }
                for (int i = 9; i < 63; i++) {
                    if (mc.player == null) break;
                    Inventory inv = mc.player.getInventory();
                    inv.setItem(i, new net.minecraft.world.item.ItemStack(
                            net.minecraft.world.item.Items.DIRT, i + 1));
                }
                return null;
            });

            context.takeScreenshot("inventoryextended-creative-tab-check");
        }
    }
}
