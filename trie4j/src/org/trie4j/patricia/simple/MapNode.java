/*
 * Copyright (C) 2012 Takao Nakaguchi
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

public class MapNode<T>
extends Node
implements Serializable, org.trie4j.MapNode<T>{
	public MapNode() {
		super();
	}

	public MapNode(char[] letters, boolean terminated) {
		super(letters, terminated);
	}

	public MapNode(char[] letters, boolean terminated, T value) {
		super(letters, terminated);
		this.value = value;
	}

	public MapNode(char[] letters, boolean terminated, Node[] children) {
		super(letters, terminated, children);
	}
	
	public MapNode(char[] letters, boolean terminated, Node[] children, T value) {
		super(letters, terminated, children);
		this.value = value;
	}
	
	public T getValue(){
		return value;
	}

	public void setValue(T value){
		this.value = value;
	}

	private T value;
	private static final long serialVersionUID = 8611758181642617230L;
}
