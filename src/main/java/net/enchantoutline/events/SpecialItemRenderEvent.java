package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge 1.21.1 equivalent for special item rendering lifecycles (shields, chests, banners, etc.).
 * Fired right around BlockEntityWithoutLevelRenderer execution.
 */
public class SpecialItemRenderEvent extends Event {
    private final ItemStack itemStack;
    private final ItemDisplayContext displayContext;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;
    private final int packedLight;
    private final int packedOverlay;

    public SpecialItemRenderEvent(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.itemStack = itemStack;
        this.displayContext = displayContext;
        this.poseStack = poseStack;
        this.bufferSource = bufferSource;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
    }

    public ItemStack getItemStack() { return itemStack; }
    public ItemDisplayContext getDisplayContext() { return displayContext; }
    public PoseStack getPoseStack() { return poseStack; }
    public MultiBufferSource getBufferSource() { return bufferSource; }
    public int getPackedLight() { return packedLight; }
    public int getPackedOverlay() { return packedOverlay; }

    /**
     * Replaces Callback<T>
     * Cancel this to skip vanilla BEWLR rendering.
     */
    public static class Pre extends SpecialItemRenderEvent implements ICancellableEvent {
        public Pre(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            super(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }

    /**
     * Replaces Post<T>
     */
    public static class Post extends SpecialItemRenderEvent {
        public Post(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
            super(itemStack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}