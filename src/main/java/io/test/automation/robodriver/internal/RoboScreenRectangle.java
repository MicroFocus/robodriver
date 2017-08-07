package io.test.automation.robodriver.internal;

import org.openqa.selenium.remote.RemoteWebElement;

public class RoboScreenRectangle extends RemoteWebElement {

	private RoboScreen screen;
	private int x;
	private int y;
	private int widht;
	private int height;

	public RoboScreenRectangle(RoboScreen screen, int x, int y, int widht, int height) {
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.widht = widht;
		this.height = height;
	}

	public RoboScreen getScreen() {
		return screen;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidht() {
		return widht;
	}

	public int getHeight() {
		return height;
	}

}
