package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.block.ReFramedEntity;
import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.mixin.MinecraftAccessor;
import fr.adrien1106.reframed.client.model.apperance.CamoAppearance;
import fr.adrien1106.reframed.client.model.apperance.CamoAppearanceManager;
import fr.adrien1106.reframed.client.model.apperance.WeightedComputedAppearance;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.renderer.v1.mesh.*;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class RetexturingBakedModel extends ForwardingBakedModel {
	public RetexturingBakedModel(BakedModel base_model, CamoAppearanceManager tam, int theme_index, ModelBakeSettings settings, BlockState item_state, boolean ao) {
		this.wrapped = base_model; //field from the superclass; vanilla getQuads etc. will delegate through to this

		this.appearance_manager = tam;
		this.theme_index = theme_index;
		this.uv_lock = settings.isUvLocked();
		this.item_state = item_state;
		this.ao = ao;

		int cache_size = 64; // default is 64 why don't ask me and it should get overwritten
		if (item_state.getBlock() instanceof ReFramedBlock frame_block) cache_size = frame_block.getModelStateCount() + 1;
		BASE_MESH_CACHE = new Object2ObjectLinkedOpenHashMap<>(cache_size, 0.25f) {
			@Override
			protected void rehash(int v) {}
		};
	}

	protected final CamoAppearanceManager appearance_manager;
	protected final int theme_index;
	protected final boolean uv_lock;
	protected final boolean ao;
	protected final BlockState item_state;

	protected record MeshCacheKey(Object state_key, CamoAppearance appearance, int model_id) {}
	/** cache that store retextured models */
	protected final Object2ObjectLinkedOpenHashMap<MeshCacheKey, Mesh> RETEXTURED_MESH_CACHE =
		new Object2ObjectLinkedOpenHashMap<>(128, 0.25f) {
			@Override
			protected void rehash(int v) {}
		};

	/** cache that stores the base meshes which has the size of the amount of models */
	protected final Object2ObjectLinkedOpenHashMap<Object, Mesh> BASE_MESH_CACHE;

	protected static final Direction[] DIRECTIONS_AND_NULL;
	static {
		Direction[] values = Direction.values();
		DIRECTIONS_AND_NULL = new Direction[values.length + 1];
		System.arraycopy(values, 0, DIRECTIONS_AND_NULL, 0, values.length);
	}

	protected Mesh getBaseMesh(Object key, BlockState state) {
		//Convert models to re-texturable Meshes lazily, the first time we encounter each blockstate
		if (BASE_MESH_CACHE.containsKey(key)) return BASE_MESH_CACHE.getAndMoveToFirst(key);
		Mesh mesh = convertModel(state);
		BASE_MESH_CACHE.putAndMoveToFirst(key, mesh);
		return mesh;
	}

	protected abstract Mesh convertModel(BlockState state);
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public Sprite getParticleSprite() {
		return appearance_manager.getDefaultAppearance(theme_index).getSprites(Direction.UP, 0).get(0).sprite();
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		// skip render if block not a frame (which should always be the case
		if (!(state.getBlock() instanceof ReFramedBlock frame_block)) return;
		BlockState theme = (world.getBlockEntity(pos) instanceof ThemeableBlockEntity s) ? s.getTheme(theme_index) : null;
		QuadEmitter quad_emitter = context.getEmitter();
		if(theme == null || theme.isAir()) {
			getRetexturedMesh(
				new MeshCacheKey(
					frame_block.getModelCacheKey(state),
					appearance_manager.getDefaultAppearance(theme_index),
					0
				),
				state
			).outputTo(quad_emitter);
			return;
		}
		if(theme.getBlock() == Blocks.BARRIER) return;
		
		CamoAppearance camo = appearance_manager.getCamoAppearance(world, theme, pos, theme_index);
		long seed = theme.getRenderingSeed(pos);
		int model_id = 0;
		if (camo instanceof WeightedComputedAppearance wca) model_id = wca.getAppearanceIndex(seed);

		int tint = 0xFF000000 | MinecraftClient.getInstance().getBlockColors().getColor(theme, world, pos, 0);
		Mesh untintedMesh = getRetexturedMesh(new MeshCacheKey(frame_block.getModelCacheKey(state), camo, model_id), state);
		
		//The specific tint might vary a lot; imagine grass color smoothly changing. Trying to bake the tint into
		//the cached mesh will pollute it with a ton of single-use meshes with only slightly different colors.
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(quad_emitter);
		} else {
			context.pushTransform(new TintingTransformer(camo, model_id, tint));
			untintedMesh.outputTo(quad_emitter);
			context.popTransform();
		}
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it & look up the item color provider
		//none of this is accessible unless you're in creative mode doing ctrl-pick btw
		CamoAppearance appearance;
		int tint;
		BlockState theme = ReFramedEntity.readStateFromItem(stack, theme_index);
		if(!theme.isAir()) {
			appearance = appearance_manager.getCamoAppearance(null, theme, null, theme_index);
			tint = 0xFF000000 | ((MinecraftAccessor) MinecraftClient.getInstance()).getItemColors().getColor(new ItemStack(theme.getBlock()), 0);
		} else {
			appearance = appearance_manager.getDefaultAppearance(theme_index);
			tint = 0xFFFFFFFF;
		}
		
		Mesh untintedMesh = getRetexturedMesh(new MeshCacheKey("I", appearance, 0), item_state);

		QuadEmitter quad_emitter = context.getEmitter();
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(quad_emitter);
		} else {
			context.pushTransform(new TintingTransformer(appearance, 0, tint));
			untintedMesh.outputTo(quad_emitter);
			context.popTransform();
		}
	}
	
	protected Mesh getRetexturedMesh(MeshCacheKey key, BlockState state) {
		if (RETEXTURED_MESH_CACHE.containsKey(key)) return RETEXTURED_MESH_CACHE.getAndMoveToFirst(key);
		Mesh mesh = transformMesh(key, state);
		RETEXTURED_MESH_CACHE.putAndMoveToFirst(key, mesh);
		return mesh;
	}
	
	protected Mesh transformMesh(MeshCacheKey key, BlockState state) {
		MeshBuilder builder = ReFramedClient.HELPER.getFabricRenderer().meshBuilder();
		QuadEmitter emitter = builder.getEmitter();

		AtomicInteger quad_index = new AtomicInteger();
		getBaseMesh(key.state_key, state).forEach(quad -> {
			int i = -1;
			do {
				emitter.copyFrom(quad);
				i = key.appearance.transformQuad(emitter, i, quad_index.get(), key.model_id, ao, uv_lock);
			} while (i > 0);
			// kinda weird to do it like that but other directions don't use the quad_index so it doesn't matter
			if (quad.cullFace() == null) quad_index.getAndIncrement();
		});

		return builder.build();
	}

	protected static class TintingTransformer implements RenderContext.QuadTransform {
		private final CamoAppearance appearance;
		private final int model_id;
		private final int tint;

		protected TintingTransformer(CamoAppearance appearance, int model_id, int tint) {
			this.appearance = appearance;
			this.model_id = model_id;
			this.tint = tint;
		}

		@Override
		public boolean transform(MutableQuadView quad) {
			int camo_quad_index = quad.tag() - ((quad.tag() >>> 8) << 8);
			if(camo_quad_index == 0) return true;

			if(appearance.hasColor(quad.nominalFace(), model_id, camo_quad_index)) quad.color(tint, tint, tint, tint);

			return true;
		}
	}
}
