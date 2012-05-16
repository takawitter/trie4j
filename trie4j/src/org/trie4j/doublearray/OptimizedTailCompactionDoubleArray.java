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
import java.util.TreeMap;

import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.TrieVisitor;
import org.trie4j.util.Pair;

public class OptimizedTailCompactionDoubleArray implements Trie{
	private static final int BASE_EMPTY = Integer.MAX_VALUE;

	class TailTrie{
		public TailTrieNode getRoot(){
			return root;
		}
		public int insert(char[] letters){
			if(root == null){
				tails.append(letters).append('\0');
				root = new TailTrieNode(0, letters.length - 1);
				return 0;
			}
			TailTrieNode responsibleNode = root.insertChild(0,  letters, letters.length - 1);
			if(root.parent != null){
				root = root.parent;
			}
			return responsibleNode.first;
		}
		private TailTrieNode root;
	}

	class TailTrieNode{
		public final char[] emptyChars = {};

		public TailTrieNode(int first, int last) {
			this.first = first;
			this.last = last;
		}
		public TailTrieNode(int first, int last, TailTrieNode parent) {
			this.first = first;
			this.last = last;
			this.parent = parent;
		}

		public TailTrieNode(int first, int last, TailTrieNode parent, TailTrieNode[] children) {
			this.first = first;
			this.last = last;
			this.parent = parent;
			this.children = children;
		}

		public char[] getLetters(StringBuilder tails) {
			return tails.substring(first, last + 1).toCharArray();
		}

		public void setLetters(int first, int last) {
			this.first = first;
			this.last = last;
		}

		public TailTrieNode getParent() {
			return parent;
		}

		public void setParent(TailTrieNode parent) {
			this.parent = parent;
		}

		/**
		 * this.offset this.length
		 * @param childIndex
		 * @param letters
		 * @param offset
		 * @return
		 */
		public TailTrieNode insertChild(int childIndex, char[] letters, int offset){
			int matchedCount = 0;
			int lettersRest = offset + 1;
			int thisLettersLength = this.last - this.first + 1;
			int n = Math.min(lettersRest, thisLettersLength);
			int c = 0;
			while(matchedCount < n && (c = letters[offset - matchedCount] - tails.charAt(this.last - matchedCount)) == 0) matchedCount++;
			if(matchedCount == n){
				if(matchedCount != 0 && lettersRest == thisLettersLength){
					return this;
				}
				if(lettersRest < thisLettersLength){
					TailTrieNode parent = new TailTrieNode(
							this.last - matchedCount + 1, this.last
							, this.parent
							, new TailTrieNode[]{this});
					if(this.parent != null){
						this.parent.getChildren()[childIndex] = parent;
					}
					this.last -= matchedCount;
					this.parent = parent;
					return parent;
				}
				if(children != null){
					int index = 0;
					int end = getChildren().length;
					if(end > 16){
						int start = 0;
						while(start < end){
							index = (start + end) / 2;
							TailTrieNode child = children[index];
							c = letters[offset - matchedCount] - tails.charAt(child.last);
							if(c == 0){
								return child.insertChild(index, letters, offset - matchedCount);
							}
							if(c < 0){
								end = index;
							} else if(start == index){
								index = end;
								break;
							} else{
								start = index;
							}
						}
					} else{
						for(index = 0; index < end; index++){
							TailTrieNode child = getChildren()[index];
							c = letters[offset - matchedCount] - tails.charAt(child.last);
							if(c < 0) break;
							if(c == 0){
								return child.insertChild(index, letters, offset - matchedCount);
							}
						}
					}
					return addChild(index, letters, offset, matchedCount);
				} else{
					return addChild(0, letters, offset, matchedCount);
				}
			}

			TailTrieNode[] newParentsChildren = new TailTrieNode[2];
			TailTrieNode newParent = new TailTrieNode(
					this.last - matchedCount + 1, this.last, this.parent, newParentsChildren
					);
			int newChildFirst = tails.length();
			tails.append(letters, 0, lettersRest - matchedCount);
			int newChildLast = tails.length() - 1;
			if(matchedCount == 0){
				tails.append('\0');
//*
			} else if(matchedCount < 3){
				// make the copy of matched characters because those are too short to share.
				tails.append(letters, lettersRest - matchedCount, matchedCount);
				int cont = this.last + 1;
				if(tails.charAt(cont) == '\0'){
					tails.append('\0');
				} else if(tails.charAt(cont) == '\1'){
					tails.append('\1')
						.append(tails.charAt(cont + 1))
						.append(tails.charAt(cont + 2));
				} else{
					tails.append('\1')
						.append((char)(cont & 0xffff))
						.append((char)((cont & 0xffff0000) >> 16));
				}
//*/
			} else{
				int cont = this.last - matchedCount + 1;
				tails.append('\1')
					.append((char)(cont & 0xffff))
					.append((char)((cont & 0xffff0000) >> 16));
			}
			TailTrieNode newChild = new TailTrieNode(
					newChildFirst, newChildLast, newParent, null
					);
			if(tails.charAt(this.last - matchedCount) < letters[lettersRest - matchedCount - 1]){
				newParentsChildren[0] = this;
				newParentsChildren[1] = newChild;
			} else{
				newParentsChildren[0] = newChild;
				newParentsChildren[1] = this;
			}
			this.last = this.last - matchedCount;
			if(this.parent != null){
				this.parent.getChildren()[childIndex] = newParent;
			}
			this.parent = newParent;
			return newChild;
		}

