package org.trie4j.louds;

import org.trie4j.bv.BytesSuccinctBitVector;
import org.trie4j.test.LapTimer;

public class MonitoredSuccinctBitVector extends BytesSuccinctBitVector{
	public MonitoredSuccinctBitVector() {
		super();
	}
	public MonitoredSuccinctBitVector(int bitSize){
		super(bitSize);
	}

	public void resetCounts(){
		select0Count = 0;
		select0Time = 0;
		select1Count = 0;
		select1Time = 0;
		next0Count = 0;
		next0Time = 0;
		rank0Count = 0;
		rank0Time = 0;
		rank1Count = 0;
		rank1Time = 0;
	}
	public int getNext0Count() {
		return next0Count;
	}
	public long getNext0Time() {
		return next0Time;
	}
	public int getSelect0Count() {
		return select0Count;
	}
	public long getSelect0Time() {
		return select0Time;
	}
	public int getSelect1Count() {
		return select1Count;
	}
	public long getSelect1Time() {
		return select1Time;
	}
	public int getRank0Count() {
		return rank0Count;
	}
	public long getRank0Time() {
		return rank0Time;
	}
	public int getRank1Count() {
		return rank1Count;
	}
	public long getRank1Time() {
		return rank1Time;
	}

	@Override
	public int select0(int count) {
		select0Count++;
		t.reset();
		try{
			return super.select0(count);
		} finally{
			select0Time += t.lapMillis();
		}
	}
	@Override
	public int select1(int count) {
		select1Count++;
		t.reset();
		try{
			return super.select1(count);
		} finally{
			select1Time += t.lapMillis();
		}
	}
	@Override
	public int next0(int pos) {
		next0Count++;
		t.reset();
		try{
			return super.next0(pos);
		} finally{
			next0Time += t.lapMillis();
		}
	}
	@Override
	public int rank0(int pos) {
		rank0Count++;
		t.reset();
		try{
			return super.rank0(pos);
		} finally{
			rank0Time += t.lapMillis();
		}
	}
	@Override
	public int rank1(int pos) {
		rank1Count++;
		t.reset();
		try{
			return super.rank1(pos);
		} finally{
			rank1Time += t.lapMillis();
		}
	}

	private LapTimer t = new LapTimer();
	private int select0Count;
	private long select0Time;
	private int select1Count;
	private long select1Time;
	private int next0Count;
	private long next0Time;
	private int rank0Count;
	private long rank0Time;
	private int rank1Count;
	private long rank1Time;

	private static final long serialVersionUID = -1336792059094207726L;
}
