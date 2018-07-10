package io.test.automation.robodriver.remote;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.test.automation.robodriver.RoboDriver;
import io.test.automation.robodriver.RoboDriverUtil;
import io.test.automation.robodriver.TestUtil;
import io.test.automation.robodriver.internal.RoboUtil;

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

	private static RemoteWebDriver remoteRobo;

	private static boolean serverRunning;
	private static WebElement testImageInWebPage;
	private static WebElement testInputOutputField;

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
		testImageInWebPage = browser.findElementById("testimage");
		
		testInputOutputField = browser.findElementById("outputs");
		assertTrue("test io info must be empty", testInputOutputField.getAttribute("value").isEmpty());
	}

	@Before
	public void onBeforeTest() throws IOException {
		if (!serverRunning) {
			return;
		}		
		util.clearInfoTextField(browser);
		
		// Create a RemoteWebDriver instance to use a remote RoboDriver Selnium server.
		remoteRobo = new RemoteWebDriver(remoteAddress, RoboDriver.getDesiredCapabilities());
	}

	@After
	public void onAfterTest() {
		if (remoteRobo != null) {
			remoteRobo.quit();
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
		WebElement screen = remoteRobo.findElementByXPath("//screen[@default=true]");
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
		new Actions(remoteRobo)
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
		WebElement screen = remoteRobo.findElementByXPath("//screen[@default=true]");
		new Actions(remoteRobo)
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
	
	@Test
	public void testScreenshot() throws IOException {
		Assume.assumeTrue(getServerNotRunningInfoText(), serverRunning);

		// when
		File screenshotFile = remoteRobo.getScreenshotAs(OutputType.FILE);
		
		// then
		BufferedImage screenshot = ImageIO.read(screenshotFile);
		Color color = new Color(screenshot.getRGB(casp.x + 100, casp.y + 100));
		assertEquals(0x00, color.getRed());
		assertEquals(0x00, color.getGreen());
		assertEquals(0xFF, color.getBlue());
	}
	
	@Test
	public void testRectangle() throws IOException {
		Assume.assumeTrue(getServerNotRunningInfoText(), serverRunning);
		
		// when: retrieve remote rectangle x,y,width,height (x,y = position of left upper corner)
		WebElement screenRectangle = remoteRobo.findElementByXPath(
				"//screen[@default=true]//rectangle[@dim='10,20,500,300']"); 
		
		// then
		assertEquals(10, screenRectangle.getLocation().x);
		assertEquals(20, screenRectangle.getLocation().y);
		assertEquals(500, screenRectangle.getSize().width);
		assertEquals(300, screenRectangle.getSize().height);
	}	
	
	@Test
	public void testScreenshotRectangle() throws IOException {
		Assume.assumeTrue(getServerNotRunningInfoText(), serverRunning);
		
		RoboDriverUtil roboUtil = new RoboDriverUtil();
		WebElement testImage = browser.findElementById("testimage");
		Rectangle rect = roboUtil.getScreenRectangleOfBrowserElement(testImage);
		WebElement screenRectangle = remoteRobo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@dim='%d,%d,%d,%d']", 
						rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
		
		// when
		File screenRectangleFile = screenRectangle.getScreenshotAs(OutputType.FILE);
		
		// then
		assertEqualsImage("test_image_1.png", screenRectangleFile);
	}
	
	@Test
	public void testFindImageOnScreenByDataUri() throws IOException {
		RoboDriverUtil roboUtil = new RoboDriverUtil();
		Rectangle expectedImageRect = roboUtil.getScreenRectangleOfBrowserElement(testImageInWebPage);
		
		// when (data URI represents image: scr/test/resources/test_image_1.png
		WebElement foundImageRectangle = remoteRobo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@img='%s']", 
						"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAClSURBVDhPpdFbDoUgDEXR4zz0T+c/Mp2CeoglRUupsBMvmktXfEwAzrt7ASZedfSM41i2rHEZap/XtGZpBBWMFU4PWmKG8QfVGDNBFkHfGKuCzEMtjLkgs/6sYawJMr3Bw1gIZNzUwlgYJBbZGAL1nbU2N0HrMb0BF/TeWW2oCsY+wHfQBCOY9B7+gH8wSQMJfM67MKlA+TOCSYJCY+mWOw4JwHkBGJ9CpvqlAE0AAAAASUVORK5CYII="));
		foundImageRectangle.click();
		
		// then
		assertEquals(util.toString(expectedImageRect), util.toString(foundImageRectangle.getRect()));
		assertEquals("image click position (10,10)", testInputOutputField.getAttribute("value").trim());
	}
	

	private void assertEqualsImage(String expectedImage, File imageFile) throws IOException {
		File expectedImageFile = new File(this.getClass().getClassLoader().getResource(expectedImage).getFile());
		assertTrue(new RoboUtil().matchImages(expectedImageFile, imageFile));
	}
	
	private String getServerNotRunningInfoText() {
		return format("Ignored, server '%s' not connectable.", serverURL.toString());
	}	
	
}
