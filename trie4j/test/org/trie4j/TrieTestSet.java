package org.trie4j;

import junit.framework.Assert;

import org.junit.Test;

public abstract class TrieTestSet {
	protected abstract Trie trieWithWords(String... words);

	@Test
	public void testContains_1() throws Exception{
		Trie t = trieWithWords("");
		Assert.assertFalse(t.contains("hello"));
	}

	@Test
	public void testContains_2() throws Exception{
		Trie t = trieWithWords("hello");
		Assert.assertTrue(t.contains("hello"));
	}

	@Test
	public void testContains_3() throws Exception{
		Trie t = trieWithWords("helloworld");
		Assert.assertFalse(t.contains("hello"));
	}

	@Test
	public void testContains_4() throws Exception{
		Trie t = trieWithWords("hello", "world");
		Assert.assertTrue(t.contains("hello"));
	}

	@Test
	public void testContains_5() throws Exception{
		Trie t = trieWithWords("hello", "helloworld");
		Assert.assertTrue(t.contains("hello"));
	}

	@Test
	public void testContains_6() throws Exception{
		Trie trie = trieWithWords("hello", "hell");
		Assert.assertTrue(trie.contains("hello"));
	}

	@Test
	public void testContains_7() throws Exception{
		Trie t = trieWithWords("hell", "hello");
		Assert.assertTrue(t.contains("hello"));
	}
}

