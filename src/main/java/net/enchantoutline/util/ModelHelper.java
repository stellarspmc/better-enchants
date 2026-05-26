package net.enchantoutline.util;

import net.enchantoutline.model.HijackedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;

public class ModelHelper {
    public static final ThreadLocal<Boolean> FLIP_CUBOIDS = ThreadLocal.withInitial(() -> false);

    // CHANGED: 1.21.1 Models do not natively store a root() part. We must pass the root in directly.
    public static HijackedModel getThickenedModel(ModelPart root, Function<ResourceLocation, RenderType> layerFactory, float scale){
        ModelPart thickenedRoot = thickenedModelPart(root, scale);

        return new HijackedModel(thickenedRoot, layerFactory);
    }

    public static ModelPart thickenedModelPart(ModelPart original, float scale){
        //the times 15 is just because it turns out that makes the output about visually equal to the bakedItemRenderer
        return thickenedModelPartInternal(original, scale * ModelPart.Vertex.SCALE_FACTOR);
    }

    //get thick
    private static ModelPart thickenedModelPartInternal(ModelPart original, float scale) {
        ModelPartAccessor accessor = (ModelPartAccessor) (Object) original;

        // 1. Process current cuboids
        List<ModelPart.Cube> newCuboids = new ArrayList<>();
        for (var cube : accessor.enchantOutline$getCuboids()) {
            ModelPart_CubeAccessor cubeAccessor = (ModelPart_CubeAccessor) cube;
            for (Direction dir : cubeAccessor.enchantOutline$getDirections()) {
                newCuboids.addAll(thickenCuboidFace(cube, dir, scale));
            }
        }

        // 2. Build the new ModelPart with the thickened cuboids
        ModelPart newPart = new ModelPart(newCuboids, Map.of());
        newPart.loadPose(original.storePose());
        newPart.setInitialPose(original.getInitialPose());

        // 3. Recursively process children
        Map<String, ModelPart> oldChildren = accessor.enchantOutline$getChildren();
        for (var entry : oldChildren.entrySet()) {
            // Use setChild to correctly link the hierarchy
            newPart.setChild(entry.getKey(), thickenedModelPartInternal(entry.getValue(), scale));
        }

        return newPart;
    }

    private static List<ModelPart.Cube> thickenCuboidFace(ModelPart.Cube original, Direction dir, float scale/*, PoseStack stack*/){
        List<ModelPart.Cube> thickenedCuboids = new ArrayList<>(4);

        Vector3f normal = new Vector3f(dir.step());
        normal.mul(scale);

        ModelPart_CubeAccessor accessor = ((ModelPart_CubeAccessor)original);

        Vector3f[] verts;

        int u = accessor.enchantOutline$getU();
        int v = accessor.enchantOutline$getV();

        float x = original.minX;
        float y = original.minY;
        float z = original.minZ;

        Vector3f startPos = new Vector3f(x, y, z);

        float sizeX = original.maxX - x;
        float sizeY = original.maxY - y;
        float sizeZ = original.maxZ - z;

        float extraX = accessor.enchantOutline$getExtraX();
        float extraY = accessor.enchantOutline$getExtraY();
        float extraZ = accessor.enchantOutline$getExtraZ();

        boolean mirror = accessor.enchantOutline$getMirror();

        float f = x + sizeX;
        float g = y + sizeY;
        float h = z + sizeZ;

        x -= extraX;
        y -= extraY;
        z -= extraZ;

        f += extraX;
        g += extraY;
        h += extraZ;
        if (mirror) {
            float i = f;
            f = x;
            x = i;
        }

        Vector3f vertex = new Vector3f(x, y, z);
        Vector3f vertex2 = new Vector3f(f, y, z);
        Vector3f vertex3 = new Vector3f(f, g, z);
        Vector3f vertex4 = new Vector3f(x, g, z);
        Vector3f vertex5 = new Vector3f(x, y, h);
        Vector3f vertex6 = new Vector3f(f, y, h);
        Vector3f vertex7 = new Vector3f(f, g, h);
        Vector3f vertex8 = new Vector3f(x, g, h);

        if(dir.equals(Direction.DOWN)){
            verts = new Vector3f[]{vertex6, vertex5, vertex, vertex2};
        }else if(dir.equals(Direction.UP)){
            verts = new Vector3f[]{vertex3, vertex4, vertex8, vertex7};
        }else if(dir.equals(Direction.WEST)){
            verts = new Vector3f[]{vertex, vertex5, vertex8, vertex4};
        }else if(dir.equals(Direction.NORTH)){
            verts = new Vector3f[]{vertex2, vertex, vertex4, vertex3};
        }else if(dir.equals(Direction.EAST)){
            verts = new Vector3f[]{vertex6, vertex2, vertex3, vertex7};
        }else {
            verts = new Vector3f[]{vertex5, vertex6, vertex7, vertex8};
        }

        Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(verts, scale);
        if(cardinalDirs != null) {
            for (Vector3f cardDir : cardinalDirs) {
                Vector3f movedPos = VertexHelper.growVert(startPos, cardDir, normal);
                ModelHelper.FLIP_CUBOIDS.set(true);
                thickenedCuboids.add(new ModelPart.Cube(u, v, movedPos.x(), movedPos.y(), movedPos.z(), sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, accessor.enchantOutline$getTextureWidth(), accessor.enchantOutline$getTextureHeight(), Set.of(dir)));
                ModelHelper.FLIP_CUBOIDS.set(false);
            }
        }

        return thickenedCuboids;
    }
}