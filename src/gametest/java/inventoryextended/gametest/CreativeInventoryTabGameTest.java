package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

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
                    if (!InventoryMenu.isHotbarSlot(63 + i)) {
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
                Inventory inv = mc.player.getInventory();
                for (int i = 0; i < 9; i++) {
                    inv.setItem(i, new ItemStack(Items.STONE, i + 1));
                }
                for (int i = 9; i < 63; i++) {
                    inv.setItem(i, new ItemStack(Items.DIRT, i + 1));
                }
                return null;
            });

            context.computeOnClient(mc -> {
                MinecraftServer server = mc.getSingleplayerServer();
                if (server == null) {
                    throw new AssertionError("Integrated server not available "
                        + "in singleplayer — FixCreativeSlotRangeCheck may not "
                        + "be loaded for integrated server.");
                }

                ServerPlayer serverPlayer = server.getPlayerList()
                        .getPlayer(mc.player.getUUID());
                if (serverPlayer == null) {
                    throw new AssertionError("ServerPlayer not found in "
                        + "integrated server player list");
                }

                Inventory serverInv = serverPlayer.getInventory();
                int serverSize = serverInv.getContainerSize();
                if (serverSize < 63) {
                    throw new AssertionError(
                        "Integrated server inventory containerSize = "
                        + serverSize + " (expected >= 63). Mixins may not "
                        + "be applied on the integrated server side.");
                }

                for (int i = 0; i < 63; i++) {
                    serverInv.setItem(i, new ItemStack(Items.DIAMOND, 1));
                }

                int accessible = 0;
                for (int i = 0; i < 63; i++) {
                    if (serverInv.getItem(i).is(Items.DIAMOND)) accessible++;
                }

                if (accessible < 63) {
                    throw new AssertionError(
                        "Only " + accessible + "/63 server-side inventory "
                        + "slots accessible in singleplayer integrated server. "
                        + "ExtendPlayerInventory mixin may not be applied.");
                }

                return null;
            });

            context.takeScreenshot("inventoryextended-creative-tab-check");
        }
    }
}
