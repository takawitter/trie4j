package org.trie4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;
import org.trie4j.patricia.multilayer.node.InternalCharsNode;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;
import org.trie4j.util.CharsetUtil;

public class TestWikipedia {
	private static final int maxCount = 2000000;

	public static void main(String[] args) throws Exception{
		System.out.println("--- recursive patricia trie ---");
//		Trie trie = new org.trie4j.patricia.simple.PatriciaTrie();
		Trie trie = new org.trie4j.patricia.multilayer.MultilayerPatriciaTrie();
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

		final AtomicInteger cnt = new AtomicInteger();
		trie.visit(new TrieVisitor() {
			@Override
			public void accept(Node node, int nest) {
				if(node instanceof InternalCharsNode){
					if(((InternalCharsNode)node).getChildren().length == 1){
						cnt.incrementAndGet();
					}
				}
			}
		});
		System.out.println(cnt + " nodes have 1 child.");
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

	private static void dump(Trie trie){
		System.out.println("--dump--");
		trie.visit(new TrieVisitor() {
			@Override
			public void accept(Node node, int nest) {
				for(int i = 0; i < nest; i++){
					System.out.print(" ");
				}
				char[] letters = node.getLetters();
				if(letters != null && letters.length > 0){
					System.out.print(letters);
				} else if(node instanceof LabelTrieNode){
					LabelNode ln = ((LabelTrieNode)node).getLettersNode();
					if(ln != null){
						do{
							System.out.print("#");
							char[] l = ln.getLetters();
							for(int i = 0; i < l.length; i++){
								System.out.print(l[l.length - i - 1]);
							}
							ln = ln.getParent();
						} while(ln != null);
					} else{
						System.out.print("<empty>");
					}
				}
				if(node.isTerminated()){
					System.out.print("*");
				}
				System.out.println();
			}
		});
	}

	private static void investigate(Trie trie, int charCount)
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
					if(!(node instanceof LabelTrieNode)){
						chars.addAndGet(node.getLetters().length);
					}
				}
			});
		if(trie instanceof MultilayerPatriciaTrie){
			MultilayerPatriciaTrie mt = (MultilayerPatriciaTrie)trie;
			if(mt.getLabelTrie() != null){
				mt.getLabelTrie().visit(new TrieVisitor() {
					public void accept(Node node, int nest) {
						ln.incrementAndGet();
						chars.addAndGet(node.getLetters().length);
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
