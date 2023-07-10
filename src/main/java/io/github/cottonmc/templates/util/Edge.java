package io.github.cottonmc.templates.util;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.Locale;

public enum Edge implements StringIdentifiable {
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
	
	Edge(AxisRelation x, AxisRelation y, AxisRelation z) {
		this.key = key(x, y, z);
	}
	
	private final byte key;
	
	private static final Byte2ObjectMap<Edge> LOOKUP = new Byte2ObjectOpenHashMap<>();
	static { for(Edge e : values()) LOOKUP.put(e.key, e); }
	
	private static byte key(AxisRelation x, AxisRelation y, AxisRelation z) {
		return (byte) (x.ordinal() + 1 << 4 | y.ordinal() + 1 << 2 | z.ordinal() + 1);
	}
	
	public static Edge closestTo(Vec3d precise, BlockPos coarse) {
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
		
		//distances your click was from either pair of edges (each 0..5)
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
	
	//I may have skill issue
	//Beep boop i am very maintainable yes
	public record CoordinateFrame(Vector3d origin, Vector3d along, Vector3d a, Vector3d b) {}
	private static final Vector3d ZR = new Vector3d();
	private static final Vector3d PX = new Vector3d(1, 0, 0);
	private static final Vector3d NX = new Vector3d(-1, 0, 0);
	private static final Vector3d PY = new Vector3d(0, 1, 0);
	private static final Vector3d NY = new Vector3d(0, -1, 0);
	private static final Vector3d PZ = new Vector3d(0, 0, 1);
	private static final Vector3d NZ = new Vector3d(0, 0, -1);
	public CoordinateFrame makeCoordinateFrame() {
		return switch(this) {
			case DOWN_NORTH -> new CoordinateFrame(ZR, PX, PZ, PY);
			case DOWN_EAST -> new CoordinateFrame(PX, PZ, NX, PY);
			case DOWN_SOUTH -> new CoordinateFrame(new Vector3d(1, 0, 1), NX, NZ, PY);
			case DOWN_WEST -> new CoordinateFrame(PZ, NZ, PX, PY);
			case UP_NORTH -> new CoordinateFrame(PY, PX, PZ, NY);
			case UP_EAST -> new CoordinateFrame(new Vector3d(1, 1, 0), PZ, NX, NY);
			case UP_SOUTH -> new CoordinateFrame(new Vector3d(1, 1, 1), NX, NZ, NY);
			case UP_WEST -> new CoordinateFrame(new Vector3d(0, 1, 1), NZ, PX, NY);
			case NORTH_WEST -> new CoordinateFrame(ZR, PY, PZ, PX);
			case NORTH_EAST -> new CoordinateFrame(PX, PY, NX, PZ);
			case SOUTH_EAST -> new CoordinateFrame(new Vector3d(1, 0, 1), PY, NZ, NX);
			case SOUTH_WEST -> new CoordinateFrame(PZ, PY, PX, NZ);
		};
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
