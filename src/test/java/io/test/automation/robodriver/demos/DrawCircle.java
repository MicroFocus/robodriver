package io.test.automation.robodriver.demos;

import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.test.automation.robodriver.RoboDriver;
import io.test.automation.robodriver.RoboDriverUtil;
import io.test.automation.robodriver.TestUtil;

public class DrawCircle {

	public static void main(String[] args) throws Exception {
		RoboDriver robo = new RoboDriver();

		// use browser to demonstrate native actions
		TestUtil util = new TestUtil();
		RemoteWebDriver browser = util.startChrome();

		try {
			util.navigateToTestPage(browser);
			WebElement clickArea = browser.findElementById("clickarea");
			Rectangle drawArea = new RoboDriverUtil().getScreenRectangleOfBrowserElement(clickArea);
			Point drawAreaCenter = new Point(drawArea.x + drawArea.width / 2, drawArea.y + drawArea.height / 2);
			Point leftEyeCenter = new Point(drawAreaCenter.x - drawArea.width / 4 + 10,
					drawAreaCenter.y - drawArea.height / 4 + 10);
			Point rightEyeCenter = new Point(drawAreaCenter.x + drawArea.width / 4 - 10,
					drawAreaCenter.y - drawArea.height / 4 + 10);

			// find the screen
			WebElement screen = robo.findElementByXPath("//screen");

			// draw "freehand" circles
			drawCircle(robo, screen, drawAreaCenter, 84d /* radius */, 16 /* number of points */, false);
			drawCircle(robo, screen, drawAreaCenter, 50d /* radius */, 24 /* number of points */, true);
			drawCircle(robo, screen, leftEyeCenter, 8d /* radius */, 5 /* number of points */, false);
			drawCircle(robo, screen, rightEyeCenter, 8d /* radius */, 5 /* number of points */, false);

			util.sleep(3000);
		} finally {
			if (browser != null) {
				util.stopServices();
			}
		}
	}

	private static void drawCircle(RoboDriver robo, WebElement screen, Point origin, double radius, int increments,
			boolean half) {
		Actions actions = new Actions(robo);
		int vectorX = 0;
		int vectorY = 0;
		Point drawStart = null;

		actions.moveToElement(screen, origin.x, origin.y).perform();
		for (int i = 0; i < increments; i++) {
			if (half && i > increments / 2) {
				break;
			}
			double alpha = 2 * Math.PI / increments * i;
			vectorX = (int) (radius * Math.cos(alpha));
			vectorY = (int) (radius * Math.sin(alpha));
			if (i == 0) {
				drawStart = new Point(origin.x + vectorX, origin.y + vectorY);
				actions.moveToElement(screen, drawStart.x, drawStart.y).perform();
			} else {
				drawLineFromCurrentPointToNewPoint(screen, actions, origin.x + vectorX, origin.y + vectorY);
			}
		}
		if (!half) {
			drawLineFromCurrentPointToNewPoint(screen, actions, drawStart.x, drawStart.y);
		}
	}

	private static void drawLineFromCurrentPointToNewPoint(WebElement screen, Actions actions, int xOffset,
			int yOffset) {
		actions.clickAndHold().perform();
		actions.moveToElement(screen, xOffset, yOffset).perform();
		actions.release().perform();
	}

}
