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

import org.trie4j.util.Pair;

public interface IdTrie extends Trie {
	/**
	 * returns the id for the word if the trie contains the word, otherwise -1.
	 * @param word word to check it contained.
	 * @return the id for the word if the trie contains the word, otherwise -1
	 */
	int getId(String word);
	
	/**
	 * search trie for words contained in query.
	 * If query is "helloworld" and trie contains "he", "hello" and "world",
	 * the words "he" and "hello" will be found.
	 * @param query query
	 * @return Iterable object which iterates found words and their ids.
	 */
	Iterable<Pair<String, Integer>> commonPrefixSearchId(String query);

	/**
	 * search trie for words starting prefix.
	 * If prefix is "he" and trie contains "he", "hello" and "world",
	 * the words "he" and "hello" will be found.
	 * @param prefix prefix
	 * @return Iterable object which iterates found words and their ids.
	 */
	Iterable<Pair<String, Integer>> predictiveSearchId(String prefix);
}
