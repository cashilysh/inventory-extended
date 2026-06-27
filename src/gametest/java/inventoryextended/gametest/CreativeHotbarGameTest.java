package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings("UnstableApiUsage")
public class CreativeHotbarGameTest implements FabricClientGameTest {

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
                        + " (expected >= 63, mixin may not have applied)");
                }

                int menuSlots = menu.slots.size();
                if (menuSlots < 73) {
                    throw new AssertionError(
                        "Client InventoryMenu slots = " + menuSlots
                        + " (expected >= 73, RemapPlayerSlots mixin may not have applied)");
                }

                boolean hotbar63 = InventoryMenu.isHotbarSlot(63);
                boolean hotbar62 = InventoryMenu.isHotbarSlot(62);
                boolean hotbar72 = InventoryMenu.isHotbarSlot(72);

                if (!hotbar63) {
                    throw new AssertionError(
                        "isHotbarSlot(63) should be true on client side");
                }
                if (hotbar62) {
                    throw new AssertionError(
                        "isHotbarSlot(62) should be false on client side");
                }
                if (!hotbar72) {
                    throw new AssertionError(
                        "isHotbarSlot(72) should be true (offhand) on client side");
                }

                return null;
            });

            context.takeScreenshot("inventoryextended-creative-check");
        }
    }
}
