package org.trie4j.patricia.tail;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.trie4j.MapTrie;
import org.trie4j.tail.TailBuilder;
import org.trie4j.tail.TailCharIterator;
import org.trie4j.util.Pair;

public class MapTailPatriciaTrie<T>
extends TailPatriciaTrie
implements MapTrie<T>{
	public class MapNode
	extends org.trie4j.patricia.tail.Node
	implements Serializable, org.trie4j.MapNode<T>
	{
		public MapNode(char firstChar, int tailIndex, boolean terminated, MapNode[] children) {
			super(firstChar, tailIndex, terminated, children);
		}
		
		public MapNode(char firstChar, int tailIndex, boolean terminated, MapNode[] children, T value) {
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
		public char[] getLetters() {
			List<Character> letters = new ArrayList<Character>();
			if(getFirstLetter() != (char)0xffff){
				letters.add(getFirstLetter());
			}
			TailCharIterator it = new TailCharIterator(getTails(), getTailIndex());
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
		public MapNode getChild(char c) {
			return (MapNode)super.getChild(c);
		}

		@Override
		@SuppressWarnings("unchecked")
		public MapNode[] getChildren() {
			return (MapNode[])super.getChildren();
		}

		@Override
		public void setChildren(Node[] children) {
			super.setChildren(children);
		}

		@Override
		@SuppressWarnings("unchecked")
		public Node addChild(int index, Node n){
			MapNode[] newc = (MapNode[])Array.newInstance(MapNode.class, getChildren().length + 1);
			System.arraycopy(getChildren(), 0, newc, 0, index);
			newc[index] = (MapNode)n;
			System.arraycopy(getChildren(), index, newc, index + 1, getChildren().length - index);
			super.setChildren(newc);
			return this;
		}

		private T value;
		private static final long serialVersionUID = 3917921848712069426L;
	}

	public MapTailPatriciaTrie() {
	}

	public MapTailPatriciaTrie(TailBuilder tailBuilder) {
		super(tailBuilder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public MapNode getRoot(){
		return (MapNode)((NodeAdapter)super.getRoot()).getNode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T insert(String text, T value){
		MapNode node = (MapNode)insert(getRoot(), text, 0);
		T ret = node.getValue();
		node.setValue(value);
		return ret;
	}

	@Override
	public T get(String word) {
		MapNode node = getNode(word);
		if(node == null) return null;
		return node.getValue();
	}

	@Override
	public T put(String word, T value) {
		MapNode node = getNode(word);
		if(node == null) return null;
		T ret = node.getValue();
		node.setValue(value);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public MapNode getNode(String text) {
		return (MapNode)super.getNode(text);
	}

	@Override
	public Iterable<Map.Entry<String, T>> commonPrefixSearchEntries(String query){
		return new IterableAdapter(commonPrefixSearchWithNode(query));
	}

	@Override
	public Iterable<Map.Entry<String, T>> predictiveSearchEntries(String prefix) {
		return new IterableAdapter(predictiveSearchWithNode(prefix));
	}

	@Override
	protected MapNode newNode() {
		return new MapNode((char)0xffff, -1, false, newNodeArray());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Node newNode(char firstChar, int tailIndex, Node source) {
		return new MapNode(firstChar, tailIndex, source.isTerminate(),
				(MapNode[])source.getChildren(), ((MapNode)source).getValue());
	}

	@Override
	protected MapNode newNode(char firstChar, int tailIndex, boolean terminated) {
		return new MapNode(firstChar, tailIndex, terminated, newNodeArray());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected MapNode[] newNodeArray(Node... nodes){
		MapNode[] ret = (MapNode[])Array.newInstance(MapNode.class, nodes.length);
		System.arraycopy(nodes, 0, ret, 0, nodes.length);
		return ret;
	};

	private class Entry implements Map.Entry<String, T>{
		public Entry(String key, MapNode node) {
			this.key = key;
			this.node = node;
		}
	
		@Override
		public String getKey() {
			return key;
		}
		public T getValue() {
			return node.getValue();
		}
		@Override
		public T setValue(T value) {
			T ret = node.getValue();
			node.setValue(value);
			return ret;
		}
		private String key;
		private MapNode node;
	}

	private class IterableAdapter extends org.trie4j.util.IterableAdapter<Pair<String, Node>, Map.Entry<String, T>>{
		public IterableAdapter(Iterable<Pair<String, Node>> orig){
			super(orig);
		}
		@Override
		@SuppressWarnings("unchecked")
		protected Map.Entry<String, T> convert(Pair<String, Node> value) {
			return new Entry(value.getFirst(), (MapNode)value.getSecond());
		}
	}

	private static final long serialVersionUID = 6439189542310830789L;
}
