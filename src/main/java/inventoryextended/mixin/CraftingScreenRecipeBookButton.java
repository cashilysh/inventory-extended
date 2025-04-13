package inventoryextended.mixin;


import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;



@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(CraftingScreen.class)
public abstract class CraftingScreenRecipeBookButton {
	
	//change Y-level of Recipe Book in the Crafting Block Screen menu
	@ModifyConstant(method = "getRecipeBookButtonPos", constant = @Constant(intValue = 49), require = 1)
	private static int recipeBookButtonYmod(int recipeBookButtonY) {
    return recipeBookButtonY + 30;
	}
	

}