package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class ReFramedHammerItem extends Item implements RecipeSetter {
    public ReFramedHammerItem(Settings settings) {
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

        if (frame_entity.getTheme(theme_index).getBlock() == Blocks.AIR) return ActionResult.PASS;

        if (!player.isCreative()) {
            player.giveItemStack(new ItemStack(frame_entity.getTheme(theme_index).getBlock()));
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1.1f);
        }
        frame_entity.setTheme(Blocks.AIR.getDefaultState(), theme_index);
        ReFramed.chunkRerenderProxy.accept(world, pos);
        return ActionResult.SUCCESS;
    }

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this)
            .pattern(" CI")
            .pattern(" ~C")
            .pattern("~  ")
            .input('I', Items.IRON_INGOT)
            .input('C', ReFramed.CUBE)
            .input('~', Items.STICK)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }
}
