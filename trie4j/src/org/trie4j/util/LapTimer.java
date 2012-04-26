package org.trie4j.util;

public class LapTimer {
	public LapTimer(){
		prev = System.currentTimeMillis();
	}

	public long lap(){
		long c = System.currentTimeMillis();
		long ret = c - prev;
		prev = c;
		return ret;
	}
	
	public long lapWithMsg(String format){
		long c = System.currentTimeMillis();
		long ret = c - prev;
		prev = c;
		System.out.println(String.format(format, ret));
		return ret;
	}
	
	private long prev;
}
