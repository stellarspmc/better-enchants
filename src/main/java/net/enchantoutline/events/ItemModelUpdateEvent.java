package net.enchantoutline.events;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * NeoForge 1.21.1 equivalent of ItemModelManagerUpdateModelCallback.
 * Fired when an item's model is being fetched/updated based on its current context
 * (such as who is holding it and what world they are in).
 */
public class ItemModelUpdateEvent extends Event {
    private final ItemStack itemStack;
    private BakedModel model;
    @Nullable private final ClientLevel level;
    @Nullable private final LivingEntity entity;
    private final int seed;

    public ItemModelUpdateEvent(ItemStack itemStack, BakedModel model, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        this.itemStack = itemStack;
        this.model = model;
        this.level = level;
        this.entity = entity;
        this.seed = seed;
    }

    public ItemStack getItemStack() { return itemStack; }
    @Nullable public ClientLevel getLevel() { return level; }
    @Nullable public LivingEntity getEntity() { return entity; }
    public int getSeed() { return seed; }

    /**
     * Gets the current resolved model.
     */
    public BakedModel getModel() {
        return this.model;
    }

    /**
     * Allows listeners to substitute or overwrite the model completely during the update loop.
     */
    public void setModel(BakedModel model) {
        this.model = model;
    }
}