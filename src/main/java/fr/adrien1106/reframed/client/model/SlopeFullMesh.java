package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.client.ReFramedClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.Direction;

public final class SlopeFullMesh {

    private SlopeFullMesh() {}

    private static Mesh BASE_MESH;

    public static Mesh getBaseMesh() {
        if (BASE_MESH == null) {
            BASE_MESH = buildMesh();
        }
        return BASE_MESH;
    }

    private static Mesh buildMesh() {
        Renderer renderer = ReFramedClient.HELPER.getFabricRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter e = builder.getEmitter();

        e.square(Direction.DOWN, 0f, 0f, 1f, 1f, 0f);
        e.color(-1, -1, -1, -1);
        e.cullFace(Direction.DOWN);
        e.nominalFace(Direction.DOWN);
        e.tag(Direction.DOWN.ordinal() + 1);
        e.emit();

        e.square(Direction.SOUTH, 0f, 0f, 1f, 1f, 0f);
        e.color(-1, -1, -1, -1);
        e.cullFace(Direction.SOUTH);
        e.nominalFace(Direction.SOUTH);
        e.tag(Direction.SOUTH.ordinal() + 1);
        e.emit();

        e.pos(0, 0f, 0f, 0f);
        e.pos(1, 0f, 0f, 1f);
        e.pos(2, 0f, 1f, 1f);
        e.pos(3, 0f, 0f, 0f);

        e.uv(0, 0f, 1f);
        e.uv(1, 1f, 1f);
        e.uv(2, 1f, 0f);
        e.uv(3, 0f, 1f);

        e.color(-1, -1, -1, -1);
        e.cullFace(Direction.WEST);
        e.nominalFace(Direction.WEST);
        e.tag(Direction.WEST.ordinal() + 1);
        e.emit();

        e.pos(0, 1f, 0f, 0f);
        e.pos(1, 1f, 1f, 1f);
        e.pos(2, 1f, 0f, 1f);
        e.pos(3, 1f, 0f, 0f);

        e.uv(0, 0f, 1f);
        e.uv(1, 1f, 1f);
        e.uv(2, 1f, 0f);
        e.uv(3, 0f, 1f);

        e.color(-1, -1, -1, -1);
        e.cullFace(Direction.EAST);
        e.nominalFace(Direction.EAST);
        e.tag(Direction.EAST.ordinal() + 1);
        e.emit();

        e.pos(0, 0f, 0f, 0f);
        e.pos(1, 0f, 1f, 1f);
        e.pos(2, 1f, 1f, 1f);
        e.pos(3, 1f, 0f, 0f);

        e.uv(0, 0f, 1f);
        e.uv(1, 1f, 1f);
        e.uv(2, 1f, 0f);
        e.uv(3, 0f, 0f);

        e.color(-1, -1, -1, -1);
        e.cullFace(null);
        e.nominalFace(Direction.UP);
        e.tag(Direction.UP.ordinal() + 1);
        e.emit();

        return builder.build();
    }
}