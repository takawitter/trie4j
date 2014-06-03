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
package org.trie4j.tail.index;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.trie4j.bv.BytesSuccinctBitVector;

public class SBVTailIndex
implements Externalizable, TailIndex{
	public SBVTailIndex() {
		bv = new BytesSuccinctBitVector();
	}

	public SBVTailIndex(byte[] bits, int bitSize, int size) {
		this.size = size;
		this.bv = new BytesSuccinctBitVector(bits, bitSize);
	}

	public BytesSuccinctBitVector getSBV(){
		return bv;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int get(int nodeId) {
		if(nodeId == 0){
			if(bv.isZero(0)) return -1;
			else return 0;
		}
		int s = bv.select0(nodeId);
		if(bv.isZero(s + 1)) return -1;
		return bv.rank1(s);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException{
		size = in.readInt();
		bv.readExternal(in);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException{
		out.writeInt(size);
		bv.writeExternal(out);
	}

	private int size;
	private BytesSuccinctBitVector bv;
	private static final long serialVersionUID = 8843853578097509573L;
}
