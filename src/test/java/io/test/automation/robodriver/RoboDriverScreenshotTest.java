package io.test.automation.robodriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.test.automation.robodriver.internal.RoboUtil;

public class RoboDriverScreenshotTest {

	private static Point casp; // click area screen position of left upper corner
	private static RemoteWebDriver browser;
	private static TestUtil util;
	
	private RoboDriver robo;

	@BeforeClass
	public static void onBeforeClass() throws IOException {
		util = new TestUtil();
		browser = util.startChrome();
		util.navigateToTestPage(browser);
		WebElement clickArea = browser.findElementById("clickarea");
		casp = new RoboDriverUtil().getScreenRectangleOfBrowserElement(clickArea).getPoint();
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
	public void testScreenshot() throws IOException {
		// when
		File screenshotFile = robo.getScreenshotAs(OutputType.FILE);
		
		// then
		BufferedImage screenshot = ImageIO.read(screenshotFile);
		Color color = new Color(screenshot.getRGB(casp.x + 100, casp.y + 100));
		assertEquals(0x00, color.getRed());
		assertEquals(0x00, color.getGreen());
		assertEquals(0xFF, color.getBlue());
	}
	
	@Test
	public void testScreenshotFromScreenX() throws IOException {
		WebElement screen = robo.findElementByXPath("/screen[0]");
				
		// when
		File screenshotFile = screen.getScreenshotAs(OutputType.FILE);
		
		// then
		BufferedImage screenshot = ImageIO.read(screenshotFile);
		Color color = new Color(screenshot.getRGB(casp.x + 100, casp.y + 100));
		assertEquals(0x00, color.getRed());
		assertEquals(0x00, color.getGreen());
		assertEquals(0xFF, color.getBlue());
	}
	
	@Test
	public void testScreenshotRectangle() throws IOException {
		RoboDriverUtil roboUtil = new RoboDriverUtil();
		WebElement testImage = browser.findElementById("testimage");
		Rectangle rect = roboUtil.getScreenRectangleOfBrowserElement(testImage);
		WebElement screenRectElem = robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@dim='%d,%d,%d,%d']", 
						rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
		
		// when
		File screenRectangleFile = screenRectElem.getScreenshotAs(OutputType.FILE);
		
		// then
		assertEqualsImage("test_image_1.png", screenRectangleFile);
	}

	private void assertEqualsImage(String expectedImage, File imageFile) throws IOException {
		File expectedImageFile = new File(this.getClass().getClassLoader().getResource(expectedImage).getFile());
		assertTrue(new RoboUtil().matchImages(expectedImageFile, imageFile));
	}
}
