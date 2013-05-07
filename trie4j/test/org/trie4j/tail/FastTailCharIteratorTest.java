package org.trie4j.tail;

import org.junit.Assert;
import org.junit.Test;

public class FastTailCharIteratorTest {
	@Test
	public void test_3() throws Exception{
		String chars = "abc\1\6\0def\0";
		FastTailCharIterator it = new FastTailCharIterator(chars, 0);
		Assert.assertEquals('a', it.getNext());
		Assert.assertEquals('b', it.getNext());
		Assert.assertEquals('c', it.getNext());
		Assert.assertEquals('d', it.getNext());
		Assert.assertEquals('e', it.getNext());
		Assert.assertEquals('f', it.getNext());
		Assert.assertEquals('\0', it.getNext());
	}
}
