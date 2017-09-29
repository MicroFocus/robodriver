package io.test.automation.robodriver.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.event.KeyEvent;

import org.junit.Test;
import org.openqa.selenium.Keys;


public class RoboUtilTest {

	@Test
	public void testKeyMappingBasics() {
		char A = 'A';
		assertEquals((Integer)(int)A, RoboUtil.getVirtualKeyCode(A));
		char c1 = '.';
		assertEquals((Integer)(int)c1, RoboUtil.getVirtualKeyCode(c1));
		char c2 = '-';
		assertEquals((Integer)(int)c2, RoboUtil.getVirtualKeyCode(c2));
		
		assertEquals((Integer)KeyEvent.VK_ESCAPE, RoboUtil.getVirtualKeyCode(Keys.ESCAPE.charAt(0)));
		assertEquals((Integer)KeyEvent.VK_CANCEL, RoboUtil.getVirtualKeyCode(Keys.CANCEL.charAt(0)));
		assertEquals((Integer)KeyEvent.VK_NUMPAD0, RoboUtil.getVirtualKeyCode(Keys.NUMPAD0.charAt(0)));
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("mac")) {
			assertEquals((Integer)KeyEvent.VK_META, RoboUtil.getVirtualKeyCode(Keys.COMMAND.charAt(0)));
		} else {
			assertEquals((Integer)KeyEvent.VK_WINDOWS, RoboUtil.getVirtualKeyCode(Keys.COMMAND.charAt(0)));
		}		
	}
	
	@Test
	public void testSeleniumKeys() {
		for (Keys k: Keys.values()) {
			assertNotNull("selenium key: " + k.name(), RoboUtil.getVirtualKeyCode(k.charAt(0)));
		}
	}
	
	@Test
	public void testVirtualKeyCode() {
		assertEquals((Integer)KeyEvent.VK_A, RoboUtil.getVirtualKeyCode('a'));

		assertEquals((Integer)KeyEvent.VK_A, RoboUtil.getVirtualKeyCode('A'));
		
		Integer keyCode = RoboUtil.getVirtualKeyCode('@');
		System.out.println(Integer.toBinaryString(keyCode));
		assertEquals((Integer)KeyEvent.VK_AT, keyCode);
	}
	
	@Test
	public void testGetVirtualKeyCharByName() {
		assertEquals("H", RoboUtil.getVK("VK_H"));
		assertEquals("E", RoboUtil.getVK("VK_E"));
		assertEquals("L", RoboUtil.getVK("VK_L"));
		assertEquals("L", RoboUtil.getVK("VK_L"));
		assertEquals("O", RoboUtil.getVK("VK_O"));
	}
}

