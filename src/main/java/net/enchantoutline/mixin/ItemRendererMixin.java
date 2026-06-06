package net.enchantoutline.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void enchant_outline$addItemOutlinePass(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!stack.hasFoil()) return;
        if (ctx == ItemDisplayContext.GUI) return;

        ItemRenderer self = (ItemRenderer) (Object) this;
        VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getItemOutlineLayer());
        float thickness = 0.025f;

        float[][] offsets = {
                {thickness, 0.0f},  {-thickness, 0.0f},  {0.0f, thickness},  {0.0f, -thickness},
                {thickness, thickness}, {thickness, -thickness}, {-thickness, thickness}, {-thickness, -thickness}
        };

        for (float[] offset : offsets) {
            poseStack.pushPose();
            model.applyTransform(ctx, poseStack, leftHand);
            poseStack.translate(-0.5f, -0.5f, -0.5f);
            poseStack.translate(offset[0], offset[1], 0.0f);
            self.renderModelLists(model, stack, light, overlay, poseStack, outlineBuffer);
            poseStack.popPose();
        }
    }
}