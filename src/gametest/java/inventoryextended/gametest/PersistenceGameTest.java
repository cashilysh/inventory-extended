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

public class PersistenceGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testInventorySaveLoadRoundtrip(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < 63; i++) {
            inv.setItem(i, new ItemStack(Items.DIAMOND, i + 1));
        }

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

        context.assertTrue(saved.size() >= 63,
            "save() should produce at least 63 ItemStackWithSlot entries, got " + saved.size());

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

        for (int i = 0; i < 63; i++) {
            ItemStack loaded = inv.getItem(i);
            context.assertTrue(!loaded.isEmpty(),
                "Slot " + i + " should be non-empty after load()");
            context.assertTrue(loaded.is(Items.DIAMOND),
                "Slot " + i + " should contain DIAMOND after load(), got "
                        + loaded.getItem());
            context.assertTrue(loaded.getCount() == i + 1,
                "Slot " + i + " count should be " + (i + 1)
                        + " after load(), got " + loaded.getCount());
        }

        context.succeed();
    }

    @GameTest
    public void testExtendedSlotsSurviveSaveLoad(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, ItemStack.EMPTY);
        }

        inv.setItem(36, new ItemStack(Items.GOLDEN_APPLE, 1));
        inv.setItem(62, new ItemStack(Items.ENDER_PEARL, 16));

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

        context.assertTrue(saved.size() >= 2,
            "save() should produce at least 2 entries (slots 36 and 62), got " + saved.size());

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

        context.assertTrue(inv.getItem(36).is(Items.GOLDEN_APPLE),
            "Slot 36 should have GOLDEN_APPLE after save/load, was an extended slot (vanilla would have lost it)");
        context.assertTrue(inv.getItem(62).is(Items.ENDER_PEARL),
            "Slot 62 should have ENDER_PEARL after save/load");
        context.assertTrue(inv.getItem(62).getCount() == 16,
            "Slot 62 ender pearl count should be 16 after save/load");

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
