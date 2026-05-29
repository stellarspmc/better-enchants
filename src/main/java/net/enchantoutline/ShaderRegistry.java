package net.enchantoutline;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

@EventBusSubscriber(modid = GlintOutline.MOD_ID)
public class ShaderRegistry {

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), location("item"), DefaultVertexFormat.BLOCK), shaderInstance -> Shaders.itemShaderInstance = shaderInstance);
            event.registerShader(new ShaderInstance(event.getResourceProvider(), location("entity"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> Shaders.entityShaderInstance = shaderInstance);
        } catch (Exception e) {
            GlintOutline.LOGGER.error("Failed to load shader(s)!", e);
        }
    }

    private static ResourceLocation location(String id) {
        return ResourceLocation.fromNamespaceAndPath(GlintOutline.MOD_ID, id);
    }
}