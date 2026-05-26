package net.enchantoutline.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.enchantoutline.EnchantmentGlintOutlineFabricOld;
import net.enchantoutline.mixin_accessors.RenderLayerAccessor;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class Shaders {
    private static final String MOD_ID = EnchantmentGlintOutlineFabricOld.MOD_ID;

    // --- 1.21.1 Shader Registration ---
    // Custom shaders are not built inline in 1.21.1. You must register "core/outline"
    // during NeoForge's RegisterShadersEvent. Store the returned ShaderInstance here.
    public static Supplier<ShaderInstance> OUTLINE_SHADER = () -> null;

    // We wrap our shader in a ShaderStateShard to plug it into the 1.21.1 RenderType system
    private static final RenderStateShard.ShaderStateShard OUTLINE_SHADER_SHARD = new RenderStateShard.ShaderStateShard(OUTLINE_SHADER);

    public static final RenderType GLINT_CUTOUT_LAYER = RenderType.create(
            "enchout_glint_normal",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            true,  // affectsCrumbling
            false, // sortOnUpload
            RenderType.CompositeState.builder()
                    .setShaderState(OUTLINE_SHADER_SHARD)
                    .setWriteMaskState(RenderStateShard.DEPTH_WRITE)       // Depth only, no color
                    .setCullState(RenderStateShard.CULL)                   // withCull(true)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST) // CompareOp.LESS_THAN_OR_EQUAL
                    .setLightmapState(RenderStateShard.LIGHTMAP)           // useLightmap()
                    // 1.21.1 uses LOCATION_BLOCKS for both blocks and items; LOCATION_ITEMS didn't exist yet
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
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)       // Color only, no depth write
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

    public static final RenderType ARMOR_ENTITY_GLINT_FIX = RenderType.create(
            "enchout_glint_armor",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER) // 1.21.1 native glint shader
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
                    .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
                    .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING) // Equivalent to setTextureTransform
                    // Hardcoded to standard glint texture since ItemFeatureRenderer was removed
                    .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/enchanted_glint_entity.png"), false, false))
                    .createCompositeState(false)
    );

    // --- Dynamic RenderType Generators ---

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
                .setCullState(RenderStateShard.NO_CULL) // No cull
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLightmapState(RenderStateShard.LIGHTMAP);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        return RenderType.create("enchout_glint_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, false, builder.createCompositeState(false));
    }

    public static RenderType createColorRenderLayerCull(Map<String, ResourceLocation> specMap) {
        return createColorRenderLayerCull(specMap, true);
    }

    public static RenderType createColorRenderLayerCull(Map<String, ResourceLocation> specMap, boolean before) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE) // Write color only
                .setCullState(RenderStateShard.CULL)
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLightmapState(RenderStateShard.LIGHTMAP);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        RenderType layer = RenderType.create("enchout_color_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, false, builder.createCompositeState(false));

        RenderLayerAccessor accessor = (RenderLayerAccessor) layer;
        accessor.enchantOutline$setDrawBeforeCustom(before);
        accessor.enchantOutline$setShouldUseLayerBuffer(!before);
        return layer;
    }

    public static RenderType createColorRenderLayerNoCull(Map<String, ResourceLocation> specMap) {
        return createColorRenderLayerNoCull(specMap, true);
    }

    public static RenderType createColorRenderLayerNoCull(Map<String, ResourceLocation> specMap, boolean before) {
        RenderType.CompositeState.CompositeStateBuilder builder = RenderType.CompositeState.builder()
                .setShaderState(OUTLINE_SHADER_SHARD)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setCullState(RenderStateShard.NO_CULL) // No cull
                .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                .setLightmapState(RenderStateShard.LIGHTMAP);

        if (!specMap.isEmpty()) {
            builder.setTextureState(new RenderStateShard.TextureStateShard(specMap.values().iterator().next(), false, false));
        }

        RenderType layer = RenderType.create("enchout_color_model", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, true, false, builder.createCompositeState(false));

        RenderLayerAccessor accessor = (RenderLayerAccessor) layer;
        accessor.enchantOutline$setDrawBeforeCustom(before);
        accessor.enchantOutline$setShouldUseLayerBuffer(!before);
        return layer;
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