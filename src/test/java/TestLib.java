import filekeeper.Lib;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TestLib {

	@Test
	public void test() {
		Lib.readXml(Paths.get("tasks.xml"));
	}

	@Test
	public void testIsSameFile() {
		boolean fastMode = true;
		Path source = Paths.get(".classpath");
		Path dest = Paths.get(".classpath");
		boolean actual;

		boolean expected = true;
		// boolean expected2 = false;

		actual = Lib.isSameFile(source, dest, fastMode);
		assertEquals(expected, actual);
	}

}
