package fr.adrien1106.reframed.client.model.apperance;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.client.model.DynamicBakedModel;
import fr.adrien1106.reframed.client.model.QuadPosBounds;
import fr.adrien1106.reframed.mixin.model.WeightedBakedModelAccessor;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class CamoAppearanceManager {

	public CamoAppearanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		MaterialFinder finder = ReFramedClient.HELPER.getFabricRenderer().materialFinder();
		for(BlendMode blend : BlendMode.values()) {
			finder.clear().disableDiffuse(false).blendMode(blend);
			
			materialsWithoutAo.put(blend, finder.ambientOcclusion(TriState.FALSE).find());
			materialsWithAo.put(blend, finder.ambientOcclusion(TriState.DEFAULT).find()); //not "true" since that *forces* AO, i just want to *allow* AO
		}
		
		Sprite defaultSprite = spriteLookup.apply(DEFAULT_SPRITE_ID);
		if(defaultSprite == null) throw new IllegalStateException("Couldn't locate " + DEFAULT_SPRITE_ID + " !");
		this.defaultAppearance = new SingleSpriteAppearance(defaultSprite, materialsWithoutAo.get(BlendMode.CUTOUT), serialNumber.getAndIncrement());
		
		Sprite barrier = spriteLookup.apply(BARRIER_SPRITE_ID);
		if(barrier == null) barrier = defaultSprite; //eh
		this.barrierItemAppearance = new SingleSpriteAppearance(barrier, materialsWithoutAo.get(BlendMode.CUTOUT), serialNumber.getAndIncrement());
	}

	protected static final SpriteIdentifier DEFAULT_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(ReFramed.MODID, "block/framed_block"));
	private static final SpriteIdentifier BARRIER_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:item/barrier"));
	
	private final CamoAppearance defaultAppearance;
	private final CamoAppearance barrierItemAppearance;
	
	private final ConcurrentHashMap<BlockState, CamoAppearance> appearanceCache = new ConcurrentHashMap<>(); //Mutable, append-only cache
	private final AtomicInteger serialNumber = new AtomicInteger(0); //Mutable
	
	private final EnumMap<BlendMode, RenderMaterial> materialsWithAo = new EnumMap<>(BlendMode.class);
	private final EnumMap<BlendMode, RenderMaterial> materialsWithoutAo = new EnumMap<>(BlendMode.class); //Immutable contents
	
	public CamoAppearance getDefaultAppearance() {
		return defaultAppearance;
	}
	
	public CamoAppearance getCamoAppearance(BlockRenderView world, BlockState state, BlockPos pos) {
		BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);

		// add support for connected textures and more generally any compatible models injected so that they return baked quads
		if (model instanceof DynamicBakedModel dynamic_model) {
			return computeAppearance(dynamic_model.computeQuads(world, state, pos), state);
		}
		return appearanceCache.computeIfAbsent(state, block_state -> computeAppearance(model, block_state));
	}
	
	public RenderMaterial getCachedMaterial(BlockState state, boolean ao) {
		Map<BlendMode, RenderMaterial> m = ao ? materialsWithAo : materialsWithoutAo;
		return m.get(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state)));
	}
	
	// I'm pretty sure ConcurrentHashMap semantics allow for this function to be called multiple times on the same key, on different threads.
	// The computeIfAbsent map update will work without corrupting the map, but there will be some "wasted effort" computing the value twice.
	// The results are going to be the same, apart from their serialNumbers differing (= their equals & hashCode differing).
	// Tiny amount of wasted space in some caches if CamoAppearances are used as a map key, then. IMO it's not a critical issue.
	private CamoAppearance computeAppearance(BakedModel model, BlockState state) {
		if(state.getBlock() == Blocks.BARRIER) return barrierItemAppearance;

		if (!(model instanceof WeightedBakedModelAccessor weighted_model)) {
			return new ComputedAppearance(
				getAppearance(model),
				getCachedMaterial(state, true),
				getCachedMaterial(state, false),
				serialNumber.getAndIncrement()
			);
		}
		List<Weighted.Present<Appearance>> appearances = weighted_model.getModels().stream()
			.map(baked_model -> Weighted.of(getAppearance(baked_model.getData()), baked_model.getWeight().getValue()))
			.toList();

		return new WeightedComputedAppearance(
			appearances,
			getCachedMaterial(state, true),
			getCachedMaterial(state, false),
			serialNumber.getAndIncrement()
		);
	}

	private Appearance getAppearance(BakedModel model) {
		// Only for parsing vanilla quads:
		Renderer r = ReFramedClient.HELPER.getFabricRenderer();
		QuadEmitter quad_emitter = r.meshBuilder().getEmitter();
		RenderMaterial material = r.materialFinder().clear().find();
		Random random = Random.create();

		Map<Direction, List<SpriteProperties>> sprites = new EnumMap<>(Direction.class);
		byte[] color_mask = {0b000000};

		//Read quads off the model by their `cullface`
		Arrays.stream(Direction.values()).forEach(direction -> {
			List<BakedQuad> quads = model.getQuads(null, direction, random);
			if(quads.isEmpty()) { // add default appearance if none present
				sprites.put(direction, defaultAppearance.getSprites(direction, 0));
				return;
			}

			sprites.put(direction, new ArrayList<>());
			quads.forEach(quad -> {
				if(quad.hasColor()) color_mask[0] |= (byte) (1 << direction.ordinal());

				Sprite sprite = quad.getSprite();
				if(sprite == null) return;
				sprites.compute(direction, (dir, pairs) -> {
					quad_emitter.fromVanilla(quad, material, direction);
					pairs.add(new SpriteProperties(sprite, getBakeFlags(quad_emitter, sprite), QuadPosBounds.read(quad_emitter)));
					return pairs;
				});
			});
		});

		return new Appearance(sprites, color_mask[0]);
	}

	private static int getBakeFlags(QuadEmitter emitter, Sprite sprite) {
		boolean[][] order_matrix = getOrderMatrix(emitter, sprite);
		int flag = 0;
		if (!isClockwise(order_matrix)) { // check if quad has been mirrored on model
			// check which mirroring is more efficient in terms of rotations
			int rotation_u = getRotation(flipOrderMatrix(order_matrix, MutableQuadView.BAKE_FLIP_U));
			int rotation_v = getRotation(flipOrderMatrix(order_matrix, MutableQuadView.BAKE_FLIP_V));
			if (rotation_u < rotation_v) flag = MutableQuadView.BAKE_FLIP_U | rotation_u;
			else flag = MutableQuadView.BAKE_FLIP_V | rotation_v;
		} else flag |= getRotation(order_matrix);
		return flag;
	}

	private static int getRotation(boolean[][] order_matrix) {
		int rotations = MutableQuadView.BAKE_ROTATE_NONE;
		rotations |= order_matrix[0][0] && !order_matrix[0][1] ? MutableQuadView.BAKE_ROTATE_90 : 0;
		rotations |= !order_matrix[0][0] && !order_matrix[0][1] ? MutableQuadView.BAKE_ROTATE_180 : 0;
		rotations |= !order_matrix[0][0] && order_matrix[0][1] ? MutableQuadView.BAKE_ROTATE_270 : 0;
		return rotations;
	}

	private static boolean isClockwise(boolean[][] rotation_matrix) {
		for (int i = 1; i < rotation_matrix.length; i++) {
			if (rotation_matrix[i][0] != rotation_matrix[i-1][1] && rotation_matrix[i][0] != rotation_matrix[i][1])
				return false;
		}
		return true;
	}

	private static boolean[][] flipOrderMatrix(boolean[][] order_matrix, int flag) {
		boolean[][] new_matrix = new boolean[4][2];
		for (int i = 0; i < 4; i++) {
			new_matrix[i][0] = (flag == MutableQuadView.BAKE_FLIP_U) != order_matrix[i][0];
			new_matrix[i][1] = (flag == MutableQuadView.BAKE_FLIP_V) != order_matrix[i][1];
		}
		return new_matrix;
	}

	private static boolean[][] getOrderMatrix(QuadEmitter emitter, Sprite sprite) {
		float u_center = (sprite.getMinU() + sprite.getMaxU()) / 2;
		float v_center = (sprite.getMinV() + sprite.getMaxV()) / 2;
		boolean[][] order_matrix = new boolean[4][2];
		for (int i = 0; i < 4; i++) {
			order_matrix[i][0] = emitter.u(i) < u_center;
			order_matrix[i][1] = emitter.v(i) < v_center;
		}
		return order_matrix;
	}
}
