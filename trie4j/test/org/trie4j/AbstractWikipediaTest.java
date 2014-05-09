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

import java.io.PrintWriter;

import org.junit.Test;
import org.trie4j.Trie;
import org.trie4j.test.LapTimer;
import org.trie4j.test.WikipediaTitles;

public class AbstractWikipediaTest {
	// You can download archive from http://dumps.wikimedia.org/jawiki/latest/
	private static final String wikipediaFilename = "data/jawiki-20140416-all-titles-in-ns0.gz";
//	private static final String wikipediaFilename = "data/enwiki-20120403-all-titles-in-ns0.gz";

	protected Trie createFirstTrie(){
//		return new PatriciaTrie();
		return new org.trie4j.patricia.tail.TailPatriciaTrie(new org.trie4j.tail.builder.ConcatTailBuilder());
	}
	
	protected Trie buildSecondTrie(Trie first) throws Exception{
		return first;
	}

	protected void afterVerification(Trie trie) throws Exception{
	}

	@Test
	public void test() throws Exception{
		Trie trie = createFirstTrie();
		System.out.println("building first trie: " + trie.getClass().getName());
		int c = 0, chars = 0;
		long b = 0;
		LapTimer t = new LapTimer();
		for(String word : new WikipediaTitles(wikipediaFilename)){
			try{
				t.lap();
				trie.insert(word);
				b += t.lap();
			} catch(Exception e){
				System.out.println("exception at " + c + "th word: " + word);
				trie.dump(new PrintWriter(System.out));
				throw e;
			}
			c++;
			chars += word.length();
		}
		System.out.println(String.format("done in %d millis with %d words and %d chars."
				, (b / 1000000), c, chars));

		Trie second = trie;
		try{
			getClass().getDeclaredMethod("buildSecondTrie", Trie.class);
			System.out.print("building second trie: ");
			t.lap();
			second = buildSecondTrie(trie);
			System.out.println(second.getClass().getName());
			System.out.println("done in " + (t.lap() / 1000000) + "millis.");
		} catch(NoSuchMethodException e){}

		System.out.println("verifying trie.");
		long sum = 0;
		c = 0;
		for(String word : new WikipediaTitles(wikipediaFilename)){
			t.lap();
			boolean found = second.contains(word);
			sum += t.lap();
			c++;
			if(!found){
				System.out.println(String.format(
						"verification failed.  trie not contains %d th word: [%s]."
						, c, word));
				break;
			}
		}
		System.out.println("done in " + (sum / 1000000) + " millis with " + c + " words.");
		afterVerification(second);
	}
}
