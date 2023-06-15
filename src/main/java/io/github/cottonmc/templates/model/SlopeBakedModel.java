package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.function.Function;
import java.util.function.Supplier;

public final class SlopeBakedModel extends ForwardingBakedModel {
	public SlopeBakedModel(BakedModel baseModel, BlockState slopeState, Function<SpriteIdentifier, Sprite> spriteLookup) {
		this.wrapped = baseModel;
		this.slopeState = slopeState;
		this.spriteLookup = spriteLookup;
		
		this.xform = new SlopeMeshTransformer(spriteLookup);
		this.baseMesh = SlopeBaseMesh.make(slopeState);
	}
	
	public final BlockState slopeState;
	public final Function<SpriteIdentifier, Sprite> spriteLookup;
	
	private final MeshTransformer xform;
	private final Mesh baseMesh;
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		MeshTransformer xform2 = xform.prepare(blockView, state, pos, randomSupplier);
		
		if(xform2 != null) {
			context.pushTransform(xform2);
			context.meshConsumer().accept(baseMesh);
			context.popTransform();
		} else {
			context.meshConsumer().accept(baseMesh);
		}
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		MeshTransformer xform2 = xform.prepare(stack, randomSupplier);
		
		if(xform2 != null) {
			context.pushTransform(xform2);
			context.meshConsumer().accept(baseMesh);
			context.popTransform();
		} else {
			context.meshConsumer().accept(baseMesh);
		}
	}
}
