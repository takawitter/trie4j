package org.trie4j.patricia.tail;

import junit.framework.Assert;

import org.junit.Test;
import org.trie4j.Algorithms;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.NodeVisitor;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;
import org.trie4j.patricia.tail.TailPatriciaTrie;
import org.trie4j.test.WikipediaTitles;

public class TailPatriciaTrieWikipediaTest {
	@Test
	public void test_simple() throws Exception{
		Trie trie = new TailPatriciaTrie();
		String[] lines = {
				"\"74ers\"_LIVE_IN_OSAKA-JO_HALL_2003",
				"\"B\"ORDERLESS",
		};
		for(String w : lines){
			trie.insert(w);
		}
		for(String w : lines){
			Assert.assertTrue(
					"trie must conains " + w
					, trie.contains(w));
		}
	}

	@Test
	public void test() throws Exception{
		Trie trie = new TailPatriciaTrie();
		insert(trie, new WikipediaTitles("jawiki-20120220-all-titles-in-ns0.gz"), 2000000);
//		dump(trie);
		check(trie, new WikipediaTitles("jawiki-20120220-all-titles-in-ns0.gz"), 2000000);
	}

	private void insert(Trie trie, Iterable<String> source, int maxCount){
		int c = 0;
		for(String w : source){
			trie.insert(w);
			if(!trie.contains(w)){
				dump(trie);
				Assert.fail(
					"failed to insert " + c + "th word: " + w
					);
			}
			c++;
			if(c == maxCount) return;
		}
	}

	private void check(Trie trie, Iterable<String> source, int maxCount){
		int c = 0;
		for(String w : source){
			Assert.assertTrue(
					String.format("trie must contains %dth word: [%s].", c, w)
					, trie.contains(w)
					);
			c++;
			if(c == maxCount) return;
		}
	}

	private static void dump(Trie trie){
		System.out.println("--dump--");
		Algorithms.traverseByDepth(trie.getRoot(), new NodeVisitor() {
			@Override
			public boolean visit(Node node, int nest) {
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
				if(node.isTerminate()){
					System.out.print("*");
				}
				System.out.println();
				return true;
			}
		});
	}
}
