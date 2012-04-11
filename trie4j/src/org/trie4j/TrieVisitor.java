package org.trie4j;

public interface TrieVisitor {
	void accept(Node node, int nest);
}
