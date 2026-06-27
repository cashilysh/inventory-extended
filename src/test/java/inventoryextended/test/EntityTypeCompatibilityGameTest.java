package inventoryextended.test;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

public class EntityTypeCompatibilityGameTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void testHorseEntityTypeResolvable() {
        Object type = null;

        try {
            Class<?> entityTypesClass = net.minecraft.world.entity.EntityTypes.class;
            Field horseField = entityTypesClass.getField("HORSE");
            type = horseField.get(null);
        } catch (Exception e) {
            try {
                Class<?> entityTypeClass = net.minecraft.world.entity.EntityType.class;
                Field horseField = entityTypeClass.getField("HORSE");
                type = horseField.get(null);
            } catch (Exception e2) {
                fail("Cannot resolve EntityType HORSE: " + e2.getMessage());
            }
        }

        assertNotNull(type,
            "EntityType HORSE must be resolvable via EntityTypes or EntityType");
    }

    @Test
    void testAllHorseVariantTypesExist() {
        String[] horseTypes = {"HORSE", "DONKEY", "MULE",
                "SKELETON_HORSE", "ZOMBIE_HORSE"};

        for (String typeName : horseTypes) {
            Object type = null;
            try {
                Class<?> entityTypesClass = net.minecraft.world.entity.EntityTypes.class;
                Field field = entityTypesClass.getField(typeName);
                type = field.get(null);
            } catch (Exception e) {
                try {
                    Class<?> entityTypeClass = net.minecraft.world.entity.EntityType.class;
                    Field field = entityTypeClass.getField(typeName);
                    type = field.get(null);
                } catch (Exception e2) {
                    // Entity type may not exist in this MC version
                }
            }

            assertNotNull(type,
                "Entity type " + typeName
                    + " must be resolvable via EntityTypes or EntityType");
        }
    }

    @Test
    void testVersionApiConstants() {
        assertNotNull(SharedConstants.getCurrentVersion(),
            "SharedConstants.getCurrentVersion() must not be null");
        var version = SharedConstants.getCurrentVersion();
        assertNotNull(version, "Version must not be null");
    }

    @Test
    void testAbstractHorseClassExists() {
        try {
            Class<?> abstractHorseClass = net.minecraft.world.entity.animal.equine
                    .AbstractHorse.class;
            assertNotNull(abstractHorseClass,
                "AbstractHorse class must exist");
        } catch (Exception e) {
            fail("AbstractHorse class not found: " + e.getMessage());
        }

        try {
            Class<?> horseInventoryMenuClass = net.minecraft.world.inventory
                    .HorseInventoryMenu.class;
            assertNotNull(horseInventoryMenuClass,
                "HorseInventoryMenu class must exist");
        } catch (Exception e) {
            fail("HorseInventoryMenu class not found: " + e.getMessage());
        }
    }
}
