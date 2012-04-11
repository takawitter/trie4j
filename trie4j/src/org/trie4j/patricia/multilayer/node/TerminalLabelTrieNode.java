package org.trie4j.patricia.multilayer.node;

import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class TerminalLabelTrieNode extends LabelTrieNode{
	public TerminalLabelTrieNode(LabelNode lettersNode) {
		super(lettersNode);
	}

	@Override
	public boolean isTerminated() {
		return true;
	}
}
