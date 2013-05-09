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
package org.trie4j.bytes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.trie4j.util.Pair;

public class DoubleArray implements Trie{
	private static final int BASE_EMPTY = Integer.MAX_VALUE;

	public DoubleArray() {
	}

	public DoubleArray(Trie trie){
		this(trie, trie.size() * 2);
	}

	public DoubleArray(Trie trie, int arraySize){
		if(arraySize <= 1) arraySize = 2;
		size = trie.size();
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new int[arraySize];
		Arrays.fill(check, -1);
		term = new BitSet(arraySize);

		build(trie.getRoot(), 0);
	}

	public int size() {
		return size;
	}

	public Node getRoot() {
		return new DoubleArrayNode(0);
	}

	public int[] getBase(){
		return base;
	}

	private class DoubleArrayNode implements Node{
		public DoubleArrayNode(int nodeId){
			this.nodeId = nodeId;
		}

		public DoubleArrayNode(int nodeId, byte firstChar){
			this.nodeId = nodeId;
			this.firstChar = firstChar;
		}

		public boolean isTerminate() {
			int nid = nodeId;
			while(true){
				byte[] children = listupChildChars(nid);
				int n = children.length;
				if(n == 0) return term.get(nid);
				int b = base[nid];
				byte firstChar = children[0];
				int firstNid = b + firstChar;
				if(n > 1){
					return term.get(nid);
				} else{
					if(term.get(firstNid)) return true;
					nid = firstNid; 
				}
			}
		}

		@Override
		public byte[] getLetters() {
			ByteArrayOutputStream ret = new ByteArrayOutputStream();
			if(firstChar != 0) ret.write(firstChar);
			int nid = nodeId;
			while(true){
				if(term.get(nid)) return ret.toByteArray();
				byte[] children = listupChildChars(nid);
				int n = children.length;
				if(n == 0 || n > 1) return ret.toByteArray();
				byte c = children[0];
				ret.write(c);
				nid = base[nid] + c; 
			}
		}

		@Override
		public Node[] getChildren() {
			int nid = nodeId;
			while(true){
				byte[] children = listupChildChars(nid);
				int n = children.length;
				if(n == 0) return emptyNodes;
				int b = base[nid];
				if(n > 1 || term.get(nid)){
					return listupChildNodes(b, children);
				}
				nid = b + children[0];
			}
		}

		@Override
		public Node getChild(byte c) {
			int code = c;
			if(code == -1) return null;
			int nid = base[nodeId] + c;
			if(nid >= 0 && nid < check.length && check[nid] == nodeId) return new DoubleArrayNode(nid, c);
			return null;
		}
		
		@Override
		public void visit(TrieVisitor visitor, int nest) {
			// TODO Auto-generated method stub
			
		}

		private byte[] listupChildChars(int nodeId){
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			int bs = base[nodeId];
			for(byte c : chars){
				int nid = bs + c;
				if(nid >= 0 && nid < check.length && check[nid] == nodeId){
					b.write(c);
				}
			}
			return b.toByteArray();
		}

		private Node[] listupChildNodes(int base, byte[] chars){
			int n = chars.length;
			Node[] ret = new Node[n];
			for(int i = 0; i < n; i++){
				byte c = chars[i];
				byte code = c;
				ret[i] = new DoubleArrayNode(base + code, c);
			}
			return ret;
		}

		private byte firstChar = 0;
		private int nodeId;
	}

	public boolean contains(byte[] bytes){
		int nodeIndex = 0; // root
		for(byte cid : bytes){
			if(cid == 0) return false;
			int next = base[nodeIndex] + cid;
			if(check[next] != nodeIndex) return false;
			nodeIndex = next;
		}
		return term.get(nodeIndex);
	}

	public Iterable<byte[]> commonPrefixSearch(byte[] query) {
		List<byte[]> ret = new ArrayList<byte[]>();
		byte[] chars = query;
		int charsLen = chars.length;
		int checkLen = check.length;
		int nodeIndex = 0;
		for(int i = 0; i < charsLen; i++){
			byte cid = chars[i];
			if(cid == 0) return ret;
			int b = base[nodeIndex];
			if(b == BASE_EMPTY) return ret;
			int next = b + cid;
			if(next >= checkLen || check[next] != nodeIndex) return ret;
			nodeIndex = next;
			if(term.get(nodeIndex)) ret.add(Arrays.copyOf(chars, i + 1));
		}
		return ret;
	}

