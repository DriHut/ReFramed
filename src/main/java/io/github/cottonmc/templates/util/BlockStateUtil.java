package io.github.cottonmc.templates.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class BlockStateUtil {
	public static BlockState fromTag(CompoundTag tag) {
		Block block = Registry.BLOCK.get(new Identifier(tag.getString("Block")));
		CompoundTag properties = tag.getCompound("Properties");
		StateFactory<Block, BlockState> factory = block.getStateFactory();
		BlockState state = factory.getDefaultState();
		for (String key : properties.getKeys()) {
			Property<?> prop = factory.getProperty(key);
			if (prop != null) state = parseProperty(state, prop, properties.getString(key));
		}
		return state;
	}

	public static CompoundTag toTag(CompoundTag tag, BlockState state) {
		tag.putString("Block", Registry.BLOCK.getId(state.getBlock()).toString());
		CompoundTag properties = new CompoundTag();
		for (Property<?> prop : state.getProperties()) {
			String value = state.get(prop).toString();
			properties.putString(prop.getName(), value);
		}
		tag.put("Properties", properties);
		return tag;
	}

	public static <T extends Comparable<T>> BlockState parseProperty(BlockState state, Property<T> property, String value) {
		Optional<T> optional = property.getValue(value);
		if (optional.isPresent()) {
			state = state.with(property, optional.get());
		}
		return state;
	}
}
