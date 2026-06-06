package net.enchantoutline.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.enchantoutline.GlintOutline;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

@EventBusSubscriber(modid = GlintOutline.MOD_ID)
public class Shaders {

    public static ShaderInstance itemShaderInstance;
    private static RenderType itemLayer;

    public static RenderType getItemOutlineLayer() {
        if (itemLayer == null) itemLayer = RenderType.create(
                "item",
                DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS,
                1536,
                false, false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                            if (itemShaderInstance != null) return itemShaderInstance;
                            return GameRenderer.getPositionTexShader();
                        }))
                        .setTextureState(new RenderStateShard.TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .setCullState(RenderStateShard.NO_CULL)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                        .createCompositeState(false)
        );

        return itemLayer;
    }

    public static ShaderInstance modelInstance;
    private static RenderType modelLayer;
    public static RenderType getModelOutlineLayer() {
        if (modelLayer == null) modelLayer = RenderType.create(
                "model",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                786432,
                true, false,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                            if (modelInstance != null) return modelInstance;
                            return GameRenderer.getPositionTexShader();
                        }))
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .setCullState(RenderStateShard.CULL)
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setOverlayState(RenderStateShard.NO_OVERLAY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .createCompositeState(true)
        );

        return modelLayer;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(GlintOutline.MOD_ID, "item"), DefaultVertexFormat.BLOCK), shaderInstance -> itemShaderInstance = shaderInstance);
            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(GlintOutline.MOD_ID, "model"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> modelInstance = shaderInstance);
        } catch (Exception e) {
            GlintOutline.LOGGER.error("Failed to load shaders!", e);
        }
    }
}