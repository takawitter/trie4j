package org.trie4j.tail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TailArray {
	int append(char[] letters, int offset, int len);
	int appendEmpty();
	TailCharIterator newIterator(int offset);
	TailCharIterator newIterator();
	int getIteratorOffset(int index);
	void trimToSize();
	void freeze();
	void load(InputStream is) throws IOException;
	void save(OutputStream os) throws IOException;
}
