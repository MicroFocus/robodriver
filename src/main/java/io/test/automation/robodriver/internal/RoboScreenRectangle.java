package io.test.automation.robodriver.internal;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Rectangle relative to its screen.
 * 
 */
public class RoboScreenRectangle extends RoboElement {

	private static AtomicInteger ID_PROVIDER = new AtomicInteger(0);
	private static WeakHashMap<String, RoboScreenRectangle> RECTANGLES = new WeakHashMap<>();

	public static RoboScreenRectangle get(String id) {
		return RECTANGLES.get(id);
	}

	private RoboScreen screen;
	private int x;
	private int y;
	private int width;
	private int height;

	public static RoboScreenRectangle getInstance(RoboScreen screen, Rectangle rectangle) {
		String id = "rectangle-" + rectangle.toString();
		if (!RECTANGLES.containsKey(id)) {
			RoboScreenRectangle rect = new RoboScreenRectangle(id, screen, rectangle);
			RECTANGLES.put(id, rect);
			return rect;
		} else {
			return RECTANGLES.get(id);
		}
	}

	private RoboScreenRectangle(String id, RoboScreen screen, Rectangle rectangle) {
		this(id, screen, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	private RoboScreenRectangle(String id, RoboScreen screen, int x, int y, int widht, int height) {
		super(id, (RemoteWebDriver) screen.getWrappedDriver());
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.width = widht;
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public Point getLocation() {
		return new Point(getX(), getY());
	}

	@Override
	public Dimension getSize() {
		return new Dimension(this.width, this.height);
	}

	@Override
	public org.openqa.selenium.Rectangle getRect() {
		return new org.openqa.selenium.Rectangle(getX(), getY(), getHeight(), getWidth());
	}

	@Override
	public Rectangle getRectAwt() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	/**
	 * Retrieve base64 encoded PNG.
	 * 
	 * @return base64 encoded PNG
	 * @throws IOException
	 */
	public String getScreenshot() throws IOException {
		GraphicsDevice device = screen.getDevice();
		RoboUtil roboUtil = new RoboUtil();
		return roboUtil.getScreenshot(device, getRectAwt());
	}

	@Override
	public void click() {
		GraphicsDevice device = screen.getDevice();
		RoboUtil roboUtil = new RoboUtil();
		roboUtil.click(device, getRectAwt());
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
		return super.getScreenshotAs(outputType);
	}

	@Override
	public GraphicsDevice getDevice() {
		return getScreen().getDevice();
	}
}
