package org.trie4j;

public interface TreeVisitor<T> {
	void accept(T node, int nest);
}
