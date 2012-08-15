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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.trie4j.AbstractTrie;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.tail.SuffixTrieTailBuilder;
import org.trie4j.tail.TailBuilder;
import org.trie4j.tail.TailCharIterator;
import org.trie4j.tail.TailUtil;
import org.trie4j.util.Pair;
import org.trie4j.util.SuccinctBitVector;

public class LOUDSTrie extends AbstractTrie implements Trie {
	public LOUDSTrie(){
	}

	public LOUDSTrie(Trie orig){
		this(orig, 65536);
	}

	public LOUDSTrie(Trie orig, int bitSize){
		this(orig, bitSize, new SuffixTrieTailBuilder());
	}

	public LOUDSTrie(Trie orig, int bitSize, TailBuilder tb){
		bv = new SuccinctBitVector(bitSize);
		labels = new char[bitSize / 2];
		tail = new int[bitSize / 2];
		term = new BitSet(bitSize / 2);
		LinkedList<Node> queue = new LinkedList<Node>();
		int count = 0;
		if(orig.getRoot() != null) queue.add(orig.getRoot());
		while(!queue.isEmpty()){
			Node node = queue.pollFirst();
			int index = count++;
			if(index >= labels.length){
				extend();
			}
			if(node.isTerminate()){
				term.set(index);
			}
			Node[] children = node.getChildren();
			if(children != null){
				for(Node c : children){
					bv.append1();
					queue.offerLast(c);
				}
			}
			bv.append0();
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
	}

	public SuccinctBitVector getBv() {
		return bv;
	}

	public Node getRoot(){
		return new LOUDSNode(0);
	}

	@Override
	public void dump() {
		super.dump();
		System.out.println("bitvec: " + bv.toString());
		System.out.print("labels: ");
		for(char c : labels) System.out.print(c);
		System.out.println();
	}

	@Override
	public boolean contains(String word){
		char[] chars = word.toCharArray();
		int charsIndex = 0;
		int nodeId = 0;
//		LapTimer lt = new LapTimer();
		TailCharIterator tci = new TailCharIterator(tails, -1);
		while(true){
//			lt.lap();
			int start = bv.select0(nodeId) + 1;
//			select0Time += lt.lap();
			int end = bv.next0(start);
			if(end == -1) return false;
//			next0Time += lt.lap();
			int baseNodeId = bv.rank1(start) - start;
//			rank1Time += lt.lap();
			if((end - start) <= 16){
				int i = start;
				int index = baseNodeId + start;
				for(; i < end; i++){
					int d = chars[charsIndex] - labels[index];
					if(d == 0){
						charsIndex++;
						int ti = tail[index];
						if(charsIndex == chars.length){
							return (ti == -1) && term.get(index);
						}
						if(ti != -1){
							tci.setIndex(ti);
							while(tci.hasNext()){
								if(charsIndex == chars.length) return false;
								if(tci.next() != chars[charsIndex]) return false;
								charsIndex++;
							}
							if(charsIndex == chars.length) return term.get(index);
						}
						nodeId = baseNodeId + i;
						break;
					}
					index++;
				}
				if(i == end) return false;
			} else{
				do{
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
						if(charsIndex == chars.length){
							return (ti == -1) && term.get(index);
						}
						if(ti != -1){
							tci.setIndex(ti);
							while(tci.hasNext()){
								if(charsIndex == chars.length) return false;
								if(tci.next() != chars[charsIndex]) return false;
								charsIndex++;
							}
							if(charsIndex == chars.length) return term.get(index);
						}
						nodeId = baseNodeId + i;
						break;
					}
				} while(start != end);
				if(start == end) return false;
			}
		}
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		List<String> ret = new ArrayList<String>();
		char[] chars = query.toCharArray();
		int charsIndex = 0;
		int nodeId = 1;
		int start = 0;
//		LapTimer lt = new LapTimer();
		while(true){
//			lt.lap();
			start = bv.select0(nodeId) + 1;
//			select0Time += lt.lap();
			int end = bv.next0(start);
//			next0Time += lt.lap();
			int baseNodeId = bv.rank1(start) - start;
//			rank1Time += lt.lap();
			while(start != end){
				int i = (start + end) / 2;
				int index = baseNodeId + i;
				int d = chars[charsIndex] - labels[index];
				if(d < 0){
					end = i;
				} else if(d > 0){
					if(start == i) return ret;
					else start = i;
				} else{
					int ti = tail[index];
					boolean tm = term.get(index);
					if(ti != -1){
						TailCharIterator tci = new TailCharIterator(tails, ti);
						while(tci.hasNext()){
							charsIndex++;
							if(charsIndex == chars.length) return ret;
							if(tci.next() != chars[charsIndex]) return ret;
						}
					} else{
						charsIndex++;
						if(charsIndex == chars.length) return ret;
					}
					if(tm) ret.add(new String(chars, 0, charsIndex));
					nodeId = baseNodeId + i;
					break;
				}
			}
			if(start == end) return ret;
		}
	}
	
	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		List<String> ret = new ArrayList<String>();
		char[] chars = prefix.toCharArray();
		int charsIndex = 0;
		int nodeId = 1;
		int start = 0;
		
