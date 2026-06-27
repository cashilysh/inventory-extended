package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class CreativeSurvivalTransitionGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testAllSlotsAccessibleInCreative(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.CREATIVE);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < 63; i++) {
            inv.setItem(i, new ItemStack(Items.DIAMOND, 1));
        }

        int accessibleCount = 0;
        for (int i = 0; i < 63; i++) {
            if (inv.getItem(i).is(Items.DIAMOND)) accessibleCount++;
        }

        context.assertTrue(accessibleCount == 63,
            "All 63 main slots should be accessible in creative mode, got "
                    + accessibleCount);

        context.succeed();
    }

    @GameTest
    public void testAllSlotsAccessibleInSurvival(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < 63; i++) {
            inv.setItem(i, new ItemStack(Items.DIRT, 1));
        }

        int accessibleCount = 0;
        for (int i = 0; i < 63; i++) {
            if (inv.getItem(i).is(Items.DIRT)) accessibleCount++;
        }

        context.assertTrue(accessibleCount == 63,
            "All 63 main slots should be accessible in survival mode, got "
                    + accessibleCount);

        context.succeed();
    }

    @GameTest
    public void testExtendedSlotsAreSequential(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < 63; i++) {
            inv.setItem(i, new ItemStack(Items.DIAMOND, i + 1));
        }

        for (int i = 0; i < 63; i++) {
            ItemStack stack = inv.getItem(i);
            context.assertTrue(stack.is(Items.DIAMOND),
                "Slot " + i + " should contain DIAMOND");
            context.assertTrue(stack.getCount() == i + 1,
                "Slot " + i + " count should be " + (i + 1)
                        + ", got " + stack.getCount());
        }

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
