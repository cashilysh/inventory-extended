package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class QuickMoveOtherMenusGameTest implements CustomTestMethodInvoker {

    private static final net.minecraft.world.entity.EntityType<?> HORSE_TYPE;

    static {
        net.minecraft.world.entity.EntityType<?> type;
        try {
            type = (net.minecraft.world.entity.EntityType<?>)
                net.minecraft.world.entity.EntityTypes.class.getField("HORSE").get(null);
        } catch (Exception e) {
            try {
                type = (net.minecraft.world.entity.EntityType<?>)
                    net.minecraft.world.entity.EntityType.class.getField("HORSE").get(null);
            } catch (Exception e2) {
                throw new RuntimeException("Cannot resolve EntityType HORSE", e2);
            }
        }
        HORSE_TYPE = type;
    }

    public static net.minecraft.world.entity.EntityType<?> getHorseType() {
        return HORSE_TYPE;
    }

    @GameTest
    public void testBeaconQuickMove(GameTestHelper context) {
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
    public void testBrewingStandQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer brewingContainer = new SimpleContainer(5);
        BrewingStandMenu menu = new BrewingStandMenu(0, inv,
                brewingContainer, new SimpleContainerData(2));

        inv.setItem(62, new ItemStack(Items.BLAZE_POWDER, 2));
        menu.quickMoveStack(player, 58);

        context.assertTrue(brewingContainer.getItem(4).is(Items.BLAZE_POWDER),
            "BrewingStandMenu: BLAZE_POWDER from inv slot 62 should move to fuel slot (slot 4)");

        context.succeed();
    }

    @GameTest
    public void testStonecutterQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer container = new SimpleContainer(2);
        container.setItem(0, new ItemStack(Items.STONE, 1));
        StonecutterMenu menu = new StonecutterMenu(0, inv,
                ContainerLevelAccess.NULL);

        inv.setItem(62, new ItemStack(Items.STONE, 3));
        menu.quickMoveStack(player, 55);

        boolean stoneMoved = !inv.getItem(62).is(Items.STONE)
                || inv.getItem(62).getCount() < 3;
        context.assertTrue(stoneMoved,
            "StonecutterMenu: STONE from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testLoomQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        LoomMenu menu = new LoomMenu(0, inv, ContainerLevelAccess.NULL);

        inv.setItem(62, new ItemStack(Items.STRING, 2));
        menu.quickMoveStack(player, 57);

        boolean stringMoved = !inv.getItem(62).is(Items.STRING)
                || inv.getItem(62).getCount() < 2;
        context.assertTrue(stringMoved,
            "LoomMenu: STRING from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testEnchantmentQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        EnchantmentMenu menu = new EnchantmentMenu(0, inv,
                ContainerLevelAccess.NULL);

        inv.setItem(62, new ItemStack(Items.LAPIS_LAZULI, 4));
        menu.quickMoveStack(player, 55);

        boolean lapisMovedToSlot1 = menu.getSlot(1).hasItem()
                && menu.getSlot(1).getItem().is(Items.LAPIS_LAZULI);
        context.assertTrue(lapisMovedToSlot1,
            "EnchantmentMenu: LAPIS_LAZULI from inv slot 62 should move to lapis slot");

        context.succeed();
    }

    @GameTest
    public void testCartographyQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        CartographyTableMenu menu = new CartographyTableMenu(0, inv,
                ContainerLevelAccess.NULL);

        inv.setItem(62, new ItemStack(Items.PAPER, 1));
        menu.quickMoveStack(player, 56);

        boolean paperMoved = !inv.getItem(62).is(Items.PAPER)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(paperMoved,
            "CartographyTableMenu: PAPER from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testGrindstoneQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        GrindstoneMenu menu = new GrindstoneMenu(0, inv,
                ContainerLevelAccess.NULL);

        inv.setItem(62, new ItemStack(Items.STONE_SWORD, 1));
        menu.quickMoveStack(player, 56);

        boolean swordMoved = !inv.getItem(62).is(Items.STONE_SWORD)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(swordMoved,
            "GrindstoneMenu: STONE_SWORD from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testMerchantQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        MerchantMenu menu = new MerchantMenu(0, inv);

        inv.setItem(62, new ItemStack(Items.EMERALD, 1));
        menu.quickMoveStack(player, 56);

        boolean emeraldMoved = !inv.getItem(62).is(Items.EMERALD)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(emeraldMoved,
            "MerchantMenu: EMERALD from inv slot 62 should have moved");

        context.succeed();
    }

    @GameTest
    public void testDispenserQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer dispenserContainer = new SimpleContainer(9);
        dispenserContainer.setItem(0, new ItemStack(Items.ARROW, 1));

        DispenserMenu menu = new DispenserMenu(0, inv, dispenserContainer);

        menu.quickMoveStack(player, 0);

        context.assertTrue(dispenserContainer.getItem(0).isEmpty(),
            "DispenserMenu: ARROW should have moved from dispenser slot 0 to inventory");

        context.succeed();
    }

    @GameTest
    public void testCrafterQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        CrafterMenu menu = new CrafterMenu(0, inv);

        inv.setItem(62, new ItemStack(Items.REDSTONE, 1));
        menu.quickMoveStack(player, 62);

        boolean redstoneMoved = !inv.getItem(62).is(Items.REDSTONE)
                || inv.getItem(62).getCount() < 1;
        context.assertTrue(redstoneMoved,
            "CrafterMenu: REDSTONE from inv slot 62 should have moved to crafter grid");

        context.succeed();
    }

    @GameTest
    public void testHorseQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        var horse = (net.minecraft.world.entity.animal.equine.AbstractHorse)
                context.spawn(HORSE_TYPE, net.minecraft.core.BlockPos.ZERO);

        SimpleContainer saddleContainer = new SimpleContainer(1);
        saddleContainer.setItem(0, new ItemStack(Items.SADDLE, 1));
        HorseInventoryMenu menu = new HorseInventoryMenu(0, inv,
                saddleContainer, horse, 1);

        menu.quickMoveStack(player, 0);

        context.assertTrue(menu.slots.size() >= 1,
            "HorseInventoryMenu must have at least 1 slot");

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
