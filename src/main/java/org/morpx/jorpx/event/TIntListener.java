package org.morpx.jorpx.event;

@FunctionalInterface
public interface TIntListener<T> extends Listener {
	public void onTInt(T a, int b);
}
