package org.trie4j.patricia.multilayer.labeltrie;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;

public class InternalLabelNode extends LabelNode{
	public InternalLabelNode(char[] letters, LabelNode parent, LabelNode[] children) {
		super(letters, parent);
		this.children = children;
	}

	public LabelNode[] getChildren() {
		return children;
	}

	public void setChildren(LabelNode[] children) {
		this.children = children;
	}

	public LabelNode getChild(char c){
		if(children != null){
			int end = children.length;
			if(end > 16){
				int start = 0;
				while(start < end){
					int i = (start + end) / 2;
					LabelNode n = children[i];
					int d = c - n.getLetters()[0];
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
					LabelNode n = children[i];
					if(n.getLetters() != null && n.getLetters().length > 0 && n.getLetters()[0] == c){
						return n;
					}
				}
			}
		}
		return null;
	}
	
	public void pargeChildren(){
		LabelNode newNode = new LabelNode(getLetters(), getParent());
		fireReplace(newNode);
		if(children != null){
			for(LabelNode child : children){
				child.setParent(newNode);
				child.pargeChildren();
			}
		}
		this.children = null;
	}

	public void addChild(int index, LabelNode n){
		LabelNode[] newc = new LabelNode[children.length + 1];
		System.arraycopy(children,  0, newc, 0, index);
		newc[index] = n;
		System.arraycopy(children,  index, newc, index + 1, children.length - index);
		this.children = newc;
	}

	public void visit(TrieVisitor visitor, int nest){
		super.visit(visitor, nest);
		if(children != null){
			nest++;
			for(LabelNode n : children){
				n.visit(visitor, nest);
			}
		}
	}

	public void addReferer(LabelTrieNode r) {
		if(referers == null){
			referers = new LabelTrieNode[]{r};
		} else{
			LabelTrieNode[] ls = new LabelTrieNode[referers.length + 1];
			System.arraycopy(referers, 0, ls, 0, referers.length);
			ls[ls.length - 1] = r;
			referers = ls;
		}
	}

	public void removeReferer(LabelTrieNode l) {
	}

	protected void fireReplace(LabelNode newLabelNode) {
		if(referers == null) return;
		for(LabelTrieNode l : referers){
			l.setLettersNode(newLabelNode);
		}
	}
 
	private LabelNode[] children;
	private LabelTrieNode[] referers;
}
