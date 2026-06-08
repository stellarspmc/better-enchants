package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.GlintOutline;
import net.enchantoutline.config.GlintOutlineConfig;
import net.enchantoutline.util.MixinHelper;
import net.enchantoutline.util.Shaders;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Shadow private ShieldModel shieldModel;
    @Shadow private TridentModel tridentModel;

    @Inject(method = "renderByItem", at = @At("HEAD"))
    private void better_enchants$addBlockEntityOutlinePass(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay, CallbackInfo ci) {
        if (!(GlintOutlineConfig.ENABLED.get() && GlintOutlineConfig.BE_ENABLED.get())) return;
        if (MixinHelper.inventoryOutline(ctx)) return;
        if (MixinHelper.invertedCheckFoilOrEnchanted(stack)) return;
        if (GlintOutlineConfig.BLACKLISTED_ITEMS.contains(stack.getItem())) return;

        VertexConsumer consumer = null;
        ModelPart rootPart = null;

        if (stack.is(Items.SHIELD)) {
            consumer = bufferSource.getBuffer(Shaders.getModelOutlineLayer());
            rootPart = this.shieldModel.root;
            poseStack.pushPose();
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180f));
        } else if (stack.is(Items.TRIDENT)) {
            consumer = bufferSource.getBuffer(Shaders.getModelOutlineLayer());
            rootPart = this.tridentModel.root;
            poseStack.pushPose();
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180f));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180f));
        }

        LOGGER.info(stack.getDisplayName().getString());
        // TODO: cataclysm has the custom renderer here (class CMItemstackRenderer extends BlockEntityWithoutLevelRenderer), probably support here
        // TODO: artifact umbrella uses (class UmbrellaArmPoseHandler) which uses an event
        // `UmbrellaArmPoseHelper.setUmbrellaArmPose(event.getRenderer().getModel(), event.getEntity())`

        // TODO: tacz what the fuck (class AnimateGeoItemRenderer<M extends BedrockAnimatedModel, CTX extends ItemAnimationStateContext>
        //        extends BlockEntityWithoutLevelRenderer implements IFPGeoItemRenderer)

        if (consumer != null) {
            GlintOutline.IS_RENDERING_OUTLINE.set(true);
            rootPart.render(poseStack, consumer, light, overlay);
            GlintOutline.IS_RENDERING_OUTLINE.remove();
            poseStack.popPose();
        }
    }

}
