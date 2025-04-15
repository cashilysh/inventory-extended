package inventoryextended.mixin;


import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryMixin {
	

    @ModifyConstant(method = "setSelectedTab", constant = @Constant(intValue = 45))
    private int modify45(int original) {
        return original + 27;
    }
	
    @ModifyConstant(method = "setSelectedTab", constant = @Constant(intValue = 36))
    private int modify36(int original) {
        return original + 27;
    }
	
	//Creative inventory hot bar Y-Position
    @ModifyConstant(method = "setSelectedTab",constant = @Constant(intValue = 112))
    private int modify112(int original) {
        return original + 60;
    }


    @ModifyConstant(method = "onMouseClick", constant = @Constant(intValue = 36))
    private int modify36again(int original) {
        return original + 27;
    }

    @ModifyConstant(method = "onHotbarKeyPress", constant = @Constant(intValue = 36))
    private static int modify36again2(int original) {
        return original + 27;
    }

	
}