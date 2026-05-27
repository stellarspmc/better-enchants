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
    private void addOutlinePass(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!stack.isEnchanted()) return;

        ItemRenderer self = (ItemRenderer) (Object) this;
        VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getOutlineLayer());
        float thickness = 0.025f;

        // 8-way offset matrix to build a clean outline around corners
        float[][] offsets = {
                {thickness, 0.0f},  {-thickness, 0.0f},  {0.0f, thickness},  {0.0f, -thickness},
                {thickness, thickness}, {thickness, -thickness}, {-thickness, thickness}, {-thickness, -thickness}
        };

        // Render the backdrop glow passes
        for (float[] offset : offsets) {
            poseStack.pushPose();

            // Apply standard Minecraft transformations for the current perspective
            model.applyTransform(ctx, poseStack, leftHand);
            poseStack.translate(-0.5f, -0.5f, -0.5f);

            // Shift the geometry slightly out to form the outline edge.
            // We leave Z at 0.0f because this mixin runs at HEAD (before the real item).
            // Minecraft's renderer will naturally draw the real item over the center of this!
            poseStack.translate(offset[0], offset[1], 0.0f);

            // Draw this layer of the outline
            self.renderModelLists(model, stack, light, overlay, poseStack, outlineBuffer);

            poseStack.popPose();
        }
    }
}