/*
 * Copyright 2012, 2015 Takao Nakaguchi
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.trie4j.AbstractTermIdTrie;
import org.trie4j.Node;
import org.trie4j.TermIdNode;
import org.trie4j.TermIdTrie;
import org.trie4j.Trie;
import org.trie4j.bv.BytesRank1OnlySuccinctBitVector;
import org.trie4j.bv.SuccinctBitVector;
import org.trie4j.util.BitSet;
import org.trie4j.util.FastBitSet;
import org.trie4j.util.Pair;

import sun.misc.Unsafe;

@Deprecated
@SuppressWarnings("restriction")
public class UnsafeDoubleArray
extends AbstractTermIdTrie
implements Externalizable, TermIdTrie{
	public static interface TermNodeListener{
		void listen(Node node, int nodeIndex);
	}

	public UnsafeDoubleArray() {
	}

	public UnsafeDoubleArray(Trie trie){
		this(trie, trie.size() * 2);
	}

	public UnsafeDoubleArray(Trie trie, int arraySize){
		this(trie, arraySize, new TermNodeListener(){
			@Override
			public void listen(Node node, int nodeIndex) {
			}
		});
	}

	public UnsafeDoubleArray(Trie trie, int arraySize, TermNodeListener listener){
		if(arraySize <= 1) arraySize = 2;
		size = trie.size();
		nodeSize = trie.nodeSize();
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new int[arraySize];
		Arrays.fill(check, -1 * Unsafe.ARRAY_INT_INDEX_SCALE);
		FastBitSet bs = new FastBitSet(arraySize);
		build(trie.getRoot(), arrayIndexToOffset(0), bs, listener);
		term = new BytesRank1OnlySuccinctBitVector(bs.getBytes(), bs.size());
		base = Arrays.copyOf(base, offsetToIntArrayIndex(last) + chars.size());
		check = Arrays.copyOf(check, offsetToIntArrayIndex(last) + chars.size());
	}

	@Override
	public int nodeSize() {
		return nodeSize;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public TermIdNode getRoot() {
		return newDoubleArrayNode(0);
	}

	public int[] getBase(){
		return base;
	}

	public int[] getCheck(){
		return check;
	}

	public BitSet getTerm() {
		return term;
	}

	protected class DoubleArrayNode implements TermIdNode{
		public DoubleArrayNode(int nodeId){
			this.nodeId = nodeId;
		}

		public DoubleArrayNode(int nodeId, char firstChar){
			this.nodeId = nodeId;
			this.firstChar = firstChar;
		}

		@Override
		public boolean isTerminate() {
			int nid = nodeId;
			while(true){
				CharSequence children = listupChildChars(nid);
				int n = children.length();
				if(n == 0) return term.get(nid);
				int b = offsetToIntArrayIndex(base[nid]);
				char firstChar = children.charAt(0);
				if(n > 1){
					return term.get(nid);
				} else{
					int firstNid = b + charToScaledCode[firstChar] / Unsafe.ARRAY_INT_INDEX_SCALE;
					if(term.get(firstNid)) return true;
					nid = firstNid; 
				}
			}
		}

		@Override
		public char[] getLetters() {
			StringBuilder ret = new StringBuilder();
			if(firstChar != 0) ret.append(firstChar);
			return ret.toString().toCharArray();
		}

		@Override
		public DoubleArrayNode[] getChildren() {
			int nid = nodeId;
			while(true){
				CharSequence children = listupChildChars(nid);
				int n = children.length();
				if(n == 0) return emptyNodes;
				int b = offsetToIntArrayIndex(base[nid]);
				if(n > 1 || term.get(nid)){
					return listupChildNodes(b, children);
				}
				nid = b + charToScaledCode[children.charAt(0)] / Unsafe.ARRAY_INT_INDEX_SCALE;
			}
		}

		@Override
		public DoubleArrayNode getChild(char c) {
			int code = charToScaledCode[c];
			if(code == -1) return null;
			int nid = offsetToIntArrayIndex(base[nodeId] + code);
			if(nid >= 0 && nid < check.length && offsetToIntArrayIndex(check[nid]) == nodeId) return new DoubleArrayNode(nid, c);
			return null;
		}

		public int getNodeId() {
			return nodeId;
		}

		@Override
		public int getTermId(){
			if(!term.get(nodeId)){
				return -1;
			}
			return term.rank1(nodeId) - 1;
		}
	
		private CharSequence listupChildChars(int nodeId){
			StringBuilder b = new StringBuilder();
			long bs = base[nodeId];
			for(char c : chars){
				long nodeOffset = bs + charToScaledCode[c];
				if(nodeOffset >= 0 && nodeOffset < arrayIndexToOffset(check.length) &&
						unsafe.getInt(check, nodeOffset) == nodeId){
					b.append(c);
				}
			}
			return b;
		}

		private DoubleArrayNode[] listupChildNodes(int baseIndex, CharSequence chars){
			int n = chars.length();
			DoubleArrayNode[] ret = new DoubleArrayNode[n];
			for(int i = 0; i < n; i++){
				char c = chars.charAt(i);
				int code = charToScaledCode[c] / Unsafe.ARRAY_INT_INDEX_SCALE;
				ret[i] = newDoubleArrayNode(baseIndex + code, c);
			}
			return ret;
		}

		private char firstChar = 0;
		private int nodeId;
	}

	@Override
//*
	public boolean contains(String text){
		long nodeOffset = Unsafe.ARRAY_INT_BASE_OFFSET; // root
		int n = text.length();
		for(int i = 0; i < n; i++){
			int cid = charToScaledCode[text.charAt(i)];
//			int cid = unsafe.getInt(charToScaledCode, (long)Unsafe.ARRAY_INT_BASE_OFFSET + (text.charAt(i) << 2));
			if(cid == 0) return false;
			long next = unsafe.getInt(base, nodeOffset) + cid;
			if(next < 0 || unsafe.getInt(check, next) != nodeOffset) return false;
			nodeOffset = next;
		}
		return term.get((int)((nodeOffset - Unsafe.ARRAY_INT_BASE_OFFSET) >> 2));
	}
/*/
	public boolean contains(String text){
		int nodeIndex = 0; // root
		int n = text.length();
		for(int i = 0; i < n; i++){
			char cid = unsafe.getChar(charToCode, (long)Unsafe.ARRAY_CHAR_BASE_OFFSET + (text.charAt(i) << 1));
			if(cid == 0) return false;
			int next = unsafe.getInt(base, (long)Unsafe.ARRAY_INT_BASE_OFFSET + (nodeIndex << 2)) + cid;
			if(next < 0 || unsafe.getInt(check, (long)Unsafe.ARRAY_INT_BASE_OFFSET + (next << 2)) != nodeIndex) return false;
			nodeIndex = next;
		}
		return term.get(nodeIndex);
	}
