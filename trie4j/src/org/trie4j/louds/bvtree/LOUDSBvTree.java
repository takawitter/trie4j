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

import org.trie4j.bv.BytesSuccinctBitVector;
import org.trie4j.util.Range;

public class LOUDSBvTree implements Externalizable, BvTree{
	public LOUDSBvTree() {
		this(0);
	}

	public LOUDSBvTree(int initialCapacity) {
		vector = new BytesSuccinctBitVector(initialCapacity);
	}

	public LOUDSBvTree(BytesSuccinctBitVector vector) {
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
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		vector.readExternal(in);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		vector.writeExternal(out);
	}

	private BytesSuccinctBitVector vector;
}
