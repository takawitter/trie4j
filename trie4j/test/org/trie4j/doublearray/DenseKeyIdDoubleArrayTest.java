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

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.AbstractTrieTest;
import org.trie4j.Trie;

public class DenseKeyIdDoubleArrayTest extends AbstractTrieTest{
	@Override
	protected DenseKeyIdDoubleArray buildSecondTrie(Trie firstTrie) {
		return new DenseKeyIdDoubleArray(firstTrie);
	}

	@Test
	public void test() throws Exception{
		DenseKeyIdDoubleArray t = buildSecondTrie(trieWithWords("hello", "world"));
		Assert.assertEquals(0, t.getDenseKeyIdFor("hello"));
		Assert.assertEquals(1, t.getDenseKeyIdFor("world"));
	}
}
