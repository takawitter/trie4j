package org.trie4j.util;

public class Pair<T, U> {
	public static <T, U> Pair<T, U> create(T first, U second){
		return new Pair<T, U>(first, second);
	}

	public Pair(T first, U second) {
		this.first = first;
		this.second = second;
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

	private T first;
	private U second;
}
