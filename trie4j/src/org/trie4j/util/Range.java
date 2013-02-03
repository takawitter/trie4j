package org.trie4j.util;

public class Range {
	public Range() {
	}
	public Range(int start, int end) {
		this.start = start;
		this.end = end;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}

	public int getLength(){
		return end - start;
	}
	public void set(int start, int end){
		this.start = start;
		this.end = end;
	}

	private int start;
	private int end;
}
