package net.enchantoutline.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.enchantoutline.events.EquipmentRendererEnchantedEvent;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HumanoidArmorLayer.class)
public class EquipmentRendererMixin {

    /**
     * Intercepts immediate armor rendering in NeoForge 1.21.1.
     * Explicit method descriptor targets the 12-argument version of renderArmorPiece.
     */
    @SuppressWarnings("rawtypes")
    @WrapOperation(
            method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/layers/HumanoidArmorLayer;renderModel(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/model/Model;ILnet/minecraft/resources/ResourceLocation;)V"
            )
    )
    private void enchantOutline$renderArmorEnchanted(
            HumanoidArmorLayer instance,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Model model,
            int tintColor,
            ResourceLocation textureLocation,
            Operation<Void> original,
            // Method arguments from the outer renderArmorPiece parameters
            PoseStack p_117119_,
            MultiBufferSource p_117120_,
            LivingEntity entity,
            EquipmentSlot slot
    ) {
        // 1. Resolve item stack context from slot
        ItemStack armorStack = entity.getItemBySlot(slot);

        // 2. Resolve default RenderType context fallback layer
        RenderType renderLayer = RenderType.armorCutoutNoCull(textureLocation);

        // 3. Fire your generic 1.21.1 Equipment event matching your new class structure
        EquipmentRendererEnchantedEvent<LivingEntity> event = new EquipmentRendererEnchantedEvent<>(
                armorStack, bufferSource, textureLocation, model, entity, poseStack,
                renderLayer, packedLight, OverlayTexture.NO_OVERLAY, tintColor, null, 0
        );

        NeoForge.EVENT_BUS.post(event);

        // 4. Bypasses normal behavior if canceled, otherwise pass logic pipeline downstream safely
        if (!event.isCanceled()) {
            original.call(instance, poseStack, bufferSource, packedLight, model, tintColor, textureLocation);
        }
    }
}