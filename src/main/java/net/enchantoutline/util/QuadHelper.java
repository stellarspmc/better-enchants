package net.enchantoutline.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QuadHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(QuadHelper.class);

    public static List<BakedQuad> thickenQuad(List<BakedQuad> original, float percentSize){
        List<BakedQuad> newQuads = new ArrayList<>(original.size()*4);
        for (BakedQuad quad : original) {

            // 1.21.1 stores vertex data in a raw int array (8 ints per vertex = 32 total)
            int[] vData = quad.getVertices();
            Vector3f[] defaultVerts = new Vector3f[4];

            // Unpack the X, Y, Z coordinates from the raw integer bits
            for (int i = 0; i < 4; i++) {
                float x = Float.intBitsToFloat(vData[i * 8]);
                float y = Float.intBitsToFloat(vData[i * 8 + 1]);
                float z = Float.intBitsToFloat(vData[i * 8 + 2]);
                defaultVerts[i] = new Vector3f(x, y, z);
            }

            // Mojmap renames direction() to getDirection() and getUnitVec3i() to getNormal()
            Vec3i intVec = quad.getDirection().getNormal();
            Vector3f faceVec = new Vector3f(intVec.getX(), intVec.getY(), intVec.getZ());
            faceVec.mul(percentSize);

            Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(defaultVerts, percentSize);
            if(cardinalDirs != null){
                for (Vector3f dir : cardinalDirs) {
                    Vector3f[] vertPoses = VertexHelper.growFace(defaultVerts, dir, faceVec);

                    // Build the new raw int array for the 1.21.1 quad
                    int[] newVData = new int[32];
                    int[] order = {3, 2, 1, 0}; // Reversed order for opposite facing

                    for (int i = 0; i < 4; i++) {
                        int oldIdx = order[i];
                        Vector3f newPos = vertPoses[oldIdx];

                        // Copy UVs, color, normal, etc. from the original vertex
                        System.arraycopy(vData, oldIdx * 8, newVData, i * 8, 8);

                        // Overwrite the position (first 3 ints) with our new thickened coordinates
                        newVData[i * 8] = Float.floatToRawIntBits(newPos.x());
                        newVData[i * 8 + 1] = Float.floatToRawIntBits(newPos.y());
                        newVData[i * 8 + 2] = Float.floatToRawIntBits(newPos.z());
                    }

                    // 1.21.1 BakedQuad constructor
                    BakedQuad enchantmentQuad = new BakedQuad(
                            newVData,
                            quad.getTintIndex(),
                            quad.getDirection().getOpposite(),
                            quad.getSprite(),
                            quad.isShade()
                    );

                    newQuads.add(enchantmentQuad);
                }
            } else {
                LOGGER.warn("Quad did not have 4 vertices");
            }
        }
        return newQuads;
    }


    public static void renderCustomGeometryFromQuads(PoseStack.Pose pose, VertexConsumer vc, List<BakedQuad> quads, int colorTint){
        // QuadInstance doesn't exist in 1.21.1, so we calculate the color manually
        float a = ((colorTint >> 24) & 0xFF) / 255.0f;
        float r = ((colorTint >> 16) & 0xFF) / 255.0f;
        float g = ((colorTint >> 8)  & 0xFF) / 255.0f;
        float b = ( colorTint        & 0xFF) / 255.0f;

        // Push the quads directly to the VertexConsumer as bulk data
        for (BakedQuad q : quads) {
            // LightTexture.FULL_BRIGHT forces it to render at max lighting like Integer.MAX_VALUE did
            vc.putBulkData(pose, q, r, g, b, a, LightTexture.FULL_BRIGHT, 0);
        }
    }
}