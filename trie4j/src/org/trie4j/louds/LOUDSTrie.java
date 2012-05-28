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

import java.util.LinkedList;

import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.tail.SuffixTrieTailBuilder;
import org.trie4j.tail.TailBuilder;
import org.trie4j.util.BitVector;

public class LOUDSTrie {
	public LOUDSTrie(Trie orig){
		//orig.size();
		int sz = 2000;
		bv = new BitVector(sz);
		labels = new char[sz];
		tail = new int[sz];
		LinkedList<Node> queue = new LinkedList<Node>();
		int count = 0;
		TailBuilder tb = new SuffixTrieTailBuilder();
		queue.add(orig.getRoot());
		while(!queue.isEmpty()){
			Node node = queue.pollFirst();
			int index = count++;
			if(index >= labels.length){
				extend();
			}
			Node[] children = node.getChildren();
			if(children != null){
				for(Node c : children){
					bv.append(true);
					queue.push(c);
				}
			}
			bv.append(false);
			char[] letters = node.getLetters();
			if(letters.length == 0){
				labels[index] = 0xffff;
				tail[index] = -1;
			} else{
				labels[index] = letters[0];
				if(letters.length > 2){
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
	private int size;
}
