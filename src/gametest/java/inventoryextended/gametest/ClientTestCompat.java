package inventoryextended.gametest;

import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("UnstableApiUsage")
final class ClientTestCompat {

    private ClientTestCompat() {
    }

    static void waitForChunksRender(TestSingleplayerContext singleplayer) {
        Object context = invokeOrNull(singleplayer, "getConnection");
        if (context == null) {
            context = invokeOrNull(singleplayer, "getClientLevel");
        }
        if (context == null) {
            throw new AssertionError(
                "TestSingleplayerContext has neither getConnection() nor getClientLevel()");
        }

        try {
            Method wait = context.getClass().getMethod("waitForChunksRender");
            wait.invoke(context);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Failed to wait for chunks to render", e);
        }
    }

    private static Object invokeOrNull(TestSingleplayerContext singleplayer, String method) {
        try {
            return TestSingleplayerContext.class.getMethod(method).invoke(singleplayer);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }
}
