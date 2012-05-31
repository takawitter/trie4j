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
package org.trie4j.util;

public class BitVector {
	public BitVector(int initialCapacity){
		 vector = new byte[initialCapacity / 8 + 1];
		 cache = new int[initialCapacity / WIDTH];
	}

	public int size(){
		return this.size;
	}
	
	public void trimToSize(){
		int sz = size / 8 + 1;
		if(vector.length > sz){
			byte[] nv = new byte[sz];
			System.arraycopy(vector, 0, nv, 0, sz);
			vector = nv;
		}
	}

	public void append(boolean bit){
		int i = size / 8;
		if(i >= vector.length){
			byte[] n = new byte[(int)(vector.length * 1.2) + 1];
			System.arraycopy(vector, 0, n, 0, vector.length);
			n[vector.length] = 0;
			vector = n;
		}
		int r = size % 8;
		if(bit){
			vector[i] |= BITS[r];
		} else{
			vector[i] &= ~BITS[r];
		}
		size++;
	}

	public int rank1(int pos){
		int ret = 0;
		int n = pos / 8;
		for(int i = 0; i < n; i++){
			ret += BITCOUNTS[vector[i] & 0xff];
		}
		ret += BITCOUNTS[vector[n] & MASKS[pos % 8]];
		return ret;
	}

	public int rank0(int pos){
		int ret = 0;
		int n = pos / 8;
		for(int i = 0; i < n; i++){
			ret += 8 - BITCOUNTS[vector[i] & 0xff];
		}
		ret += ((pos % 8) + 1) - BITCOUNTS[vector[n] & MASKS[pos % 8]];
		return ret;
	}

	public int rank(int pos, boolean b){
		if(b) return rank1(pos);
		else return rank0(pos);
	}
	
	public int select0(int count){
		for(int i = 0; i < vector.length; i++){
			if(i * 8 >= size) return -1;
			int c = 8 - BITCOUNTS[vector[i] & 0xff];
			if(count <= c){
				int v = vector[i] & 0xff;
				for(int j = 0; j < 8; j++){
					if(i * 8 + j >= size) return -1;
					if((v & 0x80) == 0){
						count--;
						if(count == 0){
							return i * 8 + j;
						}
					}
					v <<= 1;
				}
			}
			count -= c;
		}
		return -1;
	}

	public int select1(int count){
		for(int i = 0; i < vector.length; i++){
			if(i * 8 >= size) return -1;
			int c = BITCOUNTS[vector[i] & 0xff];
			if(count <= c){
				int v = vector[i] & 0xff;
				for(int j = 0; j < 8; j++){
					if(i * 8 + j >= size) return -1;
					if((v & 0x80) != 0){
						count--;
						if(count == 0){
							return i * 8 + j;
						}
					}
					v <<= 1;
				}
			}
			count -= c;
		}
		return -1;
	}

	public int select(int count, boolean b){
		if(b) return select1(count);
		else return select0(count);
	}

	private static final int WIDTH = 32;
	private byte[] vector;
	private int size;
	private int[] cache;

	private static final int[] MASKS = {
		0x80, 0xc0, 0xe0, 0xf0
		, 0xf8, 0xfc, 0xfe, 0xff
	};
	private static final byte[] BITS = {
		(byte)0x80, (byte)0x40, (byte)0x20, (byte)0x10
		, (byte)0x08, (byte)0x04, (byte)0x02, (byte)0x01
	};
	private static final byte[] BITCOUNTS = {
		0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
		1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
		2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
		3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
		4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8
		};
}
