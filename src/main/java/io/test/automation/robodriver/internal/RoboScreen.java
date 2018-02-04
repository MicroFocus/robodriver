package io.test.automation.robodriver.internal;

import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

public class RoboScreen extends RemoteWebElement {

	private static Logger LOGGER = LoggerUtil.get(RoboScreen.class);

	private static Map<String, RoboScreen> roboScreenCache = new HashMap<>();
	
	private GraphicsDevice device;
	private java.awt.Rectangle bounds;
	private Rectangle rect;
	private Dimension size;
	private RoboUtil roboUtil = new RoboUtil();
	

	private RoboScreen(GraphicsDevice device) {
		this.device = device;
		this.bounds = device.getDefaultConfiguration().getBounds();
		this.rect = new Rectangle(bounds.x, bounds.y, bounds.height, bounds.width);
		this.size = new Dimension(bounds.width, bounds.height);
	}

	@Override
	public void click() {
		Robot robot = roboUtil.getRobot(device);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		LOGGER.log(Level.FINE, ()->String.format("send keys '%s', device=%s", charSequenceToString(keysToSend), device)); 
		Robot robot = roboUtil.getRobot(device);
		roboUtil.sendKeys(robot, keysToSend);
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

	public GraphicsDevice getDevice() {
		return device;
	}

	@Override
	public String toString() {
		return "id=" + id + " (" + device + ", " + bounds + ")";
	}

	public static RoboScreen getInstance(GraphicsDevice device, RemoteWebDriver driver) {
		String id = "screen-" + device.getIDstring();
		if (roboScreenCache.containsKey(id)) {
			return roboScreenCache.get(id);
		} else {
			RoboScreen roboScreen = new RoboScreen(device);
			roboScreen.setId(id);
			roboScreen.setParent(driver);
			roboScreenCache.put(id, roboScreen);
			return roboScreen;
		}
	}
	
	public static RoboScreen getDefaultScreen(RemoteWebDriver driver) {
		return getInstance(new RoboUtil().getDefaultDevice(), driver);
	}

	public static List<RoboScreen> getAllScreens(RemoteWebDriver driver) {
		return new RoboUtil().getAllScreens(driver);
	}

	/**
	 * @param index zero based
	 * @return screen
	 */
	public static RoboScreen getScreen(int index, RemoteWebDriver driver) {
		return getInstance(new RoboUtil().getDeviceByIndex(index), driver);
	}

	public static RoboScreen getScreenById(String id) {
		return roboScreenCache.get(id);
	}

	private String charSequenceToString(CharSequence[] charSequenceArray) {
		StringBuilder sb = new StringBuilder();
		for (CharSequence charSequence : charSequenceArray) {
			sb.append(charSequence);
		}
		return sb.toString();
	}

	public java.awt.Rectangle getRectAwt() {
		Rectangle r = getRect();
		return new java.awt.Rectangle(r.x, r.y, r.width, r.height);
	}
}

