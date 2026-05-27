package net.enchantoutline;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

@EventBusSubscriber(modid = EnchantmentGlintOutline.MOD_ID)
public class ShaderRegistry {

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), location("item"), DefaultVertexFormat.BLOCK), shaderInstance -> Shaders.itemShaderInstance = shaderInstance);
            event.registerShader(new ShaderInstance(event.getResourceProvider(), location("armor"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> Shaders.armorShaderInstance = shaderInstance);
        } catch (Exception e) {
            EnchantmentGlintOutline.LOGGER.error("Failed to load shader(s)!", e);
        }
    }

    private static ResourceLocation location(String id) {
        return ResourceLocation.fromNamespaceAndPath(EnchantmentGlintOutline.MOD_ID, id);
    }
}