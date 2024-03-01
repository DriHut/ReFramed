package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.block.ReFramedEntity;
import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.client.model.apperance.SpriteProperties;
import fr.adrien1106.reframed.mixin.MinecraftAccessor;
import fr.adrien1106.reframed.client.model.apperance.CamoAppearance;
import fr.adrien1106.reframed.client.model.apperance.CamoAppearanceManager;
import fr.adrien1106.reframed.client.model.apperance.WeightedComputedAppearance;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public abstract class RetexturingBakedModel extends ForwardingBakedModel {
	public RetexturingBakedModel(BakedModel base_model, CamoAppearanceManager tam, int theme_index, ModelBakeSettings settings, BlockState item_state, boolean ao) {
		this.wrapped = base_model; //field from the superclass; vanilla getQuads etc. will delegate through to this

		this.tam = tam;
		this.theme_index = theme_index;
		this.uv_lock = settings.isUvLocked();
		this.item_state = item_state;
		this.ao = ao;
	}

	protected final CamoAppearanceManager tam;
	protected final int theme_index;
	protected final boolean uv_lock;
	protected final BlockState item_state;
	protected final boolean ao;

	/* ----------------------------------------------- CACHE ELEMENT ------------------------------------------------ */
	// TODO make static ? for connected textures ?
	protected record MeshCacheKey(BlockState state, TransformCacheKey transform) {}
	protected record TransformCacheKey(CamoAppearance appearance, int model_id) {}
	protected final ConcurrentMap<TransformCacheKey, RetexturingTransformer> retextured_transforms = new ConcurrentHashMap<>();
	protected final ConcurrentMap<MeshCacheKey, Mesh> retextured_meshes = new ConcurrentHashMap<>(); //mutable, append-only cache

	protected static final Direction[] DIRECTIONS_AND_NULL;
	static {
		Direction[] values = Direction.values();
		DIRECTIONS_AND_NULL = new Direction[values.length + 1];
		System.arraycopy(values, 0, DIRECTIONS_AND_NULL, 0, values.length);
	}

	protected final ConcurrentMap<BlockState, Mesh> jsonToMesh = new ConcurrentHashMap<>();

	protected Mesh getBaseMesh(BlockState state) {
		//Convert models to re-texturable Meshes lazily, the first time we encounter each blockstate
		return jsonToMesh.computeIfAbsent(state, this::convertModel);
	}

	protected abstract Mesh convertModel(BlockState state);
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public Sprite getParticleSprite() {
		return tam.getDefaultAppearance(theme_index).getSprites(Direction.UP, 0).get(0).sprite();
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState theme = (world.getBlockEntity(pos) instanceof ThemeableBlockEntity s) ? s.getTheme(theme_index) : null;
		QuadEmitter quad_emitter = context.getEmitter();
		if(theme == null || theme.isAir()) {
			getUntintedRetexturedMesh(new MeshCacheKey(state, new TransformCacheKey(tam.getDefaultAppearance(theme_index), 0)), 0).outputTo(quad_emitter);
			return;
		}
		if(theme.getBlock() == Blocks.BARRIER) return;
		
		CamoAppearance camo = tam.getCamoAppearance(world, theme, pos, theme_index);
		long seed = theme.getRenderingSeed(pos);
		int model_id = 0;
		if (camo instanceof WeightedComputedAppearance wca) model_id = wca.getAppearanceIndex(seed);
		
		int tint = 0xFF000000 | MinecraftClient.getInstance().getBlockColors().getColor(theme, world, pos, 0);
		Mesh untintedMesh = getUntintedRetexturedMesh(
			new MeshCacheKey(
				state,
				new TransformCacheKey(camo, model_id)
			),
			seed
		);
		
		//The specific tint might vary a lot; imagine grass color smoothly changing. Trying to bake the tint into
		//the cached mesh will pollute it with a ton of single-use meshes with only slightly different colors.
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(quad_emitter);
		} else {
			context.pushTransform(new TintingTransformer(camo, tint, seed));
			untintedMesh.outputTo(quad_emitter);
			context.popTransform();
		}
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it & look up the item color provider
		//none of this is accessible unless you're in creative mode doing ctrl-pick btw
		CamoAppearance nbtAppearance;
		int tint;
		BlockState theme = ReFramedEntity.readStateFromItem(stack, theme_index);
		if(!theme.isAir()) {
			nbtAppearance = tam.getCamoAppearance(null, theme, null, theme_index);
			tint = 0xFF000000 | ((MinecraftAccessor) MinecraftClient.getInstance()).getItemColors().getColor(new ItemStack(theme.getBlock()), 0);
		} else {
			nbtAppearance = tam.getDefaultAppearance(theme_index);
			tint = 0xFFFFFFFF;
		}
		
		Mesh untintedMesh = getUntintedRetexturedMesh(new MeshCacheKey(item_state, new TransformCacheKey(nbtAppearance, 0)), 0);

		QuadEmitter quad_emitter = context.getEmitter();
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(quad_emitter);
		} else {
			context.pushTransform(new TintingTransformer(nbtAppearance, tint, 0));
			untintedMesh.outputTo(quad_emitter);
			context.popTransform();
		}
	}
	
	protected Mesh getUntintedRetexturedMesh(MeshCacheKey key, long seed) {
		return retextured_meshes.computeIfAbsent(key, (k) -> createUntintedRetexturedMesh(k, seed));
	}
	
	protected Mesh createUntintedRetexturedMesh(MeshCacheKey key, long seed) {
		RetexturingTransformer transformer = retextured_transforms.computeIfAbsent(key.transform, (k) -> new RetexturingTransformer(k.appearance, seed));
		return pretransformMesh(getBaseMesh(key.state), transformer);
	}

	private static Mesh pretransformMesh(Mesh mesh, RetexturingTransformer transform) {
		MeshBuilder builder = ReFramedClient.HELPER.getFabricRenderer().meshBuilder();
		QuadEmitter emitter = builder.getEmitter();

		mesh.forEach(quad -> {
			int i = -1;
			do {
				emitter.copyFrom(quad);
				i = transform.transform(emitter, i);
			} while (i > 0);
		});

		return builder.build();
	}
	
	public class RetexturingTransformer {
		private final long seed;
		protected RetexturingTransformer(CamoAppearance ta, long seed) {
			this.ta = ta;
			this.seed = seed;
		}
		
		protected final CamoAppearance ta;

		public int transform(QuadEmitter quad, int i) {
			if(quad.tag() == 0) return 0; //Pass the quad through unmodified.

			Direction direction = quad.nominalFace();
			List<SpriteProperties> sprites = ta.getSprites(direction, seed);
			if (i == -1) i = sprites.size();

			SpriteProperties properties = sprites.get(sprites.size() - i);
			i--;
			QuadPosBounds bounds = properties.bounds();

			if (bounds == null) { // sprite applies anywhere e.g. default behaviour
				quad.material(ta.getRenderMaterial(ao));
				quad.spriteBake(
					properties.sprite(),
					MutableQuadView.BAKE_NORMALIZED
						| properties.flags()
						| (uv_lock ? MutableQuadView.BAKE_LOCK_UV : 0)
				);
				quad.tag(i+1);
				quad.emit();
				return i;
			}

			// verify if sprite covers the current quad and apply the new size
			QuadPosBounds origin_bounds = QuadPosBounds.read(quad, false);
			if (!bounds.matches(origin_bounds)) return i;

			// apply new quad shape
			quad.material(ta.getRenderMaterial(ao));
			bounds.intersection(origin_bounds, direction.getAxis()).apply(quad, origin_bounds);
			quad.spriteBake( // seems to work without the flags and break with it
				properties.sprite(),
				MutableQuadView.BAKE_NORMALIZED
					| MutableQuadView.BAKE_LOCK_UV
			);
			quad.tag(i+1);
			quad.emit();
			return i;
		}
	}
	
	protected static class TintingTransformer implements RenderContext.QuadTransform {
		private final long seed;
		protected TintingTransformer(CamoAppearance ta, int tint, long seed) {
			this.ta = ta;
			this.tint = tint;
			this.seed = seed;
		}
		
		protected final CamoAppearance ta;
		protected final int tint;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			if(quad.tag() == 0) return true;

			if(ta.hasColor(quad.nominalFace(), seed, quad.tag())) quad.color(tint, tint, tint, tint);
			
			return true;
		}
	}
}
