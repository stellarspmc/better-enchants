package net.enchantoutline.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3f;

public class ThickenedVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float scale;

    public ThickenedVertexConsumer(VertexConsumer delegate, float scale) {
        this.delegate = delegate;
        this.scale = scale;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        // Here you inflate the vertex based on its position relative to the center
        // Or better: use the normal if provided in the next calls
        return delegate.vertex(x, y, z);
    }

    // Override other required methods to pass through to delegate...
    // Only 'vertex' needs the inflation logic.
}