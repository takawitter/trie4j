package org.trie4j.louds.bvtree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.trie4j.util.Range;
import org.trie4j.util.SuccinctBitVector;

public class LOUDSBvTree implements BvTree{
	public LOUDSBvTree(int initialCapacity) {
		vector = new SuccinctBitVector(initialCapacity);
	}

	public LOUDSBvTree(SuccinctBitVector vector) {
		this.vector = vector;
	}

	@Override
	public String toString() {
		String bvs = vector.toString();
		return "bitvec: " + ((bvs.length() > 100) ? bvs.substring(0, 100) : bvs);
	}

	@Override
	public void appendChild() {
		vector.append1();
	}
	
	@Override
	public void appendSelf() {
		vector.append0();
	}

	@Override
	public void getChildNodeIds(int selfNodeId, Range range) {
		int s = vector.select0(selfNodeId) + 1;
		int e = vector.next0(s);
		int startNodeId = vector.rank1(s);
		range.set(startNodeId, startNodeId + e - s);
	}
	
	@Override
	public void trimToSize() {
		vector.trimToSize();
	}

	@Override
	public void load(InputStream is) throws IOException {
		vector.load(is);
	}
	
	@Override
	public void save(OutputStream os) throws IOException {
		vector.save(os);
	}

	private SuccinctBitVector vector;
}
