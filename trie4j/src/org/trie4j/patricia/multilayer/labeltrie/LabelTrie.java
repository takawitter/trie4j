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
package org.trie4j.patricia.multilayer.labeltrie;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;

public class LabelTrie{
	public LabelTrie() {
		root.addReferer(new LabelTrieNode(new LabelNode(null){
			@Override
			public void addReferer(LabelTrieNode l) {
			}
		}) {
			@Override
			public void setLettersNode(LabelNode lettersNode) {
				root = lettersNode;
			}
		});
	}

	public LabelNode getRoot(){
		return root;
	}

	public boolean contains(String word) {
		return root.contains(word.toCharArray(), 0);
	}

	public void insert(String word) {
		insert(word.toCharArray());
	}

	public LabelNode insert(char[] letters){
		return root.insertChild(0, letters, 0);
	}

	public void pargeChildren(){
		root.pargeChildren();
	}

	public void visit(TrieVisitor visitor){
		root.visit(visitor, 0);
	}

	private LabelNode root = new InternalLabelNode(LabelNode.emptyChars, null, null);
}
