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

    // enabled cate
    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .translation("enchant_outline.configuration.enabled.desc")
            .define("enabled", true);

    public static final ModConfigSpec.BooleanValue ITEMS_ENABLED = BUILDER
            .translation("enchant_outline.configuration.itemEnabled.desc")
            .define("itemEnabled", true);

    public static final ModConfigSpec.BooleanValue ARMOR_ENABLED = BUILDER
            .translation("enchant_outline.configuration.armorEnabled.desc")
            .define("armorEnabled", true);

    public static final ModConfigSpec.BooleanValue ELYTRA_ENABLED = BUILDER
            .translation("enchant_outline.configuration.elytraEnabled.desc")
            .define("elytraEnabled", true);

    public static final ModConfigSpec.BooleanValue TRIDENT_ENABLED = BUILDER
            .translation("enchant_outline.configuration.thrownTridentEnabled.desc")
            .define("thrownTridentEnabled", true);

    public static final ModConfigSpec.BooleanValue BE_ENABLED = BUILDER
            .translation("enchant_outline.configuration.blockEntitiesEnabled.desc")
            .define("blockEntitiesEnabled", true);


    // customize cate
    public static final ModConfigSpec.DoubleValue OUTLINE_SIZE = BUILDER
            .translation("enchant_outline.configuration.outlineSize.desc")
            .defineInRange("outlineSize", 0.025, 0, 0.5);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> OUTLINE_COLOR = BUILDER
            .translation("enchant_outline.configuration.color.desc")
            .defineList(List.of("color"), () -> List.of(216.75, 178.5, 63.75, 255.0), null, GlintOutlineConfig::validateColor, ModConfigSpec.Range.of(4, 4));

    public static final ModConfigSpec.BooleanValue ENABLED_IN_INVENTORY = BUILDER
            .translation("enchant_outline.configuration.inventory.desc")
            .define("inventory", false);

    // compat cate
    public static final ModConfigSpec.BooleanValue ALL_ENCHANTED_GEAR = BUILDER
            .translation("enchant_outline.configuration.allEnchantedGear.desc")
            .define("allEnchantedGear", false);

    public static final ModConfigSpec.BooleanValue SILENT_GEAR = BUILDER
            .translation("enchant_outline.configuration.silentGear.desc")
            .define("silentGear", false);

    // inv cate
    static final ModConfigSpec.ConfigValue<List<? extends String>> CONFIG_BLACKLIST = BUILDER
            .translation("enchant_outline.configuration.blacklistItems.desc")
            .defineListAllowEmpty("blacklistItems", List.of("minecraft:barrier"), () -> "", GlintOutlineConfig::validateItemName);


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