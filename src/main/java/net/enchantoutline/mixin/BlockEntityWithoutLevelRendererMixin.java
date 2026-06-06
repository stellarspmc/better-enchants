package net.enchantoutline.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.enchantoutline.GlintOutline;
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

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin {

    @Shadow private ShieldModel shieldModel;
    @Shadow private TridentModel tridentModel;

    @Inject(method = "renderByItem", at = @At("HEAD"))
    private void better_enchants$renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource bufferSource, int a, int b, CallbackInfo ci) {
        if (ctx == ItemDisplayContext.GUI) return;
        if (!stack.hasFoil()) return;

        VertexConsumer consumer = null;
        ModelPart rootPart = null;

        if (stack.is(Items.SHIELD)) {
            consumer = bufferSource.getBuffer(Shaders.getTest());
            rootPart = this.shieldModel.root;
        } else if (stack.is(Items.TRIDENT)) {
            consumer = bufferSource.getBuffer(Shaders.getTest());
            rootPart = this.tridentModel.root;
        }

        if (consumer != null) {
            GlintOutline.IS_RENDERING_OUTLINE.set(true);
            rootPart.render(poseStack, consumer, a, b);
            GlintOutline.IS_RENDERING_OUTLINE.remove();
        }
    }

}
