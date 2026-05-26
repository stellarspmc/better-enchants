package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.enchantoutline.events.AreVerticesNotSharedEvent;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderType.class)
public class RenderLayerMixin implements RenderLayerAccessor {

    @ModifyReturnValue(method = "canConsolidateConsecutiveGeometry", at = @At("RETURN"))
    private boolean enchantOutline$canConsolidateConsecutiveGeometry(boolean original) {
        // Create and post the NeoForge event.
        // We pass '!original' because 'not shared' is the inverse of 'can consolidate'.
        AreVerticesNotSharedEvent event = new AreVerticesNotSharedEvent((RenderType) (Object) this, !original);
        NeoForge.EVENT_BUS.post(event);

        Boolean result = event.getResult();
        if (result != null) {
            // If a listener explicitly demands "vertices are not shared",
            // we must return FALSE to disallow consecutive geometry consolidation.
            return !result;
        }

        return original;
    }

    @Unique
    private boolean better_enchants$shouldUseLayerBuffer = true;

    @Unique
    private byte better_enchants$drawBeforeAfterCustom = 0;

    @Override
    public boolean enchantOutline$shouldUseLayerBuffer() {
        return better_enchants$shouldUseLayerBuffer;
    }

    @Override
    public void enchantOutline$setShouldUseLayerBuffer(boolean newUseLayerBuffer) {
        better_enchants$shouldUseLayerBuffer = newUseLayerBuffer;
    }

    @Override
    public boolean enchantOutline$shouldDrawBeforeCustom() {
        return better_enchants$drawBeforeAfterCustom == -1;
    }

    @Override
    public boolean enchantOutline$shouldDrawAfterCustom() {
        return better_enchants$drawBeforeAfterCustom == 1;
    }

    @Override
    public void enchantOutline$setDrawBeforeCustom(boolean drawBeforeCustom) {
        if (drawBeforeCustom) {
            this.better_enchants$drawBeforeAfterCustom = -1;
            return;
        }
        this.better_enchants$drawBeforeAfterCustom = 0;
    }
}