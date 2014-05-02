package org.trie4j.util;

import org.junit.Assert;

import org.junit.Test;

public class FastBitSetTest {
	@Test
	public void test_set_1() throws Exception{
		FastBitSet bs = new FastBitSet();
		for(int i = 0; i < 1000; i++){
			bs.set(i);
			Assert.assertTrue(bs.get(i));
		}
	}

	@Test
	public void test_set_2() throws Exception{
		FastBitSet bs = new FastBitSet();
		for(int i = 0; i < 8; i++){
			int index = (int)Math.pow(10, i);
			bs.set(index);
			Assert.assertTrue(bs.get(index));
		}
	}

	@Test
	public void test_unset_1() throws Exception{
		FastBitSet bs = new FastBitSet();
		for(int i = 0; i < 1000; i++){
			bs.set(i);
			bs.unset(i);
			Assert.assertFalse(bs.get(i));
		}
	}

	@Test
	public void test_unset_2() throws Exception{
		FastBitSet bs = new FastBitSet();
		for(int i = 0; i < 8; i++){
			int index = (int)Math.pow(10, i);
			bs.set(index);
			bs.unset(index);
			Assert.assertFalse(bs.get(index));
		}
	}
}
