package io.test.automation.robodriver.internal;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.Point;
import org.openqa.selenium.remote.RemoteWebElement;

public class RoboScreenRectangle extends RemoteWebElement {
	
	private static AtomicInteger ID_PROVIDER = new AtomicInteger(0);
	private static WeakHashMap<String, RoboScreenRectangle> RECTANGLES = new WeakHashMap<>();
	
	public static RoboScreenRectangle get(String id) {
		return RECTANGLES.get(id);
	}

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
		setId("rectangle-" + Integer.toString(ID_PROVIDER.incrementAndGet()));
		RECTANGLES.put(getId(), this);
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

	public int getWidth() {
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
		return new org.openqa.selenium.Rectangle(getX(), getY(), getHeight(), getWidth());
	}
	
	private Rectangle getRectAwt() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	/**
	 * Retrieve base64 encoded PNG.
	 * @return base64 encoded PNG
	 * @throws IOException 
	 */
	public String getScreenshot() throws IOException {
		GraphicsDevice device = screen.getDevice();
		RoboUtil roboUtil = new RoboUtil();
		return roboUtil.getScreenshot(device, getRectAwt()); 
	}
}
