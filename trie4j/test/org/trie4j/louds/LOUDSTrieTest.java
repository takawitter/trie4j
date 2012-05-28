package org.trie4j.louds;

import org.trie4j.Trie;
import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.test.WikipediaTitles;
import org.trie4j.util.LapTimer;

public class LOUDSTrieTest {
	private static final int maxCount = 2000000;

	public static void main(String[] args) throws Exception{
		Trie trie = new MultilayerPatriciaTrie();
		int c = 0;
		// You can download archive from http://dumps.wikimedia.org/jawiki/latest/
		LapTimer t = new LapTimer();
		for(String word : new WikipediaTitles(
				"jawiki-20120220-all-titles-in-ns0.gz"
				//"enwiki-20120403-all-titles-in-ns0.gz"
				)){
			trie.insert(word);
			c++;
			if(c == maxCount) break;
		}
		t.lap("building trie done. %d words.", c);
		LOUDSTrie lt = new LOUDSTrie(trie);
		t.lap("building LOUDS trie done.");
		trie = null;
		lt.trimToSize();
		Thread.sleep(10000);
	}
}
