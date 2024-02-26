package fr.adrien1106.reframed.client.model.apperance;

import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;

public record Appearance(Map<Direction, List<SpriteProperties>> sprites) {}
