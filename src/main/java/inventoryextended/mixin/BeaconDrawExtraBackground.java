package inventoryextended.mixin;

import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

//@Environment(EnvType.CLIENT)
@Mixin(BeaconScreen.class)
public abstract class BeaconDrawExtraBackground extends HandledScreen<BeaconScreenHandler> {

    
    private static final Identifier INVTEXTURE = Identifier.ofVanilla("textures/gui/container/beacon.png");

    public BeaconDrawExtraBackground(BeaconScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
	

	@Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.backgroundHeight += 52; // Adding 3 more rows (18px each) needed otherwise items will drop out of inventory
    }
	


    @Inject(method = "drawBackground", at = @At("RETURN"))
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
		
		context.drawTexture(RenderLayer::getGuiTextured, INVTEXTURE, i, j+190, 0.0F, 136.0F, this.backgroundWidth, 83, 256, 256);
    }
	
}