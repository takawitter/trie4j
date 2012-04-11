package org.trie4j.patricia.multilayer.node;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.Node;

public class InternalCharsNode extends CharsNode {
	public InternalCharsNode(char[] letters, Node[] children) {
		super(letters);
		if(children == null) throw new IllegalArgumentException();
		this.children = children;
	}

	public Node[] getChildren() {
		return children;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}

	public void setChild(int index, Node child){
		children[index] = child;
	}

	public Node getChild(char c){
		if(children != null){
			int end = children.length;
			if(end > 16){
				int start = 0;
				while(start < end){
					int i = (start + end) / 2;
					Node n = children[i];
					char nl = n.getFirstLetter();
					int d = c - nl;
					if(d == 0) return n;
					if(d < 0){
						end = i;
					} else if(start == i){
						break;
					} else{
						start = i;
					}
				}
			} else{
				for(int i = 0; i < end; i++){
					Node n = children[i];
					if(n.getFirstLetter() == c) return n;
				}
			}
		}
		return null;
	}

	public Node addChild(int index, Node n){
		Node[] newc = new Node[children.length + 1];
		System.arraycopy(children,  0, newc, 0, index);
		newc[index] = n;
		System.arraycopy(children,  index, newc, index + 1, children.length - index);
		this.children = newc;
		return this;
	}

	public void visit(TrieVisitor visitor, int nest){
		super.visit(visitor, nest);
		nest++;
		for(Node n : children){
			n.visit(visitor, nest);
		}
	}

	protected Node cloneWithLetters(char[] letters){
		return new InternalCharsNode(letters, children);
	}

	private Node[] children;
}
