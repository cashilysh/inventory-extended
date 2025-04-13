package inventoryextended.mixin;


import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

//@Environment(EnvType.CLIENT)

@Mixin(InventoryScreen.class)
public abstract class PlayerInventoryRecipeButton extends HandledScreen<PlayerScreenHandler> {
	

    public PlayerInventoryRecipeButton(PlayerScreenHandler screenHandler, PlayerInventory inventory, Text title) {
        super(screenHandler, inventory, title);
    }


	//Change recipe button Y-Position
	
	@ModifyConstant(method = "getRecipeBookButtonPos", constant = @Constant(intValue = 22), require = 1)
	private static int recipeBookButtonYmod(int recipeBookButtonY) {
    return recipeBookButtonY + 30;
	}

	
}