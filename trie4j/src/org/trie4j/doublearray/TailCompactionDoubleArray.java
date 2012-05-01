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

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.TrieVisitor;

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
		dic = new BitSet(65536);

		int nodeIndex = 0;
		base[0] = nodeIndex;
		Node root = trie.getRoot();
		if(root == null) return;
		if(root.getLetters() != null){
			if(root.getLetters().length == 0) dic.set(0);
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
		return contains(text.toCharArray(), 0, 0);
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		throw new UnsupportedOperationException();
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
			System.out.print(String.format("%3d|", dic.get(i) ? 1 : 0));
		}
		System.out.println();
		int count = 0;
		for(int i : tail){
			if(i != -1) count++;
		}
		System.out.println("tail count: " + count);
		System.out.println();
		System.out.print("tailBuf: [" + tailBuf.toString().substring(0, Math.min(tailBuf.length(), 32)).replace("\0", "\\0") + "]");
		System.out.println();
		{
			System.out.print("chars: ");
			int c = 0;
			for(Map.Entry<Character, Integer> e : chars.entrySet()){
				System.out.print(String.format("%c:%d,", e.getKey(), e.getValue()));
				c++;
				if(c > 16) break;
			}
			System.out.println();
			System.out.println("chars count: " + chars.size());
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

	private boolean contains(char[] chars, int charsIndex, int nodeIndex){
		if(chars.length == charsIndex){
			return dic.get(nodeIndex);
		}
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
				if(c == '\0') return dic.get(nodeIndex);
				else return false;
			}
		}
		int cid = findCharId(chars[charsIndex]);
		if(cid == -1) return false;
		int i = cid + base[nodeIndex];
		if(i < 0 || check.length <= i || check[i] != nodeIndex) return false;
		return contains(chars, charsIndex + 1, i);
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
				dic.set(nodeIndex);
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
		for(int i = 0; i < children.length; i++){
			build(children[i], offset + heads[i]);
		}
	}

	private int getCharId(char c){
		Integer cid = chars.get(c);
		if(cid == null){
			cid = chars.size() + 1;
			chars.put(c, cid);
		}
		return cid;
	}

	private int findCharId(char c){
		Integer cid = chars.get(c);
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
	private BitSet dic;
	private StringBuilder tailBuf = new StringBuilder();
	private Map<Character, Integer> chars = new HashMap<Character, Integer>();
}
