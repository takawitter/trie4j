package org.trie4j.util;

import junit.framework.Assert;

import org.junit.Test;

public class BitVectorTest {
	@Test
	public void test_rank() throws Exception{
		BitVector bv = new BitVector(16);
		for(int i = 0; i < 64; i++){
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
}
