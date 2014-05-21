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
package org.trie4j.patricia.tail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.trie4j.AbstractTrie;
import org.trie4j.Trie;
import org.trie4j.tail.FastTailCharIterator;
import org.trie4j.tail.TailBuilder;
import org.trie4j.tail.TailCharIterator;
import org.trie4j.tail.builder.SuffixTrieTailBuilder;
import org.trie4j.util.Pair;

public class TailPatriciaTrie
extends AbstractTrie
implements Serializable, Trie{
	public TailPatriciaTrie() {
		this(new SuffixTrieTailBuilder());
	}

	public TailPatriciaTrie(TailBuilder builder){
		this.tailBuilder = builder;
		this.tails = builder.getTails();
	}

	public TailPatriciaTrie(Trie orig, TailBuilder builder){
		this(builder);
		for(String s : orig.predictiveSearch("")){
			insert(s);
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public org.trie4j.Node getRoot() {
		return new NodeAdapter(root, tails);
	}

	@Override
	public boolean contains(String text) {
		Node node = root;
		FastTailCharIterator it = new FastTailCharIterator(tails, -1);
		int n = text.length();
		for(int i = 0; i < n; i++){
			node = node.getChild(text.charAt(i));
			if(node == null) return false;
			int ti = node.getTailIndex();
			if(ti == -1) continue;
			it.setIndex(node.getTailIndex());
			char c;
			while((c = it.getNext()) != '\0'){
				i++;
				if(i == n) return false;
				if(text.charAt(i) != c) return false;
			}
		}
		return node.isTerminate();
	}

	public Node getNode(String text) {
		Node node = root;
		FastTailCharIterator it = new FastTailCharIterator(tails, -1);
		int n = text.length();
		for(int i = 0; i < n; i++){
			node = node.getChild(text.charAt(i));
			if(node == null) return null;
			int ti = node.getTailIndex();
			if(ti == -1) continue;
			it.setIndex(node.getTailIndex());
			char c;
			while((c = it.getNext()) != '\0'){
				i++;
				if(i == n) return null;
				if(text.charAt(i) != c) return null;
			}
		}
		return node;
	}

	public CharSequence getTails() {
		return tails;
	}

	@Override
	public int findWord(CharSequence chars, int start, int end, StringBuilder word){
		TailCharIterator it = new TailCharIterator(tails, -1);
		for(int i = start; i < end; i++){
			Node node = root;
			for(int j = i; j < end; j++){
				node = node.getChild(chars.charAt(j));
				if(node == null) break;
				boolean matched = true;
				it.setIndex(node.getTailIndex());
				while(it.hasNext()){
					j++;
					if(j == end || chars.charAt(j) != it.next()){
						matched = false;
						break;
					}
				}
				if(matched){
					if(node.isTerminate()){
						if(word != null) word.append(chars, i, j + 1);
						return i;
					}
				} else{
					break;
				}
			}
		}
		return -1;
	}
	
	@Override
	public Iterable<String> commonPrefixSearch(final String query) {
		if(query.length() == 0) return new ArrayList<String>(0);
		return new Iterable<String>(){
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
							if(query.length() <= cur) return;
							Node child = current.getChild(query.charAt(cur));
							if(child == null) return;
							int rest = query.length() - cur;
							char[] letters = child.getLetters(tails);
							int len = letters.length;
							if(rest < len) return;
							for(int i = 1; i < len; i++){
								int c = letters[i] - query.charAt(cur + i);
								if(c != 0) return;
							}

							String b = query.substring(cur, cur + len);
							if(child.isTerminate()){
								next = currentChars + b;
							}
							cur += len;
							currentChars.append(b);
							current = child;
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

	public Iterable<Pair<String, Node>> commonPrefixSearchWithNode(final String query) {
		if(query.length() == 0) return new ArrayList<Pair<String, Node>>(0);
		return new Iterable<Pair<String, Node>>(){
			@Override
			public Iterator<Pair<String, Node>> iterator() {
				return new Iterator<Pair<String, Node>>() {
					private int cur;
					private StringBuilder currentChars = new StringBuilder();
					private Node current = root;
					private Pair<String, Node> next;
					{
						cur = 0;
						findNext();
					}
					private void findNext(){
						next = null;
						while(next == null){
							if(query.length() <= cur) return;
							Node child = current.getChild(query.charAt(cur));
							if(child == null) return;
							int rest = query.length() - cur;
							char[] letters = child.getLetters(tails);
							int len = letters.length;
							if(rest < len) return;
							for(int i = 1; i < len; i++){
								int c = letters[i] - query.charAt(cur + i);
								if(c != 0) return;
							}

							String b = query.substring(cur, cur + len);
							cur += len;
							currentChars.append(b);
							if(child.isTerminate()){
								next = Pair.create(currentChars.toString(), child);
							}
							current = child;
						}
					}
					@Override
					public boolean hasNext() {
						return next != null;
					}
					@Override
					public Pair<String, Node> next() {
						Pair<String, Node> ret = next;
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
		char[] queryChars = prefix.toCharArray();
		int cur = 0;
		Node node = root;
		while(node != null){
			char[] letters = node.getLetters(tails);
			int n = Math.min(letters.length, queryChars.length - cur);
			for(int i = 0; i < n; i++){
				if(letters[i] != queryChars[cur + i]){
					return Collections.emptyList();
				}
			}
			cur += n;
			if(queryChars.length == cur){
				List<String> ret = new ArrayList<String>();
				prefix += new String(letters, n, letters.length - n);
				if(node.isTerminate()) ret.add(prefix);
				enumLetters(node, prefix, ret);
				return ret;
			}
			node = node.getChild(queryChars[cur]);
		}
		return Collections.emptyList();
	}

	public Iterable<Pair<String, Node>> predictiveSearchWithNode(String prefix) {
		char[] queryChars = prefix.toCharArray();
		int cur = 0;
		Node node = root;
		while(node != null){
			char[] letters = node.getLetters(tails);
			int n = Math.min(letters.length, queryChars.length - cur);
			for(int i = 0; i < n; i++){
				if(letters[i] != queryChars[cur + i]){
					return Collections.emptyList();
				}
			}
			cur += n;
			if(queryChars.length == cur){
				List<Pair<String, Node>> ret = new ArrayList<Pair<String, Node>>();
				prefix += new String(letters, n, letters.length - n);
				if(node.isTerminate()) ret.add(Pair.create(prefix, node));
				enumLettersWithNode(node, prefix, ret);
				return ret;
			}
			node = node.getChild(queryChars[cur]);
		}
		return Collections.emptyList();
	}

	private void enumLetters(Node node, String prefix, List<String> letters){
		Node[] children = node.getChildren();
		if(children == null) return;
		for(Node child : children){
			String text = prefix + new String(child.getLetters(tails));
			if(child.isTerminate()) letters.add(text);
			enumLetters(child, text, letters);
		}
	}

	private void enumLettersWithNode(Node node, String prefix, List<Pair<String, Node>> letters){
		Node[] children = node.getChildren();
		if(children == null) return;
		for(Node child : children){
			String text = prefix + new String(child.getLetters(tails));
			if(child.isTerminate()) letters.add(Pair.create(text, child));
			enumLettersWithNode(child, text, letters);
		}
	}

	@Override
	public void insert(String text){
		if(tailBuilder == null){
			throw new UnsupportedOperationException("insert isn't permitted for freezed trie");
		}
		insert(root, text, 0);
	}

	protected Node insert(Node node, String letters, int offset){
		TailCharIterator it = new TailCharIterator(tails, node.getTailIndex());
		int count = 0;
		boolean matchComplete = true;
		int lettersLength = letters.length();
		while(it.hasNext() && offset < lettersLength){
			if(letters.charAt(offset) != it.next()){
				matchComplete = false;
				break;
			}
			offset++;
			count++;
		}
		if(offset == lettersLength){
			if(it.hasNext()){
				// n: abcde
				// l: abc
				char c = it.next();
				int idx = it.getNextIndex();
				if(!it.hasNext()){
					idx = -1;
				}
				Node newChild = newNode(c, idx, node);
				node.setTailIndex(
						(count > 0) ? tailBuilder.insert(letters, offset - count, count)
								: -1
						);
				node.setChildren(newNodeArray(newChild));
				node.setTerminate(true);
				size++;
				return node;
			} else{
				// n: abc
				// l: abc
				if(!node.isTerminate()){
					node.setTerminate(true);
					size++;
				}
				return node;
			}
		} else{
			if(!matchComplete){
				// n: abcwz
				// l: abcde
				int firstOffset = offset - count;
				char n1Fc = it.current();
				int n1Idx = it.getNextIndex();
				if(!it.hasNext()){
					n1Idx = -1;
				}
				Node n1 = newNode(n1Fc, n1Idx, node);
				char n2Fc = letters.charAt(offset++);
				int n2Idx = (offset < lettersLength) ?
						tailBuilder.insert(letters, offset, lettersLength - offset) :
						-1;
				Node n2 = newNode(n2Fc, n2Idx, true);
				if(count > 0){
					node.setTailIndex(tailBuilder.insert(letters, firstOffset, count));
				} else{
					node.setTailIndex(-1);
				}
				node.setTerminate(false);
				node.setChildren(
						(n1.getFirstLetter() < n2.getFirstLetter()) ?
								newNodeArray(n1, n2) : newNodeArray(n2, n1));
				size++;
				return n2;
			} else{
				// n: abc
				// l: abcde
				char fc = letters.charAt(offset++);
				// find node
				Pair<Node, Integer> ret = node.findNode(fc);
				Node child = ret.getFirst();
				if(child != null){
					return insert(child, letters, offset);
				} else{
					int idx = (offset < lettersLength) ?
						tailBuilder.insert(letters, offset, lettersLength - offset) :
						-1;
					Node newNode = newNode(fc, idx, true);
					node.addChild(ret.getSecond(), newNode);
					size++;
					return newNode;
				}
			}
		}
	}

	@Override
	public void trimToSize() {
		((StringBuilder)tails).trimToSize();
	}

	@Override
	public void freeze(){
		trimToSize();
		tailBuilder = null;
	}

	public TailBuilder getTailBuilder(){
		return tailBuilder;
	}

	protected Node newNode(){
		return new Node((char)0xffff, -1, false, newNodeArray());
	}

	protected Node newNode(char firstChar, int tailIndex, Node source){
		return new Node(firstChar, tailIndex, source.isTerminate(), source.getChildren());
	}

	protected Node newNode(char firstChar, int tailIndex, boolean terminated) {
		return new Node(firstChar, tailIndex, terminated, newNodeArray());
	}

	protected Node[] newNodeArray(Node... nodes){
		return nodes;
	}

	private int size;
	private Node root = newNode();
	private TailBuilder tailBuilder;
	private CharSequence tails;
	private static final long serialVersionUID = -2084269385978925271L;
}
