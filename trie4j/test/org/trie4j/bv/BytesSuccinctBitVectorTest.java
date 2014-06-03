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
package org.trie4j.bv;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.util.FastBitSet;

public class BytesSuccinctBitVectorTest {
	private SuccinctBitVector create(){
		return new BytesSuccinctBitVector();
	}

	private SuccinctBitVector create(int initialCapacity){
		return new BytesSuccinctBitVector(initialCapacity);
	}

	private SuccinctBitVector create(byte[] bytes, int bitsSize){
		return new BytesSuccinctBitVector(bytes, bitsSize);
	}

	@Test
	public void test_empty_1() throws Exception{
		SuccinctBitVector sbv = create();
		Assert.assertEquals(0, sbv.size());
	}

	@Test
	public void test_append_1() throws Exception{
		SuccinctBitVector sbv = create();
		sbv.append0();
		Assert.assertEquals(1, sbv.rank0(0));
		Assert.assertEquals(0, sbv.rank1(0));
	}

	@Test
	public void test_append_2() throws Exception{
		SuccinctBitVector sbv = create();
		sbv.append1();
		Assert.assertEquals(0, sbv.rank0(0));
		Assert.assertEquals(1, sbv.rank1(0));
	}

	@Test
	public void test_append_3() throws Exception{
		SuccinctBitVector sbv = create();
		for(int i = 0; i < 8; i++){
			sbv.append1();
			sbv.append0();
		}
		Assert.assertEquals(8, sbv.rank0(15));
		Assert.assertEquals(8, sbv.rank1(15));
	}

	@Test
	public void test_append_4_append0() throws Exception{
		SuccinctBitVector sbv = create(1);
		for(int i = 0; i < 1000; i++){
			sbv.append0();
		}
	}

	@Test
	public void test_create_from_bytes_1() throws Exception{
		SuccinctBitVector sbv = create(new byte[]{(byte)0xf3, 0x48}, 16);
		Assert.assertEquals(8, sbv.rank0(15));
		Assert.assertEquals(8, sbv.rank1(15));
	}

	@Test
	public void test_create_from_bytes_2() throws Exception{
		FastBitSet bs = new FastBitSet();
		int pos = 0;
		// tib.addEmpty(0);
		bs.unset(pos++); // 0
		//tib.addEmpty(1);
		bs.unset(pos++); // 1
		// tib.add(2, 0, 5);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.unset(pos++); // 7
		// tib.add(3, 5, 9);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.unset(pos++); // 12
		//tib.addEmpty(4);
		bs.unset(pos++); // 13
		// tib.add(5, 9, 12);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.unset(pos++); // 17
		// tib.add(6, 12, 16);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.unset(pos++); // 22
		// tib.add(7, 16, 20);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.set(pos++);
		bs.unset(pos++); // 27

		BytesSuccinctBitVector sbv = new BytesSuccinctBitVector(bs.getBytes(), bs.size());
		Assert.assertEquals(28, bs.size());
		Assert.assertEquals(8, sbv.getCountCache0()[0]);
		
		Assert.assertEquals(-1, sbv.select0(0));
		Assert.assertEquals(0, sbv.select0(1));
		Assert.assertEquals(1, sbv.select0(2));
		Assert.assertEquals(7, sbv.select0(3));
		Assert.assertEquals(12, sbv.select0(4));
		Assert.assertEquals(13, sbv.select0(5));
		Assert.assertEquals(17, sbv.select0(6));
		Assert.assertEquals(22, sbv.select0(7));
		Assert.assertEquals(27, sbv.select0(8));
	}
}
