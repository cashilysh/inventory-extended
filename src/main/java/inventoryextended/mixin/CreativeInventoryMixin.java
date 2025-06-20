package inventoryextended.mixin;


import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.minecraft.item.ItemGroups.INVENTORY;


@SuppressWarnings({"overwrite", "MissingJavadoc"})
//@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryMixin {

    @Inject(method = "onMouseClick", at = @At("HEAD"))
    private void debugMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot != null) {
            System.out.println("Slot clicked: " + slot.id + ", Index: " + slot.getIndex() + ", Type: " + actionType);
        }
    }

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

    @ModifyConstant(method = "onMouseClick", constant = @Constant(intValue = 45))
    private int modify45again(int original) {
        return original + 27;
    }


    @ModifyConstant(method = "onHotbarKeyPress", constant = @Constant(intValue = 36))
    private static int modify36again2(int original) {
        return original + 27;
    }


}