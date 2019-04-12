package org.morpx.jorpx.event;

@FunctionalInterface
public interface Int2Listener extends Listener {
	public void onInt2(int a, int b);
}
