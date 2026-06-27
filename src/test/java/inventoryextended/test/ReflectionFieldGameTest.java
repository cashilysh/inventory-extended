package inventoryextended.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

public class ReflectionFieldGameTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testAbstractContainerScreenFieldsExist() {
        try {
            Field imageHeightField = net.minecraft.client.gui.screens.inventory
                    .AbstractContainerScreen.class.getDeclaredField("imageHeight");
            assertNotNull(imageHeightField,
                "imageHeight field must exist in AbstractContainerScreen");
        } catch (NoSuchFieldException e) {
            fail("AbstractContainerScreen.imageHeight field not found: " + e.getMessage());
        }

        try {
            Field topPosField = net.minecraft.client.gui.screens.inventory
                    .AbstractContainerScreen.class.getDeclaredField("topPos");
            assertNotNull(topPosField,
                "topPos field must exist in AbstractContainerScreen");
        } catch (NoSuchFieldException e) {
            fail("AbstractContainerScreen.topPos field not found: " + e.getMessage());
        }
    }

    @Test
    void testSlotAccessorFieldsExist() {
        try {
            Field xField = Slot.class.getDeclaredField("x");
            assertNotNull(xField, "Slot.x field must exist");
        } catch (NoSuchFieldException e) {
            fail("Slot.x field not found: " + e.getMessage());
        }

        try {
            Field yField = Slot.class.getDeclaredField("y");
            assertNotNull(yField, "Slot.y field must exist");
        } catch (NoSuchFieldException e) {
            fail("Slot.y field not found: " + e.getMessage());
        }
    }

    @Test
    void testRegistryBootstrapReflection() {
        try {
            Class<?> itemsClass = net.minecraft.world.item.Items.class;
            Field diamondField = itemsClass.getField("DIAMOND");
            assertNotNull(diamondField.get(null),
                "Items.DIAMOND should be accessible via reflection");

            Class<?> blocksClass = net.minecraft.world.level.block.Blocks.class;
            Field stoneField = blocksClass.getField("STONE");
            assertNotNull(stoneField.get(null),
                "Blocks.STONE should be accessible via reflection");
        } catch (Exception e) {
            fail("Registry bootstrap reflection check failed: " + e.getMessage());
        }
    }

    @Test
    void testInventoryMenuIsHotbarSlotMethodAccessible() {
        try {
            java.lang.reflect.Method method = InventoryMenu.class
                    .getMethod("isHotbarSlot", int.class);
            assertNotNull(method, "isHotbarSlot method must exist");
        } catch (NoSuchMethodException e) {
            fail("InventoryMenu.isHotbarSlot(int) method not found: " + e.getMessage());
        }
    }
}
