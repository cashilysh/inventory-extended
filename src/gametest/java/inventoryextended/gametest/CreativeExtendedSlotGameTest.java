package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class CreativeExtendedSlotGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testInventoryItemSlotsAccessible0to62(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.CREATIVE);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < 63; i++) {
            inv.setItem(i, new ItemStack(Items.DIAMOND, 1));
        }

        int nonEmpty = 0;
        for (int i = 0; i < 63; i++) {
            if (inv.getItem(i).is(Items.DIAMOND)) nonEmpty++;
        }

        context.assertTrue(nonEmpty == 63,
            "All 63 main inventory slots should be writable/readable in creative, got "
                    + nonEmpty);

        context.succeed();
    }

    @GameTest
    public void testInventoryMenuHas73Slots(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.CREATIVE);
        InventoryMenu menu = player.inventoryMenu;

        context.assertTrue(menu.slots.size() >= 73,
            "InventoryMenu should have >= 73 slots (RemapPlayerSlots), got "
                    + menu.slots.size());

        context.succeed();
    }

    @GameTest
    public void testCreativeSlotsAreHotbarCorrectly(GameTestHelper context) {
        for (int i = 63; i < 72; i++) {
            context.assertTrue(InventoryMenu.isHotbarSlot(i),
                "isHotbarSlot(" + i + ") should be true (hotbar menu slot)");
        }
        context.assertTrue(InventoryMenu.isHotbarSlot(72),
            "isHotbarSlot(72) should be true (offhand)");

        for (int i = 0; i < 63; i++) {
            context.assertFalse(InventoryMenu.isHotbarSlot(i),
                "isHotbarSlot(" + i + ") should be false (inventory)");
        }

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
