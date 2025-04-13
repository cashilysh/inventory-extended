package inventoryextended.mixin;

import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.screen.HopperScreenHandler;

//@Environment(EnvType.CLIENT)
@Mixin(HopperScreen.class)
public abstract class HopperDrawExtraBackground extends HandledScreen<HopperScreenHandler> {

    
    private static final Identifier INVTEXTURE = Identifier.ofVanilla("textures/gui/container/beacon.png");

    public HopperDrawExtraBackground(HopperScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
	

	@Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.backgroundHeight += 52; // Adding 3 more rows (18px each) needed otherwise items will drop out of inventory
    }

	
}