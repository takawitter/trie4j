package org.trie4j.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

public class WikipediaTitles implements Iterable<String>{
	public WikipediaTitles(String gzFilePath) throws IOException{
		this.path = gzFilePath;
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
					try{
						reader.close();
					} catch(IOException e2){
					}
				}
			}
			@Override
			public boolean hasNext() {
				if(next != null){
					return true;
				}
				if(exception != null) return false;
				try{
					fetch();
				} catch(IOException e){
					exception = new NoSuchElementException();
					exception.initCause(e);
					try{
						reader.close();
					} catch(IOException e2){
					}
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
				while((next = reader.readLine()) != null){
					next = next.trim();
					if(next.length() > 0) break;
				}
				if(next == null) reader.close();
			}
		};
	}

	private String path;
}
