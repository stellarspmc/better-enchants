package net.enchantoutline.util;

import org.joml.Vector3f;

public class VertexHelper {

    public static int[] flip(int[] inVertexData) {
        int vertices = inVertexData.length/8;
        int[] outVertexData = new int[inVertexData.length];
        for(int i =  0; i < vertices; i++) {
            int stride = 8;
            System.arraycopy(inVertexData, i*stride, outVertexData, (vertices-i-1) * stride, stride);
        }
        return outVertexData;
    }

    public static Vector3f[] getFaceCardinalDirs(Vector3f[] defaultVerts, float scale) {
        if (defaultVerts.length == 4) {
            Vector3f uAxis = new Vector3f(defaultVerts[1]).sub(defaultVerts[0]).normalize().mul(scale);
            Vector3f vAxis = new Vector3f(defaultVerts[3]).sub(defaultVerts[0]).normalize().mul(scale);

            return new Vector3f[] {
                    new Vector3f(uAxis),
                    new Vector3f(uAxis).negate(),
                    new Vector3f(vAxis),
                    new Vector3f(vAxis).negate(),
                    new Vector3f(uAxis).add(vAxis),
                    new Vector3f(uAxis).sub(vAxis),
                    new Vector3f(uAxis).negate().add(vAxis),
                    new Vector3f(uAxis).negate().sub(vAxis)
            };
        }
        return null;
    }

    public static Vector3f[] growFace(Vector3f[] defaultVerts, Vector3f cardinalDir, Vector3f scaledNormal) {
        Vector3f[] vertPoses = new Vector3f[defaultVerts.length];
        Vector3f offsetNormal = new Vector3f(scaledNormal);

        Vector3f tinyPush = new Vector3f(offsetNormal).normalize().mul(0.0001f);
        offsetNormal.add(tinyPush);

        for (int i = 0; i < defaultVerts.length; i++) {
            Vector3f vert = new Vector3f(defaultVerts[i]);
            vert.add(offsetNormal);
            vert.add(cardinalDir);
            vertPoses[i] = vert;
        }
        return vertPoses;
    }

    /**
     * stolen from BakedQuadFactory, since its private for some reason, and I don't want to make a mixin to get what is just int manipulation
     */
    public static void packVertexData(int[] vertices, int cornerIndex, Vector3f pos, float u, float v) {
        int i = cornerIndex * 8;
        vertices[i] = Float.floatToRawIntBits(pos.x());
        vertices[i + 1] = Float.floatToRawIntBits(pos.y());
        vertices[i + 2] = Float.floatToRawIntBits(pos.z());
        vertices[i + 3] = -1;
        vertices[i + 4] = Float.floatToRawIntBits(u);
        vertices[i + 5] = Float.floatToRawIntBits(v);
    }
}