package issues;

import org.junit.Test;
import org.trie4j.MapTrie;
import org.trie4j.patricia.MapTailPatriciaTrie;
import org.trie4j.util.TrieMap;

public class Issue_047 {
	@Test
	public void test() throws Throwable{
		MapTrie<String> mp = new MapTailPatriciaTrie<>();
		mp.insert("hello", "world");
		TrieMap<String> tm = new TrieMap<String>(mp);
		// this causes java.lang.ClassCastException
		tm.entrySet();
	}

}
