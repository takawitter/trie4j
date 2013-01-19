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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.trie4j.AbstractWikipediaTest;
import org.trie4j.Algorithms;
import org.trie4j.Node;
import org.trie4j.NodeVisitor;
import org.trie4j.Trie;
import org.trie4j.tail.SuffixTrieTailBuilder;

public class LOUDSTrieWikipediaTest extends AbstractWikipediaTest{
	@Override
	protected Trie buildSecondTrie(Trie first) {
		bv.resetCounts();
		LOUDSTrie t = new LOUDSTrie(first, 65536, new SuffixTrieTailBuilder(), bv);
		return t;
	}

	@Override
	protected void afterVerification(Trie trie) throws Exception{
		LOUDSTrie t = (LOUDSTrie)trie;
		System.out.println("select0 time: " + (bv.getSelect0Time() / 1000000) + ", count: " + bv.getSelect0Count());
		System.out.println("next0 time: " + (bv.getNext0Time() / 1000000) + ", count: " + bv.getNext0Count());
		System.out.println("rank1 time: " + (bv.getRank1Time() / 1000000) + ", count: " + bv.getRank1Count());

		final Map<Integer, List<Integer>> childrenCounts = new TreeMap<Integer, List<Integer>>(
				new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						return o2 - o1;
					}
				});
		Algorithms.traverseByBreadth(t.getRoot(), new NodeVisitor() {
			@Override
			public boolean visit(Node node, int nest) {
				int n = node.getChildren().length;
				List<Integer> nodes = childrenCounts.get(n);
				if(nodes == null){
					nodes = new ArrayList<Integer>();
					childrenCounts.put(n, nodes);
				}
				nodes.add(c++);
				return c < 6189;
			}
			int c = 0;
		});
		for(Map.Entry<Integer, List<Integer>> entry : childrenCounts.entrySet()){
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}

		Thread.sleep(10000);
	}

	private MonitoredSuccinctBitVector bv = new MonitoredSuccinctBitVector(65536);
}
