package org.trie4j.patricia.multilayer;

import org.trie4j.Trie;
import org.trie4j.TrieTestSet;

public class MultilayerPatriciaTrieTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		Trie trie = new MultilayerPatriciaTrie();
		for(String w : words) trie.insert(w);
		return trie;
	}
}
