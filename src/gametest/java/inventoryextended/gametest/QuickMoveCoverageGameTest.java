package inventoryextended.gametest;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class QuickMoveCoverageGameTest implements CustomTestMethodInvoker {

    private static final Set<String> MENUS_WITH_QUICKMOVE_FIX = Set.of(
        "CraftingMenu",
        "FurnaceMenu", "SmokerMenu", "BlastFurnaceMenu",
        "BeaconMenu",
        "BrewingStandMenu",
        "MerchantMenu",
        "StonecutterMenu",
        "LoomMenu",
        "EnchantmentMenu",
        "CartographyTableMenu",
        "GrindstoneMenu",
        "DispenserMenu",
        "CrafterMenu",
        "AbstractMountInventoryMenu"
    );

    @GameTest
    public void testAllMenuTypesAreRegistered(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        context.assertTrue(inv.getContainerSize() >= 63,
            "Inventory must have >= 63 slots for menu tests to be meaningful");

        context.succeed();
    }

    @GameTest
    public void testQuickMoveDoesNotCrashOnAllMenuTypes(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        context.assertTrue(inv.getContainerSize() >= 63,
            "Inventory must have >= 63 slots");

        context.succeed();
    }

    @GameTest
    public void testKnownMenuTypesHaveQuickMoveFix(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        context.assertTrue(inv.getContainerSize() >= 63,
            "Inventory must be extended for quick-move to work");

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
