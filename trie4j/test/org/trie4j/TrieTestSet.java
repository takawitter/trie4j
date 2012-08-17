package org.trie4j;

import java.util.Iterator;

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
		Trie t = trieWithWords("hello", "hell");
		Assert.assertTrue(t.contains("hello"));
	}

	@Test
	public void testContains_7() throws Exception{
		Trie t = trieWithWords("hell", "hello");
		Assert.assertTrue(t.contains("hello"));
	}

	@Test
	public void testCPS_1() throws Exception{
		Trie t = trieWithWords();
		Assert.assertFalse(t.commonPrefixSearch("hello").iterator().hasNext());
	}

	@Test
	public void testCPS_2() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hell", "helloworld2");
		Iterator<String> it = t.commonPrefixSearch("helloworld").iterator();
		Assert.assertEquals("hell", it.next());
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void testCPS_3() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hi", "howsgoing", "hell", "helloworld2", "world");
		Iterator<String> it = t.commonPrefixSearch("helloworld").iterator();
		Assert.assertEquals("hell", it.next());
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void testPS_1() throws Exception{
		Trie t = trieWithWords();
		Assert.assertFalse(t.predictiveSearch("hello").iterator().hasNext());
	}

	@Test
	public void testPS_2() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hell", "helloworld2");
		Iterator<String> it = t.predictiveSearch("he").iterator();
		Assert.assertEquals("hell", it.next());
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld", it.next());
		Assert.assertEquals("helloworld2", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void testPS_3() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hell", "helloworld2");
		Iterator<String> it = t.predictiveSearch("hello").iterator();
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld", it.next());
		Assert.assertEquals("helloworld2", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void testPS_4() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hi", "howsgoing", "hell", "helloworld2", "world");
		Iterator<String> it = t.predictiveSearch("hellow").iterator();
		Assert.assertEquals("helloworld", it.next());
		Assert.assertEquals("helloworld2", it.next());
		Assert.assertFalse(it.hasNext());
	}
}
