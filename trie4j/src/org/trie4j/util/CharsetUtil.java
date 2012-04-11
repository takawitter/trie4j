package org.trie4j.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CharsetUtil {
	public static final Charset UTF_8 = Charset.forName("utf-8");

	public static CharsetDecoder newUTF8Decoder(){
		return UTF_8.newDecoder();
	}
}
