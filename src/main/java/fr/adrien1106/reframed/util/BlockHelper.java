package fr.adrien1106.reframed.util;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.block.ReFramedEntity;
import fr.adrien1106.reframed.util.property.Corner;
import fr.adrien1106.reframed.util.property.StairShape;
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
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.BlockProperties.LIGHT;
import static fr.adrien1106.reframed.util.property.StairShape.*;
import static net.minecraft.util.shape.VoxelShapes.combine;

public class BlockHelper {

    public static Corner getPlacementCorner(ItemPlacementContext ctx) {
        Direction side = ctx.getSide().getOpposite();
        Vec3d pos = getHitPos(ctx.getHitPos(), ctx.getBlockPos());
        Direction.Axis axis = getHitAxis(pos, side);

        Direction part_direction = Direction.from(
            axis,
            axis.choose(pos.x, pos.y, pos.z) > 0
                ? Direction.AxisDirection.POSITIVE
                : Direction.AxisDirection.NEGATIVE
        );

        return Corner.getByDirections(side, part_direction);
    }

    public static Direction.Axis getHitAxis(Vec3d pos, Direction side) {
        Stream<Direction.Axis> axes = Stream.of(Direction.Axis.values()).filter(axis -> !axis.equals(side.getAxis()));
        return axes.reduce((axis_1, axis_2) ->
            Math.abs(axis_1.choose(pos.x, pos.y, pos.z)) > Math.abs(axis_2.choose(pos.x, pos.y, pos.z))
                ? axis_1
                : axis_2
        ).get();
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

    public static StairShape getStairsShape(Block block, Corner face, BlockView world, BlockPos pos) {
        StairShape shape = STRAIGHT;

        String sol = getNeighborPos(face, face.getFirstDirection(), true, face.getSecondDirection(), world, pos, block);
        switch (sol) {
            case "right": return INNER_RIGHT;
            case "left": return INNER_LEFT;
        }

        sol = getNeighborPos(face, face.getSecondDirection(), true, face.getFirstDirection(), world, pos, block);
        switch (sol) {
            case "right": return INNER_RIGHT;
            case "left": return INNER_LEFT;
        }

        sol = getNeighborPos(face, face.getFirstDirection(), false, face.getSecondDirection(), world, pos, block);
        switch (sol) {
            case "right" -> shape = FIRST_OUTER_RIGHT;
            case "left" -> shape = FIRST_OUTER_LEFT;
        }

        sol = getNeighborPos(face, face.getSecondDirection(), false, face.getFirstDirection(), world, pos, block);
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

    public static String getNeighborPos(Corner face, Direction direction, Boolean reverse, Direction reference, BlockView world, BlockPos pos, Block block) {
        BlockState block_state = world.getBlockState(
            pos.offset(reverse ? direction.getOpposite() : direction)
        );

        if (block_state.isOf(block) && block_state.get(CORNER).hasDirection(reference)) {
            if (block_state.get(CORNER).hasDirection(face.getLeftDirection())) return "left";
            else if (block_state.get(CORNER).hasDirection(face.getRightDirection())) return "right";
        }
        return "";
    }

    public static ActionResult useCamo(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, int theme_index) {
        if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return ActionResult.PASS;

        // Changing the theme
        ItemStack held = player.getStackInHand(hand);
        if(held.getItem() instanceof BlockItem block_item && block_entity.getTheme(theme_index).getBlock() == Blocks.AIR) {
            Block block = block_item.getBlock();
            ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
            BlockState placement_state = block.getPlacementState(ctx);
            if(placement_state != null && Block.isShapeFullCube(placement_state.getCollisionShape(world, pos)) && !(block instanceof BlockEntityProvider)) {
                List<BlockState> themes = block_entity.getThemes();
                if(!world.isClient) block_entity.setTheme(placement_state, theme_index);

                // check for default light emission
                if (placement_state.getLuminance() > 0
                    && themes.stream().noneMatch(theme -> theme.getLuminance() > 0))
                    if (block_entity.emitsLight()) Block.dropStack(world, pos, new ItemStack(Items.GLOWSTONE_DUST));
                    else block_entity.toggleLight();

                world.setBlockState(pos, state.with(LIGHT, block_entity.emitsLight()));

                // check for default redstone emission
                if (placement_state.getWeakRedstonePower(world, pos, Direction.NORTH) > 0
                    && themes.stream().noneMatch(theme -> theme.getWeakRedstonePower(world, pos, Direction.NORTH) > 0))
                    if (block_entity.emitsRedstone()) Block.dropStack(world, pos, new ItemStack(Items.GLOWSTONE_DUST));
                    else block_entity.toggleRedstone();

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

            if(!player.isCreative())
                if (block_entity.emitsLight()) held.decrement(1);
                else held.increment(1);
            world.playSound(player, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        // frame will emit redstone if applied with redstone torch can deactivate redstone block camo emission
        if(held.getItem() == Items.REDSTONE_TORCH && ext.canAddRedstoneEmission(state, world, pos)) {
            block_entity.toggleRedstone();

            if(!player.isCreative())
                if (block_entity.emitsRedstone()) held.decrement(1);
                else held.increment(1);
            world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        // Frame will lose its collision if applied with popped chorus fruit
        if(held.getItem() == Items.POPPED_CHORUS_FRUIT && ext.canRemoveCollision(state, world, pos)) {
            block_entity.toggleSolidity();

            if(!player.isCreative())
                if (!block_entity.isSolid()) held.decrement(1);
                else held.increment(1);
            world.playSound(player, pos, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    // Doing this method from scratch as it is simpler to do than injecting everywhere
    public static boolean shouldDrawSide(BlockState self_state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos, int theme_index) {
        ThemeableBlockEntity self = world.getBlockEntity(pos) instanceof ThemeableBlockEntity e ? e : null;
        ThemeableBlockEntity other = world.getBlockEntity(other_pos) instanceof ThemeableBlockEntity e ? e : null;
        BlockState other_state = world.getBlockState(other_pos);

        // normal behaviour
        if (self == null && other == null) return Block.shouldDrawSide(self_state, world, pos, side, other_pos);

        // self is a normal Block
        if (self == null && other_state.getBlock() instanceof ReFramedBlock other_block) {
            VoxelShape self_shape = self_state.getCullingShape(world, pos);
            if (self_shape.isEmpty()) return true;

            int i = 0;
            VoxelShape other_shape = VoxelShapes.empty();
            for (BlockState s: other.getThemes()) {
                i++;
                if (self_state.isSideInvisible(s, side) || s.isOpaque())
                    other_shape = combine(
                        other_shape,
                        other_block
                            .getShape(other_state, i)
                            .getFace(side.getOpposite()),
                        BooleanBiFunction.OR
                    );
            }

            // determine if side needs to be rendered
            return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
        }

        BlockState self_theme = self.getTheme(theme_index);
        // other is normal Block
        if (other == null && self_state.getBlock() instanceof ReFramedBlock self_block) {
            // Transparent is simple if self and the neighbor are invisible don't render side (like default)
            if (self_theme.isSideInvisible(other_state, side)) return false;

            // Opaque is also simple as each model are rendered one by one
            if (other_state.isOpaque()) {
                // no cache section :( because it differs between each instance of the frame
                VoxelShape self_shape = self_block.getShape(self_state, theme_index).getFace(side);
                if (self_shape.isEmpty()) return true;
                VoxelShape other_shape = other_state.getCullingFace(world, other_pos, side.getOpposite());

                // determine if side needs to be rendered
                return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
            }

            return true;
        }

        // Both are frames
        // here both are computed in the same zone as there will necessarily a shape comparison
        if (self_state.getBlock() instanceof ReFramedBlock self_block && other_state.getBlock() instanceof ReFramedBlock other_block) {
            VoxelShape self_shape = self_block.getShape(self_state, theme_index).getFace(side);
            if (self_shape.isEmpty()) return true;

            int i = 0;
            VoxelShape other_shape = VoxelShapes.empty();
            for (BlockState s: other.getThemes()) {
                i++;
                if (self_theme.isSideInvisible(s, side) || s.isOpaque())
                    other_shape = combine(
                        other_shape,
                        other_block
                            .getShape(other_state, i)
                            .getFace(side.getOpposite()),
                        BooleanBiFunction.OR
                    );
            }

            // determine if side needs to be rendered
            return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
        }

        return true;
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

    public static int luminance(BlockState state) {
        return state.contains(LIGHT) && state.get(LIGHT) ? 15 : 0;
    }
}
