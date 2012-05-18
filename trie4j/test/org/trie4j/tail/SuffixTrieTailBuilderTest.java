package org.trie4j.tail;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.tail.SuffixTrieTailBuilder;

public class SuffixTrieTailBuilderTest {
	@Test
	public void test_tailtrie_1() throws Exception{
		SuffixTrieTailBuilder tb = new SuffixTrieTailBuilder();
		tb.insert("hello".toCharArray());
		tb.insert("mello".toCharArray());
		SuffixTrieTailBuilder.Node root = tb.getRoot();
		Assert.assertNotNull(root);
		Assert.assertNotNull(root.getChildren());
		Assert.assertEquals("ello", root.getLetters(tb.getTails()).toString());
		Assert.assertEquals("h", root.getChildren()[0].getLetters(tb.getTails()));
		Assert.assertEquals("m", root.getChildren()[1].getLetters(tb.getTails()));
	}

	@Test
	public void test_tailtrie_2() throws Exception{
		SuffixTrieTailBuilder tb = new SuffixTrieTailBuilder();
		tb.insert("world".toCharArray());
		tb.insert("helloworld".toCharArray());
		SuffixTrieTailBuilder.Node root = tb.getRoot();
		Assert.assertNotNull(root);
		Assert.assertNotNull(root.getChildren());
		Assert.assertEquals("world", root.getLetters(tb.getTails()));
		Assert.assertEquals("hello", root.getChildren()[0].getLetters(tb.getTails()));
	}

	@Test
	public void test_tailtrie_3() throws Exception{
		SuffixTrieTailBuilder tb = new SuffixTrieTailBuilder();
		tb.insert("world".toCharArray());
		tb.insert("hellorld".toCharArray());
		tb.insert("bold".toCharArray());
		SuffixTrieTailBuilder.Node root = tb.getRoot();
		Assert.assertNotNull(root);
		Assert.assertNotNull(root.getChildren());
		Assert.assertEquals("ld", root.getLetters(tb.getTails()));
		Assert.assertEquals("bo", root.getChildren()[0].getLetters(tb.getTails()));
		Assert.assertEquals("or", root.getChildren()[1].getLetters(tb.getTails()));
		Assert.assertEquals("hell", root.getChildren()[1].getChildren()[0].getLetters(tb.getTails()));
		Assert.assertEquals("w", root.getChildren()[1].getChildren()[1].getLetters(tb.getTails()));
		Assert.assertEquals("world\0hell\1\1\0bold\0", tb.getTails().toString());
	}

}
