package org.trie4j.tail;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractTailArray implements TailArray{
	protected abstract TailBuilder newTailBuilder(StringBuilder tails);
	protected abstract TailIndex newTailIndex(int initialCapacity);

	public AbstractTailArray(int initialCapacity) {
		builder = newTailBuilder(tails);
		index = newTailIndex(initialCapacity);
	}

	@Override
	public int append(char[] letters, int offset, int len) {
		int ret = builder.insert(letters, offset, len);
		index.add(ret, tails.length());
		return ret;
	}

	@Override
	public int appendEmpty() {
		index.addEmpty();
		return -1;
	}

	@Override
	public TailCharIterator newIterator() {
		return new TailCharIterator(tails, -1);
	}

	@Override
	public TailCharIterator newIterator(int offset) {
		return new TailCharIterator(tails, offset);
	}
	
	@Override
	public int getIteratorOffset(int index) {
		return this.index.get(index);
	}

	@Override
	public void trimToSize() {
		tails.trimToSize();
		index.trimToSize();
	}

	@Override
	public void freeze() {
		builder = null;
	}

	@Override
	public void load(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		int n = dis.readInt();
		tails = new StringBuilder(n);
		for(int i = 0; i < n; i++){
			tails.append(dis.readChar());
		}
		builder = newTailBuilder(tails);
		index = newTailIndex(0);
		index.load(is);
	}

	@Override
	public void save(OutputStream os) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		try{
			int n = tails.length();
			dos.writeInt(n);
			for(int i = 0; i < n; i++){
				dos.writeChar(tails.charAt(i));
			}
		} finally{
			dos.flush();
		}
		index.save(os);
	}

	private StringBuilder tails = new StringBuilder();
	private TailBuilder builder;
	private TailIndex index;
}
