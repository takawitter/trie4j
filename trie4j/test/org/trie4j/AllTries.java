package org.trie4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.trie4j.doublearray.DoubleArray;
import org.trie4j.doublearray.TailDoubleArray;
import org.trie4j.louds.LOUDSTrie;
import org.trie4j.louds.LOUDSTriePP;
import org.trie4j.louds.LOUDSTriePP2;
import org.trie4j.louds.NoTailLOUDSTrie;
import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.patricia.tail.TailPatriciaTrie;
import org.trie4j.tail.ConcatTailBuilder;
import org.trie4j.tail.SuffixTrieTailBuilder;
import org.trie4j.test.LapTimer;
import org.trie4j.test.WikipediaTitles;
import org.trie4j.util.Pair;

public class AllTries {
	private static Iterable<String> newWords() throws IOException{
		return new WikipediaTitles("data/jawiki-20120220-all-titles-in-ns0.gz");
	}

	private static Object holder;
	private static abstract class Process{
		public Process(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
		private String name;
		public abstract Pair<Long, Long> run() throws IOException;
	}
	private static class SetProcess extends Process{
		public SetProcess(String name, Class<? extends Set> set){
			super(name);
			this.clazz = set;
		}
		public Pair<Long, Long> run() throws IOException{
			try{
				return runForSet(clazz.newInstance());
			} catch(Exception e){
				throw new IOException(e);
			}
		}
		protected Pair<Long, Long> runForSet(Set<String> set) throws IOException{
			long b = 0, c = 0;
			LapTimer lt = new LapTimer();
			for(String w : newWords()){ lt.lap(); set.add(w); b += lt.lap();}
			for(String w : newWords()){ lt.lap(); set.contains(w); c += lt.lap();}
			holder = set;
			return Pair.create(b / 1000000, c / 1000000);
		}
		private Class<? extends Set> clazz;
	}
	private static abstract class TrieProcess extends Process{
		public TrieProcess(String name){
			super(name);
		}
		protected Pair<Long, Long> runForTrie(Trie trie) throws IOException{
			long b = 0, c = 0;
			LapTimer lt = new LapTimer();
			for(String w : newWords()){ lt.lap(); trie.insert(w); b += lt.lap();}
			afterBuildTrie(trie);
			b += lt.lap();
			for(String w : newWords()){ lt.lap(); trie.contains(w); c += lt.lap();}
			holder = trie;
			return Pair.create(b / 1000000, c / 1000000);
		}
		protected void afterBuildTrie(Trie trie){}
	}
	private static abstract class TrieProcess2 extends Process{
		public TrieProcess2(String name){
			super(name);
		}
		protected abstract Trie buildFrom(Trie trie);
		@Override
		public Pair<Long, Long> run() throws IOException {
			PatriciaTrie first = new PatriciaTrie();
			for(String w : newWords()){ first.insert(w);}
			long b = 0, c = 0;
			LapTimer lt = new LapTimer();
			Trie trie = buildFrom(first);
			b += lt.lap();
			for(String w : newWords()){ lt.lap(); trie.contains(w); c += lt.lap();}
			holder = trie;
			return Pair.create(b / 1000000, c / 1000000);
		}
	}

	public static void main(String[] args) throws Exception{
		int n = 2;
		System.out.println("run each process " + n + " times.");
		Process[] procs = {
/*				new SetProcess("HashSet", HashSet.class),
				new SetProcess("TreeSet", TreeSet.class),
				new TrieProcess("PatriciaTrie"){
					public Pair<Long, Long> run() throws IOException {
						return runForTrie(new PatriciaTrie());
					}
				},
				new TrieProcess("TailPatriciaTrie(suffixTrieTail)"){
					public Pair<Long, Long> run() throws IOException {
						return runForTrie(new TailPatriciaTrie(new SuffixTrieTailBuilder()));
					}
				},
				new TrieProcess("TailPatriciaTrie(concatTail)"){
					public Pair<Long, Long> run() throws IOException {
						return runForTrie(new TailPatriciaTrie(new ConcatTailBuilder()));
					}
				},
/*				new TrieProcess("MultilayerPatriciaTrie(no pack)"){
					public Pair<Long, Long> run() throws IOException {
						return runForTrie(new MultilayerPatriciaTrie());
					}
				},
				new TrieProcess("MultilayerPatriciaTrie"){
					public Pair<Long, Long> run() throws IOException {
						return runForTrie(new MultilayerPatriciaTrie());
					}
					protected void afterBuildTrie(Trie trie) {
						((MultilayerPatriciaTrie)trie).pack();
					}
				},
				new TrieProcess2("DoubleArray"){
					protected Trie buildFrom(Trie trie){
						return new DoubleArray(trie, 65536);
					}
				},
				new TrieProcess2("TailDoubleArray(suffixTrieTail)"){
					protected Trie buildFrom(Trie trie){
						return new TailDoubleArray(trie, 65536, new SuffixTrieTailBuilder());
					}
				},
				new TrieProcess2("TailDoubleArray(concatTail)"){
					protected Trie buildFrom(Trie trie){
						return new TailDoubleArray(trie, 65536, new ConcatTailBuilder());
					}
				},
*/				new TrieProcess2("LOUDSTrie"){
					protected Trie buildFrom(Trie trie){
						return new NoTailLOUDSTrie(trie, 65536);
					}
				},
				new TrieProcess2("TailLOUDSTrie(suffixTrieTail)"){
					protected Trie buildFrom(Trie trie){
						return new LOUDSTrie(trie, 65536, new SuffixTrieTailBuilder());
					}
				},
				new TrieProcess2("TailLOUDSTrie(concatTail)"){
					protected Trie buildFrom(Trie trie){
						return new LOUDSTrie(trie, 65536, new ConcatTailBuilder());
					}
				},
				new TrieProcess2("TailLOUDSTriePP(concatTail)"){
					protected Trie buildFrom(Trie trie){
						return new LOUDSTriePP(trie, 65536, new ConcatTailBuilder());
					}
				},
				new TrieProcess2("TailLOUDSTriePP2(concatTail)"){
					protected Trie buildFrom(Trie trie){
						return new LOUDSTriePP2(trie, 65536, new ConcatTailBuilder());
					}
				},
		};
		for(Process p : procs){
			System.out.print(p.getName());
			p.run();
			System.gc();
			System.gc();
			long b = 0, c = 0;
			for(int i = 0; i < n; i++){
				Pair<Long, Long> r = p.run();
				b += r.getFirst();
				c += r.getSecond();
				System.gc();
				System.gc();
			}
			System.out.println(String.format(", %d, %d", b / n, c / n));
			Thread.sleep(5000);
			holder.hashCode();
			holder = null;
		}
//*
//*/
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
	}
}
