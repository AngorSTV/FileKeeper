import filekeeper.Progress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestProgress {

	Progress p	= new Progress();

	@Before
	public void setupClass() {

	}

	@Test
	public void testGetCarentPersent() {
		p.setMaxValue(10240);
		p.addProgress(2300);
		assertEquals("22 %", p.getCarentPersent());
	}
}
