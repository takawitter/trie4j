package org.trie4j.tail;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.trie4j.patricia.tail.TailPatriciaTrie;
import org.trie4j.tail.builder.ConcatTailBuilder;
import org.trie4j.test.WikipediaTitles;

public class CreateTail {
	public static void main(String[] args) throws Exception{
		ConcatTailBuilder tb = new ConcatTailBuilder();
		TailPatriciaTrie trie = new TailPatriciaTrie(tb);
		for(String s : new WikipediaTitles("data/jawiki-20120220-all-titles-in-ns0.gz")){
			trie.insert(s);
		}
		OutputStream os = new FileOutputStream("data/jawiki-20120220-tail");
		try{
			CharSequence seq = tb.getTails();
			byte[] bytes = seq.toString().getBytes("UTF16");
			System.out.println(seq.length() + "chars.");
			System.out.println(bytes.length + "bytes.");
			os.write(bytes);
		} finally{
			os.close();
		}
	}
}
