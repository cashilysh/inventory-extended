package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class DeathDropGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testExtendedSlotsAccessibleForDrop(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 9; i < 63; i++) {
            inv.setItem(i, new ItemStack(Items.DIAMOND, 1));
        }

        int nonEmptyCount = 0;
        for (int i = 9; i < 63; i++) {
            if (!inv.getItem(i).isEmpty()) nonEmptyCount++;
        }

        context.assertTrue(nonEmptyCount == 54,
            "All 54 extended inventory slots (9-62) should contain items, "
                    + "found " + nonEmptyCount + " non-empty slots");

        context.succeed();
    }

    @GameTest
    public void testHotbarItemsAccessible(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, new ItemStack(Items.STONE, 1));
        }

        int hotbarCount = 0;
        for (int i = 0; i < 9; i++) {
            if (inv.getItem(i).is(Items.STONE)) hotbarCount++;
        }

        context.assertTrue(hotbarCount == 9,
            "All 9 hotbar slots (0-8) should contain STONE, found "
                    + hotbarCount);

        context.succeed();
    }

    @GameTest
    public void testArmorOffhandAccessible(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(63, new ItemStack(Items.IRON_BOOTS, 1));
        inv.setItem(64, new ItemStack(Items.IRON_LEGGINGS, 1));
        inv.setItem(65, new ItemStack(Items.IRON_CHESTPLATE, 1));
        inv.setItem(66, new ItemStack(Items.IRON_HELMET, 1));
        inv.setItem(67, new ItemStack(Items.SHIELD, 1));

        context.assertTrue(inv.getItem(63).is(Items.IRON_BOOTS),
            "Slot 63 should have IRON_BOOTS");
        context.assertTrue(inv.getItem(64).is(Items.IRON_LEGGINGS),
            "Slot 64 should have IRON_LEGGINGS");
        context.assertTrue(inv.getItem(65).is(Items.IRON_CHESTPLATE),
            "Slot 65 should have IRON_CHESTPLATE");
        context.assertTrue(inv.getItem(66).is(Items.IRON_HELMET),
            "Slot 66 should have IRON_HELMET");
        context.assertTrue(inv.getItem(67).is(Items.SHIELD),
            "Slot 67 should have SHIELD");

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
