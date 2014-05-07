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
package org.trie4j.patricia.simple;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.trie4j.AbstractTrie;
import org.trie4j.NodeVisitor;
import org.trie4j.Trie;

public class PatriciaTrie
extends AbstractTrie
implements Serializable, Trie{
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean contains(String text) {
		Node node = root;
		int n = text.length();
		for(int i = 0; i < n; i++){
			node = node.getChild(text.charAt(i));
			if(node == null) return false;
			char[] letters = node.getLetters();
			int lettersLen = letters.length;
			for(int j = 1; j < lettersLen; j++){
				i++;
				if(i == n) return false;
				if(text.charAt(i) != letters[j]) return false;
			}
		}
		return node.isTerminate();
	}

	@Override
	public Iterable<String> commonPrefixSearch(String query) {
		List<String> ret = new ArrayList<String>();
		char[] queryChars = query.toCharArray();
		int cur = 0;
		Node node = root;
		while(node != null){
			char[] letters = node.getLetters();
			if(letters.length > (queryChars.length - cur)) return ret;
			for(int i = 0; i < letters.length; i++){
				if(letters[i] != queryChars[cur + i]) return ret;
			}
			if(node.isTerminate()){
				ret.add(new String(queryChars, 0 , cur + letters.length));
			}
			cur += letters.length;
			if(queryChars.length == cur) return ret;
			node = node.getChild(queryChars[cur]);
		}
		return ret;
	}

	private static void enumLetters(Node node, String prefix, List<String> letters){
		for(Node child : node.getChildren()){
			String text = prefix + new String(child.getLetters());
			if(child.isTerminate()) letters.add(text);
			enumLetters(child, text, letters);
		}
	}

	public Iterable<String> predictiveSearch(String prefix) {
		char[] queryChars = prefix.toCharArray();
		int cur = 0;
		Node node = root;
		while(node != null){
			char[] letters = node.getLetters();
			int n = Math.min(letters.length, queryChars.length - cur);
			for(int i = 0; i < n; i++){
				if(letters[i] != queryChars[cur + i]){
					return Collections.emptyList();
				}
			}
			cur += n;
			if(queryChars.length == cur){
				List<String> ret = new ArrayList<String>();
				int rest = letters.length - n;
				if(rest > 0){
					prefix += new String(letters, n, rest);
				}
				if(node.isTerminate()) ret.add(prefix);
				enumLetters(node, prefix, ret);
				return ret;
			}
			node = node.getChild(queryChars[cur]);
		}
		return Collections.emptyList();
	}

	public void insert(String text){
		insert(root, text.toCharArray(), 0);
	}

	private void insert(Node node, char[] letters, int offset){
		int lettersRest = letters.length - offset;
		while(true){
			int thisLettersLength = node.getLetters().length;
			int n = Math.min(lettersRest, thisLettersLength);
			int i = 0;
			while(i < n && (letters[i + offset] - node.getLetters()[i]) == 0) i++;
			if(i != n){
				Node child1 = new Node(
						Arrays.copyOfRange(node.getLetters(), i, node.getLetters().length)
						, node.isTerminate(), node.getChildren());
				Node child2 = new Node(
						Arrays.copyOfRange(letters, i + offset, letters.length)
						, true);
				node.setLetters(Arrays.copyOfRange(node.getLetters(), 0, i));
				node.setTerminate(false);
				node.setChildren(
						(child1.getLetters()[0] < child2.getLetters()[0]) ?
						new Node[]{child1, child2} : new Node[]{child2, child1});
				size++;
			} else if(lettersRest == thisLettersLength){
				if(!node.isTerminate()){
					node.setTerminate(true);
					size++;
				}
			} else if(lettersRest < thisLettersLength){
				Node newChild = new Node(
						Arrays.copyOfRange(node.getLetters(), lettersRest, thisLettersLength)
						, node.isTerminate(), node.getChildren());
				node.setLetters(Arrays.copyOfRange(node.getLetters(), 0, i));
				node.setTerminate(true);
				node.setChildren(new Node[]{newChild});
				size++;
			} else{
				int index = 0;
				int end = node.getChildren().length;
				boolean cont = false;
				if(end > 16){
					int start = 0;
					while(start < end){
						index = (start + end) / 2;
						Node child = node.getChildren()[index];
						int c = letters[i + offset] - child.getLetters()[0];
						if(c == 0){
							node = child;
							offset += i;
							lettersRest -= i;
							cont = true;
							break;
						}
						if(c < 0){
							end = index;
						} else if(start == index){
							index = end;
							break;
						} else{
							start = index;
						}
					}
				} else{
					for(; index < end; index++){
						Node child = node.getChildren()[index];
						int c = letters[i + offset] - child.getLetters()[0];
						if(c < 0) break;
						if(c == 0){
							node = child;
							offset += i;
							lettersRest -= i;
							cont = true;
							break;
						}
					}
				}
				if(cont) continue;
				node.addChild(index, new Node(Arrays.copyOfRange(letters, i + offset, letters.length), true));
				size++;
			}
			break;
		}
	}

	public void visit(NodeVisitor visitor){
		root.visit(visitor, 0);
	}

	public Node getRoot(){
		return root;
	}

	private int size;
	private Node root = new Node();
	private static final long serialVersionUID = -7611399538600722195L;
}
