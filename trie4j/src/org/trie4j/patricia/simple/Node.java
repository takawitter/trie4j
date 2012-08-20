/*
 * Copyright (C) 2012 Takao Nakaguchi
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
package org.trie4j.patricia.simple;

import java.util.Arrays;

import org.trie4j.NodeVisitor;

public class Node implements org.trie4j.Node{
	public Node() {
		this(new char[]{}, false);
	}

	public Node(char[] letters, boolean terminated) {
		this(letters, terminated, new Node[]{});
	}

	public Node(char[] letters, boolean terminated, Node[] children) {
		this.letters = letters;
		this.terminated = terminated;
		this.children = children;
	}

	public Node[] getChildren() {
		return children;
	}

	public char[] getLetters() {
		return letters;
	}

	public boolean isTerminate() {
		return terminated;
	}

	public Node getChild(char c){
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
				if(n.letters[0] == c) return n;
			}
		}
		return null;
	}

	public void insertChild(char[] letters, int offset){
		int i = 0;
		int lettersRest = letters.length - offset;
		int thisLettersLength = this.letters.length;
		int n = Math.min(lettersRest, thisLettersLength);
		while(i < n && (letters[i + offset] - this.letters[i]) == 0) i++;
		if(i != n){
			Node child1 = new Node(
					Arrays.copyOfRange(this.letters, i, this.letters.length)
					, this.terminated, this.children);
			Node child2 = new Node(
					Arrays.copyOfRange(letters, i + offset, letters.length)
					, true);
			this.letters = Arrays.copyOfRange(this.letters, 0, i);
			this.terminated = false;
			this.children = (child1.getLetters()[0] < child2.getLetters()[0]) ?
					new Node[]{child1, child2} : new Node[]{child2, child1};
		} else if(lettersRest == thisLettersLength){
			terminated = true;
		} else if(lettersRest < thisLettersLength){
			Node newChild = new Node(
					Arrays.copyOfRange(this.letters, lettersRest, thisLettersLength)
					, this.terminated, this.children);
			this.letters = Arrays.copyOfRange(this.letters, 0, i);
			this.terminated = true;
			this.children = new Node[]{newChild};
		} else{
			int index = 0;
			int end = children.length;
			if(end > 16){
				int start = 0;
				while(start < end){
					index = (start + end) / 2;
					Node child = children[index];
					int c = letters[i + offset] - child.getLetters()[0];
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
					int c = letters[i + offset] - child.getLetters()[0];
					if(c < 0) break;
					if(c == 0){
						child.insertChild(letters, i + offset);
						return;
					}
				}
			}
			addChild(index, new Node(Arrays.copyOfRange(letters, i + offset, letters.length), true));
		}
	}

	public void visit(NodeVisitor visitor, int nest){
		if(!visitor.visit(this, nest)) return;
		nest++;
		for(Node n : children){
			n.visit(visitor, nest);
		}
	}

	private Node addChild(int index, Node n){
		Node[] newc = new Node[children.length + 1];
		System.arraycopy(children,  0, newc, 0, index);
		newc[index] = n;
		System.arraycopy(children,  index, newc, index + 1, children.length - index);
		this.children = newc;
		return this;
	}

	private Node[] children;
	private char[] letters;
	private boolean terminated;
}
