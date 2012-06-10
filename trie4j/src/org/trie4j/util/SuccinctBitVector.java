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


public class SuccinctBitVector {
	public SuccinctBitVector(){
		this(16);
	}

	public SuccinctBitVector(int initialCapacity){
		vector = new byte[initialCapacity / 8 + 1];
		int blockSize = CACHE_WIDTH;
		int size = initialCapacity / blockSize + (((initialCapacity % blockSize) != 0) ? 1 : 0);
		countCache0 = new int[size];
		indexCache0 = new int[size + 1];
	}

	public int size(){
		return this.size;
	}

	public void trimToSize(){
		int vectorSize = size / 8 + 1;
		byte[] nv = new byte[vectorSize];
		System.arraycopy(vector, 0, nv, 0, Math.min(vector.length, vectorSize));
		vector = nv;
		int blockSize = CACHE_WIDTH / 8;
		int size = vectorSize / blockSize + (((vectorSize % blockSize) != 0) ? 1 : 0);
		int countCacheSize0 = size;
		int[] ncc0 = new int[countCacheSize0];
		System.arraycopy(countCache0, 0, ncc0, 0, Math.min(countCache0.length, countCacheSize0));
		countCache0 = ncc0;
		int indexCacheSize = size + 1;
		int[] nic = new int[indexCacheSize];
		System.arraycopy(indexCache0, 0, nic, 0, Math.min(indexCache0.length, indexCacheSize));
		indexCache0 = nic;
	}

	public void append1(){
		int i = size / 8;
		int ci = size / CACHE_WIDTH;
		if(i >= vector.length){
			extend();
		}
		if(size % CACHE_WIDTH == 0 && ci > 0){
			countCache0[ci] = countCache0[ci - 1];
		}
		int r = size % 8;
		vector[i] |= BITS[r];
		size++;
	}

	public void append0(){
		int i = size / 8;
		int ci = size / CACHE_WIDTH;
		if(i >= vector.length){
			extend();
		}
		if(size % CACHE_WIDTH == 0 && ci > 0){
			countCache0[ci] = countCache0[ci - 1];
		}
		int r = size % 8;
		vector[i] &= ~BITS[r];
		size0++;
		if(size0 == 1){
			node1pos = size;
		} else if(size0 == 2){
			node2pos = size;
		}
		if(size0 % CACHE_WIDTH == 0){
			indexCache0[size0 / CACHE_WIDTH] = size;
		}
		countCache0[ci]++;
		size++;
	}

	public void append(boolean bit){
		if(bit) append1();
		else append0();
	}

	public int rank1(int pos){
		int ret = 0;
		int cn = pos / CACHE_WIDTH;
		if(cn > 0){
			ret = cn * CACHE_WIDTH - countCache0[cn - 1];
		}
		int n = pos / 8;
		for(int i = (cn * (CACHE_WIDTH / 8)); i < n; i++){
			ret += BITCOUNTS[vector[i] & 0xff];
		}
		ret += BITCOUNTS[vector[n] & MASKS[pos % 8]];
		return ret;
	}

	public int rank0(int pos){
		int ret = 0;
		int cn = pos / CACHE_WIDTH;
		if(cn > 0){
			ret = countCache0[cn - 1];
		}
		int n = pos / 8;
		for(int i = (cn * (CACHE_WIDTH / 8)); i < n; i++){
			ret += BITCOUNTS0[vector[i] & 0xff];
		}
		ret += BITCOUNTS0[vector[n] & MASKS[pos % 8]] - 7 + (pos % 8);
		return ret;
	}

	public int rank(int pos, boolean b){
		if(b) return rank1(pos);
		else return rank0(pos);
	}

