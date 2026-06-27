package inventoryextended.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConstantBoundaryGameTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testIsHotbarSlotFullRange() {
        for (int i = 0; i < 9; i++) {
            assertFalse(InventoryMenu.isHotbarSlot(i),
                "Index " + i + " (hotbar inventory slot) should NOT be hotbar");
        }

        for (int i = 9; i < 63; i++) {
            assertFalse(InventoryMenu.isHotbarSlot(i),
                "Index " + i + " (extended inventory) should NOT be hotbar");
        }

        for (int i = 63; i < 72; i++) {
            assertTrue(InventoryMenu.isHotbarSlot(i),
                "Index " + i + " (hotbar menu slot) should be hotbar");
        }

        assertTrue(InventoryMenu.isHotbarSlot(72),
            "Index 72 (offhand menu slot) should be hotbar");

        for (int i = 73; i < 80; i++) {
            assertFalse(InventoryMenu.isHotbarSlot(i),
                "Index " + i + " (out of bounds) should NOT be hotbar");
        }
    }

    @Test
    void testEquipmentSlotMappingFullRange() {
        var map = Inventory.EQUIPMENT_SLOT_MAPPING;

        for (int i = 0; i < 63; i++) {
            assertFalse(map.containsKey(i),
                "EQUIPMENT_SLOT_MAPPING should not contain key " + i);
        }

        assertTrue(map.containsKey(63), "Must have key 63 (FEET)");
        assertTrue(map.containsKey(64), "Must have key 64 (LEGS)");
        assertTrue(map.containsKey(65), "Must have key 65 (CHEST)");
        assertTrue(map.containsKey(66), "Must have key 66 (HEAD)");
        assertTrue(map.containsKey(67), "Must have key 67 (OFFHAND)");
        assertTrue(map.containsKey(68), "Must have key 68 (BODY)");
        assertTrue(map.containsKey(69), "Must have key 69 (SADDLE)");

        for (int i = 70; i < 80; i++) {
            assertFalse(map.containsKey(i),
                "EQUIPMENT_SLOT_MAPPING should not contain key " + i);
        }
    }

    @Test
    void testInventoryConstructorExists() {
        java.lang.reflect.Constructor<?>[] ctors = Inventory.class.getDeclaredConstructors();
        assertTrue(ctors.length >= 1,
            "Inventory must have at least one declared constructor");

        java.lang.reflect.Constructor<?> ctor = ctors[0];
        ctor.setAccessible(true);
        Class<?>[] paramTypes = ctor.getParameterTypes();
        assertTrue(paramTypes.length >= 1,
            "Inventory constructor must take at least 1 parameter");
    }
}