	public int findWord(byte[] chars, int start, int end, OutputStream word)
	throws IOException{
		for(int i = start; i < end; i++){
			int nodeIndex = 0;
			try{
				for(int j = i; j < end; j++){
					byte cid = chars[j];
					if(cid == 0) break;
					int b = base[nodeIndex];
					if(b == BASE_EMPTY) break;
					int next = b + cid;
					if(nodeIndex != check[next]) break;
					nodeIndex = next;
					if(term.get(nodeIndex)){
						if(word != null) word.write(chars, i, j + 1);
						return i;
					}
				}
			} catch(ArrayIndexOutOfBoundsException e){
				break;
			}
		}
		return -1;
	}

	public Iterable<byte[]> predictiveSearch(byte[] prefix) {
		List<byte[]> ret = new ArrayList<byte[]>();
		byte[] chars = prefix;
		int charsLen = chars.length;
		int checkLen = check.length;
		int nodeIndex = 0;
		for(int i = 0; i < charsLen; i++){
			int cid = chars[i];
			if(cid == 0) return ret;
			int next = base[nodeIndex] + cid;
			if(next < 0 || next >= checkLen || check[next] != nodeIndex) return ret;
			nodeIndex = next;
		}
		if(term.get(nodeIndex)){
			ret.add(prefix);
		}
		Deque<Pair<Integer, byte[]>> q = new LinkedList<Pair<Integer, byte[]>>();
		q.add(Pair.create(nodeIndex, prefix));
		while(!q.isEmpty()){
			Pair<Integer, byte[]> p = q.pop();
			int ni = p.getFirst();
			int b = base[ni];
			if(b == BASE_EMPTY) continue;
			byte[] c = p.getSecond();
			for(byte v : this.chars){
				int next = b + v;
				if(next >= checkLen) continue;
				if(check[next] == ni){
					byte[] n = Arrays.copyOf(c, c.length + 1);
					n[c.length] = v;
					if(term.get(next)){
						ret.add(n);
					}
					q.push(Pair.create(next, n));
				}
			}
		}
		return ret;
	}

	/**
	 * Double Array currently not support dynamic construction.
	 */
	public void insert(byte[] word) {
		throw new UnsupportedOperationException();
	}

	public void save(OutputStream os) throws IOException{
		BufferedOutputStream bos = new BufferedOutputStream(os);
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(size);
		dos.writeInt(base.length);
		for(int v : base){
			dos.writeInt(v);
		}
		for(int v : check){
			dos.writeInt(v);
		}
		dos.flush();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(term);
		oos.flush();
		dos.writeInt(firstEmptyCheck);
		dos.writeInt(chars.size());
		for(byte c : chars){
			dos.write(c);
		}
		dos.flush();
		bos.flush();
	}

	public void load(InputStream is) throws IOException{
		BufferedInputStream bis = new BufferedInputStream(is);
		DataInputStream dis = new DataInputStream(bis);
		size = dis.readInt();
		int len = dis.readInt();
		base = new int[len];
		for(int i = 0; i < len; i++){
			base[i] = dis.readInt();
		}
		check = new int[len];
		for(int i = 0; i < len; i++){
			check[i] = dis.readInt();
		}
		ObjectInputStream ois = new ObjectInputStream(bis);
		try{
			term = (BitSet)ois.readObject();
		} catch(ClassNotFoundException e){
			throw new IOException(e);
		}
		firstEmptyCheck = dis.readInt();
		int n = dis.readInt();
		for(int i = 0; i < n; i++){
			byte c = dis.readByte();
			chars.add(c);
		}
	}

	public void trimToSize(){
		int sz = last + 1 + 0xFFFF;
		base = Arrays.copyOf(base, sz);
		check = Arrays.copyOf(check, sz);
	}

	public void dump(Writer w){
		PrintWriter writer = new PrintWriter(w);
		try{
			writer.println("array size: " + base.length);
			writer.print("      |");
			for(int i = 0; i < 16; i++){
				writer.print(String.format("%3d|", i));
			}
			writer.println();
			writer.print("|base |");
			for(int i = 0; i < 16; i++){
				if(base[i] == BASE_EMPTY){
					writer.print("N/A|");
				} else{
					writer.print(String.format("%3d|", base[i]));
				}
			}
			writer.println();
			writer.print("|check|");
			for(int i = 0; i < 16; i++){
				if(check[i] < 0){
					writer.print("N/A|");
				} else{
					writer.print(String.format("%3d|", check[i]));
				}
			}
			writer.println();
			writer.print("|term |");
			for(int i = 0; i < 16; i++){
				writer.print(String.format("%3d|", term.get(i) ? 1 : 0));
			}
			writer.println();
			writer.print("chars: ");
			int c = 0;
			for(byte e : chars){
				writer.print(String.format("%c:%d,", e, (int)e));
				c++;
				if(c > 16) break;
			}
			writer.println();
			writer.println("chars count: " + chars.size());
			writer.println();
		} finally{
			writer.flush();
		}
	}
	
