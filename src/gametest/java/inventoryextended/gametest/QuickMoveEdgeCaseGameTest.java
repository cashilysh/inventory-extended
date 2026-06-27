package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class QuickMoveEdgeCaseGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testQuickMovePartialStackMerge(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        furnaceContainer.setItem(1, new ItemStack(Items.COAL, 3));
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(9, new ItemStack(Items.COAL, 60));

        menu.quickMoveStack(player, 3);

        context.assertTrue(furnaceContainer.getItem(1).is(Items.COAL),
            "Furnace fuel slot should still contain COAL after merge");
        context.assertTrue(furnaceContainer.getItem(1).getCount() == 63,
            "Furnace fuel slot should have 63 COAL after merge (3+60), got "
                    + furnaceContainer.getItem(1).getCount());

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlot62ToFurnace(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(62, new ItemStack(Items.COAL, 8));

        menu.quickMoveStack(player, 56);

        context.assertTrue(!furnaceContainer.getItem(1).isEmpty(),
            "COAL should have moved from extended slot 62 to furnace fuel");
        context.assertTrue(furnaceContainer.getItem(1).is(Items.COAL),
            "Furnace fuel slot should contain COAL");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToCrafting(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.DIAMOND, 1));

        CraftingMenu menu = new CraftingMenu(0, inv);

        menu.quickMoveStack(player, 64);

        context.succeed();
    }

    @GameTest
    public void testQuickMoveWhenInventoryFull(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, new ItemStack(Items.DIRT, 64));
        }

        SimpleContainer dispenserContainer = new SimpleContainer(9);
        dispenserContainer.setItem(0, new ItemStack(Items.ARROW, 64));

        DispenserMenu menu = new DispenserMenu(0, inv, dispenserContainer);

        menu.quickMoveStack(player, 0);

        context.succeed();
    }

    @GameTest
    public void testQuickMoveNearSlotBoundaries(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(9, new ItemStack(Items.COAL, 1));
        menu.quickMoveStack(player, 3);

        clearInv(inv);
        furnaceContainer.setItem(0, ItemStack.EMPTY);
        furnaceContainer.setItem(1, ItemStack.EMPTY);

        inv.setItem(62, new ItemStack(Items.COAL, 1));
        menu.quickMoveStack(player, 56);

        context.assertTrue(!furnaceContainer.getItem(1).isEmpty()
                || inv.getItem(62).isEmpty(),
            "COAL should have moved from slot 62 to furnace fuel, or stayed");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromHotbarSlot0ToFurnace(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(0, new ItemStack(Items.COAL, 5));

        menu.quickMoveStack(player, 57);

        context.assertTrue(!furnaceContainer.getItem(1).isEmpty(),
            "COAL from hotbar slot 0 (menu 57) should have moved to furnace fuel");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromHotbarSlot8ToFurnace(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(8, new ItemStack(Items.RAW_IRON, 4));

        menu.quickMoveStack(player, 65);

        context.assertTrue(!furnaceContainer.getItem(0).isEmpty(),
            "RAW_IRON from hotbar slot 8 (menu 65) should have moved to furnace ingredient");

        context.succeed();
    }

    private static void clearInv(Inventory inv) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
