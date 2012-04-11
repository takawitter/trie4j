package org.trie4j.patricia.multilayer.node;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class SingleChildInternalCharsNode extends CharsNode {
	public SingleChildInternalCharsNode(char[] letters, Node child) {
		super(letters);
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
		if(index != 0) throw new IllegalStateException();
		this.child = child;
	}

	public Node getChild(char c){
		if(child.getFirstLetter() == c) return child;
		return null;
	}

	public Node addChild(int index, Node n){
		if(index == 0){
			return new InternalCharsNode(getLetters(), new Node[]{n, child});
		} else{
			return new InternalCharsNode(getLetters(), new Node[]{child, n});
		}
	}

	public void visit(TrieVisitor visitor, int nest){
		super.visit(visitor, nest);
		nest++;
		child.visit(visitor, nest);
	}

	protected Node cloneWithLetters(char[] letters){
		return new SingleChildInternalCharsNode(letters, child);
	}

	protected Node newLabelTrieNode(LabelNode ln, Node[] children){
		if(children != null){
			if(children.length != 1) throw new IllegalArgumentException();
			return new SingleChildInternalLabelTrieNode(ln, children[0]);
		} else{
			return new LabelTrieNode(ln);
		}
	}

	private Node child;
}
