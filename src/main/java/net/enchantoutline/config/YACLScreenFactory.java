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
                .title(Component.literal("Glint Outline Settings"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("General Settings"))

                        .option(Option.<Double>createBuilder()
                                .name(Component.literal("Outline Size"))
                                .description(OptionDescription.of(Component.literal("Adjust the thickness of the enchanted outline overlay.")))
                                .binding(
                                        GlintOutlineConfig.OUTLINE_SIZE.getDefault(),
                                        GlintOutlineConfig.OUTLINE_SIZE,
                                        GlintOutlineConfig.OUTLINE_SIZE::set
                                )
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.0, 0.5).step(0.005))
                                .build())

                        .option(Option.<Color>createBuilder()
                                .name(Component.literal("Outline Color"))
                                .description(OptionDescription.of(Component.literal("Choose the RGBA for the enchanted outline overlay.")))
                                .binding(
                                        new Color(216, 178, 63, 255),
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

                        .option(ListOption.<String>createBuilder()
                                .name(Component.literal("Blacklisted Items"))
                                .description(OptionDescription.of(Component.literal("A list of item IDs (e.g., 'minecraft:barrier') that should not receive the glow outline effect.")))
                                .binding(
                                        List.of("minecraft:barrier"),
                                        () -> GlintOutlineConfig.CONFIG_BLACKLIST.get().stream()
                                                .map(String::toString)
                                                .toList(),
                                        GlintOutlineConfig.CONFIG_BLACKLIST::set
                                )
                                .controller(StringControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Component.literal("Use isEnchanted() Checks"))
                                .description(OptionDescription.of(Component.literal("Bypasses standard vanilla isFoil flags. Turn on to fix Silent Gear tool displays!")))
                                .binding(
                                        GlintOutlineConfig.ALL_ENCHANTED_GEAR.getDefault(),
                                        GlintOutlineConfig.ALL_ENCHANTED_GEAR,
                                        GlintOutlineConfig.ALL_ENCHANTED_GEAR::set
                                )
                                .controller(TickBoxControllerBuilder::create)
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
