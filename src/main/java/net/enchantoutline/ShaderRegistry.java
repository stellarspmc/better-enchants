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
            ResourceLocation shaderLocation = ResourceLocation.fromNamespaceAndPath(EnchantmentGlintOutline.MOD_ID, "outline");
            event.registerShader(new ShaderInstance(event.getResourceProvider(), shaderLocation, DefaultVertexFormat.BLOCK), shaderInstance -> Shaders.outlineShaderInstance = shaderInstance);
        } catch (Exception e) {
            EnchantmentGlintOutline.LOGGER.error("Failed to load outline shader!", e);
        }
    }
}