		public TailTrieNode[] getChildren() {
			return children;
		}

		public void setChildren(TailTrieNode[] children) {
			this.children = children;
		}

		private TailTrieNode addChild(int index, char[] letters, int offset, int matchedCount){
			int newFirst = tails.length();
			tails.append(letters, 0, offset - matchedCount + 1);
			int newLast = tails.length() - 1;
			if(matchedCount == 0){
				tails.append('\0');
//*
			} else if(matchedCount < 3){
				// make the copy of matched characters because those are too short to share.
				tails.append(letters, offset - matchedCount + 1, matchedCount);
				int cont = this.last + 1;
				if(tails.charAt(cont) == '\0'){
					tails.append('\0');
				} else if(tails.charAt(cont) == '\1'){
					tails.append('\1')
						.append(tails.charAt(cont + 1))
						.append(tails.charAt(cont + 2));
				} else{
					tails.append('\1')
						.append((char)(cont & 0xffff))
						.append((char)((cont & 0xffff0000) >> 16));
				}
//*/
			} else{
				int cont = this.last - matchedCount + 1;
				tails.append('\1')
					.append((char)(cont & 0xffff))
					.append((char)((cont & 0xffff0000) >> 16));
			}
			TailTrieNode child = new TailTrieNode(newFirst, newLast, this, null);
			if(children != null){
				TailTrieNode[] newc = new TailTrieNode[children.length + 1];
				System.arraycopy(children,  0, newc, 0, index);
				newc[index] = child;
				System.arraycopy(children,  index, newc, index + 1, children.length - index);
				children = newc;
			} else{
				children = new TailTrieNode[]{child};
			}
			return child;
		}

		private int first;
		private int last;
		private TailTrieNode parent;
		private TailTrieNode[] children;
	}
	
	
	public OptimizedTailCompactionDoubleArray(){
	}
	
	public OptimizedTailCompactionDoubleArray(Trie trie){
		this(trie, 65536);
	}

