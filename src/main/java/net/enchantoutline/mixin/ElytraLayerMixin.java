package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.GlintOutline;
import net.enchantoutline.config.GlintOutlineConfig;
import net.enchantoutline.util.MixinHelper;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin {

    @Shadow
    public ResourceLocation getElytraTexture(ItemStack stack, LivingEntity entity) {
        return null;
    }

    @Shadow @Final private ElytraModel<LivingEntity> elytraModel;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At("TAIL"))
    public void enchant_outline$addElytraArmorOutlinePass(PoseStack poseStack, MultiBufferSource bufferSource, int light, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(GlintOutlineConfig.ENABLED.get() && GlintOutlineConfig.ELYTRA_ENABLED.get())) return;
        ItemStack elytra = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (!elytra.is(Items.ELYTRA)) return; // this is another hardcode, have to get if the item extends ElytraItem
        ResourceLocation texture = getElytraTexture(elytra, entity);
        if (MixinHelper.invertedCheckFoilOrEnchanted(elytra)) return;
        if (entity instanceof AbstractClientPlayer player) {
            PlayerSkin skin = player.getSkin();
            if (skin.elytraTexture() != null) texture = skin.elytraTexture();
            else if (skin.capeTexture() != null && player.isModelPartShown(PlayerModelPart.CAPE)) texture = skin.capeTexture();
        }

        poseStack.pushPose();
        poseStack.translate(0f, 0f, 0.1f); // probably works although hardcoded in
        GlintOutline.IS_RENDERING_OUTLINE.set(true);
        VertexConsumer outlineConsumer = bufferSource.getBuffer(Shaders.getArmorOutlineLayer(texture));
        elytraModel.renderToBuffer(poseStack, outlineConsumer, light, OverlayTexture.NO_OVERLAY);
        GlintOutline.IS_RENDERING_OUTLINE.remove();
        poseStack.popPose();
    }

}
