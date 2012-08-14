package org.trie4j.patricia.simple;

import org.trie4j.Trie;
import org.trie4j.TrieTestSet;

public class PatriciaTrieTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		Trie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		return trie;
	}
}
