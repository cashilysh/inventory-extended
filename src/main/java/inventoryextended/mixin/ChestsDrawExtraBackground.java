package inventoryextended.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerScreen.class)
public abstract class ChestsDrawExtraBackground extends AbstractContainerScreen<ChestMenu> {

    @Shadow
    private int containerRows;

    private static final Identifier INVTEXTURE = Identifier.withDefaultNamespace("textures/gui/container/inventory.png");

    public ChestsDrawExtraBackground(ChestMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;<init>(Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/network/chat/Component;II)V"), index = 4)
    private int modifyImageHeight(int originalHeight) {
        return originalHeight + 58; // Adding 3 more rows (18px each) + some extra padding
    }

    @Inject(method = "renderBg", at = @At("RETURN"))
    protected void drawBackground(GuiGraphicsExtractor context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, INVTEXTURE, i, j + this.containerRows * 18 + 73, 0.0F, 126.0F, this.imageWidth, 96, 256, 256);
    }
}