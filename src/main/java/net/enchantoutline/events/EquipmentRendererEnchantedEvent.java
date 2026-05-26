package net.enchantoutline.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

public class EquipmentRendererEnchantedEvent<S> extends Event implements ICancellableEvent {
    private final ItemStack renderedStack;
    private final MultiBufferSource bufferSource;
    private final ResourceLocation texture;
    private final Model model;
    private final S entityState; // Equivalent to 's'
    private final PoseStack poseStack;
    private final RenderType renderLayer;
    private final int light;
    private final int overlay;
    private final int tintColor;
    @Nullable
    private final TextureAtlasSprite sprite;
    private final int outlineColor;

    public EquipmentRendererEnchantedEvent(ItemStack renderedStack, MultiBufferSource bufferSource, ResourceLocation texture, Model model, S entityState, PoseStack poseStack, RenderType renderLayer, int light, int overlay, int tintColor, @Nullable TextureAtlasSprite sprite, int outlineColor) {
        this.renderedStack = renderedStack;
        this.bufferSource = bufferSource;
        this.texture = texture;
        this.model = model;
        this.entityState = entityState;
        this.poseStack = poseStack;
        this.renderLayer = renderLayer;
        this.light = light;
        this.overlay = overlay;
        this.tintColor = tintColor;
        this.sprite = sprite;
        this.outlineColor = outlineColor;
    }

    public ItemStack getRenderedStack() { return renderedStack; }
    public MultiBufferSource getBufferSource() { return bufferSource; }
    public ResourceLocation getTexture() { return texture; }
    public Model getModel() { return model; }
    public S getEntityState() { return entityState; }
    public PoseStack getPoseStack() { return poseStack; }
    public RenderType getRenderLayer() { return renderLayer; }
    public int getLight() { return light; }
    public int getOverlay() { return overlay; }
    public int getTintColor() { return tintColor; }
    @Nullable public TextureAtlasSprite getSprite() { return sprite; }
    public int getOutlineColor() { return outlineColor; }
}