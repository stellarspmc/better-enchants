package net.enchantoutline.util;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class Shaders {

    private static final Map<ResourceLocation, RenderType> GLINT_CACHE = Maps.newHashMap();
    public static ShaderInstance outlineShaderInstance;
    private static final RenderStateShard.ShaderStateShard OUTLINE_SHADER_SHARD = new RenderStateShard.ShaderStateShard(() -> outlineShaderInstance != null ? outlineShaderInstance : GameRenderer.getPositionTexShader());
    //private static final Supplier<ShaderInstance> OUTLINE_SHADER = () -> outlineShaderInstance;
    //private static final RenderStateShard.ShaderStateShard OUTLINE_SHADER_SHARD = new RenderStateShard.ShaderStateShard(OUTLINE_SHADER);

    public static final RenderType GLINT_CUTOUT_LAYER = RenderType.create(
            "enchout_glint_normal",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            true,  // affectsCrumbling
            false, // sortOnUpload
            RenderType.CompositeState.builder()
                    .setShaderState(OUTLINE_SHADER_SHARD)
                    .setWriteMaskState(RenderStateShard.DEPTH_WRITE)
                    .setCullState(RenderStateShard.CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .createCompositeState(false)
    );

    public static final RenderType COLOR_CUTOUT_LAYER = RenderType.create(
            "enchout_color_normal",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(OUTLINE_SHADER_SHARD)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .createCompositeState(false)
    );

    public static final RenderType ZFIX_CUTOUT_LAYER = RenderType.create(
            "enchout_zfix_normal",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(OUTLINE_SHADER_SHARD)
                    .setWriteMaskState(RenderStateShard.DEPTH_WRITE)
                    .setCullState(RenderStateShard.CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .createCompositeState(false)
    );

    /*public static final RenderType ARMOR_ENTITY_GLINT_FIX = RenderType.create(
            "enchout_glint_armor",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                    .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                    .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
                    .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/enchanted_glint_entity.png"), false, false))
                    .createCompositeState(false)
    );*/

    public static RenderType createGlintRenderLayerCull(Map<String, ResourceLocation> specMap) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.DEPTH_WRITE)
                .setCullState(RenderStateShard.CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLightmapState(RenderStateShard.LIGHTMAP);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        return RenderType.create("enchout_glint_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, false, builder.createCompositeState(false));
    }

    public static RenderType createGlintRenderLayerNoCull(Map<String, ResourceLocation> specMap) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.DEPTH_WRITE)
                .setCullState(RenderStateShard.NO_CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLightmapState(RenderStateShard.LIGHTMAP);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        return RenderType.create("enchout_glint_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, false, builder.createCompositeState(false));
    }

    public static RenderType createColorRenderLayerCull(Map<String, ResourceLocation> specMap) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setCullState(RenderStateShard.CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLightmapState(RenderStateShard.LIGHTMAP);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        return RenderType.create("enchout_color_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, false, builder.createCompositeState(false));
    }

    public static RenderType createColorRenderLayerNoCull(Map<String, ResourceLocation> specMap) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setCullState(RenderStateShard.NO_CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLightmapState(RenderStateShard.LIGHTMAP);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        return RenderType.create("enchout_color_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, false, builder.createCompositeState(false));
    }

    public static RenderType createZFixRenderLayerCull(Map<String, ResourceLocation> specMap) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.DEPTH_WRITE)
                .setCullState(RenderStateShard.CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        return RenderType.create("enchout_zfix_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, false, builder.createCompositeState(false));
    }

    public static RenderType createZFixRenderLayerNoCull(Map<String, ResourceLocation> specMap) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.DEPTH_WRITE)
                .setCullState(RenderStateShard.NO_CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        return RenderType.create("enchout_zfix_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, false, builder.createCompositeState(false));
    }
}