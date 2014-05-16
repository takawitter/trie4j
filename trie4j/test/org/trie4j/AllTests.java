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
import org.trie4j.bv.BitVectorUtilTest;
import org.trie4j.bv.Rank0OnlySuccinctBitVectorTest;
import org.trie4j.bv.Rank1OnlySuccinctBitVectorTest;
import org.trie4j.doublearray.DoubleArrayTest;
import org.trie4j.doublearray.MapDoubleArrayTest;
import org.trie4j.doublearray.MapTailDoubleArrayTest;
import org.trie4j.doublearray.OptimizedTailDoubleArrayWithConcatTailBuilderTest;
import org.trie4j.doublearray.OptimizedTailDoubleArrayWithSuffixTrieTailBuilderTest;
import org.trie4j.doublearray.TailDoubleArrayWithConcatTailBuilderTest;
import org.trie4j.doublearray.TailDoubleArrayWithSuffixTrieTailBuilderTest;
import org.trie4j.louds.LOUDSTrieTest;
import org.trie4j.louds.MapTailLOUDSPPTrieTest;
import org.trie4j.louds.MapTailLOUDSTrieTest;
import org.trie4j.louds.TailLOUDSPPTrieWithConcatTailBuilderTest;
import org.trie4j.louds.TailLOUDSPPTrieWithSuffixTrieTailBuilderTest;
import org.trie4j.louds.TailLOUDSTrieWithConcatTailBuilderTest;
import org.trie4j.louds.TailLOUDSTrieWithSuffixTrieTailBuilderTest;
import org.trie4j.patricia.simple.MapPatriciaTrieTest;
import org.trie4j.patricia.simple.PatriciaTrieTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithConcatTailBuilderTest;
import org.trie4j.patricia.tail.TailPatriciaTrieWithSuffixTrieTailBuilderTest;
import org.trie4j.tail.ConcatTailArrayTest;
import org.trie4j.tail.builder.SuffixTrieTailBuilderTest;
import org.trie4j.tail.index.ArrayTailIndexTest;
import org.trie4j.tail.index.SBVTailIndexTest;
import org.trie4j.util.CharsCharSequenceTest;
import org.trie4j.util.FastBitSetTest;
import org.trie4j.util.SuccinctBitVectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AlgorithmsTest.class,
	ArrayTailIndexTest.class,
	BitVectorUtilTest.class,
	ConcatTailArrayTest.class,
	CharsCharSequenceTest.class,
	FastBitSetTest.class,
	Rank0OnlySuccinctBitVectorTest.class,
	Rank1OnlySuccinctBitVectorTest.class,
	SBVTailIndexTest.class,
	SuccinctBitVectorTest.class,
	SuffixTrieTailBuilderTest.class,
	PatriciaTrieTest.class,
	MapPatriciaTrieTest.class,
	TailPatriciaTrieWithConcatTailBuilderTest.class,
	TailPatriciaTrieWithSuffixTrieTailBuilderTest.class,
	DoubleArrayTest.class,
	TailDoubleArrayWithConcatTailBuilderTest.class,
	TailDoubleArrayWithSuffixTrieTailBuilderTest.class,
	OptimizedTailDoubleArrayWithConcatTailBuilderTest.class,
	OptimizedTailDoubleArrayWithSuffixTrieTailBuilderTest.class,
	MapDoubleArrayTest.class,
	MapTailDoubleArrayTest.class,
	LOUDSTrieTest.class,
	TailLOUDSTrieWithConcatTailBuilderTest.class,
	TailLOUDSTrieWithSuffixTrieTailBuilderTest.class,
	TailLOUDSPPTrieWithConcatTailBuilderTest.class,
	TailLOUDSPPTrieWithSuffixTrieTailBuilderTest.class,
	MapTailLOUDSTrieTest.class,
	MapTailLOUDSPPTrieTest.class,
})
public class AllTests {
}
