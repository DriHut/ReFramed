package fr.adrien1106.reframedtemplates.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.MathHelper;

record QuadUvBounds(float minU, float maxU, float minV, float maxV) {
	static QuadUvBounds read(QuadView quad) {
		float u0 = quad.u(0); float u1 = quad.u(1); float u2 = quad.u(2); float u3 = quad.u(3);
		float v0 = quad.v(0); float v1 = quad.v(1); float v2 = quad.v(2); float v3 = quad.v(3);
		return new QuadUvBounds(
			Math.min(Math.min(u0, u1), Math.min(u2, u3)),
			Math.max(Math.max(u0, u1), Math.max(u2, u3)),
			Math.min(Math.min(v0, v1), Math.min(v2, v3)),
			Math.max(Math.max(v0, v1), Math.max(v2, v3))
		);
	}
	
	boolean displaysSprite(Sprite sprite) {
		return sprite.getMinU() <= minU && sprite.getMaxU() >= maxU && sprite.getMinV() <= minV && sprite.getMaxV() >= maxV;
	}
	
	void normalizeUv(MutableQuadView quad, Sprite specialSprite) {
		float remappedMinU = norm(minU, specialSprite.getMinU(), specialSprite.getMaxU());
		float remappedMaxU = norm(maxU, specialSprite.getMinU(), specialSprite.getMaxU());
		float remappedMinV = norm(minV, specialSprite.getMinV(), specialSprite.getMaxV());
		float remappedMaxV = norm(maxV, specialSprite.getMinV(), specialSprite.getMaxV());
		quad.uv(0, MathHelper.approximatelyEquals(quad.u(0), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(0), minV) ? remappedMinV : remappedMaxV);
		quad.uv(1, MathHelper.approximatelyEquals(quad.u(1), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(1), minV) ? remappedMinV : remappedMaxV);
		quad.uv(2, MathHelper.approximatelyEquals(quad.u(2), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(2), minV) ? remappedMinV : remappedMaxV);
		quad.uv(3, MathHelper.approximatelyEquals(quad.u(3), minU) ? remappedMinU : remappedMaxU, MathHelper.approximatelyEquals(quad.v(3), minV) ? remappedMinV : remappedMaxV);
	}
	
	static float norm(float value, float low, float high) {
		float value2 = MathHelper.clamp(value, low, high);
		return (value2 - low) / (high - low);
	}
	
	//static float rangeRemap(float value, float low1, float high1, float low2, float high2) {
	//	float value2 = MathHelper.clamp(value, low1, high1);
	//	return low2 + (value2 - low1) * (high2 - low2) / (high1 - low1);
	//}
}
