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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BitVector01Devider implements Externalizable{
	public BitVector01Devider(BitVector r0, BitVector r1){
		this.r0 = r0;
		this.r1 = r1;
	}

	public void append0(){
		if(first){
			firstProc(false);
			return;
		}
		if(zeroCounting){
			r0.append1();
		} else{
			r1.append0();
			zeroCounting = true;
		}
	}

	public void append1(){
		if(first){
			firstProc(true);
			return;
		}
		if(zeroCounting){
			r0.append0();
			zeroCounting = false;
		} else{
			r1.append1();
		}
	}

	private void firstProc(boolean b){
		zeroCounting = !b;
		r0.append0();
		r1.append0();
		first = false;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException {
		first = in.readBoolean();
		zeroCounting = in.readBoolean();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(first);
		out.writeBoolean(zeroCounting);
	}

	private BitVector r0;
	private BitVector r1;
	private boolean first = true;
	private boolean zeroCounting;
}
