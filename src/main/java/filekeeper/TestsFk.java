package filekeeper;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class TestsFk {

	@Test
	public void testFk() {
		Path source = Paths.get("c:\\Wallpapers\\");
		Path dest = Paths.get("c:\\bb\\");
		Task task = new Task(source, dest, true);
		task.sync();
	}
}
