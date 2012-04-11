package org.trie4j.patricia.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.trie4j.Trie;
import org.trie4j.TrieVisitor;

public class PatriciaTrie implements Trie{
	@Override
	public boolean contains(String word) {
		return root.contains(word.toCharArray(), 0);
	}

	@Override
	public Iterable<String> commonPrefixSearch(final String query) {
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
			if(node.isTerminated()){
				ret.add(new String(queryChars, 0 , cur + letters.length));
			}
			cur += letters.length;
			if(queryChars.length == cur) return ret;
			node = node.getChild(queryChars[cur]);
		}
		return ret;
	}

	private static void enumLetters(org.trie4j.Node node, String prefix, List<String> letters){
		org.trie4j.Node[] children = node.getChildren();
		if(children == null) return;
		for(org.trie4j.Node child : children){
			String text = prefix + new String(child.getLetters());
			if(child.isTerminated()) letters.add(text);
			enumLetters(child, text, letters);
		}
	}

	public Iterable<String> predictiveSearch(String prefix) {
		char[] queryChars = prefix.toCharArray();
		int cur = 0;
		Node node = root;
		while(node != null){
			char[] letters = node.getLetters();
			if(letters.length > (queryChars.length - cur)) return Collections.emptyList();
			for(int i = 0; i < letters.length; i++){
				if(letters[i] != queryChars[cur + i]){
					return Collections.emptyList();
				}
			}
			cur += letters.length;
			if(queryChars.length == cur){
				List<String> ret = new ArrayList<String>();
				if(node.isTerminated()) ret.add(prefix);
				enumLetters(node, prefix, ret);
				return ret;
			}
			node = node.getChild(queryChars[cur]);
		}
		return Collections.emptyList();
	}

	public void insert(String text){
		char[] letters = text.toCharArray();
		if(root == null){
			root = new Node(letters, true);
			return;
		}
		root.insertChild(letters, 0);
	}
	public void visit(TrieVisitor visitor){
		root.visit(visitor, 0);
	}

	public Node getRoot(){
		return root;
	}

	private Node root;
}
