package net.enchantoutline.util;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class RenderLayerHelper {
    public static final Logger LOGGER = LoggerFactory.getLogger(RenderLayerHelper.class);

    /**
     * Checks if a RenderType has culling disabled (double-sided rendering).
     */
    public static boolean isRenderLayerDoubleSided(RenderType renderLayer) {
        if (renderLayer instanceof RenderType.CompositeRenderType composite) {
            RenderType.CompositeState state = composite.state; // Accessed via AT
            if (state != null) return state.cullState == RenderStateShard.NO_CULL;

        }
        return false;
    }

    @Nullable
    public static Map<String, ResourceLocation> getIdentifierFromSprite(TextureAtlasSprite sprite) {
        if (sprite != null) {
            ResourceLocation atlas = sprite.atlasLocation();
            return Map.of(atlas.getPath(), atlas);
        }
        return null;
    }

    /**
     * Extracts the texture ResourceLocation straight from the 1.21.1 RenderType state.
     */
    @Nullable
    public static Map<String, ResourceLocation> getIdentifierFromRenderLayer(RenderType layer) {
        if (layer instanceof RenderType.CompositeRenderType composite) {
            RenderType.CompositeState state = composite.state; // Accessed via AT
            if (state != null && state.textureState instanceof RenderStateShard.TextureStateShard textureShard) {
                // texture field is an Optional<ResourceLocation> accessed via AT
                return textureShard.texture
                        .map(id -> Map.of(id.getPath(), id))
                        .orElse(null);
            }
        }
        return null;
    }

    public static RenderType renderLayerFromRenderLayerDoubleSided(RenderType renderLayer, CustomRenderLayers customRenderLayers, Function<Map<String, ResourceLocation>, RenderType> doubleSidedFactory, Function<Map<String, ResourceLocation>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        return renderLayerFromMapDoubleSided(getIdentifierFromRenderLayer(renderLayer), customRenderLayers, doubleSidedFactory, singleSidedFactory, fallback, isDoubleSided);
    }

    public static RenderType renderLayerFromSpriteDoubleSided(TextureAtlasSprite sprite, CustomRenderLayers customRenderLayers, Function<Map<String, ResourceLocation>, RenderType> doubleSidedFactory, Function<Map<String, ResourceLocation>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        return renderLayerFromMapDoubleSided(getIdentifierFromSprite(sprite), customRenderLayers, doubleSidedFactory, singleSidedFactory, fallback, isDoubleSided);
    }

    public static RenderType renderLayerFromMapDoubleSided(@Nullable Map<String, ResourceLocation> identifier, CustomRenderLayers customRenderLayers, Function<Map<String, ResourceLocation>, RenderType> doubleSidedFactory, Function<Map<String, ResourceLocation>, RenderType> singleSidedFactory, RenderType fallback, boolean isDoubleSided){
        String first;
        if (identifier != null) first = identifier.entrySet().stream().findFirst().map(Map.Entry::getKey).orElse(null);
        else first = null;


        if (isDoubleSided) return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, doubleSidedFactory, identifier, first + "_db"), fallback);
        return renderLayerFromMapWithFallback(identifier, (id) -> getOrCreateRenderLayerMap(customRenderLayers, singleSidedFactory, identifier, first), fallback);
    }

    public static RenderType renderLayerFromMapWithFallback(@Nullable Map<String, ResourceLocation> identifier, Function<Map<String, ResourceLocation>, RenderType> layerFactory, RenderType fallback){
        if (identifier != null) {
            RenderType newLayer = layerFactory.apply(identifier);
            if (newLayer != null) return newLayer;
        }
        return fallback;
    }

    @Nullable
    public static RenderType getOrCreateRenderLayerMap(CustomRenderLayers customRenderLayers, Function<Map<String, ResourceLocation>, RenderType> layerFactory, Map<String, ResourceLocation> identifier, String storagePath){
        RenderType output = customRenderLayers.getCustomRenderLayer(storagePath);
        if (output != null) {
            return output;
        }
        return customRenderLayers.addCustomRenderLayer(storagePath, layerFactory.apply(identifier));
    }

    @Nullable
    public static RenderType getOrCreateRenderLayer(CustomRenderLayers customRenderLayers, Function<ResourceLocation, RenderType> layerFactory, ResourceLocation identifier, String storagePath){
        RenderType output = customRenderLayers.getCustomRenderLayer(storagePath);
        if (output != null) {
            return output;
        }
        return customRenderLayers.addCustomRenderLayer(storagePath, layerFactory.apply(identifier));
    }

    @Nullable
    public static RenderType getOrCreateRenderLayer(CustomRenderLayers customRenderLayers, Function<ResourceLocation, RenderType> layerCreationFunction, ResourceLocation identifier){
        return getOrCreateRenderLayer(customRenderLayers, layerCreationFunction, identifier, identifier.toString());
    }
}