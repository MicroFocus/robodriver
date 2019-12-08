package io.test.automation.robodriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import io.test.automation.robodriver.internal.RoboScreen;
import io.test.automation.robodriver.internal.RoboScreenRectangle;

public class RoboDriverBasicsTest {

	@Test
	public void testRoboDriver() {
		RoboDriver roboDriver = new RoboDriver();
		roboDriver.quit();
	}

	@Test
	public void testStartApp() {
		TestUtil util = new TestUtil();
		String testAppPath = "C:\\Windows\\System32\\notepad.exe"; // TODO: add implementation for linux, macos
		assumeTrue("test ignored, not implemented for OS: " + System.getProperty("os.name"), util.isWindows());

		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		roboCapabilities.setCapability("app", testAppPath);
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		assertTrue(roboDriver.getAppProcess().isAlive());
		roboDriver.quit();
	}

	@Test
	public void testFindDefaultScreen() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		WebElement screen = roboDriver.findElementByXPath("//screen[@default=true]");
		assertNotNull(screen);
		assertTrue(screen instanceof RoboScreen);
		assertTrue(screen.toString(), screen.toString().contains("GraphicsDevice"));
		assertScrenRectangle(screen);
	}

	@Test
	public void testFindAllScreens() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		List<WebElement> screens = roboDriver.findElementsByXPath("//screen");
		assertTrue(screens.size() >= 1);
	}

	@Test
	public void testFindSingleScreen() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		WebElement screen = roboDriver.findElementByXPath("//screen");
		assertScrenRectangle(screen);
	}

	@Test
	public void testFindSingleScreenByIndex() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		WebElement screen = roboDriver.findElementByXPath("//screen[0]");
		assertScrenRectangle(screen);
	}

	@Test
	public void testCommandExecutorUsage() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RemoteWebDriver roboDriver = new RemoteWebDriver(new RoboDriverCommandExecutor(), roboCapabilities);
		WebElement screen = roboDriver.findElementByXPath("//screen[0]");
		assertScrenRectangle(screen);
	}

	@Test
	public void testFindWithInvalidIndex() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RemoteWebDriver roboDriver = new RemoteWebDriver(new RoboDriverCommandExecutor(), roboCapabilities);
		try {
			roboDriver.findElementByXPath("//screen[999991]");
			fail("expected invalid index error");
		} catch (Exception e) {
			assertTrue(e.getCause().getMessage().contains("999991"));
		}
	}

	@Test
	public void testRoboScreenCaching() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		RoboScreen screen = (RoboScreen) roboDriver.findElementByXPath("//screen[@default=true]");

		assertEquals("screen-" + screen.getDevice().getIDstring(), screen.getId());
		assertEquals("screen-" + screen.getDevice().getIDstring(), RoboScreen.getScreenById(screen.getId()).getId());
	}

	@Test
	public void testFindRectangleOfScreen() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		WebElement screen = roboDriver.findElementByXPath("//screen[@default=true]");

		WebElement rectangle = screen.findElement(By.xpath("//rectangle[@dim='70,80,100,200']"));
		assertNotNull(rectangle);
		assertEquals(70, rectangle.getLocation().getX());
		assertEquals(80, rectangle.getLocation().getY());
		assertEquals(70, rectangle.getRect().getX());
		assertEquals(80, rectangle.getRect().getY());
		assertEquals(100, rectangle.getRect().getWidth());
		assertEquals(200, rectangle.getRect().getHeight());
	}

	@Test
	public void testFindRectangleOfScreenByFullXpath() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);

		WebElement rectangle = roboDriver
				.findElement(By.xpath("//screen[@default=true]//rectangle[@dim='70,80,100,200']"));

		assertNotNull(rectangle);
		assertEquals(RoboScreenRectangle.class.getSimpleName(), rectangle.getClass().getSimpleName());
		assertEquals(70, rectangle.getLocation().getX());
		assertEquals(80, rectangle.getLocation().getY());
		assertEquals(70, rectangle.getRect().getX());
		assertEquals(80, rectangle.getRect().getY());
		assertEquals(100, rectangle.getRect().getWidth());
		assertEquals(200, rectangle.getRect().getHeight());
	}

	@Test
	public void testRoboScreenEquals() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		RemoteWebElement screen1 = (RemoteWebElement) roboDriver.findElementByXPath("//screen[0]");
		RemoteWebElement screen2 = (RemoteWebElement) roboDriver.findElementByXPath("//screen[0]");

		assertTrue(screen1.equals(screen2));
	}

	@Test
	public void testRoboScreenRectangleTestEquals() {
		DesiredCapabilities roboCapabilities = RoboDriver.getDesiredCapabilities();
		RoboDriver roboDriver = new RoboDriver(roboCapabilities);
		WebElement screen = roboDriver.findElementByXPath("//screen[@default=true]");
		WebElement rectangle1 = screen.findElement(By.xpath("//rectangle[@dim='70,80,100,200']"));
		WebElement rectangle2 = screen.findElement(By.xpath("//rectangle[@dim='70,80,100,200']"));
		WebElement rectangle3 = screen.findElement(By.xpath("//rectangle[@dim='90,80,100,200']"));

		assertTrue(rectangle1.equals(rectangle2));
		assertFalse(rectangle1.equals(rectangle3));
		assertFalse(rectangle2.equals(rectangle3));
	}

	private void assertScrenRectangle(WebElement screen) {
		int x = screen.getRect().getX();
		assertTrue("x=" + x, x == 0 || x < 0);
		int y = screen.getRect().getY();
		assertTrue("y=" + y, y == 0 || y < 0);
		int width = screen.getRect().getWidth();
		assertTrue("with=" + width, width >= 1024);
		int height = screen.getRect().getHeight();
		assertTrue("height=" + height, height >= 768);
	}

}
