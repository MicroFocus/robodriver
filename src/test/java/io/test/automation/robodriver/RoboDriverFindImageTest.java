package io.test.automation.robodriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RoboDriverFindImageTest {

	private static RemoteWebDriver browser;
	private static TestUtil util;
	private static WebElement clickInfo;
	
	private static WebElement testImageInWebPage;
	
	/**
	 * Same image as {@link #testImageInWebPage}
	 */
	private static File testImageToFind;
	private static File testImageNotToFind; 
	
	private RoboDriver robo;

	@BeforeClass
	public static void onBeforeClass() throws IOException {
		util = new TestUtil();
		browser = util.startChrome();
		util.navigateToTestPage(browser);
		
		clickInfo = browser.findElementById("outputs");
		assertTrue("click info must be empty", clickInfo.getAttribute("value").isEmpty());
		
		testImageInWebPage = browser.findElementById("testimage");
		testImageToFind = new File(RoboDriverFindImageTest.class.getClassLoader().getResource("test_image_1.png").getFile());
		testImageNotToFind = new File(RoboDriverFindImageTest.class.getClassLoader().getResource("test_image_2.png").getFile());
	}
	
	@AfterClass
	public static void onAfterClass() {
		if (browser != null) {
			browser.quit();
			util.stopServices();
		}
	}
	
	@Before
	public void onBeforeTest() throws IOException {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		robo = new RoboDriver(roboCapabilities);
	}
	
	@Test
	public void testFindImageOnScreenByUri() throws IOException {
		RoboDriverUtil roboUtil = new RoboDriverUtil();
		Rectangle expectedImageRect = roboUtil.getScreenRectangleOfBrowserElement(testImageInWebPage);
		
		// when
		WebElement foundImageRectangle = robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@img='%s']", 
						testImageToFind.toURI()));
		
		// then
		assertEquals(toString(expectedImageRect), toString(foundImageRectangle.getRect()));
	}

	@Test
	public void testClickToCenterOfImageOnScreenByUri() throws IOException {
		// when
		WebElement foundImageRectangle = robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@img='%s']", 
						testImageToFind.toURI()));
		foundImageRectangle.click();
		
		// then
		assertEquals("image click position (10,10)", clickInfo.getAttribute("value").trim());
	}
	
	@Test (expected = WebDriverException.class) // TODO fix exception type, should be NoSuchElementException
	public void testCannotFindImageOnScreenByUri() throws IOException {
		robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@img='%s']", 
						testImageNotToFind.toURI()));
	}
	
	private String toString(Rectangle r) {
		return String.format("x=%d, y=%d, width=%d, height=%d", r.x, r.y, r.width, r.height);
	}
}
