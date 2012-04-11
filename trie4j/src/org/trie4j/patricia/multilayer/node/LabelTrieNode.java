package org.trie4j.patricia.multilayer.node;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;
import org.trie4j.patricia.multilayer.labeltrie.LabelTrie;
import org.trie4j.util.CharsUtil;
import org.trie4j.util.Pair;

public class LabelTrieNode extends Node{
	public LabelTrieNode(LabelNode lettersNode) {
		this.lettersNode = lettersNode;
		lettersNode.addReferer(this);
	}
	public LabelNode getLettersNode() {
		return lettersNode;
	}

	public void setLettersNode(LabelNode lettersNode) {
		this.lettersNode = lettersNode;
	}

	public boolean isTerminated() {
		return false;
	}

	@Override
	public Node getChild(char c) {
		return null;
	}
	
	@Override
	public Node[] getChildren() {
		return null;
	}

	@Override
	public void setChildren(Node[] children) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setChild(int index, Node child) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Node addChild(int index, Node n) {
		throw new UnsupportedOperationException();
	}

	
	public Node insertChild(char[] letters, int offset){
		throw new UnsupportedOperationException();
	}

	public Node pushLabel(LabelTrie trie){
		throw new UnsupportedOperationException();
	}

	public boolean contains(char[] letters, int offset){
		if(lettersNode != null){
			Pair<Integer, Boolean> ret = lettersNode.containsBottomup(letters, offset);
			int matchCount = ret.getFirst();
			boolean matchCompleted = ret.getSecond();
			if(!matchCompleted) return false;
			offset += matchCount;
			if(letters.length == offset){
				return isTerminated();
			}
		}

		char c = letters[offset];
		Node n = getChild(c);
		if(n != null){
			return n.contains(letters, offset);
		}
		return false;
	}

	@Override
	public char getFirstLetter() {
		char[] l = lettersNode.getLetters();
		return l[l.length - 1];
	}

	@Override
	public char[] getLetters() {
		StringBuilder b = new StringBuilder();
		LabelNode n = lettersNode;
		while(n != null){
			b.append(CharsUtil.revert(n.getLetters()));
			n = n.getParent();
		}
		return b.toString().toCharArray();
	}

	@Override
	public void visit(TrieVisitor visitor, int nest) {
		visitor.accept(this, nest);
	}

	private LabelNode lettersNode;
}
