package org.trie4j.patricia.multilayer.node;

import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class TerminalInternalLabelTrieNode extends InternalLabelTrieNode {
	public TerminalInternalLabelTrieNode(LabelNode ln, Node[] children) {
		super(ln, children);
	}

	@Override
	public boolean isTerminated() {
		return true;
	}
}
