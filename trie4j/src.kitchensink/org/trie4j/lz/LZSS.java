package org.trie4j.lz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.trie4j.test.LapTimer;

public class LZSS {
	public static void main(String[] args) throws Exception{
		LapTimer lt = new LapTimer();
		String src = "abcabdrz";
		src = read("data/jawiki-20120220-tail");
		int windowSize = 8192;
		lt.lap();
		LZSSData ret = compress(src, windowSize);
		lt.lap("compress done. %d elements, %d chars", ret.match.length(), ret.dest.length());

		dump(ret);

		StringBuilder b = new StringBuilder();
		lt.lap();
		decompress(ret, b);
		lt.lap("decompress done.");
		StringBuilder dest = ret.dest;

		int sz = dest.length();
		int bsz = ret.size / 8 + (((ret.size) % 8 == 0) ? 0 : 1);
		boolean eq = src.equals(b.toString());
		System.out.println(String.format(
				"src: %d, comp: %d(%02.1f%%) + %dbytes, decomp: %d, %b",
				src.length(), sz, 1.0 *  sz / src.length() * 100, bsz, b.length(), eq));
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

	public static class LZSSData{
		public LZSSData(BitSet match, StringBuilder dest, int size) {
			this.match = match;
			this.dest = dest;
			this.size = size;
		}
		private BitSet match = new BitSet();
		private StringBuilder dest = new StringBuilder();
		private int size;
	}

	public static LZSSData compress(CharSequence src, int windowSize)
	throws IOException{
		BitSet match = new BitSet();
		StringBuilder out = new StringBuilder();
		int size = 0;
		Map<Character, List<Integer>> startPoss = new HashMap<Character, List<Integer>>();
		int n = src.length();
		for(int i = 0; i < n; i++){
			char target = src.charAt(i);
			// find longest match
			boolean found = false;
			int start = 0;
			int matchLen = 0;
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
					}
					found = true;
				}
				poss.add(i);
				int jn = Math.min(i + matchLen, n);
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
			if(found && matchLen > 1){
				match.set(size);
				out.append((char)start)
					.append((char)matchLen);
				i += matchLen - 1;
			} else{
				match.set(size, false);
				out.append(target);
			}
			size++;
		}
		return new LZSSData(match, out, size);
	}

	public static void decompress(LZSSData src, StringBuilder out){
		int index = 0;
		int n = src.size;
		for(int i = 0; i < n; i++){
			if(src.match.get(i)){
				int start = src.dest.charAt(index++);
				int matchedLen = src.dest.charAt(index++);
				int s = out.length() - start;
				int e = s + matchedLen;
				for(; s < e; s++){
					out.append(out.charAt(s));
				}
			} else{
				out.append(src.dest.charAt(index++));
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

	private static void dump(LZSSData src){
		int index = 0;
		int n = src.match.size();
		for(int i = 0; i < Math.min(n, 42); i++){
			if(src.match.get(i)){
				System.out.println(String.format(
						"%02d %02d:%02d",
						i, (int)src.dest.charAt(index++), (int)src.dest.charAt(index++)
						));
			} else{
				System.out.println(String.format(
						"%02d %s",
						i, toString(src.dest.charAt(index++))
						));
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
}
