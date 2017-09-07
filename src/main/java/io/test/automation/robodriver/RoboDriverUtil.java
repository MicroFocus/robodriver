package io.test.automation.robodriver;

import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class RoboDriverUtil {
	
	private int leftViewportToWindowBorderInPixel = -1;
	private int bottomViewPortBorderInPixel = -1;

	public RoboDriverUtil() {
	}

	/**
	 * @param leftViewportToWindowBorderInPixel distance in pixel between left edge of outer browser window and the browser rendering viewport,
	 * <br>with -1 the default is used, it is half of the difference between the window width and the viewport width.
	 * 
	 * @param bottomViewportToWindowBorder distance in pixel between bottom edge of outer browser window and the browser rendering viewport,
	 * <br>with -1 the default is used, it is leftViewportToWindowBorder.
	 */
	public RoboDriverUtil(int leftViewportToWindowBorderInPixel, int bottomViewPortBorderInPixel) {
		this.leftViewportToWindowBorderInPixel = leftViewportToWindowBorderInPixel;
		this.bottomViewPortBorderInPixel = bottomViewPortBorderInPixel;
	}

	/**
	 * Calculates the screen rectangle of a browser DOM element with x,y position in pixel of the left upper corner.
	 * 
	 * @param element browser DOM element
	 * @return the rectangle with position and dimension.
	 */
	public Rectangle getScreenRectangleOfBrowserElement(WebElement element) {
		RemoteWebDriver driver = validateWebElement(element);
		Rectangle viewportScreenRectangle = getScreenRectangleOfBrowserViewport(driver);
		Rectangle elementViewportRectangle = new Rectangle(element.getLocation(), element.getSize());
		int elementScreenX = viewportScreenRectangle.getX() + elementViewportRectangle.getX();
		int elementScreenY = viewportScreenRectangle.getY() + elementViewportRectangle.getY();
		return new Rectangle(new Point(elementScreenX, elementScreenY), new Dimension(elementViewportRectangle.getWidth(), elementViewportRectangle.getHeight()));
	}

	public Rectangle getScreenRectangleOfBrowserViewport(RemoteWebDriver driver) {
		validateWebDriver(driver);
		Rectangle windowScreenRect = getScreenRectangleOfCurrentBrowserWindow(driver);
		int viewportHeight = (int) (long) driver.executeScript("return window.innerHeight;");
		int viewportWidth = (int) (long) driver.executeScript("return window.innerWidth;");
		int viewportBorderLeft = getLeftViewportBorder(windowScreenRect, viewportWidth);
		int viewportBorderBottom = getBottomViewportBorder(windowScreenRect, viewportWidth);
		int viewportScreenX = windowScreenRect.getX() + viewportBorderLeft;
		int viewportScreenY = windowScreenRect.getY() + windowScreenRect.getHeight() - viewportBorderBottom - viewportHeight;
		Rectangle viewportScreenRectangle = new Rectangle(new Point(viewportScreenX, viewportScreenY), new Dimension(viewportWidth, viewportHeight));
		return viewportScreenRectangle;
	}

	public Rectangle getScreenRectangleOfCurrentBrowserWindow(RemoteWebDriver driver) {
		validateWebDriver(driver);
		Window window = driver.manage().window();
		return getScreenRectangleOfWindow(window);
	}

	public Rectangle getScreenRectangleOfWindow(Window window) {
		return new Rectangle(window.getPosition(), window.getSize());
	}
	
	// TODO: find screen element for a window (useful in case of multiple screens)

	private int getLeftViewportBorder(Rectangle windowScreenRect, int viewportWidth) {
		if (leftViewportToWindowBorderInPixel >= 0) {
			return leftViewportToWindowBorderInPixel;
		}
		int borderLeftAndRight = windowScreenRect.getWidth() - viewportWidth;
		if (borderLeftAndRight < 2) {
			return 0;
		}
		return borderLeftAndRight / 2;
	}

	private int getBottomViewportBorder(Rectangle windowScreenRect, int viewportWidth) {
		if (bottomViewPortBorderInPixel >= 0) {
			return bottomViewPortBorderInPixel;
		}
		return getLeftViewportBorder(windowScreenRect, viewportWidth);
	}

	private RemoteWebDriver validateWebElement(WebElement elem) {
		if (!(elem instanceof RemoteWebElement)) {
			throw new InvalidArgumentException("element argument must be of type RemoteWebElement");
		}
		RemoteWebElement element = (RemoteWebElement) elem;
		if (!(element.getWrappedDriver() instanceof RemoteWebDriver)) {
			throw new InvalidArgumentException("element argument must belong to a browser driver");
		}
		RemoteWebDriver driver = (RemoteWebDriver) element.getWrappedDriver();
		validateWebDriver(driver);
		return driver;
	}

	private void validateWebDriver(RemoteWebDriver driver) {
		if (driver.getCapabilities().getBrowserName().equals(RoboDriver.BROWSER_NAME)) {
			throw new InvalidArgumentException("driver argument must be a browser driver");
		}
	}
}
