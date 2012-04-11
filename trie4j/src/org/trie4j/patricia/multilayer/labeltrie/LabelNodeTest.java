package org.trie4j.patricia.multilayer.labeltrie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.trie4j.Node;
import org.trie4j.TrieVisitor;
import org.trie4j.util.CharsUtil;

public class LabelNodeTest {
	public static void main(String[] args) throws Exception{
		String[] words = {
/*
				"apple", "appear", "a", "orange"
				, "applejuice", "appletea", "appleshower"
				, "orangejuice"
/*/
				"page_title",
				"!",
				"!!",
				"!!!",
				"!!!Fuck_You!!!",
				"!?",
				"!LOUD!",
				"!SHOUT!",
				"!_-attention-",
				"!wagero!",
				"\"",
				"\"74ers\"_LIVE_IN_OSAKA-JO_HALL_2003",
//*/

				};
		Map<String, LabelNode> nodes = new HashMap<String, LabelNode>();
		LabelNode root = new LabelNode(new char[]{});
		for(String w : words){
			System.out.println("--insert [" + w + "]--");
			nodes.put(w, root.insertChild(0, CharsUtil.revert(w.toCharArray()), 0));
			System.out.println("--dump--");
			root.visit(new TrieVisitor() {
				@Override
				public void accept(Node node, int nest) {
					for(int i = 0; i < nest; i++){
						System.out.print(" ");
					}
					if(node.getLetters().length > 0){
						System.out.print(node.getLetters());
					} else{
						System.out.print("<empty>");
					}
					System.out.println();
				}
			}, 0);
		}
		for(String w : words){
			System.out.print(w + ": ");
			LabelNode node = nodes.get(w);
			while(node != null){
				System.out.print(CharsUtil.revert(node.getLetters()));
				System.out.print(" ");
				node = node.getParent();
			}
			System.out.println();
		}
		
		char[][] charss = {
				{'!', '!', (char)-1},
				{'!', (char)-1},
				{'p', 'a', 'g', 'e', '_', 't', 'i', 't', 'l', 'e', (char)-1}
		};
		for(char[] c : charss){
			System.out.println("--insert [" + new String(c) + "]--");
			LabelNode n = root.insertChild(0, CharsUtil.revert(c), 0);
			dump(root);
			System.out.println("--containsBottomup: " + n.containsBottomup(Arrays.copyOf(
					c, c.length - 1), 0));
		}
	}

	private static void dump(LabelNode root){
		System.out.println("--dump--");
		root.visit(new TrieVisitor() {
			@Override
			public void accept(Node node, int nest) {
				for(int i = 0; i < nest; i++){
					System.out.print(" ");
				}
				if(node.getLetters().length > 0){
					System.out.print(node.getLetters());
				} else{
					System.out.print("<empty>");
				}
				System.out.println();
			}
		}, 0);
	}
}
