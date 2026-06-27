package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

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
                return null;
            });

            context.takeScreenshot("inventoryextended-container-bg-check");
        }
    }
}
