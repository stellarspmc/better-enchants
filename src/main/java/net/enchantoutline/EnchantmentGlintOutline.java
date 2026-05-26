package net.enchantoutline;

import net.enchantoutline.config.EnchantmentOutlineConfig;
import net.enchantoutline.config.ItemOverride;
import net.enchantoutline.mixin.ItemRendererMixin;
import net.enchantoutline.shader.Shaders;
import net.enchantoutline.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(EnchantmentGlintOutline.MOD_ID)
public class EnchantmentGlintOutline {
    public static final String MOD_ID = "enchantment-glint-outline";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // ThreadLocal to suppress ModelPart callbacks during our custom outline renders
    public static final ThreadLocal<Boolean> skipModelPartCallback = ThreadLocal.withInitial(() -> false);

    private static EnchantmentOutlineConfig config;

    public static final CustomRenderLayers GLINT_LAYERS = new CustomRenderLayers();
    public static final CustomRenderLayers COLOR_LAYERS = new CustomRenderLayers();
    public static final CustomRenderLayers ZFIX_LAYERS = new CustomRenderLayers();

    public EnchantmentGlintOutline(IEventBus modEventBus) {
        loadConfig();
    }

    public static EnchantmentOutlineConfig getConfig() {
        return config;
    }

    /**
     * Helper to get item override based on the CURRENT rendering context
     * instead of using LayerRenderState accessors.
     */
    @Nullable
    public static ItemOverride getActiveOverride() {
        ItemStack stack = ItemRendererMixin.enchantOutline$getCurrentlyRenderingStack();
        if (stack == null) return null;

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null) return null;

        return config.getItemOverride(itemId.toString());
    }

    private static void loadConfig() {
        Path configFile = EnchantmentOutlineConfig.CONFIG_FILE;
        if (Files.exists(configFile)) {
            try (var reader = Files.newBufferedReader(configFile)) {
                config = EnchantmentOutlineConfig.fromJson(reader);
            } catch (Exception e) {
                LOGGER.error("Error loading config, using defaults.", e);
                config = new EnchantmentOutlineConfig();
            }
        } else {
            config = new EnchantmentOutlineConfig();
        }
        config.saveAsync();
    }
}