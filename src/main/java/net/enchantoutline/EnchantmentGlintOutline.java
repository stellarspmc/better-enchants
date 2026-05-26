package net.enchantoutline;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EnchantmentGlintOutline.MOD_ID)
public class EnchantmentGlintOutline {
    public static final String MOD_ID = "enchant_outline";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public EnchantmentGlintOutline(IEventBus modEventBus) {
    }
}