package org.morpx.jorpx.struct;

public class Pair<T1, T2> {
	private T1 a;
	private T2 b;
	
	public Pair(T1 a, T2 b) {
		this.a=a;
		this.b=b;
	}
	
	public T1 a() {
		return this.a;
	}
	public T2 b() {
		return this.b;
	}
	
	@Override
	public boolean equals(Object other) {
		if(this==other) return true;
		if(!(other instanceof Pair)) return false;
		Pair<?, ?> op=(Pair<?, ?>) other;
		return op.a==this.a && op.b==this.b;
	}
	@Override
	public int hashCode() {
		return this.a.hashCode()+this.b.hashCode();
	}
}
