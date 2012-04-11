package org.trie4j.util;

public class Trio<T, U, V> {
	public static <T, U, V> Trio<T, U, V> create(T first, U second, V third){
		return new Trio<T, U, V>(first, second, third);
	}

	public Trio(T first, U second, V third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public U getSecond() {
		return second;
	}

	public void setSecond(U second) {
		this.second = second;
	}

	public V getThird() {
		return third;
	}

	public void setThird(V third) {
		this.third = third;
	}

	private T first;
	private U second;
	private V third;
}
