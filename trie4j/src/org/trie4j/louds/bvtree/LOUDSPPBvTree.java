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
package org.trie4j.louds.bvtree;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.trie4j.bv.BitVector01Devider;
import org.trie4j.bv.BytesSuccinctBitVector;
import org.trie4j.bv.Rank0OnlySuccinctBitVector;
import org.trie4j.util.Range;

public class LOUDSPPBvTree
implements Externalizable, BvTree{
	public LOUDSPPBvTree() {
		this(0);
	}

	public LOUDSPPBvTree(int initialCapacity) {
		r0 = new Rank0OnlySuccinctBitVector(initialCapacity);
		r1 = new BytesSuccinctBitVector(initialCapacity);
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
	public void readExternal(ObjectInput in) throws IOException {
		divider.readExternal(in);
		r0.readExternal(in);
		r1.readExternal(in);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		divider.writeExternal(out);
		r0.writeExternal(out);
		r1.writeExternal(out);
	}

	private BitVector01Devider divider;
	private Rank0OnlySuccinctBitVector r0;
	private BytesSuccinctBitVector r1;
}
