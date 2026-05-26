package net.enchantoutline.events;

import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * NeoForge 1.21.1 equivalent of WorldRendererFirstRenderMainPassCallback.
 * Fired right when the main solid/cutout geometry batch is completing.
 * Cancel this event to skip flushing the vanilla solid batch (allowing custom overrides).
 */
public class MainPassRenderEvent extends Event implements ICancellableEvent {
    private final MultiBufferSource.BufferSource bufferSource;

    public MainPassRenderEvent(MultiBufferSource.BufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    public MultiBufferSource.BufferSource getBufferSource() {
        return this.bufferSource;
    }
}