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
package org.trie4j.doublearray;

import java.util.Arrays;

import org.trie4j.AbstractDenseKeyIdMapTrie;
import org.trie4j.MapNode;
import org.trie4j.MapTrie;
import org.trie4j.Node;
import org.trie4j.doublearray.DoubleArray.TermNodeListener;

/**
 * TODO protected DoubleArray(.., NodeListener l)を用意して
 *      MapDoubleArrayから利用する?
 * @author nakaguchi
 *
 * @param <T>
 */
public class MapDoubleArray2<T> extends AbstractDenseKeyIdMapTrie<T> implements MapTrie<T>{
	public MapDoubleArray2() {
	}

	public MapDoubleArray2(MapTrie<T> trie){
		this(trie, trie.size() * 2);
	}

	public MapDoubleArray2(MapTrie<T> trie, int arraySize){
		DenseKeyIdDoubleArray da = new DenseKeyIdDoubleArray(trie, arraySize, new TermNodeListener(){
			@Override
			@SuppressWarnings("unchecked")
			public void listen(Node node, int nodeIndex) {
				if(nodeIndex >= getValues().length){
					setValues((Object[])Arrays.copyOf(getValues(), nodeIndex + 1));
				}
				getValues()[nodeIndex] = ((MapNode<T>)node).getValue();
			}
		});
		setTrie(da);
		Object[] values = getValues();
		int n = values.length;
		int c = 0;
		for(int i = 0; i < n; i++){
			if(values[i] != null){
				values[c++] = values[i];
			}
		}
		setValues(Arrays.copyOf(values, c));
	}
}
