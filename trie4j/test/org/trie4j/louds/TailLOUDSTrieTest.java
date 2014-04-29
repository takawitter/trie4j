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
package org.trie4j.louds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.AbstractTrieTest;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.util.Pair;

public class TailLOUDSTrieTest extends AbstractTrieTest{
	@Override
	protected Trie buildSecondTrie(Trie firstTrie) {
		return new LOUDSTrie(firstTrie, 65536);
	}

	@Test
	public void test() throws Exception{
		String[] words = {"こんにちは", "さようなら", "おはよう", "おはようございます", "おおきなかぶ", "おおやまざき"};
		Trie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		TailLOUDSTrie lt = new TailLOUDSTrie(trie);
		for(String w : words){
			Assert.assertTrue(w, lt.contains(w));
		}
		Assert.assertFalse(lt.contains("おやすみなさい"));
		
		SortedSet<Integer> idSet = new TreeSet<Integer>();
		for (String word : words) {
			idSet.add(lt.getId(word)); 
		}
		Assert.assertEquals(words.length, idSet.size());
		Assert.assertEquals(0, idSet.first().intValue());
		Assert.assertEquals(words.length - 1, idSet.last().intValue());
		
		Iterable<Pair<String, Integer>> prefixList = lt.commonPrefixSearchId("おはようございます");
		int count = 0;
		for (Pair<String, Integer> prefix : prefixList) {
			++count;
			if (prefix.getFirst().equals("おはよう")) {
				Assert.assertEquals(lt.getId("おはよう"), prefix.getSecond().intValue());
			} else if (prefix.getFirst().equals("おはようございます")) {
				Assert.assertEquals(lt.getId("おはようございます"), prefix.getSecond().intValue());
			} else {
				Assert.fail("Unexpected result: " + prefix.getFirst());
			}
		}
		Assert.assertEquals(2, count);
		
		Iterable<Pair<String, Integer>> predictionList = lt.predictiveSearchId("おお");
		count = 0;
		for (Pair<String, Integer> prediction : predictionList) {
			++count;
			if (prediction.getFirst().equals("おおきなかぶ")) {
				Assert.assertEquals(lt.getId("おおきなかぶ"), prediction.getSecond().intValue());
			} else if (prediction.getFirst().equals("おおやまざき")) {
				Assert.assertEquals(lt.getId("おおやまざき"), prediction.getSecond().intValue());
			} else {
				Assert.fail("Unexpected result: " + prediction.getFirst());
			}
		}
		Assert.assertEquals(2, count);
		

		StringBuilder b = new StringBuilder();
		Node[] children = lt.getRoot().getChildren();
		for(Node n : children){
			char[] letters = n.getLetters();
			b.append(letters[0]);
		}
		Assert.assertEquals("おこさ", b.toString());
	}

	@Test
	public void test_save_load() throws Exception{
		String[] words = {"こんにちは", "さようなら", "おはよう", "おおきなかぶ", "おおやまざき"};
		Trie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		TailLOUDSTrie lt = new TailLOUDSTrie(trie);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		lt.save(baos);
		lt = new TailLOUDSTrie();
		lt.load(new ByteArrayInputStream(baos.toByteArray()));
		for(String w : words){
			Assert.assertTrue(lt.contains(w));
		}
		Assert.assertFalse(lt.contains("おやすみなさい"));

		SortedSet<Integer> idSet = new TreeSet<Integer>();
		for (String word : words) {
			idSet.add(lt.getId(word)); 
		}
		Assert.assertEquals(words.length, idSet.size());
		Assert.assertEquals(0, idSet.first().intValue());
		Assert.assertEquals(words.length - 1, idSet.last().intValue());
		
		StringBuilder b = new StringBuilder();
		Node[] children = lt.getRoot().getChildren();
		for(Node n : children){
			char[] letters = n.getLetters();
			b.append(letters[0]);
		}
		Assert.assertEquals("おこさ", b.toString());
	}
}
