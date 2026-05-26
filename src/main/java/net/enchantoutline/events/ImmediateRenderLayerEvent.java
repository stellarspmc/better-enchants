package net.enchantoutline.events;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge 1.21.1 equivalent of ImmediateRenderCurrentLayer.
 * Tracks the lifecycle phases right before and after a BufferSource draws a specific RenderType layer.
 */
public class ImmediateRenderLayerEvent extends Event {
    private final MultiBufferSource.BufferSource bufferSource;
    private final RenderType renderLayer;

    public ImmediateRenderLayerEvent(MultiBufferSource.BufferSource bufferSource, RenderType renderLayer) {
        this.bufferSource = bufferSource;
        this.renderLayer = renderLayer;
    }

    public MultiBufferSource.BufferSource getBufferSource() {
        return this.bufferSource;
    }

    public RenderType getRenderLayer() {
        return this.renderLayer;
    }

    /**
     * Replaces Before interface.
     * Cancel this event to skip vanilla drawing execution for this layer.
     */
    public static class Before extends ImmediateRenderLayerEvent implements ICancellableEvent {
        public Before(MultiBufferSource.BufferSource bufferSource, RenderType renderLayer) {
            super(bufferSource, renderLayer);
        }
    }

    /**
     * Replaces After interface.
     */
    public static class After extends ImmediateRenderLayerEvent {
        public After(MultiBufferSource.BufferSource bufferSource, RenderType renderLayer) {
            super(bufferSource, renderLayer);
        }
    }
}