package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ItemRenderEvent extends Event {
    private final ItemStack itemStack;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;
    private final int packedOverlay;
    private final ItemDisplayContext displayContext;
    private final BakedModel model;

    public ItemRenderEvent(ItemStack itemStack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, ItemDisplayContext displayContext, BakedModel model) {
        this.itemStack = itemStack;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
        this.displayContext = displayContext;
        this.model = model;
    }

    public ItemStack getItemStack() { return itemStack; }
    public PoseStack getPoseStack() { return poseStack; }
    public MultiBufferSource getBufferSource() { return bufferSource; }
    public int getPackedLight() { return packedLight; }
    public int getPackedOverlay() { return packedOverlay; }
    public ItemDisplayContext getDisplayContext() { return displayContext; }
    public BakedModel getModel() { return model; }

    public static class Pre extends ItemRenderEvent implements ICancellableEvent {
        public Pre(ItemStack itemStack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, ItemDisplayContext displayContext, BakedModel model) {
            super(itemStack, poseStack, bufferSource, packedLight, packedOverlay, displayContext, model);
        }
    }

    public static class Post extends ItemRenderEvent {
        public Post(ItemStack itemStack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, ItemDisplayContext displayContext, BakedModel model) {
            super(itemStack, poseStack, bufferSource, packedLight, packedOverlay, displayContext, model);
        }
    }
}