package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.Shaders;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTridentRenderer.class)
public class ThrownTridentRendererMixin {

    @Final
    @Shadow
    private TridentModel model;

    @Inject(method = "render(Lnet/minecraft/world/entity/projectile/ThrownTrident;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void addOutlinePass(ThrownTrident trident, float test1, float test2, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        if (!trident.isFoil()) return;

        float yaw = net.minecraft.util.Mth.lerp(test2, trident.yRotO, trident.getYRot());
        float pitch = net.minecraft.util.Mth.lerp(test2, trident.xRotO, trident.getXRot());

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(pitch + 90.0F));
        poseStack.scale(1.1f, 1.1f, 1.1f);

        VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getEntityOutlineLayer(TridentModel.TEXTURE));
        model.renderToBuffer(poseStack, outlineBuffer, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

}
