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

public abstract class AbstractTrie implements Trie{
	@Override
	public int findCommonPrefix(CharSequence chars, int start, int end) {
		for(int i = start; i < end; i++){
			if(commonPrefixSearch(chars.subSequence(start, end).toString()).iterator().hasNext()){
				return i;
			}
		}
		return -1;
	}

	@Override
	public void trimToSize() {
	}

	@Override
	public void traverse(NodeVisitor visitor){
		Algorithms.traverseDepth(visitor, getRoot());
	}

	@Override
	public void dump(){
		System.out.println("-- dump " + getClass().getName() + " --");
		Algorithms.dump(getRoot());
	}
}
