package org.trie4j.patricia.multilayer;

import org.trie4j.Trie;
import org.trie4j.TrieTestSet;

public class MultilayerPatriciaTriePackedTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		MultilayerPatriciaTrie trie = new MultilayerPatriciaTrie();
		for(String w : words) trie.insert(w);
		trie.pack();
		return trie;
	}
}
