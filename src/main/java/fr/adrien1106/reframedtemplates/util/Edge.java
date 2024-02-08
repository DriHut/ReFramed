package fr.adrien1106.reframedtemplates.util;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import org.joml.Vector3d;

import java.util.Locale;

public enum Edge implements StringIdentifiable {
	DOWN_NORTH,
	DOWN_SOUTH,
	UP_SOUTH,
	UP_NORTH,
	NORTH_WEST,
 	SOUTH_WEST,
	SOUTH_EAST,
	NORTH_EAST,
	DOWN_WEST,
	UP_WEST,
	UP_EAST,
	DOWN_EAST;
	
	public static Edge stairslikePlacement(ItemPlacementContext ctx) {
		Direction playerHorizontalFacing = ctx.getHorizontalPlayerFacing();
		Direction clickedFace = ctx.getSide();
		boolean sneaky = ctx.getPlayer() != null && ctx.getPlayer().isSneaky();
		
		double dx = ctx.getHitPos().x - ctx.getBlockPos().getX();
		double dy = ctx.getHitPos().y - ctx.getBlockPos().getY();
		double dz = ctx.getHitPos().z - ctx.getBlockPos().getZ();
		
		if(clickedFace == Direction.UP || (!sneaky && dy <= 0.5)) return switch(playerHorizontalFacing) {
			case NORTH -> DOWN_NORTH;
			case EAST -> DOWN_EAST;
			case SOUTH -> DOWN_SOUTH;
			case WEST -> DOWN_WEST;
			default -> throw new IllegalArgumentException();
		};
		
		else if(clickedFace == Direction.DOWN || (!sneaky && dy >= 0.5)) return switch(playerHorizontalFacing) {
			case NORTH -> UP_NORTH;
			case EAST -> UP_EAST;
			case SOUTH -> UP_SOUTH;
			case WEST -> UP_WEST;
			default -> throw new IllegalArgumentException();
		};
		
		else return switch(playerHorizontalFacing) {
			case NORTH -> dx < 0.5 ? Edge.NORTH_WEST : Edge.NORTH_EAST;
			case EAST -> dz < 0.5 ? Edge.NORTH_EAST : Edge.SOUTH_EAST;
			case SOUTH -> dx > 0.5 ? Edge.SOUTH_EAST : Edge.SOUTH_WEST;
			case WEST -> dz > 0.5 ? Edge.SOUTH_WEST : Edge.NORTH_WEST;
			default -> throw new IllegalArgumentException();
		};
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
}
