package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.config.GlintOutlineConfig;
import net.enchantoutline.util.MixinHelper;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Unique
    private static final float[][] OUTLINE_OFFSETS_BASE = {
            {1.0f, 0.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f},  {0.0f, -1.0f},
            {1.0f, 1.0f},  {1.0f, -1.0f},  {-1.0f, 1.0f}, {-1.0f, -1.0f}
    };

    @Shadow @Final private ItemModelShaper itemModelShaper;
    @Shadow @Final private TextureManager textureManager;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "render", at = @At("HEAD"))
    private void enchant_outline$addItemOutlinePass(ItemStack stack, ItemDisplayContext ctx, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!(GlintOutlineConfig.ENABLED.get() && GlintOutlineConfig.ITEMS_ENABLED.get())) return;
        if (MixinHelper.inventoryOutline(ctx)) return;
        if (GlintOutlineConfig.BLACKLISTED_ITEMS.contains(stack.getItem())) return;
        if (MixinHelper.invertedCheckFoilOrEnchanted(stack)) return;

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
        var customRenderer = IClientItemExtensions.of(stack).getCustomRenderer();
        MultiBufferSource outlineWrapper = renderType -> new VertexConsumer() {
            private final VertexConsumer parent = bufferSource.getBuffer(Shaders.getModelOutlineLayer());
            @Override public @NotNull VertexConsumer addVertex(float x, float y, float z) { parent.addVertex(x, y, z); return this; }
            @Override public @NotNull VertexConsumer setColor(int r, int g, int b, int a) { parent.setColor(r, g, b, a); return this; }
            @Override public @NotNull VertexConsumer setUv(float u, float v) { parent.setUv(u, v); return this; }
            @Override public @NotNull VertexConsumer setUv1(int u, int v) { parent.setUv1(u, v); return this; }
            @Override public @NotNull VertexConsumer setOverlay(int u) { parent.setOverlay(u); return this; }
            @Override public @NotNull VertexConsumer setUv2(int u, int v) { parent.setUv2(u, v); return this; }
            @Override public @NotNull VertexConsumer setNormal(float x, float y, float z) { parent.setNormal(x, y, z); return this; }
        };
        VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getItemOutlineLayer());
        ItemRenderer self = (ItemRenderer) (Object) this;

        for (float[] baseOffset : OUTLINE_OFFSETS_BASE) {
            poseStack.pushPose();

            BakedModel transformedModel = ClientHooks.handleCameraTransforms(poseStack, model, ctx, leftHand);
            poseStack.translate(-0.5f + (baseOffset[0] * thickness), -0.5f + (baseOffset[1] * thickness), -0.5f);

            // the impending hell of whatever the fuck this is
            String className = transformedModel.getClass().getName();
            boolean avaritia = model.getClass().getName().contains("committee.nova");
            if (transformedModel.isCustomRenderer() && customRenderer != null && !avaritia) // custom BEWLR
                customRenderer.renderByItem(stack, ctx, poseStack, outlineWrapper, light, overlay);
            else if (avaritia || className.contains("committee.nova")) { // avaritia things
                if (className.contains("Bake") || className.contains("Model")) { // the better classes
                    self.renderModelLists(better_enchants$unwrapModel(transformedModel), stack, light, overlay, poseStack, outlineBuffer);
                    poseStack.popPose();
                    continue;
                } else { // fucking hell
                    try {
                        transformedModel.getClass().getMethod("renderItem",
                                        ItemStack.class, ItemDisplayContext.class, PoseStack.class, MultiBufferSource.class,
                                        int.class, int.class, ItemModelShaper.class, TextureManager.class)
                                .invoke(transformedModel, stack, ctx, poseStack, outlineWrapper, light, overlay, this.itemModelShaper, this.textureManager);
                    } catch (Exception e) {
                        self.renderModelLists(transformedModel, stack, light, overlay, poseStack, outlineBuffer);
                    }
                }
            } else self.renderModelLists(transformedModel, stack, light, overlay, poseStack, outlineBuffer); // vanilla
            poseStack.popPose();
        }
    }

    @Unique
    private static final Map<BakedModel, BakedModel> better_enchants$unwrapCache =
            Collections.synchronizedMap(new WeakHashMap<>());

    @Unique
    private static BakedModel better_enchants$unwrapModel(BakedModel cosmicModel) {
        if (cosmicModel == null) return null;
        if (better_enchants$unwrapCache.containsKey(cosmicModel)) return better_enchants$unwrapCache.get(cosmicModel);

        Class<?> clazz = cosmicModel.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                for (Field field : clazz.getDeclaredFields()) {
                    // Scenario A: Standard BakedModel field pointer
                    if (BakedModel.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        BakedModel internalModel = (BakedModel) field.get(cosmicModel);
                        if (internalModel != null && internalModel != cosmicModel) {
                            BakedModel unwrapped = better_enchants$unwrapModel(internalModel);
                            better_enchants$unwrapCache.put(cosmicModel, unwrapped);
                            return unwrapped;
                        }
                    }

                    else if (Supplier.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        Object value = field.get(cosmicModel);
                        if (value instanceof Supplier<?> supplier) {
                            Object resolved = supplier.get();
                            if (resolved instanceof BakedModel internalModel && internalModel != cosmicModel) {
                                BakedModel unwrapped = better_enchants$unwrapModel(internalModel);
                                better_enchants$unwrapCache.put(cosmicModel, unwrapped);
                                return unwrapped;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
            clazz = clazz.getSuperclass();
        }

        better_enchants$unwrapCache.put(cosmicModel, cosmicModel);
        return cosmicModel;
    }
}