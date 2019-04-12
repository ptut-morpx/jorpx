package org.morpx.jorpx.event;

import java.util.function.IntConsumer;

@FunctionalInterface
public interface IntListener extends Listener {
	public void onInt(int value);
	
	public static IntListener convert(IntConsumer ic) {
		return val -> ic.accept(val);
	}
}
