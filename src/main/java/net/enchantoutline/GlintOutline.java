package net.enchantoutline;

import net.enchantoutline.config.GlintOutlineConfig;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GlintOutline.MOD_ID)
public class GlintOutline {
    public static final String MOD_ID = "enchant_outline";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ThreadLocal<Boolean> IS_RENDERING_OUTLINE = ThreadLocal.withInitial(() -> false);

    public GlintOutline(ModContainer modContainer, IEventBus modBusEvent) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, GlintOutlineConfig.SPEC);
        modBusEvent.addListener(GlintOutline::onConfigReload);
    }

    private static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(MOD_ID)) GlintOutlineConfig.updateBlacklistCache();
    }
}