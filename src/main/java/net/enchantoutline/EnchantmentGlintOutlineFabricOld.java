package net.enchantoutline;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.enchantoutline.config.EnchantmentOutlineConfig;
import net.enchantoutline.config.ItemOverride;
import net.enchantoutline.mixin_accessors.*;
import net.enchantoutline.model.HijackedModel;
import net.enchantoutline.shader.Shaders;
import net.enchantoutline.util.CustomRenderLayers;
import net.enchantoutline.util.ModelHelper;
import net.enchantoutline.util.RenderLayerHelper;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class EnchantmentGlintOutlineFabricOld {


	private static RenderType getTargetEnchantGlintLayer() {
		// 1.21.1 equivalent of armorEntityGlint()
		return RenderType.armorEntityGlint();
	}

	private static RenderType getTargetEnchantColorLayer() {
		// 1.21.1 equivalent: takes the direct shield texture atlas ResourceLocation
		return RenderType.entitySolid(Sheets.SHIELD_SHEET);
	}

	private static RenderType getTargetEnchantZFixLayer() {
		// 1.21.1 equivalent
		return RenderType.waterMask();
	}

	public void onInitialize() {

		EquipmentRendererQueueEnchantedCallback.EVENT.register((( queueHolder, renderedStack, queue, texture, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
			//I can build this using the current renderLayer the model class is surprisingly simple. It just is made of a model part which I already am able to render an outline for. just build a new model every frame and we should be set
			if(config.isEnabled()){
				@Nullable ItemOverride override = null;
				if(renderedStack != null){
					override = getOverrideFromNullableItem(config::getArmorOverride, renderedStack.getItem());
				}
				if(override == null && config.shouldRenderArmor() || override != null && override.shouldRender()){
					model.setupAnim(s);

					float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));
					//should create it straight from the identifier but it's just not working, this does. hence the garbage
					RenderType garbageHackPatchLayer = RenderTypes.armorCutoutNoCull(texture);
					if(config.getRenderSolidOverrideOrDefault(override, true)){
						int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

						//armor is literally always double-sided, the equipment renderer forces it to use double-sided.
						RenderType colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, true); //RenderLayerHelper.renderLayerFromIdentifierDoubleSided(texture, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, true);

						HijackedModel thickColorModel = ModelHelper.getThickenedModel(model, layer -> Shaders.COLOR_CUTOUT_LAYER, scale);

						queueHolder.order(getColorBatchingQueue()).submitModel(thickColorModel, s, matrixStack, colorLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlayCommand);
					}
					else{
						RenderType glintZLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, true);

						HijackedModel thickGlintZModel = ModelHelper.getThickenedModel(model, layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

						queueHolder.order(getZFixBatchingQueue()).submitModel(thickGlintZModel, s, matrixStack, glintZLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
						queueHolder.order(getZFixBatchingQueue()+1).submitModel(thickGlintZModel, s, matrixStack, Shaders.ARMOR_ENTITY_GLINT_FIX, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand);
					}
				}
			}
			return InteractionResult.PASS;
		}));

		TridentEntityRendererQueueEnchantedCallback.EVENT.register(((queueHolder, queue, model, s, matrixStack, renderLayer, light, overlay, tintColor, sprite, outlineColor, crumblingOverlayCommand) -> {
			if(config.isEnabled()){
				if(renderLayer.equals(RenderTypes.entityGlint())){
					@Nullable ItemOverride override = getOverrideFromNullableItem(config::getItemOverride, Items.TRIDENT);
					if(override == null || override.shouldRender()){
						float scale = config.getScaleFactorFromOutlineSize(config.getOutlineSizeOverrideOrDefault(override, true));
						RenderType garbageHackPatchLayer = model.renderType(ThrownTridentRenderer.TRIDENT_LOCATION);
						if(config.getRenderSolidOverrideOrDefault(override, false)){
							int tint = config.getOutlineColorAsInt(config.getOutlineColorOverrideOrDefault(override));

							RenderType colorLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, COLOR_LAYERS, Shaders::createColorRenderLayerNoCull, Shaders::createColorRenderLayerCull, Shaders.COLOR_CUTOUT_LAYER, false);

							HijackedModel thickColorModel = ModelHelper.getThickenedModel(model, layer -> Shaders.COLOR_CUTOUT_LAYER, scale);

							queueHolder.submitModel(thickColorModel, s, matrixStack, colorLayer, Integer.MAX_VALUE, 0, tint, sprite, outlineColor, crumblingOverlayCommand);
						}
						else{
							RenderType glintZLayer = RenderLayerHelper.renderLayerFromRenderLayerDoubleSided(garbageHackPatchLayer, GLINT_LAYERS, Shaders::createGlintRenderLayerNoCull, Shaders::createGlintRenderLayerCull, Shaders.GLINT_CUTOUT_LAYER, false);

							HijackedModel thickGlintZModel = ModelHelper.getThickenedModel(model, layer -> Shaders.GLINT_CUTOUT_LAYER, scale);

							queueHolder.submitModel(thickGlintZModel, s, matrixStack, glintZLayer, Integer.MAX_VALUE, 0, 0, sprite, outlineColor, crumblingOverlayCommand);
							queueHolder.submitModel(thickGlintZModel, s, matrixStack, renderLayer, Integer.MAX_VALUE, 0, 0, sprite, outlineColor, crumblingOverlayCommand);
						}
					}
				}
			}
			return InteractionResult.PASS;
		}));

		//---------- End Render Calls ----------

		//---------- Render Layer Order ----------

		//solid ordering in LevelRenderer
		ImmediateRenderCurrentLayer.Before.EVENT.register((receiver, renderLayer) -> {

			if(renderLayer.equals(getTargetEnchantColorLayer())){
				for(var customLayer : COLOR_LAYERS.renderLayers())
				{
					if(((RenderLayerAccessor)customLayer).enchantOutline$shouldUseLayerBuffer()) {
						receiver.endBatch(customLayer);
					}
				}
			}
			return InteractionResult.PASS;
		});

		//glint ordering in world renderer
		ImmediateRenderCurrentLayer.Before.EVENT.register((receiver, renderLayer) -> {

			if(renderLayer.equals(getTargetEnchantGlintLayer())){
				for(var customLayer : GLINT_LAYERS.renderLayers())
				{
					if(((RenderLayerAccessor)customLayer).enchantOutline$shouldUseLayerBuffer()) {
						receiver.endBatch(customLayer);
					}
				}
			}
			return InteractionResult.PASS;
		});

		ImmediateRenderCurrentLayer.Before.EVENT.register((receiver, layer) -> {
			for (RenderType renderLayer : ((MultiBufferSource_BufferSourceAccessor)receiver).enchantOutline$getLayerBuffers().keySet()) {
				if(((RenderLayerAccessor)renderLayer).enchantOutline$shouldDrawBeforeCustom()){
					receiver.endBatch(renderLayer);
				}
			}

			return InteractionResult.PASS;
		});

		ImmediateRenderCurrentLayer.After.EVENT.register((receiver, layer) -> {
			for (RenderType renderLayer : ((MultiBufferSource_BufferSourceAccessor)receiver).enchantOutline$getLayerBuffers().keySet()) {
				if(((RenderLayerAccessor)renderLayer).enchantOutline$shouldDrawAfterCustom()){
					receiver.endBatch(renderLayer);
				}
			}

			return InteractionResult.PASS;
		});

		//MultiBufferSource contains a setDirty method used to track if we need to update it's return value or not.
		BufferBuilderModifyReturnValue.EVENT.register((original) -> {
			MultiBufferSource_BufferSourceAccessor accessor = (MultiBufferSource_BufferSourceAccessor)original;

			var enchantGlintLayer = getTargetEnchantGlintLayer();
			var enchantColorLayer = getTargetEnchantColorLayer();
			var enchantZFixLayer = getTargetEnchantZFixLayer();

			var buffers = accessor.enchantOutline$getLayerBuffers();
			if(!Objects.equals(accessor.enchantOutline$getDirty(GLINT_LAYERS), GLINT_LAYERS.getDirty()) && buffers.containsKey(enchantGlintLayer) || !Objects.equals(accessor.enchantOutline$getDirty(COLOR_LAYERS), COLOR_LAYERS.getDirty()) && buffers.containsKey(enchantColorLayer) || !Objects.equals(accessor.enchantOutline$getDirty(ZFIX_LAYERS), ZFIX_LAYERS.getDirty()) && buffers.containsKey(enchantZFixLayer)){
				accessor.enchantOutline$setDirty(GLINT_LAYERS, GLINT_LAYERS.getDirty());
				accessor.enchantOutline$setDirty(COLOR_LAYERS, COLOR_LAYERS.getDirty());
				accessor.enchantOutline$setDirty(ZFIX_LAYERS, ZFIX_LAYERS.getDirty());

				SequencedMap<RenderType, ByteBufferBuilder> clonedBuffer = new Object2ObjectLinkedOpenHashMap<>(buffers);
				buffers.clear();
				for(var set : clonedBuffer.entrySet()) {
					if(!GLINT_LAYERS.containsRenderLayer(set.getKey()) && !COLOR_LAYERS.containsRenderLayer(set.getKey()) && !ZFIX_LAYERS.containsRenderLayer(set.getKey())) {
						if(set.getKey() == enchantColorLayer) {
							for(RenderType layer : COLOR_LAYERS.renderLayers()) {
								if(((RenderLayerAccessor)layer).enchantOutline$shouldUseLayerBuffer()){
									buffers.put(layer, new ByteBufferBuilder(layer.bufferSize()));
								}
							}
						}

						//this block is stupid, but we need to make sure our armor layer goes where we want it to. This is how
						if(set.getKey() == Shaders.ARMOR_ENTITY_GLINT_FIX){
							enchantGlintLayer = Shaders.ARMOR_ENTITY_GLINT_FIX;
						}
						if(set.getKey() == enchantGlintLayer) {
							for(RenderType layer : GLINT_LAYERS.renderLayers())
							{
								if(((RenderLayerAccessor)layer).enchantOutline$shouldUseLayerBuffer()) {
									buffers.put(layer, new ByteBufferBuilder(layer.bufferSize()));
								}
							}
						}
						if(set.getKey() == getTargetEnchantGlintLayer()){
							buffers.put(Shaders.ARMOR_ENTITY_GLINT_FIX, new ByteBufferBuilder(Shaders.ARMOR_ENTITY_GLINT_FIX.bufferSize()));
						}
						//end dumb block

						if(set.getKey() == enchantZFixLayer){
							for(RenderType layer : ZFIX_LAYERS.renderLayers())
							{
								if(((RenderLayerAccessor)layer).enchantOutline$shouldUseLayerBuffer()) {
									buffers.put(layer, new ByteBufferBuilder(layer.bufferSize()));
								}
							}
						}
						buffers.put(set.getKey(), set.getValue());
					}
				}
			}

			return null;
		});
		//--------- End Render Layer Order ----------

		//---------- Item Type Storage ----------
		ItemModelManagerUpdateModelCallback.EVENT.register(((receiver, model, itemRenderState, itemStack, itemModelManager, itemDisplayContext, clientWorld, heldItemContext, seed) -> {
			((ItemRenderStateAccessor)itemRenderState).enchantOutline$setItemRendered(itemStack.getItem());

			return InteractionResult.PASS;
		}));

		ItemRenderStateRenderLayerCallback.EVENT.register(((receiver, layerRenderState, matrices, orderedRenderCommandQueue, light, overlay, i) -> {
			((ItemRenderState_LayerRenderStateAccessor)layerRenderState).enchantOutline$setOwningItemRenderState(receiver);

			return InteractionResult.PASS;
		}));

		//set the item right before we lose the type
		LayerRenderStateRenderSpecial.Callback.EVENT.register(((receiver, specialModelRenderer, o, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
			LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.set(receiver);

			return InteractionResult.PASS;
		}));

		//clear the item right after calling
		LayerRenderStateRenderSpecial.Post.EVENT.register(((receiver, specialModelRenderer, o, matrixStack, orderedRenderCommandQueue, light, overlay, glint, i) -> {
			LAYER_RENDER_STATE_RENDER_MODEL_STORAGE.remove();

			return InteractionResult.PASS;
		}));
		//---------- End Item Type Storage ----------

		//---------- Mod Patches ----------

		//--------- End Mod Patches ----------

		//--------- Overwrite Vanilla ----------
		WorldRendererFirstRenderMainPassCallback.EVENT.register(() -> {
			if(getConfig().shouldRemoveRenderPass()){
				return InteractionResult.FAIL;
			}
			return InteractionResult.PASS;
		});
	}

	private static void initLayers(){
		GLINT_LAYERS.addCustomRenderLayer(ResourceLocation.fromNamespaceAndPath(MOD_ID,"cutoutlayer").toString(), Shaders.GLINT_CUTOUT_LAYER);
		COLOR_LAYERS.addCustomRenderLayer(ResourceLocation.fromNamespaceAndPath(MOD_ID,"cutoutlayer").toString(), Shaders.COLOR_CUTOUT_LAYER);
		ZFIX_LAYERS.addCustomRenderLayer(ResourceLocation.fromNamespaceAndPath(MOD_ID, "cutoutlayer").toString(), Shaders.ZFIX_CUTOUT_LAYER);
	}

	@Nullable ItemOverride getOverrideFromLayerRenderState(Function<String, @Nullable ItemOverride> overrideGetter, ItemStackRenderState.LayerRenderState layerRenderState){
		@Nullable ItemStackRenderState owningState = ((ItemRenderState_LayerRenderStateAccessor)layerRenderState).enchantOutline$getOwningRenderState();
		if(owningState != null){
			@Nullable Item renderedItem = ((ItemRenderStateAccessor)owningState).enchantOutline$getItemRendered();
			return getOverrideFromNullableItem(overrideGetter, renderedItem);
		}
		return null;
	}

	@Nullable ItemOverride getOverrideFromNullableItem(Function<String, @Nullable ItemOverride> overrideGetter, @Nullable Item renderedItem){
		if(renderedItem != null){
			ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(renderedItem);
			if (itemId != null){
				return overrideGetter.apply(itemId.toString());
			}
		}
		return null;
	}

}