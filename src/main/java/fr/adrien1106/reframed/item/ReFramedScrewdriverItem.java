package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ReFramedScrewdriverItem extends Item {

    public ReFramedScrewdriverItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity)) return ActionResult.PASS;
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        int theme_index = state.getBlock() instanceof ReFramedDoubleBlock b
            ? b.getHitShape(
                state,
                context.getHitPos(),
                context.getBlockPos(),
                context.getSide()
            )
            : 1;


        BlockState theme = frame_entity.getTheme(theme_index);
        if (!theme.contains(Properties.AXIS)) return ActionResult.PASS;

        Direction.Axis axis = theme.get(Properties.AXIS);
        BlockSoundGroup group = theme.getSoundGroup();
        world.playSound(player, pos, group.getPlaceSound(), SoundCategory.BLOCKS, group.getVolume(), group.getPitch());
        frame_entity.setTheme(theme.with(
            Properties.AXIS,
            switch (axis) {
                case X -> Direction.Axis.Y;
                case Y -> Direction.Axis.Z;
                case Z -> Direction.Axis.X;
            }
        ), theme_index);
        ReFramed.chunkRerenderProxy.accept(world, pos);
        return ActionResult.SUCCESS;
    }
}
