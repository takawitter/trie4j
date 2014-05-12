package org.trie4j.doublearray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.trie4j.DenseKeyIdNode;
import org.trie4j.DenseKeyIdTrie;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.bv.Rank1OnlySuccinctBitVector;
import org.trie4j.bv.SuccinctBitVector;
import org.trie4j.util.FastBitSet;
import org.trie4j.util.Pair;

public class DenseKeyIdDoubleArray
extends DoubleArray
implements DenseKeyIdTrie{
	public DenseKeyIdDoubleArray() {
	}

	public DenseKeyIdDoubleArray(Trie trie){
		this(trie, trie.size() * 2);
	}

	public DenseKeyIdDoubleArray(Trie trie, int arraySize){
		this(trie, arraySize, new TermNodeListener() {
			@Override
			public void listen(Node node, int nodeIndex) {
			}
		});
	}

	public DenseKeyIdDoubleArray(Trie trie, int arraySize, TermNodeListener listener){
		if(arraySize <= 1) arraySize = 2;
		size = trie.size();
		base = new int[arraySize];
		Arrays.fill(base, BASE_EMPTY);
		check = new int[arraySize];
		Arrays.fill(check, -1);
		FastBitSet bs = new FastBitSet();
		build(trie.getRoot(), 0, bs, listener);
		termbv = new Rank1OnlySuccinctBitVector(bs.getBytes(), bs.size());
		term = termbv;
	}

	protected class DenseKeyIdDoubleArrayNode extends DoubleArrayNode implements DenseKeyIdNode{
		public DenseKeyIdDoubleArrayNode(int id){
			super(id);
		}

		public DenseKeyIdDoubleArrayNode(int id, char s){
			super(id, s);
		}

		@Override
		public DenseKeyIdNode getChild(char c) {
			return (DenseKeyIdNode)super.getChild(c);
		}

		@Override
		public DenseKeyIdNode[] getChildren() {
			return (DenseKeyIdNode[])super.getChildren();
		}

		@Override
		public int getDenseKeyId() {
			int id = getNodeId();
			if(id == -1) return -1;
			return termbv.rank1(id) - 1;
		}

		@Override
		protected DenseKeyIdDoubleArrayNode[] newNodeArray(int size) {
			return new DenseKeyIdDoubleArrayNode[size];
		}
	}

	@Override
	public DenseKeyIdNode getRoot() {
		return (DenseKeyIdNode)super.getRoot();
	}

	@Override
	public int getDenseKeyIdFor(String text) {
		int id = getInternalIdFor(text);
		if(id == -1) return id;
		return termbv.rank1(id) - 1;
	}

	@Override
	public int geteMaxDenseKeyId() {
		return termbv.size() - 1;
	}

	@Override
	public Iterable<Pair<String, Integer>> commonPrefixSearchWithDenseKeyId(
			String query) {
		List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
		char[] chars = query.toCharArray();
		int charsLen = chars.length;
		int checkLen = check.length;
		int nodeIndex = 0;
		for(int i = 0; i < charsLen; i++){
			int cid = findCharId(chars[i]);
			if(cid == -1) return ret;
			int b = base[nodeIndex];
			if(b == BASE_EMPTY) return ret;
			int next = b + cid;
			if(next >= checkLen || check[next] != nodeIndex) return ret;
			nodeIndex = next;
			if(term.get(nodeIndex)){
				ret.add(Pair.create(
					new String(chars, 0, i + 1),
					termbv.rank1(nodeIndex) - 1
					));
			}
		}
		return ret;
	}

	@Override
	public Iterable<Pair<String, Integer>> predictiveSearchWithDenseKeyId(
			String prefix) {
		List<Pair<String, Integer>> ret = new ArrayList<Pair<String, Integer>>();
		char[] chars = prefix.toCharArray();
		int charsLen = chars.length;
		int checkLen = check.length;
		int nodeIndex = 0;
		for(int i = 0; i < charsLen; i++){
			int cid = findCharId(chars[i]);
			if(cid == -1) return ret;
			int next = base[nodeIndex] + cid;
			if(next < 0 || next >= checkLen || check[next] != nodeIndex) return ret;
			nodeIndex = next;
		}
		if(term.get(nodeIndex)){
			ret.add(Pair.create(prefix, nodeIndex));
		}
		Deque<Pair<Integer, String>> q = new LinkedList<Pair<Integer, String>>();
		q.add(Pair.create(nodeIndex, prefix));
		while(!q.isEmpty()){
			Pair<Integer, String> p = q.pop();
			int ni = p.getFirst();
			int b = base[ni];
			if(b == BASE_EMPTY) continue;
			String c = p.getSecond();
			for(char v : this.chars){
				int next = b + charToCode[v];
				if(next < 0 || next >= checkLen) continue;
				if(check[next] == ni){
					String n = new StringBuilder(c).append(v).toString();
					if(term.get(next)){
						ret.add(Pair.create(
								n,
								termbv.rank1(next) - 1
								));
					}
					q.push(Pair.create(next, n));
				}
			}
		}
		return ret;
	}

	@Override
	protected DoubleArrayNode newDoubleArrayNode(int id){
		return new DenseKeyIdDoubleArrayNode(id);
	}

	@Override
	protected DoubleArrayNode newDoubleArrayNode(int id, char s){
		return new DenseKeyIdDoubleArrayNode(id, s);
	}

	private SuccinctBitVector termbv;
}
