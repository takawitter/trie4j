package org.trie4j.doublearray;

import junit.framework.Assert;

import org.junit.Test;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.Trie;

public class DoubleArrayTest {
	@Test
	public void test() throws Exception{
		DoubleArray da = new DoubleArray(new PatriciaTrie());
		Assert.assertFalse(da.contains("hello"));
	}

	@Test
	public void test2() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		DoubleArray da = new DoubleArray(trie);
		da.dump();
		Assert.assertTrue(da.contains("hello"));
		Assert.assertFalse(da.contains("hi"));
	}

	@Test
	public void test3() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		trie.insert("hi");
		DoubleArray da = new DoubleArray(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertTrue(da.contains("hi"));
		Assert.assertFalse(da.contains("world"));
	}

	@Test
	public void test4() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		trie.insert("hi");
		trie.insert("world");
		DoubleArray da = new DoubleArray(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertTrue(da.contains("hi"));
		Assert.assertTrue(da.contains("world"));
	}

	@Test
	public void test5() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		trie.insert("world");
		DoubleArray da = new DoubleArray(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertFalse(da.contains("hi"));
		Assert.assertTrue(da.contains("world"));
	}

	public static void main(String[] args) throws Exception{
		Trie t = new PatriciaTrie();
		t.insert("hello");
//		t.insert("hi");
		DoubleArray da = new DoubleArray(t);
		da.dump();
		System.out.println("hello: " + da.contains("hello"));
		System.out.println("hi: " + da.contains("hi"));
		System.out.println("world: " + da.contains("world"));
	}
}
