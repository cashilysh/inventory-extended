package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class MixinApplicationGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testInventoryContainerSizeIs63(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        context.assertTrue(inv.getContainerSize() >= 63,
            "ExtendPlayerInventory mixin should set container size >= 63, got "
                    + inv.getContainerSize());

        context.succeed();
    }

    @GameTest
    public void testInventoryMenuSlotCountIs73(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        InventoryMenu menu = player.inventoryMenu;

        context.assertTrue(menu.slots.size() >= 73,
            "RemapPlayerSlots mixin should give >= 73 menu slots, got "
                    + menu.slots.size());

        context.succeed();
    }

    @GameTest
    public void testIsHotbarSlotMixinApplied(GameTestHelper context) {
        context.assertTrue(InventoryMenu.isHotbarSlot(63),
            "RemapPlayerSlots mixin: isHotbarSlot(63) should be true");
        context.assertFalse(InventoryMenu.isHotbarSlot(62),
            "RemapPlayerSlots mixin: isHotbarSlot(62) should be false");
        context.assertTrue(InventoryMenu.isHotbarSlot(72),
            "RemapPlayerSlots mixin: isHotbarSlot(72) should be true");

        context.succeed();
    }

    @GameTest
    public void testEquipmentSlotMappingMixinApplied(GameTestHelper context) {
        var map = Inventory.EQUIPMENT_SLOT_MAPPING;

        context.assertTrue(map.containsKey(63),
            "ExtendPlayerInventory mixin: EQUIPMENT_SLOT_MAPPING must have key 63");
        context.assertTrue(map.containsKey(67),
            "ExtendPlayerInventory mixin: EQUIPMENT_SLOT_MAPPING must have key 67");
        context.assertFalse(map.containsKey(36),
            "ExtendPlayerInventory mixin: old key 36 must not exist");
        context.assertFalse(map.containsKey(40),
            "ExtendPlayerInventory mixin: old key 40 must not exist");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveMixinsApplied(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        SimpleContainer furnaceContainer = new SimpleContainer(3);
        FurnaceMenu furnaceMenu = new FurnaceMenu(0, inv, furnaceContainer,
                new SimpleContainerData(4));

        inv.setItem(9, new ItemStack(Items.COAL, 1));
        furnaceMenu.quickMoveStack(player, 3);

        context.assertTrue(furnaceContainer.getItem(1).is(Items.COAL),
            "FixFurnaceMenuQuickMove: COAL should quick-move to fuel slot");

        context.succeed();
    }

    @GameTest
    public void testAllQuickMoveMenuTypesWork(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(62, new ItemStack(Items.NETHER_STAR, 1));
        CraftingMenu craftingMenu = new CraftingMenu(0, inv);
        int cSourceSlot = -1;
        for (Slot slot : craftingMenu.slots) {
            if (slot.hasItem() && slot.getItem().is(Items.NETHER_STAR)) {
                cSourceSlot = slot.index;
                break;
            }
        }
        context.assertTrue(cSourceSlot >= 0,
            "CraftingMenu: must find menu slot for inv 62");
        craftingMenu.quickMoveStack(player, cSourceSlot);
        boolean netherStarMoved = !(craftingMenu.getSlot(cSourceSlot).hasItem()
                && craftingMenu.getSlot(cSourceSlot).getItem().is(Items.NETHER_STAR));
        context.assertTrue(netherStarMoved,
            "CraftingMenu: NETHER_STAR from inv 62 should move");

        SimpleContainer beaconCont = new SimpleContainer(1);
        BeaconMenu beaconMenu = new BeaconMenu(0, beaconCont,
                new SimpleContainerData(3), ContainerLevelAccess.NULL);
        beaconMenu.quickMoveStack(player, 0);

        inv.setItem(62, new ItemStack(Items.BLAZE_POWDER, 1));
        SimpleContainer brewCont = new SimpleContainer(5);
        BrewingStandMenu brewingMenu = new BrewingStandMenu(0, inv, brewCont,
                new SimpleContainerData(2));
        brewingMenu.quickMoveStack(player, 58);
        context.assertTrue(brewCont.getItem(4).is(Items.BLAZE_POWDER),
            "BrewingStandMenu: BLAZE_POWDER from inv 62 should move to fuel");

        inv.setItem(62, new ItemStack(Items.STONE, 1));
        StonecutterMenu stonecutterMenu = new StonecutterMenu(0, inv,
                ContainerLevelAccess.NULL);
        stonecutterMenu.quickMoveStack(player, 55);

        inv.setItem(62, new ItemStack(Items.STRING, 1));
        LoomMenu loomMenu = new LoomMenu(0, inv, ContainerLevelAccess.NULL);
        loomMenu.quickMoveStack(player, 57);

        inv.setItem(62, new ItemStack(Items.LAPIS_LAZULI, 1));
        EnchantmentMenu enchantMenu = new EnchantmentMenu(0, inv,
                ContainerLevelAccess.NULL);
        enchantMenu.quickMoveStack(player, 55);
        context.assertTrue(enchantMenu.getSlot(1).hasItem(),
            "EnchantmentMenu: LAPIS from inv 62 should move to lapis slot");

        inv.setItem(62, new ItemStack(Items.PAPER, 1));
        CartographyTableMenu cartoMenu = new CartographyTableMenu(0, inv,
                ContainerLevelAccess.NULL);
        cartoMenu.quickMoveStack(player, 56);

        inv.setItem(62, new ItemStack(Items.STONE_SWORD, 1));
        GrindstoneMenu grindMenu = new GrindstoneMenu(0, inv,
                ContainerLevelAccess.NULL);
        grindMenu.quickMoveStack(player, 56);

        inv.setItem(62, new ItemStack(Items.EMERALD, 1));
        MerchantMenu merchantMenu = new MerchantMenu(0, inv);
        merchantMenu.quickMoveStack(player, 56);

        SimpleContainer dispContainer = new SimpleContainer(9);
        dispContainer.setItem(0, new ItemStack(Items.ARROW, 1));
        DispenserMenu dispMenu = new DispenserMenu(0, inv, dispContainer);
        dispMenu.quickMoveStack(player, 0);
        context.assertTrue(dispContainer.getItem(0).isEmpty(),
            "DispenserMenu: ARROW should move from dispenser to inventory");

        inv.setItem(62, new ItemStack(Items.REDSTONE, 1));
        CrafterMenu crafterMenu = new CrafterMenu(0, inv);
        crafterMenu.quickMoveStack(player, 62);

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
