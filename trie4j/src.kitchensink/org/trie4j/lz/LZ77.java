package org.trie4j.lz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.trie4j.test.LapTimer;

public class LZ77 {
	public static void main(String[] args) throws Exception{
		main1(args);
	}
	public static void main1(String[] args) throws Exception{
		LapTimer lt = new LapTimer();
		String src = "abcabdrz";
		src = read("data/jawiki-20120220-tail");
		int windowSize = 8192;
		StringBuilder dest = new StringBuilder();
		lt.lap();
		compress2(src, dest, windowSize);
		lt.lap("compress done.");
		int l = 0;
		for(int i = 0; i < dest.length() / 3; i++){
			l = Math.max(dest.charAt(i * 3 + 1), l);
		}
		System.out.println("max matched length: " + l);
		dump(dest);

		StringBuilder b = new StringBuilder();
		lt.lap();
		decompress(dest, b);
		lt.lap("decompress done.");
		
		boolean eq = src.equals(b.toString());
		System.out.println(String.format(
				"src: %d, comp: %d(%02.1f%%), decomp: %d, %b",
				src.length(), dest.length(), 1.0 *  dest.length() / src.length() * 100, b.length(), eq));
		for(int i = 0; i < src.length(); i++){
			if(src.charAt(i) != b.charAt(i)){
				System.out.println(String.format(
						"%dth char different [%c:%c]",
						i, src.charAt(i), b.charAt(i)));
				int s = Math.max(i - 5, 0);
				int e = Math.min(i + 5, src.length());
				System.out.println("src: " + src.substring(s, e));
				System.out.println("dec: " + b.substring(s, e));
				break;
			}
		}
	}
	public static void main2(String[] args) throws Exception{
		LapTimer lt = new LapTimer();
		String src = "abcabdrz";
		src = read("data/jawiki-20120220-tail");
		int windowSize = 8192;

		System.out.println("total " + src.length() + " chars. windowSize: " + windowSize);

		StringBuilder dest1 = new StringBuilder();
		lt.lap();
		compress1(src, dest1, windowSize);
		lt.lap("compress1 done.");
		StringBuilder dest2 = new StringBuilder();
		lt.lap();
		compress2(src, dest2, windowSize);
		lt.lap("compress2 done.");
		System.out.println(String.format(
				"src: %d, comp1: %d(%02.1f%%)",
				src.length(), dest1.length(), 1.0 *  dest1.length() / src.length() * 100));
		System.out.println(String.format(
				"src: %d, comp2: %d(%02.1f%%)",
				src.length(), dest2.length(), 1.0 *  dest2.length() / src.length() * 100));
		for(int i = 0; i < Math.min(dest1.length(), dest2.length()); i++){
			if(dest1.charAt(i) != dest2.charAt(i)){
				System.out.println(String.format(
						"%dth char different [%s:%s]",
						i, toString(dest1.charAt(i)), toString(dest2.charAt(i))));
				dump(dest1, dest2);
				break;
			}
		}
	}

	private static String toString(char c){
		if(c < 0x20){
			return String.format("(0x%02x)", (int)c);
		} else{
			return "" + c;
		}
	}
	private static String read(String filename)
	throws IOException{
		InputStream is = new FileInputStream(filename);
		try{
			Reader r = new InputStreamReader(is, "UTF-16");
			char[] buff = new char[is.available() / 2];
			r.read(buff);
			return new String(buff);
		} finally{
			is.close();
		}
	}

	private static void dump(CharSequence... src){
		int n = 3 * 42; // Integer.MAX_VALUE;
		for(CharSequence s : src){
			n = Math.min(n, s.length() / 3);
		}
		int ns = src.length;
		int[] sumchars = new int[ns];
		for(int i = 0; i < n; i += 3){
			for(int j = 0; j < ns; j++){
				int start = src[j].charAt(i);
				int count = src[j].charAt(i + 1);
				char stopchar = src[j].charAt(i + 2);
				System.out.print(String.format(
						"%02d:%02d  %02d:%02d:%-6s  ",
						i / 3, i / 3 + sumchars[j], start, count, toString(stopchar)));
				sumchars[j] += count;
			}
			System.out.println();
		}
	}

	private static void compress1(CharSequence src, Appendable out, int windowSize)
	throws IOException{
		int n = src.length();
		for(int i = 0; i < n; i++){
			char target = src.charAt(i);
			// find longest match
			boolean found = false;
			int start = 0;
			int matchLen = 0;
			char nonMatchChar = 0xff;
			for(int s = Math.max(0, i - windowSize); s < i; s++){
				if(target == src.charAt(s)){
					int len = getMatchedLen(src, s + 1, i + 1, n) + 1;
					if(len > matchLen){
						start = i - s;
						matchLen = len;
						nonMatchChar = (char)0xff;
						if((i + matchLen) < n){
							nonMatchChar = src.charAt(i + matchLen);
						}
					}
					found = true;
				}
			}
			if(found){
				out.append((char)start)
					.append((char)matchLen)
					.append(nonMatchChar);
				i += matchLen;
			} else{
				out.append((char)0x00).append((char)0x00).append(target);
			}
		}
	}

	private static void compress2(CharSequence src, Appendable out, int windowSize)
	throws IOException{
		Map<Character, List<Integer>> startPoss = new HashMap<Character, List<Integer>>();
		int n = src.length();
		for(int i = 0; i < n; i++){
			char target = src.charAt(i);
			// find longest match
			boolean found = false;
			int start = 0;
			int matchLen = 0;
			char nonMatchChar = 0xff;
			List<Integer> poss = startPoss.get(target);
			if(poss != null){
				Iterator<Integer> it = poss.iterator();
				while(it.hasNext()){
					int s = it.next();
					if((i - s) > windowSize){
						it.remove();
						continue;
					}
					int len = getMatchedLen(src, s + 1, i + 1, n) + 1;
					if(len > matchLen){
						start = i - s;
						matchLen = len;
						nonMatchChar = (char)0xff;
						if((i + matchLen) < n){
							nonMatchChar = src.charAt(i + matchLen);
						}
					}
					found = true;
				}
				poss.add(i);
				int jn = Math.min(i + matchLen + 1, n);
				for(int j = i + 1; j < jn; j++){
					List<Integer> p = startPoss.get(src.charAt(j));
					if(p == null){
						p = new LinkedList<Integer>();
						startPoss.put(src.charAt(j), p);
					}
					p.add(j);
				}
			} else{
				poss = new LinkedList<Integer>();
				poss.add(i);
				startPoss.put(target, poss);
			}
			if(found){
				out.append((char)start)
					.append((char)matchLen)
					.append(nonMatchChar);
				i += matchLen;
			} else{
				out.append((char)0x00).append((char)0x00).append(target);
			}
		}
	}

	private static int getMatchedLen(CharSequence src, int i1, int i2, int end){
		int n = Math.min(i2 - i1, end - i2);
		for(int i = 0; i < n; i++){
			if(src.charAt(i1++) != src.charAt(i2++)) return i;
		}
		return 0;
	}

	public static void decompress(CharSequence src, StringBuilder out){
		int n = src.length();
		for(int i = 0; i < n; i += 3){
			int start = src.charAt(i);
			int matchedLen = src.charAt(i + 1);
			char nonMatchChar = src.charAt(i + 2);
			if(start != 0){
				int s = out.length() - start;
				int e = s + matchedLen;
				for(; s < e; s++){
					out.append(out.charAt(s));
				}
			}
			if(nonMatchChar != 0xff){
				out.append(nonMatchChar);
			}
		}
	}
}
