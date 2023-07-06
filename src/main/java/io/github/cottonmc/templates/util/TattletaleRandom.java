package io.github.cottonmc.templates.util;

import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSplitter;

public class TattletaleRandom implements Random {
	public TattletaleRandom(Random delegate) {
		this.delegate = delegate;
	}
	
	private final Random delegate;
	public boolean wasUsed = false;
	
	public Random split() {
		wasUsed = true;
		return delegate.split();
	}
	
	public RandomSplitter nextSplitter() {
		wasUsed = true;
		return delegate.nextSplitter();
	}
	
	public void setSeed(long l) {
		wasUsed = true;
		delegate.setSeed(l);
	}
	
	public int nextInt() {
		wasUsed = true;
		return delegate.nextInt();
	}
	
	public int nextInt(int i) {
		wasUsed = true;
		return delegate.nextInt(i);
	}
	
	public int nextBetween(int i, int j) {
		wasUsed = true;
		return delegate.nextBetween(i, j);
	}
	
	public long nextLong() {
		wasUsed = true;
		return delegate.nextLong();
	}
	
	public boolean nextBoolean() {
		wasUsed = true;
		return delegate.nextBoolean();
	}
	
	public float nextFloat() {
		wasUsed = true;
		return delegate.nextFloat();
	}
	
	public double nextDouble() {
		wasUsed = true;
		return delegate.nextDouble();
	}
	
	public double nextGaussian() {
		wasUsed = true;
		return delegate.nextGaussian();
	}
	
	public double nextTriangular(double d, double e) {
		wasUsed = true;
		return delegate.nextTriangular(d, e);
	}
	
	public void skip(int i) {
		wasUsed = true;
		delegate.skip(i);
	}
	
	public int nextBetweenExclusive(int i, int j) {
		wasUsed = true;
		return delegate.nextBetweenExclusive(i, j);
	}
}
