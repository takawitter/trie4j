/*
 * Copyright 2012 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trie4j.doublearray;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.Trie;
import org.trie4j.TrieTestSet;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.tail.SuffixTrieTailBuilder;

public class TailDoubleArrayWithSuffixTrieTailBuilderTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		PatriciaTrie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		return new TailDoubleArray(trie, new SuffixTrieTailBuilder());
	}

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
		Assert.assertTrue(da.contains("page_title"));
	}

	private Trie newDA(Trie trie){
		return new TailDoubleArray(trie, new SuffixTrieTailBuilder());
	}

	public static void main(String[] args) throws Exception{
		Trie t = new PatriciaTrie();
		t.insert("hello");
//		t.insert("hi");
		DoubleArray da = new DoubleArray(t);
		System.out.println("hello: " + da.contains("hello"));
		System.out.println("hi: " + da.contains("hi"));
		System.out.println("world: " + da.contains("world"));
	}
}
