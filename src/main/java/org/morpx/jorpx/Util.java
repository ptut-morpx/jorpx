package org.morpx.jorpx;

import java.util.Collection;
import java.util.function.Predicate;

public class Util {
	public static class StringUtil {
		public static String between(String src, String before, String after) {
			if(src.contains(before)) {
				src=src.split(before, 2)[1];
			}
			if(src.contains(after)) {
				src=src.split(after)[0];
			}
			return src;
		}
		public static String escape(String src) {
			return src
					.replace("\\", "\\\\")
					.replace("\'", "\\\'")
					.replace("\"", "\\\"")
					.replace("\n", "\\n")
					.replace("\r", "\\r")
					.replace("\t", "\\t")
					.replace("\b", "\\v")
					.replace("\f", "\\f")
					;
		}
	}
	public static class CollectionUtil {
		public static <T> T find(Collection<T> col, Predicate<T> pre){
			for(T i:col) {
				if(pre.test(i)) return i;
			}
			return null;
		}
	}
}
