package org.trie4j.bv;

import junit.framework.Assert;

import org.junit.Test;

public class BitVectorUtilTest {
	@Test
	public void test() throws Exception{
		BytesSuccinctBitVector r = new BytesSuccinctBitVector();
		// build tree on r
		r.append1(); r.append0(); // super root
		r.append1(); r.append1(); r.append1(); r.append0(); // 0
		r.append1(); r.append1(); r.append1(); r.append0(); // 1
		r.append0(); // 2
		r.append1(); r.append1(); r.append0(); // 3
		r.append0(); // 4
		r.append0(); // 5
		r.append0(); // 6
		r.append1(); r.append0(); // 7
		r.append0(); // 8
		r.append0(); // 9

		// divide to two sbv
		BytesSuccinctBitVector r0 = new BytesSuccinctBitVector();
		BytesSuccinctBitVector r1 = new BytesSuccinctBitVector();
		r0.append0();
		r1.append0();
		BitVectorUtil.divide01(r, r0, r1);
		Assert.assertEquals("101110111001100001000", r.toString());
		Assert.assertEquals("00010111011", r0.toString());
		Assert.assertEquals("00110110100", r1.toString());
//		Assert.assertEquals("1101000100", r0.toString());
//		Assert.assertEquals("1001001011", r1.toString());
		// find first child
		Assert.assertEquals(1, r1.select0(r0.rank0(0)) + 1);
		Assert.assertEquals(5, r1.select0(r0.rank0(2)) + 1);
		Assert.assertEquals(8, r1.select0(r0.rank0(4)) + 1);
		Assert.assertEquals(10, r1.select0(r0.rank0(8)) + 1);
	}
}
