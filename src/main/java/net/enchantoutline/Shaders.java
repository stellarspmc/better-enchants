package net.enchantoutline;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.inventory.InventoryMenu;
import org.lwjgl.opengl.GL11;

public class Shaders {

    public static ShaderInstance itemShaderInstance;
    public static ShaderInstance armorShaderInstance;
    private static RenderType itemLayer;
    private static RenderType armorLayer;

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
        if (itemLayer == null) {
            itemLayer = RenderType.create(
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
                            .setTextureState(new RenderStateShard.TextureStateShard(
                                    InventoryMenu.BLOCK_ATLAS, false, false))
                            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                            .setCullState(RenderStateShard.NO_CULL)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                            .createCompositeState(false)
            );
        }
        return itemLayer;
    }

    public static RenderType getArmorOutlineLayer() {
        if (armorLayer == null) {
            armorLayer = RenderType.create(
                    "armor_outline",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    1536,
                    false,
                    true,
                    RenderType.CompositeState.builder()
                            .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                                if (armorShaderInstance != null) return armorShaderInstance;
                                return GameRenderer.getRendertypeArmorCutoutNoCullShader();
                            }))
                            .setTextureState(new RenderStateShard.TextureStateShard(net.minecraft.resources.ResourceLocation.parse("minecraft:textures/misc/white.png"), false, false))
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setLightmapState(RenderStateShard.LIGHTMAP)
                            .setOverlayState(RenderStateShard.OVERLAY)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .setCullState(CULL_FRONT)
                            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                            .createCompositeState(false)
            );
        }
        return armorLayer;
    }
}