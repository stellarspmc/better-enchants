package net.enchantoutline.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class GlintOutlineConfig { // NOT 100% parity!
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enables / Disables the mod")
            .define("modEnabled", true);

    public static final ModConfigSpec.DoubleValue OUTLINE_SIZE = BUILDER
            .comment("The size of the outline")
            .defineInRange("outlineSize", 0.025, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> OUTLINE_COLOR = BUILDER
            .comment("The size of the outline")
            .defineListAllowEmpty("color", List.of(216.75, 178.5, 63.75, 255d), () -> 0d, GlintOutlineConfig::validateColor);

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST_ITEM = BUILDER
            .comment("idk")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", GlintOutlineConfig::validateItemName);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    private static boolean validateColor(final Object obj) {
        return obj instanceof Double d && d >= 0 && d <= 255;
    }
}