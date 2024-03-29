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

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractTermIdTrieTest<T extends TermIdTrie>
extends AbstractImmutableTrieTest<T>{
	@Test
	public void test_termId() throws Exception{
		String[] words = {"hello", "world", "apple", "banana", "strawbelly"};
		TermIdTrie t = trieWithWords(words);
		Set<Integer> ids = new HashSet<Integer>();
		for(String w : words){
			ids.add(t.getTermId(w));
		}
		Assert.assertEquals(words.length, ids.size());
		for(String w : words){
			ids.remove(t.getTermId(w));
		}
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void test_childNode() throws Throwable{
		TermIdTrie tit = trieWithWords("ab", "ac");
		TermIdNode root = tit.getRoot();
		Assert.assertNull(root.getChild('b'));
		TermIdNode a = root.getChild('a');
		Assert.assertNotNull(a);
		Assert.assertNotNull(a.getChild('b'));
		Assert.assertNull(a.getChild('b').getChild('c'));
		Assert.assertNotNull(a.getChild('c'));
		Assert.assertNull(a.getChild('d'));
	}
}
