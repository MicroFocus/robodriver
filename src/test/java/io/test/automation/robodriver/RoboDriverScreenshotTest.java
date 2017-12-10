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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

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
		
		WebElement clickInfo = browser.findElementById("outputs");
		assertTrue("click info must be empty", clickInfo.getAttribute("value").isEmpty());
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
}
