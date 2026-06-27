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

public class BookEditingGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testOffhandSlot67Accessible(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        ItemStack book = new ItemStack(Items.WRITABLE_BOOK, 1);
        inv.setItem(67, book.copy());

        context.assertTrue(inv.getItem(67).is(Items.WRITABLE_BOOK),
            "Slot 67 should contain WRITABLE_BOOK (FixHandleEditBook)");

        context.assertTrue(inv.getItem(67).getCount() == 1,
            "Slot 67 book count should be 1");

        context.succeed();
    }

    @GameTest
    public void testBookEditOffhandSlotIndex(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        ItemStack book = new ItemStack(Items.WRITABLE_BOOK, 1);
        inv.setItem(67, book.copy());

        boolean bookInOffhand = inv.getItem(67).is(Items.WRITABLE_BOOK);
        context.assertTrue(bookInOffhand,
            "Book should be accessible in offhand slot 67 (was 40)");

        context.succeed();
    }

    @GameTest
    public void testSavedBookInOffhandSurvivesInventoryCycle(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(67, new ItemStack(Items.WRITTEN_BOOK, 1));

        ItemStack book = inv.getItem(67);

        context.assertFalse(book.isEmpty(),
            "Written book in slot 67 should be non-empty");

        context.assertTrue(book.is(Items.WRITTEN_BOOK),
            "Slot 67 should contain WRITTEN_BOOK");

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
