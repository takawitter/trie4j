/*
 * Copyright 2014 Takao Nakaguchi
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
import java.util.Arrays;

import org.trie4j.bv.Rank1OnlySuccinctBitVector;
import org.trie4j.bv.SuccinctBitVector;
import org.trie4j.tail.TailIndex;

public class DenseArrayTailIndex
implements Externalizable, TailIndex{
	public DenseArrayTailIndex(int[] tail, byte[] bits, int bitsSize) {
		this.sbv = new Rank1OnlySuccinctBitVector(bits, bitsSize);
		this.tail = tail;
		current = bitsSize;
		currentIndex = tail.length;
	}

	public DenseArrayTailIndex(int initialCapacity) {
		tail = new int[initialCapacity];
	}

	public DenseArrayTailIndex() {
	}

	@Override
	public void add(int nodeId, int start, int end) {
		if(nodeId != current){
			throw new IllegalArgumentException("nodeId must be a monoinc.");
		}
		ensureCapacity();
		tail[currentIndex++] = start;
		current++;
		sbv.append1();
	}

	@Override
	public void addEmpty(int nodeId) {
		if(nodeId != current){
			throw new IllegalArgumentException("nodeId must be a monoinc.");
		}
		current++;
		sbv.append0();
	}

	@Override
	public int get(int nodeId) {
		if(sbv.isZero(nodeId)) return -1;
		return tail[sbv.rank1(nodeId) - 1];
	}

	@Override
	public void trimToSize() {
		tail = Arrays.copyOf(tail, currentIndex);
	}

	private void ensureCapacity(){
		if(currentIndex == tail.length){
			tail = Arrays.copyOf(tail, (int)((current + 1) * 1.2));
		}
	}

	@Override
	public void readExternal(ObjectInput in)
	throws ClassNotFoundException, IOException{
		current = in.readInt();
		currentIndex = in.readInt();
		tail = (int[])in.readObject();
		sbv = (SuccinctBitVector)in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		trimToSize();
		out.writeInt(current);
		out.writeInt(currentIndex);
		out.writeObject(tail);
		out.writeObject(sbv);
	}

	private SuccinctBitVector sbv = new Rank1OnlySuccinctBitVector();
	private int[] tail = {};
	private int current;
	private int currentIndex;
}
