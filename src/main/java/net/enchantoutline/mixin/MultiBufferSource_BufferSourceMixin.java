package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.enchantoutline.events.ImmediateRenderLayerEvent;
import net.enchantoutline.mixin_accessors.MultiBufferSource_BufferSourceAccessor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(MultiBufferSource.BufferSource.class)
public class MultiBufferSource_BufferSourceMixin implements MultiBufferSource_BufferSourceAccessor {

    // 1. In 1.21.1, this is a normal Map storing BufferBuilder, not a SequencedMap storing ByteBufferBuilder
    @Shadow
    @Final
    protected Map<RenderType, BufferBuilder> fixedBuffers;

    /**
     * Intercepts layer rendering passes right as a batch completes in 1.21.1.
     * Replaces the missing 1.21.2 endLastBatch method.
     */
    @WrapOperation(
            method = "endBatch()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch(Lnet/minecraft/client/renderer/RenderType;)V"
            )
    )
    private void enchantOutline$OnDrawCurrentLayer(MultiBufferSource.BufferSource instance, RenderType layer, Operation<Void> original) {
        // 2. Fire our 1.21.1 custom event before rendering the layer
        ImmediateRenderLayerEvent.Before beforeEvent = new ImmediateRenderLayerEvent.Before(instance, layer);
        NeoForge.EVENT_BUS.post(beforeEvent);

        if (!beforeEvent.isCanceled()) {
            original.call(instance, layer);

            // 3. Fire our after event post rendering
            ImmediateRenderLayerEvent.After afterEvent = new ImmediateRenderLayerEvent.After(instance, layer);
            NeoForge.EVENT_BUS.post(afterEvent);
        }
    }

    @Unique
    private final Map<Object, Integer> better_enchants$dirty = new HashMap<>();

    @Override
    public Map<RenderType, BufferBuilder> enchantOutline$getLayerBuffers() {
        return this.fixedBuffers;
    }

    @Override
    public Map<Object, Integer> enchantOutline$getDirtyMap() {
        return this.better_enchants$dirty;
    }

    @Override
    public @Nullable Integer enchantOutline$getDirty(Object o) {
        return this.better_enchants$dirty.get(o);
    }

    @Override
    public void enchantOutline$setDirty(Object o, int newDirty) {
        this.better_enchants$dirty.put(o, newDirty);
    }
}