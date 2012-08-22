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

import java.util.Iterator;

public abstract class AbstractTrie implements Trie{
	@Override
	public int findWord(CharSequence chars, int start, int end, StringBuilder word){
		for(int i = start; i < end; i++){
			Iterator<String> it = commonPrefixSearch(chars.subSequence(i, end).toString()).iterator();
			if(it.hasNext()){
				if(word != null) word.append(it.next());
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
