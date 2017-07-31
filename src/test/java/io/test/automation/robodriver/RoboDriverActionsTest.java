package io.test.automation.robodriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RoboDriverActionsTest {

	private static Point CASP; // screen position of left upper corner of click area
	private static RemoteWebDriver BROWSER;
	
	private RoboDriver robo;

	@BeforeClass
	public static void onBeforeClass() throws IOException {
		TestUtil util = new TestUtil();
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
		}
	}

	@Before
	public void onBeforeTest() throws IOException {
		(new TestUtil()).clearInfoTextField(BROWSER);
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
		// given
		WebElement textInputField = BROWSER.findElementById("outputs");
		textInputField.click(); // set focus to input field
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		screen.sendKeys("hello");
		
		// then
		assertEquals(textInputField.getAttribute("value"), "hello");
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
}