	public OptimizedTailCompactionDoubleArray(Trie trie, int arraySize){
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new short[arraySize];
		Arrays.fill(check, (short)-1);
		tail = new int[arraySize];
		Arrays.fill(tail, -1);
		term = new BitSet(65536);

		int nodeIndex = 0;
		base[0] = nodeIndex;
		Node root = trie.getRoot();
		if(root == null) return;
		if(root.getLetters() != null){
			if(root.getLetters().length == 0){
				if(root.isTerminated()) term.set(0);
			} else{
				int c = getCharId(root.getLetters()[0]);
				check[c] = (short)c;
				nodeIndex = c;
				firstEmptyCheck = 1;
			}
		}
		/*
		trie.visit(new TrieVisitor() {
			@Override
			public void accept(Node node, int nest) {
				if(nest >= 6) return;
				Node[] children = node.getChildren();
				if(children == null) return;
				if(children.length > 10){
					for(Node c : children){
						getCharId(c.getLetters()[0]);
					}
				}
			}
		});
		*/
		tailTrie = new TailTrie();
		build(root, nodeIndex);
		tailTrie = null;
		trimToSize();
	}

	@Override
	public Node getRoot() {
		throw new UnsupportedOperationException();
	}
	
	public StringBuilder getTails(){
		return tails;
	}

