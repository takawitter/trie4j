package org.trie4j.patricia.multilayer.node;

import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class TerminalCharsNode extends CharsNode{
	public TerminalCharsNode(char[] letters) {
		super(letters);
	}

	@Override
	public boolean isTerminated() {
		return true;
	}

	@Override
	protected Node newInternalCharsNode(char[] letters, Node[] children){
		if(children.length == 1){
			return new TerminalSingleChildInternalCharsNode(letters, children[0]);
		} else{
			return new TerminalInternalCharsNode(letters, children);
		}
	}

	@Override
	protected Node cloneWithLetters(char[] letters) {
		return new TerminalCharsNode(letters);
	}
	
	@Override
	protected Node newLabelTrieNode(LabelNode ln, Node[] children) {
		if(children != null){
			return new TerminalInternalLabelTrieNode(ln, children);
		} else{
			return new TerminalLabelTrieNode(ln);
		}
	}
}
