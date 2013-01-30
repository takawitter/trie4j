/*
 * Copyright 2013 Takao Nakaguchi
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
package org.trie4j.bv;

/**
 * Utility class for BitVector.
 * @author Takao Nakaguchi
 */
public class BitVectorUtil {
	public static void divide01(BitVector org, BitVector vec0, BitVector vec1){
		if(org.size() == 0) return;
		if(org.size() == 1){
			if(!org.isZero(0)) throw new IllegalArgumentException("invalid bv for trie");
			vec0.append1();
			return;
		}
		boolean zeroCounting = org.isZero(0);
		int n = org.size();
		for(int i = 1; i < n; i++){
			if(zeroCounting){
				if(org.isZero(i)){
					vec0.append1();
				} else{
					vec0.append0();
					zeroCounting = false;
				}
			} else{
				if(org.isZero(i)){
					vec1.append0();
					zeroCounting = true;
				} else{
					vec1.append1();
				}
			}
		}
	}
}
