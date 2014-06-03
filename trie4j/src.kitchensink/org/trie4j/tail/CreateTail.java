package org.trie4j.tail;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.trie4j.louds.TailLOUDSTrie;
import org.trie4j.patricia.TailPatriciaTrie;
import org.trie4j.test.WikipediaTitles;

public class CreateTail {
	public static void main(String[] args) throws Exception{
		TailPatriciaTrie trie = new TailPatriciaTrie();
		for(String s : new WikipediaTitles("data/jawiki-20120220-all-titles-in-ns0.gz")){
			trie.insert(s);
		}
		ConcatTailArrayBuilder ta = new ConcatTailArrayBuilder(trie.size());
		new TailLOUDSTrie(trie, ta);
		OutputStream os = new FileOutputStream("data/jawiki-20120220-tail");
		try{
/*			CharSequence seq = ta.build().getTails();
			byte[] bytes = seq.toString().getBytes("UTF16");
			System.out.println(seq.length() + "chars.");
			System.out.println(bytes.length + "bytes.");
			os.write(bytes);
*/		} finally{
			os.close();
		}
	}
}
