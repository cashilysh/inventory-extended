package inventoryextended.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InventorySlotConstantsTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testIsHotbarSlotBoundaries() {
        assertFalse(InventoryMenu.isHotbarSlot(-1),
            "Negative index should not be hotbar");
        assertFalse(InventoryMenu.isHotbarSlot(0),
            "Index 0 should not be hotbar");
        assertFalse(InventoryMenu.isHotbarSlot(9),
            "Index 9 should not be hotbar");
        assertFalse(InventoryMenu.isHotbarSlot(62),
            "Index 62 should not be hotbar (last extended inventory slot, was 35)");

        assertTrue(InventoryMenu.isHotbarSlot(63),
            "Index 63 should be hotbar/fast-access slot 0 (was 36)");
        assertTrue(InventoryMenu.isHotbarSlot(67),
            "Index 67 should be hotbar slot 4");
        assertTrue(InventoryMenu.isHotbarSlot(71),
            "Index 71 should be hotbar slot 8 (was 44)");

        assertTrue(InventoryMenu.isHotbarSlot(72),
            "Index 72 should be hotbar (offhand/shield slot, was 45, always considered fast-access)");

        assertFalse(InventoryMenu.isHotbarSlot(73),
            "Index 73 should not be hotbar (beyond menu)");
    }

    @Test
    void testEquipmentSlotMappingKeys() {
        var map = Inventory.EQUIPMENT_SLOT_MAPPING;
        assertNotNull(map, "EQUIPMENT_SLOT_MAPPING must exist");

        assertTrue(map.containsKey(63),
            "EQUIPMENT_SLOT_MAPPING must contain key 63 (armor FEET, was 36)");
        assertTrue(map.containsKey(67),
            "EQUIPMENT_SLOT_MAPPING must contain key 67 (OFFHAND, was 40)");
        assertTrue(map.containsKey(68),
            "EQUIPMENT_SLOT_MAPPING must contain key 68 (BODY, was 41)");
        assertTrue(map.containsKey(69),
            "EQUIPMENT_SLOT_MAPPING must contain key 69 (SADDLE, was 42)");
    }

    @Test
    void testEquipmentSlotMappingDoesNotHaveOldKeys() {
        var map = Inventory.EQUIPMENT_SLOT_MAPPING;

        assertFalse(map.containsKey(36),
            "EQUIPMENT_SLOT_MAPPING must NOT contain old armor key 36");
        assertFalse(map.containsKey(40),
            "EQUIPMENT_SLOT_MAPPING must NOT contain old offhand key 40");
    }

    @Test
    void testRegistriesBootstrappedSuccessfully() {
        assertNotNull(net.minecraft.world.item.Items.DIAMOND,
            "Registry bootstrap failed — Items not available");
        assertNotNull(net.minecraft.world.level.block.Blocks.STONE,
            "Registry bootstrap failed — Blocks not available");
    }
}
