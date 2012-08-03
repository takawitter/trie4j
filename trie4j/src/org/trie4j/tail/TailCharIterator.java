package org.trie4j.tail;

import java.util.NoSuchElementException;

public class TailCharIterator{
	public TailCharIterator(CharSequence chars, int index){
		this.chars = chars;
		this.index = index;
		if(this.index != -1){
			this.next = chars.charAt(index);
		}
	}

	public int getNextIndex(){
		return index;
	}

	public boolean hasNext() {
		return index != -1;
	}

	public char next() {
		if(!hasNext()){
			throw new NoSuchElementException(); 
		}
		current = next;
		index++;
		char c = chars.charAt(index);
		if(c == '\0'){
			index = -1;
		}
		if(c == '\1'){
			int i = chars.charAt(index + 1);
			i += chars.charAt(index + 2) << 16;
			index = i;
			c = chars.charAt(index);
		}
		next = c;
		return current;
	}

	public char current(){
		return current;
	}

	private CharSequence chars;
	private int index;
	private char current = '\0';
	private char next = '\0';
}
