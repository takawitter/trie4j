package org.trie4j.doublearray;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.Trie;
import org.trie4j.patricia.simple.PatriciaTrie;

public class TailCompactionDoubleArrayTest {
	@Test
	public void test() throws Exception{
		Trie da = newDA(new PatriciaTrie());
		Assert.assertFalse(da.contains("hello"));
	}

	@Test
	public void test2() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		Trie da = newDA(trie);
		((TailCompactionDoubleArray)da).dump();
		Assert.assertTrue(da.contains("hello"));
		Assert.assertFalse(da.contains("hi"));
	}

	@Test
	public void test3() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		trie.insert("hi");
		Trie da = newDA(trie);
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
		Trie da = newDA(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertTrue(da.contains("hi"));
		Assert.assertTrue(da.contains("world"));
	}

	@Test
	public void test5() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		trie.insert("world");
		Trie da = newDA(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertFalse(da.contains("hi"));
		Assert.assertTrue(da.contains("world"));
	}

	@Test
	public void test6() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("hello");
		trie.insert("world");
		trie.insert("helloworld");
		trie.insert("javaworld");
		Trie da = newDA(trie);
		Assert.assertTrue(da.contains("hello"));
		Assert.assertFalse(da.contains("hi"));
		Assert.assertTrue(da.contains("world"));
		Assert.assertTrue(da.contains("helloworld"));
		Assert.assertTrue(da.contains("javaworld"));
	}

	@Test
	public void test7() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("page_title");
		trie.insert("!!!");
		trie.insert("!_-attention-");
		Trie da = newDA(trie);
		for(String s : da.predictiveSearch("p")){
			System.out.println(s);
		}
		da.dump();
		Assert.assertTrue(da.contains("page_title"));
	}

	@Test
	public void test_tailtrie_1() throws Exception{
		TailCompactionDoubleArray da = new TailCompactionDoubleArray();
		TailCompactionDoubleArray.TailTrie tt = da.new TailTrie();
		tt.insert("hello".toCharArray());
		tt.insert("mello".toCharArray());
		TailCompactionDoubleArray.TailTrieNode root = tt.getRoot();
		Assert.assertNotNull(root);
		Assert.assertNotNull(root.getChildren());
		Assert.assertArrayEquals("ello".toCharArray(), root.getLetters(da.getTails()));
		Assert.assertArrayEquals("h".toCharArray(), root.getChildren()[0].getLetters(da.getTails()));
		Assert.assertArrayEquals("m".toCharArray(), root.getChildren()[1].getLetters(da.getTails()));
	}

	@Test
	public void test_tailtrie_2() throws Exception{
		TailCompactionDoubleArray da = new TailCompactionDoubleArray();
		TailCompactionDoubleArray.TailTrie tt = da.new TailTrie();
		tt.insert("world".toCharArray());
		tt.insert("helloworld".toCharArray());
		TailCompactionDoubleArray.TailTrieNode root = tt.getRoot();
		Assert.assertNotNull(root);
		Assert.assertNotNull(root.getChildren());
		Assert.assertArrayEquals("world".toCharArray(), root.getLetters(da.getTails()));
		Assert.assertArrayEquals("hello".toCharArray(), root.getChildren()[0].getLetters(da.getTails()));
	}

	@Test
	public void test_tailtrie_3() throws Exception{
		TailCompactionDoubleArray da = new TailCompactionDoubleArray();
		TailCompactionDoubleArray.TailTrie tt = da.new TailTrie();
		tt.insert("world".toCharArray());
		tt.insert("hellorld".toCharArray());
		tt.insert("bold".toCharArray());
		TailCompactionDoubleArray.TailTrieNode root = tt.getRoot();
		Assert.assertNotNull(root);
		Assert.assertNotNull(root.getChildren());
		Assert.assertArrayEquals("ld".toCharArray(), root.getLetters(da.getTails()));
		Assert.assertArrayEquals("bo".toCharArray(), root.getChildren()[0].getLetters(da.getTails()));
		Assert.assertArrayEquals("or".toCharArray(), root.getChildren()[1].getLetters(da.getTails()));
		Assert.assertArrayEquals("hell".toCharArray(), root.getChildren()[1].getChildren()[0].getLetters(da.getTails()));
		Assert.assertArrayEquals("w".toCharArray(), root.getChildren()[1].getChildren()[1].getLetters(da.getTails()));
		Assert.assertEquals("world\0hell\1\1\0bo\1\3\0", da.getTails().toString());
	}

	private Trie newDA(Trie trie){
		return new TailCompactionDoubleArray(trie);
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
