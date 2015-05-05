package org.trie4j.patricia;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.trie4j.tail.TailCharIterator;

public class MapTailPatriciaTrieNode<T>
extends org.trie4j.patricia.TailPatriciaTrieNode
implements Serializable
{
	public MapTailPatriciaTrieNode(char firstChar, int tailIndex, boolean terminated,
			MapTailPatriciaTrieNode<T>[] children) {
		super(firstChar, tailIndex, terminated, children);
	}
	
	public MapTailPatriciaTrieNode(char firstChar, int tailIndex, boolean terminated,
			MapTailPatriciaTrieNode<T>[] children, T value) {
		super(firstChar, tailIndex, terminated, children);
		this.value = value;
	}
	
	public T getValue(){
		return value;
	}

	public void setValue(T value){
		this.value = value;
	}

	@Override
	public char[] getLetters(CharSequence tails) {
		List<Character> letters = new ArrayList<Character>();
		if(getFirstLetter() != (char)0xffff){
			letters.add(getFirstLetter());
		}
		TailCharIterator it = new TailCharIterator(tails, getTailIndex());
		while(it.hasNext()){
			letters.add(it.next());
		}
		char[] ret = new char[letters.size()];
		for(int i = 0; i < ret.length; i++){
			ret[i] = letters.get(i);
		}
		return ret;
	}

	@Override
	@SuppressWarnings("unchecked")
	public MapTailPatriciaTrieNode<T> getChild(char c) {
		return (MapTailPatriciaTrieNode<T>)super.getChild(c);
	}

	@Override
	@SuppressWarnings("unchecked")
	public MapTailPatriciaTrieNode<T>[] getChildren() {
		return (MapTailPatriciaTrieNode<T>[])super.getChildren();
	}

	@Override
	public void setChildren(TailPatriciaTrieNode[] children) {
		super.setChildren(children);
	}

	@Override
	@SuppressWarnings("unchecked")
	public TailPatriciaTrieNode addChild(int index, TailPatriciaTrieNode n){
		MapTailPatriciaTrieNode<T>[] newc = (MapTailPatriciaTrieNode<T>[])Array.newInstance(
				MapTailPatriciaTrieNode.class, getChildren().length + 1);
		System.arraycopy(getChildren(), 0, newc, 0, index);
		newc[index] = (MapTailPatriciaTrieNode<T>)n;
		System.arraycopy(getChildren(), index, newc, index + 1, getChildren().length - index);
		super.setChildren(newc);
		return this;
	}

	private T value;
	private static final long serialVersionUID = 3917921848712069426L;
}
