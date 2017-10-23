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
		util.clearInfoTextField(browser);
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
		WebElement textInputField = browser.findElementById("outputs");
		textInputField.click(); // set focus to input field
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		screen.sendKeys("hEllo");
		
		// then
		assertEquals("hello", textInputField.getAttribute("value"));
	}
	
	@Test
	public void testSendKeysBackTick() throws Exception {
		WebElement textInputField = browser.findElementById("outputs");
		textInputField.click(); // set focus to input field
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		
		// when
		new Actions(robo)
			.keyDown(Keys.SHIFT)
			.perform();
		screen.sendKeys("VK_DEAD_ACUTE", " ");
		new Actions(robo)
			.keyUp(Keys.SHIFT)
			.perform();

		// then
		assertEquals("`", textInputField.getAttribute("value"));
	}
	
	@Test
	public void testSendKeysCapitalLetterWithAcute() throws Exception {
		WebElement textInputField = browser.findElementById("outputs");
		textInputField.click(); // set focus to input field
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		
		// when
		new Actions(robo)
			.keyDown(Keys.SHIFT)
			.perform();
		
		screen.sendKeys("VK_DEAD_ACUTE", "A"); // single 'VK_xx' arguments are interpreted as virtual key code, see also Java KeyEvent.class 
		
		new Actions(robo)
			.keyUp(Keys.SHIFT)
			.perform();
		
		// then
		assertEquals("À", textInputField.getAttribute("value"));
	}
	
	@Test
	public void testSendKeysWithVirtualKeyNames() throws Exception {
		WebElement textInputField = browser.findElementById("outputs");
		textInputField.click(); // set focus to input field
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		screen.sendKeys("VK_H", "VK_E", "VK_L", "VK_L", "VK_O");
		
		// then
		assertEquals("hello", textInputField.getAttribute("value"));
	}
	
	@Test
	public void testSendKeyActions() throws Exception {
		WebElement textInputField = browser.findElementById("outputs");
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
	@Ignore // FIX Selenium 3.4 binding adds click to (0,0) actions to the target element to bring it in focus, that is bad and not needed!
	public void testSendShiftKeyWithTargetActions() throws Exception {
		WebElement textInputField = browser.findElementById("outputs");
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
	public void testMixedKeyMouseActions() throws Exception {
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
	
	@Test
	public void testClick() throws Exception {
		// given
		WebElement clickInfo = browser.findElementById("outputs");
		
		// when
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		new Actions(robo)
			.moveToElement(screen, casp.getX() + 100, casp.getY() + 100)
			.click()
			.perform();
		
		// then
		String clickInfoText = clickInfo.getAttribute("value");
		assertFalse("click not executed, click info was empty", clickInfoText.isEmpty());
		assertTrue("unexpected click info: " + clickInfoText, clickInfoText.contains("100,100"));
	}
	
	@Test
	public void testDragAndDrop() {
		WebElement outputs = browser.findElementById("outputs");
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		
		// when
		WebElement caspCorner = screen.findElement(
				By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", casp.getX() + 1, casp.getY() + 1)));
		WebElement caspCenter = screen.findElement(
				By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", casp.getX() + 100, casp.getY() + 100)));
		new Actions(robo)
			.dragAndDrop(caspCorner, caspCenter)
			.perform();
		
		// then
		assertEquals("mouse move: from (1,1) to (100,100)", outputs.getAttribute("value").trim());
	}
	
	@Test
	public void testDragAndDropByOffset() {
		WebElement outputs = browser.findElementById("outputs");
		WebElement screen = robo.findElementByXPath("//screen[@default=true]");
		
		// when
		WebElement caspCenter = screen.findElement(
				By.xpath(String.format("//rectangle[@dim='%d,%d,0,0']", casp.getX() + 100, casp.getY() + 100)));
		new Actions(robo)
		.dragAndDropBy(caspCenter, 20, 20)
		.perform();
		
		// then
		assertEquals("mouse move: from (100,100) to (120,120)", outputs.getAttribute("value").trim());
	}
}
