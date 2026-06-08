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

    public static final Vector3f[] CARDINAL_DIR_POOL = new Vector3f[8];
    public static final Vector3f[] GROWN_VERT_POOL = new Vector3f[4];

    private static final Vector3f offsetNormal = new Vector3f();
    private static final Vector3f tinyPush = new Vector3f();

    public static Vector3f[] growFace(Vector3f[] defaultVerts, Vector3f cardinalDir, Vector3f scaledNormal) {
        int len = defaultVerts.length;
        offsetNormal.set(scaledNormal);
        tinyPush.set(offsetNormal).normalize().mul(0.0001f);
        offsetNormal.add(tinyPush);

        for (int i = 0; i < len; i++) GROWN_VERT_POOL[i].set(defaultVerts[i]).add(offsetNormal).add(cardinalDir);

        return GROWN_VERT_POOL;
    }

    private static final Vector3f uAxis = new Vector3f();
    private static final Vector3f vAxis = new Vector3f();

    public static Vector3f[] getFaceCardinalDirs(Vector3f[] defaultVerts, float scale) {
        if (defaultVerts.length != 4) return null;

        uAxis.set(defaultVerts[1]).sub(defaultVerts[0]).normalize().mul(scale);
        vAxis.set(defaultVerts[3]).sub(defaultVerts[0]).normalize().mul(scale);
        CARDINAL_DIR_POOL[0].set(uAxis);
        CARDINAL_DIR_POOL[1].set(uAxis).negate();
        CARDINAL_DIR_POOL[2].set(vAxis);
        CARDINAL_DIR_POOL[3].set(vAxis).negate();
        CARDINAL_DIR_POOL[4].set(uAxis).add(vAxis);
        CARDINAL_DIR_POOL[5].set(uAxis).sub(vAxis);
        CARDINAL_DIR_POOL[6].set(uAxis).negate().add(vAxis);
        CARDINAL_DIR_POOL[7].set(uAxis).negate().sub(vAxis);

        return CARDINAL_DIR_POOL;
    }

    static {
        for (int i = 0; i < 8; i++) CARDINAL_DIR_POOL[i] = new Vector3f();
        for (int i = 0; i < 4; i++) GROWN_VERT_POOL[i] = new Vector3f();
    }
}