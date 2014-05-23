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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.trie4j.doublearray.TailDoubleArray;
import org.trie4j.louds.AbstractTailLOUDSTrie;
import org.trie4j.patricia.TailPatriciaTrie;
import org.trie4j.test.LapTimer;
import org.trie4j.test.WikipediaTitles;

public abstract class AbstractWikipediaSerializeTest{
	protected Trie firstTrie(){
		return new TailPatriciaTrie();
	}
	protected Trie secondTrie(Trie first){
		return first;
	}

	@Test
	public void test() throws Exception{
		WikipediaTitles wt = new WikipediaTitles();
		Trie trie = wt.insertTo(firstTrie());
		trie = secondTrie(trie);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		LapTimer lt = new LapTimer();
		oos.writeObject(trie);
		oos.flush();
		long wd = lt.lapMillis();
		byte[] serialized = baos.toByteArray();
		lt.reset();
		Trie t = (Trie)new ObjectInputStream(new ByteArrayInputStream(serialized))
				.readObject();
		long rd = lt.lapMillis();
		wt.assertAllContains(t);
		System.out.println(String.format(
				"%s%s, size: %d, write(ms): %d, read(ms): %d, verified.",
				trie.getClass().getSimpleName(),
				getTailClassName(trie),
				serialized.length, wd, rd
				));
	}

	static String getTailClassName(Trie trie){
		if(trie instanceof TailPatriciaTrie){
			return "(" + ((TailPatriciaTrie) trie).getTailBuilder().getClass().getSimpleName() + ")";
		} else if(trie instanceof TailDoubleArray){
			return "(unknown)";
		} else if(trie instanceof AbstractTailLOUDSTrie){
			return "(" + ((AbstractTailLOUDSTrie) trie).getTailArray().getClass().getSimpleName() + ")";
		} else{
			return "";
		}
	}
}
