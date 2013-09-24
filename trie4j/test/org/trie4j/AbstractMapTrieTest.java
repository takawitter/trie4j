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
package org.trie4j;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.patricia.simple.MapPatriciaTrie;

public abstract class AbstractMapTrieTest extends AbstractTrieTest {
	@Override
	protected MapTrie<Integer> createFirstTrie(){
		return new MapPatriciaTrie<Integer>();
	}

	protected MapTrie<Integer> buildSecondTrie(MapTrie<Integer> firstTrie){
		return firstTrie;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected final MapTrie<Integer> buildSecondTrie(Trie firstTrie){
		return (MapTrie<Integer>)buildSecondTrie((MapTrie<Integer>)firstTrie);
	}

	@Test
	public void test_MapTrie_get_1() throws Exception{
		String[] words = {"hello", "hi", "world", "happy"};
		Integer[] values = {0, 1, 2, 3};
		MapTrie<Integer> trie = newMapTrie(words, values);
		for(int i = 0; i < words.length; i++){
			Assert.assertEquals(values[i], trie.get(words[i]));
		}
	}

	@Test
	public void test_MapTrie_get_2() throws Exception{
		MapTrie<Integer> mapTrie = new MapPatriciaTrie<Integer>();
		mapTrie.insert("Test", 1);
		mapTrie.insert("Tes", 4);
		Assert.assertEquals(1, mapTrie.get("Test").intValue());
	}

	private MapTrie<Integer> newMapTrie(String[] words, Integer[] values){
		Assert.assertEquals(words.length, values.length);
		int n = words.length;
		MapTrie<Integer> trie = createFirstTrie();
		for(int i = 0; i < n; i++){
			trie.insert(words[i], values[i]);
		}
		return buildSecondTrie(trie);
	}
}
