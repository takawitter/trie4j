package org.trie4j.patricia.multilayer.labeltrie;

import java.util.Arrays;

import org.trie4j.Node;
import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;
import org.trie4j.util.Pair;

public class LabelNode implements Node{
	public static final char[] emptyChars = {};

	public LabelNode(char[] letters) {
		this.letters = letters;
	}
	public LabelNode(char[] letters, LabelNode parent) {
		this.letters = letters;
		this.parent = parent;
	}
	public char[] getLetters() {
		return letters;
	}
	public void setLetters(char[] letters) {
		this.letters = letters;
	}
	@Override
	public boolean isTerminated() {
		return false;
	}
	public LabelNode getParent() {
		return parent;
	}
	public void setParent(LabelNode parent) {
		this.parent = parent;
	}
	public LabelNode[] getChildren() {
		return null;
	}
	public void setChildren(LabelNode[] children) {
		throw new UnsupportedOperationException();
	}
	public LabelNode getChild(char c){
		return null;
	}

	public LabelNode insertChild(int childIndex, char[] letters, int offset){
		if(this.letters == null){
			this.letters = Arrays.copyOfRange(letters, offset, letters.length);
			return this;
		}
//hello$
//h -> insert to children (a)
//h$ -> insert to children (b)
//hat -> h, at, ello$ (c)
//hat$ -> h, at$, ello$ (d)
//hello -> hello$ (e)
//hello$ -> hello$ (f)
//helloworld$ -> hello$, helloworld$ (g)
//helloworld (h)
		int i = 0;
		int lettersRest = letters.length - offset;
		int thisLettersLength = this.letters.length;
		int n = Math.min(lettersRest, thisLettersLength);
		int c = 0;
		while(i < n && (c = letters[i + offset] - this.letters[i]) == 0) i++;
		if(i == n){
			if(lettersRest == thisLettersLength){
				return this;
			}
			if(lettersRest < thisLettersLength){
				LabelNode parent = new InternalLabelNode(
						Arrays.copyOf(this.letters, i)
						, this.parent
						, new LabelNode[]{this});
				if(this.parent != null){
					this.parent.getChildren()[childIndex] = parent;
				}
				this.letters = Arrays.copyOfRange(this.letters, i, this.letters.length);
				this.parent = parent;
				return parent;
			}
			if(getChildren() != null){
				int index = 0;
				int end = getChildren().length;
				if(end > 16){
					int start = 0;
					while(start < end){
						index = (start + end) / 2;
						LabelNode child = getChildren()[index];
						c = letters[i + offset] - child.letters[0];
						if(c == 0){
							return child.insertChild(index, letters, i + offset);
						}
						if(c < 0){
							end = index;
						} else if(start == index){
							index = end;
							break;
						} else{
							start = index;
						}
					}
				} else{
					for(index = 0; index < end; index++){
						LabelNode child = getChildren()[index];
						c = letters[i + offset] - child.letters[0];
						if(c < 0) break;
						if(c == 0){
							return child.insertChild(index, letters, i + offset);
						}
					}
				}
				LabelNode child = new InternalLabelNode(
						Arrays.copyOfRange(letters, i + offset, letters.length)
						, this, null);
				addChild(index, child);
				return child;
			} else{
				LabelNode child = new InternalLabelNode(
						Arrays.copyOfRange(letters, i + offset, letters.length)
						, this, null
						);
				setChildren(new LabelNode[]{child});
				return child;
			}
		}
		char[] newParentsLetter = Arrays.copyOfRange(this.letters, 0, i);
		char[] newThisLetter = Arrays.copyOfRange(this.letters, i, this.letters.length);
		char[] newChildsLetter = Arrays.copyOfRange(letters, i + offset, letters.length);
		LabelNode[] newParentsChildren = new LabelNode[2];
		LabelNode newParent = new InternalLabelNode(
				newParentsLetter, this.parent, newParentsChildren
				);
		LabelNode newChild = new InternalLabelNode(
				newChildsLetter, newParent, null
				);
		if(newThisLetter[0] < newChildsLetter[0]){
			newParentsChildren[0] = this;
			newParentsChildren[1] = newChild;
		} else{
			newParentsChildren[0] = newChild;
			newParentsChildren[1] = this;
		}
		this.letters = newThisLetter;
		if(this.parent != null){
			this.parent.getChildren()[childIndex] = newParent;
		}
		this.parent = newParent;
		return newChild;
	}

	boolean contains(char[] letters, int offset){
		int rest = letters.length - offset;
		int tll = this.letters.length;
		if(tll > rest) return false;
		for(int i = 0; i < tll; i++){
			if(this.letters[i] != letters[i + offset]) return false;
		}
		if(tll == rest){
			return true;
		}
		offset += tll;

		char c = letters[offset];
		LabelNode n = getChild(c);
		if(n != null){
			return n.contains(letters, offset);
		}
		return false;
	}

	public Pair<Integer, Boolean> containsBottomup(char[] letters, int offset){
		int rest = letters.length - offset;
		int n = this.letters.length;
		int ttlm1 = n - 1;
		if(n > rest) return Pair.create(0, false);
		for(int i = 0; i < n; i++){
			if(this.letters[ttlm1 - i] != letters[offset]){
				return Pair.create(i, false);
			}
			offset++;
		}
		if(n == rest){
			return Pair.create(n, true);
		}
		if(parent != null){
			Pair<Integer, Boolean> ret = parent.containsBottomup(letters, offset);
			return Pair.create(ret.getFirst() + n, ret.getSecond());
		} else{
			return Pair.create(n, true);
		}
	}

	public void pargeChildren(){
	}

	public void visit(TrieVisitor visitor, int nest){
		visitor.accept(this, nest);
	}
	
	public void addChild(int index, LabelNode n){
		throw new UnsupportedOperationException();
	}

	public void addReferer(LabelTrieNode l) {
		throw new UnsupportedOperationException();
	}

	public void removeReferer(LabelTrieNode l) {
	}

	private char[] letters;
	private LabelNode parent;
}