	public int select0(int count){
		if(count <= 2){
			if(count == 1) return node1pos;
			else return node2pos;
		}
//*
		int start = indexCache0[count / CACHE_WIDTH] / CACHE_WIDTH;
		int end = countCache0.length;
		int ici = count / CACHE_WIDTH + 1;
		if(indexCache0.length > ici){
			end = (indexCache0[ici]) / CACHE_WIDTH + 1;
		}
/*/
		int start = Math.max(offset / CACHE_WIDTH - 1, 0);
		int end = countCache.length;
//*/
		int m = 0;
		int d = 0;
		while(start != end){
			m = (start + end) / 2;
			d = count - countCache0[m];
//			if(m == (countCache0.length - 1)){
//				d += CACHE_WIDTH - (size % CACHE_WIDTH);
//			}
			if(d < 0){
				end = m;
				continue;
			} else if(d > 0){
				if(start != m) start = m;
				else break;
			} else{
				break;
			}
		}
		if(d > 0){
			count = d;
			for(int i = (m + 1) * CACHE_WIDTH / 8; i < vector.length; i++){
				if(i * 8 >= size) return -1;
				int c = BITCOUNTS0[vector[i] & 0xff];
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
		} else{
			count = d - 1;
			int i = Math.min(((m + 1) * CACHE_WIDTH) - 1, size - 1);
			int v = vector[i / 8] & 0xff;
			v >>= 8 - (i % 8) - 1;
			while(i >= 0){
				if((v & 0x01) == 0){
					count++;
					if(count == 0){
						return i;
					}
				}
				if(i % 8 == 0){
					v = vector[(i - 1) / 8] & 0xff;
				} else{
					v >>= 1;
				}
				i--;
			}
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

	public int next0(int pos){
		if(pos <= node2pos){
			if(pos <= node1pos) return node1pos;
			else return node2pos;
		}
		int i = pos / 8;
		{
			int v = vector[i] & 0xff;
			v <<= pos % 8;
			for(int j = pos % 8; j < 8; j++){
				if(i * 8 + j >= size) return -1;
				if((v & 0x80) == 0){
					return i * 8 + j;
				}
				v <<= 1;
			}
		}
		for(i++; i < vector.length; i++){
			if(i * 8 >= size) return -1;
			int c = BITCOUNTS0[vector[i] & 0xff];
			if(1 <= c){
				int v = vector[i] & 0xff;
				for(int j = 0; j < 8; j++){
					if(i * 8 + j >= size) return -1;
					if((v & 0x80) == 0){
						return i * 8 + j;
					}
					v <<= 1;
				}
			}
		}
		return -1;
	}

	private void extend(){
		int vectorSize = (int)(vector.length * 1.2) + 1;
		byte[] nv = new byte[vectorSize];
		System.arraycopy(vector, 0, nv, 0, vector.length);
		vector = nv;
		int blockSize = CACHE_WIDTH / 8;
		int size = vectorSize / blockSize + (((vectorSize % blockSize) != 0) ? 1 : 0);
		int[] nc0 = new int[size];
		System.arraycopy(countCache0, 0, nc0, 0, countCache0.length);
		countCache0 = nc0;
		int[] nic = new int[size];
		System.arraycopy(indexCache0, 0, nic, 0, indexCache0.length - 1);
		indexCache0 = nic;
	}

	private static final int CACHE_WIDTH = 64;
	private byte[] vector;
	private int node1pos = -1;
	private int node2pos = -1;
	private int size;
	private int size0;
	private int[] countCache0;
	private int[] indexCache0;

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
	private static final byte[] BITCOUNTS0 = {
		8, 7, 7, 6, 7, 6, 6, 5, 7, 6, 6, 5, 6, 5, 5, 4, 
		7, 6, 6, 5, 6, 5, 5, 4, 6, 5, 5, 4, 5, 4, 4, 3, 
		7, 6, 6, 5, 6, 5, 5, 4, 6, 5, 5, 4, 5, 4, 4, 3, 
		6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 
		7, 6, 6, 5, 6, 5, 5, 4, 6, 5, 5, 4, 5, 4, 4, 3, 
		6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 
		6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 
		5, 4, 4, 3, 4, 3, 3, 2, 4, 3, 3, 2, 3, 2, 2, 1, 
		7, 6, 6, 5, 6, 5, 5, 4, 6, 5, 5, 4, 5, 4, 4, 3, 
		6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 
		6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 
		5, 4, 4, 3, 4, 3, 3, 2, 4, 3, 3, 2, 3, 2, 2, 1, 
		6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 
		5, 4, 4, 3, 4, 3, 3, 2, 4, 3, 3, 2, 3, 2, 2, 1, 
		5, 4, 4, 3, 4, 3, 3, 2, 4, 3, 3, 2, 3, 2, 2, 1, 
		4, 3, 3, 2, 3, 2, 2, 1, 3, 2, 2, 1, 2, 1, 1, 0, 
	};
}
