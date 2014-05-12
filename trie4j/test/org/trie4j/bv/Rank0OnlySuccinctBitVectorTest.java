package org.trie4j.bv;

import org.junit.Assert;
import org.junit.Test;

public class Rank0OnlySuccinctBitVectorTest {
	@Test
	public void test() throws Exception{
		Rank0OnlySuccinctBitVector bv = new Rank0OnlySuccinctBitVector(1);
		bv.append0();
		bv.append1();
		Assert.assertEquals(1, bv.rank0(0));
		Assert.assertEquals(1, bv.rank0(1));
		Assert.assertEquals(2, bv.rank0(2));
		Assert.assertEquals(3, bv.rank0(3));
		Assert.assertEquals(4, bv.rank0(4));
		Assert.assertEquals(5, bv.rank0(5));
		Assert.assertEquals(6, bv.rank0(6));
		Assert.assertEquals(7, bv.rank0(7));
		try{
			bv.rank0(8);
			Assert.fail();
		} catch(ArrayIndexOutOfBoundsException e){
		}
	}

	@Test
	public void test2() throws Exception{
		Rank0OnlySuccinctBitVector bv = new Rank0OnlySuccinctBitVector(
				new byte[]{0x2d, 0x3f}, 16);
		Assert.assertEquals(1, bv.rank0(0));
		Assert.assertEquals(2, bv.rank0(1));
		Assert.assertEquals(2, bv.rank0(2));
		Assert.assertEquals(3, bv.rank0(3));
		Assert.assertEquals(3, bv.rank0(4));
		Assert.assertEquals(3, bv.rank0(5));
		Assert.assertEquals(4, bv.rank0(6));
		Assert.assertEquals(4, bv.rank0(7));
		Assert.assertEquals(5, bv.rank0(8));
		Assert.assertEquals(6, bv.rank0(9));
		Assert.assertEquals(6, bv.rank0(10));
		Assert.assertEquals(6, bv.rank0(11));
		Assert.assertEquals(6, bv.rank0(12));
		Assert.assertEquals(6, bv.rank0(13));
		Assert.assertEquals(6, bv.rank0(14));
		Assert.assertEquals(6, bv.rank0(15));
		try{
			bv.rank0(16);
			Assert.fail();
		} catch(ArrayIndexOutOfBoundsException e){
		}
	}
}
