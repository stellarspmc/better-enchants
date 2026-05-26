package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge 1.21.1 equivalent of TridentEntityRendererQueueEnchantedCallback.
 * Implements ICancellableEvent so you can cancel vanilla behavior by calling event.setCanceled(true).
 *
 * @param <S> The type of the entity/state used by the trident model (typically ThrownTrident).
 */
public class TridentEntityRendererEnchantedEvent<S> extends Event implements ICancellableEvent {
    private final MultiBufferSource bufferSource;
    private final Model model;
    private final S entity;
    private final PoseStack poseStack;
    private final RenderType renderLayer;
    private final int light;
    private final int overlay;
    private final int tintColor;
    @Nullable
    private final TextureAtlasSprite sprite;
    private final int outlineColor;

    public TridentEntityRendererEnchantedEvent(MultiBufferSource bufferSource, Model model, S entity, PoseStack poseStack, RenderType renderLayer, int light, int overlay, int tintColor, @Nullable TextureAtlasSprite sprite, int outlineColor) {
        this.bufferSource = bufferSource;
        this.model = model;
        this.entity = entity;
        this.poseStack = poseStack;
        this.renderLayer = renderLayer;
        this.light = light;
        this.overlay = overlay;
        this.tintColor = tintColor;
        this.sprite = sprite;
        this.outlineColor = outlineColor;
    }

    public MultiBufferSource getBufferSource() { return bufferSource; }
    public Model getModel() { return model; }
    public S getEntity() { return entity; }
    public PoseStack getPoseStack() { return poseStack; }
    public RenderType getRenderLayer() { return renderLayer; }
    public int getLight() { return light; }
    public int getOverlay() { return overlay; }
    public int getTintColor() { return tintColor; }
    @Nullable public TextureAtlasSprite getSprite() { return sprite; }
    public int getOutlineColor() { return outlineColor; }
}