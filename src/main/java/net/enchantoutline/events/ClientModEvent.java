package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.enchantoutline.EnchantmentGlintOutline;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = EnchantmentGlintOutline.MOD_ID, value = Dist.CLIENT)
public class ClientModEvent {

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            ResourceLocation shaderLocation = ResourceLocation.fromNamespaceAndPath(EnchantmentGlintOutline.MOD_ID, "outline");
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), shaderLocation, DefaultVertexFormat.BLOCK),
                    shaderInstance -> {
                        Shaders.outlineShaderInstance = shaderInstance;
                        EnchantmentGlintOutline.LOGGER.info("Shader successfully loaded: {}", Shaders.outlineShaderInstance != null);
                    }
            );
        } catch (Exception e) {
            EnchantmentGlintOutline.LOGGER.error("Failed to load outline shader!", e);
        }
    }
}