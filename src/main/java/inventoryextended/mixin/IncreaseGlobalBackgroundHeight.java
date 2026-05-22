package inventoryextended.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;




@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class IncreaseGlobalBackgroundHeight {


	@Shadow
	protected int inventoryLabelY;
	
	@ModifyConstant(method = "*", constant = @Constant(intValue = 166), require = 1)
	private static int backgroundHeightExtender(int backgroundHeight) {
		return backgroundHeight + 60;    //for all screens - shifts texture/GUI
	}

	@ModifyArg(
			method = "extractLabels",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)V",
					ordinal = 1  // 0 = title, 1 = player inventory title
			),
			index = 3  // 0=font, 1=text, 2=x, 3=y, 4=color, 5=shadow
	)
	private int adjustInventoryLabelY(int originalY) {
		return originalY - 58;
	}
	

}