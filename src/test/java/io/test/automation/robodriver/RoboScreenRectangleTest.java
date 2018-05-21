package io.test.automation.robodriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RoboScreenRectangleTest {
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
	public void testRectangle() throws IOException {
		// when: retrieve rectangle x,y,width,height (x,y = position of left upper corner)
		WebElement screenRectangle = robo.findElementByXPath(
				"//screen[@default=true]//rectangle[@dim='10,20,500,300']"); 
		
		// then
		assertEquals(10, screenRectangle.getLocation().x);
		assertEquals(20, screenRectangle.getLocation().y);
		assertEquals(500, screenRectangle.getSize().width);
		assertEquals(300, screenRectangle.getSize().height);
	}

	@Test
	public void testRectangleOfScreen() throws IOException {
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");

		// when: retrieve rectangle x,y,width,height (x,y = position of left upper corner)
		WebElement screenRectangle = screen.findElement(By.xpath("rectangle[@dim='10,20,500,300']")); 
		
		// then
		assertEquals(10, screenRectangle.getLocation().x);
		assertEquals(20, screenRectangle.getLocation().y);
		assertEquals(500, screenRectangle.getSize().width);
		assertEquals(300, screenRectangle.getSize().height);
	}	

	@Test
	public void testRectangleClick() throws IOException {
		WebElement clickInfo = browser.findElementById("outputs");
		
		// when: click to center of a rectangle from screen
		WebElement screenRectElem = robo.findElementByXPath(
				String.format("//screen[@default=true]//rectangle[@dim='%d,%d,%d,%d']", 
						casp.getX(), casp.getY(), 100, 80));
		screenRectElem.click();
		
		// then
		String clickInfoText = clickInfo.getAttribute("value");
		assertFalse("click not executed, click info was empty", clickInfoText.isEmpty());
		assertTrue("unexpected click info: " + clickInfoText, clickInfoText.contains("50,40"));
	}
}
