package net.enchantoutline.util;

import net.enchantoutline.config.GlintOutlineConfig;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MixinHelper {

    public static boolean invertedCheckFoilOrEnchanted(ItemStack stack) {
        return !stack.hasFoil() && (!GlintOutlineConfig.ALL_ENCHANTED_GEAR.get() || !stack.isEnchanted());
    }

    public static boolean inventoryOutline(ItemDisplayContext ctx) {
        return (ctx == ItemDisplayContext.GUI && !GlintOutlineConfig.ENABLED_IN_INVENTORY.get());
    }

}
