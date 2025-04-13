package inventoryextended.mixin;


import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;



@SuppressWarnings({"overwrite", "MissingJavadoc"})
@Mixin(AbstractFurnaceScreen.class)
//@Environment(EnvType.CLIENT)
public abstract class FurnaceScreenRecipeBookButton {
	
	
	//change Y-level of Recipe Book in the Crafting Screen menu
	@ModifyConstant(method = "getRecipeBookButtonPos", constant = @Constant(intValue = 49), require = 1)
	private static int recipeBookButtonYmod(int recipeBookButtonY) {
    return recipeBookButtonY + 30;
	}
	

}