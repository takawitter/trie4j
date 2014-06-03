/*
 * Copyright 2014 Takao Nakaguchi
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

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.List;

import org.trie4j.AbstractTermIdMapTrie;
import org.trie4j.MapNode;
import org.trie4j.MapTrie;
import org.trie4j.Node;
import org.trie4j.louds.AbstractTailLOUDSTrie.NodeListener;
import org.trie4j.louds.bvtree.BvTree;
import org.trie4j.louds.bvtree.LOUDSPPBvTree;
import org.trie4j.tail.ConcatTailArrayBuilder;
import org.trie4j.tail.TailArrayBuilder;

public class MapTailLOUDSPPTrie<T>
extends AbstractTermIdMapTrie<T>
implements Externalizable, MapTrie<T>{
	public MapTailLOUDSPPTrie(){
	}

	public MapTailLOUDSPPTrie(MapTrie<T> orig){
		this(orig, new ConcatTailArrayBuilder(orig.size()));
	}

	public MapTailLOUDSPPTrie(MapTrie<T> orig, TailArrayBuilder tailArrayBuilder){
		this(orig, new LOUDSPPBvTree(orig.size() * 2), tailArrayBuilder);
	}

	public MapTailLOUDSPPTrie(MapTrie<T> orig, BvTree bvtree, TailArrayBuilder tailArrayBuilder){
		final List<T> values = new ArrayList<T>();
		setTrie(new TailLOUDSTrie(orig, bvtree, tailArrayBuilder, new NodeListener(){
			@Override
			@SuppressWarnings("unchecked")
			public void listen(Node node, int id) {
				if(node.isTerminate()){
					values.add(((MapNode<T>)node).getValue());
				}
			}
		}));
		setValues(values.toArray());
	}
}
