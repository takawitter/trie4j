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

import java.util.concurrent.atomic.AtomicInteger;

import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.patricia.multilayer.node.InternalCharsNode;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;
import org.trie4j.patricia.tail.TailPatriciaTrie;
import org.trie4j.tail.ConcatTailBuilder;
import org.trie4j.test.WikipediaTitles;

public class TestWikipedia {
	private static final int maxCount = 2000000;
	// You can download archive from http://dumps.wikimedia.org/jawiki/latest/
	private static final String wikipediaFile = "jawiki-20120220-all-titles-in-ns0.gz";
//	private static final String wikipediaFile = "enwiki-20120403-all-titles-in-ns0.gz";

	public static void main(String[] args) throws Exception{
		System.out.println("--- recursive patricia trie ---");
//		Trie trie = new org.trie4j.patricia.simple.PatriciaTrie();
//		Trie trie = new org.trie4j.patricia.multilayer.MultilayerPatriciaTrie();
		Trie trie = new org.trie4j.patricia.tail.TailPatriciaTrie(
				new ConcatTailBuilder());
		int c = 0;
		long sum = 0;
		long lap = System.currentTimeMillis();
		int charCount = 0;
		for(String word : new WikipediaTitles(wikipediaFile)){
			long d = System.currentTimeMillis();
			trie.insert(word);
			sum += System.currentTimeMillis() - d;
			charCount += word.length();
			if(c % 100000 == 0){
				d = System.currentTimeMillis() - lap;
				long free = Runtime.getRuntime().freeMemory();
				System.out.println(
						c + "," + free + "," + Runtime.getRuntime().maxMemory() + "," + d
						);
				lap = System.currentTimeMillis();
			}
			c++;
			if(c == maxCount) break;
		}
		System.out.println(c + "entries in ja wikipedia titles.");
		System.out.println("insert time: " + sum + " millis.");

		System.out.println("-- insert done.");
//		if(trie instanceof TailPatriciaTrie){
//			((TailPatriciaTrie) trie).pack();
//		}
		System.gc();
		Thread.sleep(1000);
		System.out.println(Runtime.getRuntime().freeMemory() + " bytes free.");

		final AtomicInteger cnt = new AtomicInteger();
		trie.traverse(new NodeVisitor() {
			@Override
			public boolean visit(Node node, int nest) {
				if(node instanceof InternalCharsNode){
					if(((InternalCharsNode)node).getChildren().length == 1){
						cnt.incrementAndGet();
					}
				}
				return true;
			}
		});
		System.out.println(cnt + " nodes have 1 child.");
		investigate(trie, charCount);
//*
//		dump(trie);
		System.out.println("-- pack");
		lap = System.currentTimeMillis();
		if(trie instanceof MultilayerPatriciaTrie){
			MultilayerPatriciaTrie mt = (MultilayerPatriciaTrie)trie;
			mt.pack();
			System.out.println("-- pack done in " + (System.currentTimeMillis() - lap) + " millis.");
	//		dump(trie);
			System.gc();
			Thread.sleep(1000);
			System.out.println(Runtime.getRuntime().freeMemory() + " bytes free.");
			investigate(mt, charCount);
		}
//*/
	}

	private static void investigate(Trie trie, int charCount)
	throws Exception{
		System.out.println("-- dump root children.");
		for(Node n : trie.getRoot().getChildren()){
			System.out.print(n.getLetters()[0]);
		}
		System.out.println();
		System.out.println("-- count elements.");
		final AtomicInteger count = new AtomicInteger();
		trie.traverse(new NodeVisitor() {
			public boolean visit(Node node, int nest) {
				if(node.isTerminate()) count.incrementAndGet();
				return true;
			}
		});
		System.out.println(count.intValue() + " elements.");
//*
		System.out.println("-- list elements.");

		final AtomicInteger n = new AtomicInteger();
		final AtomicInteger l = new AtomicInteger();
		final AtomicInteger ln = new AtomicInteger();
		final AtomicInteger chars = new AtomicInteger();
		trie.traverse(new NodeVisitor() {
				public boolean visit(Node node, int nest) {
					if(node.isTerminate()){
						l.incrementAndGet();
					} else{
						n.incrementAndGet();
					}
					if(!(node instanceof LabelTrieNode)){
						chars.addAndGet(node.getLetters().length);
					}
					return true;
				}
			});
		if(trie instanceof MultilayerPatriciaTrie){
			MultilayerPatriciaTrie mt = (MultilayerPatriciaTrie)trie;
			if(mt.getLabelTrie() != null){
				mt.getLabelTrie().visit(new NodeVisitor() {
					public boolean visit(Node node, int nest) {
						ln.incrementAndGet();
						chars.addAndGet(node.getLetters().length);
						return true;
					}
				});
			}
		}
		System.out.println("node: " + n.intValue());
		System.out.println("leaf: " + l.intValue());
		System.out.println("label node: " + ln.intValue());
		System.out.println("total char count: " + charCount);
		System.out.println("total char count in trie: " + chars.intValue());

		System.out.println("verifying trie...");
		long lap = System.currentTimeMillis();
		int c = 0;
		int sum = 0;
		for(String word : new WikipediaTitles(wikipediaFile)){
			if(c == maxCount) break;
			long d = System.currentTimeMillis();
			boolean found = Algorithms.contains(trie.getRoot(), word);//trie.contains(word);
			sum += System.currentTimeMillis() - d;
			if(!found){
				System.out.println("trie not contains [" + word + "]");
				break;
			}
			if(c % 100000 == 0){
				System.out.println(c + " elements done.");
			}
			c++;
		}
		System.out.println("done in " + (System.currentTimeMillis() - lap) + " millis.");
		System.out.println("contains time: " + sum + " millis.");
		
//		System.out.println(trie.getRoot().getChildren().length + "children in root");
		if(trie instanceof TailPatriciaTrie){
//			((TailPatriciaTrie) trie).pack();
			System.out.println("tail length: " + ((TailPatriciaTrie) trie).getTailBuilder().getTails().length());
		}
		if(trie instanceof MultilayerPatriciaTrie){
			((MultilayerPatriciaTrie)trie).morePack();
		}
		final Trie t = trie;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100000);
					t.contains("hello");
				} catch (InterruptedException e) {
				}
			}
		}).start();
//*/
	}
}
