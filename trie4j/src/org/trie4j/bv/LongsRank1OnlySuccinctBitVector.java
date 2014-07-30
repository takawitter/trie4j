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
package org.trie4j.bv;

import java.io.Serializable;
import java.util.Arrays;

public class LongsRank1OnlySuccinctBitVector
implements Serializable, SuccinctBitVector{
	public LongsRank1OnlySuccinctBitVector(){
		this(16);
	}

	public LongsRank1OnlySuccinctBitVector(int initialCapacity){
		if(initialCapacity == 0){
			this.longs = new long[]{};
			this.countCache1 = new int[]{};
		} else{
			this.longs = new long[longsSize(initialCapacity)];
			this.countCache1 = new int[countCacheSize(initialCapacity)];
		}
	}

	public LongsRank1OnlySuccinctBitVector(byte[] bytes, int bitsSize){
		if(bytes.length != ((bitsSize / 8) + 1)){
			this.longs = new long[]{};
			this.countCache1 = new int[]{};
			return;
		}
		this.size = bitsSize;
		this.longs = new long[longsSize(bitsSize)];
		this.countCache1 = new int[countCacheSize(bitsSize)];
		int sum = 0;
		int n = bytes.length - 1;
		if(n > 0){
			sum = BITCOUNTS1[bytes[0] & 0xff];
			for(int i = 1; i < n; i++){
				if(i % BITS_IN_COUNTCACHE == 0) countCache1[(i / BITS_IN_COUNTCACHE) - 1] = sum;
				sum += BITCOUNTS1[bytes[i] & 0xff];
			}
		}
		sum += BITCOUNTS1[bytes[n] & (0x80 >> (bitsSize % BITS_IN_COUNTCACHE))];
		countCache1[bytes.length / BITS_IN_COUNTCACHE] = sum;
	}

	public LongsRank1OnlySuccinctBitVector(
			long[] longs, int size, int size1,
			int[] countCache1) {
		this.longs = longs;
		this.size = size;
		this.size1 = size1;
		this.countCache1 = countCache1;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		int n = Math.min(size, 64);
		for(int i = 0; i < n; i++){
			long m = 0x8000000000000000L >>> (i % BITS_IN_BLOCK);
			long bi = longs[(i / BITS_IN_BLOCK)] & m;
			b.append((bi) != 0 ? "1" : "0");
		}
		return b.toString();
	}

	public long[] getLongs(){
		return longs;
	}

	public int[] getCountCache1(){
		return countCache1;
	}

	@Override
	public boolean get(int pos) {
		return isOne(pos);
	}

	public boolean isZero(int pos){
		return (longs[pos / BITS_IN_BLOCK] & (0x8000000000000000L >>> pos % BITS_IN_BLOCK)) == 0;
	}

	public boolean isOne(int pos){
		return (longs[pos / BITS_IN_BLOCK] & (0x8000000000000000L >>> pos % BITS_IN_BLOCK)) != 0;
	}

	public int size(){
		return size;
	}

	public int getSize1() {
		return size1;
	}

	public void trimToSize(){
		longs = Arrays.copyOf(longs, longsSize(size));
		countCache1 = Arrays.copyOf(countCache1, countCacheSize(size));
	}

	public void append1(){
		int longsi = size / BITS_IN_BLOCK;
		int countCachei = size / BITS_IN_COUNTCACHE;
		if(longsi >= longs.length){
			extendLongsAndCountCache();
		}
		if(size % BITS_IN_COUNTCACHE == 0 && countCachei > 0){
			countCache1[countCachei] = countCache1[countCachei - 1];
		}
		longs[longsi] |= 0x8000000000000000L >>> (size % BITS_IN_BLOCK);
		size++;

		countCache1[countCachei]++;
		size1++;
	}

	public void append0(){
		int longsi = size / BITS_IN_BLOCK;
		int countCachei = size / BITS_IN_COUNTCACHE;
		if(longsi >= longs.length){
			extendLongsAndCountCache();
		}
		if(size % BITS_IN_COUNTCACHE == 0 && countCachei > 0){
			countCache1[countCachei] = countCache1[countCachei - 1];
		}
		size++;
	}

	public void append(boolean bit){
		if(bit) append1();
		else append0();
	}

	public int rank0(int pos){
		int cn = pos / BITS_IN_COUNTCACHE;
		if((pos + 1) % BITS_IN_COUNTCACHE == 0) return (cn + 1) * BITS_IN_COUNTCACHE - countCache1[cn];
		int ret = (cn > 0) ? cn * BITS_IN_COUNTCACHE - countCache1[cn - 1] : 0;
		int n = pos / BITS_IN_BLOCK;
		for(int i = (cn * BITS_IN_COUNTCACHE / BITS_IN_BLOCK); i < n; i++){
			ret += Long.bitCount(~longs[i]);
		}
		return ret + Long.bitCount(~longs[n] & (0x8000000000000000L >> (pos % BITS_IN_BLOCK)));
	}

	public int rank1(int pos){
		int cn = pos / BITS_IN_COUNTCACHE;
		if((pos + 1) % BITS_IN_COUNTCACHE == 0) return countCache1[cn];
		int ret = (cn > 0) ? countCache1[cn - 1] : 0;
		int n = pos / BITS_IN_BLOCK;
		for(int i = (cn * BITS_IN_COUNTCACHE / BITS_IN_BLOCK); i < n; i++){
			ret += Long.bitCount(longs[i]);
		}
		return ret + Long.bitCount(longs[n] & (0x8000000000000000L >> (pos % BITS_IN_BLOCK)));
	}

	public int rank(int pos, boolean b){
		if(b) return rank1(pos);
		else return rank0(pos);
	}

	@Override
	public int select0(int count) {
		for(int i = 0; i < longs.length; i++){
			if(i * BITS_IN_BLOCK >= size) return -1;
			long v = longs[i];
			int c = BITS_IN_BLOCK - Long.bitCount(v);
			if(count <= c){
				for(int j = 0; j < BITS_IN_BLOCK; j++){
					if(i * BITS_IN_BLOCK + j >= size) return -1;
					if((v & 0x8000000000000000L) != 1){
						count--;
						if(count == 0){
							return i * BITS_IN_BLOCK + j;
						}
					}
					v <<= 1;
				}
				return -1;
			}
			count -= c;
		}
		return -1;
	}

	@Override
	public int select1(int count) {
		for(int i = 0; i < longs.length; i++){
			if(i * BITS_IN_BLOCK >= size) return -1;
			long v = longs[i];
			int c = Long.bitCount(v);
			if(count <= c){
				for(int j = 0; j < BITS_IN_BLOCK; j++){
					if(i * BITS_IN_BLOCK + j >= size) return -1;
					if((v & 0x8000000000000000L) != 0){
						count--;
						if(count == 0){
							return i * BITS_IN_BLOCK + j;
						}
					}
					v <<= 1;
				}
				return -1;
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
		if(pos >= size) return -1;
		int longsi = pos / BITS_IN_BLOCK;
		int s = pos % BITS_IN_BLOCK;
		for(; longsi < longs.length; longsi++){
			long v = longs[longsi];
			for(int i = s; i < BITS_IN_BLOCK; i++){
				int p = longsi * BITS_IN_BLOCK + i;
				if(p >= size) return -1;
				if((v & (0x8000000000000000L >>> i)) == 0){
					return p;
				}
			}
			s = 0;
		}
		return -1;
	}

	private void extendLongsAndCountCache(){
		int longsSize = (int)(longs.length * 1.2) + 1;
		longs = Arrays.copyOf(longs, longsSize);
		int cacheSize = longsSize * BITS_IN_BLOCK / BITS_IN_COUNTCACHE + 1;
		countCache1 = Arrays.copyOf(countCache1, cacheSize);
	}

	private static int longsSize(int bitSize){
		return (bitSize - 1) / BITS_IN_BLOCK + 1;
	}

	private static int countCacheSize(int bitSize){
		return (bitSize - 1) / BITS_IN_COUNTCACHE + 1;
	}

	static final int BITS_IN_BLOCK = 64;
	static final int BITS_IN_COUNTCACHE = 1 * 64;
	private long[] longs;
	private int size;
	private int size1;
	private int[] countCache1;

	private static final byte[] BITCOUNTS1 = {
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

	private static final long serialVersionUID = -7658605229245494623L;
}
