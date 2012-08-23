package org.trie4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.trie4j.doublearray.DoubleArray;
import org.trie4j.doublearray.OptimizedTailDoubleArray;
import org.trie4j.doublearray.TailDoubleArray;
import org.trie4j.louds.LOUDSTrie;
import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.patricia.tail.TailPatriciaTrie;
import org.trie4j.tail.ConcatTailBuilder;
import org.trie4j.tail.SuffixTrieTailBuilder;
import org.trie4j.test.LapTimer;
import org.trie4j.test.WikipediaTitles;

public class AllTries {
	private static Iterable<String> newWords() throws IOException{
		return new WikipediaTitles("data/jawiki-20120220-all-titles-in-ns0.gz");
	}

	public static void main(String[] args) throws Exception{
		LapTimer lt = new LapTimer();
//*
		for(int i = 0; i < 2; i++){
			// hash set
			Set<String> s = new HashSet<String>();
			lt.lap();
			long b = 0, c = 0;
			for(String w : newWords()){ lt.lap(); s.add(w); b += lt.lap();}
			for(String w : newWords()){ lt.lap(); s.contains(w); c += lt.lap();}
			log("HashSet, %d, %d", b, c);
			System.gc();
			System.gc();
		}
		for(int i = 0; i < 2; i++){
			// tree set
			Set<String> s = new HashSet<String>();
			lt.lap();
			long b = 0, c = 0;
			for(String w : newWords()){ lt.lap(); s.add(w); b += lt.lap();}
			for(String w : newWords()){ lt.lap(); s.contains(w); c += lt.lap();}
			log("TreeSet, %d, %d", b, c);
			System.gc();
			System.gc();
		}

		for(int i = 0; i < 2; i++){	
			// multilayer patriciatrie
			Trie t = new MultilayerPatriciaTrie();
			long b = 0;
			for(String word : newWords()){ lt.lap(); t.insert(word); b += lt.lap();}
			((MultilayerPatriciaTrie)t).pack();
			((MultilayerPatriciaTrie)t).morePack();
			b += lt.lap();
			long c = lapContains(t);
			log("MultilayerPatriciaTrie, %d, %d", b, c);
			System.gc();
			System.gc();
		}
//*
		{	// tail patriciatrie
			Trie t = new TailPatriciaTrie(new SuffixTrieTailBuilder());
			long b = 0;
			for(String word : newWords()){ lt.lap(); t.insert(word); b += lt.lap();}
			long c = lapContains(t);
			log("TailPatriciaTrie(suffixTrieTail), %d, %d", b, c);
		}
		System.gc();
		System.gc();

		{	// tail patriciatrie
			Trie t = null;
			t = new TailPatriciaTrie(new ConcatTailBuilder());
			long b = 0;
			for(String word : newWords()){ lt.lap(); t.insert(word); b += lt.lap();}
			long c = lapContains(t);
			log("TailPatriciaTrie(concatTail), %d, %d", b, c);
		}
		System.gc();
		System.gc();
//*/
		Trie pattrie = null;
		for(int i = 0; i < 2; i++){
			// patriciatrie
			pattrie = new PatriciaTrie();
			long b = 0;
			for(String word : newWords()){ lt.lap(); pattrie.insert(word); b += lt.lap();}
			long c = lapContains(pattrie);
			log("PatriciaTrie, %d, %d", b, c);
			System.gc();
			System.gc();
		}
//*
		for(int i = 0; i < 2; i++){
			// double array
			lt.lap();
			Trie t = new DoubleArray(pattrie, 65536);
			long b = lt.lap();
			long c = lapContains(t);
			log("DoubleArray, %d, %d", b, c);
			System.gc();
			System.gc();
		}

		for(int i = 0; i < 2; i++){
			// tail double array
			lt.lap();
			Trie t = new TailDoubleArray(pattrie, 65536, new SuffixTrieTailBuilder());
			long b = lt.lap();
			long c = lapContains(t);
			log("TailDoubleArray(suffixTrieTail), %d, %d", b, c);
			System.gc();
			System.gc();
		}

		for(int i = 0; i < 2; i++){
			// tail double array
			lt.lap();
			Trie t = new TailDoubleArray(pattrie, 65536, new ConcatTailBuilder());
			long b = lt.lap();
			long c = lapContains(t);
			log("TailDoubleArray(concatTail), %d, %d", b, c);
			System.gc();
			System.gc();
		}
/*
		{	// optimized double array
			lt.lap();
			OptimizedTailDoubleArray da = new OptimizedTailDoubleArray(pattrie, 65536, new SuffixTrieTailBuilder());
			long b = lt.lap();
			long c = lapContains(da);
			log("OptimizedTailDoubleArray(suffixTrieTail), %d, %d", b, c);
		}
		System.gc();
		System.gc();

		{	// optimized double array with simple tb
			lt.lap();
			OptimizedTailDoubleArray da = new OptimizedTailDoubleArray(pattrie, 655536, new ConcatTailBuilder());
			long b = lt.lap();
			long c = lapContains(da);
			log("OptimizedTailDoubleArray(concatTail), %d, %d", b, c);
		}
		System.gc();
		System.gc();
//*/
		for(int i = 0; i < 2; i++){
			// louds trie with suffixtrie tb
			lt.lap();
			LOUDSTrie louds = new LOUDSTrie(pattrie, 65536, new SuffixTrieTailBuilder());
			long b = lt.lap();
			long c = lapContains(louds);
			log("LOUDSTrie(suffixTrieTail), %d, %d", b, c);
			System.gc();
			System.gc();
		}

		for(int i = 0; i < 2; i++){
			// louds trie with simple tb
			lt.lap();
			LOUDSTrie louds = new LOUDSTrie(pattrie, 65535, new ConcatTailBuilder());
			long b = lt.lap();
			long c = lapContains(louds);
			log("LOUDSTrie(concatTail), %d, %d", b, c);
		}
//*/
	}

	private static void log(String format, long b, long c){
		System.out.println(String.format(format, b / 1000000, c / 1000000));
	}

	private static long lapContains(Trie trie) throws IOException{
		Iterable<String> words = newWords();
		long ret = 0;
		LapTimer lt = new LapTimer();
		for(String word : words){
			lt.lap();
			trie.contains(word);
			ret += lt.lap();
		}
		return ret;
	}
}
