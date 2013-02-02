package org.trie4j.tail;

import org.junit.Test;

import junit.framework.Assert;

public class ArrayTailIndexTest {
	@Test
	public void test() throws Exception{
		TailIndex ti = new ArrayTailIndex();
		ti.add(0, 10);
		ti.add(10, 15);
		Assert.assertEquals(0, ti.get(0));
		Assert.assertEquals(10, ti.get(1));
	}

	@Test
	public void test2() throws Exception{
		TailIndex ti = new ArrayTailIndex();
		try{
			ti.get(0);
			Assert.fail();
		} catch(ArrayIndexOutOfBoundsException e){
		}
	}

	@Test
	public void test3() throws Exception{
		TailIndex ti = new ArrayTailIndex();
		ti.add(0, 10);
		ti.add(-1, -1);
		ti.add(10, 15);
		Assert.assertEquals(0, ti.get(0));
		Assert.assertEquals(-1, ti.get(1));
		Assert.assertEquals(10, ti.get(2));
	}
}
