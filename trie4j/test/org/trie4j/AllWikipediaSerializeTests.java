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
package org.trie4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.trie4j.doublearray.DoubleArrayWikipediaSerializeTest;
import org.trie4j.doublearray.MapDoubleArrayWikipediaSerializeTest;
import org.trie4j.louds.MapTailLOUDSPPTrieWikipediaSerializeTest;
import org.trie4j.louds.MapTailLOUDSTrieWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSPPTrieWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSTrieWikipediaSerializeTest;
import org.trie4j.patricia.simple.MapPatriciaTrieWikipediaSerializeTest;
import org.trie4j.patricia.simple.PatriciaTrieWikipediaSerializeTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithConcatTailBuilderWikipediaSerializeTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithSuffixTailBuilderWikipediaSerializeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	PatriciaTrieWikipediaSerializeTest.class,
	MapPatriciaTrieWikipediaSerializeTest.class,
	TailPatriciaTrieWithConcatTailBuilderWikipediaSerializeTest.class,
	TailPatriciaTrieWithSuffixTailBuilderWikipediaSerializeTest.class,
//	MapTailPatriciaTrieWithConcatTailBuilderWikipediaSerializeTest.class,
//	MapTailPatriciaTrieWithSuffixTailBuilderWikipediaSerializeTest.class,
	DoubleArrayWikipediaSerializeTest.class,
	MapDoubleArrayWikipediaSerializeTest.class,
	TailLOUDSTrieWikipediaSerializeTest.class,
	MapTailLOUDSTrieWikipediaSerializeTest.class,
	TailLOUDSPPTrieWikipediaSerializeTest.class,
	MapTailLOUDSPPTrieWikipediaSerializeTest.class,
})
public class AllWikipediaSerializeTests {
}
