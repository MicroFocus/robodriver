package io.test.automation.robodriver;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

public class RoboDriverTest {

	@Test
	public void testRoboDriver() {
		RoboDriver roboDriver = new RoboDriver();
		roboDriver.quit();
	}

	@Test
	public void testStartApp() {
		String testAppPath = "C:\\Windows\\System32\\notepad.exe"; // TODO only windows for now, extend for linux, macos
		Assume.assumeTrue("test ignored, app not existing: " + testAppPath, new File(testAppPath).exists());
		
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		roboCapabilities.setCapability("app", testAppPath);
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		assertTrue(roboDriver.getAppProcess().isAlive());
		roboDriver.quit();
	}
	
}
