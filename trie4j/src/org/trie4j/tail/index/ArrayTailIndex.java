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
import java.util.Arrays;

import org.trie4j.tail.TailIndex;

public class ArrayTailIndex
implements Externalizable, TailIndex{
	public ArrayTailIndex() {
	}

	public ArrayTailIndex(int initialCapacity) {
		tail = new int[initialCapacity];
	}

	@Override
	public void add(int start, int end) {
		ensureCapacity();
		tail[current++] = start;
	}
	
	@Override
	public void addEmpty() {
		ensureCapacity();
		tail[current++] = -1;
	}

	@Override
	public int get(int nodeId) {
		return tail[nodeId];
	}

	@Override
	public void trimToSize() {
		tail = Arrays.copyOf(tail, current);
	}

	private void ensureCapacity(){
		if(current == tail.length){
			tail = Arrays.copyOf(tail, (int)((current + 1) * 1.2));
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException{
		int n = in.readInt();
		current = n;
		tail = new int[n];
		for(int i = 0; i < n; i++){
			tail[i] = in.readInt();
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		int n = current;
		out.writeInt(n);
		for(int i = 0; i < n; i++){
			out.writeInt(tail[i]);
		}
	}

	private int[] tail = new int[]{};
	private int current;
}
