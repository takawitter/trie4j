package org.trie4j.bytes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public interface Trie {
	/**
	 * returns inserted word count(equals to terminal node count)
	 * @return inserted word count
	 */
	int size();

	/**
	 * retuns root node.
	 * @return root node.
	 */
	Node getRoot();

	/**
	 * returns true if trie contains word.
	 * @param word word to check it contained.
	 * @return true if trie contains word.
	 */
	boolean contains(byte[] word);

	/**
	 * search trie for word contained in chars. If the word is found, this method
	 * returns the position in chars and add found word to word parameter.
	 * @param chars chars
	 * @param start start position
	 * @param end end position
	 * @param word buffer to append found word. this can be null
	 * @return found position. -1 if no word found.
	 */
	int findWord(byte[] chars, int start, int end, OutputStream word)
	throws IOException;
	
	/**
	 * search trie for words contained in query.
	 * If query is "helloworld" and trie contains "he", "hello" and "world",
	 * the words "he" and "hello" will be found.
	 * @param query query
	 * @return Iterable object which iterates found words.
	 */
	Iterable<byte[]> commonPrefixSearch(byte[] query);

	/**
	 * search trie for words starting prefix.
	 * If prefix is "he" and trie contains "he", "hello" and "world",
	 * the words "he" and "hello" will be found.
	 * @param prefix prefix
	 * @return Iterable object which iterates found words.
	 */
	Iterable<byte[]> predictiveSearch(byte[] prefix);

	/**
	 * insert word.
	 * @param word word to insert.
	 */
	void insert(byte[] word);

	/**
	 * dump trie to Writer.
	 * @param writer writer
	 */
	void dump(Writer writer) throws IOException;

	/**
	 * shrink buffer size to fit actual node count.
	 */
	void trimToSize();

	/**
	 * freeze trie and drop objects allocated for insert operation.
	 * trie goes immutable.
	 */
	void freeze();
}
