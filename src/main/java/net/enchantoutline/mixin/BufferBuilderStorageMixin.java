package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.enchantoutline.events.BufferBuilderModifyReturnValueEvent;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderBuffers.class)
public class BufferBuilderStorageMixin {

    @ModifyReturnValue(method = "bufferSource", at = @At("RETURN"))
    private MultiBufferSource.BufferSource enchantOutline$getEntityVertexConsumers(MultiBufferSource.BufferSource original) {
        // 1. Create the NeoForge event object, passing the original buffer source
        BufferBuilderModifyReturnValueEvent event = new BufferBuilderModifyReturnValueEvent(original);

        // 2. Post the event to NeoForge's global event bus so listeners can see it
        NeoForge.EVENT_BUS.post(event);

        // 3. Extract the modified result from the event
        MultiBufferSource.BufferSource result = event.getResult();

        // 4. Return the new source if someone changed it, otherwise fall back to original
        if (result != null) {
            return result;
        }

        return original;
    }
}