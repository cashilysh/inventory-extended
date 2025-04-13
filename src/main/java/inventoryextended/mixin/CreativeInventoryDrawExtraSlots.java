package inventoryextended.mixin;


import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryDrawExtraSlots {
	

    @ModifyConstant(
        method = "setSelectedTab",
        constant = @Constant(intValue = 45)
    )
    private int modify45(int original) {
        return 45+27; // Change from 3 rows to 6 rows
    }
	
    @ModifyConstant(
        method = "setSelectedTab",
        constant = @Constant(intValue = 36)
    )
    private int modify36(int original) {
        return 36+27; // Change from 3 rows to 6 rows
    }
	
	//Creative inventory hot bar Y-Position
	    @ModifyConstant(
        method = "setSelectedTab",
        constant = @Constant(intValue = 112)
    )
    private int modify112(int original) {
        return 112+60; // Change from 3 rows to 6 rows
    }

	
}