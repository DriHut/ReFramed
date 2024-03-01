package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.util.shape.VoxelShapes.empty;
import static net.minecraft.util.shape.VoxelShapes.fullCube;

public abstract class ReFramedDoubleBlock extends ReFramedBlock {
    public ReFramedDoubleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ReFramed.REFRAMED_DOUBLE_BLOCK_ENTITY.instantiate(pos, state);
    }

    protected int getHitPart(BlockState state, BlockHitResult hit) {
        Direction side = hit.getSide();
        VoxelShape first_shape = getShape(state, 1);
        VoxelShape second_shape = getShape(state, 2);

        // Determine if any of the two shape is covering the side entirely
        if (isFaceFullSquare(first_shape, side)) return 1;
        if (isFaceFullSquare(second_shape, side)) return 2;

        Vec3d pos = hit.getPos();
        BlockPos origin = hit.getBlockPos();
        Map<Direction.Axis, Double> axes = Arrays.stream(Direction.Axis.values())
            .filter(axis -> axis != side.getAxis())
            .collect(Collectors.toMap(
                axis -> axis,
                axis -> axis.choose(pos.getX() - origin.getX(), pos.getY() - origin.getY(), pos.getZ() - origin.getZ()))
            );

        if (matchesFace(first_shape.getFace(side), axes)) return 1;
        if (matchesFace(second_shape.getFace(side), axes)) return 2;
        return 0;
    }

    private static boolean matchesFace(VoxelShape shape, Map<Direction.Axis, Double> axes) {
        return shape.getBoundingBoxes().stream()
            .anyMatch(box ->
                axes.keySet().stream()
                    .map(axis -> box.getMin(axis) <= axes.get(axis) && box.getMax(axis) >= axes.get(axis))
                    .reduce((prev, current) -> prev && current).get()
            );
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity
            && framed_entity.getThemes().stream().allMatch(theme -> theme.isTransparent(world, pos));
    }

    public VoxelShape getRenderOutline(BlockState state, BlockHitResult hit) {
        return getShape(state, getHitPart(state, hit));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return isGhost(view, pos) ? empty() : fullCube();
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return isGhost(view, pos) ? empty() : fullCube();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!canUse(world, pos, player)) return superUse(state, world, pos, player, hand, hit);
        ActionResult result = useUpgrade(state, world, pos, player, hand);
        if (result.isAccepted()) return result;
        return useCamo(state, world, pos, player, hand, hit, getHitPart(state, hit));
    }
}
