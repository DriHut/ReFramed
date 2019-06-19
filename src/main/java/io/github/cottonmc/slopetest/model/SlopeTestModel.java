package io.github.cottonmc.slopetest.model;

import java.util.Random;
import java.util.function.Supplier;

import org.apache.commons.lang3.ObjectUtils;

import io.github.cottonmc.slopetest.util.SpriteSet;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

public class SlopeTestModel extends SimpleModel {

    private static final ThreadLocal<Transformer> TRANSFORMERS = ThreadLocal.withInitial(Transformer::new);
    
    public SlopeTestModel(BlockState blockState) {
        super(baseMesh(blockState), TRANSFORMERS::get, MissingSprite.getMissingSprite(), ModelHelper.MODEL_TRANSFORM_BLOCK);
    }

    private static Mesh baseMesh(BlockState state) {
        final MeshBuilder builder = RENDERER.meshBuilder();
        final QuadEmitter quad = builder.getEmitter();
        final Direction dir = state.get(Properties.HORIZONTAL_FACING);
        drawSlope(quad.spriteColor(0, -1, -1, -1, -1), dir);
        drawLeftSide(quad.spriteColor(0, -1, -1, -1, -1), dir);
        drawRightSide(quad.spriteColor(0, -1, -1, -1, -1), dir);
        drawBack(quad.spriteColor(0, -1, -1, -1, -1), dir);
        drawBottom(quad.spriteColor(0, -1, -1, -1, -1));
        return builder.build();
    }
    
    private static final int TAG_SLOPE = 0;
    private static final int TAG_LEFT = 1;
    private static final int TAG_RIGHT = 2;
    private static final int TAG_BACK = 3;
    private static final int TAG_BOTTOM = 4;
    
