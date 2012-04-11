package org.trie4j.patricia.multilayer.labeltrie;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;

public class LabelTrie{
	public LabelTrie() {
		root.addReferer(new LabelTrieNode(new LabelNode(null){
			@Override
			public void addReferer(LabelTrieNode l) {
			}
		}) {
			@Override
			public void setLettersNode(LabelNode lettersNode) {
				root = lettersNode;
			}
		});
	}

	public LabelNode getRoot(){
		return root;
	}

	public boolean contains(String word) {
		return root.contains(word.toCharArray(), 0);
	}

	public void insert(String word) {
		insert(word.toCharArray());
	}

	public LabelNode insert(char[] letters){
		return root.insertChild(0, letters, 0);
	}

	public void pargeChildren(){
		root.pargeChildren();
	}

	public void visit(TrieVisitor visitor){
		root.visit(visitor, 0);
	}

	private LabelNode root = new InternalLabelNode(LabelNode.emptyChars, null, null);
}
