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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.trie4j.AbstractTrie;
import org.trie4j.IdTrie;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.bv.BytesSuccinctBitVector;
import org.trie4j.bv.SuccinctBitVector;
import org.trie4j.louds.bvtree.BvTree;
import org.trie4j.louds.bvtree.LOUDSBvTree;
import org.trie4j.tail.ConcatTailArray;
import org.trie4j.tail.TailArray;
import org.trie4j.tail.TailCharIterator;
import org.trie4j.util.Pair;
import org.trie4j.util.Range;

public class AbstractTailLOUDSTrie extends AbstractTrie implements IdTrie {
	public AbstractTailLOUDSTrie(){
	}

	public AbstractTailLOUDSTrie(Trie orig){
		this(orig, new LOUDSBvTree(orig.size()), new ConcatTailArray(orig.size() * 3));
	}

	public AbstractTailLOUDSTrie(Trie orig, BvTree bvtree, TailArray tailArray){
		this.tailArray = tailArray;
		this.bvtree = bvtree;
		this.size = orig.size();
		this.labels = new char[size];
		this.term = new BitSet(size);
		this.termBV = new BytesSuccinctBitVector(size);
		LinkedList<Node> queue = new LinkedList<Node>();
		int count = 0;
		if(orig.getRoot() != null) queue.add(orig.getRoot());
		while(!queue.isEmpty()){
			Node node = queue.pollFirst();
			int index = count++;
			if(index >= labels.length){
				extend();
			}
			if(node.isTerminate()) {
				term.set(index);
				termBV.append1();
			} else {
				termBV.append0();
			}
			for(Node c : node.getChildren()){
				bvtree.appendChild();
				queue.offerLast(c);
			}
			bvtree.appendSelf();
			char[] letters = node.getLetters();
			if(letters.length == 0){
				labels[index] = 0xffff;
				tailArray.appendEmpty();
			} else{
				labels[index] = letters[0];
				if(letters.length >= 2){
					tailArray.append(letters, 1, letters.length - 1);
				} else{
					tailArray.appendEmpty();
				}
			}
		}
		nodeSize = count;
		tailArray.freeze();
	}

	public BvTree getBvTree() {
		return bvtree;
	}

	public char[] getLabels(){
		return labels;
	}

	public TailArray getTailArray(){
		return tailArray;
	}

	public BitSet getTerm(){
		return term;
	}

	@Override
	public Node getRoot(){
		return new LOUDSNode(0);
	}

	@Override
	public void dump(Writer writer) throws IOException{
		super.dump(writer);
		writer.write(bvtree.toString());
		writer.write("\nlabels: ");
		int count = 0;
		for(char c : labels){
			writer.write(c);
			if(count++ == 99) break;
		}
		writer.write("\n");
	}
	
	@Override
	public int getId(String text) {
		int nodeId = 0; // root
		Range r = new Range();
		TailCharIterator it = tailArray.newIterator();
		int n = text.length();
		for(int i = 0; i < n; i++){
			nodeId = getChildNode(nodeId, text.charAt(i), r);
			if(nodeId == -1) return -1;
			it.setOffset(tailArray.getIteratorOffset(nodeId));
			while(it.hasNext()){
				i++;
				if(i == n) return -1;
				if(text.charAt(i) != it.next()) return -1;
			}
		}
		return termBV.rank1(nodeId) - 1;
	}

