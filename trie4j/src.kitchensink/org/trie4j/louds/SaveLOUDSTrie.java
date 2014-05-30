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
package org.trie4j.louds;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.trie4j.patricia.TailPatriciaTrie;
import org.trie4j.tail.ConcatTailArray;
import org.trie4j.tail.SBVConcatTailArray;
import org.trie4j.test.WikipediaTitles;

public class SaveLOUDSTrie {
	public static void main(String[] args) throws Exception{
		TailPatriciaTrie trie1 = new TailPatriciaTrie();
		for(String s : new WikipediaTitles("data/jawiki-20120220-all-titles-in-ns0.gz")){
			trie1.insert(s);
		}
		System.out.println(trie1.size() + "nodes.");

		SBVConcatTailArray tailArray = new SBVConcatTailArray(trie1.size());
		TailLOUDSTrie trie = new TailLOUDSTrie(trie1, tailArray);
		System.out.println(trie.size() + "nodes.");
		trie.freeze();

		OutputStream os = new FileOutputStream("louds.dat");
		try{
			ObjectOutputStream oos = new ObjectOutputStream(os);
			trie.writeExternal(oos);
			oos.flush();
		} finally{
			os.close();
		}

		os = new FileOutputStream("louds-bv.dat");
		try{
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(trie.getBvTree());
			oos.flush();
		} finally{
			os.close();
		}

		os = new FileOutputStream("louds-labels.dat");
		try{
			DataOutputStream dos = new DataOutputStream(os);
			for(char c : trie.getLabels()){
				dos.writeChar(c);
			}
			dos.flush();
		} finally{
			os.close();
		}

		os = new FileOutputStream("louds-tails.dat");
		try{
			ObjectOutputStream dos = new ObjectOutputStream(os);
			dos.writeObject(tailArray.getTails());
			dos.flush();
		} finally{
			os.close();
		}

		os = new FileOutputStream("louds-tailIndex.dat");
		try{
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(tailArray.getTailIndex());
			oos.flush();
		} finally{
			os.close();
		}

		os = new FileOutputStream("louds-term.dat");
		try{
			ObjectOutputStream dos = new ObjectOutputStream(os);
			dos.writeObject(trie.getTerm());
			dos.flush();
		} finally{
			os.close();
		}
	}
}
