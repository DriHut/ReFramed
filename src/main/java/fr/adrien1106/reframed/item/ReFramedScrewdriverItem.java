package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.BlockState;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ReFramedScrewdriverItem extends Item implements RecipeSetter {

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

    @Override
    public void setRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.TOOLS, this)
            .pattern("  I")
            .pattern(" I ")
            .pattern("C  ")
            .input('I', Items.IRON_INGOT)
            .input('C', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }
}
