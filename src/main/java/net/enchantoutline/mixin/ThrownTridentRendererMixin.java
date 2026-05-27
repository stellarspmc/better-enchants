package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.Shaders;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
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
    private void addOutlinePass(ThrownTrident trident, float p_116112_, float p_116113_, PoseStack poseStack, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        if (!trident.getWeaponItem().hasFoil()) return;

        VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getArmorOutlineLayer());
        poseStack.pushPose();
        poseStack.scale(1.05F, 1.05F, 1.05F);
        model.renderToBuffer(poseStack, outlineBuffer, light, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

}
