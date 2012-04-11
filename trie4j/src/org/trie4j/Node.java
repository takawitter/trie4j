package org.trie4j;

public interface Node {
	char[] getLetters();
	boolean isTerminated();
	Node[] getChildren();
/*	void setChildren(LeafNode[] children);
	boolean hasFirstLetter();
	char getFirstLetter();
	int getLettersLength();
	int getLettersLengthInNode();
	Node insertChild(char[] letters, int offset);
	Node pushLabel(LabelTrie trie);
	boolean contains(char[] letters, int offset);

	Node getChild(char c);
	void visit(TrieVisitor visitor, int nest);
	void addChild(int index, LeafNode n);
*/
}
