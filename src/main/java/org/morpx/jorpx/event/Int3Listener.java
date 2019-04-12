package org.morpx.jorpx.event;

@FunctionalInterface
public interface Int3Listener extends Listener {
	public void onInt3(int a, int b, int c);
}
