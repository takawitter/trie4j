package org.trie4j.util;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.trie4j.MapTrie;

public class TrieMap<T> extends AbstractMap<String, T>{
	public TrieMap(MapTrie<T> trie) {
		this.trie = trie;
	}

	@Override
	public T get(Object key) {
		return trie.get(key.toString());
	}

	@Override
	public T put(String key, T value) {
		return trie.insert(key, value);
	}

	@Override
	public int size() {
		return trie.size();
	}

	@Override
	public Set<Map.Entry<String, T>> entrySet() {
		Set<Map.Entry<String, T>> ret = new TreeSet<Map.Entry<String,T>>();
		for(final String s : trie.predictiveSearch("")){
			final T v = trie.get(s);
			ret.add(new Map.Entry<String, T>() {
				@Override
				public String getKey() {
					return s;
				}

				@Override
				public T getValue() {
					return v;
				}

				@Override
				public T setValue(T value) {
					throw new UnsupportedOperationException();
				}
			});
		}
		return Collections.unmodifiableSet(ret);
	}
	
	private MapTrie<T> trie;
}
