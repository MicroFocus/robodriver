package io.test.automation.robodriver.remote;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.test.automation.robodriver.RoboDriver;
import io.test.automation.robodriver.TestUtil;

/**
 * Tests remote robodriver usage.
 * RoboDriver must be hosted in a Selenium server instance, see how to start Selenium server in README.md.
 */
public class RoboDriverServerTest {
	
	private static String serverURL = System.getProperty("robodriver.test.server.url", "http://localhost:4444/wd/hub");

	private static TestUtil util;
	private static RemoteWebDriver browser;

	private static RemoteWebDriver robo;

	private static boolean serverRunning;

	@BeforeClass
	public static void onBeforeClass() throws IOException {
		URL remoteAddress = new URL(serverURL);
		if (!(serverRunning = aliveCheck(remoteAddress))) {
			return;
		}
		// Create a RemoteWebDriver instance to use a remote RoboDriver Selnium server.
		robo = new RemoteWebDriver(remoteAddress, RoboDriver.getDesiredCapabilities());

		// Start browser used for testing RoboDriver.
		util = new TestUtil();
		browser = util.startChrome(new URL(serverURL));
		util.navigateToTestPage(browser);
		
		WebElement testInputOutputField = browser.findElementById("outputs");
		assertTrue("test io info must be empty", testInputOutputField.getAttribute("value").isEmpty());
	}

	private static boolean aliveCheck(URL remoteAddress) throws IOException {
		try (Socket s = new Socket()) {
		  s.connect(new InetSocketAddress(remoteAddress.getHost(), remoteAddress.getPort()));
		  return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@AfterClass
	public static void onAfterClass() {
		if (browser != null) {
			browser.quit();
		}
	}

	@Test
	public void testSendKeys() throws Exception {
		Assume.assumeTrue(getServerNotRunningInfoText(), serverRunning);
		WebElement textInputField = browser.findElementById("outputs");
		textInputField.click(); // set focus to input field
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		screen.sendKeys("hello");
		
		// then
		assertEquals("hello", textInputField.getAttribute("value"));
	}

	private String getServerNotRunningInfoText() {
		return format("Ignored, server '%s' not connectable.", serverURL.toString());
	}	
	
}