	@Override
	public boolean contains(String text){
		return getId(text) != -1;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Iterable<Pair<String, Integer>> commonPrefixSearchId(String query) {
		List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
		char[] chars = query.toCharArray();
		int charsLen = chars.length;
		int nodeId = 0; // root
		TailCharIterator tci = tailArray.newIterator();
		Range r = new Range();
		for(int charsIndex = 0; charsIndex < charsLen; charsIndex++){
			int child = getChildNode(nodeId, chars[charsIndex], r);
			if(child == -1) return ret;
			tci.setOffset(tailArray.getIteratorOffset(child));
			while(tci.hasNext()){
				charsIndex++;
				if(charsLen <= charsIndex) return ret;
				if(chars[charsIndex] != tci.next()) return ret;
			}
			if(term.get(child)){
				ret.add(new Pair<String, Integer>(new String(chars, 0, charsIndex + 1),
						termBV.rank1(child) - 1));
			}
			nodeId = child;
		}
		return ret;
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		Iterable<Pair<String, Integer>> retID = commonPrefixSearchId(query);
		List<String> ret = new ArrayList<String>();
		for (Pair<String, Integer> pair : retID) {
			ret.add(pair.getFirst());
		}
		return ret;
	}

	@Override
	public Iterable<Pair<String, Integer>> predictiveSearchId(String query) {
		List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
		char[] chars = query.toCharArray();
		int charsLen = chars.length;
		int nodeId = 0; // root
		Range r = new Range();
		TailCharIterator tci = tailArray.newIterator();
		String pfx = null;
		int charsIndexBack = 0;
		for(int charsIndex = 0; charsIndex < charsLen; charsIndex++){
			charsIndexBack = charsIndex;
			int child = getChildNode(nodeId, chars[charsIndex], r);
			if(child == -1) return ret;
			tci.setOffset(tailArray.getIteratorOffset(child));
			while(tci.hasNext()){
				charsIndex++;
				if(charsIndex >= charsLen) break;
				if(chars[charsIndex] != tci.next()) return ret;
			}
			nodeId = child;
		}
		pfx = new String(chars, 0, charsIndexBack);

		Deque<Pair<Integer, String>> queue = new LinkedList<Pair<Integer,String>>();
		queue.offerLast(Pair.create(nodeId, pfx));
		while(queue.size() > 0){
			Pair<Integer, String> element = queue.pollFirst();
			int nid = element.getFirst();

			StringBuilder b = new StringBuilder(element.getSecond());
			b.append(labels[nid]);
			tci.setOffset(tailArray.getIteratorOffset(nid));
			while(tci.hasNext()) b.append(tci.next());
			String letter = b.toString();
			if(term.get(nid)) ret.add(new Pair<String, Integer>(letter, termBV.rank1(nid) - 1));
			bvtree.getChildNodeIds(nid, r);
			for(int i = (r.getEnd() - 1); i >= r.getStart(); i--){
				queue.offerFirst(Pair.create(i, letter));
			}
		}
		return ret;
	}
	
	@Override
	public Iterable<String> predictiveSearch(String query) {
		Iterable<Pair<String, Integer>> retID = predictiveSearchId(query);
		List<String> ret = new ArrayList<String>();
		for (Pair<String, Integer> pair : retID) {
			ret.add(pair.getFirst());
		}
		return ret;
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
			int ti = tailArray.getIteratorOffset(nodeId);
			if(ti != -1){
				TailCharIterator it = tailArray.newIterator(ti);
				it.setOffset(ti);
				while(it.hasNext()) b.append(it.next());
			}
			return b.toString().toCharArray();
		}
		@Override
		public boolean isTerminate() {
			return term.get(nodeId);
		}
		@Override
		public Node getChild(char c) {
			int nid = getChildNode(nodeId, c, new Range());
			if(nid == -1) return null;
			else return new LOUDSNode(nid);
		}
		@Override
		public Node[] getChildren() {
			Range r = new Range();
			bvtree.getChildNodeIds(nodeId, r);
			Node[] children = new Node[r.getLength()];
			for(int i = r.getStart(); i < r.getEnd(); i++){
				children[i - r.getStart()] = new LOUDSNode(i);
			}
			return children;
		}

		private int nodeId;
	}

	public void trimToSize(){
		if(labels.length > nodeSize){
			labels = Arrays.copyOf(labels, nodeSize);
		}
		bvtree.trimToSize();
		tailArray.trimToSize();
	}

	public void save(OutputStream os) throws IOException{
		DataOutputStream dos = new DataOutputStream(os);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		dos.writeInt(size);
		dos.writeInt(nodeSize);
		dos.flush();
		trimToSize();
		bvtree.save(os);
		for(char c : labels){
			dos.writeChar(c);
		}
		dos.flush();
		tailArray.save(os);
		oos.writeObject(term);
		oos.flush();
	}

	public void load(InputStream is) throws ClassNotFoundException, IOException{
		DataInputStream dis = new DataInputStream(is);
		ObjectInputStream ois = new ObjectInputStream(is);
		size = dis.readInt();
		nodeSize = dis.readInt();
		bvtree.load(is);
		labels = new char[nodeSize];
		for(int i = 0; i < nodeSize; i++){
			labels[i] = dis.readChar();
		}
		tailArray.load(is);
		term = (BitSet)ois.readObject();
		termBV = new BytesSuccinctBitVector(term.length());
		for (int i = 0; i < term.length(); ++i) {
			if (term.get(i)) {
				termBV.append1();
			} else {
				termBV.append0();
			}
		}
	}

	private int getChildNode(int nodeId, char c, Range r){
		bvtree.getChildNodeIds(nodeId, r);
		int start = r.getStart();
		int end = r.getEnd();
		if(end == -1) return -1;
		if((end - start) <= 16){
			for(int i = start; i < end; i++){
				int d = c - labels[i];
				if(d == 0) return i;
			}
			return -1;
		} else{
			do{
				int i = (start + end) / 2;
				int d = c - labels[i];
				if(d < 0){
					end = i;
				} else if(d > 0){
					if(start == i) return -1;
					else start = i;
				} else{
					return i;
				}
			} while(start != end);
			return -1;
		}
	}

	private void extend(){
		int nsz = (int)(labels.length * 1.2);
		if(nsz <= labels.length) nsz = labels.length * 2 + 1;
		labels = Arrays.copyOf(labels, nsz);
	}

	private BvTree bvtree;
	private int size;
	private char[] labels;
	private TailArray tailArray;
	private BitSet term;
	private SuccinctBitVector termBV;
	private int nodeSize;
}
