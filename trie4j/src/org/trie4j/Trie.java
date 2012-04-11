package org.trie4j;

public interface Trie {
	Node getRoot();
	boolean contains(String word);
	Iterable<String> commonPrefixSearch(String query);
	Iterable<String> predictiveSearch(String prefix);
	void insert(String word);
	void visit(TrieVisitor visitor);
}
