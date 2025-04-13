package inventoryextended.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;


import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;




@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class IncreaseGlobalBackgroundHeight {
	
	@ModifyConstant(method = "*", constant = @Constant(intValue = 166), require = 1)
	private static int backgroundHeightExtender(int backgroundHeight) {
    return backgroundHeight + 60;	//for all screens
	}
	
	@ModifyConstant(method = "*", constant = @Constant(intValue = 94), require = 1)
	private static int playerInventoryTitleYExtender(int playerInventoryTitleY) {
    return playerInventoryTitleY + 60;	//for all screens
	}
	

}