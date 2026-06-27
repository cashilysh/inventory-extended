package inventoryextended.gametest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class PersistenceEdgeCaseGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testExtendedSlotsEmptySaveLoad(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(0, new ItemStack(Items.STONE, 1));
        inv.setItem(30, new ItemStack(Items.APPLE, 2));
        inv.setItem(62, new ItemStack(Items.DIAMOND, 3));

        List<ItemStackWithSlot> saved = new ArrayList<>();
        ValueOutput.TypedOutputList<ItemStackWithSlot> outList =
                new ValueOutput.TypedOutputList<>() {
            @Override public void add(ItemStackWithSlot item) {
                saved.add(new ItemStackWithSlot(item.slot(),
                        item.stack().copy()));
            }
            @Override public boolean isEmpty() { return saved.isEmpty(); }
        };

        inv.save(outList);

        context.assertTrue(saved.size() >= 3,
            "save() should produce at least 3 entries (slots 0, 30, 62), got " + saved.size());

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        ValueInput.TypedInputList<ItemStackWithSlot> inList =
                new ValueInput.TypedInputList<>() {
            @Override public boolean isEmpty() { return saved.isEmpty(); }
            @Override public Stream<ItemStackWithSlot> stream() {
                return saved.stream();
            }
            @Override public Iterator<ItemStackWithSlot> iterator() {
                return saved.iterator();
            }
        };

        inv.load(inList);

        context.assertTrue(inv.getItem(0).is(Items.STONE),
            "Slot 0 should have STONE after save/load");
        context.assertTrue(inv.getItem(30).is(Items.APPLE),
            "Slot 30 should have APPLE after save/load");
        context.assertTrue(inv.getItem(62).is(Items.DIAMOND),
            "Slot 62 should have DIAMOND after save/load");

        context.assertTrue(inv.getItem(9).isEmpty(),
            "Slot 9 should be empty after save/load (was empty)");

        context.succeed();
    }

    @GameTest
    public void testStackOf64InExtendedSlotSaveLoad(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(9, new ItemStack(Items.DIAMOND, 64));

        List<ItemStackWithSlot> saved = new ArrayList<>();
        ValueOutput.TypedOutputList<ItemStackWithSlot> outList =
                new ValueOutput.TypedOutputList<>() {
            @Override public void add(ItemStackWithSlot item) {
                saved.add(new ItemStackWithSlot(item.slot(),
                        item.stack().copy()));
            }
            @Override public boolean isEmpty() { return saved.isEmpty(); }
        };

        inv.save(outList);

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        ValueInput.TypedInputList<ItemStackWithSlot> inList =
                new ValueInput.TypedInputList<>() {
            @Override public boolean isEmpty() { return saved.isEmpty(); }
            @Override public Stream<ItemStackWithSlot> stream() {
                return saved.stream();
            }
            @Override public Iterator<ItemStackWithSlot> iterator() {
                return saved.iterator();
            }
        };

        inv.load(inList);

        context.assertTrue(inv.getItem(9).is(Items.DIAMOND),
            "Slot 9 should have DIAMOND after save/load");
        context.assertTrue(inv.getItem(9).getCount() == 64,
            "Slot 9 diamond count should be 64, got "
                    + inv.getItem(9).getCount());

        context.succeed();
    }

    @GameTest
    public void testArmorOffhandItemsReadable(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(63, new ItemStack(Items.LEATHER_BOOTS, 1));
        inv.setItem(64, new ItemStack(Items.LEATHER_LEGGINGS, 1));
        inv.setItem(65, new ItemStack(Items.LEATHER_CHESTPLATE, 1));
        inv.setItem(66, new ItemStack(Items.LEATHER_HELMET, 1));
        inv.setItem(67, new ItemStack(Items.SHIELD, 1));

        context.assertTrue(inv.getItem(63).is(Items.LEATHER_BOOTS),
            "Slot 63 should have LEATHER_BOOTS");
        context.assertTrue(inv.getItem(64).is(Items.LEATHER_LEGGINGS),
            "Slot 64 should have LEATHER_LEGGINGS");
        context.assertTrue(inv.getItem(65).is(Items.LEATHER_CHESTPLATE),
            "Slot 65 should have LEATHER_CHESTPLATE");
        context.assertTrue(inv.getItem(66).is(Items.LEATHER_HELMET),
            "Slot 66 should have LEATHER_HELMET");
        context.assertTrue(inv.getItem(67).is(Items.SHIELD),
            "Slot 67 should have SHIELD");

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
