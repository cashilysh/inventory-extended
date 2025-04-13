package inventoryextended.mixin;

import net.minecraft.screen.PlayerScreenHandler;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;




@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(PlayerScreenHandler.class)
public abstract class RemapPlayerSlots {

	  @ModifyConstant(
    method = {"<clinit>", "isInHotbar", "quickMove"}, // Correct array syntax
        constant = @Constant(intValue = 36),
        require = 1
    )
    private static int modify36(int original) {
        return original + 27;
    }

    // Change 45 → 72 in all methods
    @ModifyConstant(
    method = {"<clinit>", "isInHotbar", "quickMove"}, // Correct array syntax
        constant = @Constant(intValue = 45),
        require = 1
    )
    private static int modify45(int original) {
        return original + 27;
    }
	
	// Change 46 → 73 in all methods
	    @ModifyConstant(
    method = {"<clinit>", "isInHotbar", "quickMove"}, // Correct array syntax
        constant = @Constant(intValue = 46),
        require = 1
    )
    private static int modify46(int original) {
        return original + 27;
    }
	
	


 // Changes GUI armor slot indices (39 → 39+27) at constructor
    @ModifyConstant(
        method = "<init>",
        constant = @Constant(intValue = 39)
    )
    private int modifyArmorSlotIndex(int original) {
        return original + 27; // 39 → 66
    }

    // Changes offhand slot index (40 → 40+27) at constructor
    @ModifyConstant(
        method = "<init>",
        constant = @Constant(intValue = 40)
    )
    private int modifyOffhandSlotIndex(int original) {
        return original + 27; // 40 → 67
    }
	


}