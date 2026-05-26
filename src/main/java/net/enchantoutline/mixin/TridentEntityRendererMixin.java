package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.events.TridentEntityRendererEnchantedEvent;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownTridentRenderer.class)
public class TridentEntityRendererMixin {

    @Shadow @Final private TridentModel model;

    /**
     * Intercepts the rendering of the trident model in 1.21.1.
     * We target the VertexConsumer render call on the TridentModel inside ThrownTridentRenderer#render.
     */
    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/TridentModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"
            )
    )
    private void enchantOutline$onRenderTridentModel(
            TridentModel modelInstance,
            PoseStack poseStack,
            VertexConsumer originalConsumer,
            int packedLight,
            int packedOverlay,
            Operation<Void> original,
            ThrownTrident entity,
            float entityYaw,
            float partialTicks,
            PoseStack matrixStack, // capture the outer parameters if needed
            MultiBufferSource bufferSource
    ) {
        // 1. Resolve what RenderType vanilla would use (trident uses entityCutoutNoCull)
        ResourceLocation texture = ((ThrownTridentRenderer)(Object)this).getTextureLocation(entity);
        RenderType vanillaLayer = RenderType.entityCutoutNoCull(texture);

        // 2. Fire our NeoForge 1.21.1 event
        // We pass the 1.21.1 bufferSource, model, entity, and render metrics
        TridentEntityRendererEnchantedEvent<ThrownTrident> event = new TridentEntityRendererEnchantedEvent<>(
                bufferSource, this.model, entity, poseStack, vanillaLayer, packedLight, packedOverlay, 0xFFFFFFFF, null, 0
        );

        NeoForge.EVENT_BUS.post(event);

        // 3. If the event is canceled (meaning a listener intercepted and drew the custom outline/geometry),
        // we skip the vanilla model render call. Otherwise, let vanilla execute normally.
        if (!event.isCanceled()) {
            original.call(modelInstance, poseStack, originalConsumer, packedLight, packedOverlay);
        }
    }
}