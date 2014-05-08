/*
 * Copyright 2014 Takao Nakaguchi
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
package org.trie4j;

import java.util.Iterator;

import org.trie4j.util.Pair;

public abstract class AbstractIdTrie extends AbstractTrie implements IdTrie{
	static class StringIterableAdapter implements Iterable<String>{
		public StringIterableAdapter(Iterable<Pair<String, Integer>> iterable) {
			this.iterable = iterable;
		}
		@Override
		public Iterator<String> iterator() {
			final Iterator<Pair<String, Integer>> it = iterable.iterator();
			return new Iterator<String>(){
				@Override
				public boolean hasNext() {
					return it.hasNext();
				}
				@Override
				public String next() {
					return it.next().getFirst();
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
	public boolean contains(String text){
		return getIdFor(text) != -1;
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		return new StringIterableAdapter(commonPrefixSearchWithId(query));
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		return new StringIterableAdapter(predictiveSearchWithId(prefix));
	}
}
