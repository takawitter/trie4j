package org.trie4j.doublearray;

import org.trie4j.Trie;
import org.trie4j.TrieTestSet;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.tail.builder.ConcatTailBuilder;

public class OptimizedTailDoubleArrayWithConcatTailBuilderTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		PatriciaTrie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		return new OptimizedTailDoubleArray(trie, 65536, new ConcatTailBuilder());
	}
}
