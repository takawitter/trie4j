/*
 * Copyright 2012 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trie4j.test;

public class LapTimer {
	public LapTimer(){
		reset();
	}

	public void reset(){
		prev = System.nanoTime();
	}

	public long lapMillis(){
		long c = System.nanoTime();
		long ret = c - prev;
		prev = c;
		return ret / 1000000;
	}

	public long lapNanos(){
		long c = System.nanoTime();
		long ret = c - prev;
		prev = c;
		return ret;
	}

	public long lapMillis(String format, Object... args){
		long ret = lapMillis();
		println("[" + ret + "ms]: " + format, args);
		return ret;
	}

	public long lapNanos(String format, Object... args){
		long ret = lapNanos();
		println("[" + ret + "ns]: " + format, args);
		return ret;
	}

	private void println(String format, Object... args){
		System.out.println(String.format(format, args));
	}

	private long prev;
}
