package misc;

import org.junit.Assert;
import org.junit.Test;

public class ArrayInheritanceTest {
	@Test
	public void test() throws Exception{
		Integer[] ia = {1, 2, 3};
		Object[] oa = ia;
		Integer[] ia2 = (Integer[])oa;
		Assert.assertEquals((Integer)1, ia2[0]);
	}

	@Test
	public void test2() throws Exception{
		Object[] oa = {1, 2, 3};
		try{
			Integer[] ia2 = (Integer[])oa;
			Assert.assertEquals((Integer)1, ia2[0]);
			Assert.fail();
		} catch(ClassCastException e){
		}
	}
}
