package net.enchantoutline.mixin.avaritia;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.config.GlintOutlineConfig;
import net.enchantoutline.util.MixinHelper;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = {
        "committee.nova.mods.avaritia.client.model.loader.CosmicBakeModel",
        "committee.nova.mods.avaritia.client.model.loader.EternalBakeModel"
})
public abstract class AvaritiaBakedModelMixin {

    @Unique
    private static final float[][] OUTLINE_OFFSETS_BASE = {
            {1.0f, 0.0f},  {-1.0f, 0.0f},  {0.0f, 1.0f},  {0.0f, -1.0f},
            {1.0f, 1.0f},  {1.0f, -1.0f},  {-1.0f, 1.0f}, {-1.0f, -1.0f}
    };

    @Shadow(remap = false) public abstract void renderWrapped(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, boolean allowRender);
    @Shadow(remap = false) public abstract void renderCosmicLayer(ItemStack stack, ItemDisplayContext transformType, PoseStack pStack, MultiBufferSource source, int packedLight, int packedOverlay);

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void enchant_outline$addAvaritiaOutlinePass(ItemStack stack, ItemDisplayContext transformType, PoseStack pStack, MultiBufferSource source, int packedLight, int packedOverlay, CallbackInfo ci) {
        if (!(GlintOutlineConfig.ENABLED.get() && GlintOutlineConfig.ITEMS_ENABLED.get())) return;
        if (MixinHelper.inventoryOutline(transformType)) return;
        if (GlintOutlineConfig.BLACKLISTED_ITEMS.contains(stack.getItem())) return;
        if (MixinHelper.invertedCheckFoilOrEnchanted(stack)) return;

        float thickness = (float) GlintOutlineConfig.OUTLINE_SIZE.getAsDouble();

        MultiBufferSource outlineWrapper = renderType -> new VertexConsumer() {
            private final VertexConsumer parent = source.getBuffer(
                    renderType == Shaders.getItemOutlineLayer() ? Shaders.getItemOutlineLayer() : Shaders.getModelOutlineLayer()
            );

            @Override public @NotNull VertexConsumer addVertex(float x, float y, float z) { parent.addVertex(x, y, z); return this; }
            @Override public @NotNull VertexConsumer setColor(int r, int g, int b, int a) { parent.setColor(r, g, b, a); return this; }
            @Override public @NotNull VertexConsumer setUv(float u, float v) { parent.setUv(u, v); return this; }
            @Override public @NotNull VertexConsumer setUv1(int u, int v) { parent.setUv1(u, v); return this; }
            @Override public @NotNull VertexConsumer setOverlay(int u) { parent.setOverlay(u); return this; }
            @Override public @NotNull VertexConsumer setUv2(int u, int v) { parent.setUv2(u, v); return this; }
            @Override public @NotNull VertexConsumer setNormal(float x, float y, float z) { parent.setNormal(x, y, z); return this; }
        };

        for (float[] baseOffset : OUTLINE_OFFSETS_BASE) {
            pStack.pushPose();
            pStack.translate(baseOffset[0] * thickness, baseOffset[1] * thickness, 0.0f);
            this.renderWrapped(stack, pStack, outlineWrapper, packedLight, packedOverlay, true);
            this.renderCosmicLayer(stack, transformType, pStack, outlineWrapper, packedLight, packedOverlay);
            pStack.popPose();
        }
    }
}