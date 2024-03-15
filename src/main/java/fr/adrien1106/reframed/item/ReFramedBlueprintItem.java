package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedEntity;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
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

public class ReFramedBlueprintItem extends Item implements RecipeSetter {
    public ReFramedBlueprintItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)
            || frame_entity.getThemes().stream().noneMatch(state -> state.getBlock() != Blocks.AIR)
        ) return ActionResult.PASS;

        context.getStack().decrement(1);
        ItemStack stack = ReFramed.BLUEPRINT_WRITTEN.getDefaultStack();
        frame_entity.setStackNbt(stack);
        context.getPlayer().giveItemStack(stack);
        world.playSound(context.getPlayer(), context.getPlayer().getBlockPos(), SoundEvents.ITEM_BOOK_PUT, SoundCategory.PLAYERS);

        return ActionResult.SUCCESS;
    }

    @Override
    public void setRecipe(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this, 3)
            .pattern("PI")
            .pattern("PP")
            .input('P', Items.PAPER)
            .input('I', Items.INK_SAC)
            .criterion(FabricRecipeProvider.hasItem(Items.PAPER), FabricRecipeProvider.conditionsFromItem(Items.PAPER))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }
}
