package org.morpx.jorpx.event;

import java.util.function.Consumer;

@FunctionalInterface
public interface TListener<T> extends Listener {
	public void onT(T value);
	
	public static <T> TListener<T> convert(Consumer<T> ic) {
		return val -> ic.accept(val);
	}
}
