package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired right before a specific ModelPart renders its cubes in 1.21.1.
 * Cancel this event to bypass vanilla rendering and handle custom drawing instead.
 */
public class ModelPartRenderEvent extends Event implements ICancellableEvent {
    private final ModelPart modelPart;
    private final PoseStack poseStack;
    private final VertexConsumer vertexConsumer;
    private final int packedLight;
    private final int packedOverlay;
    private final int color;

    public ModelPartRenderEvent(ModelPart modelPart, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.modelPart = modelPart;
        this.poseStack = poseStack;
        this.vertexConsumer = vertexConsumer;
        this.packedLight = packedLight;
        this.packedOverlay = packedOverlay;
        this.color = color;
    }

    public ModelPart getModelPart() { return modelPart; }
    public PoseStack getPoseStack() { return poseStack; }
    public VertexConsumer getVertexConsumer() { return vertexConsumer; }
    public int getPackedLight() { return packedLight; }
    public int getPackedOverlay() { return packedOverlay; }
    public int getColor() { return color; }
}