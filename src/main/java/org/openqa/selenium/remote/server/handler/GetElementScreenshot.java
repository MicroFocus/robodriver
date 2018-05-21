// PATCH: find in this file 'robodriver' to see changes

package org.openqa.selenium.remote.server.handler;

import static org.openqa.selenium.OutputType.BASE64;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.server.Session;

public class GetElementScreenshot extends WebElementHandler<String> {

	public GetElementScreenshot(Session session) {
		super(session);
	}

	@Override
	public String call() throws Exception {
		TakesScreenshot element = (TakesScreenshot)getElement();
		return element.getScreenshotAs(BASE64);
	}


	@Override
	public String toString() {
		return String.format("[get element screenshot: %s]", getElementAsString());
	}
}
