package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class QuickMoveGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testFurnaceQuickMoveFuelFromInventory(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(9, new ItemStack(Items.COAL, 5));

        menu.quickMoveStack(player, 3);

        context.assertTrue(!furnaceContainer.getItem(1).isEmpty(),
            "COAL should have moved to furnace fuel slot from inventory index 9 (menu slot 3)");
        context.assertTrue(furnaceContainer.getItem(1).is(Items.COAL),
            "Furnace fuel slot should contain COAL");

        context.succeed();
    }

    @GameTest
    public void testFurnaceQuickMoveIngredientFromInventory(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(10, new ItemStack(Items.RAW_IRON, 3));

        menu.quickMoveStack(player, 4);

        context.assertTrue(!furnaceContainer.getItem(0).isEmpty(),
            "RAW_IRON should have moved to furnace ingredient slot from inventory");
        context.assertTrue(furnaceContainer.getItem(0).is(Items.RAW_IRON),
            "Furnace ingredient slot should contain RAW_IRON");

        context.succeed();
    }

    @GameTest
    public void testFurnaceQuickMoveHotbarToFuel(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(0, new ItemStack(Items.COAL, 3));

        menu.quickMoveStack(player, 57);

        context.assertTrue(!furnaceContainer.getItem(1).isEmpty(),
            "COAL should have moved to furnace fuel slot from hotbar slot 0 (menu slot 57)");

        context.succeed();
    }

    @GameTest
    public void testCraftingQuickMoveRange(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(62, new ItemStack(Items.NETHER_STAR, 1));

        CraftingMenu menu = new CraftingMenu(0, inv);

        int sourceSlot = -1;
        for (Slot slot : menu.slots) {
            if (slot.hasItem() && slot.getItem().is(Items.NETHER_STAR)) {
                sourceSlot = slot.index;
                break;
            }
        }
        context.assertTrue(sourceSlot >= 0,
            "CraftingMenu: must find menu slot containing NETHER_STAR from inv 62");

        menu.quickMoveStack(player, sourceSlot);

        boolean moved = !(menu.getSlot(sourceSlot).hasItem()
                && menu.getSlot(sourceSlot).getItem().is(Items.NETHER_STAR));
        context.assertTrue(moved,
            "CraftingMenu: NETHER_STAR from inv 62 should have moved from slot "
                    + sourceSlot);

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
