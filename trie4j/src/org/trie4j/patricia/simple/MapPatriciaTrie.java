/*
 * Copyright 2012 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trie4j.patricia.simple;

import java.io.Serializable;
import java.util.Map;

import org.trie4j.MapTrie;
import org.trie4j.util.Pair;

public class MapPatriciaTrie<T>
extends PatriciaTrie
implements Serializable, MapTrie<T>{
	@Override
	@SuppressWarnings("unchecked")
	public MapNode<T> getRoot(){
		return (MapNode<T>)super.getRoot();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T insert(String text, T value){
		MapNode<T> node = (MapNode<T>)insert(getRoot(), text, 0);
		T ret = node.getValue();
		node.setValue(value);
		return ret;
	}

	@Override
	public T get(String word) {
		MapNode<T> node = getNode(word);
		if(node == null) return null;
		return node.getValue();
	}

	@Override
	public T put(String word, T value) {
		MapNode<T> node = getNode(word);
		if(node == null) return null;
		T ret = node.getValue();
		node.setValue(value);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public MapNode<T> getNode(String text) {
		return (MapNode<T>)super.getNode(text);
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
	protected MapNode<T> newNode() {
		return new MapNode<T>();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Node newNode(char[] letters, Node source) {
		return new MapNode<T>(letters, source.isTerminate(),
				(MapNode<T>[])source.getChildren(), ((MapNode<T>)source).getValue());
	}

	@Override
	protected MapNode<T> newNode(char[] letters, boolean terminated) {
		return new MapNode<T>(letters, terminated);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected MapNode<T> newNode(char[] letters, boolean terminated, Node[] children) {
		return new MapNode<T>(letters, terminated, (MapNode<T>[])children);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected MapNode<T>[] newNodeArray(Node... nodes){
		MapNode<T>[] ret = new MapNode[nodes.length];
		System.arraycopy(nodes, 0, ret, 0, nodes.length);
		return ret;
	};

	private class Entry implements Map.Entry<String, T>{
		public Entry(String key, MapNode<T> node) {
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
		private MapNode<T> node;
	}

	private class IterableAdapter extends org.trie4j.util.IterableAdapter<Pair<String, Node>, Map.Entry<String, T>>{
		public IterableAdapter(Iterable<Pair<String, Node>> orig){
			super(orig);
		}
		@Override
		@SuppressWarnings("unchecked")
		protected Map.Entry<String, T> convert(Pair<String, Node> value) {
			return new Entry(value.getFirst(), (MapNode<T>)value.getSecond());
		}
	}

	private static final long serialVersionUID = 2165079531157534766L;
}
