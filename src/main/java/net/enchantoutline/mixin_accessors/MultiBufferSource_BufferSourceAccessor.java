package net.enchantoutline.mixin_accessors;

import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.BufferBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MultiBufferSource_BufferSourceAccessor {
    Map<RenderType, BufferBuilder> enchantOutline$getLayerBuffers();
    Map<Object, Integer> enchantOutline$getDirtyMap();
    @Nullable Integer enchantOutline$getDirty(Object o);
    void enchantOutline$setDirty(Object o, int newDirty);
}