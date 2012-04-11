package org.trie4j.patricia.multilayer.node;

import org.trie4j.patricia.multilayer.Node;
import org.trie4j.patricia.multilayer.labeltrie.LabelNode;

public class TerminalSingleChildInternalCharsNode extends SingleChildInternalCharsNode {
	public TerminalSingleChildInternalCharsNode(char[] letters, Node child) {
		super(letters, child);
	}

	@Override
	public boolean isTerminated() {
		return true;
	}

	public Node addChild(int index, Node n){
		if(index == 0){
			return new TerminalInternalCharsNode(getLetters(), new Node[]{n, getChildren()[0]});
		} else{
			return new TerminalInternalCharsNode(getLetters(), new Node[]{getChildren()[0], n});
		}
	}

	@Override
	protected Node newInternalCharsNode(char[] letters, Node[] children) {
		if(children.length != 1) throw new IllegalStateException();
		return new TerminalSingleChildInternalCharsNode(letters, children[0]);
	}

	@Override
	protected Node cloneWithLetters(char[] letters) {
		return new TerminalSingleChildInternalCharsNode(letters, getChildren()[0]);
	}

	protected Node newLabelTrieNode(LabelNode ln, Node[] children){
		if(children != null){
			if(children.length != 1) throw new IllegalArgumentException();
			return new TerminalSingleChildInternalLabelTrieNode(ln, children[0]);
		} else{
			return new TerminalLabelTrieNode(ln);
		}
	}
}
