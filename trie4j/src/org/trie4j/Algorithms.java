package org.trie4j;

import java.util.ArrayList;
import java.util.List;

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

	public Iterable<String> commonPrefixSearch(Node root, String query) {
		List<String> ret = new ArrayList<String>();
		char[] queryChars = query.toCharArray();
		int cur = 0;
		Node node = root;
		while(node != null){
			char[] letters = node.getLetters();
			if(letters.length > (queryChars.length - cur)) return ret;
			for(int i = 0; i < letters.length; i++){
				if(letters[i] != queryChars[cur + i]) return ret;
			}
			if(node.isTerminate()){
				ret.add(new String(queryChars, 0 , cur + letters.length));
			}
			cur += letters.length;
			if(queryChars.length == cur) return ret;
			node = node.getChild(queryChars[cur]);
		}
		return ret;
	}
}
