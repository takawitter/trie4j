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
import org.trie4j.louds.MapTailLOUDSPPTrieWithConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.MapTailLOUDSPPTrieWithSBVConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.MapTailLOUDSPPTrieWithSuffixTrieTailArrayWikipediaSerializeTest;
import org.trie4j.louds.MapTailLOUDSTrieWithConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.MapTailLOUDSTrieWithSBVConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.MapTailLOUDSTrieWithSuffixTrieTailArrayWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSPPTrieWithConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSPPTrieWithSBVConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSPPTrieWithSuffixTrieTailArrayWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSTrieWithConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSTrieWithSBVConcatTailArrayWikipediaSerializeTest;
import org.trie4j.louds.TailLOUDSTrieWithSuffixTrieTailArrayWikipediaSerializeTest;
import org.trie4j.patricia.simple.MapPatriciaTrieWikipediaSerializeTest;
import org.trie4j.patricia.simple.PatriciaTrieWikipediaSerializeTest;
import org.trie4j.patricia.tail.MapTailPatriciaTrieWithConcatTailBuilderWikipediaSerializeTest;
import org.trie4j.patricia.tail.MapTailPatriciaTrieWithSuffixTrieTailBuilderWikipediaSerializeTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithConcatTailBuilderWikipediaSerializeTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithSuffixTrieTailBuilderWikipediaSerializeTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	PatriciaTrieWikipediaSerializeTest.class,
	MapPatriciaTrieWikipediaSerializeTest.class,
	TailPatriciaTrieWithConcatTailBuilderWikipediaSerializeTest.class,
	TailPatriciaTrieWithSuffixTrieTailBuilderWikipediaSerializeTest.class,
	MapTailPatriciaTrieWithConcatTailBuilderWikipediaSerializeTest.class,
	MapTailPatriciaTrieWithSuffixTrieTailBuilderWikipediaSerializeTest.class,
	DoubleArrayWikipediaSerializeTest.class,
	MapDoubleArrayWikipediaSerializeTest.class,
	TailLOUDSTrieWithConcatTailArrayWikipediaSerializeTest.class,
	TailLOUDSTrieWithSBVConcatTailArrayWikipediaSerializeTest.class,
	TailLOUDSTrieWithSuffixTrieTailArrayWikipediaSerializeTest.class,
	MapTailLOUDSTrieWithConcatTailArrayWikipediaSerializeTest.class,
	MapTailLOUDSTrieWithSBVConcatTailArrayWikipediaSerializeTest.class,
	MapTailLOUDSTrieWithSuffixTrieTailArrayWikipediaSerializeTest.class,
	TailLOUDSPPTrieWithConcatTailArrayWikipediaSerializeTest.class,
	TailLOUDSPPTrieWithSBVConcatTailArrayWikipediaSerializeTest.class,
	TailLOUDSPPTrieWithSuffixTrieTailArrayWikipediaSerializeTest.class,
	MapTailLOUDSPPTrieWithConcatTailArrayWikipediaSerializeTest.class,
	MapTailLOUDSPPTrieWithSBVConcatTailArrayWikipediaSerializeTest.class,
	MapTailLOUDSPPTrieWithSuffixTrieTailArrayWikipediaSerializeTest.class,
})
public class AllWikipediaSerializeTests {
}
