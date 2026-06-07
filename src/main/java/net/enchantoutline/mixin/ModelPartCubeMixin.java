package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.GlintOutline;
import net.enchantoutline.util.VertexHelper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPart.Cube.class)
public class ModelPartCubeMixin {

    @Shadow
    @Final
    private ModelPart.Polygon[] polygons;

    @Inject(method = "compile", at = @At("HEAD"), cancellable = true)
    public void better_enchants$outlineMagic(PoseStack.Pose pose, VertexConsumer consumer, int p_171335_, int p_171336_, int p_350744_, CallbackInfo ci) {
        if (GlintOutline.IS_RENDERING_OUTLINE.get()) {
            for(var quad : polygons) {
                var vertices = quad.vertices;
                Vector3f[] defaultVerts = new Vector3f[vertices.length];

                for(int i = 0; i < defaultVerts.length; i++) defaultVerts[i] = (new Vector3f(vertices[i].pos)).div(16.0F);
                Vector3f faceVec = new Vector3f(quad.normal);
                faceVec.normalize();
                faceVec.mul(GlintOutline.SCALE);

                Vector3f[] cardinalDirs = VertexHelper.getFaceCardinalDirs(defaultVerts, GlintOutline.SCALE);
                if (cardinalDirs != null) {
                    for (Vector3f dir : cardinalDirs) {
                        Vector3f[] vertPoses = VertexHelper.growFace(defaultVerts, dir, faceVec);
                        int[] vertexData = new int[vertPoses.length * 8];

                        for(int i = 0; i < vertPoses.length; i++) VertexHelper.packVertexData(vertexData, i, vertPoses[i], vertices[i].u, vertices[i].v);
                        BakedQuad enchantmentQuad = new BakedQuad(VertexHelper.flip(vertexData), -1, Direction.getNearest(quad.normal.x, quad.normal.y, quad.normal.z), null, false, true);
                        consumer.putBulkData(pose, enchantmentQuad, GlintOutline.OUTLINE_COLOR[0], GlintOutline.OUTLINE_COLOR[1], GlintOutline.OUTLINE_COLOR[2], .99f, 0, 0); // I love the inconsistency about this
                    }
                }
            }
            ci.cancel();
        }
    }
}
