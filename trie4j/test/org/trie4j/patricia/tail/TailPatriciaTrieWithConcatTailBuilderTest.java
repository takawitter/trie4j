package org.trie4j.patricia.tail;

import org.trie4j.Trie;
import org.trie4j.TrieTestSet;
import org.trie4j.tail.ConcatTailBuilder;

public class TailPatriciaTrieWithConcatTailBuilderTest extends TrieTestSet {
	@Override
	protected Trie trieWithWords(String... words) {
		Trie trie = new TailPatriciaTrie(new ConcatTailBuilder());
		for(String w : words) trie.insert(w);
		return trie;
	}
}
