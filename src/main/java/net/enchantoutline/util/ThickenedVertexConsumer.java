package net.enchantoutline.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jetbrains.annotations.NotNull;

public class ThickenedVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;
    private final float scale;

    // Local vertex builder data cache
    private float cachedX, cachedY, cachedZ;
    private int cachedColor = 0xFFFFFFFF;
    private float cachedU, cachedV;
    private int cachedU1, cachedV1; // UV1 usually holds Overlay
    private int cachedU2, cachedV2; // UV2 usually holds Light
    private float cachedNx, cachedNy, cachedNz;
    private boolean hasVertex = false;

    public ThickenedVertexConsumer(VertexConsumer delegate, float scale) {
        this.delegate = delegate;
        this.scale = scale;
    }

    // --- 1.21.1 Mapping Replacements ---

    @Override
    public @NotNull VertexConsumer addVertex(float x, float y, float z) {
        this.cachedX = x;
        this.cachedY = y;
        this.cachedZ = z;
        this.hasVertex = true;
        return this;
    }

    @Override
    public @NotNull VertexConsumer setColor(int r, int g, int b, int a) {
        this.cachedColor = (a << 24) | (r << 16) | (g << 8) | b;
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv(float u, float v) {
        this.cachedU = u;
        this.cachedV = v;
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv1(int u, int v) {
        this.cachedU1 = u;
        this.cachedV1 = v;
        return this;
    }

    @Override
    public @NotNull VertexConsumer setUv2(int u, int v) {
        this.cachedU2 = u;
        this.cachedV2 = v;
        return this;
    }

    @Override
    public @NotNull VertexConsumer setNormal(float x, float y, float z) {
        this.cachedNx = x;
        this.cachedNy = y;
        this.cachedNz = z;

        // In Mojang's modern pipeline layout state machine,
        // setNormal is typically the final element right before a vertex is compiled.
        // We flush/extrude the vertex right here or on a fallback mechanism.
        flushVertex();
        return this;
    }

    /**
     * Replaces the old endVertex processing step by calculating
     * the extruded normal direction and flushing down to our delegate.
     */
    private void flushVertex() {
        if (hasVertex) {
            // Apply vertex thickening calculation along normal direction vectors
            float expandedX = cachedX + (cachedNx * scale);
            float expandedY = cachedY + (cachedNy * scale);
            float expandedZ = cachedZ + (cachedNz * scale);

            // Forward the calculations sequentially down to the native pipeline buffer
            delegate.addVertex(expandedX, expandedY, expandedZ);

            // Unpack color data fields
            int a = (cachedColor >> 24) & 0xFF;
            int r = (cachedColor >> 16) & 0xFF;
            int g = (cachedColor >> 8) & 0xFF;
            int b = cachedColor & 0xFF;
            delegate.setColor(r, g, b, a);

            delegate.setUv(cachedU, cachedV);
            delegate.setUv1(cachedU1, cachedV1);
            delegate.setUv2(cachedU2, cachedV2);
            delegate.setNormal(cachedNx, cachedNy, cachedNz);
        }
        hasVertex = false;
    }

    // --- Bulk Data Passthroughs ---

    @Override
    public void putBulkData(PoseStack.@NotNull Pose pose, @NotNull BakedQuad bakedQuad, float r, float g, float b, float a, int light, int overlay, boolean readExistingColor) {
        delegate.putBulkData(pose, bakedQuad, r, g, b, a, light, overlay, readExistingColor);
    }

    @Override
    public void putBulkData(PoseStack.@NotNull Pose pose, @NotNull BakedQuad quad, float @NotNull [] brightness, float r, float g, float b, float a, int @NotNull [] light, int overlay, boolean readExistingColor) {
        delegate.putBulkData(pose, quad, brightness, r, g, b, a, light, overlay, readExistingColor);
    }
}