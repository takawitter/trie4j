/*
 * Copyright 2012 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trie4j;

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
	boolean contains(String word);

	int findWord(CharSequence chars, int start, int end, StringBuilder word);
	
	Iterable<String> commonPrefixSearch(String query);
	
	Iterable<String> predictiveSearch(String prefix);

	/**
	 * insert word.
	 * @param word word to insert.
	 */
	void insert(String word);

	void dump();

	/**
	 * shrink buffer size to fit actual node count.
	 */
	void trimToSize();
}
