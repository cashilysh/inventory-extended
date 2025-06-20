package inventoryextended.mixin;


import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.v1.FabricCreativeInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.ItemGroup.Row;
import net.minecraft.item.ItemGroup.Type;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.item.tooltip.TooltipType.Default;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.screen.slot.Slot;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.inventory.Inventory;

import static net.minecraft.item.ItemGroups.INVENTORY;

@Mixin(CreativeInventoryScreen.CreativeScreenHandler.class)
public class CreativeScreenHandlerMixin {




    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 5))
    private int modifyCreativeRows(int original) {
        return original + 0; // Expand creative grid from 5x9 to 8x9 to match your inventory expansion
    }


/*
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen$CreativeScreenHandler;addPlayerHotbarSlots(Lnet/minecraft/inventory/Inventory;II)V"))
    private void fixHotbarSlotAlignment(CreativeInventoryScreen.CreativeScreenHandler handler, Inventory playerInventory, int left, int y) {
        // Add dummy slots to reach the correct index (63)
        // Using a simple inventory for dummy slots since they're hidden
        SimpleInventory dummyInventory = new SimpleInventory(27);

        while(handler.slots.size() < 63) {
            handler.addSlot(new Slot(dummyInventory, 0, -2000, -2000)); // Hidden dummy slots
        }

        // Now add hotbar at the correct indices (63-71)
        for(int i = 0; i < 9; ++i) {
            handler.addSlot(new Slot(playerInventory, i, left + i * 18, y));
        }
    }

    // Update the hotbar Y position constant
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 112))
    private int modifyHotbarY(int original) {
        return 166; // Match your expanded inventory hotbar position
    }

 */
}