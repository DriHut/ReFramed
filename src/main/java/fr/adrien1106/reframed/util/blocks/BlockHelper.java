package fr.adrien1106.reframed.util.blocks;

import fr.adrien1106.reframed.block.ReFramedEntity;
import fr.adrien1106.reframed.block.ReFramedStairBlock;
import fr.adrien1106.reframed.block.ReFramedStairsCubeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;
import static fr.adrien1106.reframed.util.blocks.StairShape.*;

public class BlockHelper {

    public static Corner getPlacementCorner(ItemPlacementContext ctx) {
        Direction side = ctx.getSide().getOpposite();
        Vec3d pos = getHitPos(ctx.getHitPos(), ctx.getBlockPos());
        Pair<Direction, Direction> sides = getHitSides(pos, side);

        return Corner.getByDirections(side, sides.getLeft(), sides.getRight());
    }

    private static Pair<Direction, Direction> getHitSides(Vec3d pos, Direction side) {
        Iterator<Direction.Axis> axes = Stream.of(Direction.Axis.values())
            .filter(axis -> !axis.equals(side.getAxis())).iterator();
        return new Pair<>(getHitDirection(axes.next(), pos), getHitDirection(axes.next(), pos));
    }

    public static Edge getPlacementEdge(ItemPlacementContext ctx) {
        Direction side = ctx.getSide().getOpposite();
        Vec3d pos = getHitPos(ctx.getHitPos(), ctx.getBlockPos());
        Direction.Axis axis = getHitAxis(pos, side);

        Direction part_direction = getHitDirection(axis, pos);

        return Edge.getByDirections(side, part_direction);
    }

    public static Direction.Axis getHitAxis(Vec3d pos, Direction side) {
        Stream<Direction.Axis> axes = Stream.of(Direction.Axis.values()).filter(axis -> !axis.equals(side.getAxis()));
        return axes.reduce((axis_1, axis_2) ->
            Math.abs(axis_1.choose(pos.x, pos.y, pos.z)) > Math.abs(axis_2.choose(pos.x, pos.y, pos.z))
                ? axis_1
                : axis_2
        ).orElse(null);
    }

    public static Direction getHitDirection(Direction.Axis axis, Vec3d pos) {
        return Direction.from(
            axis,
            axis.choose(pos.x, pos.y, pos.z) > 0
                ? Direction.AxisDirection.POSITIVE
                : Direction.AxisDirection.NEGATIVE
        );
    }

    public static Vec3d getRelativePos(Vec3d pos, BlockPos block_pos) {
        return new Vec3d(
            pos.getX() - block_pos.getX(),
            pos.getY() - block_pos.getY(),
            pos.getZ() - block_pos.getZ()
        );
    }

    public static Vec3d getHitPos(Vec3d pos, BlockPos block_pos) {
        pos = getRelativePos(pos, block_pos);
        return new Vec3d(
            pos.getX() - .5d,
            pos.getY() - .5d,
            pos.getZ() - .5d
        );
    }

    public static StairShape getStairsShape(Edge face, BlockView world, BlockPos pos) {
        StairShape shape = STRAIGHT;

        String sol = getNeighborPos(face, face.getFirstDirection(), true, face.getSecondDirection(), world, pos);
        switch (sol) {
            case "right": return INNER_RIGHT;
            case "left": return INNER_LEFT;
        }

        sol = getNeighborPos(face, face.getSecondDirection(), true, face.getFirstDirection(), world, pos);
        switch (sol) {
            case "right": return INNER_RIGHT;
            case "left": return INNER_LEFT;
        }

        sol = getNeighborPos(face, face.getFirstDirection(), false, face.getSecondDirection(), world, pos);
        switch (sol) {
            case "right" -> shape = FIRST_OUTER_RIGHT;
            case "left" -> shape = FIRST_OUTER_LEFT;
        }

        sol = getNeighborPos(face, face.getSecondDirection(), false, face.getFirstDirection(), world, pos);
        switch (sol) {
            case "right" -> {
                if (shape.equals(STRAIGHT)) shape = SECOND_OUTER_RIGHT;
                else if (shape.equals(FIRST_OUTER_RIGHT)) shape = OUTER_RIGHT;
            }
            case "left" -> {
                if (shape.equals(STRAIGHT)) shape = SECOND_OUTER_LEFT;
                else if (shape.equals(FIRST_OUTER_LEFT)) shape = OUTER_LEFT;
            }
        }

        return shape;
    }

