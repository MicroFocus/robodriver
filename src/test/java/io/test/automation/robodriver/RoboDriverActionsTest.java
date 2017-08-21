package io.test.automation.robodriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RoboDriverActionsTest {

	private static Point CASP; // click area screen position of left upper corner
	private static RemoteWebDriver BROWSER;
	private static TestUtil util;
	
	private RoboDriver robo;

	@BeforeClass
	public static void onBeforeClass() throws IOException {
		util = new TestUtil();
		BROWSER = util.startFirefox();
		util.navigateToTestPage(BROWSER);
		WebElement clickInfo = BROWSER.findElementById("outputs");
		CASP = util.getAbsoluteClickAreaPosition(BROWSER, clickInfo);
		assertTrue("click info must be empty", clickInfo.getAttribute("value").isEmpty());
	}
	
	@AfterClass
	public static void onAfterClass() {
		if (BROWSER != null) {
			BROWSER.quit();
			util.stopServices();
		}
	}

	@Before
	public void onBeforeTest() throws IOException {
		util.clearInfoTextField(BROWSER);
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		robo = new RoboDriver(roboCapabilities);
	}
	
	@After
	public void onAfterTest() {
		if (robo != null) {
			robo.quit();
		}
	}
	
	@Test
	public void testSendKeys() throws Exception {
		WebElement textInputField = BROWSER.findElementById("outputs");
		textInputField.click(); // set focus to input field
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		screen.sendKeys("hello");
		
		// then
		assertEquals("hello", textInputField.getAttribute("value"));
	}
	
	@Test
	public void testSendKeyActions() throws Exception {
		WebElement textInputField = BROWSER.findElementById("outputs");
		textInputField.click(); // set focus to input field
		
		// when
		new Actions(robo)
			.sendKeys("hello")
			.perform();
		
		// then
		assertEquals("hello", textInputField.getAttribute("value"));
	}
	
	@Test
	public void testSendShiftKeyActions() throws Exception {
		WebElement textInputField = BROWSER.findElementById("outputs");
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
	@Ignore // FIX Selenium 3.4 binding adds click to (0,0) actions to the target element to bring it in focus, that is bad and not needed!
	public void testSendShiftKeyWithTargetActions() throws Exception {
		WebElement textInputField = BROWSER.findElementById("outputs");
		textInputField.click(); // set focus to input field
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		new Actions(robo)
			.keyDown(screen, Keys.SHIFT)
			.sendKeys(screen, "hello")
			.keyUp(screen, Keys.SHIFT)
			.perform();
		
		// then
		assertEquals("HELLO", textInputField.getAttribute("value"));
	}
	
	@Test
	public void testClick() throws Exception {
		// given
		WebElement clickInfo = BROWSER.findElementById("outputs");
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		new Actions(robo)
			.moveToElement(screen, CASP.getX() + 100, CASP.getY() + 100)
			.click()
			.perform();
		
		// then
		String clickInfoText = clickInfo.getAttribute("value");
		assertFalse("click not executed, click info was empty", clickInfoText.isEmpty());
		assertTrue("unexpected click info: " + clickInfoText, clickInfoText.contains("100,100"));
	}
	
	@Test
	public void testDragAndDrop() {
		WebElement outputs = BROWSER.findElementById("outputs");
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		
		// when
		WebElement caspCorner = screen.findElement(
				By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", CASP.getX() + 1, CASP.getY() + 1)));
		WebElement caspCenter = screen.findElement(
				By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", CASP.getX() + 100, CASP.getY() + 100)));
		new Actions(robo)
			.dragAndDrop(caspCorner, caspCenter)
			.perform();
		
		// then
		assertEquals("mouse move: from (1,1) to (100,100)", outputs.getAttribute("value"));
	}
	
	@Test
	public void testDragAndDropByOffset() {
		WebElement outputs = BROWSER.findElementById("outputs");
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		
		// when
		WebElement caspCenter = screen.findElement(
				By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", CASP.getX() + 100, CASP.getY() + 100)));
		new Actions(robo)
		.dragAndDropBy(caspCenter, 20, 20)
		.perform();
		
		// then
		assertEquals("mouse move: from (100,100) to (120,120)", outputs.getAttribute("value"));
	}
}
