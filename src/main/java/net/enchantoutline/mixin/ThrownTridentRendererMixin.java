package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.enchantoutline.GlintOutline;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTridentRenderer.class)
public class ThrownTridentRendererMixin {

    @Final @Shadow private TridentModel model;

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void better_enchants$addOutlinePass(ThrownTrident trident, float yaw, float pitch, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        if (!trident.isFoil()) return;

        VertexConsumer consumer = bufferSource.getBuffer(Shaders.getModelOutlineLayer());

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pitch, trident.yRotO, trident.getYRot()) - 90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pitch, trident.xRotO, trident.getXRot()) + 90f));

        GlintOutline.IS_RENDERING_OUTLINE.set(true);
        this.model.root.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);
        GlintOutline.IS_RENDERING_OUTLINE.remove();

        poseStack.popPose();
    }

}
