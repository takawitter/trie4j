package org.trie4j.patricia.simple;

import junit.framework.Assert;

import org.junit.Test;
import org.trie4j.Trie;
import org.trie4j.TrieTestSet;

public class MapPatriciaTrieTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		Trie trie = new MapPatriciaTrie<Integer>();
		for(String w : words) trie.insert(w);
		return trie;
	}

	@Test
	public void test_get() throws Exception{
		MapPatriciaTrie<Integer> trie = new MapPatriciaTrie<Integer>();
		for(String s : new String[]{"hello", "hi", "world", "happy"}){
			trie.insert(s, s.length());
		}
		for(String s : trie.predictiveSearch("")){
			Assert.assertEquals(s.length(), trie.get(s).intValue());
		}
	}
}
