package fr.adrien1106.reframedtemplates.api;

import fr.adrien1106.reframedtemplates.model.apperance.TemplateAppearanceManager;
import fr.adrien1106.reframedtemplates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.AvailableSince("2.2")
@SuppressWarnings({"unused", "UnusedReturnValue"}) //this is all public api
public interface TemplatesClientApi {
	/**
	 * Obtain the current API instance.
	 */
	static TemplatesClientApi getInstance() {
		return TemplatesClient.API_IMPL;
	}
	
	/// CONSTRUCTING UNBAKED MODELS ///
	
	/**
	 * - the quad: Sourced from the ID you pass in. It can be a json model.
	 * - whether you want to retexture it: "Yes". All quads will be retextured.
	 * - what face of the block: Automatically determined by facing direction.
	 */
	TweakableUnbakedModel auto(Identifier parent);
	
	/**
	 * - the quad: Sourced from the ID you pass in. It can be a json model.
	 * - whether you want to retexture it: Determined from the texture applied to the quad.
	 * - what face of the block: Determined via the texture applied to the quad.
	 */
	TweakableUnbakedModel json(Identifier parent);
	
	/**
	 * - the quad: Sourced from a `Mesh`.
	 * - whether you want to retexture it: Quads with a nonzero `tag`.
	 * - what face of the block: Determined from the `tag`.
	 * <p>
	 * This form doesn't give the ability to look up sprites, so it's hard to make a sensible quad that won't be retextured.
	 */
	default TweakableUnbakedModel mesh(Identifier parent, Supplier<Mesh> baseMeshFactory) {
		return mesh(parent, __ -> baseMeshFactory.get());
	}
	
	/**
	 * - the quad: Sourced from a `Mesh`.
	 * - whether you want to retexture it: Quads with a nonzero `tag`.
	 * - what face of the block: Determined from the `tag`.
	 * <p>
	 * You can use the provided Function<SpriteIdentifier, Sprite> to look up sprite UVs and put them on faces with a 0 tag.
	 * These faces will not get retextured.
	 */
	TweakableUnbakedModel mesh(Identifier parent, Function<Function<SpriteIdentifier, Sprite>, Mesh> baseMeshFactory);
	
	/**
	 * Get the TemplateAppearanceManager instance. To retexture a template, there has to be some way of determining what texture should
	 * go on the top, what texture should go on the north side, and the TemplateAppearanceManager is in charge of gleaning this information
	 * from the target blockmodels. It also caches this information.
	 * <p>
	 * There is one TemplateApperanceManager per resource-load. Please obtain a new one every model bake.
	 *
	 * @param spriteLookup Something you'll find as part of UnbakedModel#bake.
	 */
	TemplateAppearanceManager getOrCreateTemplateApperanceManager(Function<SpriteIdentifier, Sprite> spriteLookup);
	
	/// REGISTERING UNBAKED MODELS ///
	
	/**
	 * Register an UnbakedModel to be loaded behind a particular ID.
	 * Astute viewers will note that this is, *currently*, a thin wrapper around the fabric ModelResourceProvider system.
	 */
	void addTemplateModel(Identifier id, UnbakedModel unbaked);
	
	/**
	 * When the game loads this ModelIdentifier, it will instead load the UnbakedModel corresponding to the id passed to addTemplateModel.
	 * Astute viewers will note that this is, *currently*, a thin wrapper around the fabric ModelVariantProvider system.
	 */
	void assignItemModel(Identifier templateModelId, ModelIdentifier... modelIds);
	
	/**
	 * Calls assignItemModel(Identifier, ModelIdentifier) with "#inventory" appended.
	 * In practice: you can pass an item's ID.
	 */
	void assignItemModel(Identifier templateModelId, Identifier... itemIds);
	
	/**
	 * Calls assignItemModel(Identifier, Identifier) by first converting the argument to an item, then taking its ID.
	 * In practice: you can pass a Block (or Item), and the model will be assigned to the block's item form.
	 */
	void assignItemModel(Identifier templateModelId, ItemConvertible... itemConvs);
	
	/// OTHER STUFF LOL ///
	
	/**
	 * Simple wrapper around fabric's RenderAccess.INSTANCE.getRenderer() that throws a slightly more informative error if one is not
	 * present. Note that NullPointerException is not a checked exception.
	 */
	@NotNull Renderer getFabricRenderer() throws NullPointerException;
	
	interface TweakableUnbakedModel extends UnbakedModel {
		TweakableUnbakedModel disableAo();
		TweakableUnbakedModel itemModelState(BlockState state);
	}
}
