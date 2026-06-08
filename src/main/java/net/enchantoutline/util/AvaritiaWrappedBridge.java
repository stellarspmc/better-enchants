package net.enchantoutline.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public interface AvaritiaWrappedBridge {
    void enchant_outline$bridgeRenderWrapped(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, boolean allowRender);
}
