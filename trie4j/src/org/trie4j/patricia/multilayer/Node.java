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
package org.trie4j.patricia.multilayer;

import org.trie4j.TrieVisitor;
import org.trie4j.patricia.multilayer.labeltrie.LabelTrie;

public abstract class Node implements org.trie4j.Node {
	public abstract boolean contains(char[] letters, int offset);

	@Override
	public abstract boolean isTerminate();

	@Override
	public abstract char[] getLetters();
	public abstract char getFirstLetter();

	@Override
	public abstract Node[] getChildren();
	public abstract void setChildren(Node[] children);

	public abstract Node getChild(char c);
	public abstract void setChild(int index, Node child);
	public abstract Node insertChild(char[] letters, int offset);
	public abstract Node addChild(int index, Node n);

	public abstract void visit(TrieVisitor visitor, int nest);

	public abstract Node pushLabel(LabelTrie trie);
}