	public boolean contains(String text){
		char[] chars = text.toCharArray();
		int charsIndex = 0;
		int nodeIndex = 0;
		while(charsIndex < chars.length){
			int tailIndex = tail[nodeIndex];
			if(tailIndex != -1){
				char c = tails.charAt(tailIndex);
				while(c != '\0'){
					if(c == '\1'){
						tailIndex = getNextIndex(tailIndex + 1);
						c = tails.charAt(tailIndex);
					}
					if(chars.length <= charsIndex) return false;
					if(chars[charsIndex] != c) return false;
					charsIndex++;
					tailIndex++;
					c = tails.charAt(tailIndex);
				}
				if(chars.length == charsIndex){
					if(c == '\0') return term.get(nodeIndex);
					else return false;
				}
			}
			int cid = findCharId(chars[charsIndex]);
			if(cid == -1) return false;
			int i = cid + base[nodeIndex];
			if(i < 0 || check.length <= i || check[i] != cid) return false;
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
				char c = tails.charAt(ti);
				if(c == '\0'){
					break;
				}
				if(c == '\1'){
					ti = getNextIndex(++ti);
					c = tails.charAt(ti);
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
			if(check.length <= next || check[next] != cid) return ret;
			ni = next;
			if(tail[ni] != -1){
				int ti = tail[ni];
				char c = tails.charAt(ti);
				while(c != '\0'){
					if(c == '\1'){
						ti = getNextIndex(ti + 1);
						c = tails.charAt(ti);
					}
					ci++;
					if(ci >= chars.length) return ret;
					if(c != chars[ci]) return ret;
					ti++;
					c = tails.charAt(ti);
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
					char c = tails.charAt(ti);
					if(c == '\0') break;
					if(c == '\1'){
						ti = getNextIndex(ti + 1);
						c = tails.charAt(ti);
					}
					if(c != chars[i]) return ret;
					i++;
					ti++;
				} while(i < chars.length);
				if(i >= chars.length) break;
				current.append(tails.substring(tail[nodeIndex], ti));
			}
			int cid = findCharId(chars[i]);
			if(cid == -1) return ret;
			int next = base[nodeIndex] + cid;
			if(next < 0 || check.length <= next || check[next] != cid) return ret;
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
				char c = tails.charAt(ti);
				while(c != '\0'){
					if(c == '\1'){
						ti = getNextIndex(ti + 1);
						c = tails.charAt(ti);
					}
					buff.append(c);
					ti++;
					c = tails.charAt(ti);
				}
			}
			if(term.get(ni)) ret.add(buff.toString());
			for(Map.Entry<Character, Integer> e : charCodes.entrySet()){
				int b = base[ni];
				if(b == BASE_EMPTY) continue;
				int next = b + e.getValue();
				if(check.length <= next) continue;
				if(check[next] == e.getValue()){
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

	public void save(OutputStream os) throws IOException{
		BufferedOutputStream bos = new BufferedOutputStream(os);
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(base.length);
		for(int v : base){
			dos.writeInt(v);
		}
		for(int v : check){
			dos.writeShort(v);
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
		check = new short[len];
		for(int i = 0; i < len; i++){
			check[i] = dis.readShort();
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
		tails = new StringBuilder(n);
		for(int i = 0; i < n; i++){
			tails.append(dis.readChar());
		}
		n = dis.readInt();
		for(int i = 0; i < n; i++){
			char c = dis.readChar();
			int v = dis.readInt();
			charCodes.put(c, v);
		}
	}

	public void dump(){
		System.out.println("--- dump Double Array ---");
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
		char[] tailChars = tails.substring(0, Math.min(tails.length(), 64)).toCharArray();
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
		System.out.println("tailBuf size: " + tails.length());
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
				if(b == BASE_EMPTY) continue;
				min = Math.min(min, b);
			}
			System.out.println("min: " + min);
		}
		System.out.println();
	}

	private void build(Node node, int nodeIndex){
		// letters
		char[] letters = node.getLetters();
		if(letters != null){
			if(letters.length > 1){
				int tailIndex = tailTrie.insert(Arrays.copyOfRange(letters, 1, letters.length));
				tail[nodeIndex] = tailIndex;
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
		int minHead = Integer.MAX_VALUE;
		for(int i = 0; i < children.length; i++){
			heads[i] = getCharId(children[i].getLetters()[0]);
			maxHead = Math.max(maxHead, heads[i]);
			minHead = Math.min(minHead, heads[i]);
		}
		int empty = findFirstEmptyCheck();
		int offset = empty - minHead;
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
			offset = empty - minHead;
		}
		base[nodeIndex] = offset;
		for(int cid : heads){
			if(cid > Short.MAX_VALUE){
				throw new RuntimeException("check value overflow");
			}
			setCheck(offset + cid, (short)cid);
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

	private int getCharId(char c){
		Integer cid = charCodes.get(c);
		if(cid == null){
			cid = charCodes.size() + 1;
			if(cid > Short.MAX_VALUE){
				throw new RuntimeException("too many kinds of character(max: 32767).");
			}
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
		short[] nc = new short[nsz];
		System.arraycopy(check, 0, nc, 0, sz);
		Arrays.fill(nc, sz, nsz, (short)-1);
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
				int v = prev - i;
				if(v < Short.MIN_VALUE){
					throw new RuntimeException("check value overflow");
				}
				check[prev] = (short)v;
				return i;
			}
		}
		extend(i);
		int v = prev - i;
		if(v < Short.MIN_VALUE){
			throw new RuntimeException("check value overflow");
		}
		check[prev] = (short)v;
		return i;
//*/
	}

	private void setCheck(int index, short value){
		if(firstEmptyCheck == index){
			firstEmptyCheck = findNextEmptyCheck(firstEmptyCheck);
		}
		check[index] = value;
		last = Math.max(last, index);
	}

	private int getNextIndex(int tailIndex){
		int i = tails.charAt(tailIndex);
		i += tails.charAt(tailIndex + 1) << 16;
		return i;
	}

	private void trimToSize(){
		int sz = last + 1;
		int[] nb = new int[sz];
		System.arraycopy(base, 0, nb, 0, sz);
		base = nb;
		short[] nc = new short[sz];
		System.arraycopy(check, 0, nc, 0, sz);
		check = nc;
		int[] nt = new int[sz];
		System.arraycopy(tail, 0, nt, 0, sz);
		tail = nt;
		tails.trimToSize();
	}

	private int[] base;
	private short[] check;
	private int[] tail;
	private int firstEmptyCheck;
	private int last;
	private BitSet term;
	private TailTrie tailTrie;
	private StringBuilder tails = new StringBuilder();
	private Map<Character, Integer> charCodes = new TreeMap<Character, Integer>(new Comparator<Character>(){
		@Override
		public int compare(Character arg0, Character arg1) {
			return arg1 - arg0;
		}
	});
}
