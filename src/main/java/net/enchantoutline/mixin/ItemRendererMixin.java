package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.config.GlintOutlineConfig;
import net.enchantoutline.util.MixinHelper;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Unique
    private static final float[][] OUTLINE_OFFSETS_BASE = {
            {1.0f, 0.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f},  {0.0f, -1.0f},
            {1.0f, 1.0f},  {1.0f, -1.0f},  {-1.0f, 1.0f}, {-1.0f, -1.0f}
    };

    @Shadow @Final private BlockEntityWithoutLevelRenderer blockEntityRenderer;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "render", at = @At("HEAD"))
    private void enchant_outline$addItemOutlinePass(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!(GlintOutlineConfig.ENABLED.get() && GlintOutlineConfig.ITEMS_ENABLED.get())) return;
        if (MixinHelper.inventoryOutline(ctx)) return;
        if (GlintOutlineConfig.BLACKLISTED_ITEMS.contains(stack.getItem())) return;
        if (MixinHelper.invertedCheckFoilOrEnchanted(stack)) return;
        var customRenderer = IClientItemExtensions.of(stack).getCustomRenderer();
        LOGGER.info("{} {}", stack.getDisplayName().getString(), model.isCustomRenderer());

        if (model.isCustomRenderer() && customRenderer == this.blockEntityRenderer) return;

        // are streams slower?
        if (Shaders.itemShaderInstance != null) {
            var glowUniform = Shaders.itemShaderInstance.getUniform("GlowColor");
            if (glowUniform != null) {
                List<? extends Double> color = GlintOutlineConfig.OUTLINE_COLOR.get();
                glowUniform.set(
                        color.get(0).floatValue() / 255f,
                        color.get(1).floatValue() / 255f,
                        color.get(2).floatValue() / 255f,
                        color.get(3).floatValue() / 255f
                );
            }
        }
        float thickness = (float) GlintOutlineConfig.OUTLINE_SIZE.getAsDouble();
        MultiBufferSource outlineWrapper = renderType -> bufferSource.getBuffer(Shaders.getItemOutlineLayer());
        VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getItemOutlineLayer());
        for (float[] baseOffset: OUTLINE_OFFSETS_BASE) {
            poseStack.pushPose();
            model.applyTransform(ctx, poseStack, leftHand);
            poseStack.translate(baseOffset[0] * thickness - .5f, baseOffset[1] * thickness - .5f, -.5f);
            if (customRenderer != null) customRenderer.renderByItem(stack, ctx, poseStack, outlineWrapper, light, overlay);
            else ((ItemRenderer) (Object) this).renderModelLists(model, stack, light, overlay, poseStack, outlineBuffer);
            poseStack.popPose();
        }
    }
}