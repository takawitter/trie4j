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
package org.trie4j.patricia.tail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.trie4j.NodeVisitor;
import org.trie4j.tail.TailBuilder;
import org.trie4j.tail.TailCharIterator;
import org.trie4j.util.Pair;

public class Node {
	public Node() {
		this((char)0xffff, -1, false);
	}

	public Node(char firstChar, int tailIndex, boolean terminate) {
		this(firstChar, tailIndex, terminate, emptyChildren);
	}

	public Node(char firstChar, int tailIndex, boolean terminate, Node[] children) {
		this.firstChar = firstChar;
		this.tailIndex = tailIndex;
		this.terminate = terminate;
		this.children = children;
	}

	public char[] getLetters(CharSequence tails) {
		List<Character> letters = new ArrayList<Character>();
		if(firstChar != (char)0xffff){
			letters.add(firstChar);
		}
		TailCharIterator it = new TailCharIterator(tails, tailIndex);
		while(it.hasNext()){
			letters.add(it.next());
		}
		char[] ret = new char[letters.size()];
		for(int i = 0; i < ret.length; i++){
			ret[i] = letters.get(i);
		}
		return ret;
	}

	public boolean isTerminate() {
		return terminate;
	}
	
	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

	public char getFirstLetter() {
		return firstChar;
	}

	public int getTailIndex(){
		return tailIndex;
	}

	public void setTailIndex(int tailIndex) {
		this.tailIndex = tailIndex;
	}

	public TailCharIterator getSecondLetters(CharSequence tails){
		return new TailCharIterator(tails, tailIndex);
	}

	public Node getChild(char c) {
		if(children == null){
			return null;
		}
		return findNodeOnly(c);
	}

	public Node[] getChildren() {
		return children;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}

	public Node insertChild(char[] letters, int offset, CharSequence tails, TailBuilder tailBuilder){
		TailCharIterator it = new TailCharIterator(tails, tailIndex);
		int count = 0;
		boolean matchComplete = true;
		while(it.hasNext() && offset < letters.length){
			if(letters[offset] != it.next()){
				matchComplete = false;
				break;
			}
			offset++;
			count++;
		}
		if(offset == letters.length){
			if(it.hasNext()){
				// n: abcde
				// l: abc
				char c = it.next();
				int idx = it.getNextIndex();
				if(!it.hasNext()){
					idx = -1;
				}
				Node newChild = new Node(c, idx, this.terminate, this.children);
				this.tailIndex = (count > 0) ? tailBuilder.insert(Arrays.copyOfRange(letters, offset - count, offset)) : -1;
				this.terminate = true;
				this.children = new Node[]{newChild};
				return this;
			} else{
				// n: abc
				// l: abc
				terminate = true;
				return this;
			}
		} else{
			if(!matchComplete){
				// n: abcwz
				// l: abcde
				int firstOffset = offset - count;
				char n1Fc = it.current();
				int n1Idx = it.getNextIndex();
				if(!it.hasNext()){
					n1Idx = -1;
				}
				Node n1 = new Node(n1Fc, n1Idx, terminate, children);
				char n2Fc = letters[offset++];
				int n2Idx = (offset < letters.length) ?
						tailBuilder.insert(Arrays.copyOfRange(letters, offset, letters.length)) :
						-1;
				Node n2 = new Node(n2Fc, n2Idx, true);
				if(count > 0){
					this.tailIndex = tailBuilder.insert(letters, firstOffset, count);
				} else{
					this.tailIndex = -1;
				}
				this.terminate = false;
				if(n1.getFirstLetter() < n2.getFirstLetter()){
					this.children = new Node[]{n1, n2};
				} else{
					this.children = new Node[]{n2, n1};
				}
				return this;
			} else{
				// n: abc
				// l: abcde
				char fc = letters[offset++];
				if(children == null){
					int idx = (offset < letters.length) ?
							tailBuilder.insert(Arrays.copyOfRange(letters, offset, letters.length)) :
							-1;
					this.children = new Node[]{new Node(fc, idx, true)};
				} else{
					// find node
					Pair<Node, Integer> ret = findNode(fc);
					Node child = ret.getFirst();
					if(child != null){
						Node newChild = child.insertChild(letters, offset, tails, tailBuilder);
						children[ret.getSecond()] = newChild;
					} else{
						int idx = (offset < letters.length) ?
							tailBuilder.insert(letters, offset, letters.length - offset) :
							-1;
						addChild(ret.getSecond(), new Node(fc, idx, true));
					}
				}
				return this;
			}
		}
	}

	public void visit(NodeVisitor visitor, int nest, CharSequence tails) {
		visitor.visit(new NodeAdapter(this, tails), nest);
		if(children != null){
			nest++;
			for(Node n : children){
				n.visit(visitor, nest, tails);
			}
		}
	}

	public Node addChild(int index, Node n) {
		Node[] newc = new Node[children.length + 1];
		System.arraycopy(children,  0, newc, 0, index);
		newc[index] = n;
		System.arraycopy(children,  index, newc, index + 1, children.length - index);
		this.children = newc;
		return this;
	}

	public Pair<Node, Integer> findNode(char firstChar){
		int end = children.length;
		if(end > 16){
			int start = 0;
			while(start < end){
				int i = (start + end) / 2;
				Node child = children[i];
				int d = firstChar - child.getFirstLetter();
				if(d == 0){
					return Pair.create(child, i);
				} else if(d < 0){
					end = i;
				} else if(start == i){
					return Pair.create(null, i + 1);
				} else{
					start = i;
				}
			}
		} else{
			for(int i = 0; i < end; i++){
				Node child = children[i];
				int c = firstChar - child.getFirstLetter();
				if(c < 0){
					return Pair.create(null, i);
				} else if(c == 0){
					return Pair.create(child, i);
				}
			}
		}
		return Pair.create(null, end);
	}

	private Node findNodeOnly(char firstChar){
		int end = children.length;
		if(end > 16){
			int start = 0;
			while(start < end){
				int i = (start + end) / 2;
				Node child = children[i];
				int d = firstChar - child.getFirstLetter();
				if(d == 0){
					return child;
				} else if(d < 0){
					end = i;
				} else if(start == i){
					return null;
				} else{
					start = i;
				}
			}
		} else{
			for(int i = 0; i < end; i++){
				Node child = children[i];
				int c = firstChar - child.getFirstLetter();
				if(c < 0){
					return null;
				} else if(c == 0){
					return child;
				}
			}
		}
		return null;
	}

	private char firstChar;
	private int tailIndex;
	private boolean terminate;
	private Node[] children;
	private static Node[] emptyChildren = {};
}