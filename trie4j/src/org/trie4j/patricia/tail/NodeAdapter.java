package org.trie4j.patricia.tail;

public class NodeAdapter implements org.trie4j.Node{
	public NodeAdapter(Node node, CharSequence tails){
		this.node = node;
		this.tails = tails;
	}
	@Override
	public org.trie4j.Node getChild(char c) {
		Node n = node.getChild(c);
		if(n == null) return null;
		else return new NodeAdapter(n, tails);
	}
	@Override
	public org.trie4j.Node[] getChildren() {
		Node[] children = node.getChildren();
		if(children == null) return null;
		org.trie4j.Node[] ret = new org.trie4j.Node[children.length];
		for(int i = 0; i < ret.length; i++){
			ret[i] = new NodeAdapter(node.getChildren()[i], tails);
		}
		return ret;
	}
	public char getFirstLetter(){
		return node.getFirstLetter();
	}
	@Override
	public char[] getLetters() {
		return node.getLetters(tails);
	}
	@Override
	public boolean isTerminate() {
		return node.isTerminate();
	}
	private Node node;
	private CharSequence tails;
}
