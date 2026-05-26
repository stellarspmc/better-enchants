package net.enchantoutline.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;

public class ModelHelper {

    /**
     * Intercepts and thickens armor/equipment renders by wrapping the pipeline's
     * vertex builder instead of modifying immutable game objects.
     */
    public static void renderThickened(Model model, PoseStack poseStack, VertexConsumer consumer, int light, int overlay, int tint, float scale) {
        // Intercept positions and normals on the fly
        ThickenedVertexConsumer thickConsumer = new ThickenedVertexConsumer(consumer, scale);

        // Let Minecraft compute sub-parts, children, and animations automatically
        model.renderToBuffer(poseStack, thickConsumer, light, overlay, tint);
    }
}