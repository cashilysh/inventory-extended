package inventoryextended.mixin;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

//@Environment(EnvType.CLIENT)
@Mixin(GenericContainerScreen.class)
public abstract class ChestsDrawExtraBackground extends HandledScreen<GenericContainerScreenHandler> {
	
    @Shadow private int rows;
    
    private static final Identifier INVTEXTURE = Identifier.ofVanilla("textures/gui/container/inventory.png");

    public ChestsDrawExtraBackground(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
	
	
	@Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.backgroundHeight += 58; // Adding 3 more rows (18px each) needed otherwise items will drop out of inventory
    }
	

    @Inject(method = "drawBackground", at = @At("RETURN"))
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, INVTEXTURE, i, j + this.rows * 18 + 73, 0.0F, 126.0F, this.backgroundWidth, 96, 256, 256);
    }
	
}