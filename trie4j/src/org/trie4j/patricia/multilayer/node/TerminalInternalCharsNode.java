package org.trie4j.patricia.multilayer.node;

import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class TerminalInternalCharsNode extends InternalCharsNode {
	public TerminalInternalCharsNode(char[] letters, Node[] children) {
		super(letters, children);
	}

	@Override
	public boolean isTerminated() {
		return true;
	}

	@Override
	protected Node newInternalCharsNode(char[] letters, Node[] children) {
		return new TerminalInternalCharsNode(letters, children);
	}

	@Override
	protected Node cloneWithLetters(char[] letters) {
		return new TerminalInternalCharsNode(letters, getChildren());
	}

	protected Node newLabelTrieNode(LabelNode ln, Node[] children){
		if(children != null){
			return new TerminalInternalLabelTrieNode(ln, children);
		} else{
			return new TerminalLabelTrieNode(ln);
		}
	}
}
