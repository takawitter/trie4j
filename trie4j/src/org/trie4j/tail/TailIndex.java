package org.trie4j.tail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TailIndex {
	void add(int start, int end);
	void addEmpty();
	int get(int nodeId);
	void trimToSize();
	void load(InputStream is) throws IOException;
	void save(OutputStream os) throws IOException;
}
