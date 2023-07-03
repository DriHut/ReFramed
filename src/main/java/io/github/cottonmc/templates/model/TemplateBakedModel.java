package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TemplateBakedModel extends ForwardingBakedModel {
	public TemplateBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, AffineTransformation aff, Mesh baseMesh) {
		this.wrapped = baseModel;
		
		this.tam = tam;
		this.baseMesh = MatrixMeshTransformer.transformAroundCenter(aff, baseMesh);
		
		//Hard to explain what this is for...
		//Basically, the previous incarnation of this mod assembled the north/south/east/west faces all individually.
		//This means it was easy to get the orientation of the block correct - to popular the north face of the slope, look at
		//the north texture of the theme block. In this version, there is only *one* slope model that is dynamically rotated
		//to form the other possible orientations. If I populate the north face of the model using the north face of the theme,
		//that model will then be rotated so it's no longer facing the right way.
		//
		//This seems to work, but I'm kinda surprised I don't need to invert the transformation here, which is a clue that
		//I don't really understand all the math, loool
		for(Direction input : Direction.values()) {
			Direction output = Direction.transform(aff.getMatrix(), input);
			facePermutation.put(input, output);
		}
	}
	
	private final TemplateAppearanceManager tam;
	private final Mesh baseMesh;
	
	private final Map<Direction, Direction> facePermutation = new EnumMap<>(Direction.class);
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(retexturingBlockTransformer(blockView, state, pos, randomSupplier));
		context.meshConsumer().accept(baseMesh);
		context.popTransform();
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(retexturingItemTransformer(stack, randomSupplier));
		context.meshConsumer().accept(baseMesh);
		context.popTransform();
	}
	
	@Override
	public boolean isSideLit() {
		//Makes item models look less bad. TODO: possibly a weird spot to put this. corresponds to `gui_light: front` in the json
		return false;
	}
	
	public @NotNull RenderContext.QuadTransform retexturingBlockTransformer(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
		BlockState template = (((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos) instanceof BlockState s) ? s : null;
		if(template == null || template.isAir()) return new RetexturingTransformer(tam.getDefaultAppearance(), 0xFFFFFFFF, facePermutation);
		
		BlockColorProvider prov = ColorProviderRegistry.BLOCK.get(template.getBlock());
		int globalTint = prov != null ? prov.getColor(state, blockView, pos, 1) : 0xFFFFFFFF;
		return new RetexturingTransformer(tam.getAppearance(template), globalTint, facePermutation);
	}
	
	public @NotNull RenderContext.QuadTransform retexturingItemTransformer(ItemStack stack, Supplier<Random> randomSupplier) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it
		NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
		if(tag != null && tag.contains("BlockState")) {
			BlockState state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
			if(!state.isAir()) return new RetexturingTransformer(tam.getAppearance(state), 0xFFFFFFFF, facePermutation);
		}
		
		return new RetexturingTransformer(tam.getDefaultAppearance(), 0xFFFFFFFF, facePermutation);
	}
	
	public static record RetexturingTransformer(TemplateAppearance appearance, int color, Map<Direction, Direction> facePermutation) implements RenderContext.QuadTransform {
		private static final Direction[] DIRECTIONS = Direction.values();
		
		@Override
		public boolean transform(MutableQuadView quad) {
			quad.material(appearance.getRenderMaterial());
			
			//The quad tag numbers were selected so this magic trick works:
			Direction dir = facePermutation.get(DIRECTIONS[quad.tag()]);
			
			//TODO: this newly-simplified direction passing to hasColor is almost certainly incorrect
			// I think hasColor was kinda incorrect in the first place tho
			if(appearance.hasColor(dir)) quad.color(color, color, color, color);
			Sprite sprite = appearance.getSprite(dir);
			
			quad.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
			return true;
		}
	}
}
