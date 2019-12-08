package io.test.automation.robodriver.internal;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.util.WeakHashMap;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.server.handler.FindElement;

abstract public class RoboElement extends RemoteWebElement {

	private static WeakHashMap<String, RoboElement> ELEMENTS = new WeakHashMap<>();

	public static RoboElement getByElementId(String elementId) {
		return ELEMENTS.get(elementId);
	}

	public RoboElement(String id, RemoteWebDriver parent) {
		this.setId(id);
		this.setParent(parent);
	}

	@Override
	public boolean equals(Object obj) {
		return getId().equals(((RoboElement) obj).getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	/**
	 * Selenium server internal id, see {@link FindElement}
	 * 
	 * @param elementId
	 */
	public void addKnownElement(String elementId) {
		ELEMENTS.put(elementId, this);
	}

	public abstract GraphicsDevice getDevice();

	protected abstract Rectangle getRectAwt();

}
