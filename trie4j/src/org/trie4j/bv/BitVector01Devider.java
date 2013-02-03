package org.trie4j.bv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitVector01Devider {
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

	public void load(InputStream is) throws IOException{
		DataInputStream dis = new DataInputStream(is);
		first = dis.readBoolean();
		zeroCounting = dis.readBoolean();
	}

	public void save(OutputStream os) throws IOException{
		DataOutputStream dos = new DataOutputStream(os);
		try{
			dos.writeBoolean(first);
			dos.writeBoolean(zeroCounting);
		} finally{
			dos.flush();
		}
	}

	private BitVector r0;
	private BitVector r1;
	private boolean first = true;
	private boolean zeroCounting;
}
