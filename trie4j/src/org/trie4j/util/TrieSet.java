package org.trie4j.util;

import java.util.AbstractSet;
import java.util.Iterator;

import org.trie4j.Trie;

public class TrieSet extends AbstractSet<String>{
	public TrieSet(Trie trie) {
		this.trie = trie;
	}

	@Override
	public boolean add(String e) {
		int prev = trie.size();
		trie.insert(e);
		return prev != trie.size();
	}

	@Override
	public Iterator<String> iterator() {
		return trie.commonPrefixSearch("").iterator();
	}

	@Override
	public int size() {
		return trie.size();
	}

	private Trie trie;
}
