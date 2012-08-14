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

import java.util.concurrent.atomic.AtomicInteger;

import org.trie4j.patricia.multilayer.labeltrie.LabelNode;
import org.trie4j.patricia.multilayer.node.LabelTrieNode;

public abstract class AbstractTrie implements Trie{
	@Override
	public int findCommonPrefix(char[] chars, int begin, int end) {
		for(int i = begin; i < end; i++){
			if(commonPrefixSearch(new String(chars, i, end - i)).iterator().hasNext()){
				return i;
			}
		}
		return -1;
	}

	@Override
	public void trimToSize() {
	}

	@Override
	public void dump(){
		System.out.println("-- dump " + getClass().getName() + " --");
		final AtomicInteger c = new AtomicInteger();
		visit(new TrieVisitor() {
			@Override
			public void accept(Node node, int nest) {
				int cur = c.get();
				if(cur > 100) return;
				for(int i = 0; i < nest; i++){
					System.out.print(" ");
				}
				if(cur == 100){
					System.out.println("... over 100 nodes");
					return;
				}
				c.incrementAndGet();
				char[] letters = node.getLetters();
				if(letters != null && letters.length > 0){
					System.out.print(letters);
				} else if(node instanceof LabelTrieNode){
					LabelNode ln = ((LabelTrieNode)node).getLettersNode();
					if(ln != null){
						do{
							System.out.print("#");
							char[] l = ln.getLetters();
							for(int i = 0; i < l.length; i++){
								System.out.print(l[l.length - i - 1]);
							}
							ln = ln.getParent();
						} while(ln != null);
					} else{
						System.out.print("<empty>");
					}
				}
				if(node.isTerminate()){
					System.out.print("*");
				}
				System.out.println();
			}
		});
	}

}
