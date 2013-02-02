package org.trie4j.patricia.tail;

import org.trie4j.Trie;
import org.trie4j.TrieTestSet;
import org.trie4j.tail.builder.SuffixTrieTailBuilder;

public class TailPatriciaTrieWithSuffixTrieTailBuilderTest extends TrieTestSet {
	@Override
	protected Trie trieWithWords(String... words) {
		Trie trie = new TailPatriciaTrie(new SuffixTrieTailBuilder());
		for(String w : words) trie.insert(w);
		return trie;
	}
}
