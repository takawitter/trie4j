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
package org.trie4j.patricia.terminletters;

import java.util.Arrays;

import org.trie4j.TrieVisitor;
import org.trie4j.util.CharsUtil;
import org.trie4j.util.Pair;

public class Node implements org.trie4j.Node{
	public Node() {
	}
	public Node(char[] letters) {
		this.letters = letters;
	}
	public Node(char[] letters, Node[] children) {
		this.children = children;
		this.letters = letters;
	}
	public org.trie4j.Node[] getChildren() {
		return children;
	}
	public void setChildren(Node[] children) {
		this.children = children;
	}
	public char[] getLetters() {
		return letters;
	}
	public void setLetters(char[] letters) {
		this.letters = letters;
	}
	public Pair<Boolean, Integer> compareLetters(char[] value, int offset){
		int rest = value.length - offset;

		int len = letters.length;
		if(letters[len - 1] == 0xffff){
			len--;
		}
		int n = Math.min(len, rest);
		for(int i = 0; i < len; i++){
			int c = letters[i] - value[offset + i];
			if(c == 0) continue;
			if(c < 0) return Pair.create(false, -i);
			return Pair.create(false, i);
		}
		if(len == rest) return Pair.create(true, n);
		if(len - rest < 0) return Pair.create(true, -n);
		return Pair.create(true, n);
	}
	public boolean isTerminated() {
		return children == null || (letters != null && letters.length > 0 && letters[letters.length - 1] == 0xffff);
	}
	public Node getChild(char c){
		if(children != null){
			int end = children.length;
			if(end > 16){
				int start = 0;
				while(start < end){
					int i = (start + end) / 2;
					Node n = children[i];
					int d = c - n.letters[0];
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
					if(n.letters != null && n.letters.length > 0 && n.letters[0] == c){
						return n;
					}
				}
			}
		}
		return null;
	}
	public void insertChild(char[] letters, int offset){
		if(this.letters == null){
			this.letters = CharsUtil.newTerminatedCharsFrom(letters, offset, letters.length);
			return;
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
		boolean terminated = false;
		if(thisLettersLength > 0 && this.letters[thisLettersLength - 1] == (char)-1){
			thisLettersLength--;
			terminated = true;
		}
		if(children == null){
			terminated = true;
		}
		int n = Math.min(lettersRest, thisLettersLength);
		int c = 0;
		while(i < n && (c = letters[i + offset] - this.letters[i]) == 0) i++;
		if(i == n){
			if(lettersRest == thisLettersLength){
				if(!terminated && children != null){
					this.letters = CharsUtil.newTerminatedChars(this.letters);
				}
				return;
			}
			if(lettersRest < thisLettersLength){
				Node child = new Node(
						Arrays.copyOfRange(this.letters, lettersRest, this.letters.length)
						, this.children);
				this.letters = CharsUtil.newTerminatedCharsFrom(this.letters, 0, i);
				this.children = new Node[]{child};
				return;
			}
			if(children != null){
				int index = 0;
				int end = children.length;
				if(end > 16){
					int start = 0;
					while(start < end){
						index = (start + end) / 2;
						Node child = children[index];
						c = letters[i + offset] - child.letters[0];
						if(c == 0){
							child.insertChild(letters, i + offset);
							return;
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
						c = letters[i + offset] - child.letters[0];
						if(c < 0) break;
						if(c == 0){
							child.insertChild(letters, i + offset);
							return;
						}
					}
				}
				addChild(index, new Node(Arrays.copyOfRange(letters, i + offset, letters.length)));
			} else{
				this.letters = CharsUtil.newTerminatedChars(this.letters);
				this.children = new Node[]{
						new Node(Arrays.copyOfRange(letters, i + offset, letters.length))
						};
			}
			return;
		}
		char[] newLetter1 = Arrays.copyOfRange(this.letters, 0, i);
		char[] newLetter2 = Arrays.copyOfRange(this.letters, i, this.letters.length);
		char[] newLetter3 = Arrays.copyOfRange(letters, i + offset, letters.length);
		Node[] newChildren = new Node[2];
		if(newLetter2[0] < newLetter3[0]){
			newChildren[0] = new Node(newLetter2, this.children);
			newChildren[1] = new Node(newLetter3);
		} else{
			newChildren[0] = new Node(newLetter3);
			newChildren[1] = new Node(newLetter2, this.children);
		}
		this.letters = newLetter1;
		this.children = newChildren;
	}
	public boolean contains(char[] letters, int offset){
		int rest = letters.length - offset;
		int tll = this.letters.length;
		boolean terminated = false;
		if(tll > 0 && this.letters[tll - 1] == (char)-1){
			tll--;
			terminated = true;
		}
		if(children == null){
			terminated = true;
		}
		if(tll > rest) return false;
		for(int i = 0; i < tll; i++){
			if(this.letters[i] != letters[i + offset]) return false;
		}
		if(tll == rest){
			return terminated;
		}
		offset += tll;
		char c = letters[offset];
		Node n = getChild(c);
		if(n != null){
			return n.contains(letters, offset);
		}
		return false;
	}
	public void visit(TrieVisitor visitor, int nest){
		visitor.accept(this, nest);
		nest++;
		if(children != null){
			for(Node n : children){
				n.visit(visitor, nest);
			}
		}
	}
	private void addChild(int index, Node n){
		Node[] newc = new Node[children.length + 1];
		System.arraycopy(children,  0, newc, 0, index);
		newc[index] = n;
		System.arraycopy(children,  index, newc, index + 1, children.length - index);
		this.children = newc;
	}
	private Node[] children;
	private char[] letters;

}
