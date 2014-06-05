package org.trie4j.patricia;

import java.lang.reflect.Array;
import java.util.Map;

import org.trie4j.MapNode;
import org.trie4j.MapTrie;
import org.trie4j.tail.builder.TailBuilder;
import org.trie4j.util.Pair;

public class MapTailPatriciaTrie<T>
extends TailPatriciaTrie
implements MapTrie<T>{
	public MapTailPatriciaTrie() {
	}

	public MapTailPatriciaTrie(TailBuilder tailBuilder) {
		super(tailBuilder);
	}

	@Override
	@SuppressWarnings("unchecked")
	public MapNode<T> getRoot(){
		return new MapTailPatriciaTrieNodeAdapter<T>(
				(MapTailPatriciaTrieNode<T>)((TailPatriciaTrieNodeAdapter)super.getRoot()).getNode(),
				getTails());
	}

	@Override
	@SuppressWarnings("unchecked")
	public T insert(String text, T value){
		MapTailPatriciaTrieNode<T> node = (MapTailPatriciaTrieNode<T>)insert(
				((TailPatriciaTrieNodeAdapter)super.getRoot()).getNode(), text, 0);
		T ret = node.getValue();
		node.setValue(value);
		return ret;
	}

	@Override
	public T get(String word) {
		MapTailPatriciaTrieNode<T> node = getNode(word);
		if(node == null) return null;
		return node.getValue();
	}

	@Override
	public T put(String word, T value) {
		MapTailPatriciaTrieNode<T> node = getNode(word);
		if(node == null) return null;
		T ret = node.getValue();
		node.setValue(value);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public MapTailPatriciaTrieNode<T> getNode(String text) {
		return (MapTailPatriciaTrieNode<T>)super.getNode(text);
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
	protected MapTailPatriciaTrieNode<T> newNode() {
		return new MapTailPatriciaTrieNode<T>((char)0xffff, -1, false, newNodeArray());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected MapTailPatriciaTrieNode<T> newNode(char firstChar, int tailIndex, TailPatriciaTrieNode source) {
		return new MapTailPatriciaTrieNode<T>(firstChar, tailIndex, source.isTerminate(),
				(MapTailPatriciaTrieNode<T>[])source.getChildren(),
				((MapTailPatriciaTrieNode<T>)source).getValue());
	}

	@Override
	protected MapTailPatriciaTrieNode<T> newNode(char firstChar, int tailIndex, boolean terminated) {
		return new MapTailPatriciaTrieNode<T>(firstChar, tailIndex, terminated, newNodeArray());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected MapTailPatriciaTrieNode<T>[] newNodeArray(TailPatriciaTrieNode... nodes){
		MapTailPatriciaTrieNode<T>[] ret = (MapTailPatriciaTrieNode<T>[])Array.newInstance(
				MapTailPatriciaTrieNode.class, nodes.length);
		System.arraycopy(nodes, 0, ret, 0, nodes.length);
		return ret;
	};

	private class Entry implements Map.Entry<String, T>{
		public Entry(String key, MapTailPatriciaTrieNode<T> node) {
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
		private MapTailPatriciaTrieNode<T> node;
	}

	private class IterableAdapter extends org.trie4j.util.IterableAdapter<Pair<String, TailPatriciaTrieNode>, Map.Entry<String, T>>{
		public IterableAdapter(Iterable<Pair<String, TailPatriciaTrieNode>> orig){
			super(orig);
		}
		@Override
		@SuppressWarnings("unchecked")
		protected Map.Entry<String, T> convert(Pair<String, TailPatriciaTrieNode> value) {
			return new Entry(value.getFirst(), (MapTailPatriciaTrieNode<T>)value.getSecond());
		}
	}

	private static final long serialVersionUID = 6439189542310830789L;
}
