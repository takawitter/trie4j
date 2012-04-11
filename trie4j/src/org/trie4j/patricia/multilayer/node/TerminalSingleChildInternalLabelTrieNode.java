package org.trie4j.patricia.multilayer.node;

import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class TerminalSingleChildInternalLabelTrieNode extends SingleChildInternalLabelTrieNode {
	public TerminalSingleChildInternalLabelTrieNode(LabelNode ln, Node child) {
		super(ln, child);
	}

	@Override
	public boolean isTerminated() {
		return true;
	}
}
