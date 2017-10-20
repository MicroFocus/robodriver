package io.test.automation.robodriver.remote;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import org.junit.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.test.automation.robodriver.RoboDriver;
import io.test.automation.robodriver.RoboDriverUtil;
import io.test.automation.robodriver.TestUtil;

/**
 * Tests remote robodriver usage.
 * RoboDriver must be hosted in a Selenium server instance, see how to start Selenium server in README.md.
 */
public class RoboDriverServerTest {
	
	private static String serverURL = System.getProperty("robodriver.test.server.url", "http://localhost:4444/wd/hub");
	private static URL remoteAddress;

	private static TestUtil util;
	private static RemoteWebDriver browser;
	private static Point casp; // click area screen position of left upper corner

	private static RemoteWebDriver robo;

	private static boolean serverRunning;

	private static boolean aliveCheck(URL remoteAddress) throws IOException {
		try (Socket s = new Socket()) {
		  s.connect(new InetSocketAddress(remoteAddress.getHost(), remoteAddress.getPort()));
		  return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	@BeforeClass
	public static void onBeforeClass() throws IOException {
		remoteAddress = new URL(serverURL);
		if (!(serverRunning = aliveCheck(remoteAddress))) {
			return;
		}
		// Start browser used for testing RoboDriver.
		util = new TestUtil();
		browser = util.startChrome(new URL(serverURL));
		util.navigateToTestPage(browser);
		WebElement clickArea = browser.findElementById("clickarea");
		casp = new RoboDriverUtil().getScreenRectangleOfBrowserElement(clickArea).getPoint();
		
		WebElement testInputOutputField = browser.findElementById("outputs");
		assertTrue("test io info must be empty", testInputOutputField.getAttribute("value").isEmpty());
	}

	@Before
	public void onBeforeTest() throws IOException {
		if (!serverRunning) {
			return;
		}		
		util.clearInfoTextField(browser);
		
		// Create a RemoteWebDriver instance to use a remote RoboDriver Selnium server.
		robo = new RemoteWebDriver(remoteAddress, RoboDriver.getDesiredCapabilities());
	}

	@After
	public void onAfterTest() {
		if (robo != null) {
			robo.quit();
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
	
	@Test
	public void testSendShiftKeyActions() throws Exception {
		Assume.assumeTrue(getServerNotRunningInfoText(), serverRunning);
		WebElement textInputField = browser.findElementById("outputs");
		textInputField.click(); // set focus to input field

		// when
		new Actions(robo)
			.keyDown(Keys.SHIFT)
			.sendKeys("hello")
			.keyUp(Keys.SHIFT)
			.perform();

		// then
		assertEquals("HELLO", textInputField.getAttribute("value"));
	}

	@Test
	public void testMixedKeyMouseActions() throws Exception {
		Assume.assumeTrue(getServerNotRunningInfoText(), serverRunning);
		WebElement textInputField = browser.findElementById("outputs");
		textInputField.click(); // set focus to input field
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		new Actions(robo)
			.keyDown(Keys.SHIFT)
			.sendKeys("hello")
			.keyUp(Keys.SHIFT)
			.sendKeys(Keys.RETURN)
			.moveToElement(screen, casp.getX() + 100, casp.getY() + 100)
			.click()
			.perform();
		
		// then
		assertEquals("HELLO\nmouse move: from (100,100) to (100,100)\nclick pos: 100,100", textInputField.getAttribute("value").trim());
	}
	
	private String getServerNotRunningInfoText() {
		return format("Ignored, server '%s' not connectable.", serverURL.toString());
	}	
	
}
