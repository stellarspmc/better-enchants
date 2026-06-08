package net.enchantoutline.mixin.avaritia;

import com.mojang.blaze3d.vertex.PoseStack;
import net.enchantoutline.util.AvaritiaWrappedBridge;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "committee.nova.mods.avaritia.api.client.model.bakedmodels.WrappedItemModel")
public abstract class AvaritiaWrappedMixin implements AvaritiaWrappedBridge {

    @Shadow(remap = false)
    public abstract void renderWrapped(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, boolean allowRender);

    @Override
    public void enchant_outline$bridgeRenderWrapped(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, boolean allowRender) {
        // Route the bridge call directly into the shadowed mod method safely
        this.renderWrapped(stack, matrixStack, buffer, combinedLight, combinedOverlay, allowRender);
    }
}