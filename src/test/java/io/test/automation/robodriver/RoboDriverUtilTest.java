package io.test.automation.robodriver;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RoboDriverUtilTest {

	private static RemoteWebDriver browser;
	private static TestUtil util;
	private static WebElement testPageClickArea;
	private static WebElement testPageClickInfo;
	
	private static RoboDriver robo;

	@BeforeClass
	public static void onBeforeClass() throws IOException {
		util = new TestUtil();
		browser = util.startChrome();
		util.navigateToTestPage(browser);
		testPageClickArea = browser.findElementById("clickarea");
		testPageClickInfo = browser.findElementById("outputs");
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		robo = new RoboDriver(roboCapabilities);
	}
	
	@AfterClass
	public static void onAfterClass() {
		if (browser != null) {
			browser.quit();
		}
		if (robo != null) {
			robo.quit();
		}
	}
	
	@Test
	public void testGetScreenRectangleOfBrowserElement() {
		RoboDriverUtil roboDriverUtil = new RoboDriverUtil();
		Rectangle clickAreaScreenRectangle = roboDriverUtil.getScreenRectangleOfBrowserElement(testPageClickArea);
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		new Actions(robo)
			.moveToElement(screen, clickAreaScreenRectangle.getX() + 100, clickAreaScreenRectangle.getY() + 100)
			.click()
			.perform();
		String clickInfoText = testPageClickInfo.getAttribute("value");
		assertTrue("expected click to position (100,100), but was '" + clickInfoText + "'", clickInfoText.contains("100,100"));
	}

}