    private static void drawSlope(QuadEmitter quad, Direction dir) {
        quad.tag(TAG_SLOPE);
        switch (dir) {
            case NORTH:
                quad.pos(0, 0f, 1f, 0f).pos(1, 0f, 0f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 1f, 0f).emit();
                break;
            case SOUTH:
                quad.pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 1f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 0f).emit();
                break;
            case EAST:
                quad.pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 1f).pos(2, 1f, 1f, 1f).pos(3, 1f, 1f, 0f).emit();
                break;
            case WEST:
                quad.pos(0, 0f, 1f, 0f).pos(1, 0f, 1f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 0f, 0f).emit();
            default:
                break;
        }
    }

    private static void drawLeftSide(QuadEmitter quad, Direction dir) {
        switch(dir) {
            case NORTH:
                quad.tag(TAG_LEFT).pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 1f).pos(2, 0f, 0f, 1f).pos(3, 0f, 1f, 0f).emit();
                break;
            case SOUTH:
                quad.tag(TAG_LEFT).pos(0, 1f, 0f, 0f).pos(1, 1f, 1f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 0f, 1f).emit();
                break;
            case EAST:
                quad.tag(TAG_LEFT).pos(0, 1f, 0f, 0f).pos(1, 0f, 0f, 0f).pos(2, 0f, 0f, 0f).pos(3, 1f, 1f, 0f).emit();
                break;
            case WEST:
                quad.tag(TAG_LEFT).pos(0, 0f, 0f, 1f).pos(1, 1f, 0f, 1f).pos(2, 1f, 0f, 1f).pos(3, 0f, 1f, 1f).emit();
            default:
                break;
        }
    }

    private static void drawRightSide(QuadEmitter quad, Direction dir) {
        switch(dir) {
            case NORTH:
                quad.tag(TAG_RIGHT).pos(0, 1f, 0f, 0f).pos(1, 1f, 1f, 0f).pos(2, 1f, 0f, 1f).pos(3, 1f, 0f, 1f).emit();
                break;
            case SOUTH:
                quad.tag(TAG_RIGHT).pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 0f).pos(2, 0f, 0f, 1f).pos(3, 0f, 1f, 1f).emit();
                break;
            case EAST:
                quad.tag(TAG_RIGHT).pos(0, 0f, 0f, 1f).pos(1, 0f, 0f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 1f, 1f).emit();
                break;
            case WEST:
                quad.tag(TAG_RIGHT).pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 0f).pos(2, 1f, 0f, 0f).pos(3, 1f, 0f, 0f).emit();
            default:
                break;
        }
    }

    private static void drawBack(QuadEmitter quad, Direction dir) {
        switch(dir) {
            case NORTH:
                quad.tag(TAG_BACK).pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 0f).pos(2, 1f, 1f, 0f).pos(3, 1f, 0f, 0f).emit();
                break;
            case SOUTH:
                quad.tag(TAG_BACK).pos(0, 0f, 0f, 1f).pos(1, 1f, 0f, 1f).pos(2, 1f, 1f, 1f).pos(3, 0f, 1f, 1f).emit();
                break;
            case EAST:
                quad.tag(TAG_BACK).pos(0, 1f, 0f, 0f).pos(1, 1f, 1f, 0f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 1f).emit();
                break;
            case WEST:
                quad.tag(TAG_BACK).pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 1f).pos(2, 0f, 1f, 1f).pos(3, 0f, 1f, 0f).emit();
            default:
                break;
        }
    }

    private static void drawBottom(QuadEmitter quad) {
        quad.tag(TAG_BOTTOM).pos(0, 0f, 0f, 0f).pos(1, 1f, 0f, 0f).pos(2, 1f, 0f, 1f).pos(3, 0f, 0f, 1f).emit();
    }
    
    private static class Transformer implements MeshTransformer {
        private final MinecraftClient minecraft = MinecraftClient.getInstance();
        private final SpriteSet sprites = new SpriteSet();
        private final MaterialFinder finder = RENDERER.materialFinder();
        
        private int color;
        private Direction dir;
        private RenderMaterial material;
        
        @Override
        public MeshTransformer prepare(ExtendedBlockView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
            dir = state.get(Properties.HORIZONTAL_FACING);
            color = 0xffffff;
            final BlockState template =  ObjectUtils.defaultIfNull((BlockState) ((RenderAttachedBlockView)blockView).getBlockEntityRenderAttachment(pos), Blocks.AIR.getDefaultState());
            final Block block = template.getBlock();
            
            if(block == Blocks.AIR) {
                sprites.clear();
                material = finder.clear().blendMode(0, BlockRenderLayer.CUTOUT).find();
            } else {
                material = finder.clear().disableDiffuse(0, false).disableAo(0, false).blendMode(0, block.getRenderLayer()).find();
                BakedModel model = minecraft.getBlockRenderManager().getModel(template);
                sprites.prepare(model, randomSupplier.get());
                BlockColorProvider blockColor =  ColorProviderRegistry.BLOCK.get(block);
                if (blockColor != null) {
                    color = 0xff000000 | blockColor.getColor(template, blockView, pos, 1);
                }
            }
            return this;
        }

        @Override
        public MeshTransformer prepare(ItemStack stack, Supplier<Random> randomSupplier) {
            dir = Direction.NORTH;
            color = 0xffffff;
            sprites.clear();
            material = finder.clear().find();
            return this;
        }
        
        @Override
        public boolean transform(MutableQuadView quad) {
            quad.material(material);
            
            final SpriteSet sprites = this.sprites;
            switch(quad.tag()) {
            
                case TAG_SLOPE: 
                    if(sprites.hasColor(Direction.UP)) {
                        quad.spriteColor(0, color, color, color, color);
                    }
                    paintSlope(quad, dir, sprites.getSprite(Direction.UP));
                    break;
                
                
                case TAG_LEFT: 
                    final Direction leftDir = this.dir.rotateYCounterclockwise();
                    if(sprites.hasColor(leftDir)) {
                        quad.spriteColor(0, color, color, color, color);
                    }
                    paintLeftSide(quad, dir, sprites.getSprite(leftDir));
                    break;
                
                
                case TAG_RIGHT: {
                    final Direction rightDir = this.dir.rotateYClockwise();
                    if(sprites.hasColor(rightDir)) {
                        quad.spriteColor(0, color, color, color, color);
                    }
                    paintRightSide(quad, dir, sprites.getSprite(rightDir));
                    break;
                }
                    
                case TAG_BACK: {
                    if(sprites.hasColor(dir)) {
                        quad.spriteColor(0, color, color, color, color);
                    }
                    paintBack(quad, dir, sprites.getSprite(dir));
                    break;
                }
                
                case TAG_BOTTOM: {
                    if(sprites.hasColor(Direction.DOWN)) {
                        quad.spriteColor(0, color, color, color, color);
                    }
                    paintBottom(quad, sprites.getSprite(Direction.DOWN));
                    break;
                }
                    
                default:
            }
            return true;
        }
        
        private static void paintSlope(MutableQuadView quad, Direction dir, Sprite sprite) {
            switch (dir) {
                case NORTH:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(1, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(3, 0, sprite.getMaxU(), sprite.getMinV());
                    break;
                case SOUTH:
                    quad.sprite(0, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(2, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMaxV());
                    break;
                case EAST:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMinV());
                    break;
                case WEST:
                    quad.sprite(0, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(1, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(2, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(3, 0, sprite.getMaxU(), sprite.getMaxV());
                default:
                    break;
            }
        }

        private static void paintLeftSide(MutableQuadView quad, Direction dir, Sprite sprite) {
            switch(dir) {
                case NORTH:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMinV());
                    break;
                case SOUTH:
                    quad.sprite(0, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(1, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(2, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(3, 0, sprite.getMaxU(), sprite.getMaxV());
                    break;
                case EAST:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMinV());
                    break;
                case WEST:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMinV());
                default:
                    break;
            }
        }

        private static void paintRightSide(MutableQuadView quad, Direction dir, Sprite sprite) {
            switch(dir) {
                case NORTH:
                    quad.sprite(0, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(2, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMaxV());
                    break;
                case SOUTH:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(1, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(3, 0, sprite.getMaxU(), sprite.getMinV());
                    break;
                case EAST:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(1, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(3, 0, sprite.getMaxU(), sprite.getMinV());
                    break;
                case WEST:
                    quad.sprite(0, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(2, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMaxV());
                default:
                    break;
            }
        }

        private static void paintBack(MutableQuadView quad, Direction dir, Sprite sprite) {
            switch(dir) {
                case NORTH:
                    quad.sprite(0, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(2, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMaxV());
                    break;
                case SOUTH:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMinV());
                    break;
                case EAST:
                    quad.sprite(0, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(2, 0, sprite.getMinU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMaxV());
                    break;
                case WEST:
                    quad.sprite(0, 0, sprite.getMinU(), sprite.getMaxV())
                        .sprite(1, 0, sprite.getMaxU(), sprite.getMaxV())
                        .sprite(2, 0, sprite.getMaxU(), sprite.getMinV())
                        .sprite(3, 0, sprite.getMinU(), sprite.getMinV());
                default:
                    break;
            }
        }

        private static void paintBottom(MutableQuadView quad, Sprite sprite) {
            quad.sprite(0, 0, sprite.getMinU(), sprite.getMaxV())
                .sprite(1, 0, sprite.getMaxU(), sprite.getMaxV())
                .sprite(2, 0, sprite.getMaxU(), sprite.getMinV())
                .sprite(3, 0, sprite.getMinU(), sprite.getMinV());
        }
    }
}
