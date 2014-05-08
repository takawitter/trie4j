package org.trie4j.util;

import java.io.Serializable;
import java.util.Arrays;

import org.trie4j.bv.Rank0OnlySuccinctBitVector;

public class SBVIntMap<T> implements Serializable{
	public SBVIntMap() {
		values = new Object[]{};
		idToValueIndex = new Rank0OnlySuccinctBitVector();
	}

	public SBVIntMap(int initialCapacity){
		values = new Object[]{};
		idToValueIndex = new Rank0OnlySuccinctBitVector(initialCapacity);
	}

	public int addValue(T value){
		idToValueIndex.append0();
		values[current] = value;
		current++;
		return idToValueIndex.size();
	}

	public int addNone(){
		idToValueIndex.append1();
		return idToValueIndex.size();
	}

	@SuppressWarnings("unchecked")
	public T get(int id){
		return (T)values[idToValueIndex.rank0(id) - 1];
	}

	public void set(int id, T value){
		if(!idToValueIndex.isZero(id)) throw new IllegalStateException(
				"try to set value for invalid id.");
		values[idToValueIndex.rank0(id) - 1] = value;
	}

	protected void extend(){
		if(current >= values.length){
			values = Arrays.copyOf(values, (int)Math.ceil(values.length * 1.2));
		}
	}

	private int current;
	private Object[] values;
	private Rank0OnlySuccinctBitVector idToValueIndex = new Rank0OnlySuccinctBitVector();
	private static final long serialVersionUID = -4753279563025571408L;
}
