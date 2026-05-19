package inventoryextended.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.HopperScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;

@Mixin(HopperScreen.class)
public abstract class HopperDrawExtraBackground extends AbstractContainerScreen<HopperMenu> {

    private static final Identifier INVTEXTURE = Identifier.withDefaultNamespace("textures/gui/container/beacon.png");

    public HopperDrawExtraBackground(HopperMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V"), index = 4)
    private int modifyImageHeight(int originalHeight) {
        return originalHeight + 52; // Change 133 to 185
    }
}