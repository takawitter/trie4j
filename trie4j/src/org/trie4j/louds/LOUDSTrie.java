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
package org.trie4j.louds;

import java.util.BitSet;
import java.util.LinkedList;

import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.tail.SuffixTrieTailBuilder;
import org.trie4j.tail.TailBuilder;
import org.trie4j.tail.TailCharIterator;
import org.trie4j.tail.TailUtil;
import org.trie4j.util.BitVector;

public class LOUDSTrie {
	public LOUDSTrie(Trie orig){
		//orig.size();
		int sz = 2000;
		bv = new BitVector(sz);
		labels = new char[sz];
		tail = new int[sz];
		term = new BitSet(sz);
		LinkedList<Node> queue = new LinkedList<Node>();
		int count = 0;

		bv.append(true);
		bv.append(false);
		labels[0] = 0xffff;
		tail[0] = -1;
		count++;

		TailBuilder tb = new SuffixTrieTailBuilder();
		queue.add(orig.getRoot());
		while(!queue.isEmpty()){
			Node node = queue.pollFirst();
			int index = count++;
			if(index >= labels.length){
				extend();
			}
			if(node.isTerminated()){
				term.set(index);
			}
			Node[] children = node.getChildren();
			if(children != null){
				for(Node c : children){
					bv.append(true);
					queue.offerLast(c);
				}
			}
			bv.append(false);
			char[] letters = node.getLetters();
			if(letters.length == 0){
				labels[index] = 0xffff;
				tail[index] = -1;
			} else{
				labels[index] = letters[0];
				if(letters.length >= 2){
					tail[index] = tb.insert(letters, 1, letters.length - 1);
				} else{
					tail[index] = -1;
				}
			}
		}
		size = count;
		tails = tb.getTails();
		trimToSize();
	}

	public Node getRoot(){
		return new LOUDSNode(1);
	}

	public boolean contains(String word){
		char[] chars = word.toCharArray();
		int charsIndex = 0;
		int nodeId = 1;
		while(true){
			int start = bv.select0(nodeId) + 1;
			int end = bv.next0(start);
			int baseNodeId = bv.rank1(start) - start;
			while(start != end){
				int i = (start + end) / 2;
				int index = baseNodeId + i;
				int d = chars[charsIndex] - labels[index];
				if(d < 0){
					end = i;
				} else if(d > 0){
					if(start == i) return false;
					else start = i;
				} else{
					charsIndex++;
					int ti = tail[index];
					boolean tm = term.get(index);
					if(charsIndex == chars.length){
						return (ti == -1) && tm;
					}
					if(ti != -1){
						TailCharIterator tci = new TailCharIterator(tails, ti);
						while(tci.hasNext()){
							if(charsIndex == chars.length) return false;
							if(tci.next() != chars[charsIndex]) return false;
							charsIndex++;
						}
						if(charsIndex == chars.length) return tm;
					}
					nodeId = baseNodeId + i;
					break;
				}
			}
			if(start == end) return false;
		}
	}

	public class LOUDSNode implements Node{
		public LOUDSNode(int nodeId) {
			this.nodeId = nodeId;
		}
		public int getId(){
			return nodeId;
		}
		@Override
		public char[] getLetters() {
			StringBuilder b = new StringBuilder();
			char h = labels[nodeId];
			if(h != 0xffff){
				b.append(h);
			}
			int ti = tail[nodeId];
			if(ti != -1){
				TailUtil.appendChars(tails, ti, b);
			}
			return b.toString().toCharArray();
		}
		@Override
		public boolean isTerminated() {
			return term.get(nodeId);
		}
		@Override
		public Node[] getChildren() {
			int start = 0;
			if(nodeId > 0){
				start = bv.select0(nodeId) + 1;
			}
			int end = bv.next0(start);
			int ci = bv.rank1(start);
			int n = end - start;
			Node[] children = new Node[n];
			for(int i = 0; i < n; i++){
				children[i] = new LOUDSNode(ci + i);
			}
			return children;
		}
		
		private int nodeId;
	}

	private void extend(){
		int nsz = (int)(labels.length * 1.2);
		char[] nl = new char[nsz];
		System.arraycopy(labels, 0, nl, 0, labels.length);
		labels = nl;
		int[] nt = new int[nsz];
		System.arraycopy(tail, 0, nt, 0, tail.length);
		tail = nt;
	}

	public void trimToSize(){
		if(labels.length > size){
			char[] nl = new char[size];
			System.arraycopy(labels, 0, nl, 0, size);
			labels = nl;
			int[] nt = new int[size];
			System.arraycopy(tail, 0, nt, 0, size);
			tail = nt;
		}
		bv.trimToSize();
	}

	private BitVector bv;
	private char[] labels;
	private int[] tail;
	private CharSequence tails;
	private BitSet term;
	private int size;
}
