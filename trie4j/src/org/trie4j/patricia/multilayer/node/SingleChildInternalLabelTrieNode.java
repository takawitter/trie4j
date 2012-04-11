package org.trie4j.patricia.multilayer.node;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class SingleChildInternalLabelTrieNode extends LabelTrieNode{
	public SingleChildInternalLabelTrieNode(LabelNode lettersNode, Node child) {
		super(lettersNode);
		this.child = child;
	}

	public Node[] getChildren() {
		return new Node[]{child};
	}

	public void setChildren(Node[] children) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setChild(int index, Node child) {
		if(index != 0) throw new IllegalArgumentException();
		this.child = child;
	}

	public Node getChild(char c){
		if(child.getFirstLetter() == c) return child;
		return null;
	}

	public Node addChild(int index, Node n){
		if(index == 0){
			return new InternalLabelTrieNode(getLettersNode(), new Node[]{n, child});
		} else if(index == 1){
			return new InternalLabelTrieNode(getLettersNode(), new Node[]{child, n});
		} else{
			throw new IllegalArgumentException();
		}
	}

	public void visit(TrieVisitor visitor, int nest){
		super.visit(visitor, nest);
		nest++;
		child.visit(visitor, nest);
	}

	private Node child;
}
