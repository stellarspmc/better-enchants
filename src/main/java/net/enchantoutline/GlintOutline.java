package net.enchantoutline;

import net.neoforged.fml.common.Mod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GlintOutline.MOD_ID)
public class GlintOutline {
    public static final String MOD_ID = "enchant_outline";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ThreadLocal<Boolean> IS_RENDERING_OUTLINE = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> IS_ARMOR = ThreadLocal.withInitial(() -> false);
}