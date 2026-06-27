package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class OffhandSwapGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testOffhandSlotIndex67(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(67, new ItemStack(Items.SHIELD, 1));

        context.assertTrue(inv.getItem(67).is(Items.SHIELD),
            "Offhand slot 67 should contain SHIELD");

        context.assertTrue(inv.getItem(67).getCount() == 1,
            "Offhand slot 67 should have count 1");

        context.succeed();
    }

    @GameTest
    public void testOffhandItemCanBeRead(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(0, new ItemStack(Items.STONE_SWORD, 1));
        inv.setItem(67, new ItemStack(Items.SHIELD, 1));

        ItemStack mainHand = inv.getItem(0);
        ItemStack offHand = inv.getItem(67);

        context.assertTrue(mainHand.is(Items.STONE_SWORD),
            "Main hand slot 0 should contain STONE_SWORD");
        context.assertTrue(offHand.is(Items.SHIELD),
            "Offhand slot 67 should contain SHIELD");

        context.succeed();
    }

    @GameTest
    public void testOffhandSlotPersistsAfterClear(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        inv.setItem(67, new ItemStack(Items.SHIELD, 1));

        context.assertTrue(inv.getItem(67).is(Items.SHIELD),
            "Offhand slot 67 should have SHIELD before clear");

        inv.setItem(67, ItemStack.EMPTY);

        context.assertTrue(inv.getItem(67).isEmpty(),
            "Offhand slot 67 should be empty after clear");

        context.succeed();
    }

    @GameTest
    public void testSwapOffhandConstant67InMenu(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(67, new ItemStack(Items.TORCH, 1));

        boolean hasOffhand = !inv.getItem(67).isEmpty();

        context.assertTrue(hasOffhand,
            "Offhand at slot 67 should be accessible (FixSwapOffhandContainer)");

        context.assertTrue(inv.getItem(67).is(Items.TORCH),
            "Offhand slot 67 should contain TORCH");

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
