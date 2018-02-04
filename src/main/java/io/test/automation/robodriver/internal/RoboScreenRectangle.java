package io.test.automation.robodriver.internal;

import java.awt.Rectangle;

import org.openqa.selenium.Point;
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

	public RoboScreenRectangle(RoboScreen screen, Rectangle rectangle) {
		this(screen, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
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

	@Override
	public Point getLocation() {
		return new Point(getX(), getY());
	}
	
	@Override
	public org.openqa.selenium.Rectangle getRect() {
		return new org.openqa.selenium.Rectangle(getX(), getY(), getHeight(), getWidht());
	}
}
