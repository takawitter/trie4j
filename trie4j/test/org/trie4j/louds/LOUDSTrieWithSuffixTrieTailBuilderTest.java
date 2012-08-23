package org.trie4j.louds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.Node;
import org.trie4j.Trie;
import org.trie4j.TrieTestSet;
import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.tail.SuffixTrieTailBuilder;
import org.trie4j.test.LapTimer;
import org.trie4j.test.WikipediaTitles;

public class LOUDSTrieWithSuffixTrieTailBuilderTest extends TrieTestSet{
	@Override
	protected Trie trieWithWords(String... words) {
		Trie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		return new LOUDSTrie(trie, 65536, new SuffixTrieTailBuilder());
	}

	@Test
	public void test() throws Exception{
		String[] words = {"こんにちは", "さようなら", "おはよう", "おおきなかぶ", "おおやまざき"};
		Trie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		LOUDSTrie lt = new LOUDSTrie(trie);
		for(String w : words){
			Assert.assertTrue(w, lt.contains(w));
		}
		Assert.assertFalse(lt.contains("おやすみなさい"));

		StringBuilder b = new StringBuilder();
		Node[] children = lt.getRoot().getChildren();
		for(Node n : children){
			char[] letters = n.getLetters();
			b.append(letters[0]);
		}
		Assert.assertEquals("おこさ", b.toString());
	}

	@Test
	public void test_save_load() throws Exception{
		String[] words = {"こんにちは", "さようなら", "おはよう", "おおきなかぶ", "おおやまざき"};
		Trie trie = new PatriciaTrie();
		for(String w : words) trie.insert(w);
		LOUDSTrie lt = new LOUDSTrie(trie);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		lt.save(baos);
		lt = new LOUDSTrie();
		lt.load(new ByteArrayInputStream(baos.toByteArray()));
		for(String w : words){
			Assert.assertTrue(lt.contains(w));
		}
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
//		for(int i = 0; i < 10; i++){
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
//		}
		t.lap("verification done.");
/*
		System.out.println("select0 time: " + lt.getSelect0Time() / 1000000);
		System.out.println("next0 time: " + lt.getNext0Time() / 1000000);
		System.out.println("rank1 time: " + lt.getRank1Time() / 1000000);
/*
		System.out.println("---- common prefix search ----");
		for(String w : lt.commonPrefixSearch("東京国際フォーラム")){
			System.out.println(w);
		}
		for(String s : lt.commonPrefixSearch("大阪城ホール")){
			System.out.println(s);
		}
		System.out.println("---- predictive search ----");
		System.out.println("-- for 大阪城");
		for(String s : lt.predictiveSearch("大阪城")){
			System.out.println(s);
		}

		Thread.sleep(10000);
		lt.contains("hello");
*/
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
