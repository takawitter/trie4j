package org.trie4j.tail;

import java.util.NoSuchElementException;

public class TailCharIterator{
	public TailCharIterator(CharSequence chars, int index){
		this.chars = chars;
		this.index = index;
		if(index != -1){
			this.next = chars.charAt(index);
		} else{
			this.next = '\0';
		}
		this.fetched = true;
		this.current = this.next;
	}

	public int getNextIndex(){
		return index;
	}

	public boolean hasNext() {
		if(!fetched) fetchNext();
		return next > '\0';
	}

	public char next() {
		if(!fetched) fetchNext();
		if(next == '\0'){
			throw new NoSuchElementException(); 
		}
		fetched = false;
		return next;
	}

	public char current(){
		return current;
	}

	private void fetchNext(){
		index++;
		char c = chars.charAt(index);
		if(c == '\1'){
			int i = chars.charAt(index + 1);
			i += chars.charAt(index + 2) << 16;
			index = i;
			c = chars.charAt(index);
		}
		current = next;
		next = c;
		fetched = true;
	}

	private CharSequence chars;
	private int index;
	private char next = '\0';
	private char current = '\0';
	private boolean fetched;
}
