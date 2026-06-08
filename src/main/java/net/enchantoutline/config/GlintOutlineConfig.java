package net.enchantoutline.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GlintOutlineConfig { // NOT 100% parity!
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue OUTLINE_SIZE = BUILDER
            .comment("Adjust the thickness of the enchanted outline overlay.")
            .defineInRange("outlineSize", 0.025, 0, 1000);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> OUTLINE_COLOR = BUILDER
            .comment("Choose the RGBA for the enchanted outline overlay.")
            .defineList(List.of("color"), () -> List.of(216.75, 178.5, 63.75, 255.0), null, GlintOutlineConfig::validateColor, ModConfigSpec.Range.of(4, 4));

    static final ModConfigSpec.ConfigValue<List<? extends String>> CONFIG_BLACKLIST = BUILDER
            .comment("A list of item IDs (e.g., 'minecraft:barrier') that should not receive the glow outline effect.")
            .defineListAllowEmpty("blacklistItems", List.of("minecraft:barrier"), () -> "", GlintOutlineConfig::validateItemName);

    public static final ModConfigSpec.BooleanValue ALL_ENCHANTED_GEAR = BUILDER
            .comment("Bypasses standard vanilla isFoil flags. Turn on to fix Silent Gear tool displays!")
            .define("allEnchantedGear", false);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    private static boolean validateColor(final Object obj) {
        return obj instanceof Double d && d >= 0 && d <= 255;
    }

    public static Set<Item> BLACKLISTED_ITEMS = new HashSet<>();

    public static void updateBlacklistCache() {
        List<? extends String> configList = GlintOutlineConfig.CONFIG_BLACKLIST.get();
        BLACKLISTED_ITEMS = configList.stream()
                .map(s -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(s)))
                .filter(item -> item != Items.AIR)
                .collect(Collectors.toSet());
    }
}