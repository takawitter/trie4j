package org.trie4j.louds.bvtree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.trie4j.util.Range;

public interface BvTree {
	void appendChild();
	void appendSelf();
	void getChildNodeIds(int selfNodeId, Range range);
	void trimToSize();
	void load(InputStream is) throws IOException;
	void save(OutputStream os) throws IOException;
}
