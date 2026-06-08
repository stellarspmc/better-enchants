package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.enchantoutline.config.GlintOutlineConfig;
import net.enchantoutline.util.MixinHelper;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
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

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "render", at = @At("HEAD"))
    private void enchant_outline$addItemOutlinePass(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!(GlintOutlineConfig.ENABLED.get() && GlintOutlineConfig.ITEMS_ENABLED.get())) return;
        if (MixinHelper.inventoryOutline(ctx)) return;
        if (GlintOutlineConfig.BLACKLISTED_ITEMS.contains(stack.getItem())) return;
        if (MixinHelper.invertedCheckFoilOrEnchanted(stack)) return;

        if (IClientItemExtensions.of(stack).getCustomRenderer() != null) return;
        String modId = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        if (modId.equals("avaritia")) return;

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

        for (float[] baseOffset : OUTLINE_OFFSETS_BASE) {
            poseStack.pushPose();

            model.applyTransform(ctx, poseStack, leftHand);
            poseStack.translate(baseOffset[0] * thickness, baseOffset[1] * thickness, 0.0f);

            ((ItemRenderer) (Object) this).renderModelLists(model, stack, light, overlay, poseStack, bufferSource.getBuffer(Shaders.getItemOutlineLayer()));

            poseStack.popPose();
        }
    }
}