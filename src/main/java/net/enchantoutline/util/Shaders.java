package net.enchantoutline.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class Shaders {

    public static ShaderInstance outlineShaderInstance;
    static final RenderStateShard.ShaderStateShard OUTLINE_SHADER_SHARD =
            new RenderStateShard.ShaderStateShard(() -> {
                if (outlineShaderInstance != null) return outlineShaderInstance;
                return GameRenderer.getPositionTexShader();
            });

    private static RenderType cachedOutlineLayer;

    public static RenderType getOutlineLayer() {
        if (cachedOutlineLayer == null) {
            cachedOutlineLayer = RenderType.create(
                    "enchout_outline",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    1536,
                    false, false,
                    RenderType.CompositeState.builder()
                            .setShaderState(OUTLINE_SHADER_SHARD)
                            .setTextureState(new RenderStateShard.TextureStateShard(
                                    TextureAtlas.LOCATION_BLOCKS, false, false))
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                            .createCompositeState(false)
            );
        }
        return cachedOutlineLayer;
    }
}