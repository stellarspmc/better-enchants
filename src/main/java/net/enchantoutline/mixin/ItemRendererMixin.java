package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.EnchantmentGlintOutline;
import net.enchantoutline.config.ItemOverride;
import net.enchantoutline.util.Shaders;
import net.enchantoutline.util.RenderLayerHelper;
import net.enchantoutline.util.ThickenedVertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    /**
     * Intercepts the rendering pipeline right before vanilla draws the model list geometry.
     */
    @WrapOperation(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void enchantOutline$onRenderModelLists(ItemRenderer instance, BakedModel model, ItemStack stack, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer originalConsumer, Operation<Void> original, ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHanded, PoseStack matrices, MultiBufferSource bufferSource) {
        try {
            // Track the current item frame context so your RenderLayerHelper can query it
            RenderLayerHelper.setCurrentlyRenderingStack(itemStack);
            EnchantmentGlintOutline.LOGGER.info(itemStack.toString());

            // 1. Run the vanilla rendering pass first so items render normally underneath
            original.call(instance, model, stack, packedLight, packedOverlay, poseStack, originalConsumer);

            // 2. Evaluate configurations and overrides
            var config = EnchantmentGlintOutline.getConfig();
            if (config == null || !config.isEnabled() || !itemStack.hasFoil()) return;

            @Nullable ItemOverride override = config.getItemOverride(itemStack.getItem().toString());
            if (override != null && !override.shouldRender()) return;

            // 3. Process custom outline overlays
            float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, false));

            // Grab item particle sprite to feed into texture sheet coordinates
            var sprite = model.getParticleIcon();

            if (config.getRenderSolidOverrideOrDefault(override, false)) {
                int colorTint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

                RenderType colorLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(sprite, EnchantmentGlintOutline.COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, false);
                RenderType zFixLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(sprite, EnchantmentGlintOutline.ZFIX_LAYERS, Shaders::createZFixRenderLayerNoCull, Shaders::createZFixRenderLayerCull, Shaders.ZFIX_CUTOUT_LAYER, false);

                // Wrap buffer requests inside our stream thickener
                VertexConsumer colorConsumer = new ThickenedVertexConsumer(bufferSource.getBuffer(colorLayer), scale);
                VertexConsumer zFixConsumer = new ThickenedVertexConsumer(bufferSource.getBuffer(zFixLayer), scale);

                // Directly stream geometry through the thickener passes
                original.call(instance, model, stack, Integer.MAX_VALUE, packedOverlay, poseStack, colorConsumer);
                original.call(instance, model, stack, packedLight, packedOverlay, poseStack, zFixConsumer);

            } else {
                RenderType glintLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(sprite, EnchantmentGlintOutline.GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, false);

                VertexConsumer customGlintConsumer = new ThickenedVertexConsumer(bufferSource.getBuffer(glintLayer), scale);
                VertexConsumer nativeGlintConsumer = new ThickenedVertexConsumer(bufferSource.getBuffer(RenderType.glint()), scale);

                // Directly stream geometry through the thickener passes
                original.call(instance, model, stack, packedLight, packedOverlay, poseStack, customGlintConsumer);
                original.call(instance, model, stack, packedLight, packedOverlay, poseStack, nativeGlintConsumer);
            }
        } finally {
            // Clean up the thread container immediately after drawing finishes
            RenderLayerHelper.clearCurrentRenderingStack();
        }
    }
}