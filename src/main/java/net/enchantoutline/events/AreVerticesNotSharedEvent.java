package net.enchantoutline.events;

import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge equivalent of Fabric's AreVerticesNotSharedCallback event.
 * Fired to determine if vertices should not be shared for a specific RenderType.
 */
public class AreVerticesNotSharedEvent extends Event {
    private final RenderType receiver;
    private final boolean original;
    @Nullable
    private Boolean result;

    public AreVerticesNotSharedEvent(RenderType receiver, boolean original) {
        this.receiver = receiver;
        this.original = original;
        this.result = null; // null means no listener has forced an override yet
    }

    public RenderType getReceiver() {
        return this.receiver;
    }

    public boolean getOriginal() {
        return this.original;
    }

    @Nullable
    public Boolean getResult() {
        return this.result;
    }

    /**
     * Set the overridden value. True forces unshared vertices, False forces shared,
     * leaving it untouched (null) falls back to vanilla/original behavior.
     */
    public void setResult(@Nullable Boolean result) {
        this.result = result;
    }
}