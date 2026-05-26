package net.enchantoutline.events.subscriber;

import net.enchantoutline.EnchantmentGlintOutline;
import net.enchantoutline.config.ItemOverride;
import net.enchantoutline.events.EquipmentRendererEnchantedEvent;
import net.enchantoutline.shader.Shaders;
import net.enchantoutline.util.*;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = EnchantmentGlintOutline.MOD_ID)
public class EnchantmentGlintOutlineEventSubscriber {

    @SubscribeEvent
    public static void onArmorRender(EquipmentRendererEnchantedEvent<?> event) {
        var config = EnchantmentGlintOutline.getConfig();
        if (!config.isEnabled()) return;

        ItemOverride override = (event.getRenderedStack() != null)
                ? config.getArmorOverride(event.getRenderedStack().getItem().toString())
                : null;

        if (override == null && !config.shouldRenderArmor() || (override != null && !override.shouldRender())) return;

        float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));

        // Use your ModelHelper to get the thickened version
        var thickModel = ModelHelper.getThickenedModel(event.getModel(), layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

        if (config.getRenderSolidOverrideOrDefault(override, true)) {
            int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));
            var colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(event.getRenderLayer(),
                    EnchantmentGlintOutline.COLOR_LAYERS, Shaders::createColorRenderLayerNoCull,
                    Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, true);

            // Direct draw
            thickModel.renderToBuffer(event.getPoseStack(), event.getBufferSource().getBuffer(colorLayer),
                    Integer.MAX_VALUE, event.getOverlay(), tint);
        } else {
            var glintLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(event.getRenderLayer(),
                    EnchantmentGlintOutline.GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull,
                    Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, true);

            // Direct draw
            thickModel.renderToBuffer(event.getPoseStack(), event.getBufferSource().getBuffer(glintLayer),
                    event.getLight(), event.getOverlay(), event.getTintColor());
        }
    }
}