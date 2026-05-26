package net.enchantoutline.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * A class to copy models. The input root must have the same traverse as the original model
 */
public class HijackedModel extends Model {
    private final ModelPart root;

    public HijackedModel(ModelPart root, Function<ResourceLocation, RenderType> layerFactory) {
        super(layerFactory);
        this.root = root; // 1.21.1 Model doesn't store this automatically, so we do it manually
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        // Explicitly render the root part since the base 1.21.1 Model class doesn't handle it anymore
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}