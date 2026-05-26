package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge 1.21.1 equivalent of ItemRenderStateRenderLayerCallback.
 * Fired right before a specific model layout phase processes its vertex streams.
 */
public class ItemModelRenderLayerEvent extends Event implements ICancellableEvent {
    private final ItemStack itemStack;
    private final BakedModel model;
    private final ItemDisplayContext displayContext;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;
    private final int packedOverlay;

    public ItemModelRenderLayerEvent(ItemStack itemStack, BakedModel model, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.itemStack = itemStack;
        this.model = model;
        this.displayContext = displayContext;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    // ---------- Getters ----------
    public ItemStack getItemStack() { return itemStack; }
    public BakedModel getModel() { return model; }
    public ItemDisplayContext getDisplayContext() { return displayContext; }
    public PoseStack getPoseStack() { return poseStack; }
    public MultiBufferSource getBufferSource() { return bufferSource; }
    public int getPackedLight() { return packedLight; }
    public int getPackedOverlay() { return packedOverlay; }
}