package net.enchantoutline.events;

import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge equivalent of the Fabric BufferBuilderModifyReturnValue event.
 * This event is fired when you want to modify or intercept a BufferSource.
 */
public class BufferBuilderModifyReturnValueEvent extends Event {
    private final MultiBufferSource.BufferSource original;
    @Nullable
    private MultiBufferSource.BufferSource result;

    public BufferBuilderModifyReturnValueEvent(MultiBufferSource.BufferSource original) {
        this.original = original;
        this.result = null;
    }

    public MultiBufferSource.BufferSource getOriginal() {
        return this.original;
    }

    @Nullable
    public MultiBufferSource.BufferSource getResult() {
        return this.result;
    }

    public void setResult(@Nullable MultiBufferSource.BufferSource result) {
        this.result = result;
    }
}