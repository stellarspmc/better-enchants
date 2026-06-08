package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.config.GlintOutlineConfig;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Unique
    private static final float[][] OUTLINE_OFFSETS_BASE = {
            {1.0f, 0.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f},  {0.0f, -1.0f},
            {1.0f, 1.0f},  {1.0f, -1.0f},  {-1.0f, 1.0f}, {-1.0f, -1.0f}
    };

    @Inject(method = "render", at = @At("HEAD"))
    private void enchant_outline$addItemOutlinePass(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!stack.hasFoil()) return;
        if (ctx == ItemDisplayContext.GUI) return;
        if (GlintOutlineConfig.BLACKLISTED_ITEMS.contains(stack.getItem())) return;

        if (Shaders.itemShaderInstance != null) {
            var glowUniform = Shaders.itemShaderInstance.getUniform("GlowColor");
            if (glowUniform != null) {
                List<Float> color = GlintOutlineConfig.OUTLINE_COLOR.get().stream().map(d -> d.floatValue() / 255f).toList();
                glowUniform.set(color.get(0), color.get(1), color.get(2), color.get(3));
            }
        }

        ItemRenderer self = (ItemRenderer) (Object) this;
        VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getItemOutlineLayer());
        float thickness = (float) GlintOutlineConfig.OUTLINE_SIZE.getAsDouble();

        for (float[] baseOffset : OUTLINE_OFFSETS_BASE) {
            poseStack.pushPose();
            model.applyTransform(ctx, poseStack, leftHand);
            poseStack.translate(baseOffset[0] * thickness - .5f, baseOffset[1] * thickness - .5f, -.5f);
            self.renderModelLists(model, stack, light, overlay, poseStack, outlineBuffer);
            poseStack.popPose();
        }
    }
}