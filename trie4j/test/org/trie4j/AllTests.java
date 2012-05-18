package org.trie4j;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.trie4j.doublearray.DoubleArrayTest;
import org.trie4j.doublearray.TailCompactionDoubleArrayTest;
import org.trie4j.doublearray.TailDoubleArrayTest;
import org.trie4j.tail.SuffixTrieTailBuilderTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DoubleArrayTest.class, TailCompactionDoubleArrayTest.class
	, TailDoubleArrayTest.class
	, SuffixTrieTailBuilderTest.class
})
public class AllTests {

}
