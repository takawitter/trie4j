/*
 * Copyright 2013 Takao Nakaguchi
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

	interface ComparableEntry<T, U> extends Map.Entry<T, U>, Comparable<ComparableEntry<T, U>>{}

	@Override
	public Set<Map.Entry<String, T>> entrySet() {
		Set<Map.Entry<String, T>> ret = new TreeSet<Map.Entry<String,T>>();
		for(final String s : trie.predictiveSearch("")){
			final T v = trie.get(s);
			ret.add(new ComparableEntry<String, T>() {
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
				
				@Override
				public int compareTo(ComparableEntry<String, T> o) {
					return s.compareTo(o.getKey());
				}
			});
		}
		return Collections.unmodifiableSet(ret);
	}
	
	private MapTrie<T> trie;
}
