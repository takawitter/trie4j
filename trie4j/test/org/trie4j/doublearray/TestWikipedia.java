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
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.trie4j.Trie;
import org.trie4j.util.CharsetUtil;
import org.trie4j.util.LapTimer;

public class TestWikipedia {
	private static final int maxCount = 2000000;

	public static void main(String[] args) throws Exception{
		System.out.println("--- recursive patricia trie ---");
		Trie trie = new org.trie4j.patricia.multilayer.MultilayerPatriciaTrie();
		int c = 0;
		// You can download archive from http://dumps.wikimedia.org/jawiki/latest/
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream("jawiki-20120220-all-titles-in-ns0.gz"))
				, CharsetUtil.newUTF8Decoder()));
		String word = null;
		System.gc();
		Thread.sleep(1000);
		System.out.println(Runtime.getRuntime().freeMemory() + " bytes free.");

		long sum = 0;
		LapTimer t1 = new LapTimer();
		LapTimer t2 = new LapTimer();
		while((word = r.readLine()) != null){
			t2.lap();
			trie.insert(word);
			sum += t2.lap();
			if(c % 10000 == 0){
				long free = Runtime.getRuntime().freeMemory();
				System.out.println(
						c + "," + free + "," + Runtime.getRuntime().maxMemory() + "," + t1.lap()
						);
			}
			c++;
			if(c == maxCount) break;
		}
		System.out.println(c + "entries in ja wikipedia titles.");
		System.out.println("insert time: " + sum + " millis.");

		System.out.println("-- insert done.");
		System.gc();
		Thread.sleep(1000);
		System.out.println(Runtime.getRuntime().freeMemory() + " bytes free.");

		System.out.println("-- building double array.");
		t1.lap();
		DoubleArray da = new DoubleArray(trie);
		System.out.println("-- done in " + t1.lap() + " millis.");
		da.dump();

		verify(da);
	}

	private static void verify(DoubleArray da) throws Exception{
		System.out.println("verifying double array...");
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new GZIPInputStream(new FileInputStream("jawiki-20120220-all-titles-in-ns0.gz"))
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
			if(!found){
				System.out.println("trie not contains [" + word + "]");
				break;
			}
			if(c % 100000 == 0){
				System.out.println(c + " elements done.");
			}
			c++;
		}
		System.out.println("done in " + t1.lap() + " millis.");
		System.out.println("contains time: " + sum + " millis.");

		final DoubleArray d = da;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100000);
					d.contains("hello");
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}
}
