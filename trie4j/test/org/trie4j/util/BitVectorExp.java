package org.trie4j.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.NodeVisitor;
import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.test.LapTimer;
import org.trie4j.test.WikipediaTitles;

public class BitVectorExp {
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
		t.lap("trie building done. $d words.", c);
		final SuccinctBitVector bv = new SuccinctBitVector(5000000);
		final AtomicInteger nodeCount = new AtomicInteger();
		trie.traverse(new NodeVisitor() {
			@Override
			public boolean visit(Node node, int nest) {
				Node[] children = node.getChildren();
				if(children != null){
					int n = node.getChildren().length;
					for(int i = 0 ;i  < n; i++){
						bv.append(true);
					}
				}
				bv.append(false);
				nodeCount.incrementAndGet();
				return true;
			}
		});
		trie = null;
		t.lap("done. %d nodes inserted. do rank and select", nodeCount.intValue());
		for(int i = 0; i < c; i += 10){
			int count = bv.rank(i, true);
			bv.select(count, true);
		}
		t.lap("done.");
		Thread.sleep(10000);
	}
}