//*/
	private static final sun.misc.Unsafe unsafe;
	static {
		try {
			Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (sun.misc.Unsafe) field.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getNodeId(String text) {
		long nodeOffset = Unsafe.ARRAY_INT_BASE_OFFSET; // root
		int n = text.length();
		for(int i = 0; i < n; i++){
			int code = charToScaledCode[text.charAt(i)];
			if(code == 0) return -1;
			long next = unsafe.getInt(base, nodeOffset) + code;
			if(next < 0 || unsafe.getInt(check, next) != nodeOffset) return -1;
			nodeOffset = next;
		}
		return offsetToIntArrayIndex(nodeOffset);
	}

	@Override
	public int getTermId(String text) {
		int nid = getNodeId(text);
		return term.get(nid) ? term.rank1(nid) - 1 : -1;
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		List<String> ret = new ArrayList<String>();
		char[] chars = query.toCharArray();
		int charsLen = chars.length;
		long checkEnd = arrayIndexToOffset(check.length);
		long nodeOffset = Unsafe.ARRAY_INT_BASE_OFFSET;
		for(int i = 0; i < charsLen; i++){
			int cid = findCharScaledCode(chars[i]);
			if(cid == -1) return ret;
			int b = unsafe.getInt(base, nodeOffset);
			if(b == BASE_EMPTY) return ret;
			long next = b + cid;
			if(next >= checkEnd || unsafe.getInt(check, next) != nodeOffset) return ret;
			nodeOffset = next;
			if(term.get(offsetToIntArrayIndex(nodeOffset))) ret.add(new String(chars, 0, i + 1));
		}
		return ret;
	}

	@Override
	public Iterable<Pair<String, Integer>> commonPrefixSearchWithTermId(
			String query) {
		List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
		char[] chars = query.toCharArray();
		int charsLen = chars.length;
		long checkEnd = arrayIndexToOffset(check.length);
		long nodeOffset = Unsafe.ARRAY_INT_BASE_OFFSET;
		for(int i = 0; i < charsLen; i++){
			int cid = findCharScaledCode(chars[i]);
			if(cid == -1) return ret;
			int b = unsafe.getInt(base, nodeOffset);
			if(b == BASE_EMPTY) return ret;
			long next = b + cid;
			if(next >= checkEnd || unsafe.getInt(check, next) != nodeOffset) return ret;
			nodeOffset = next;
			int nodeIndex = offsetToIntArrayIndex(nodeOffset);
			if(term.get(nodeIndex)){
				ret.add(Pair.create(
					new String(chars, 0, i + 1),
					term.rank1(nodeIndex) - 1
					));
			}
		}
		return ret;
	}

	@Override
	public int findWord(CharSequence chars, int start, int end, StringBuilder word) {
		for(int i = start; i < end; i++){
			long nodeOffset = Unsafe.ARRAY_INT_BASE_OFFSET;
			try{
				for(int j = i; j < end; j++){
					int cid = findCharScaledCode(chars.charAt(j));
					if(cid == -1) break;
					int b = unsafe.getInt(base, nodeOffset);
					if(b == BASE_EMPTY) break;
					long next = b + cid;
					if(nodeOffset != unsafe.getInt(check, next)) break;
					nodeOffset = next;
					if(term.get(offsetToIntArrayIndex(nodeOffset))){
						if(word != null) word.append(chars, i, j + 1);
						return i;
					}
				}
			} catch(ArrayIndexOutOfBoundsException e){
				break;
			}
		}
		return -1;
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		List<String> ret = new ArrayList<String>();
		char[] chars = prefix.toCharArray();
		int charsLen = chars.length;
		long checkEnd = arrayIndexToOffset(check.length);
		long nodeOffset = Unsafe.ARRAY_INT_BASE_OFFSET;
		for(int i = 0; i < charsLen; i++){
			int cid = findCharScaledCode(chars[i]);
			if(cid == -1) return ret;
			long next = unsafe.getInt(base, nodeOffset) + cid;
			if(next < 0 || next >= checkEnd || unsafe.getInt(check, next) != nodeOffset) return ret;
			nodeOffset = next;
		}
		int nodeIndex = offsetToIntArrayIndex(nodeOffset);
		if(term.get(nodeIndex)){
			ret.add(prefix);
		}
		Deque<Pair<Long, String>> q = new LinkedList<Pair<Long, String>>();
		q.add(Pair.create(nodeOffset, prefix));
		while(!q.isEmpty()){
			Pair<Long, String> p = q.pop();
			long no = p.getFirst();
			long b = unsafe.getInt(base, no);
			if(b == BASE_EMPTY) continue;
			String c = p.getSecond();
			for(char v : this.chars){
				long next = b + charToScaledCode[v];
				if(next < 0 || next >= checkEnd) continue;
				if(unsafe.getInt(check, next) == no){
					String n = new StringBuilder(c).append(v).toString();
					if(term.get(offsetToIntArrayIndex(next))){
						ret.add(n);
					}
					q.push(Pair.create(next, n));
				}
			}
		}
		return ret;
	}

	@Override
	public Iterable<Pair<String, Integer>> predictiveSearchWithTermId(
			String prefix) {
		List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
		char[] chars = prefix.toCharArray();
		int charsLen = chars.length;
		if(charsLen == 0) return ret;
		if(this.nodeSize == 0) return ret;
		long checkEnd = arrayIndexToOffset(check.length);
		long nodeOffset = Unsafe.ARRAY_INT_BASE_OFFSET;
		for(int i = 0; i < charsLen; i++){
			int cid = findCharScaledCode(chars[i]);
			if(cid == -1) return ret;
			long next = unsafe.getInt(base, nodeOffset) + cid;
			if(next < 0 || next >= checkEnd || unsafe.getInt(check, next) != nodeOffset) return ret;
			nodeOffset = next;
		}
		int nodeIndex = offsetToIntArrayIndex(nodeOffset);
		if(term.get(nodeIndex)){
			ret.add(Pair.create(prefix, term.rank1(nodeIndex) - 1));
		}
		Deque<Pair<Long, String>> q = new LinkedList<Pair<Long, String>>();
		q.add(Pair.create(nodeOffset, prefix));
		while(!q.isEmpty()){
			Pair<Long, String> p = q.pop();
			long no = p.getFirst();
			int b = unsafe.getInt(base, no);
			if(b == BASE_EMPTY) continue;
			String c = p.getSecond();
			for(char v : this.chars){
				long next = b + charToScaledCode[v];
				if(next < 0 || next >= checkEnd) continue;
				if(unsafe.getInt(check, next) == no){
					String n = new StringBuilder(c).append(v).toString();
					int nextIndex = offsetToIntArrayIndex(next);
					if(term.get(nextIndex)){
						ret.add(Pair.create(
								n,
								term.rank1(nextIndex) - 1
								));
					}
					q.push(Pair.create(next, n));
				}
			}
		}
		return ret;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(size);
		out.writeInt(nodeSize);
		out.writeInt(base.length);
		for(int v : base){
			out.writeInt(v);
		}
		for(int v : check){
			out.writeInt(v);
		}
		out.writeObject(term);
		out.writeInt((int)firstEmptyCheckOffset);
		out.writeInt(chars.size());
		for(char c : chars){
			out.writeChar(c);
			out.writeInt(charToScaledCode[c]);
		}
	}

	public void save(OutputStream os) throws IOException{
		ObjectOutputStream out = new ObjectOutputStream(os);
		try{
			writeExternal(out);
		} finally{
			out.flush();
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		size = in.readInt();
		nodeSize = in.readInt();
		int len = in.readInt();
		base = new int[len];
		for(int i = 0; i < len; i++){
			base[i] = in.readInt();
		}
		check = new int[len];
		for(int i = 0; i < len; i++){
			check[i] = in.readInt();
		}
		try{
			term = (SuccinctBitVector)in.readObject();
		} catch(ClassNotFoundException e){
			throw new IOException(e);
		}
		firstEmptyCheckOffset = in.readInt();
		int n = in.readInt();
		for(int i = 0; i < n; i++){
			char c = in.readChar();
			int v = in.readInt();
			chars.add(c);
			charToScaledCode[c] = v;
		}
	}

	public void load(InputStream is) throws IOException{
		try{
			readExternal(new ObjectInputStream(is));
		} catch(ClassNotFoundException e){
			throw new IOException(e);
		}
	}

	@Override
	public void trimToSize(){
		int sz = (int)last + 1 + 0xFFFF;
		base = Arrays.copyOf(base, sz);
		check = Arrays.copyOf(check, sz);
	}

	@Override
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
			for(char e : chars){
				writer.print(String.format("%c:%d,", e, (int)charToScaledCode[e]));
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

	private void build(Node node, long nodeOffset,
			FastBitSet bs, TermNodeListener listener){
		// letters
		char[] letters = node.getLetters();
		int lettersLen = letters.length;
		for(int i = 1; i < lettersLen; i++){
			bs.unsetIfLE(offsetToIntArrayIndex(nodeOffset));
			int cid = getCharScaledCode(letters[i]);
			long emptyOffset = findFirstEmptyCheckOffset();
			setCheck(emptyOffset, nodeOffset);
			unsafe.putInt(base, nodeOffset, (int)(emptyOffset - cid));
			nodeOffset = emptyOffset;
		}
		int nodeIndex = offsetToIntArrayIndex(nodeOffset);
		if(node.isTerminate()){
			bs.set(nodeIndex);
			listener.listen(node, nodeIndex);
		} else{
			bs.unsetIfLE(nodeIndex);
		}

		// children
		Node[] children = node.getChildren();
		int childrenLen = children.length;
		if(childrenLen == 0) return;
		int[] heads = new int[childrenLen];
		int maxHead = 0;
		int minHead = Integer.MAX_VALUE;
		for(int i = 0; i < childrenLen; i++){
			heads[i] = getCharScaledCode(children[i].getLetters()[0]);
			maxHead = Math.max(maxHead, heads[i]);
			minHead = Math.min(minHead, heads[i]);
		}

		long offset = findInsertOffset(heads, minHead, maxHead);
		unsafe.putInt(base, nodeOffset, (int)offset);
		for(int cid : heads){
			setCheck(offset + cid, nodeOffset);
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
				build(e2.getFirst(), e2.getSecond() + offset, bs, listener);
			}
		}
//*/
	}

	private DoubleArrayNode newDoubleArrayNode(int id){
		return new DoubleArrayNode(id);
	}

	private DoubleArrayNode newDoubleArrayNode(int id, char s){
		return new DoubleArrayNode(id, s);
	}

	private int findCharScaledCode(char c){
		int v = charToScaledCode[c];
		if(v != 0) return v;
		return -1;
	}

	private int offsetToIntArrayIndex(long offset){
		return (int)((offset - Unsafe.ARRAY_INT_BASE_OFFSET) / Unsafe.ARRAY_INT_INDEX_SCALE);
	}
	private long arrayIndexToOffset(int index){
		return Unsafe.ARRAY_INT_BASE_OFFSET + index * Unsafe.ARRAY_INT_INDEX_SCALE;
	}
	private long findInsertOffset(int[] headOffsets, int minHeadOffset, int maxHeadOffset){
		for(long empty = findFirstEmptyCheckOffset(); ; empty = findNextEmptyCheckOffset(empty)){
			long offset = empty - minHeadOffset;
			if((offset + maxHeadOffset) >= arrayIndexToOffset(check.length)){
				extend(offsetToIntArrayIndex(offset + maxHeadOffset));
			}
			// find space
			boolean found = true;
			for(int ho : headOffsets){
				if(unsafe.getInt(check, offset + ho) >= 0){
					found = false;
					break;
				}
			}
			if(found) return offset;
		}
	}

	private int getCharScaledCode(char c){
		int v = charToScaledCode[c];
		if(v != 0) return v;
		chars.add(c);
		v = (chars.size() + 1) * Unsafe.ARRAY_INT_INDEX_SCALE;
		charToScaledCode[c] = v;
		return v;
	}

	private void extend(int i){
		int sz = base.length;
		int nsz = Math.max(i + 0xFFFF, (int)(sz * 1.5));
//		System.out.println("extend to " + nsz);
		base = Arrays.copyOf(base, nsz);
		Arrays.fill(base, sz, nsz, BASE_EMPTY);
		check = Arrays.copyOf(check, nsz);
		Arrays.fill(check, sz, nsz, -1 * Unsafe.ARRAY_INT_INDEX_SCALE);
	}

	private long findFirstEmptyCheckOffset(){
		long i = firstEmptyCheckOffset;
		while(unsafe.getInt(check, i) >= 0 || unsafe.getInt(base, i) != BASE_EMPTY){
			i += Unsafe.ARRAY_INT_INDEX_SCALE;
		}
		firstEmptyCheckOffset = i;
		return i;
	}

	private long findNextEmptyCheckOffset(long offset){
/*
		for(i++; i < check.length; i++){
			if(check[i] < 0) return i;
		}
		extend(i);
		return i;
/*/
		int d = unsafe.getInt(check, offset) * -1;
		if(d <= 0){
			throw new RuntimeException();
		}
		long prev = offset;
		offset += d;
		long endOffset = arrayIndexToOffset(check.length);
		if(endOffset <= offset){
			extend(offsetToIntArrayIndex(offset));
			return offset;
		}
		if(unsafe.getInt(check, offset) < 0){
			return offset;
		}
		for(offset += Unsafe.ARRAY_INT_INDEX_SCALE; offset < endOffset; offset += Unsafe.ARRAY_INT_INDEX_SCALE){
			if(unsafe.getInt(check, offset) < 0){
				unsafe.putInt(check, prev, (int)(prev - offset));
				return offset;
			}
		}
		extend(offsetToIntArrayIndex(offset));
		unsafe.putInt(check, prev, (int)(prev - offset));
		return offset;
//*/
	}

	private void setCheck(long offset, long nodeOffset){
		if(firstEmptyCheckOffset == offset){
			firstEmptyCheckOffset = findNextEmptyCheckOffset(firstEmptyCheckOffset);
		}
		unsafe.putInt(check, offset, (int)nodeOffset);
		last = Math.max(last, offset);
	}

	private int size;
	private int nodeSize;
	private int[] base;
	private int[] check;
	private long firstEmptyCheckOffset = Unsafe.ARRAY_INT_BASE_OFFSET + Unsafe.ARRAY_INT_INDEX_SCALE;
	private long last = Unsafe.ARRAY_INT_BASE_OFFSET;
	private SuccinctBitVector term;
	private Set<Character> chars = new TreeSet<Character>();
	private int[] charToScaledCode = new int[Character.MAX_VALUE];
	private static final int BASE_EMPTY = Integer.MAX_VALUE;
	private static final DoubleArrayNode[] emptyNodes = {};
}
