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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.TrieVisitor;
import org.trie4j.util.Pair;

public class TailCompactionDoubleArray implements Trie{
	private static final int BASE_EMPTY = Integer.MAX_VALUE;

	public TailCompactionDoubleArray(Trie trie){
		this(trie, 65536);
	}

	public TailCompactionDoubleArray(Trie trie, int arraySize){
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new int[arraySize];
		Arrays.fill(check, -1);
		tail = new int[arraySize];
		Arrays.fill(tail, -1);
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
		int charsIndex = 0;
		int nodeIndex = 0;
		while(charsIndex < chars.length){
			int tailIndex = tail[nodeIndex];
			if(tailIndex != -1){
				char c = tailBuf.charAt(tailIndex);
				while(c != '\0'){
					if(chars.length <= charsIndex) return false;
					if(chars[charsIndex] != c) return false;
					charsIndex++;
					tailIndex++;
					c = tailBuf.charAt(tailIndex);
				}
				if(chars.length == charsIndex){
					if(c == '\0') return term.get(nodeIndex);
					else return false;
				}
			}
			int cid = findCharId(chars[charsIndex]);
			if(cid == -1) return false;
			int i = cid + base[nodeIndex];
			if(i < 0 || check.length <= i || check[i] != nodeIndex) return false;
			charsIndex++;
			nodeIndex = i;
		}
		return term.get(nodeIndex);
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		List<String> ret = new ArrayList<String>();

		char[] chars = query.toCharArray();
		int ci = 0;
		int ni = 0;
		if(tail[0] != -1){
			int ti = tail[0];
			while(true){
				char c = tailBuf.charAt(ti);
				if(c == '\0'){
					break;
				}
				ci++;
				if(ci >= chars.length) return ret;
				if(c != chars[ci]) return ret;
				ti++;
			}
			if(term.get(0)) ret.add(new String(chars, 0, ci + 1));
		}
		for(; ci < chars.length; ci++){
			int cid = findCharId(chars[ci]);
			if(cid == -1) return ret;
			int b = base[ni];
			if(b == BASE_EMPTY) return ret;
			int next = b + cid;
			if(check.length <= next || check[next] != ni) return ret;
			ni = next;
			if(tail[ni] != -1){
				int ti = tail[ni];
				while(true){
					char c = tailBuf.charAt(ti);
					if(c == '\0'){
						break;
					}
					ci++;
					if(ci >= chars.length) return ret;
					if(c != chars[ci]) return ret;
					ti++;
				}
			}
			if(term.get(ni)) ret.add(new String(chars, 0, ci + 1));
		}
		return ret;
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		List<String> ret = new ArrayList<String>();
		StringBuilder current = new StringBuilder();
		char[] chars = prefix.toCharArray();
		int nodeIndex = 0;
		for(int i = 0; i < chars.length; i++){
			int ti = tail[nodeIndex];
			if(ti != -1){
				do{
					char c = tailBuf.charAt(ti);
					if(c == '\0') break;
					if(c != chars[i]) return ret;
					i++;
					ti++;
				} while(i < chars.length);
				if(i >= chars.length) break;
				current.append(tailBuf.substring(tail[nodeIndex], ti));
			}
			int cid = findCharId(chars[i]);
			if(cid == -1) return ret;
			int next = base[nodeIndex] + cid;
			if(next < 0 || check.length <= next || check[next] != nodeIndex) return ret;
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
				while(tailBuf.charAt(ti) != '\0'){
					buff.append(tailBuf.charAt(ti++));
				}
			}
			if(term.get(ni)) ret.add(buff.toString());
			for(Map.Entry<Character, Integer> e : charCodes.entrySet()){
				int b = base[ni];
				if(b == BASE_EMPTY) continue;
				int next = b + e.getValue();
				if(check.length <= next) continue;
				if(check[next] == ni){
					StringBuilder bu = new StringBuilder(buff);
					bu.append(e.getKey());
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

	@Override
	public void visit(TrieVisitor visitor) {
		throw new UnsupportedOperationException();
	}

	public void dump(){
		System.out.println("--- dump Double Array ---");
		System.out.println("array size: " + base.length);
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
		System.out.println("tailBuf: [" + tailBuf.toString().substring(0, Math.min(tailBuf.length(), 32)).replace("\0", "\\0") + "]");
		System.out.println("tailBuf size: " + tailBuf.length());
		{
			System.out.print("chars: ");
			int c = 0;
			for(Map.Entry<Character, Integer> e : charCodes.entrySet()){
				System.out.print(String.format("%c:%d,", e.getKey(), e.getValue()));
				c++;
				if(c > 16) break;
			}
			System.out.println();
			System.out.println("chars count: " + charCodes.size());
		}
		{
			System.out.println("max and min index.");
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
		System.out.println();
	}

	private void build(Node node, int nodeIndex){
		// letters
		char[] letters = node.getLetters();
		if(letters != null){
			if(letters.length > 1){
				tail[nodeIndex] = tailBuf.length();
				tailBuf.append(letters, 1, letters.length - 1)
					.append('\0');
			}
			if(node.isTerminated()){
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
	private int[] tail;
	private int firstEmptyCheck;
	private BitSet term;
	private StringBuilder tailBuf = new StringBuilder();
	private Map<Character, Integer> charCodes = new TreeMap<Character, Integer>(new Comparator<Character>(){
		@Override
		public int compare(Character arg0, Character arg1) {
			return arg1 - arg0;
		}
	});
}
