package net.enchantoutline.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.awt.Color;
import java.util.List;

public class YACLScreenFactory {

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("enchant_outline.configuration.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("enchant_outline.configuration.enabledCategory"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.enabled"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.enabled.desc")))
                                .binding(
                                        GlintOutlineConfig.ENABLED.getDefault(),
                                        GlintOutlineConfig.ENABLED,
                                        GlintOutlineConfig.ENABLED::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.itemEnabled"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.itemEnabled.desc")))
                                .binding(
                                        GlintOutlineConfig.ITEMS_ENABLED.getDefault(),
                                        GlintOutlineConfig.ITEMS_ENABLED,
                                        GlintOutlineConfig.ITEMS_ENABLED::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.armorEnabled"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.armorEnabled.desc")))
                                .binding(
                                        GlintOutlineConfig.ARMOR_ENABLED.getDefault(),
                                        GlintOutlineConfig.ARMOR_ENABLED,
                                        GlintOutlineConfig.ARMOR_ENABLED::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.blockEntitiesEnabled"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.blockEntitiesEnabled.desc")))
                                .binding(
                                        GlintOutlineConfig.BE_ENABLED.getDefault(),
                                        GlintOutlineConfig.BE_ENABLED,
                                        GlintOutlineConfig.BE_ENABLED::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.elytraEnabled"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.elytraEnabled.desc")))
                                .binding(
                                        GlintOutlineConfig.ELYTRA_ENABLED.getDefault(),
                                        GlintOutlineConfig.ELYTRA_ENABLED,
                                        GlintOutlineConfig.ELYTRA_ENABLED::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.thrownTridentEnabled"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.thrownTridentEnabled.desc")))
                                .binding(
                                        GlintOutlineConfig.TRIDENT_ENABLED.getDefault(),
                                        GlintOutlineConfig.TRIDENT_ENABLED,
                                        GlintOutlineConfig.TRIDENT_ENABLED::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("enchant_outline.configuration.customizationCategory"))

                        .option(Option.<Double>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.outlineSize"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.outlineSize.desc")))
                                .binding(
                                        GlintOutlineConfig.OUTLINE_SIZE.getDefault(),
                                        GlintOutlineConfig.OUTLINE_SIZE,
                                        GlintOutlineConfig.OUTLINE_SIZE::set
                                )
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.0, 0.25).step(0.005))
                                .build())

                        .option(Option.<Color>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.color"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.color.desc")))
                                .binding(
                                        new Color(
                                                GlintOutlineConfig.OUTLINE_COLOR.getDefault().getFirst().intValue(),
                                                GlintOutlineConfig.OUTLINE_COLOR.getDefault().get(1).intValue(),
                                                GlintOutlineConfig.OUTLINE_COLOR.getDefault().get(2).intValue(),
                                                GlintOutlineConfig.OUTLINE_COLOR.getDefault().get(3).intValue()
                                        ),
                                        () -> new Color(
                                                GlintOutlineConfig.OUTLINE_COLOR.get().getFirst().intValue(),
                                                GlintOutlineConfig.OUTLINE_COLOR.get().get(1).intValue(),
                                                GlintOutlineConfig.OUTLINE_COLOR.get().get(2).intValue(),
                                                GlintOutlineConfig.OUTLINE_COLOR.get().get(3).intValue()
                                        ),
                                        color -> GlintOutlineConfig.OUTLINE_COLOR.set(List.of((double) color.getRed(), (double) color.getGreen(), (double) color.getBlue(), (double) color.getAlpha()))
                                )
                                .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.inventory"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.inventory.desc")))
                                .binding(
                                        GlintOutlineConfig.ENABLED_IN_INVENTORY.getDefault(),
                                        GlintOutlineConfig.ENABLED_IN_INVENTORY,
                                        GlintOutlineConfig.ENABLED_IN_INVENTORY::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("enchant_outline.configuration.compatCategory"))

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.allEnchantedGear"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.allEnchantedGear.desc")))
                                .binding(
                                        GlintOutlineConfig.ALL_ENCHANTED_GEAR.getDefault(),
                                        GlintOutlineConfig.ALL_ENCHANTED_GEAR,
                                        GlintOutlineConfig.ALL_ENCHANTED_GEAR::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.silentGear"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.silentGear.desc")))
                                .binding(
                                        GlintOutlineConfig.SILENT_GEAR.getDefault(),
                                        GlintOutlineConfig.SILENT_GEAR,
                                        GlintOutlineConfig.SILENT_GEAR::set
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build())

                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("enchant_outline.configuration.itemCategory"))

                        .option(ListOption.<String>createBuilder()
                                .name(Component.translatable("enchant_outline.configuration.blacklistItems"))
                                .description(OptionDescription.of(Component.translatable("enchant_outline.configuration.blacklistItems.desc")))
                                .initial("")
                                .binding(
                                        List.of("minecraft:barrier"),
                                        () -> GlintOutlineConfig.CONFIG_BLACKLIST.get().stream()
                                                .map(String::toString)
                                                .toList(),
                                        GlintOutlineConfig.CONFIG_BLACKLIST::set
                                )
                                .controller(StringControllerBuilder::create)
                                .build())

                        .build())

                .save(() -> {
                    GlintOutlineConfig.SPEC.save();
                    GlintOutlineConfig.updateBlacklistCache();
                })
                .build()
                .generateScreen(parent);
    }
}
