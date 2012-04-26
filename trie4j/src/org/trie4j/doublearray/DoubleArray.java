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
import java.util.Map;
import java.util.TreeMap;

import org.trie4j.Node;
import org.trie4j.Trie;

public class DoubleArray {
	private static final int BASE_EMPTY = Integer.MAX_VALUE;

	public DoubleArray(Trie trie){
		this(trie, 65536);
	}

	public DoubleArray(Trie trie, int arraySize){
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new int[arraySize];
		Arrays.fill(check, -1);
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

	public boolean contains(String text){
		return contains(text.toCharArray(), 0, 0);
	}

	public void dump(){
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
			System.out.print(String.format("%3d|", dic.get(i) ? 1 : 0));
		}
		System.out.println();
		System.out.print("chars: ");
		for(Map.Entry<Character, Integer> e : chars.entrySet()){
			System.out.print(String.format("%c:%d,", e.getKey(), e.getValue()));
		}
		System.out.println();
	}

	private boolean contains(char[] chars, int charsIndex, int nodeIndex){
		if(chars.length == charsIndex){
			return dic.get(nodeIndex);
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
			for(int i = 1; i < letters.length; i++){
				char c = letters[i];
				int cid = getCharId(c);
				int empty = findFirstEmptyCheck();
				setCheck(empty, nodeIndex);
				base[nodeIndex] = empty - cid;
				nodeIndex = empty;
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
	private BitSet dic;
	private Map<Character, Integer> chars = new TreeMap<Character, Integer>();
}
