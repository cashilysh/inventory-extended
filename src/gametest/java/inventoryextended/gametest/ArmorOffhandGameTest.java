package inventoryextended.gametest;

import java.lang.reflect.Method;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.GameType;

import net.minecraft.gametest.framework.GameTestHelper;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class ArmorOffhandGameTest implements CustomTestMethodInvoker {

    @GameTest
    public void testArmorSlotIndices(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        context.assertTrue(inv.getItem(63) != null,
            "Armor slot 63 must exist (was 36)");
        context.assertTrue(inv.getItem(64) != null,
            "Armor slot 64 must exist (was 37)");
        context.assertTrue(inv.getItem(65) != null,
            "Armor slot 65 must exist (was 38)");
        context.assertTrue(inv.getItem(66) != null,
            "Armor slot 66 must exist (was 39)");

        context.succeed();
    }

    @GameTest
    public void testOffhandSlotIndex(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        ItemStack shield = new ItemStack(Items.SHIELD);
        inv.setItem(67, shield);

        context.assertTrue(inv.getItem(67).is(Items.SHIELD),
            "Offhand slot 67 should contain SHIELD");

        context.succeed();
    }

    @GameTest
    public void testArmorEquippingViaSetItem(GameTestHelper context) {
        var player = context.makeMockPlayer(GameType.SURVIVAL);
        Inventory inv = player.getInventory();

        inv.setItem(63, new ItemStack(Items.LEATHER_BOOTS));
        inv.setItem(64, new ItemStack(Items.LEATHER_LEGGINGS));
        inv.setItem(65, new ItemStack(Items.LEATHER_CHESTPLATE));
        inv.setItem(66, new ItemStack(Items.LEATHER_HELMET));

        context.assertTrue(inv.getItem(63).is(Items.LEATHER_BOOTS),
            "Slot 63 should be LEATHER_BOOTS");
        context.assertTrue(inv.getItem(64).is(Items.LEATHER_LEGGINGS),
            "Slot 64 should be LEATHER_LEGGINGS");
        context.assertTrue(inv.getItem(65).is(Items.LEATHER_CHESTPLATE),
            "Slot 65 should be LEATHER_CHESTPLATE");
        context.assertTrue(inv.getItem(66).is(Items.LEATHER_HELMET),
            "Slot 66 should be LEATHER_HELMET");

        context.succeed();
    }

    @GameTest
    public void testEquipmentSlotMappingLookup(GameTestHelper context) {
        context.assertTrue(
            Inventory.EQUIPMENT_SLOT_MAPPING.containsKey(67),
            "EQUIPMENT_SLOT_MAPPING must map offhand key 67");
        context.assertTrue(
            Inventory.EQUIPMENT_SLOT_MAPPING.containsKey(63),
            "EQUIPMENT_SLOT_MAPPING must map armor key 63");

        var offhand = Inventory.EQUIPMENT_SLOT_MAPPING.get(67);
        context.assertTrue(offhand == EquipmentSlot.OFFHAND,
            "Key 67 must map to OFFHAND, got " + offhand);

        var feet = Inventory.EQUIPMENT_SLOT_MAPPING.get(63);
        context.assertTrue(feet == EquipmentSlot.FEET,
            "Key 63 must map to FEET, got " + feet);

        context.succeed();
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method)
            throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}
