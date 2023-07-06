package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class RetexturedJsonModelBakedModel extends ForwardingBakedModel {
	public RetexturedJsonModelBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, ModelBakeSettings settings, Function<SpriteIdentifier, Sprite> spriteLookup, BlockState itemModelState) {
		this.wrapped = baseModel;
		this.tam = tam;
		this.facePermutation = MeshTransformUtil.facePermutation(settings);
		this.itemModelState = itemModelState;
		
		for(int i = 0; i < DIRECTIONS.length; i++) {
			SpriteIdentifier id = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Templates.id("templates_special/" + DIRECTIONS[i].getName()));
			this.specialSprites[i] = Objects.requireNonNull(spriteLookup.apply(id), () -> "Couldn't find sprite " + id + " !");
		}
	}
	
	private final TemplateAppearanceManager tam;
	private final Map<Direction, Direction> facePermutation;
	private final Sprite[] specialSprites = new Sprite[DIRECTIONS.length];
	private final BlockState itemModelState;
	
	private record CacheKey(BlockState state, TemplateAppearance appearance) {}
	private final ConcurrentHashMap<CacheKey, Mesh> meshCache = new ConcurrentHashMap<>();
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState theme = (((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos) instanceof BlockState s) ? s : null;
		TemplateAppearance ta = theme == null || theme.isAir() ? tam.getDefaultAppearance() : tam.getAppearance(theme);
		
		CacheKey key = new CacheKey(state, ta);
		context.meshConsumer().accept(meshCache.computeIfAbsent(key, this::makeMesh));
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		TemplateAppearance nbtAppearance = null;
		
		//cheeky: if the item has NBT data, pluck out the blockstate from it
		NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
		if(tag != null && tag.contains("BlockState")) {
			BlockState theme = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
			if(!theme.isAir()) nbtAppearance = tam.getAppearance(theme);
		}
		
		CacheKey key = new CacheKey(itemModelState, nbtAppearance == null ? tam.getDefaultAppearance() : nbtAppearance);
		context.meshConsumer().accept(meshCache.computeIfAbsent(key, this::makeMesh));
	}
	
	@Override
	public Sprite getParticleSprite() {
		return tam.getDefaultAppearance().getParticleSprite();
	}
	
	protected Mesh makeMesh(CacheKey key) {
		Renderer r = TemplatesClient.getFabricRenderer();
		MeshBuilder builder = r.meshBuilder();
		QuadEmitter emitter = builder.getEmitter();
		RenderMaterial mat = key.appearance().getRenderMaterial();
		
		Random rand = Random.create(42);
		
		for(Direction cullFace : DIRECTIONS_AND_NULL) {
			for(BakedQuad quad : wrapped.getQuads(key.state, cullFace, rand)) {
				emitter.fromVanilla(quad, mat, cullFace);
				
				QuadUvBounds bounds = QuadUvBounds.read(emitter);
				for(int i = 0; i < specialSprites.length; i++) {
					if(bounds.displaysSprite(specialSprites[i])) {
						bounds.remap(
							emitter,
							specialSprites[i],
							key.appearance().getSprite(facePermutation.get(DIRECTIONS[i])),
							key.appearance().getBakeFlags(facePermutation.get(DIRECTIONS[i]))
						);
						break;
					}
				}
				
				emitter.emit();
			}
		}
		
		return builder.build();
	}
	
	record QuadUvBounds(float minU, float maxU, float minV, float maxV) {
		static QuadUvBounds read(QuadView quad) {
			float u0 = quad.u(0); float u1 = quad.u(1); float u2 = quad.u(2); float u3 = quad.u(3);
			float v0 = quad.v(0); float v1 = quad.v(1); float v2 = quad.v(2); float v3 = quad.v(3);
			return new QuadUvBounds(
				Math.min(Math.min(u0, u1), Math.min(u2, u3)),
				Math.max(Math.max(u0, u1), Math.max(u2, u3)),
				Math.min(Math.min(v0, v1), Math.min(v2, v3)),
				Math.max(Math.max(v0, v1), Math.max(v2, v3))
			);
		}
		
		boolean displaysSprite(Sprite sprite) {
			return sprite.getMinU() <= minU && sprite.getMaxU() >= maxU && sprite.getMinV() <= minV && sprite.getMaxV() >= maxV;
		}
		
		void remap(MutableQuadView quad, Sprite specialSprite, Sprite newSprite, int bakeFlags) {
			//move the UVs into 0..1 range
			float remappedMinU = norm(minU, specialSprite.getMinU(), specialSprite.getMaxU());
			float remappedMaxU = norm(maxU, specialSprite.getMinU(), specialSprite.getMaxU());
			float remappedMinV = norm(minV, specialSprite.getMinV(), specialSprite.getMaxV());
			float remappedMaxV = norm(maxV, specialSprite.getMinV(), specialSprite.getMaxV());
			quad.uv(0, MathHelper.approximatelyEquals(quad.u(0), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(0), minV) ? remappedMinV : remappedMaxV);
			quad.uv(1, MathHelper.approximatelyEquals(quad.u(1), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(1), minV) ? remappedMinV : remappedMaxV);
			quad.uv(2, MathHelper.approximatelyEquals(quad.u(2), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(2), minV) ? remappedMinV : remappedMaxV);
			quad.uv(3, MathHelper.approximatelyEquals(quad.u(3), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(3), minV) ? remappedMinV : remappedMaxV);
			
			//call spriteBake
			//done this way (instead of directly setting UV coordinates to their final values) so that I can use the convenient bakeFlags option
			quad.spriteBake(newSprite, MutableQuadView.BAKE_NORMALIZED | bakeFlags);
		}
		
		static float norm(float value, float low, float high) {
			float value2 = MathHelper.clamp(value, low, high);
			return (value2 - low) / (high - low);
		}
		
		//static float rangeRemap(float value, float low1, float high1, float low2, float high2) {
		//	float value2 = MathHelper.clamp(value, low1, high1);
		//	return low2 + (value2 - low1) * (high2 - low2) / (high1 - low1);
		//}
	}
	
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final Direction[] DIRECTIONS_AND_NULL = new Direction[DIRECTIONS.length + 1];
	static {
		System.arraycopy(DIRECTIONS, 0, DIRECTIONS_AND_NULL, 0, DIRECTIONS.length);
	}
}
