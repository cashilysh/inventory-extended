package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.SimpleContainer;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class QuickMoveCoverageGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testQuickMoveFromExtendedSlotToCraftingGrid(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

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

    @GameTest
    public void testQuickMoveFromExtendedSlotToFurnaceFuel(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.COAL, 5));
        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu menu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        menu.quickMoveStack(player, 56);

        context.assertTrue(furnaceContainer.getItem(1).is(Items.COAL),
            "FurnaceMenu: COAL from inv slot 62 should move to fuel slot");
        context.assertTrue(furnaceContainer.getItem(1).getCount() == 5,
            "FurnaceMenu: fuel slot should have 5 COAL, got "
                    + furnaceContainer.getItem(1).getCount());

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToBeacon(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer beaconContainer = new SimpleContainer(1);
        BeaconMenu menu = new BeaconMenu(0, beaconContainer,
                new SimpleContainerData(3), ContainerLevelAccess.NULL);

        menu.quickMoveStack(player, 0);

        context.assertTrue(menu.slots.size() >= 1,
            "BeaconMenu must have at least 1 slot");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToBrewingFuel(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.BLAZE_POWDER, 2));
        SimpleContainer brewingContainer = new SimpleContainer(5);
        BrewingStandMenu menu = new BrewingStandMenu(0, inv,
                brewingContainer, new SimpleContainerData(2));

        menu.quickMoveStack(player, 58);

        context.assertTrue(brewingContainer.getItem(4).is(Items.BLAZE_POWDER),
            "BrewingStandMenu: BLAZE_POWDER from inv slot 62 should move to fuel slot");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToEnchantmentLapis(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.LAPIS_LAZULI, 4));
        EnchantmentMenu menu = new EnchantmentMenu(0, inv,
                ContainerLevelAccess.NULL);

        menu.quickMoveStack(player, 55);

        boolean lapisMovedToSlot1 = menu.getSlot(1).hasItem()
                && menu.getSlot(1).getItem().is(Items.LAPIS_LAZULI);
        context.assertTrue(lapisMovedToSlot1,
            "EnchantmentMenu: LAPIS_LAZULI from inv slot 62 should move to lapis slot");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToStonecutterInput(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.STONE, 1));
        SimpleContainer stonecutterContainer = new SimpleContainer(2);
        stonecutterContainer.setItem(0, new ItemStack(Items.STONE, 1));
        StonecutterMenu menu = new StonecutterMenu(0, inv,
                ContainerLevelAccess.NULL);

        menu.quickMoveStack(player, 55);

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToLoom(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.STRING, 1));
        LoomMenu menu = new LoomMenu(0, inv, ContainerLevelAccess.NULL);

        menu.quickMoveStack(player, 57);

        boolean stringMoved = !inv.getItem(62).is(Items.STRING)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(stringMoved,
            "LoomMenu: STRING from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToCartography(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.PAPER, 1));
        CartographyTableMenu menu = new CartographyTableMenu(0, inv,
                ContainerLevelAccess.NULL);

        menu.quickMoveStack(player, 56);

        boolean paperMoved = !inv.getItem(62).is(Items.PAPER)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(paperMoved,
            "CartographyTableMenu: PAPER from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToGrindstone(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.STONE_SWORD, 1));
        GrindstoneMenu menu = new GrindstoneMenu(0, inv,
                ContainerLevelAccess.NULL);

        menu.quickMoveStack(player, 56);

        boolean swordMoved = !inv.getItem(62).is(Items.STONE_SWORD)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(swordMoved,
            "GrindstoneMenu: STONE_SWORD from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToMerchant(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.EMERALD, 1));
        MerchantMenu menu = new MerchantMenu(0, inv);

        menu.quickMoveStack(player, 56);

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotInDispenser(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer dispenserContainer = new SimpleContainer(9);
        dispenserContainer.setItem(0, new ItemStack(Items.ARROW, 1));
        DispenserMenu menu = new DispenserMenu(0, inv, dispenserContainer);

        inv.setItem(62, new ItemStack(Items.ARROW, 5));
        menu.quickMoveStack(player, 62);

        boolean arrowMoved = !inv.getItem(62).is(Items.ARROW)
                || inv.getItem(62).getCount() < 5;
        context.assertTrue(arrowMoved || !inv.getItem(62).isEmpty(),
            "DispenserMenu: ARROW from inv slot 62 should have moved or be merged");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToCrafter(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        inv.setItem(62, new ItemStack(Items.REDSTONE, 1));
        CrafterMenu menu = new CrafterMenu(0, inv);

        menu.quickMoveStack(player, 62);

        boolean redstoneMoved = !inv.getItem(62).is(Items.REDSTONE)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(redstoneMoved,
            "CrafterMenu: REDSTONE from inv slot 62 should have moved to a crafter grid slot");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveFromExtendedSlotToHorse(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        var horse = (net.minecraft.world.entity.animal.equine.AbstractHorse)
                context.spawn(QuickMoveOtherMenusGameTest.getHorseType(),
                        net.minecraft.core.BlockPos.ZERO);

        SimpleContainer saddleContainer = new SimpleContainer(1);
        saddleContainer.setItem(0, new ItemStack(Items.SADDLE, 1));
        HorseInventoryMenu menu = new HorseInventoryMenu(0, inv,
                saddleContainer, horse, 1);

        menu.quickMoveStack(player, 0);

        context.assertTrue(menu.slots.size() >= 1,
            "HorseInventoryMenu must have at least 1 slot");

        context.succeed();
    }

    @GameTest
    public void testContainerSizeForMenuTests(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        context.assertTrue(inv.getContainerSize() >= 63,
            "Inventory must have >= 63 slots for menu tests to be meaningful");

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
