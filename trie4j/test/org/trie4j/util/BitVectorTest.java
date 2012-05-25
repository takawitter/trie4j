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

import junit.framework.Assert;

import org.junit.Test;

public class BitVectorTest {
	@Test
	public void test_rank() throws Exception{
		BitVector bv = new BitVector(16);
		for(int i = 0; i < 135; i++){
			bv.append(true);
			Assert.assertEquals(i + 1, bv.rank(i, true));
			Assert.assertEquals(0, bv.rank(i, false));
		}
	}

	@Test
	public void test_select() throws Exception{
		BitVector bv = new BitVector(16);
		bv.append(true);
		bv.append(true);
		bv.append(false);
		bv.append(true);
		bv.append(true);
		bv.append(false);
		Assert.assertEquals(4, bv.select(4, true));
		Assert.assertEquals(2, bv.select(1, false));
	}

	@Test
	public void test_select_2() throws Exception{
		BitVector bv = new BitVector(1);
		for(int i = 0; i < 8; i++){
			bv.append(true);
			bv.append(true);
			bv.append(false);
		}
		Assert.assertEquals(16, bv.select(12, true));
		Assert.assertEquals(14, bv.select(5, false));
	}

	@Test
	public void test_select_fail_1() throws Exception{
		BitVector bv = new BitVector(1);
		Assert.assertEquals(-1, bv.select(9, true));
		Assert.assertEquals(-1, bv.select(1, false));
		bv.append(false);
		Assert.assertEquals(-1, bv.select(9, true));
		Assert.assertEquals(0, bv.select(1, false));
		Assert.assertEquals(-1, bv.select(2, false));
	}
}
