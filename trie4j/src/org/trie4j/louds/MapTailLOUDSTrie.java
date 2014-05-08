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
package org.trie4j.louds;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.trie4j.FilteredNode;
import org.trie4j.IdNode;
import org.trie4j.MapNode;
import org.trie4j.MapTrie;
import org.trie4j.Node;
import org.trie4j.louds.bvtree.LOUDSBvTree;
import org.trie4j.tail.ConcatTailArray;
import org.trie4j.util.Pair;
import org.trie4j.util.SBVIntMap;

public class MapTailLOUDSTrie<T>
implements Externalizable, MapTrie<T>{
	public MapTailLOUDSTrie() {
		trie = new TailLOUDSTrie();
	}
	public MapTailLOUDSTrie(MapTrie<T> orig){
		trie = new TailLOUDSTrie();
		trie.build(orig, new LOUDSBvTree(orig.size() * 2),
				new ConcatTailArray(orig.size() * 3),
				new AbstractTailLOUDSTrie.NodeListener() {
					@Override
					public void listen(Node node) {
						if(node.isTerminate()){
							values.addValue((T)((MapNode)node).getValue());
						} else{
							values.addNone();
						}
					}
				});
	}

	@Override
	public boolean contains(String word) {
		return trie.contains(word);
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		return trie.commonPrefixSearch(query);
	}

	@Override
	public int findWord(CharSequence chars, int start, int end,
			StringBuilder word) {
		return trie.findWord(chars, start, end, word);
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		return trie.predictiveSearch(prefix);
	}

	@Override
	public void insert(String word) {
		trie.insert(word);
	}

	@Override
	public int size() {
		return trie.size();
	}

	@Override
	public void trimToSize() {
		trie.trimToSize();
	}

	@Override
	public void dump(Writer writer) throws IOException {
		trie.dump(writer);
	}

	@Override
	public void freeze() {
		trie.freeze();
	}

	public class MapNodeAdapter extends FilteredNode implements MapNode<T>{
		public MapNodeAdapter(IdNode orig){
			super(orig);
			this.id = orig.getId();
		}

		@Override
		public MapNode<T> getChild(char c) {
			return new MapNodeAdapter((IdNode)super.getChild(c));
		}

		@Override
		public MapNode<T>[] getChildren() {
			Node[] orig = super.getChildren();
			MapNode<T>[] ret = new MapNode[orig.length];
			for(int i = 0; i < ret.length; i++){
				ret[i] = new MapNodeAdapter((IdNode)orig[i]);
			}
			return ret;
		}

		@Override
		public T getValue() {
			return values.get(id);
		}

		@Override
		public void setValue(T value) {
			values.set(id, value);
		}

		private int id;
	}
	@Override
	public MapNode<T> getRoot() {
		return new MapNodeAdapter(trie.getRoot());
	}

	@Override
	public T get(String text) {
		int id = trie.getIdFor(text);
		if(id == -1) return null;
		return values.get(id);
	}

	@Override
	public T insert(String word, T value) {
		throw new UnsupportedOperationException();
	}

	class IterableAdapter implements Iterable<Map.Entry<String, T>>{
		public IterableAdapter(Iterable<Pair<String, Integer>> iterable) {
			this.iterable = iterable;
		}
		@Override
		public Iterator<Map.Entry<String, T>> iterator(){
			final Iterator<Pair<String, Integer>> it = iterable.iterator();
			return new Iterator<Map.Entry<String, T>>(){
				@Override
				public boolean hasNext() {
					return it.hasNext();
				}
				@Override
				public Map.Entry<String, T> next() {
					final Pair<String, Integer> e = it.next();
					return new Map.Entry<String, T>() {
						@Override
						public String getKey() {
							return e.getFirst();
						}
						@Override
						public T getValue() {
							return values.get(e.getSecond());
						}
						@Override
						public T setValue(T value) {
							T ret = getValue();
							values.set(e.getSecond(), value);
							return ret;
						}
					};
				}
				@Override
				public void remove() {
					it.remove();
				}
			};
		}
		private Iterable<Pair<String, Integer>> iterable;
	}
	@Override
	public Iterable<Map.Entry<String, T>> commonPrefixSearchEntries(final String query) {
		return new IterableAdapter(trie.commonPrefixSearchWithId(query));
	}

	@Override
	public Iterable<Entry<String, T>> predictiveSearchEntries(String prefix) {
		return new IterableAdapter(trie.predictiveSearchWithId(prefix));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
		trie = (TailLOUDSTrie)in.readObject();
		values = (SBVIntMap<T>)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(trie);
		out.writeObject(values);
	}

	private TailLOUDSTrie trie;
	private SBVIntMap<T> values = new SBVIntMap<T>();
}
