package org.trie4j.patricia.multilayer;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.labeltrie.LabelTrie;

public abstract class Node implements org.trie4j.Node {
	public abstract boolean contains(char[] letters, int offset);

	@Override
	public abstract boolean isTerminated();

	@Override
	public abstract char[] getLetters();
	public abstract char getFirstLetter();

	@Override
	public abstract Node[] getChildren();
	public abstract void setChildren(Node[] children);

	public abstract Node getChild(char c);
	public abstract void setChild(int index, Node child);
	public abstract Node insertChild(char[] letters, int offset);
	public abstract Node addChild(int index, Node n);

	public abstract void visit(TrieVisitor visitor, int nest);

	public abstract Node pushLabel(LabelTrie trie);
}
