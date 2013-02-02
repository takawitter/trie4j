package org.trie4j.tail.index;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.trie4j.tail.TailIndex;

public class ArrayTailIndex implements TailIndex{
	public ArrayTailIndex() {
	}

	public ArrayTailIndex(int initialCapacity) {
		tail = new int[initialCapacity];
	}

	@Override
	public void add(int start, int end) {
		ensureCapacity();
		tail[current++] = start;
	}
	
	@Override
	public void addEmpty() {
		ensureCapacity();
		tail[current++] = -1;
	}

	@Override
	public int get(int nodeId) {
		return tail[nodeId];
	}

	@Override
	public void trimToSize() {
		tail = Arrays.copyOf(tail, current);
	}

	@Override
	public void load(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		int sz = dis.readInt();
		tail = new int[sz];
		for(int i = 0; i < sz; i++){
			tail[i] = dis.readInt();
		}
		current = sz;
	}

	@Override
	public void save(OutputStream os) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		try{
			dos.writeInt(current);
			for(int i = 0; i < current; i++){
				dos.writeInt(tail[i]);
			}
		} finally{
			dos.flush();
		}
	}

	private void ensureCapacity(){
		if(current == tail.length){
			tail = Arrays.copyOf(tail, (int)((current + 1) * 1.2));
		}
	}

	private int[] tail = new int[]{};
	private int current;
}