		int rootNodeId = -1;
		String pfx = null;

//		LapTimer lt = new LapTimer();
		while(true){
//			lt.lap();
			start = bv.select0(nodeId) + 1;
//			select0Time += lt.lap();
			int end = bv.next0(start);
//			next0Time += lt.lap();
			int baseNodeId = bv.rank1(start) - start;
//			rank1Time += lt.lap();
			while(start != end){
				int i = (start + end) / 2;
				int index = baseNodeId + i;
				int d = chars[charsIndex] - labels[index];
				int prevCharsIndex = charsIndex;
				if(d < 0){
					end = i;
				} else if(d > 0){
					if(start == i) return ret;
					else start = i;
				} else{
					charsIndex++;
					if(charsIndex == chars.length){
						rootNodeId = baseNodeId + i;
						pfx = new String(chars, 0, prevCharsIndex);
						break;
					}
					int ti = tail[index];
					if(ti != -1){
						TailCharIterator tci = new TailCharIterator(tails, ti);
						while(tci.hasNext()){
							if(charsIndex == chars.length){
								rootNodeId = baseNodeId + i;
								pfx = new String(chars, 0, prevCharsIndex);
								break;
							}
							if(tci.next() != chars[charsIndex]) return ret;
							charsIndex++;
						}
					}
					nodeId = baseNodeId + i;
					break;
				}
			}
			if(pfx != null) break;
			if(start == end) return ret;
		}
		if(pfx == null) return ret;

		Deque<Pair<Integer, String>> queue = new LinkedList<Pair<Integer,String>>();
		queue.offerLast(Pair.create(rootNodeId, pfx));
		while(queue.size() > 0){
			Pair<Integer, String> element = queue.pollFirst();
			int nid = element.getFirst();

			StringBuilder b = new StringBuilder(element.getSecond());
			b.append(labels[nid]);
			int ti = tail[nid];
			if(ti != -1){
				TailCharIterator it = new TailCharIterator(tails, tail[nid]);
				while(it.hasNext()){
					b.append(it.next());
				}
			}
			String letter = b.toString();
			if(term.get(nid)) ret.add(letter);
			int s = bv.select0(nid) + 1;
			int e = bv.next0(s);
			int lastNodeId = bv.rank1(s) + e - s - 1;
			for(int i = (e - 1); i >= s; i--){
				queue.offerFirst(Pair.create(lastNodeId--, letter));
			}
		}
		return ret;
	}
	
	@Override
	public void insert(String word) {
		throw new UnsupportedOperationException();
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
		public boolean isTerminate() {
			return term.get(nodeId);
		}
		@Override
		public Node getChild(char c) {
			int start = bv.select0(nodeId) + 1;
			int end = bv.next0(start);
			int baseNodeId = bv.rank1(start) - start;
			while(start != end){
				int i = (start + end) / 2;
				int index = baseNodeId + i;
				int d = c - labels[index];
				if(d < 0){
					end = i;
				} else if(d > 0){
					if(start == i) return null;
					else start = i;
				} else{
					return new LOUDSNode(index);
				}
			}
			return null;
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

	public void save(OutputStream os) throws IOException{
		DataOutputStream dos = new DataOutputStream(os);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		dos.writeInt(size);
		trimToSize();
		for(char c : labels){
			dos.writeChar(c);
		}
		for(int i : tail){
			dos.writeInt(i);
		}
		dos.writeInt(tails.length());
		for(int i = 0; i < tails.length(); i++){
			dos.writeChar(tails.charAt(i));
		}
		dos.flush();
		oos.writeObject(term);
		oos.flush();
		bv.save(os);
	}

	public void load(InputStream is) throws ClassNotFoundException, IOException{
		DataInputStream dis = new DataInputStream(is);
		ObjectInputStream ois = new ObjectInputStream(is);
		size = dis.readInt();
		labels = new char[size];
		for(int i = 0; i < size; i++){
			labels[i] = dis.readChar();
		}
		tail = new int[size];
		for(int i = 0; i < size; i++){
			tail[i] = dis.readInt();
		}
		int ts = dis.readInt();
		StringBuilder b = new StringBuilder(ts);
		for(int i = 0; i < ts; i++){
			b.append(dis.readChar());
		}
		tails = b;
		term = (BitSet)ois.readObject();
		bv = new SuccinctBitVector();
		bv.load(is);
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

	private SuccinctBitVector bv;
	private char[] labels;
	private int[] tail;
	private CharSequence tails;
	private BitSet term;
	private int size;
/*
	public long getSelect0Time() {
		return select0Time;
	}
	public long getNext0Time() {
		return next0Time;
	}
	public long getRank1Time() {
		return rank1Time;
	}
	private long select0Time;
	private long next0Time;
	private long rank1Time;
//*/
}
