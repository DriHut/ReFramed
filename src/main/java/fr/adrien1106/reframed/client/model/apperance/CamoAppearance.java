package fr.adrien1106.reframed.client.model.apperance;

import fr.adrien1106.reframed.client.model.QuadPosBounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class CamoAppearance {
	protected final int id;
	protected final RenderMaterial ao_material;
	protected final RenderMaterial material;

	protected CamoAppearance(RenderMaterial ao_material, RenderMaterial material, int id) {
		this.id = id;

		this.ao_material = ao_material;
		this.material = material;
	}

	public abstract @NotNull List<SpriteProperties> getSprites(Direction dir, int model_id);
	public abstract boolean hasColor(Direction dir, int model_id, int index);

	public @NotNull RenderMaterial getRenderMaterial(boolean ao) {
		return ao && ao_material != null? ao_material : material;
	}

	public int transformQuad(QuadEmitter quad, int i, int quad_index, int model_id, boolean ao, boolean uv_lock) {
		if(quad.tag() == 0) return 0; // Pass the quad through unmodified.

		Direction direction = quad.nominalFace();
		List<SpriteProperties> sprites = getSprites(direction, model_id);
		if (i == -1) i = sprites.size();

		SpriteProperties properties = sprites.get(sprites.size() - i);
		int tag = i + (quad_index << 8);
		i--;
		QuadPosBounds bounds = properties.bounds();

		if (bounds == null) { // sprite applies anywhere e.g. default behaviour
			quad.material(getRenderMaterial(ao));
			quad.spriteBake(
				properties.sprite(),
				MutableQuadView.BAKE_NORMALIZED
					| properties.flags()
					| (uv_lock ? MutableQuadView.BAKE_LOCK_UV : 0)
			);
			quad.tag(tag);
			quad.emit();
			return i;
		}

		// verify if sprite covers the current quad and apply the new size
		QuadPosBounds origin_bounds = QuadPosBounds.read(quad, false);
		if (!bounds.matches(origin_bounds)) return i;

		// apply new quad shape
		quad.material(getRenderMaterial(ao));
		bounds.intersection(origin_bounds, direction.getAxis()).apply(quad, origin_bounds);
		quad.spriteBake( // seems to work without the flags and break with it
			properties.sprite(),
			MutableQuadView.BAKE_NORMALIZED
				| MutableQuadView.BAKE_LOCK_UV
		);
		quad.tag(tag);
		quad.emit();
		return i;
	}

	@Override
	public int hashCode() {
		return id;
	}
}
