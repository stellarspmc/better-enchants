package net.enchantoutline.events.subscriber;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.EnchantmentGlintOutline;
import net.enchantoutline.config.ItemOverride;
import net.enchantoutline.events.EquipmentRendererEnchantedEvent;
import net.enchantoutline.events.TridentEntityRendererEnchantedEvent;
import net.enchantoutline.shader.Shaders;
import net.enchantoutline.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = EnchantmentGlintOutline.MOD_ID)
public class EnchantmentGlintOutlineEventSubscriber {

    @SubscribeEvent
    public static void onArmorRender(EquipmentRendererEnchantedEvent<?> event) {
        var config = EnchantmentGlintOutline.getConfig();
        if (!config.isEnabled()) return;

        @Nullable ItemOverride override = (event.getRenderedStack() != null)
                ? config.getArmorOverride(event.getRenderedStack().getItem().toString())
                : null;

        if (override == null && !config.shouldRenderArmor() || (override != null && !override.shouldRender())) return;

        //event.getModel().setupAnim(event.getEntityState());

        float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));
        RenderType baseArmorLayer = RenderType.armorCutoutNoCull(event.getTexture());

        if (config.getRenderSolidOverrideOrDefault(override, true)) {
            int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

            RenderType colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(
                    baseArmorLayer,
                    EnchantmentGlintOutline.COLOR_LAYERS,
                    Shaders::createColorRenderLayerNoCull,
                    Shaders::createColorRenderLayerCull,
                    Shaders.COLOR_CUTOUT_LAYER,
                    true
            );

            VertexConsumer buffer = event.getBufferSource().getBuffer(colorLayer);

            // Draw via the thickened vertex wrapper
            ModelHelper.renderThickened(event.getModel(), event.getPoseStack(), buffer, Integer.MAX_VALUE, event.getOverlay(), tint, scale);
        } else {
            RenderType glintLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(
                    baseArmorLayer,
                    EnchantmentGlintOutline.GLINT_LAYERS,
                    Shaders::createGlintRenderLayerNoCull,
                    Shaders::createGlintRenderLayerCull,
                    Shaders.GLINT_CUTOUT_LAYER,
                    true
            );

            VertexConsumer glintBuffer = event.getBufferSource().getBuffer(glintLayer);

            // Draw via the thickened vertex wrapper
            ModelHelper.renderThickened(event.getModel(), event.getPoseStack(), glintBuffer, event.getLight(), event.getOverlay(), event.getTintColor(), scale);
        }
    }

    @SubscribeEvent
    public static void onTridentRender(TridentEntityRendererEnchantedEvent<ThrownTrident> event) {
        var config = EnchantmentGlintOutline.getConfig();
        if (!config.isEnabled()) return;

        // Check if the current render layer is an entity glint layer
        // 1.21.1 equivalent for RenderTypes.entityGlint() is usually RenderType.entityGlint()
        if (event.getRenderLayer().equals(RenderType.entityGlint())) {
            @Nullable ItemOverride override = config.getItemOverride(net.minecraft.world.item.Items.TRIDENT);
            if (override == null || override.shouldRender()) {

                float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));

                // Get the base texture location from the renderer's static field
                // ThrownTridentRenderer.TRIDENT_LOCATION is the standard location
                RenderType garbageHackPatchLayer = event.getModel().renderType(net.minecraft.client.renderer.entity.ThrownTridentRenderer.TRIDENT_LOCATION);

                if (config.getRenderSolidOverrideOrDefault(override, false)) {
                    int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

                    RenderType colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(
                            garbageHackPatchLayer,
                            EnchantmentGlintOutline.COLOR_LAYERS,
                            Shaders::createColorRenderLayerNoCull,
                            Shaders::createColorRenderLayerCull,
                            Shaders.COLOR_CUTOUT_LAYER,
                            false
                    );

                    VertexConsumer buffer = event.getBufferSource().getBuffer(colorLayer);

                    // Pure immediate mode drawing via our stream interceptor
                    ModelHelper.renderThickened(event.getModel(), event.getPoseStack(), buffer, Integer.MAX_VALUE, 0, tint, scale);
                } else {
                    RenderType glintZLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(
                            garbageHackPatchLayer,
                            EnchantmentGlintOutline.GLINT_LAYERS,
                            Shaders::createGlintRenderLayerNoCull,
                            Shaders::createGlintRenderLayerCull,
                            Shaders.GLINT_CUTOUT_LAYER,
                            false
                    );

                    VertexConsumer glintBuffer = event.getBufferSource().getBuffer(glintZLayer);
                    VertexConsumer nativeGlintBuffer = event.getBufferSource().getBuffer(event.getRenderLayer());

                    // Draw the custom glint outline pass
                    ModelHelper.renderThickened(event.getModel(), event.getPoseStack(), glintBuffer, Integer.MAX_VALUE, 0, 0xFFFFFFFF, scale);

                    // Draw the native glint overlay pass
                    ModelHelper.renderThickened(event.getModel(), event.getPoseStack(), nativeGlintBuffer, Integer.MAX_VALUE, 0, 0xFFFFFFFF, scale);
                }
            }
        }
    }
}