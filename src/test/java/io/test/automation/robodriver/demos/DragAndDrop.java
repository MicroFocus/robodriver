package io.test.automation.robodriver.demos;

import java.io.IOException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.test.automation.robodriver.RoboDriver;
import io.test.automation.robodriver.RoboDriverUtil;
import io.test.automation.robodriver.TestUtil;

/**
 * Demos OS native drag and drop action using {@link #roboDriver}.
 */
public class DragAndDrop {

	public static void main(String[] args) throws IOException {
		RemoteWebDriver robo = new RoboDriver();

		// use browser to demonstrate native actions
		TestUtil util = new TestUtil();
		RemoteWebDriver chromeDriver = util.startChrome(); // to demonstrate native actions

		try {
			chromeDriver.get("http://the-internet.herokuapp.com/drag_and_drop");
			WebElement elementToDrag = chromeDriver.findElementById("column-a");
			WebElement elementDragTarget = chromeDriver.findElementById("column-b");

			// roboDriver is used drag and drop a web element
			RoboDriverUtil roboUtil = new RoboDriverUtil();
			WebElement rectDragFrom = roboUtil.findSreenRectangleFromWebElement(robo, elementToDrag);
			WebElement rectDragTo = roboUtil.findSreenRectangleFromWebElement(robo, elementDragTarget);
			// drag from center of rectangle A 'rectDragFrom' to center of rectangle B
			// 'rectDragTo'
			new Actions(robo).clickAndHold(rectDragFrom).pause(200).moveByOffset(20, 0).pause(200).moveByOffset(20, 0)
					.pause(200).moveByOffset(20, 0).moveByOffset(20, 0).moveByOffset(20, 0).moveByOffset(20, 0)
					.moveByOffset(20, 0).moveByOffset(20, 0).moveByOffset(20, 0).moveByOffset(20, 0)
					.moveToElement(rectDragTo).release().perform();

			util.sleep(3000);
		} finally {
			if (chromeDriver != null) {
				util.stopServices();
			}
		}
	}
}
