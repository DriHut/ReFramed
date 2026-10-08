package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class UnbakedSlopeModel extends UnbakedRetexturedModel {

    private static final Mesh[] SLOPE_MESHES = new Mesh[12];
    private static Mesh ITEM_MESH;

    public UnbakedSlopeModel(net.minecraft.util.Identifier parent) {
        super(parent);
        this.item_state = Blocks.AIR.getDefaultState();
    }

    @Override
    public BakedModel bake(
            Baker baker,
            Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings bakeSettings
    ) {
        initializeMeshes();

        return new RetexturingBakedModel(
                baker.bake(parent, bakeSettings),
                ReFramedClient.HELPER.getCamoAppearanceManager(textureGetter),
                theme_index,
                bakeSettings,
                item_state
        ) {
            @Override
            protected Mesh convertModel(BlockState state) {
                if (state == null || state.isAir()) {
                    return ITEM_MESH;
                }
                return SLOPE_MESHES[state.get(EDGE).getID()];
            }
        };
    }

    private static void initializeMeshes() {
        if (SLOPE_MESHES[0] != null) return;

        Mesh baseMesh = SlopeFullMesh.getBaseMesh();
        ITEM_MESH = baseMesh;
        Renderer renderer = ReFramedClient.HELPER.getFabricRenderer();

        for (Edge edge : Edge.values()) {
            Direction targetBase;
            Direction targetBack;

            switch (edge) {
                case DOWN_SOUTH -> { targetBase = Direction.DOWN; targetBack = Direction.NORTH; }
                case NORTH_DOWN -> { targetBase = Direction.DOWN; targetBack = Direction.SOUTH; }
                case DOWN_EAST -> { targetBase = Direction.DOWN; targetBack = Direction.EAST; }
                case WEST_DOWN -> { targetBase = Direction.DOWN; targetBack = Direction.WEST; }
                case SOUTH_UP -> { targetBase = Direction.UP; targetBack = Direction.NORTH; }
                case UP_NORTH -> { targetBase = Direction.UP; targetBack = Direction.SOUTH; }
                case EAST_UP -> { targetBase = Direction.UP; targetBack = Direction.WEST; }
                case UP_WEST -> { targetBase = Direction.UP; targetBack = Direction.EAST; }
                case EAST_SOUTH -> { targetBase = Direction.EAST; targetBack = Direction.NORTH; }
                case SOUTH_WEST -> { targetBase = Direction.WEST; targetBack = Direction.NORTH; }
                case NORTH_EAST -> { targetBase = Direction.WEST; targetBack = Direction.SOUTH; }
                case WEST_NORTH -> { targetBase = Direction.EAST; targetBack = Direction.SOUTH; }
                default -> { targetBase = Direction.DOWN; targetBack = Direction.SOUTH; }
            }

            Matrix4f transform = computeTransformForBaseBack(targetBase, targetBack);

            SLOPE_MESHES[edge.getID()] = transformMesh(renderer, baseMesh, transform, edge);
        }
    }

    private static Matrix4f computeTransformForBaseBack(Direction base, Direction back) {
        Matrix4f matrix = new Matrix4f().identity();

        matrix.mul(getRotationFromDown(base));

        matrix.mul(getRotationAroundAxis(base, back));

        return matrix;
    }

    private static Matrix4f getRotationFromDown(Direction target) {
        Matrix4f m = new Matrix4f().identity();
        return switch (target) {
            case DOWN -> m;
            case UP -> m.rotateX((float) Math.PI);
            case NORTH -> m.rotateX((float) Math.PI / 2f);
            case SOUTH -> m.rotateX(-(float) Math.PI / 2f);
            case WEST -> m.rotateZ(-(float) Math.PI / 2f);
            case EAST -> m.rotateZ((float) Math.PI / 2f);
        };
    }

    private static Matrix4f getRotationAroundAxis(Direction base, Direction back) {
        Direction defaultBack = switch (base) {
            case DOWN -> Direction.SOUTH;
            case UP -> Direction.NORTH;
            case NORTH -> Direction.DOWN;
            case SOUTH -> Direction.UP;
            case WEST -> Direction.SOUTH;
            case EAST -> Direction.SOUTH;
        };

        float angle = computeRotationAngle(base, defaultBack, back);

        Matrix4f m = new Matrix4f().identity();
        if (angle == 0f) return m;

        return switch (base.getAxis()) {
            case X -> m.rotateX(angle);
            case Y -> m.rotateY(angle);
            case Z -> m.rotateZ(angle);
        };
    }

    private static float computeRotationAngle(Direction base, Direction from, Direction to) {
        Direction[] perpendiculars = switch (base) {
            case DOWN, UP -> new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
            case NORTH, SOUTH -> new Direction[]{Direction.UP, Direction.WEST, Direction.DOWN, Direction.EAST};
            case WEST, EAST -> new Direction[]{Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH};
        };

        int fromIndex = -1, toIndex = -1;
        for (int i = 0; i < perpendiculars.length; i++) {
            if (perpendiculars[i] == from) fromIndex = i;
            if (perpendiculars[i] == to) toIndex = i;
        }

        if (fromIndex == -1 || toIndex == -1) return 0f;

        int steps = (toIndex - fromIndex + 4) % 4;
        return steps * (float) Math.PI / 2f;
    }

    private static Mesh transformMesh(Renderer renderer, Mesh source, Matrix4f matrix, Edge edge) {
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        Map<Direction, Direction> faceMap = createFaceMapping(matrix);

        source.forEach(quad -> {
            emitter.copyFrom(quad);

            Vector3f pos = new Vector3f();
            for (int i = 0; i < 4; i++) {
                emitter.copyPos(i, pos);
                pos.sub(0.5f, 0.5f, 0.5f);
                pos.mulPosition(matrix);
                pos.add(0.5f, 0.5f, 0.5f);
                emitter.pos(i, pos.x(), pos.y(), pos.z());
            }

            Direction cullFace = quad.cullFace();
            if (cullFace != null) emitter.cullFace(faceMap.get(cullFace));

            Direction nominalFace = quad.nominalFace();
            if (nominalFace != null) {
                Direction newNominal = faceMap.get(nominalFace);
                emitter.nominalFace(newNominal);
                emitter.tag(newNominal.ordinal() + 1);
            }

            emitter.emit();
        });

        return builder.build();
    }

    private static Map<Direction, Direction> createFaceMapping(Matrix4f matrix) {
        Map<Direction, Direction> map = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            map.put(dir, transformDirection(matrix, dir));
        }
        return map;
    }

    private static Direction transformDirection(Matrix4f matrix, Direction dir) {
        Vector3f vec = dir.getUnitVector();
        vec.mulDirection(matrix);

        Direction closest = Direction.UP;
        float maxDot = -1f;

        for (Direction d : Direction.values()) {
            float dot = vec.dot(d.getUnitVector());
            if (dot > maxDot) {
                maxDot = dot;
                closest = d;
            }
        }

        return closest;
    }
}
