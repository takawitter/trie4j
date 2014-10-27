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
package org.trie4j.bytes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.trie4j.util.StringUtil;

public class PatriciaTrie implements Trie{
	public boolean contains(String word) {
		return root.contains(StringUtil.toUTF8(word), 0);
	}

	@Override
	public boolean contains(byte[] word) {
		return root.contains(word, 0);
	}

	@Override
	public Iterable<byte[]> commonPrefixSearch(final byte[] query) {
		List<byte[]> ret = new ArrayList<byte[]>();
		byte[] queryChars = query;
		int cur = 0;
		Node node = root;
		while(node != null){
			byte[] letters = node.getLetters();
			if(letters.length > (queryChars.length - cur)) return ret;
			for(int i = 0; i < letters.length; i++){
				if(letters[i] != queryChars[cur + i]) return ret;
			}
			if(node.isTerminate()){
				ret.add(Arrays.copyOfRange(queryChars, 0 , cur + letters.length));
			}
			cur += letters.length;
			if(queryChars.length == cur) return ret;
			node = node.getChild(queryChars[cur]);
		}
		return ret;
	}

	private static void enumLetters(Node node, String prefix, List<String> letters){
		Node[] children = node.getChildren();
		if(children == null) return;
		for(Node child : children){
			String text = prefix + StringUtil.fromUTF8(child.getLetters());
			if(child.isTerminate()) letters.add(text);
			enumLetters(child, text, letters);
		}
	}

	public Iterable<String> predictiveSearch(String prefix) {
		byte[] queryChars = StringUtil.toUTF8(prefix);
		int cur = 0;
		Node node = root;
		while(node != null){
			byte[] letters = node.getLetters();
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

	public void insert(byte[] text){
		byte[] letters = text;
		if(root == null){
			root = new PatriciaTrieNode(letters, true);
			return;
		}
		root.insertChild(letters, 0);
	}
	public void visit(TrieVisitor visitor){
		root.visit(visitor, 0);
	}

	public Node getRoot(){
		return root;
	}
	
	@Override
	public void freeze() {
		
	}

	@Override
	public void dump(Writer writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int findWord(byte[] chars, int start, int end, OutputStream word)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterable<byte[]> predictiveSearch(byte[] prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trimToSize() {
		// TODO Auto-generated method stub
		
	}

	private PatriciaTrieNode root;
}
