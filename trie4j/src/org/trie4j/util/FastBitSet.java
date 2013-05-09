package org.trie4j.util;

import java.io.Serializable;
import java.util.Arrays;

public class FastBitSet implements Serializable{
	public FastBitSet() {
	}

	public FastBitSet(int size){
		bytes = new byte[size];
	}

	public boolean get(int index){
		return (bytes[index / 8] & (0x80 >> (index % 8))) != 0;
	}

	public void set(int index){
		if(index >= bytes.length){
			extend(index);
		}
		bytes[index / 8] |= 0x80 >> (index % 8);
	}

	public void unset(int index){
		if(index >= bytes.length){
			extend(index);
		} else{
			bytes[index / 8] &= ~(0x80 >> (index % 8));
		}
	}

	private void extend(int index){
		bytes = Arrays.copyOf(bytes,
				Math.max(index + 1, (int)(bytes.length * 1.5))
				);
	}

	private byte[] bytes = {};
	private static final long serialVersionUID = -3346250300546707823L;
}
