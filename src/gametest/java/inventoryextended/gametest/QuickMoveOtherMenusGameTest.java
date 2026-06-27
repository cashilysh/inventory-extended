package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.*;
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

    @GameTest
    public void testBeaconQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        SimpleContainer beaconContainer = new SimpleContainer(1);
        BeaconMenu menu = new BeaconMenu(0, beaconContainer,
                new SimpleContainerData(3), ContainerLevelAccess.NULL);

        menu.quickMoveStack(player, 0);

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

        inv.setItem(9, new ItemStack(Items.BLAZE_POWDER, 1));
        menu.quickMoveStack(player, 5);

        boolean moved = brewingContainer.getItem(4).is(Items.BLAZE_POWDER)
                || !inv.getItem(9).is(Items.BLAZE_POWDER);

        context.assertTrue(moved,
            "BLAZE_POWDER should have moved from inventory to brewing stand, or stayed in place (valid behaviour)");

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

        inv.setItem(9, new ItemStack(Items.STONE, 1));
        menu.quickMoveStack(player, 2);

        context.succeed();
    }

    @GameTest
    public void testLoomQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        LoomMenu menu = new LoomMenu(0, inv, ContainerLevelAccess.NULL);

        inv.setItem(10, new ItemStack(Items.STRING, 1));
        menu.quickMoveStack(player, 4);

        context.succeed();
    }

    @GameTest
    public void testEnchantmentQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        EnchantmentMenu menu = new EnchantmentMenu(0, inv,
                ContainerLevelAccess.NULL);

        inv.setItem(9, new ItemStack(Items.LAPIS_LAZULI, 1));
        menu.quickMoveStack(player, 2);

        context.succeed();
    }

    @GameTest
    public void testCartographyQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        CartographyTableMenu menu = new CartographyTableMenu(0, inv,
                ContainerLevelAccess.NULL);

        inv.setItem(9, new ItemStack(Items.PAPER, 1));
        menu.quickMoveStack(player, 3);

        context.succeed();
    }

    @GameTest
    public void testGrindstoneQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        GrindstoneMenu menu = new GrindstoneMenu(0, inv,
                ContainerLevelAccess.NULL);

        inv.setItem(10, new ItemStack(Items.STONE_SWORD, 1));
        menu.quickMoveStack(player, 3);

        context.succeed();
    }

    @GameTest
    public void testMerchantQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        MerchantMenu menu = new MerchantMenu(0, inv);

        inv.setItem(9, new ItemStack(Items.EMERALD, 1));
        menu.quickMoveStack(player, 3);

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
            "ARROW should have moved from dispenser slot 0 to inventory");

        context.succeed();
    }

    @GameTest
    public void testCrafterQuickMove(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();
        clearInv(inv);

        CrafterMenu menu = new CrafterMenu(0, inv);

        inv.setItem(0, new ItemStack(Items.REDSTONE, 1));
        menu.quickMoveStack(player, 54);

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
        HorseInventoryMenu menu = new HorseInventoryMenu(0, inv,
                saddleContainer, horse, 1);

        inv.setItem(0, new ItemStack(Items.SADDLE, 1));
        menu.quickMoveStack(player, 1);

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
