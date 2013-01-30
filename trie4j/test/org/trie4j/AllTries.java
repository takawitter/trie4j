package org.trie4j;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.trie4j.doublearray.DoubleArray;
import org.trie4j.doublearray.MapDoubleArray;
import org.trie4j.doublearray.TailDoubleArray;
import org.trie4j.louds.LOUDSPPTrie;
import org.trie4j.louds.LOUDSTrie;
import org.trie4j.patricia.simple.MapPatriciaTrie;
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
		@SuppressWarnings("rawtypes")
		public SetProcess(String name, Class<? extends Set> set){
			super(name);
			this.clazz = set;
		}
		@SuppressWarnings("unchecked")
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
			for(String w : newWords()){
				lt.lap();
				boolean r = set.contains(w);
				c += lt.lap();
				if(!r) throw new RuntimeException("verification failed for \"" + w + "\"");
			}
			holder = set;
			return Pair.create(b / 1000000, c / 1000000);
		}
		@SuppressWarnings("rawtypes")
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
			for(String w : newWords()){
				lt.lap();
				boolean r = trie.contains(w);
				c += lt.lap();
				if(!r) throw new RuntimeException("verification failed for \"" + w + "\"");
				}
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
			for(String w : newWords()){
				lt.lap();
				boolean r = trie.contains(w);
				c += lt.lap();
				if(!r) throw new RuntimeException("verification failed for \"" + w + "\"");
			}
			holder = trie;
			return Pair.create(b / 1000000, c / 1000000);
		}
	}
	private static abstract class MapTrieProcess2 extends Process{
		public MapTrieProcess2(String name){
			super(name);
		}
		protected abstract MapTrie<Integer> buildFrom(MapTrie<Integer> trie);
		@Override
		public Pair<Long, Long> run() throws IOException {
			MapPatriciaTrie<Integer> first = new MapPatriciaTrie<Integer>();
			int i = 0;
			for(String w : newWords()){ first.insert(w, i++);}
			long b = 0, c = 0;
			LapTimer lt = new LapTimer();
			MapTrie<Integer> trie = buildFrom(first);
			b += lt.lap();
			i = 0;
			for(String w : newWords()){
				lt.lap();
				Integer r = trie.get(w);
				c += lt.lap();
				if(r != i++) throw new RuntimeException("verification failed for \"" + w + "\"");
			}
			holder = trie;
			return Pair.create(b / 1000000, c / 1000000);
		}
	}
	private static Process[] procs = {
//*
			new SetProcess("HashSet", HashSet.class),
			new SetProcess("TreeSet", TreeSet.class),
			new TrieProcess("PatriciaTrie"){
				public Pair<Long, Long> run() throws IOException {
					return runForTrie(new PatriciaTrie());
				}
			},
			new TrieProcess("MapPatriciaTrie"){
				public Pair<Long, Long> run() throws IOException {
					return runForTrie(new MapPatriciaTrie<Object>());
				}
			},
			new TrieProcess("TailPatriciaTrie(suffixTrieTail)"){
				public Pair<Long, Long> run() throws IOException {
					return runForTrie(new TailPatriciaTrie(new SuffixTrieTailBuilder()));
				}
			},
			new TrieProcess("TailPatriciaTrie(suffixTrieTail,freezed)"){
				public Pair<Long, Long> run() throws IOException {
					return runForTrie(new TailPatriciaTrie(new SuffixTrieTailBuilder()));
				}
				@Override
				protected void afterBuildTrie(Trie trie) {
					super.afterBuildTrie(trie);
					trie.freeze();
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
//*/
			new TrieProcess2("DoubleArray"){
				protected Trie buildFrom(Trie trie){
					return new DoubleArray(trie);
				}
			},
			new MapTrieProcess2("MapDoubleArray"){
				protected MapTrie<Integer> buildFrom(MapTrie<Integer> trie){
					return new MapDoubleArray<Integer>(trie);
				}
			},
			new TrieProcess2("TailDoubleArray(suffixTrieTail)"){
				protected Trie buildFrom(Trie trie){
					return new TailDoubleArray(trie, new SuffixTrieTailBuilder());
				}
			},
			new TrieProcess2("TailDoubleArray(concatTail)"){
				protected Trie buildFrom(Trie trie){
					return new TailDoubleArray(trie, new ConcatTailBuilder());
				}
			},
/*				new TrieProcess2("LOUDSTrie"){
				protected Trie buildFrom(Trie trie){
					return new NoTailLOUDSTrie(trie, 65536);
				}
			},
*/				new TrieProcess2("TailLOUDSTrie(suffixTrieTail)"){
				protected Trie buildFrom(Trie trie){
					return new LOUDSTrie(trie, new SuffixTrieTailBuilder());
				}
			},
			new TrieProcess2("TailLOUDSTrie(concatTail)"){
				protected Trie buildFrom(Trie trie){
					return new LOUDSTrie(trie, new ConcatTailBuilder());
				}
			},
			new TrieProcess2("TailLOUDSPPTrie(suffixTrieTail)"){
				protected Trie buildFrom(Trie trie){
					return new LOUDSPPTrie(trie, new SuffixTrieTailBuilder());
				}
			},
			new TrieProcess2("TailLOUDSTriePP2(concatTail)"){
				protected Trie buildFrom(Trie trie){
					return new LOUDSPPTrie(trie, new ConcatTailBuilder());
				}
			},
		};

	public static void main(String[] args) throws Exception{
		MemoryMXBean mb = ManagementFactory.getMemoryMXBean();
		int n = 3;
		System.out.println("run each process " + n + " times.");
		System.out.println("warming up... running all trie once");
		for(Process p : procs){
			System.out.print(p.getName() + " ");
			p.run();
			System.gc();
		}
		System.out.println("warming up... done");
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
				mb.gc();
				mb.gc();
			}
			System.out.println(String.format(", %d, %d, %d", b / n, c / n, mb.getHeapMemoryUsage().getUsed()));
//			Thread.sleep(5000);
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
