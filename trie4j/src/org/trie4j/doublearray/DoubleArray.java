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
package org.trie4j.doublearray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.trie4j.AbstractTrie;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.util.Pair;

public class DoubleArray extends AbstractTrie implements Trie{
	private static final int BASE_EMPTY = Integer.MAX_VALUE;

	public DoubleArray(Trie trie){
		this(trie, 65536);
	}

	public DoubleArray(Trie trie, int arraySize){
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new int[arraySize];
		Arrays.fill(check, -1);
		term = new BitSet(65536);

		int nodeIndex = 0;
		base[0] = nodeIndex;
		Node root = trie.getRoot();
		if(root == null) return;
		if(root.getLetters() != null){
			if(root.getLetters().length == 0) term.set(0);
			else{
				int c = getCharId(root.getLetters()[0]);
				check[c] = 0;
				nodeIndex = c;
				firstEmptyCheck = 1;
			}
		}
		build(root, nodeIndex);
	}

	@Override
	public Node getRoot() {
		throw new UnsupportedOperationException();
	}

	public boolean contains(String text){
		char[] chars = text.toCharArray();
		int nodeIndex = 0;
		for(int i = 0; i < chars.length; i++){
			int cid = findCharId(chars[i]);
			if(cid == -1) return false;
			int next = base[nodeIndex] + cid;
			if(next < 0 || check.length <= next || check[next] != nodeIndex) return false;
			nodeIndex = next;
		}
		return term.get(nodeIndex);
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		List<String> ret = new ArrayList<String>();
		char[] chars = query.toCharArray();
		int nodeIndex = 0;
		for(int i = 0; i < chars.length; i++){
			int cid = findCharId(chars[i]);
			if(cid == -1) return ret;
			int b = base[nodeIndex];
			if(b == BASE_EMPTY) return ret;
			int next = b + cid;
			if(check.length <= next || check[next] != nodeIndex) return ret;
			nodeIndex = next;
			if(term.get(nodeIndex)) ret.add(new String(chars, 0, i + 1));
		}
		return ret;
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		List<String> ret = new ArrayList<String>();
		char[] chars = prefix.toCharArray();
		int nodeIndex = 0;
		for(int i = 0; i < chars.length; i++){
			int cid = findCharId(chars[i]);
			if(cid == -1) return ret;
			int next = base[nodeIndex] + cid;
			if(next < 0 || check.length <= next || check[next] != nodeIndex) return ret;
			nodeIndex = next;
		}
		if(term.get(nodeIndex)){
			ret.add(prefix);
		}
		Deque<Pair<Integer, char[]>> q = new LinkedList<Pair<Integer,char[]>>();
		q.add(Pair.create(nodeIndex, chars));
		while(!q.isEmpty()){
			Pair<Integer, char[]> p = q.pop();
			int ni = p.getFirst();
			int b = base[ni];
			if(b == BASE_EMPTY) continue;
			char[] c = p.getSecond();
			for(Map.Entry<Character, Integer> e : charCodes.entrySet()){
				int next = b + e.getValue();
				if(check.length <= next) continue;
				if(check[next] == ni){
					String n = new StringBuilder().append(c).append(e.getKey()).toString();
					if(term.get(next)){
						ret.add(n);
					}
					q.push(Pair.create(next, n.toCharArray()));
				}
			}
		}
		return ret;
	}

	/**
	 * Double Array currently not support dynamic construction.
	 */
	@Override
	public void insert(String word) {
		throw new UnsupportedOperationException();
	}

	public void save(OutputStream os) throws IOException{
		BufferedOutputStream bos = new BufferedOutputStream(os);
		DataOutputStream dos = new DataOutputStream(bos);
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
		dos.writeInt(charCodes.size());
		for(Map.Entry<Character, Integer> e : charCodes.entrySet()){
			dos.writeChar(e.getKey());
			dos.writeInt(e.getValue());
		}
		dos.flush();
		
		bos.flush();
	}

