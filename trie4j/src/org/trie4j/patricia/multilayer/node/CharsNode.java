/*
 * Copyright 2012 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trie4j.patricia.multilayer.node;

import java.util.Arrays;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;
import org.trie4j.patricia.multilayer.labeltrie.LabelTrie;
import org.trie4j.util.CharsUtil;

public class CharsNode extends Node{
	public CharsNode(char[] letters) {
		this.letters = letters;
	}
	public char[] getLetters() {
		return letters;
	}
	public void setLetters(char[] letters) {
		this.letters = letters;
	}
	public boolean isTerminate() {
		return false;
	}

	@Override
	public char getFirstLetter() {
		return letters[0];
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
	
	public void setChild(int index, Node child){
		throw new UnsupportedOperationException();
	}

	public Node insertChild(char[] letters, int offset){
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
		Node[] children = getChildren();
		if(i == n){
			if(lettersRest == thisLettersLength){
				if(isTerminate()) return null;
				if(children != null){
					if(children.length == 1){
						return new TerminalSingleChildInternalCharsNode(this.letters, children[0]);
					} else{
						return new TerminalInternalCharsNode(this.letters, children);
					}
				} else{
					return new TerminalCharsNode(this.letters);
				}
			}
			if(lettersRest < thisLettersLength){
				char[] newLetters = Arrays.copyOfRange(this.letters, 0, i);
				char[] newChildLetters = Arrays.copyOfRange(this.letters, lettersRest, thisLettersLength);
				this.letters = newChildLetters;
				return new TerminalSingleChildInternalCharsNode(
						newLetters
						, this);
			}
			if(children != null){
				int index = 0;
				int end = getChildren().length;
				if(end > 16){
					int start = 0;
					while(start < end){
						index = (start + end) / 2;
						Node child = children[index];
						c = letters[i + offset] - child.getFirstLetter();
						if(c == 0){
							Node newChild = child.insertChild(letters, i + offset);
							if(newChild != null){
								setChild(index, newChild);
							}
							return null;
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
					for(; index < end; index++){
						Node child = children[index];
						c = letters[i + offset] - child.getFirstLetter();
						if(c < 0) break;
						if(c == 0){
							Node newChild = child.insertChild(letters, i + offset);
							if(newChild != null){
								setChild(index, newChild);
							}
							return null;
						}
					}
				}
				return addChild(index, new TerminalCharsNode(Arrays.copyOfRange(letters, i + offset, letters.length)));
			} else{
				return newInternalCharsNode(
						this.letters
						, new Node[]{
								new TerminalCharsNode(Arrays.copyOfRange(letters, i + offset, letters.length))
						});
			}
		}

		char[] newLetter1 = Arrays.copyOfRange(this.letters, 0, i);
		char[] newLetter2 = Arrays.copyOfRange(this.letters, i, this.letters.length);
		char[] newLetter3 = Arrays.copyOfRange(letters, i + offset, letters.length);
		Node[] newChildren = new Node[2];
		if(newLetter2[0] < newLetter3[0]){
			newChildren[0] = cloneWithLetters(newLetter2);
			newChildren[1] = new TerminalCharsNode(newLetter3);
		} else{
			newChildren[0] = new TerminalCharsNode(newLetter3);
			newChildren[1] = cloneWithLetters(newLetter2);
		}
		return new InternalCharsNode(newLetter1, newChildren);
	}

	public Node pushLabel(LabelTrie trie){
		Node[] children = getChildren();
		Node ret = this;
		if(letters.length > 0){
			ret = newLabelTrieNode(
					trie.insert(CharsUtil.revert(letters))
					, children);
			letters = LabelNode.emptyChars;
		}
		if(children != null){
			int n = children.length;
			for(int i = 0; i < n; i++){
				ret.setChild(i, children[i].pushLabel(trie));
			}
		}
		return ret;
	}

	@Override
	public void visit(TrieVisitor visitor, int nest) {
		visitor.accept(this, nest);
	}

	protected Node newInternalCharsNode(char[] letters, Node[] children){
		if(children.length == 1){
			return new SingleChildInternalCharsNode(letters, children[0]);
		} else{
			return new InternalCharsNode(letters, children);
		}
	}

	protected Node cloneWithLetters(char[] letters){
		return new CharsNode(letters);
	}

	protected Node newLabelTrieNode(LabelNode ln, Node[] children){
		if(children != null){
			return new InternalLabelTrieNode(ln, children);
		} else{
			return new LabelTrieNode(ln);
		}
	}
	
	@Override
	public Node addChild(int index, Node n) {
		throw new UnsupportedOperationException();
	}

	public boolean contains(char[] letters, int offset){
		int rest = letters.length - offset;
		int tll = this.letters.length;
		if(tll > 0){
			if(tll > rest) return false;
			for(int i = 0; i < tll; i++){
				if(this.letters[i] != letters[i + offset]) return false;
			}
			if(tll == rest){
				return isTerminate();
			}
			offset += tll;
		}

		if(rest > 0){
			char c = letters[offset];
			Node n = getChild(c);
			if(n != null){
				return n.contains(letters, offset);
			}
		}
		return false;
	}

	private char[] letters;
}
