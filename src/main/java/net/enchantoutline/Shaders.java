package net.enchantoutline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.inventory.InventoryMenu;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Shaders {

    public static ShaderInstance itemShaderInstance;
    public static ShaderInstance armorShaderInstance;
    private static RenderType itemLayer;
    private static final Map<ResourceLocation, RenderType> armorLayerCache = new ConcurrentHashMap<>();

    public static final RenderStateShard.CullStateShard CULL_FRONT = new RenderStateShard.CullStateShard(true) {
        @Override
        public void setupRenderState() {
            RenderSystem.enableCull();
            GL11.glCullFace(GL11.GL_FRONT); // Force front-face culling
        }

        @Override
        public void clearRenderState() {
            GL11.glCullFace(GL11.GL_BACK);  // Reset back to normal back-face culling
        }
    };

    public static RenderType getItemOutlineLayer() {
        if (itemLayer == null) itemLayer = RenderType.create(
                "item",
                DefaultVertexFormat.NEW_ENTITY,
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

    public static RenderType getArmorOutlineLayer(ResourceLocation location) {
        return armorLayerCache.computeIfAbsent(location, texture -> RenderType.create(
                "armor",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                1536,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(new RenderStateShard.ShaderStateShard(() -> armorShaderInstance))
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setOverlayState(RenderStateShard.OVERLAY)
                        .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                        .setCullState(CULL_FRONT)
                        .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                        .createCompositeState(false)
        ));
    }
}