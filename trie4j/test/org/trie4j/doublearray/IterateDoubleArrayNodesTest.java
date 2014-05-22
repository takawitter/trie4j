package org.trie4j.doublearray;

import org.junit.Test;
import org.trie4j.Algorithms;
import org.trie4j.Node;
import org.trie4j.NodeVisitor;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.test.WikipediaTitles;

public class IterateDoubleArrayNodesTest {
	@Test
	public void test() throws Exception{
		Algorithms.traverseByBreadth(
				new DoubleArray(new WikipediaTitles().insertTo(new PatriciaTrie())).getRoot(),
				new NodeVisitor() {
					@Override
					public boolean visit(Node node, int nest) {
						return true;
					}
				});
	}
}
