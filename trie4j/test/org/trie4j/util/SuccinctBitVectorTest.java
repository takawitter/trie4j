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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;

import org.junit.Test;
import org.trie4j.bv.BytesSuccinctBitVector;

public class SuccinctBitVectorTest {
	@Test
	public void test_rank() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(16);
		for(int i = 0; i < 2048; i++){
			bv.append(i % 2 == 0);
			Assert.assertEquals(i / 2 + 1, bv.rank1(i));
			Assert.assertEquals(i / 2 + i % 2, bv.rank0(i));
		}
	}

	@Test
	public void test_select0_1() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(16);
		bv.append(true);
		bv.append(true);
		bv.append(false);
		bv.append(true);
		bv.append(true);
		bv.append(false);
		Assert.assertEquals(2, bv.select0(1));
	}

	@Test
	public void test_select0_2() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(1);
		for(int i = 0; i < 2000; i++){
			bv.append(true);
			bv.append(true);
			bv.append(false);
		}
		Assert.assertEquals(14, bv.select0(5));
		Assert.assertEquals(59, bv.select0(20));
		Assert.assertEquals(89, bv.select0(30));
		Assert.assertEquals(104, bv.select0(35));
		Assert.assertEquals(299, bv.select0(100));
		Assert.assertEquals(1076, bv.select0(359));
		Assert.assertEquals(3899, bv.select0(1300));
		Assert.assertEquals(-1, bv.select0(2001));
	}

	@Test
	public void test_select0_3() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(1);
		for(int i = 0; i < 64; i++){
			bv.append0();
		}
		bv.append1();
		Assert.assertEquals(-1, bv.select0(65));
		bv.append1();
		bv.append1();
		bv.append1();
		bv.append1();
		bv.append1();
		bv.append1();
		Assert.assertEquals(-1, bv.select0(65));
		bv.append0();
		Assert.assertEquals(71, bv.select0(65));
		Assert.assertEquals(-1, bv.select0(2001));
	}

	@Test
	public void test_select1_1() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(1);
		for(int i = 0; i < 2000; i++){
			bv.append(true);
			bv.append(true);
			bv.append(false);
		}
		Assert.assertEquals(0, bv.select1(1));
		Assert.assertEquals(4, bv.select1(4));
		Assert.assertEquals(10, bv.select1(8));
		Assert.assertEquals(16, bv.select1(12));
		Assert.assertEquals(1948, bv.select1(1300));
	}

	@Test
	public void test_select_fail_1() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(1);
		Assert.assertEquals(-1, bv.select1(9));
		Assert.assertEquals(-1, bv.select0(1));
		bv.append0();
		Assert.assertEquals(-1, bv.select1(9));
		Assert.assertEquals(0, bv.select0(1));
		Assert.assertEquals(-1, bv.select0(2));
	}

	@Test
	public void test_next0_1() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector();
		bv.append(false);
		bv.append(false);
		Assert.assertEquals(0, bv.next0(0));
		Assert.assertEquals(1, bv.next0(1));
	}

	@Test
	public void test_next0_2() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector();
		bv.append(true);
		bv.append(false);
		bv.append(true);
		bv.append(false);
		Assert.assertEquals(1, bv.next0(0));
		Assert.assertEquals(3, bv.next0(2));
	}

	@Test
	public void test_next0_3() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector();
		for(int i = 0; i < 8; i++){
			bv.append(true);
		}
		bv.append(false);
		Assert.assertEquals(8, bv.next0(0));
	}

	@Test
	public void test_next0_4() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector();
		for(int i = 0; i < 130; i++){
			bv.append(true);
		}
		bv.append(false);
		Assert.assertEquals(130, bv.next0(0));
	}

	@Test
	public void test_next0_5() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector();
		for(int i = 0; i < 63; i++){
			bv.append(true);
		}
		bv.append(false);
		bv.append(false);
		Assert.assertEquals(63, bv.next0(0));
		Assert.assertEquals(64, bv.next0(64));
	}

	@Test
	public void test_hugedata_rank1() throws Exception{
		int size = 1000000;
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(size);
		for(int i = 0; i < size; i++){
			bv.append(true);
		}
		for(int i = 0; i < 100000; i++){
			Assert.assertEquals(size, bv.rank1(size - 1));
		}
	}

	@Test
	public void test_hugedata_select0() throws Exception{
		int size = 1000000;
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector(size);
		for(int i = 0; i < size; i++){
			bv.append0();
		}
		for(int i = 1; i <= 100000; i++){
			Assert.assertEquals(i - 1, bv.select0(i));
		}
	}

	@Test
	public void test_write_read() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector();
		for(int i = 0; i < 1000; i++){
			bv.append0();
			bv.append1();
			bv.append1();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new ObjectOutputStream(baos).writeObject(bv);
		BytesSuccinctBitVector bv2 = (BytesSuccinctBitVector)new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject();
		for(int i = 0; i < 1000; i++){
			Assert.assertEquals(i + 1, bv2.rank0(i * 3));
			Assert.assertEquals(i * 2 + 1, bv2.rank1(i * 3 + 1));
			Assert.assertEquals(i * 2 + 2, bv2.rank1(i * 3 + 2));
		}
	}

	@Test
	public void test_save_load() throws Exception{
		BytesSuccinctBitVector bv = new BytesSuccinctBitVector();
		for(int i = 0; i < 1000; i++){
			bv.append0();
			bv.append1();
			bv.append1();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(bv);
		BytesSuccinctBitVector bv2 = (BytesSuccinctBitVector)new ObjectInputStream(
				new ByteArrayInputStream(baos.toByteArray()))
				.readObject();
		for(int i = 0; i < 1000; i++){
			Assert.assertEquals(i + 1, bv2.rank0(i * 3));
			Assert.assertEquals(i * 2 + 1, bv2.rank1(i * 3 + 1));
			Assert.assertEquals(i * 2 + 2, bv2.rank1(i * 3 + 2));
		}
	}
}
