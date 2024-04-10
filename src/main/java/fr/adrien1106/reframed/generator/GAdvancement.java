package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class GAdvancement extends FabricAdvancementProvider {
    protected GAdvancement(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        Advancement.Builder builder = Advancement.Builder.create()
            .display(
                Items.CAKE,
                Text.literal("Is Everything A Lie ?"),
                Text.translatable("advancements.reframed.description"),
                new Identifier("textures/gui/advancements/backgrounds/adventure.png"),
                AdvancementFrame.TASK,
                true,
                true,
                false
            ).rewards(AdvancementRewards.Builder.experience(1000));
        ReFramed.BLOCKS.forEach(block ->
            builder.criterion(
                "get_" + Registries.BLOCK.getId(block).getPath(),
                InventoryChangedCriterion.Conditions.items(block)
            )
        );
        builder.build(consumer, ReFramed.MODID + "/root");
    }
}
