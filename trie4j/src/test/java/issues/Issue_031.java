package issues;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Test;
import org.trie4j.MapTrie;
import org.trie4j.Trie;
import org.trie4j.doublearray.DoubleArray;
import org.trie4j.doublearray.MapDoubleArray;
import org.trie4j.doublearray.MapTailDoubleArray;
import org.trie4j.doublearray.TailDoubleArray;
import org.trie4j.patricia.MapPatriciaTrie;
import org.trie4j.patricia.PatriciaTrie;

public class Issue_031 {
	private void insertLines(Trie trie, String filePath)
	throws IOException{
		try(
				InputStream is = new FileInputStream(filePath);
				Reader r = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(r)){
			String line = null;
			while((line = br.readLine()) != null){
				trie.insert(line);
			}
		}
	}
	private static final String FILE_NAME = "data/Issue_031.txt";

	@Test
	public void test_DoubleArray() throws Throwable{
		Trie trie = new PatriciaTrie();
		insertLines(trie, FILE_NAME);
		Trie da = new DoubleArray(trie);
		Assert.assertFalse(trie.contains("you"));
		Assert.assertFalse(da.contains("you"));
	}

	@Test
	public void test_TailDoubleArray() throws Throwable{
		Trie trie = new PatriciaTrie();
		insertLines(trie, FILE_NAME);
		Trie da = new TailDoubleArray(trie);
		Assert.assertFalse(trie.contains("you"));
		Assert.assertFalse(da.contains("you"));
	}

	@Test
	public void test_UnsafeDoubleArray() throws Throwable{
		Trie trie = new PatriciaTrie();
		insertLines(trie, FILE_NAME);
		@SuppressWarnings("deprecation")
		Trie da = new org.trie4j.doublearray.UnsafeDoubleArray(trie);
		Assert.assertFalse(trie.contains("you"));
		Assert.assertFalse(da.contains("you"));
	}

	@Test
	public void test_MapDoubleArray() throws Throwable{
		MapTrie<Object> mpt = new MapPatriciaTrie<>();
		insertLines(mpt, FILE_NAME);
		MapTrie<Object> da = new MapDoubleArray<>(mpt);
		Assert.assertNull(mpt.get("you"));
		Assert.assertNull(da.get("you"));
	}

	@Test
	public void test_MapTailDoubleArray() throws Throwable{
		MapTrie<Object> mpt = new MapPatriciaTrie<>();
		insertLines(mpt, FILE_NAME);
		MapTrie<Object> da = new MapTailDoubleArray<>(mpt);
		Assert.assertNull(mpt.get("you"));
		Assert.assertNull(da.get("you"));
	}
}
