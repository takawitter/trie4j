package org.trie4j.tail;

public class SBVConcatTailArray extends AbstractTailArray implements TailArray{
	public SBVConcatTailArray(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	protected TailBuilder newTailBuilder(StringBuilder tails) {
		return new ConcatTailBuilder(tails);
	}
	@Override
	protected TailIndex newTailIndex(int initialCapacity) {
		return new SBVTailIndex(initialCapacity);
	}
}
