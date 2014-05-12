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
package org.trie4j.louds;

import org.trie4j.Trie;
import org.trie4j.louds.bvtree.BvTree;
import org.trie4j.louds.bvtree.LOUDSBvTree;
import org.trie4j.patricia.simple.PatriciaTrie;
import org.trie4j.tail.ConcatTailArray;
import org.trie4j.tail.TailArray;

public class TailLOUDSTrie extends AbstractTailLOUDSTrie implements Trie {
	public TailLOUDSTrie(){
		this(new PatriciaTrie());
	}

	public TailLOUDSTrie(Trie orig){
		this(orig, new ConcatTailArray(orig.size()));
	}

	public TailLOUDSTrie(Trie orig, TailArray tailArray){
		this(orig, new LOUDSBvTree(orig.size() * 2), tailArray);
	}

	public TailLOUDSTrie(Trie orig, BvTree bvtree, TailArray tailArray){
		super(orig, bvtree, tailArray);
	}

	public TailLOUDSTrie(Trie orig, BvTree bvtree, TailArray tailArray, NodeListener listener){
		super(orig, bvtree, tailArray, listener);
	}
}
