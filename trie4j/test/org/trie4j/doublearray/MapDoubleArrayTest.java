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
package org.trie4j.doublearray;

import junit.framework.Assert;

import org.junit.Test;
import org.trie4j.MapTrie;
import org.trie4j.Trie;
import org.trie4j.TrieTestSet;
import org.trie4j.patricia.simple.MapPatriciaTrie;

public class MapDoubleArrayTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		int i = 0;
		MapTrie<Integer> trie = new MapPatriciaTrie<Integer>();
		for(String w : words) trie.insert(w, i++);
		return new MapDoubleArray<Integer>(trie);
	}

	@Test
	public void test_get() throws Exception{
		MapTrie<Integer> trie = new MapPatriciaTrie<Integer>();
		for(String s : new String[]{"hello", "hi", "world", "happy"}){
			trie.insert(s, s.length());
		}
		trie = new MapDoubleArray<Integer>(trie);
		for(String s : trie.predictiveSearch("")){
			Assert.assertEquals(s.length(), trie.get(s).intValue());
		}
	}

	@Test
	public void test() throws Exception{
		MapDoubleArray<Integer> da = new MapDoubleArray<Integer>(new MapPatriciaTrie<Integer>());
		Assert.assertFalse(da.contains("hello"));
	}

	@Test
	public void test2() throws Exception{
		MapTrie<Integer> trie = new MapPatriciaTrie<Integer>();
		trie.insert("hello", 0);
		MapDoubleArray<Integer> da = new MapDoubleArray<Integer>(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertFalse(da.contains("hi"));
	}

	@Test
	public void test3() throws Exception{
		MapTrie<Integer> trie = new MapPatriciaTrie<Integer>();
		trie.insert("hello", 0);
		trie.insert("hi", 1);
		MapDoubleArray<Integer> da = new MapDoubleArray<Integer>(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertTrue(da.contains("hi"));
		Assert.assertFalse(da.contains("world"));
	}

	@Test
	public void test4() throws Exception{
		MapTrie<Integer> trie = new MapPatriciaTrie<Integer>();
		trie.insert("hello", 0);
		trie.insert("hi", 1);
		trie.insert("world", 2);
		MapDoubleArray<Integer> da = new MapDoubleArray<Integer>(trie);
		Assert.assertEquals(new Integer(0), da.get("hello"));
		Assert.assertEquals(new Integer(1), da.get("hi"));
		Assert.assertEquals(new Integer(2), da.get("world"));
	}

	@Test
	public void test5() throws Exception{
		MapTrie<Integer> trie = new MapPatriciaTrie<Integer>();
		trie.insert("hello", 0);
		trie.insert("world", 1);
		MapDoubleArray<Integer> da = new MapDoubleArray<Integer>(trie);
		Assert.assertEquals(new Integer(0), da.get("hello"));
		Assert.assertNull(da.get("hi"));
		Assert.assertEquals(new Integer(1), da.get("world"));
	}

	public static void main(String[] args) throws Exception{
		MapTrie<Integer> t = new MapPatriciaTrie<Integer>();
		t.insert("hello");
//		t.insert("hi");
		MapDoubleArray<Integer> da = new MapDoubleArray<Integer>(t);
		System.out.println("hello: " + da.contains("hello"));
		System.out.println("hi: " + da.contains("hi"));
		System.out.println("world: " + da.contains("world"));
	}
}
