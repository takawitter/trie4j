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
package org.trie4j;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.patricia.tail.TailPatriciaTrie;
import org.trie4j.tail.builder.ConcatTailBuilder;

public abstract class AbstractTrieTest {
	protected Trie createFirstTrie(){
		return new TailPatriciaTrie(new ConcatTailBuilder());
	}

	protected Trie buildSecondTrie(Trie firstTrie){
		return firstTrie;
	}

	@Test
	public void test_empty() throws Exception{
		Trie trie = createFirstTrie();
		trie = buildSecondTrie(trie);
		Assert.assertEquals(0, trie.size());
		Assert.assertFalse(trie.contains("hello"));
		Assert.assertFalse(trie.commonPrefixSearch("hello").iterator().hasNext());
		Assert.assertFalse(trie.predictiveSearch("hello").iterator().hasNext());
		Assert.assertEquals(-1, trie.findWord("hello", 0, 5, new StringBuilder()));
	}

	@Test
	public void test_size_1() throws Exception{
		Trie t = trieWithWords("hello", "world");
		Assert.assertEquals(2, t.size());
	}

	@Test
	public void test_size_2() throws Exception{
		Trie t = trieWithWords("hello", "hel", "world");
		Assert.assertEquals(3, t.size());
	}

	@Test
	public void test_size_3() throws Exception{
		Trie t = trieWithWords("hello", "hel", "world", "hel");
		Assert.assertEquals(3, t.size());
	}

	@Test
	public void test_size_4() throws Exception{
		Trie t = trieWithWords("hello", "helicoptor", "world", "hel");
		Assert.assertEquals(4, t.size());
	}

	@Test
	public void test_size_5() throws Exception{
		Trie t = trieWithWords("");
		Assert.assertEquals(1, t.size());
	}

	@Test
	public void test_contains_1() throws Exception{
		doTestContains("");
	}

	@Test
	public void test_contains_2() throws Exception{
		doTestContains("hello");
	}

	@Test
	public void test_contains_3() throws Exception{
		doTestContains("hello", "hi");
	}

	@Test
	public void test_contains_4() throws Exception{
		doTestContains("hello", "hi", "world");
	}

	@Test
	public void test_contains_5() throws Exception{
		doTestContains("hello", "hi", "hell", "helloworld", "world");
	}

	@Test
	public void test_contains_6() throws Exception{
		doTestContainsAndNot(new String[]{"hello", "hi", "helloworld", "world"}, new String[]{"h", "hell", "worl"});
	}

	@Test
	public void test_contains_7() throws Exception{
		doTestContains("hell", "hello", "apple", "orange", "banana", "watermelon",
				"peach", "kiwi", "cherry", "hassaku", 
				"yokan", "yatsuhashi", "anmitsu", "zenzai", "shiratama",
				"hiyokomanju", "zundamochi", "kuromitsu", "wasanbon", "botamochi",
				"warabimochi", "jelly", "momo", "nori", "donburi", "engawa",
				"gomokuni", "ikura"
				);
	}

	@Test
	public void test_commonPrefixSearch_1() throws Exception{
		Trie t = trieWithWords();
		Assert.assertFalse(t.commonPrefixSearch("hello").iterator().hasNext());
	}

	@Test
	public void test_commonPrefixSearch_2() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hell", "helloworld2");
		Iterator<String> it = t.commonPrefixSearch("helloworld").iterator();
		Assert.assertEquals("hell", it.next());
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void test_commonPrefixSearch_3() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hi", "howsgoing", "hell", "helloworld2", "world");
		Iterator<String> it = t.commonPrefixSearch("helloworld").iterator();
		Assert.assertEquals("hell", it.next());
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void test_findWord_1() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hi", "howsgoing", "hell", "helloworld2", "world");
		String text = "This is the best sweets.";
		StringBuilder b = new StringBuilder();
		int i = t.findWord(text, 0, text.length(), b);
		Assert.assertEquals(1, i);
		Assert.assertEquals("hi", b.toString());
	}

	@Test
	public void test_predictiveSearch_1() throws Exception{
		Trie t = trieWithWords();
		Assert.assertFalse(t.predictiveSearch("hello").iterator().hasNext());
	}

	@Test
	public void test_predictiveSearch_2() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hell", "helloworld2");
		Iterator<String> it = t.predictiveSearch("he").iterator();
		Assert.assertEquals("hell", it.next());
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld", it.next());
		Assert.assertEquals("helloworld2", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void test_predictiveSearch_3() throws Exception{
		Trie t = trieWithWords("hello", "helloworld1", "hell", "helloworld2");
		Iterator<String> it = t.predictiveSearch("hello").iterator();
		Assert.assertEquals("hello", it.next());
		Assert.assertEquals("helloworld1", it.next());
		Assert.assertEquals("helloworld2", it.next());
		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void test_predictiveSearch_4() throws Exception{
		Trie t = trieWithWords("hello", "helloworld", "hi", "howsgoing", "hell", "helloworld2", "world");
		Iterator<String> it = t.predictiveSearch("hellow").iterator();
		Assert.assertEquals("helloworld", it.next());
		Assert.assertEquals("helloworld2", it.next());
		Assert.assertFalse(it.hasNext());
	}

	private void doTestContains(String... words) throws Exception{
		Trie trie = trieWithWords(words);
		for(String w : words){
			Assert.assertTrue("must contain \"" + w  + "\"", trie.contains(w));
		}
		Assert.assertFalse("must not contain \"buzzbuzz\"", trie.contains("buzzbuzz"));
	}

	private void doTestContainsAndNot(String[] words, String[] notContains) throws Exception{
		Trie trie = trieWithWords(words);
		for(String w : words){
			Assert.assertTrue("must contain \"" + w  + "\"", trie.contains(w));
		}
		for(String w : notContains){
			Assert.assertFalse("must not contain \"" + w  + "\"", trie.contains(w));
		}
	}

	private Trie trieWithWords(String... words){
		Trie ret = createFirstTrie();
		for(String w : words) ret.insert(w);
		return ret;
	}
}