	public void load(InputStream is) throws IOException{
		BufferedInputStream bis = new BufferedInputStream(is);
		DataInputStream dis = new DataInputStream(bis);
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
			char c = dis.readChar();
			int v = dis.readInt();
			charCodes.put(c, v);
		}
	}

	public void dump(){
		System.out.println("array size: " + base.length);
		System.out.print("      |");
		for(int i = 0; i < 16; i++){
			System.out.print(String.format("%3d|", i));
		}
		System.out.println();
		System.out.print("|base |");
		for(int i = 0; i < 16; i++){
			if(base[i] == BASE_EMPTY){
				System.out.print("N/A|");
			} else{
				System.out.print(String.format("%3d|", base[i]));
			}
		}
		System.out.println();
		System.out.print("|check|");
		for(int i = 0; i < 16; i++){
			if(check[i] < 0){
				System.out.print("N/A|");
			} else{
				System.out.print(String.format("%3d|", check[i]));
			}
		}
		System.out.println();
		System.out.print("|term |");
		for(int i = 0; i < 16; i++){
			System.out.print(String.format("%3d|", term.get(i) ? 1 : 0));
		}
		System.out.println();
		System.out.print("chars: ");
		for(Map.Entry<Character, Integer> e : charCodes.entrySet()){
			System.out.print(String.format("%c:%d,", e.getKey(), e.getValue()));
		}
		System.out.println();
	}

	@Override
	public void trimToSize() {
	}

	static class WorkNode{
		private char[] letters;
		private int childNodeIndex;
	}

	private void build(Iterator<String> it){
		String prev = null;
		while(it.hasNext()){
			String word = it.next();
			if(prev == null){
				prev = word;
				continue;
			}
			int n = Math.min(prev.length(), word.length());
			int i = 0;
			for(;i < n; i++){
				if(prev.charAt(i) != word.charAt(i)) break;
			}
			if(i == n) continue;
		}
	}

	private void build(Node node, int nodeIndex){
		// letters
		char[] letters = node.getLetters();
		if(letters != null){
			for(int i = 1; i < letters.length; i++){
				char c = letters[i];
				int cid = getCharId(c);
				int empty = findFirstEmptyCheck();
				setCheck(empty, nodeIndex);
				base[nodeIndex] = empty - cid;
				nodeIndex = empty;
			}
			if(node.isTerminate()){
				term.set(nodeIndex);
			}
		}

		// children
		Node[] children = node.getChildren();
		if(children == null || children.length == 0) return;
		int[] heads = new int[children.length];
		int maxHead = 0;
		for(int i = 0; i < children.length; i++){
			heads[i] = getCharId(children[i].getLetters()[0]);
			maxHead = Math.max(maxHead, heads[i]);
		}
		int empty = findFirstEmptyCheck();
		int offset = empty - heads[0];
		while(true){
			if(check.length <= (offset + maxHead)){
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
			if(found) break;

			empty = findNextEmptyCheck(empty);
			offset = empty - heads[0];
		}
		base[nodeIndex] = offset;
		for(int cid : heads){
			setCheck(offset + cid, nodeIndex);
		}
/*
		for(int i = 0; i < children.length; i++){
			build(children[i], offset + heads[i]);
		}
/*/
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

	private int getCharId(char c){
		Integer cid = charCodes.get(c);
		if(cid == null){
			cid = charCodes.size() + 1;
			charCodes.put(c, cid);
		}
		return cid;
	}

	private int findCharId(char c){
		Integer cid = charCodes.get(c);
		if(cid == null){
			return -1;
		}
		return cid;
	}

	private void extend(int i){
		int sz = base.length;
		int nsz = Math.max(i, (int)(sz * 1.5));
//		System.out.println("extend to " + nsz);
		int[] nb = new int[nsz];
		System.arraycopy(base, 0, nb, 0, sz);
		Arrays.fill(nb, sz, nsz, BASE_EMPTY);
		base = nb;
		int[] nc = new int[nsz];
		System.arraycopy(check, 0, nc, 0, sz);
		Arrays.fill(nc, sz, nsz, -1);
		check = nc;
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
	}

	private int[] base;
	private int[] check;
	private int firstEmptyCheck;
	private BitSet term;
	private Map<Character, Integer> charCodes = new HashMap<Character, Integer>();
}
