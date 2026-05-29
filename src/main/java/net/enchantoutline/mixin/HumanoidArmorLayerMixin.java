package net.enchantoutline.mixin;

import net.enchantoutline.Shaders;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.ClientHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    @Shadow
    private boolean usesInnerModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS;
    }

    @Inject(method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V", at = @At("TAIL"))
    private void addArmorOutlinePass(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<?> model, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        ItemStack armorStack = entity.getItemBySlot(slot);
        if (armorStack.isEmpty() || !armorStack.hasFoil() || armorStack.is(Items.ELYTRA)) return;
        if (armorStack.getItem() instanceof ArmorItem armoritem) {
            for (int layerIdx = 0; layerIdx < armoritem.getMaterial().value().layers().size(); layerIdx++) {
                ResourceLocation test = ClientHooks.getArmorTexture(entity, armorStack, armoritem.getMaterial().value().layers().get(layerIdx), usesInnerModel(slot), slot);
                //VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.getEntityOutlineLayer(test));
                //poseStack.scale(1.1f, 1.1f, 1.1f);
                //model.renderToBuffer(poseStack, outlineBuffer, light, OverlayTexture.NO_OVERLAY);

                VertexConsumer outlineBuffer = bufferSource.getBuffer(Shaders.test(test));
                var thickenedModel = ModelHelper.getThickenedModel(model, 0.05f);
                thickenedModel.renderToBuffer(poseStack, outlineBuffer, light, OverlayTexture.NO_OVERLAY, 0xFFFF0000);
            }
        }
    }

}
