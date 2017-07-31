package io.test.automation.robodriver;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.test.automation.robodriver.internal.RoboUtil;

public class TestUtil {
	
	private static final int GUESS_X = 115;
	private static final int GUESS_Y = 245;

	public void navigateToTestPage(RemoteWebDriver driver) throws MalformedURLException {
		File testPageFile = new File(this.getClass().getClassLoader().getResource("test.html").getFile());
		URL url = testPageFile.toURI().toURL();
		driver.get(url.toString());
	}
	
	public RemoteWebDriver startFirefox() throws IOException {
		URL url = startLocalFirefox();
		return new RemoteWebDriver(url, (new FirefoxOptions().toCapabilities()));
	}
	
	private URL startLocalFirefox() throws IOException {
		FirefoxOptions options = new FirefoxOptions();
		GeckoDriverService service = new GeckoDriverService.Builder()
				.usingAnyFreePort()
				.usingFirefoxBinary(options.getBinary())
				.build();
		service.start();
		URL url = service.getUrl();
		return url;
	}

	public RemoteWebDriver startChrome() throws IOException {
		URL url = startLocalChrome();
		return new RemoteWebDriver(url, DesiredCapabilities.chrome());
	}
	
	private URL startLocalChrome() throws IOException {
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingAnyFreePort()
				.build();
		service.start();
		URL url = service.getUrl();
		return url;
	}
	
	public boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
	
	private static Pattern outputCoordinates = Pattern.compile("\\D*(\\d+),(\\d+)");

	// TODO: implement the case when browser is not at the default screen
	/**
	 * Retrieves the screen position of the left upper corner of the 200x200 click area.
	 * @param browser
	 * @param clickInfo
	 * @return screen position 
	 */
	public org.openqa.selenium.Point getAbsoluteClickAreaPosition(RemoteWebDriver browser, WebElement clickInfo) {
		sleep(2000);
		org.openqa.selenium.Point windowScreenPos = browser.manage().window().getPosition();
		// click somewhere the to click area
		Robot robot = RoboUtil.getDefaultRobot();
		robot.mouseMove(windowScreenPos.getX() + GUESS_X, windowScreenPos.getY() + GUESS_Y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		// narrow correct click area position
		String clickInfoOutput = clickInfo.getAttribute("value");
		Matcher matcher = outputCoordinates.matcher(clickInfoOutput);
		matcher.find();
		int areaClickPosX = Integer.parseInt(matcher.group(1));
		int areaClickPosY = Integer.parseInt(matcher.group(2));
		int xdeltaToCenter = 100 - areaClickPosX;
		int ydeltaToCenter = 100 - areaClickPosY;
		clearInfoTextField(browser);
		return new Point(windowScreenPos.getX() + GUESS_X - 100 - xdeltaToCenter, 
				windowScreenPos.getY() + GUESS_Y - 100 - ydeltaToCenter);
	}

	public void clearInfoTextField(RemoteWebDriver browser) {
		// clear info outputs
		WebElement clearButton = browser.findElementById("clearbutton");
		clearButton.click();
	}

	private void sleep(int i) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
		
		RemoteWebDriver driver = null;
		try {
			TestUtil util = new TestUtil();
			driver = util.startChrome();
			util.navigateToTestPage(driver);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
	}
	
}
