package org.trie4j.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.trie4j.Trie;

public class WikipediaTitles implements Iterable<String>{
	public WikipediaTitles(String gzFilePath) throws IOException{
		if(!new File(gzFilePath).exists()) throw new FileNotFoundException(gzFilePath);
		this.path = gzFilePath;
	}

	public WikipediaTitles() throws IOException{
		String gzFilePath = "data/" + IOUtil.readLine("data/wiki");
		if(!new File(gzFilePath).exists()) throw new FileNotFoundException(gzFilePath);
		this.path = gzFilePath;
	}

	public <T extends Trie> T insertTo(T trie){
		for(String s : this){
			trie.insert(s);
		}
		return trie;
	}

	public void assertAllContains(Trie trie){
		for(String s : this){
			Assert.assertTrue(trie.contains(s));
		}
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			private String next;
			private BufferedReader reader;
			private NoSuchElementException exception;
			{
				try{
					reader = new BufferedReader(new InputStreamReader(
							new GZIPInputStream(new FileInputStream(path)), "UTF-8"));
					fetch();
				} catch(IOException e){
					exception = new NoSuchElementException();
					exception.initCause(e);
				}
			}
			@Override
			public boolean hasNext() {
				if(next != null) return true;
				if(exception != null) return false;
				try{
					fetch();
				} catch(IOException e){
					exception = new NoSuchElementException();
					exception.initCause(e);
					return false;
				}
				return next != null;
			}
			@Override
			public String next() {
				if(exception != null){
					throw exception;
				}
				if(next == null){
					if(!hasNext()){
						exception = new NoSuchElementException();
						throw exception;
					}
				}
				String ret = next;
				next = null;
				return ret;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			private void fetch() throws IOException{
				if(reader == null) return;
				try{
					while((next = reader.readLine()) != null){
						next = next.trim();
						if(next.length() > 0) break;
					}
				} catch(IOException e){
					reader.close();
					reader = null;
					throw e;
				}
				if(next == null){
					reader.close();
					reader = null;
				}
			}
		};
	}

	private String path;
}
