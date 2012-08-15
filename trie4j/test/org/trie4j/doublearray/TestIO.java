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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;
import org.trie4j.Trie;
import org.trie4j.util.CharsetUtil;
import org.trie4j.util.LapTimer;

public class TestIO {
	private static final int maxCount = 2000000;
	
	@Test
	public void testSave() throws Exception{
		// You can download archive from http://dumps.wikimedia.org/jawiki/latest/
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream("data/jawiki-20120220-all-titles-in-ns0.gz"))
//				new GZIPInputStream(new FileInputStream("data/enwiki-20120403-all-titles-in-ns0.gz"))
				, CharsetUtil.newUTF8Decoder()));
		System.out.println("--- building patricia trie ---");
		Trie trie = new org.trie4j.patricia.multilayer.MultilayerPatriciaTrie();
		int c = 0;
		String word = null;
		LapTimer t1 = new LapTimer();
		while((word = r.readLine()) != null){
			trie.insert(word);
			c++;
			if(c == maxCount) break;
		}
		System.out.println("done in " + t1.lap() + " millis.");
		System.out.println(c + "entries in ja wikipedia titles.");

		System.out.println("-- building double array.");
		t1.lap();
		TailDoubleArray da = new TailDoubleArray(trie);
		trie = null;
		System.out.println("done in " + t1.lap() + " millis.");

		OutputStream os = new GZIPOutputStream(new FileOutputStream("da.dat"));
		try{
			System.out.println("-- saving double array.");
			t1.lap();
			da.save(os);
			System.out.println("done in " + t1.lap() + " millis.");
			da.dump();
		} finally{
			os.close();
		}
	}

	@Test
	public void testLoad() throws Exception{
		TailDoubleArray da = new TailDoubleArray();
		LapTimer t = new LapTimer();
		System.out.println("-- loading double array.");
		da.load(new GZIPInputStream(new FileInputStream("da.dat")));
		System.out.println("done in " + t.lap() + " millis.");
		da.dump();
		verify(da);
		System.out.println("---- common prefix search ----");
		System.out.println("-- for 東京国際フォーラム");
		for(String s : da.commonPrefixSearch("東京国際フォーラム")){
			System.out.println(s);
		}
		System.out.println("-- for 大阪城ホール");
		for(String s : da.commonPrefixSearch("大阪城ホール")){
			System.out.println(s);
		}
		System.out.println("---- predictive search ----");
		System.out.println("-- for 大阪城");
		for(String s : da.predictiveSearch("大阪城")){
			System.out.println(s);
		}
		Thread.sleep(10000);
		da.contains("hello");
	}

	private static void verify(Trie da) throws Exception{
		System.out.println("verifying double array...");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream("data/jawiki-20120220-all-titles-in-ns0.gz"))
//				new GZIPInputStream(new FileInputStream("enwiki-20120403-all-titles-in-ns0.gz"))
				, CharsetUtil.newUTF8Decoder()));
		int c = 0;
		int sum = 0;
		String word = null;
		LapTimer t1 = new LapTimer();
		LapTimer t = new LapTimer();
		while((word = r.readLine()) != null){
			if(c == maxCount) break;
			t.lap();
			boolean found = da.contains(word);
			sum += t.lap();
			c++;
			if(!found){
				System.out.println("verification failed.  trie not contains " + c + " th word: [" + word + "]");
				break;
			}
		}
		System.out.println("done " + c + "words in " + t1.lap() + " millis.");
		System.out.println("contains time: " + sum + " millis.");
	}
}
