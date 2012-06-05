package org.trie4j.louds;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.test.WikipediaTitles;
import org.trie4j.util.LapTimer;

public class LOUDSTrieTest {
	@Test
	public void test() throws Exception{
		Trie trie = new PatriciaTrie();
		trie.insert("こんにちは");
		trie.insert("さようなら");
		trie.insert("おはよう");
		trie.insert("おおきなかぶ");
		trie.insert("おおやまざき");
		LOUDSTrie lt = new LOUDSTrie(trie);
		Assert.assertTrue(lt.contains("こんにちは"));
		Assert.assertTrue(lt.contains("さようなら"));
		Assert.assertTrue(lt.contains("おはよう"));
		Assert.assertTrue(lt.contains("おおきなかぶ"));
		Assert.assertTrue(lt.contains("おおやまざき"));
		Assert.assertFalse(lt.contains("おやすみなさい"));

		StringBuilder b = new StringBuilder();
		Node[] children = lt.getRoot().getChildren();
		for(Node n : children){
			char[] letters = n.getLetters();
			b.append(letters[0]);
		}
		Assert.assertEquals("おこさ", b.toString());
	}

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
		printHeads(trie.getRoot());
		printHeads(lt.getRoot());
		print(trie.getRoot());
		print(lt.getRoot());
		trie = null;
		lt.trimToSize();
		t.lap("verifying the LOUDS trie.");
		c = 0;
		for(String word : new WikipediaTitles(
				"jawiki-20120220-all-titles-in-ns0.gz"
				//"enwiki-20120403-all-titles-in-ns0.gz"
				)){
			if(!lt.contains(word)){
				t.lap("trie not contains %dth word: %s", c + 1, word);
				break;
			}
			c++;
			if(c == maxCount) break;
		}
		t.lap("verification done.");

		Thread.sleep(10000);
		lt.contains("hello");
	}

	private static void printHeads(Node node){
		Node[] children = node.getChildren();
		for(Node n : children){
			char[] letters = n.getLetters();
			if(letters.length == 0) continue;
			System.out.print(letters[0]);
		}
		System.out.println();
	}

	private static void print(Node node){
		StringBuilder b = new StringBuilder();
		Node[] children = null;
		b.append(node.getLetters());
		while((children = node.getChildren()) != null){
			if(children.length > 0){
				b.append(children[0].getLetters());
				node = children[0];
			} else{
				break;
			}
		}
		System.out.println(b);
	}
}
