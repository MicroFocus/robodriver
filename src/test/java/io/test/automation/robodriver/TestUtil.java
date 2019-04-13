package io.test.automation.robodriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class TestUtil {
	private GeckoDriverService geckoDriverService;
	private ChromeDriverService chromeDriverService;

	public void navigateToTestPage(RemoteWebDriver driver) throws MalformedURLException {
		File testPageFile = new File(this.getClass().getClassLoader().getResource("test.html").getFile());
		URL url = testPageFile.toURI().toURL();
		driver.get(url.toString());
	}

	public RemoteWebDriver startFirefox() throws IOException {
		// System.setProperty(FirefoxDriver.SystemProperty.BROWSER_BINARY, "C:\\Program
		// Files (x86)\\Mozilla Firefox_50\\firefox.exe");
		URL url = startLocalFirefox();
		return new RemoteWebDriver(url, DesiredCapabilities.firefox());
	}

	private URL startLocalFirefox() throws IOException {
		FirefoxOptions options = new FirefoxOptions();
		geckoDriverService = new GeckoDriverService.Builder().usingAnyFreePort().usingFirefoxBinary(options.getBinary())
				.build();
		geckoDriverService.start();
		URL url = geckoDriverService.getUrl();
		return url;
	}

	public RemoteWebDriver startFirefox(URL url) {
		return new RemoteWebDriver(url, DesiredCapabilities.firefox());
	}

	/**
	 * Starts local Chrome for testing. With 'chromedriver.exe' in the system PATH
	 * this works out of the box.
	 * 
	 * @throws IOException
	 */
	public RemoteWebDriver startChrome() throws IOException {
		URL url = startLocalChrome();
		return startChrome(url);
	}

	public RemoteWebDriver startChrome(URL url) {
		LoggingPreferences logs = new LoggingPreferences();
		logs.enable(LogType.DRIVER, Level.FINE);
		logs.enable(LogType.CLIENT, Level.FINE);
		logs.enable(LogType.BROWSER, Level.FINE);
		logs.enable(LogType.SERVER, Level.FINE);
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setCapability(CapabilityType.LOGGING_PREFS, logs);
		return new RemoteWebDriver(url, chromeOptions);
	}

	private URL startLocalChrome() throws IOException {
		chromeDriverService = new ChromeDriverService.Builder().usingAnyFreePort().build();
		chromeDriverService.start();
		URL url = chromeDriverService.getUrl();
		return url;
	}

	public void stopServices() {
		try {
			if (geckoDriverService != null) {
				geckoDriverService.stop();
			}
			if (chromeDriverService != null) {
				chromeDriverService.stop();
			}
		} finally {
			geckoDriverService = null;
			chromeDriverService = null;
		}
	}

	public boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	public void clearInfoTextField(RemoteWebDriver browser) {
		// clear info outputs
		WebElement clearButton = browser.findElementById("clearbutton");
		clearButton.click();
	}

	public void sleep(int i) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String toString(Rectangle r) {
		return String.format("x=%d, y=%d, width=%d, height=%d", r.x, r.y, r.width, r.height);
	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));

		RemoteWebDriver driver = null;
		TestUtil util = new TestUtil();
		try {
			driver = util.startChrome();
			util.navigateToTestPage(driver);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (driver != null) {
				driver.quit();
				util.stopServices();
			}
		}
	}

}
