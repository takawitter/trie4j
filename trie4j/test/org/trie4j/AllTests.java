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
import org.trie4j.doublearray.MapTailDoubleArrayWithConcatTailBuilderTest;
import org.trie4j.doublearray.MapTailDoubleArrayWithSuffixTrieTailBuilderTest;
import org.trie4j.doublearray.OptimizedTailDoubleArrayWithConcatTailBuilderTest;
import org.trie4j.doublearray.OptimizedTailDoubleArrayWithSuffixTrieTailBuilderTest;
import org.trie4j.doublearray.TailDoubleArrayWithConcatTailBuilderTest;
import org.trie4j.doublearray.TailDoubleArrayWithSuffixTrieTailBuilderTest;
import org.trie4j.louds.LOUDSTrieTest;
import org.trie4j.louds.MapTailLOUDSPPTrieWithConcatTailArrayTest;
import org.trie4j.louds.MapTailLOUDSPPTrieWithSBVConcatTailArrayTest;
import org.trie4j.louds.MapTailLOUDSPPTrieWithSuffixTrieTailArrayTest;
import org.trie4j.louds.MapTailLOUDSTrieWithConcatTailArrayTest;
import org.trie4j.louds.MapTailLOUDSTrieWithSBVConcatTailArrayTest;
import org.trie4j.louds.MapTailLOUDSTrieWithSuffixTrieTailArrayTest;
import org.trie4j.louds.TailLOUDSPPTrieWithConcatTailArrayTest;
import org.trie4j.louds.TailLOUDSPPTrieWithSBVConcatTailArrayTest;
import org.trie4j.louds.TailLOUDSPPTrieWithSuffixTrieTailArrayTest;
import org.trie4j.louds.TailLOUDSTrieWithConcatTailArrayTest;
import org.trie4j.louds.TailLOUDSTrieWithSBVConcatTailArrayTest;
import org.trie4j.louds.TailLOUDSTrieWithSuffixTrieTailArrayTest;
import org.trie4j.patricia.MapPatriciaTrieTest;
import org.trie4j.patricia.MapTailPatriciaTrieWithConcatTailBuilderTest;
import org.trie4j.patricia.MapTailPatriciaTrieWithSuffixTrieTailBuilderTest;
import org.trie4j.patricia.PatriciaTrieTest;
import org.trie4j.patricia.TailPatriciaTrieWithConcatTailBuilderTest;
import org.trie4j.patricia.TailPatriciaTrieWithSuffixTrieTailBuilderTest;
import org.trie4j.tail.ConcatTailArrayTest;
import org.trie4j.tail.builder.SuffixTrieTailBuilderTest;
import org.trie4j.tail.index.ArrayTailIndexTest;
import org.trie4j.tail.index.DenseArrayTailIndexTest;
import org.trie4j.tail.index.SBVTailIndexTest;
import org.trie4j.util.CharsCharSequenceTest;
import org.trie4j.util.FastBitSetTest;
import org.trie4j.util.SuccinctBitVectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AlgorithmsTest.class,
	BitVectorUtilTest.class,
	ConcatTailArrayTest.class,
	CharsCharSequenceTest.class,
	FastBitSetTest.class,
	Rank0OnlySuccinctBitVectorTest.class,
	Rank1OnlySuccinctBitVectorTest.class,
	ArrayTailIndexTest.class,
	DenseArrayTailIndexTest.class,
	SBVTailIndexTest.class,
	SuccinctBitVectorTest.class,
	SuffixTrieTailBuilderTest.class,
	PatriciaTrieTest.class,
	MapPatriciaTrieTest.class,
	TailPatriciaTrieWithConcatTailBuilderTest.class,
	TailPatriciaTrieWithSuffixTrieTailBuilderTest.class,
	MapTailPatriciaTrieWithConcatTailBuilderTest.class,
	MapTailPatriciaTrieWithSuffixTrieTailBuilderTest.class,
	DoubleArrayTest.class,
	MapDoubleArrayTest.class,
	TailDoubleArrayWithConcatTailBuilderTest.class,
	TailDoubleArrayWithSuffixTrieTailBuilderTest.class,
	MapTailDoubleArrayWithConcatTailBuilderTest.class,
	MapTailDoubleArrayWithSuffixTrieTailBuilderTest.class,
	OptimizedTailDoubleArrayWithConcatTailBuilderTest.class,
	OptimizedTailDoubleArrayWithSuffixTrieTailBuilderTest.class,
	LOUDSTrieTest.class,
	TailLOUDSTrieWithConcatTailArrayTest.class,
	TailLOUDSTrieWithSBVConcatTailArrayTest.class,
	TailLOUDSTrieWithSuffixTrieTailArrayTest.class,
	MapTailLOUDSTrieWithConcatTailArrayTest.class,
	MapTailLOUDSTrieWithSBVConcatTailArrayTest.class,
	MapTailLOUDSTrieWithSuffixTrieTailArrayTest.class,
	TailLOUDSPPTrieWithConcatTailArrayTest.class,
	TailLOUDSPPTrieWithSBVConcatTailArrayTest.class,
	TailLOUDSPPTrieWithSuffixTrieTailArrayTest.class,
	MapTailLOUDSPPTrieWithConcatTailArrayTest.class,
	MapTailLOUDSPPTrieWithSBVConcatTailArrayTest.class,
	MapTailLOUDSPPTrieWithSuffixTrieTailArrayTest.class,
})
public class AllTests {
}
