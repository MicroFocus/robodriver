package io.test.automation.robodriver.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.sun.glass.events.KeyEvent;

public class RoboUtilTest {

	@Test
	public void testKeyMappingBasics() {
		char A = 'A';
		assertEquals((Integer)(int)A, RoboUtil.getVirtualKeyCode(A));
		char c1 = '.';
		assertEquals((Integer)(int)c1, RoboUtil.getVirtualKeyCode(c1));
		char c2 = '-';
		assertEquals((Integer)(int)c2, RoboUtil.getVirtualKeyCode(c2));
		
		assertEquals((Integer)KeyEvent.VK_NUMPAD0, RoboUtil.getVirtualKeyCode(Keys.NUMPAD0.charAt(0)));
	}
	
	@Test
	public void testSeleniumKeys() {
		for (Keys k: Keys.values()) {
			assertNotNull("selenium key: " + k.name(), RoboUtil.getVirtualKeyCode(k.charAt(0)));
		}
	}
}

