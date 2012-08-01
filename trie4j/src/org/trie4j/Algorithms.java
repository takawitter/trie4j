package org.trie4j;

import org.trie4j.Node;

public class Algorithms {
	public static boolean contains(Node root, String text){
		if(text.length() == 0){
			return root.getLetters().length == 0 && root.isTerminate();
		}
		int i = 0;
		Node node = root;
		while(node != null){
			char[] letters = node.getLetters();
			if(letters.length > 0){
				for(char c : letters){
					if(c != text.charAt(i++)) return false;
				}
				if(i == text.length()){
					return node.isTerminate();
				}
			}
			node = node.getChild(text.charAt(i));
		}
		return false;
	}
}
