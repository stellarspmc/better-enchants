package net.enchantoutline.config.yacl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import net.enchantoutline.EnchantmentGlintOutlineFabricOld;
import net.enchantoutline.config.EnchantmentOutlineConfig;
import net.enchantoutline.config.ItemOverrideContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class YACLScreenFactory {
    private static final EnchantmentOutlineConfig defaultConfig = new EnchantmentOutlineConfig();
    public static Screen makeConfig(Screen parent)
    {
        YetAnotherConfigLib configScreen = YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("title.enchantoutline.config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("category.enchantoutline.main"))
                        .tooltip(Component.translatable("tooltip.category.enchantoutline.main"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Component.translatable("option.enchantoutline.rendermain"))
                                .description(OptionDescription.of(Component.translatable("tooltip.option.enchantoutline.rendermain")))
                                .binding(defaultConfig.isEnabled(), EnchantmentGlintOutlineFabricOld.getConfig()::isEnabled, EnchantmentGlintOutlineFabricOld.getConfig()::setEnabled)
                                .controller(BooleanControllerBuilderImpl::new)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Component.translatable("option.enchantoutline.solidcolor"))
                                .description(OptionDescription.of(Component.translatable("tooltip.option.enchantoutline.solidcolor").append(Component.translatable("tooltip.error.yamlcolorissue").withStyle(ChatFormatting.YELLOW))))
                                .binding(EnchantmentOutlineConfig.getColorFromInt(defaultConfig.getOutlineColor()), () -> EnchantmentOutlineConfig.getColorFromInt(EnchantmentGlintOutlineFabricOld.getConfig().getOutlineColor()), (color) -> {
                                    EnchantmentGlintOutlineFabricOld.getConfig().setBaseSolidOutlineColor(EnchantmentOutlineConfig.getIntFromColor(color));})
                                .controller(opt -> ColorControllerBuilder.create(opt)
                                        .allowAlpha(true))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("group.enchantoutline.compatibility"))
                                .description(OptionDescription.of(Component.translatable("tooltip.category.enchantoutline.compatibility")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("option.compatibility.enchantoutline.skippass"))
                                        .description(OptionDescription.of(Component.translatable("tooltip.option.compatibility.enchantoutline.skippass1").withStyle(ChatFormatting.YELLOW)
                                                .append(Component.translatable("tooltip.option.compatibility.enchantoutline.skippass2").withStyle(ChatFormatting.RED))
                                                .append(Component.translatable("tooltip.option.compatibility.enchantoutline.skippass3").withStyle(ChatFormatting.WHITE))))
                                        .binding(defaultConfig.shouldRemoveRenderPass(), EnchantmentGlintOutlineFabricOld.getConfig()::shouldRemoveRenderPass, EnchantmentGlintOutlineFabricOld.getConfig()::setRemoveRenderPass)
                                        .controller(BooleanControllerBuilderImpl::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("group.enchantoutline.item"))
                                .description(OptionDescription.of(Component.translatable("tooltip.category.enchantoutline.item")))
                                .option(Option.<Float>createBuilder()
                                        .name(Component.translatable("option.item.enchantoutline.size"))
                                        .description(OptionDescription.of(Component.translatable("tooltip.option.item.enchantoutline.size")))
                                        .binding(defaultConfig.getOutlineSize(), EnchantmentGlintOutlineFabricOld.getConfig()::getOutlineSize, EnchantmentGlintOutlineFabricOld.getConfig()::setOutlineSize)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(0f, EnchantmentOutlineConfig.MAX_OUTLINE_SIZE)
                                                .step(1f)
                                                .formatValue(new FloatValueFormatter(0)))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("option.item.enchantoutline.rendersolid"))
                                        .description(OptionDescription.of(Component.translatable("tooltip.option.item.enchantoutline.rendersolid")))
                                        .binding(defaultConfig.shouldRenderSolid(), EnchantmentGlintOutlineFabricOld.getConfig()::shouldRenderSolid, EnchantmentGlintOutlineFabricOld.getConfig()::setRenderSolid)
                                        .controller(BooleanControllerBuilderImpl::new)
                                        .build())
                                .build())
                        .group(ListOption.<ItemOverrideContainer>createBuilder()
                                .name(Component.translatable("list.enchantoutline.override.item"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("tooltip.list.enchantoutline.override.item"))
                                        .webpImage(Identifier.fromNamespaceAndPath(EnchantmentGlintOutlineFabricOld.MOD_ID, "textures/listexplained.webp"))
                                        .build())
                                .binding(defaultConfig.getItemOverridesAsContainerList(), EnchantmentGlintOutlineFabricOld.getConfig()::getItemOverridesAsContainerList, EnchantmentGlintOutlineFabricOld.getConfig()::setItemOverridesFromContainerList)
                                .controller(ItemOverrideContainerControllerBuilderImpl::new)
                                .initial(ItemOverrideContainer::new)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("group.enchantoutline.equipment"))
                                .description(OptionDescription.of(Component.translatable("tooltip.category.enchantoutline.equipment")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("option.equipment.enchantoutline.render"))
                                        .description(OptionDescription.of(Component.translatable("tooltip.option.equipment.enchantoutline.render")))
                                        .binding(defaultConfig.shouldRenderArmor(), EnchantmentGlintOutlineFabricOld.getConfig()::shouldRenderArmor, EnchantmentGlintOutlineFabricOld.getConfig()::setRenderArmor)
                                        .controller(BooleanControllerBuilderImpl::new)
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Component.translatable("option.equipment.enchantoutline.size"))
                                        .description(OptionDescription.of(Component.translatable("tooltip.option.equipment.enchantoutline.size")))
                                        .binding(defaultConfig.getArmorOutlineSize(), EnchantmentGlintOutlineFabricOld.getConfig()::getArmorOutlineSize, EnchantmentGlintOutlineFabricOld.getConfig()::setArmorOutlineSize)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .range(0f, EnchantmentOutlineConfig.MAX_OUTLINE_SIZE)
                                                .step(1f)
                                                .formatValue(new FloatValueFormatter(0)))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("option.equipment.enchantoutline.rendersolid"))
                                        .description(OptionDescription.of(Component.translatable("tooltip.option.equipment.enchantoutline.rendersolid")))
                                        .binding(defaultConfig.shouldRenderArmorSolid(), EnchantmentGlintOutlineFabricOld.getConfig()::shouldRenderArmorSolid, EnchantmentGlintOutlineFabricOld.getConfig()::setRenderArmorSolid)
                                        .controller(BooleanControllerBuilderImpl::new)
                                        .build())
                                .build())
                        .group(ListOption.<ItemOverrideContainer>createBuilder()
                                .name(Component.translatable("list.enchantoutline.override.equipment"))
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("tooltip.list.enchantoutline.override.equipment"))
                                        .webpImage(Identifier.fromNamespaceAndPath(EnchantmentGlintOutlineFabricOld.MOD_ID, "textures/listexplained.webp"))
                                        .build())
                                .binding(defaultConfig.getArmorOverridesAsContainerList(), EnchantmentGlintOutlineFabricOld.getConfig()::getArmorOverridesAsContainerList, EnchantmentGlintOutlineFabricOld.getConfig()::setArmorOverridesFromContainerList)
                                .controller(ItemOverrideContainerControllerBuilderImpl::new)
                                .initial(ItemOverrideContainer::new)
                                .build())
                        .build())
                .save(EnchantmentGlintOutlineFabricOld.getConfig()::saveAsync)
                .build();

        return configScreen.generateScreen(parent);
    }
}
