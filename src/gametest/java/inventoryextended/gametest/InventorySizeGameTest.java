package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class InventorySizeGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testInventoryHasAtLeast63MainSlots(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        context.assertTrue(inv.getContainerSize() >= 63,
            "Inventory should have at least 63 slots, got " + inv.getContainerSize());

        context.succeed();
    }

    @GameTest
    public void testPlaceItemsInAllMainSlots(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < 63; i++) {
            ItemStack stack = new ItemStack(Items.DIAMOND, i + 1);
            inv.setItem(i, stack.copy());
        }

        for (int i = 0; i < 63; i++) {
            ItemStack retrieved = inv.getItem(i);
            context.assertTrue(!retrieved.isEmpty(),
                "Slot " + i + " should not be empty");
            context.assertTrue(retrieved.getItem() == Items.DIAMOND,
                "Slot " + i + " should contain diamond, got " + retrieved.getItem());
            context.assertTrue(retrieved.getCount() == i + 1,
                "Slot " + i + " count should be " + (i + 1) + ", got " + retrieved.getCount());
        }

        context.succeed();
    }

    @GameTest
    public void testHotbarSlotsAreCorrectRange(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, new ItemStack(Items.STONE, i + 1));
        }
        for (int i = 9; i < 63; i++) {
            inv.setItem(i, new ItemStack(Items.DIRT, i + 1));
        }

        context.assertTrue(inv.getItem(0).is(Items.STONE),
            "Hotbar slot 0 should be STONE");
        context.assertTrue(inv.getItem(8).is(Items.STONE),
            "Hotbar slot 8 should be STONE");
        context.assertTrue(inv.getItem(9).is(Items.DIRT),
            "Inventory slot 9 should be DIRT (not hotbar)");
        context.assertTrue(inv.getItem(62).is(Items.DIRT),
            "Inventory slot 62 should be DIRT (last extended slot)");

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
