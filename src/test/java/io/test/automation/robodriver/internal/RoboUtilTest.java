package io.test.automation.robodriver.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.event.KeyEvent;

import org.junit.Test;
import org.openqa.selenium.Keys;


public class RoboUtilTest {
	
	private RoboUtil roboUtil = new RoboUtil();

	@Test
	public void testKeyMappingBasics() {
		char A = 'A';
		assertEquals((Integer)(int)A, roboUtil.getVirtualKeyCode(A));
		char c1 = '.';
		assertEquals((Integer)(int)c1, roboUtil.getVirtualKeyCode(c1));
		char c2 = '-';
		assertEquals((Integer)(int)c2, roboUtil.getVirtualKeyCode(c2));
		
		assertEquals((Integer)KeyEvent.VK_ESCAPE, roboUtil.getVirtualKeyCode(Keys.ESCAPE.charAt(0)));
		assertEquals((Integer)KeyEvent.VK_CANCEL, roboUtil.getVirtualKeyCode(Keys.CANCEL.charAt(0)));
		assertEquals((Integer)KeyEvent.VK_NUMPAD0, roboUtil.getVirtualKeyCode(Keys.NUMPAD0.charAt(0)));
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("mac")) {
			assertEquals((Integer)KeyEvent.VK_META, roboUtil.getVirtualKeyCode(Keys.COMMAND.charAt(0)));
		} else {
			assertEquals((Integer)KeyEvent.VK_WINDOWS, roboUtil.getVirtualKeyCode(Keys.COMMAND.charAt(0)));
		}		
	}
	
	@Test
	public void testSeleniumKeys() {
		for (Keys k: Keys.values()) {
			assertNotNull("selenium key: " + k.name(), roboUtil.getVirtualKeyCode(k.charAt(0)));
		}
	}
	
	@Test
	public void testVirtualKeyCode() {
		assertEquals((Integer)KeyEvent.VK_A, roboUtil.getVirtualKeyCode('a'));

		assertEquals((Integer)KeyEvent.VK_A, roboUtil.getVirtualKeyCode('A'));
		
		Integer keyCode = roboUtil.getVirtualKeyCode('@');
		assertEquals((Integer)KeyEvent.VK_AT, keyCode);
	}
	
	@Test
	public void testGetVirtualKeyCharByName() {
		int ki = KeyEvent.VK_DEAD_ACUTE;
		char kc = (char)ki;
		CharSequence vk = roboUtil.getVirtualKeyCharSeq("VK_DEAD_ACUTE");
		assertEquals(""+kc, vk);
		assertEquals(kc, vk.charAt(0));
		assertEquals((Integer)ki, roboUtil.getVirtualKeyCode(kc));
		
		assertEquals("H", roboUtil.getVirtualKeyCharSeq("VK_H"));
		assertEquals("E", roboUtil.getVirtualKeyCharSeq("VK_E"));
		assertEquals("L", roboUtil.getVirtualKeyCharSeq("VK_L"));
		assertEquals("L", roboUtil.getVirtualKeyCharSeq("VK_L"));
		assertEquals("O", roboUtil.getVirtualKeyCharSeq("VK_O"));
	}
}

