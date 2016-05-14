import filekeeper.Task;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestsFk {

	@Test
	public void testFk() {
		Path source = Paths.get("c:\\Wallpapers\\");
		Path dest = Paths.get("c:\\bb\\");
		Task task = new Task(source, dest, true);
		task.sync();
	}
}
