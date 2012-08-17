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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.trie4j.doublearray.DoubleArrayTest;
import org.trie4j.doublearray.TailDoubleArrayWithSuffixTrieTailBuilderTest;
import org.trie4j.doublearray.TailDoubleArrayWithConcatTailBuilderTest;
import org.trie4j.louds.LOUDSTrieWithConcatTailBuilderTest;
import org.trie4j.louds.LOUDSTrieWithSuffixTrieTailBuilderTest;
import org.trie4j.patricia.multilayer.MultilayerPatriciaTrieTest;
import org.trie4j.patricia.simple.PatriciaTrieTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithConcatTailBuilderTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithSuffixTrieTailBuilderTest;
import org.trie4j.tail.SuffixTrieTailBuilderTest;
import org.trie4j.util.SuccinctBitVectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	SuffixTrieTailBuilderTest.class,
	SuccinctBitVectorTest.class,
	PatriciaTrieTest.class,
	MultilayerPatriciaTrieTest.class,
	TailPatriciaTrieWithConcatTailBuilderTest.class,
	TailPatriciaTrieWithSuffixTrieTailBuilderTest.class,
	DoubleArrayTest.class,
	TailDoubleArrayWithConcatTailBuilderTest.class,
	TailDoubleArrayWithSuffixTrieTailBuilderTest.class,
	LOUDSTrieWithConcatTailBuilderTest.class,
	LOUDSTrieWithSuffixTrieTailBuilderTest.class,
	//WikipediaTitlesTest.class,
})
public class AllTests {
}
