package io.test.automation.robodriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Util {
	
	private static Util UTIL;

	public static Util getInstance() {
		if (UTIL == null) {
			UTIL = new Util();
		}
		return UTIL;
	}
	
	private Util() {
	}

	public void navigateToTestPage(RemoteWebDriver driver) throws MalformedURLException {
		URL url = new File("./src/test/resources/test.html").toURI().toURL();
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
	
	public static void main(String[] args) {
		RemoteWebDriver driver = null;
		try {
			Util util = Util.getInstance();
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