    public static String getNeighborPos(Edge face, Direction direction, Boolean reverse, Direction reference, BlockView world, BlockPos pos) {
        BlockState block_state = world.getBlockState(
            pos.offset(reverse ? direction.getOpposite() : direction)
        );

        if (isStair(block_state) && block_state.get(EDGE).hasDirection(reference)) {
            if (block_state.get(EDGE).hasDirection(face.getLeftDirection())) return "left";
            else if (block_state.get(EDGE).hasDirection(face.getRightDirection())) return "right";
        }
        return "";
    }

    public static boolean isStair(BlockState state) {
        return state.getBlock() instanceof ReFramedStairBlock
            || state.getBlock() instanceof ReFramedStairsCubeBlock;
    }

    public static ActionResult useCamo(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, int theme_index) {
        if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);
        // Changing the theme
        if(held.getItem() instanceof BlockItem block_item && block_entity.getTheme(theme_index).getBlock() == Blocks.AIR) {
            Block block = block_item.getBlock();
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
            BlockState placement_state = block.getPlacementState(ctx);
            if(placement_state != null && Block.isShapeFullCube(placement_state.getCollisionShape(world, pos)) && !(block instanceof BlockEntityProvider)) {
                List<BlockState> themes = block_entity.getThemes();
                if(!world.isClient) block_entity.setTheme(placement_state, theme_index);

                // check for default light emission
                if (placement_state.getLuminance() > 0
                    && themes.stream().noneMatch(theme -> theme.getLuminance() > 0)
                    && !block_entity.emitsLight()
                )
                    block_entity.toggleLight();

                world.setBlockState(pos, state.with(LIGHT, block_entity.emitsLight()));

                // check for default redstone emission
                if (placement_state.getWeakRedstonePower(world, pos, Direction.NORTH) > 0
                    && themes.stream().noneMatch(theme -> theme.getWeakRedstonePower(world, pos, Direction.NORTH) > 0)
                    && !block_entity.emitsRedstone()
                ) block_entity.toggleRedstone();

                if(!player.isCreative()) held.decrement(1);
                world.playSound(player, pos, placement_state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1.1f);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    public static ActionResult useUpgrade(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return ActionResult.PASS;

        ItemStack held = player.getStackInHand(hand);
        ReframedInteractible ext = state.getBlock() instanceof ReframedInteractible e ? e : ReframedInteractible.Default.INSTANCE;

        // frame will emit light if applied with glowstone
        if(state.contains(LIGHT) && held.getItem() == Items.GLOWSTONE_DUST) {
            block_entity.toggleLight();
            world.setBlockState(pos, state.with(LIGHT, block_entity.emitsLight()));
            world.playSound(player, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        // frame will emit redstone if applied with redstone torch can deactivate redstone block camo emission
        if(held.getItem() == Items.REDSTONE_TORCH && ext.canAddRedstoneEmission(state, world, pos)) {
            block_entity.toggleRedstone();
            world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        // Frame will lose its collision if applied with popped chorus fruit
        if(held.getItem() == Items.POPPED_CHORUS_FRUIT && ext.canRemoveCollision(state, world, pos)) {
            block_entity.toggleSolidity();
            world.playSound(player, pos, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static boolean cursorMatchesFace(VoxelShape shape, Vec3d pos) {
        Map<Direction.Axis, Double> axes = Arrays.stream(Direction.Axis.values())
            .collect(Collectors.toMap(
                x -> x,
                x -> x.choose(pos.getX(), pos.getY(), pos.getZ())
            ));

        return shape.getBoundingBoxes().stream()
            .anyMatch(box ->
                axes.keySet().stream()
                    .map(x -> box.getMin(x) <= axes.get(x) && box.getMax(x) >= axes.get(x))
                    .reduce((prev, current) -> prev && current).orElse(false)
            );
    }
}
