package inventoryextended;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryExtended implements ModInitializer {
    public static final String MOD_ID = "inventoryextended";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final boolean debug = false;

    @Override
    public void onInitialize() {
        LOGGER.info("InventoryExtended mod initialized!");
    }
}