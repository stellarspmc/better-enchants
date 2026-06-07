package net.enchantoutline;

import net.neoforged.fml.common.Mod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(GlintOutline.MOD_ID)
public class GlintOutline {
    public static final String MOD_ID = "enchant_outline";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ThreadLocal<Boolean> IS_RENDERING_OUTLINE = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> IS_RENDERING_LEGGINGS = ThreadLocal.withInitial(() -> false); // custom fix for leggings

    public static final float SCALE = 0.025f;
    public static final float[] OUTLINE_COLOR = {.85f, .7f, .25f, 1f};
}