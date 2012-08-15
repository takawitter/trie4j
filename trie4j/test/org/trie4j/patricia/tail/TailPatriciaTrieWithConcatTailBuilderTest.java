package org.trie4j.patricia.tail;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.TrieTestSet;
import org.trie4j.patricia.tail.TailPatriciaTrie;
import org.trie4j.tail.ConcatTailBuilder;
import org.trie4j.test.WikipediaTitles;

public class TailPatriciaTrieWithConcatTailBuilderTest extends TrieTestSet {
	@Override
	protected Trie trieWithWords(String... words) {
		Trie trie = new TailPatriciaTrie(new ConcatTailBuilder());
		for(String w : words) trie.insert(w);
		return trie;
	}

	@Test
	public void test() throws Exception{
		Trie trie = new TailPatriciaTrie();
		int c = 0;
		for(String w : newWords()){
			if(w.length() == 1 && w.toCharArray()[0] >= '0' && w.toCharArray()[0] <= '9'){
				System.out.println(w + " inserted at " + c);
			}
			if(w.equals("\"文学少女\"シリーズ")){
				for(Node n : trie.getRoot().getChildren()){
					System.out.print(n.getLetters()[0]);
				}
				System.out.println("here");
			}
			trie.insert(w);
			Assert.assertTrue(String.format(
					"must contains %dth word \"%s\"."
					, c,  w), trie.contains(w));
			c++;
		}
	}
	private static Iterable<String> newWords() throws IOException{
		return new WikipediaTitles("jawiki-20120220-all-titles-in-ns0.gz");
	}
}
