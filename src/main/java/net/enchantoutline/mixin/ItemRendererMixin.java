package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.enchantoutline.EnchantmentGlintOutline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.enchantoutline.events.ItemModelUpdateEvent;
import net.enchantoutline.EnchantmentGlintOutlineFabricOld;
import net.enchantoutline.config.ItemOverride;
import net.enchantoutline.shader.Shaders;
import net.enchantoutline.util.QuadHelper;
import net.enchantoutline.events.ItemRenderEvent;
import net.enchantoutline.util.RenderLayerHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import java.util.List;

import static net.enchantoutline.EnchantmentGlintOutlineFabricOld.*;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Unique
    private static final ThreadLocal<ItemStack> CURRENT_ITEM_STACK_STORAGE = ThreadLocal.withInitial(() -> null);

    /**
     * Public getter so your ModelPart registration listener can securely access the item context.
     */
    @Unique
    public static ItemStack enchantOutline$getCurrentlyRenderingStack() {
        return CURRENT_ITEM_STACK_STORAGE.get();
    }

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V")
    )
    private void enchantOutline$beforeRenderModel(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        try {
            CURRENT_ITEM_STACK_STORAGE.set(itemStack);

            var config = EnchantmentGlintOutlineFabricOld.getConfig();
            if (config != null && config.isEnabled() && itemStack.hasFoil()) {
                List<BakedQuad> quads = model.getQuads(null, null, net.minecraft.util.RandomSource.create(42L));
                if (!quads.isEmpty()) better_enchants$renderEnchantmentOutline(poseStack, bufferSource, model, quads, null);
            }
        } finally {
            CURRENT_ITEM_STACK_STORAGE.remove(); // Use .remove() to clean up the ThreadLocal
        }
    }

    /**
     * Hooks into the 1.21.1 point where item models are evaluated using the item stack context.
     */
    @Inject(
            method = "getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void enchantOutline$onGetModel(ItemStack stack, Level level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel originalModel = cir.getReturnValue();
        ClientLevel clientLevel = level instanceof ClientLevel ? (ClientLevel) level : null;

        ItemModelUpdateEvent event = new ItemModelUpdateEvent(stack, originalModel, clientLevel, entity, seed);
        NeoForge.EVENT_BUS.post(event);

        if (event.getModel() != originalModel) {
            cir.setReturnValue(event.getModel());
        }
    }

    /**
     * Hooks into the primary item render sequence in 1.21.1 to fire events.
     */
    @WrapOperation(
            method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderModelLists(Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/item/ItemStack;IILcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V"
            )
    )
    private void enchantOutline$onRenderModelLists(
            ItemRenderer instance,
            BakedModel model,
            ItemStack stack,
            int packedLight,
            int packedOverlay,
            PoseStack poseStack,
            com.mojang.blaze3d.vertex.VertexConsumer normalConsumer,
            Operation<Void> original,
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            boolean leftHanded,
            PoseStack matrices,
            MultiBufferSource bufferSource
    ) {
        ItemRenderEvent.Pre preEvent = new ItemRenderEvent.Pre(
                itemStack, matrices, bufferSource, packedLight, packedOverlay, displayContext, model
        );
        NeoForge.EVENT_BUS.post(preEvent);

        if (preEvent.isCanceled()) {
            return;
        }

        original.call(instance, model, stack, packedLight, packedOverlay, poseStack, normalConsumer);

        ItemRenderEvent.Post postEvent = new ItemRenderEvent.Post(
                itemStack, matrices, bufferSource, packedLight, packedOverlay, displayContext, model
        );
        NeoForge.EVENT_BUS.post(postEvent);
    }

    // Inside your ItemRendererMixin or a custom RenderingUtil class
    @Unique
    private static void better_enchants$renderEnchantmentOutline(PoseStack poseStack, MultiBufferSource bufferSource, BakedModel model, List<BakedQuad> quads, int[] tintLayers) {
        var config = EnchantmentGlintOutline.getConfig();
        @Nullable ItemOverride override = EnchantmentGlintOutline.getActiveOverride();

        if (override == null || !override.shouldRender()) return;

        float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, false));
        List<BakedQuad> thickenedQuads = QuadHelper.thickenQuad(quads, scale);

        // 1.21.1: Grab sprite from the first quad
        var sprite = quads.getFirst().getSprite();

        if (config.getRenderSolidOverrideOrDefault(override, false)) {
            int colorTint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));
            int zFixTint = (tintLayers != null && tintLayers.length > 0) ? tintLayers[0] : 0xFFFFFFFF;

            RenderType colorLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(sprite, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, false);
            RenderType zFixLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(sprite, ZFIX_LAYERS, Shaders::createZFixRenderLayerNoCull, Shaders::createZFixRenderLayerCull, Shaders.ZFIX_CUTOUT_LAYER, false);

            // Immediate mode render: fetch consumer and draw
            QuadHelper.renderCustomGeometryFromQuads(poseStack.last(), bufferSource.getBuffer(colorLayer), thickenedQuads, colorTint);
            QuadHelper.renderCustomGeometryFromQuads(poseStack.last(), bufferSource.getBuffer(zFixLayer), thickenedQuads, zFixTint);

        } else {
            int glintTint = (tintLayers != null && tintLayers.length > 0) ? tintLayers[0] : 0xFFFFFFFF;

            RenderType glintLayer = RenderLayerHelper.renderLayerFromSpriteDoubleSided(sprite, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, false);

            // Immediate mode render
            QuadHelper.renderCustomGeometryFromQuads(poseStack.last(), bufferSource.getBuffer(glintLayer), thickenedQuads, glintTint);
            QuadHelper.renderCustomGeometryFromQuads(poseStack.last(), bufferSource.getBuffer(RenderType.glint()), thickenedQuads, glintTint);
        }
    }
}