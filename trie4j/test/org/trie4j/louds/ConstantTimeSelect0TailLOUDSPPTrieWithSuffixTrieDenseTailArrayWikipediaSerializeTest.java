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

import org.trie4j.AbstractWikipediaSerializeTest;
import org.trie4j.Trie;
import org.trie4j.bv.BitVector01Divider;
import org.trie4j.bv.BytesConstantTimeSelect0SuccinctBitVector;
import org.trie4j.bv.BytesRank0OnlySuccinctBitVector;
import org.trie4j.bv.SuccinctBitVector;
import org.trie4j.louds.bvtree.LOUDSPPBvTree;
import org.trie4j.tail.SuffixTrieDenseTailArrayBuilder;

public class ConstantTimeSelect0TailLOUDSPPTrieWithSuffixTrieDenseTailArrayWikipediaSerializeTest
extends AbstractWikipediaSerializeTest{
	@Override
	protected Trie secondTrie(Trie firstTrie) {
		SuccinctBitVector r0 = new BytesRank0OnlySuccinctBitVector();
		SuccinctBitVector r1 = new BytesConstantTimeSelect0SuccinctBitVector();
		Trie t = new TailLOUDSTrie(
				firstTrie,
				new LOUDSPPBvTree(
						new BitVector01Divider(r0, r1),
						r0, r1),
				new SuffixTrieDenseTailArrayBuilder());
		t.trimToSize();
		return t;
	}
}
