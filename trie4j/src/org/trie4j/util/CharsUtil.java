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
package org.trie4j.util;

public class CharsUtil {
	public static char[] newTerminatedChars(char[] letters){
		char[] l = new char[letters.length + 1];
		System.arraycopy(letters, 0, l, 0, l.length - 1);
		l[l.length - 1] = 0xffff;
		return l;
	}

	public static char[] newTerminatedCharsFrom(char[] org, int from, int to){
		char[] ret = new char[to - from + 1];
		System.arraycopy(org,  from, ret, 0, to - from);
		ret[ret.length - 1] = 0xffff;
		return ret;
	}

	public static char[] revert(char[] values){
		int n = values.length;
		char[] ret = new char[n];
		for(int i = 0; i < n; i++){
			ret[n - i - 1] = values[i];
		}
		return ret;
	}

	public static char[] revertWithoutTerminator(char[] values){
		int n = values.length;
		if(n > 0 && values[n - 1] == 0xffff){
			n--;
		}
		char[] ret = new char[n];
		for(int i = 0; i < n; i++){
			ret[n - i - 1] = values[i];
		}
		return ret;
	}
}
