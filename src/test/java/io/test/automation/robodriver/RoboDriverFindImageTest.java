package io.test.automation.robodriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
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
		testImageToFind = new File(
				RoboDriverFindImageTest.class.getClassLoader().getResource("test_image_1.png").getFile());
		testImageNotToFind = new File(
				RoboDriverFindImageTest.class.getClassLoader().getResource("test_image_2.png").getFile());
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
	public void testFindImageOnScreenByFileUri() throws IOException {
		RoboDriverUtil roboUtil = new RoboDriverUtil();
		Rectangle expectedImageRect = roboUtil.getScreenRectangleOfBrowserElement(testImageInWebPage);

		// when
		WebElement foundImageRectangle = robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@img='%s']", testImageToFind.toURI()));

		// then
		assertEquals(util.toString(expectedImageRect), util.toString(foundImageRectangle.getRect()));
	}

	@Test
	public void testFindImageOnScreenByDataUri() throws IOException {
		RoboDriverUtil roboUtil = new RoboDriverUtil();
		Rectangle expectedImageRect = roboUtil.getScreenRectangleOfBrowserElement(testImageInWebPage);

		// when (data URI represents image: scr/test/resources/test_image_1.png
		WebElement foundImageRectangle = robo.findElementByXPath(String.format(
				"//screen[@default=true]//rectangle[@img='%s']",
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAClSURBVDhPpdFbDoUgDEXR4zz0T+c/Mp2CeoglRUupsBMvmktXfEwAzrt7ASZedfSM41i2rHEZap/XtGZpBBWMFU4PWmKG8QfVGDNBFkHfGKuCzEMtjLkgs/6sYawJMr3Bw1gIZNzUwlgYJBbZGAL1nbU2N0HrMb0BF/TeWW2oCsY+wHfQBCOY9B7+gH8wSQMJfM67MKlA+TOCSYJCY+mWOw4JwHkBGJ9CpvqlAE0AAAAASUVORK5CYII="));

		// then
		assertEquals(util.toString(expectedImageRect), util.toString(foundImageRectangle.getRect()));
	}

	@Test
	public void testFindImageWithinRectangle() throws IOException {
		RoboDriverUtil roboUtil = new RoboDriverUtil();
		Rectangle expectedImageRect = roboUtil.getScreenRectangleOfBrowserElement(testImageInWebPage);
		WebElement testImage = browser.findElementById("testimage");
		Rectangle rect = roboUtil.getScreenRectangleOfBrowserElement(testImage);
		WebElement rectangle = robo
				.findElementByXPath(String.format("//screen[@default=true]//rectangle[@dim='%d,%d,%d,%d']",
						rect.getX() - 10, rect.getY() - 10, rect.getWidth() + 20, rect.getHeight() + 20));

		// when
		WebElement foundImageRectangle = rectangle.findElement(By.xpath(String.format("//rectangle[@img='%s']",
				"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAClSURBVDhPpdFbDoUgDEXR4zz0T+c/Mp2CeoglRUupsBMvmktXfEwAzrt7ASZedfSM41i2rHEZap/XtGZpBBWMFU4PWmKG8QfVGDNBFkHfGKuCzEMtjLkgs/6sYawJMr3Bw1gIZNzUwlgYJBbZGAL1nbU2N0HrMb0BF/TeWW2oCsY+wHfQBCOY9B7+gH8wSQMJfM67MKlA+TOCSYJCY+mWOw4JwHkBGJ9CpvqlAE0AAAAASUVORK5CYII=")));

		// then
		assertEquals(util.toString(expectedImageRect), util.toString(foundImageRectangle.getRect()));
	}

	@Test
	public void testClickToCenterOfImageOnScreenByUri() throws IOException {
		// when
		WebElement foundImageRectangle = robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@img='%s']", testImageToFind.toURI()));
		foundImageRectangle.click();

		// then
		assertEquals("image click position (10,10)", clickInfo.getAttribute("value").trim());
	}

	@Test(expected = WebDriverException.class) // TODO fix exception type, should be NoSuchElementException
	public void testCannotFindImageOnScreenByUri() throws IOException {
		robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@img='%s']", testImageNotToFind.toURI()));
	}
}
