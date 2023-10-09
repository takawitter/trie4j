package org.trie4j.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.Trie;
import org.trie4j.louds.TailLOUDSTrie;
import org.trie4j.louds.bvtree.LOUDSPPBvTree;
import org.trie4j.patricia.PatriciaTrie;
import org.trie4j.tail.SuffixTrieDenseTailArrayBuilder;


public class RWTest {
	@Test
	public void test() throws Throwable{
		Trie ot = new PatriciaTrie();
		ot.insert("hello");
		ot.insert("hell");
		ot.insert("world");
		Trie t2 = new TailLOUDSTrie(ot, new LOUDSPPBvTree(ot.nodeSize()),
				new SuffixTrieDenseTailArrayBuilder());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		new TrieWriter(baos).write(t2);
		Trie t3 = new TrieReader(new ByteArrayInputStream(baos.toByteArray()))
			.read();
		Assert.assertEquals(t2.getClass(), t3.getClass());
		Assert.assertEquals(t2.size(), t3.size());
		Assert.assertEquals(t2.nodeSize(), t3.nodeSize());
		for(String w : t2.predictiveSearch("")) {
			Assert.assertTrue(t3.contains(w));
		}
		
	}
}
