package inventoryextended.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;

//@Environment(EnvType.CLIENT)

@Mixin(InventoryScreen.class)
public abstract class PlayerInventoryRecipeButton extends AbstractContainerScreen<InventoryMenu> {
	

    public PlayerInventoryRecipeButton(InventoryMenu screenHandler, Inventory inventory, Component title) {
        super(screenHandler, inventory, title);
    }


	//Change recipe button Y-Position
	
	@ModifyConstant(method = "getRecipeBookButtonPosition", constant = @Constant(intValue = 22), require = 1)
	private static int recipeBookButtonYmod(int recipeBookButtonY) {
    return recipeBookButtonY + 30;
	}

	
}