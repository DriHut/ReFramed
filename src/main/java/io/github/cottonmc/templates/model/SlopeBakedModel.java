package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.function.Supplier;

public final class SlopeBakedModel extends ForwardingBakedModel {
	public SlopeBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, AffineTransformation aff) {
		this.wrapped = baseModel;
		
		this.preparer = new SlopeQuadTransformFactory(tam);
		this.blockAffineTransformer = new AffineQuadTransformer(aff);
		this.itemAffineTransformer = AffineQuadTransformer.EAST; //Makes items point the same way as stairs. Kinda clunky
		this.baseMesh = SlopeBaseMesh.make();
	}
	
	private final TemplateQuadTransformFactory preparer;
	private final AffineQuadTransformer blockAffineTransformer;
	private final AffineQuadTransformer itemAffineTransformer;
	private final Mesh baseMesh;
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(blockAffineTransformer);
		context.pushTransform(preparer.blockTransformer(blockView, state, pos, randomSupplier));
		context.meshConsumer().accept(baseMesh);
		context.popTransform();
		context.popTransform();
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(itemAffineTransformer);
		context.pushTransform(preparer.itemTransformer(stack, randomSupplier));
		context.meshConsumer().accept(baseMesh);
		context.popTransform();
		context.popTransform();
	}
}
