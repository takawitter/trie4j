package org.trie4j.tail.index;

import org.junit.Test;
import org.trie4j.tail.TailIndex;
import org.trie4j.tail.index.SBVTailIndex;

import junit.framework.Assert;

public class SBVTailIndexTest {
	@Test
	public void test() throws Exception{
		SBVTailIndex ti = new SBVTailIndex();
		ti.add(0, 10);
		ti.add(10, 15);
		Assert.assertEquals(0, ti.get(0));
		Assert.assertEquals(10, ti.get(1));
	}

	@Test
	public void test2() throws Exception{
		TailIndex ti = new SBVTailIndex();
		Assert.assertEquals(-1, ti.get(0));
	}

	@Test
	public void test3() throws Exception{
		SBVTailIndex ti = new SBVTailIndex();
		ti.add(0, 10);
		ti.add(-1, -1);
		ti.add(10, 15);
		Assert.assertEquals(0, ti.get(0));
		Assert.assertEquals(-1, ti.get(1));
		Assert.assertEquals(10, ti.get(2));
	}

	@Test
	public void test4() throws Exception{
		TailIndex ti = new SBVTailIndex();
		ti.add(-1, -1);
		ti.add(-1, -1);
		ti.add(0, 5);
		ti.add(5, 9);
		ti.add(-1, -1);
		ti.add(9, 12);
		ti.add(12, 16);
		ti.add(16, 20);
		Assert.assertEquals(-1, ti.get(0));
		Assert.assertEquals(-1, ti.get(1));
		Assert.assertEquals(0, ti.get(2));
		Assert.assertEquals(5, ti.get(3));
		Assert.assertEquals(-1, ti.get(4));
		Assert.assertEquals(9, ti.get(5));
		Assert.assertEquals(12, ti.get(6));
		Assert.assertEquals(16, ti.get(7));
	}
}
