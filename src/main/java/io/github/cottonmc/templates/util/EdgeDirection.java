package io.github.cottonmc.templates.util;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public enum EdgeDirection implements StringIdentifiable {
	DOWN_NORTH(AxisRelation.PARALLEL, AxisRelation.LOW_SIDE, AxisRelation.LOW_SIDE),
	DOWN_SOUTH(AxisRelation.PARALLEL, AxisRelation.LOW_SIDE, AxisRelation.HIGH_SIDE),
	UP_SOUTH(AxisRelation.PARALLEL, AxisRelation.HIGH_SIDE, AxisRelation.HIGH_SIDE),
	UP_NORTH(AxisRelation.PARALLEL, AxisRelation.HIGH_SIDE, AxisRelation.LOW_SIDE),
	NORTH_WEST(AxisRelation.LOW_SIDE, AxisRelation.PARALLEL, AxisRelation.LOW_SIDE),
 	SOUTH_WEST(AxisRelation.LOW_SIDE, AxisRelation.PARALLEL, AxisRelation.HIGH_SIDE),
	SOUTH_EAST(AxisRelation.HIGH_SIDE, AxisRelation.PARALLEL, AxisRelation.HIGH_SIDE),
	NORTH_EAST(AxisRelation.HIGH_SIDE, AxisRelation.PARALLEL, AxisRelation.LOW_SIDE),
	DOWN_WEST(AxisRelation.LOW_SIDE, AxisRelation.LOW_SIDE, AxisRelation.PARALLEL),
	UP_WEST(AxisRelation.LOW_SIDE, AxisRelation.HIGH_SIDE, AxisRelation.PARALLEL),
	UP_EAST(AxisRelation.HIGH_SIDE, AxisRelation.HIGH_SIDE, AxisRelation.PARALLEL),
	DOWN_EAST(AxisRelation.HIGH_SIDE, AxisRelation.LOW_SIDE, AxisRelation.PARALLEL),
	;
	
	EdgeDirection(AxisRelation x, AxisRelation y, AxisRelation z) {
		this.key = key(x, y, z);
	}
	
	private final byte key;
	
	private static final Byte2ObjectMap<EdgeDirection> LOOKUP = new Byte2ObjectOpenHashMap<>();
	static { for(EdgeDirection e : values()) LOOKUP.put(e.key, e); }
	
	private static byte key(AxisRelation x, AxisRelation y, AxisRelation z) {
		return (byte) (x.ordinal() + 1 << 4 | y.ordinal() + 1 << 2 | z.ordinal() + 1);
	}
	
	public static EdgeDirection guessFromHitResult(Vec3d precise, BlockPos coarse) {
		double dx = precise.x - coarse.getX();
		double dy = precise.y - coarse.getY();
		double dz = precise.z - coarse.getZ();
		
		//distances your click was from the 6 faces of the block (each 0..1)
		float distToLowX = (float) (dx);
		float distToHighX = (float) (1 - dx);
		float distToLowY = (float) (dy);
		float distToHighY = (float) (1 - dy);
		float distToLowZ = (float) (dz);
		float distToHighZ = (float) (1 - dz);
		
		//distances your click was from either pair of edges (each 0..0.5)
		float distToAnyX = Math.min(distToLowX, distToHighX);
		float distToAnyY = Math.min(distToLowY, distToHighY);
		float distToAnyZ = Math.min(distToLowZ, distToHighZ);
		
		//figure out which two differences are the smallest
		AxisRelation clickX, clickY, clickZ;
		if((distToAnyX < distToAnyZ) && (distToAnyY < distToAnyZ)) {
			clickX = lowOrHigh(distToLowX);
			clickY = lowOrHigh(distToLowY);
			clickZ = AxisRelation.PARALLEL;
		} else if((distToAnyX < distToAnyY) && (distToAnyZ < distToAnyY)) {
			clickX = lowOrHigh(distToLowX);
			clickY = AxisRelation.PARALLEL;
			clickZ = lowOrHigh(distToLowZ);
		} else {
			clickX = AxisRelation.PARALLEL;
			clickY = lowOrHigh(distToLowY);
			clickZ = lowOrHigh(distToLowZ);
		}
		
		return LOOKUP.getOrDefault(key(clickX, clickY, clickZ), DOWN_SOUTH);
	}
	
	@Override
	public String asString() {
		return name().toLowerCase(Locale.ROOT);
	}
	
	//if you imagine moving along this edge, your coordinates:
	public enum AxisRelation {
		//change along this axis
		PARALLEL,
		//stay fixed along this axis, with the coordinate being the more negative possibility
		LOW_SIDE,
		//stay fixed along this axis, with the coordinate being the more positive possibility
		HIGH_SIDE
	}
	
	private static AxisRelation lowOrHigh(float distToLow) {
		return (distToLow < 0.5f) ? AxisRelation.LOW_SIDE : AxisRelation.HIGH_SIDE;
	}
}
