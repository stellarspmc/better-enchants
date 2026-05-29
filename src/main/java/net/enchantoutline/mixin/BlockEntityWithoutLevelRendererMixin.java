package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.Shaders;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Shadow
    private ShieldModel shieldModel;

    @Shadow
    private TridentModel tridentModel;

    @Inject(method = "renderByItem", at = @At("TAIL"))
    private void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource bufferSource, int a, int b, CallbackInfo ci) {
        if (ctx == ItemDisplayContext.GUI) return;
        if (!stack.hasFoil()) return;

        if (stack.is(Items.SHIELD)) {
            VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getEntityOutlineLayer(ModelBakery.NO_PATTERN_SHIELD.atlasLocation()));

            shieldModel.renderToBuffer(poseStack, outlineBuffer, a, OverlayTexture.NO_OVERLAY);
        } if (stack.is(Items.TRIDENT)) {
            VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getEntityOutlineLayer(TridentModel.TEXTURE));
            poseStack.pushPose();
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180.0F));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
            //poseStack.scale(1.1f, 1.1f, 1.1f);

            tridentModel.renderToBuffer(poseStack, outlineBuffer, a, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

    }

}
