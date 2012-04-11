package org.trie4j;

import org.trie4j.patricia.multilayer.MultilayerPatriciaTrie;

public class Test {
	public static void main(String[] args) throws Exception{
		System.out.println("--- patricia trie ---");
		go(new MultilayerPatriciaTrie());
//		System.out.println("--- hash trie ---");
//		go(new HashSetTrie());
	}

	private static void go(Trie trie) throws Exception{
		String[] words = {
				"apple", "appear", "a", "orange"
				, "applejuice", "appletea", "appleshower"
				, "orangejuice"
				};
		trie.insert("");
		for(String w : words){
			System.out.println("insert \"" + w + "\"");
			trie.insert(w);
			System.out.println("--dump--");
			trie.visit(new TrieVisitor() {
				@Override
				public void accept(Node node, int nest) {
					for(int i = 0; i < nest; i++){
						System.out.print(" ");
					}
					char[] letters = node.getLetters();
					if(letters == null || letters.length == 0){
						System.out.print("<empty>");
					} else{
						System.out.print(letters);
					}
					if(node.isTerminated()){
						System.out.println("*");
					} else{
						System.out.println("");
					}
				}
			});
		}
		System.out.println(trie.contains(""));

		System.out.println("--test contains--");
		for(String w : words){
			System.out.print(w + ": ");
			System.out.println(trie.contains(w));
		}
		System.out.println("--test not contains--");
		for(String w : new String[]{"banana", "app", "applebeer", "applejuice2"}){
			System.out.println(w + ": " + trie.contains(w));
		}
		System.out.println("-- test common prefix search --");
		System.out.println("query: applejuicebar");
//		for(String w : trie.commonPrefixSearch("applejuicebar")){
//			System.out.println(w);
//		}

		if(trie instanceof MultilayerPatriciaTrie){
			System.out.println("--pack--");
			((MultilayerPatriciaTrie)trie).pack();
			((MultilayerPatriciaTrie)trie).morePack();
			System.out.println("--dump--");
			trie.visit(new TrieVisitor() {
				@Override
				public void accept(Node node, int nest) {
					for(int i = 0; i < nest; i++){
						System.out.print(" ");
					}
					char[] letters = node.getLetters();
					if(letters == null || letters.length == 0){
						System.out.print("<empty>");
					} else{
						System.out.print(letters);
					}
					if(node.isTerminated()){
						System.out.println("*");
					} else{
						System.out.println("");
					}
				}
			});
			System.out.println("--test contains--");
			for(String w : words){
				System.out.print(w + ": ");
				System.out.println(trie.contains(w));
			}
			System.out.println("--test not contains--");
			for(String w : new String[]{"banana", "app", "applebeer", "applejuice2"}){
				System.out.println(w + ": " + trie.contains(w));
			}
		}
	}
}
