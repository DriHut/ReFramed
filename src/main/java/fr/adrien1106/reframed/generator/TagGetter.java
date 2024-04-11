package fr.adrien1106.reframed.generator;

import net.minecraft.block.Block;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public interface TagGetter {

    List<TagKey<Block>> getTags();
}
