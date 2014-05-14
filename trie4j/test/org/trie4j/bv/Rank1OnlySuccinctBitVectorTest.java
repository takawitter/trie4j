package org.trie4j.bv;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.util.FastBitSet;

public class Rank1OnlySuccinctBitVectorTest {
	@Test
	public void test_1() throws Exception{
		FastBitSet bs = new FastBitSet();
		bs.set(11);
		bs.set(22);
		bs.set(28);
		bs.set(34);
		bs.set(67);
		Rank1OnlySuccinctBitVector bv = new Rank1OnlySuccinctBitVector(
				bs.getBytes(), bs.size());
		Assert.assertEquals(1, bv.rank1(11));
		Assert.assertEquals(2, bv.rank1(22));
		Assert.assertEquals(3, bv.rank1(28));
		Assert.assertEquals(4, bv.rank1(34));
		Assert.assertEquals(5, bv.rank1(67));
	}

	@Test
	public void test_2() throws Exception{
		Rank1OnlySuccinctBitVector bv = new Rank1OnlySuccinctBitVector(
				new byte[]{127, -12, -102, -1, -6, 95, -1, -33, -128},
				65
				);
		Assert.assertEquals(52, bv.rank1(64));
	}

	@Test
	public void test_3() throws Exception{
		Rank1OnlySuccinctBitVector bv = new Rank1OnlySuccinctBitVector();
		for(int i = 0; i < 9; i++){
			bv.append1();
		}
		Assert.assertEquals(9, bv.rank1(8));
	}
}
