package io.test.automation.robodriver.internal;

import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.HasIdentity;
import org.openqa.selenium.internal.Locatable;

public class RoboScreen implements WebElement, Locatable, HasIdentity {

	private GraphicsDevice device;
	private java.awt.Rectangle bounds;
	private Rectangle rect;
	private Dimension size;

	public RoboScreen(GraphicsDevice device) {
		this.device = device;
		this.bounds = device.getDefaultConfiguration().getBounds();
		this.rect = new Rectangle(bounds.x, bounds.y, bounds.height, bounds.width);
		this.size = new Dimension(bounds.width, bounds.height);
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void click() {
		Robot robot = RoboUtil.getRobot(device);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	@Override
	public void submit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		Robot robot = RoboUtil.getRobot(device);
		RoboUtil.sendKeys(robot, keysToSend);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WebElement> findElements(By by) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WebElement findElement(By by) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDisplayed() {
		return true;
	}

	@Override
	public Point getLocation() {
		return new Point(bounds.x, bounds.y);
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public Rectangle getRect() {
		return rect;
	}

	@Override
	public String getCssValue(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return device + ", " + bounds;
	}

	public static RoboScreen getDefaultScreen() {
		return new RoboScreen(RoboUtil.getDefaultDevice());
	}

	public static List<RoboScreen> getAllScreens() {
		return RoboUtil.getAllScreens();
	}

	/**
	 * @param index zero based
	 * @return screen
	 */
	public static RoboScreen getScreen(int index) {
		return new RoboScreen(RoboUtil.getDeviceByIndex(index));
	}

	@Override
	public Coordinates getCoordinates() {
		return new Coordinates() {

			@Override
			public Point onScreen() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public Point inViewPort() {
				return getLocation();
			}

			@Override
			public Point onPage() {
				return getLocation();
			}

			@Override
			public Object getAuxiliary() {
				return device.getIDstring();
			}
		};
	}

	@Override
	public String getId() {
		return device.getIDstring();
	}

}

