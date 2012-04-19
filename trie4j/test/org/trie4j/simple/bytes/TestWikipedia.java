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
package org.trie4j.simple.bytes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import org.trie4j.patricia.simple.bytes.Node;
import org.trie4j.patricia.simple.bytes.PatriciaTrie;
import org.trie4j.patricia.simple.bytes.TrieVisitor;
import org.trie4j.util.CharsetUtil;
import org.trie4j.util.StringUtil;

public class TestWikipedia {
	private static final int maxCount = 2000000;

	public static void main(String[] args) throws Exception{
		System.out.println("--- recursive patricia trie ---");
		PatriciaTrie trie = new org.trie4j.patricia.simple.bytes.PatriciaTrie();
		int c = 0;
		// You can download archive from http://dumps.wikimedia.org/jawiki/latest/
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream("jawiki-20120220-all-titles-in-ns0.gz"))
				, CharsetUtil.newUTF8Decoder()));
		String word = null;
		System.gc();
		Thread.sleep(1000);
		System.out.println(Runtime.getRuntime().freeMemory() + " bytes free.");

		long sum = 0;
		long lap = System.currentTimeMillis();
		int charCount = 0;
		while((word = r.readLine()) != null){
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
		System.gc();
		Thread.sleep(1000);
		System.out.println(Runtime.getRuntime().freeMemory() + " bytes free.");

		investigate(trie, charCount);
/*
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

	private static void dump(PatriciaTrie trie){
		System.out.println("--dump--");
		trie.visit(new TrieVisitor() {
			@Override
			public void accept(Node node, int nest) {
				for(int i = 0; i < nest; i++){
					System.out.print(" ");
				}
				byte[] letters = node.getLetters();
				if(letters != null && letters.length > 0){
					System.out.print(StringUtil.fromUTF8(letters));
				}
				if(node.isTerminated()){
					System.out.print("*");
				}
				System.out.println();
			}
		});
	}

	private static void investigate(PatriciaTrie trie, int charCount)
	throws Exception{
		System.out.println("-- count elements.");
		final AtomicInteger count = new AtomicInteger();
		trie.visit(new TrieVisitor() {
			public void accept(Node node, int nest) {
				if(node.isTerminated()) count.incrementAndGet();
			}
		});
		System.out.println(count.intValue() + " elements.");
//*
		System.out.println("-- list elements.");

		final AtomicInteger n = new AtomicInteger();
		final AtomicInteger l = new AtomicInteger();
		final AtomicInteger ln = new AtomicInteger();
		final AtomicInteger chars = new AtomicInteger();
		trie.visit(new TrieVisitor() {
				public void accept(Node node, int nest) {
					if(node.isTerminated()){
						l.incrementAndGet();
					} else{
						n.incrementAndGet();
					}
					chars.addAndGet(node.getLetters().length);
				}
			});
		System.out.println("node: " + n.intValue());
		System.out.println("leaf: " + l.intValue());
		System.out.println("label node: " + ln.intValue());
		System.out.println("total char count: " + charCount);
		System.out.println("total char count in trie: " + chars.intValue());

		System.out.println("verifying trie...");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream("jawiki-20120220-all-titles-in-ns0.gz"))
				, CharsetUtil.newUTF8Decoder()));
		long lap = System.currentTimeMillis();
		int c = 0;
		int sum = 0;
		String word = null;
		while((word = r.readLine()) != null){
			if(c == maxCount) break;
			long d = System.currentTimeMillis();
			boolean found = trie.contains(word);
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
		
		System.out.println(trie.getRoot().getChildren().length + "children in root");

		final PatriciaTrie t = trie;
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
