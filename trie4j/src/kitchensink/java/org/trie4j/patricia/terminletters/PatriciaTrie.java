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
package org.trie4j.patricia.terminletters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.trie4j.AbstractTrie;
import org.trie4j.NodeVisitor;
import org.trie4j.Trie;
import org.trie4j.util.Pair;

@Deprecated
public class PatriciaTrie extends AbstractTrie implements Trie{
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean contains(String word) {
		return root.contains(word.toCharArray(), 0);
	}

	@Override
	public Iterable<String> commonPrefixSearch(final String query) {
		if(query.length() == 0) return new ArrayList<String>(0);
		return new Iterable<String>(){
			{
				this.queryChars = query.toCharArray();
			}
			private char[] queryChars;
			@Override
			public Iterator<String> iterator() {
				return new Iterator<String>() {
					private int cur;
					private StringBuilder currentChars = new StringBuilder();
					private Node current = root;
					private String next;
					{
						cur = 0;
						findNext();
					}
					private void findNext(){
						next = null;
						while(next == null){
//*
							Node n = current.getChild(queryChars[cur]);
							if(n == null) return;
							Pair<Boolean, Integer> r = n.compareLetters(queryChars, cur);
							if(!r.getFirst()) return;
							if(r.getSecond() > 0) return;
							int d = r.getSecond();
							if(d > 0) return;
							d *= -1;

							String b = new String(queryChars, cur, d);
							if(n.isTerminate()){
								next = currentChars + b;
							}
							cur += d;
							currentChars.append(b);
							current = n;
							
/*/
							int rest = queryChars.length - cur;
							Node n = current.getChild(queryChars[cur]);
							if(n == null) return;
							char[] l = n.getLetters();
							int len = l.length;
							boolean t = false;
							if(l[len - 1] == 0xffff){
								t = true;
								len--;
							}
							if(n.getChildren() == null){
								t = true;
							}
							if(rest < len) return;
							for(int i = 0; i < len; i++){
								if(l[i] != queryChars[cur++])  return;
							}
							String b = new String(l, 0, len);
							if(t){
								next = currentChars + b;
							}
							currentChars.append(b);
							current = n;
//*/
						}
					}
					@Override
					public boolean hasNext() {
						return next != null;
					}
					@Override
					public String next() {
						String ret = next;
						if(ret == null){
							throw new NoSuchElementException();
						}
						findNext();
						return ret;
					}
					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	@Override
	public Iterable<String> predictiveSearch(String prefix) {
		return null;
	}

	public void insert(String text){
		char[] letters = text.toCharArray();
		if(root == null){
			root = new Node(letters);
		} else{
			root.insertChild(letters, 0);
		}
		size++;
	}
	public void visit(NodeVisitor visitor){
		root.visit(visitor, 0);
	}

	public int nodeSize(){
		throw new UnsupportedOperationException();
	}

	public Node getRoot(){
		return root;
	}

	public void trimToSize() {
	}

	private int size;
	private Node root;
}
