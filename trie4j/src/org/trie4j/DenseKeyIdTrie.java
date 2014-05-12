/*
 * Copyright 2014 Takao Nakaguchi
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

/**
 * Represents the Trie that can manage dense key ID for containing key set.
 * The dense key ID will not be changed through any method call.
 * The "dense key ID" means serial ID and it starts from 0.
 * @author Takao Nakaguchi
 */
public interface DenseKeyIdTrie extends Trie{
	@Override
	public DenseKeyIdNode getRoot();

	/**
	 * Returns the dense key ID for text. If text isn't exists in this Trie, this
	 * method returns -1.
	 * @param text key to obtain dense key ID.
	 * @return
	 */
	int getDenseKeyIdFor(String text);

	int geteMaxDenseKeyId();

	/**
	 * Search texts that is part of query and returns found keys with
	 * dense key id.
	 * @param query
	 * @return Iterable of found pairs (key and dense key id).
	 */
	Iterable<Pair<String, Integer>> commonPrefixSearchWithDenseKeyId(String query);

	
	/**
	 * Search texts that is begin with prefix and returns found keys with
	 * dense key id.
	 * @param query
	 * @return Iterable of found pairs (key and dense key id).
	 */
	Iterable<Pair<String, Integer>> predictiveSearchWithDenseKeyId(String prefix);
}
