package net.enchantoutline.events;

import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class WorldRendererFirstRenderMainPassEvent extends Event implements ICancellableEvent {
    private InteractionResult result = InteractionResult.PASS;

    public WorldRendererFirstRenderMainPassEvent() {
    }

    /**
     * Gets the result of the render pass attempt.
     */
    public InteractionResult getInteractionResult() {
        return this.result;
    }

    /**
     * Sets the result. If the result is not PASS, this cancels the event,
     * preventing any other listeners from firing (mimicking Fabric's early return).
     */
    public void setInteractionResult(InteractionResult result) {
        this.result = result;
        if (result != InteractionResult.PASS) {
            this.setCanceled(true);
        }
    }
}