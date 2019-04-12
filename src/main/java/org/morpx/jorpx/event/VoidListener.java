package org.morpx.jorpx.event;

@FunctionalInterface
public interface VoidListener extends Listener {
	public void onVoid();
	
	public static VoidListener convert(Runnable r) {
		return () -> r.run();
	}
}
