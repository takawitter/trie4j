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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.trie4j.AbstractTrie;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.tail.SuffixTrieTailBuilder;
import org.trie4j.tail.TailBuilder;
import org.trie4j.tail.TailCharIterator;
import org.trie4j.util.Pair;

public class TailDoubleArray extends AbstractTrie implements Trie{
	private static final int BASE_EMPTY = Integer.MAX_VALUE;

	public TailDoubleArray(){
	}

	public TailDoubleArray(Trie orig){
		this(orig, 65536);
	}

	public TailDoubleArray(Trie orig, int arraySize){
		this(orig, 65536, new SuffixTrieTailBuilder());
	}

	public TailDoubleArray(Trie trie, int arraySize, TailBuilder tb){
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new int[arraySize];
		Arrays.fill(check, -1);
		tail = new int[arraySize];
		Arrays.fill(tail, -1);
		term = new BitSet(65536);
		Arrays.fill(charToCode, (char)0);

		build(trie.getRoot(), 0, tb);
		tails = tb.getTails();
	}

	@Override
	public Node getRoot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(String text){
		try{
			int nodeIndex = 0; // root
			TailCharIterator it = new TailCharIterator(tails, -1);
			int n = text.length();
			for(int i = 0; i < n; i++){
				int cid = findCharId(text.charAt(i));
				if(cid == -1) return false;
				int next = base[nodeIndex] + cid;
				if(check[next] != nodeIndex) return false;
				nodeIndex = next;
				it.setIndex(tail[nodeIndex]);
				while(it.hasNext()){
					i++;
					if(i == n) return false;
					if(text.charAt(i) != it.next()) return false;
				}
			}
			return term.get(nodeIndex);
		} catch(ArrayIndexOutOfBoundsException e){
			return false;
		}
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		List<String> ret = new ArrayList<String>();
		int charsLen = query.length();
		int ci = 0;
		int ni = 0;
		TailCharIterator it = new TailCharIterator(tails, -1);
		for(; ci < charsLen; ci++){
			int cid = findCharId(query.charAt(ci));
			if(cid == -1) return ret;
			int b = base[ni];
			if(b == BASE_EMPTY) return ret;
			int next = b + cid;
			if(check.length <= next || check[next] != ni) return ret;
			ni = next;
			int ti = tail[ni];
			if(ti != -1){
				it.setIndex(ti);
				while(it.hasNext()){
					ci++;
					if(ci >= charsLen) return ret;
					if(it.next() != query.charAt(ci)) return ret;
				}
			}
			if(term.get(ni)) ret.add(query.substring(0, ci + 1));
		}
		return ret;
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		List<String> ret = new ArrayList<String>();
		StringBuilder current = new StringBuilder();
		char[] chars = prefix.toCharArray();
		int charsLen = chars.length;
		int checkLen = check.length;
		int nodeIndex = 0;
		TailCharIterator it = new TailCharIterator(tails,  -1);
		for(int i = 0; i < chars.length; i++){
			int ti = tail[nodeIndex];
			if(ti != -1){
				int first = i;
				it.setIndex(ti);
				do{
					if(!it.hasNext()) break;
					if(it.next() != chars[i]) return ret;
					i++;
				} while(i < charsLen);
				if(i >= charsLen) break;
				current.append(chars, first, i - first);
			}
			int cid = findCharId(chars[i]);
			if(cid == -1) return ret;
			int next = base[nodeIndex] + cid;
			if(next < 0 || checkLen <= next || check[next] != nodeIndex) return ret;
			nodeIndex = next;
			current.append(chars[i]);
		}
		Deque<Pair<Integer, char[]>> q = new LinkedList<Pair<Integer,char[]>>();
		q.add(Pair.create(nodeIndex, current.toString().toCharArray()));
		while(!q.isEmpty()){
			Pair<Integer, char[]> p = q.pop();
			int ni = p.getFirst();
			StringBuilder buff = new StringBuilder().append(p.getSecond());
			int ti = tail[ni];
			if(ti != -1){
				it.setIndex(ti);
				while(it.hasNext()){
					buff.append(it.next());
				}
			}
			if(term.get(ni)) ret.add(buff.toString());
			int b = base[ni];
			if(b == BASE_EMPTY) continue;
			for(char v : this.chars){
				int next = b + charToCode[v];
				if(next >= checkLen) continue;
				if(check[next] == ni){
					StringBuilder bu = new StringBuilder(buff);
					bu.append(v);
					q.push(Pair.create(next, bu.toString().toCharArray()));
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
		for(int v : tail){
			dos.writeInt(v);
		}
		dos.flush();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(term);
		oos.flush();
		dos.writeInt(firstEmptyCheck);
		dos.writeInt(tails.length());
		dos.writeChars(tails.toString());
		dos.writeInt(chars.size());
		for(char c : chars){
			dos.writeChar(c);
			dos.writeChar(charToCode[c]);
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
		tail = new int[len];
		for(int i = 0; i < len; i++){
			tail[i] = dis.readInt();
		}
		ObjectInputStream ois = new ObjectInputStream(bis);
		try{
			term = (BitSet)ois.readObject();
		} catch(ClassNotFoundException e){
			throw new IOException(e);
		}
		firstEmptyCheck = dis.readInt();
		int n = dis.readInt();
		StringBuilder b = new StringBuilder(n);
		for(int i = 0; i < n; i++){
			b.append(dis.readChar());
		}
		tails = b;
		n = dis.readInt();
		for(int i = 0; i < n; i++){
			char c = dis.readChar();
			char v = dis.readChar();
			chars.add(c);
			charToCode[c] = v;
		}
	}

	public void dump(){
		System.out.println("--- dump " + getClass().getSimpleName() + " ---");
		System.out.println("array size: " + base.length);
		System.out.println("last index of valid element: " + last);
		int vc = 0;
		for(int i = 0; i < base.length; i++){
			if(base[i] != BASE_EMPTY || check[i] >= 0) vc++;
		}
		System.out.println("valid elements: " + vc);
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
		System.out.print("|tail |");
		for(int i = 0; i < 16; i++){
			if(tail[i] < 0){
				System.out.print("N/A|");
			} else{
				System.out.print(String.format("%3d|", tail[i]));
			}
		}
		System.out.println();
		System.out.print("|term |");
		for(int i = 0; i < 16; i++){
			System.out.print(String.format("%3d|", term.get(i) ? 1 : 0));
		}
		System.out.println();
		int count = 0;
		for(int i : tail){
			if(i != -1) count++;
		}
		System.out.println("tail count: " + count);
		System.out.println();
		System.out.print("tails: [");
		char[] tailChars = tails.subSequence(0, Math.min(tails.length(), 64)).toString().toCharArray();
		for(int i = 0; i < tailChars.length; i++){
			char c = tailChars[i];
			if(c == '\0'){
				System.out.print("\\0");
				continue;
			}
			if(c == '\1'){
				int index = tailChars[i + 1] + (tailChars[i + 2] << 16);
				i += 2;
				System.out.print(String.format("\\1(%d)", index));
				continue;
			}
			System.out.print(c);
		}
		System.out.println("]");
		System.out.print("tailBuf size: " + tails.length());
		if(tails instanceof StringBuilder){
			System.out.print("(capacity: " + ((StringBuilder)tails).capacity() + ")");
		}
		System.out.println();
		{
			System.out.print("chars: ");
			int c = 0;
			for(char e : chars){
				System.out.print(String.format("%c:%d,", e, (int)charToCode[e]));
				c++;
				if(c > 16) break;
			}
			System.out.println();
			System.out.println("chars count: " + chars.size());
		}
		{
			System.out.println("calculating max and min base.");
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			int maxDelta = Integer.MIN_VALUE;
			for(int i = 0; i < base.length; i++){
				int b = base[i];
				if(b == BASE_EMPTY) continue;
				min = Math.min(min, b);
				max = Math.max(max, b);
				maxDelta = Math.max(maxDelta, Math.abs(i - b));
			}
			System.out.println("maxDelta: " + maxDelta);
			System.out.println("max: " + max);
			System.out.println("min: " + min);
		}
		{
			System.out.println("calculating min check.");
			int min = Integer.MAX_VALUE;
			for(int i = 0; i < base.length; i++){
				int b = check[i];
				min = Math.min(min, b);
			}
			System.out.println("min: " + min);
		}
		System.out.println();
	}

	public void trimToSize(){
		int sz = last + 1;
		int[] nb = new int[sz];
		System.arraycopy(base, 0, nb, 0, sz);
		base = nb;
		int[] nc = new int[sz];
		System.arraycopy(check, 0, nc, 0, sz);
		check = nc;
		int[] nt = new int[sz];
		System.arraycopy(tail, 0, nt, 0, sz);
		tail = nt;
		if(tails instanceof StringBuilder){
			((StringBuilder)tails).trimToSize();
		}
	}

	private void build(Node node, int nodeIndex, TailBuilder tailb){
		// letters
		char[] letters = node.getLetters();
		if(letters.length > 1){
			tail[nodeIndex] = tailb.insert(letters, 1, letters.length - 1);
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
			heads[i] = getCharId(children[i].getLetters()[0]);
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
				return arg0 - arg1;//arg1 - arg0;
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
				build(e2.getFirst(), e2.getSecond() + offset, tailb);
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

	private int getCharId(char c){
		char v = charToCode[c];
		if(v != 0) return v;
		v = (char)(chars.size() + 1);
		chars.add(c);
		charToCode[c] = v;
		return v;
	}

	private int findCharId(char c){
		char v = charToCode[c];
		if(v != 0) return v;
		return -1;
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
		int[] nt = new int[nsz];
		System.arraycopy(tail, 0, nt, 0, sz);
		Arrays.fill(nt, sz, nsz, -1);
		tail = nt;
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
		if(i >= check.length){
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

	private void setCheck(int index, int value){
		if(firstEmptyCheck == index){
			firstEmptyCheck = findNextEmptyCheck(firstEmptyCheck);
		}
		check[index] = value;
		last = Math.max(last, index);
	}

	private int[] base;
	private int[] check;
	private int[] tail;
	private int firstEmptyCheck = 1;
	private int last;
	private BitSet term;
	private CharSequence tails;
	private Set<Character> chars = new TreeSet<Character>();
	private char[] charToCode = new char[Character.MAX_VALUE];
}
