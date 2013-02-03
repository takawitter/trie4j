package org.trie4j.louds.bvtree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.trie4j.bv.BitVector01Devider;
import org.trie4j.bv.Rank0OnlySuccinctBitVector;
import org.trie4j.util.Range;
import org.trie4j.util.SuccinctBitVector;

public class LOUDSPPBvTree implements BvTree{
	public LOUDSPPBvTree(int initialCapacity) {
		r0 = new Rank0OnlySuccinctBitVector(initialCapacity);
		r1 = new SuccinctBitVector(initialCapacity);
		divider = new BitVector01Devider(r0, r1);
	}

	@Override
	public String toString() {
		return "r0: " + r0.toString() + "  r1: " + r1.toString();
	}

	@Override
	public void appendChild() {
		divider.append1();
	}
	
	@Override
	public void appendSelf() {
		divider.append0();
	}

	@Override
	public void getChildNodeIds(int selfNodeId, Range range) {
		if(r0.isZero(selfNodeId)){
			int start = r1.select0(r0.rank0(selfNodeId)) + 1;
			range.set(start, r1.next0(start) + 1);
			return;
		}
		range.set(-1, -1);
	}
	
	@Override
	public void trimToSize() {
		r0.trimToSize();
		r1.trimToSize();
	}

	@Override
	public void load(InputStream is) throws IOException {
		divider.load(is);
		r0.load(is);
		r1.load(is);
	}
	
	@Override
	public void save(OutputStream os) throws IOException {
		divider.save(os);
		r0.save(os);
		r1.save(os);
	}

	private BitVector01Devider divider;
	private Rank0OnlySuccinctBitVector r0;
	private SuccinctBitVector r1;
}
