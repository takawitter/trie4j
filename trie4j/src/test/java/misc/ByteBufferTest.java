package misc;

import java.nio.ByteBuffer;

import org.junit.Test;

public class ByteBufferTest {
	@Test
	public void test() throws Throwable{
		var bb = ByteBuffer.allocateDirect(100);
		System.out.println(bb.limit());
	}
}