	@Override
	public void freeze() {
	}

	private void build(Node node, int nodeIndex){
		// letters
		byte[] letters = node.getLetters();
		int lettersLen = letters.length;
		for(int i = 1; i < lettersLen; i++){
			byte cid = letters[i];
			int empty = findFirstEmptyCheck();
			setCheck(empty, nodeIndex);
			base[nodeIndex] = empty - cid;
			nodeIndex = empty;
		}
		if(node.isTerminate()){
			term.set(nodeIndex);
		}

		// children
		Node[] children = node.getChildren();
		int childrenLen = children.length;
		if(childrenLen == 0) return;
		int[] heads = new int[childrenLen];
		int maxHead = 0;
		int minHead = Integer.MAX_VALUE;
		for(int i = 0; i < childrenLen; i++){
			heads[i] = children[i].getLetters()[0];
			maxHead = Math.max(maxHead, heads[i]);
			minHead = Math.min(minHead, heads[i]);
		}

		int offset = findInsertOffset(heads, minHead, maxHead);
		base[nodeIndex] = offset;
		for(int cid : heads){
			setCheck(offset + cid, nodeIndex);
		}
/*
		for(int i = 0; i < children.length; i++){
			build(children[i], offset + heads[i]);
		}
/*/
		// sort children by children's children count.
		Map<Integer, List<Pair<Node, Integer>>> nodes = new TreeMap<Integer, List<Pair<Node, Integer>>>(new Comparator<Integer>() {
			@Override
			public int compare(Integer arg0, Integer arg1) {
				return arg1 - arg0;
			}
		});
		for(int i = 0; i < children.length; i++){
			Node[] c = children[i].getChildren();
			int n = 0;
			if(c != null){
				n = c.length;
			}
			List<Pair<Node, Integer>> p = nodes.get(n);
			if(p == null){
				p = new ArrayList<Pair<Node, Integer>>();
				nodes.put(n, p);
			}
			p.add(Pair.create(children[i], heads[i]));
		}
		for(Map.Entry<Integer, List<Pair<Node, Integer>>> e : nodes.entrySet()){
			for(Pair<Node, Integer> e2 : e.getValue()){
				build(e2.getFirst(), e2.getSecond() + offset);
			}
		}
//*/
	}

	private int findInsertOffset(int[] heads, int minHead, int maxHead){
		for(int empty = findFirstEmptyCheck(); ; empty = findNextEmptyCheck(empty)){
			int offset = empty - minHead;
			if((offset + maxHead) >= check.length){
				extend(offset + maxHead);
			}
			// find space
			boolean found = true;
			for(int cid : heads){
				if(check[offset + cid] >= 0){
					found = false;
					break;
				}
			}
			if(found) return offset;
		}
	}

	private void extend(int i){
		int sz = base.length;
		int nsz = Math.max(i + 0xFFFF, (int)(sz * 1.5));
//		System.out.println("extend to " + nsz);
		base = Arrays.copyOf(base, nsz);
		Arrays.fill(base, sz, nsz, BASE_EMPTY);
		check = Arrays.copyOf(check, nsz);
		Arrays.fill(check, sz, nsz, -1);
	}

	private int findFirstEmptyCheck(){
		int i = firstEmptyCheck;
		while(check[i] >= 0 || base[i] != BASE_EMPTY){
			i++;
		}
		firstEmptyCheck = i;
		return i;
	}

	private int findNextEmptyCheck(int i){
/*
		for(i++; i < check.length; i++){
			if(check[i] < 0) return i;
		}
		extend(i);
		return i;
/*/
		int d = check[i] * -1;
		if(d <= 0){
			throw new RuntimeException();
		}
		int prev = i;
		i += d;
		if(check.length <= i){
			extend(i);
			return i;
		}
		if(check[i] < 0){
			return i;
		}
		for(i++; i < check.length; i++){
			if(check[i] < 0){
				check[prev] = prev - i;
				return i;
			}
		}
		extend(i);
		check[prev] = prev - i;
		return i;
//*/
	}

	private void setCheck(int index, int id){
		if(firstEmptyCheck == index){
			firstEmptyCheck = findNextEmptyCheck(firstEmptyCheck);
		}
		check[index] = id;
		last = Math.max(last, index);
	}

	private int size;
	private int[] base;
	private int[] check;
	private int firstEmptyCheck = 1;
	private int last;
	private BitSet term;
	private Set<Byte> chars = new TreeSet<Byte>();
	private static final Node[] emptyNodes = {};
}
