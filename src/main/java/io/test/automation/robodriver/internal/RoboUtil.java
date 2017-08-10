package io.test.automation.robodriver.internal;


import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RoboUtil {
	
	private static Logger LOGGER = LoggerUtil.get(RoboUtil.class);
	
	private static Map<String, Robot> robots = new HashMap<>();

	private static Map<Keys, Integer> webDriverKeyMap = new HashMap<>();
	
	static {
		webDriverKeyMap.put(Keys.SHIFT, KeyEvent.VK_SHIFT);
		webDriverKeyMap.put(Keys.ALT, KeyEvent.VK_ALT);
		webDriverKeyMap.put(Keys.CONTROL, KeyEvent.VK_CONTROL);
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("mac")) {
			webDriverKeyMap.put(Keys.COMMAND, KeyEvent.VK_META);
		} else {
			webDriverKeyMap.put(Keys.COMMAND, KeyEvent.VK_WINDOWS);
		}
		// TODO...
	}

	public static void showScreenDevices() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsDevice defaultScreenDevice = ge.getDefaultScreenDevice();
		System.out.println("defaultScreenDevice: " + defaultScreenDevice);
		for (int j = 0; j < gs.length; j++) {
			GraphicsDevice gd = gs[j];
			System.out.println("device[" + j + "]: " + gd);
			GraphicsConfiguration[] gconfs = gd.getConfigurations();
			int i = 0;
			for (GraphicsConfiguration gconf : gconfs) {
				System.out.println("gconf[" + i + "]: " + gconf);
				AffineTransform transform = gconf.getDefaultTransform();
				System.out.println(transform);
				System.out.println("scale x = " + transform.getScaleX());
				System.out.println("scale y = " + transform.getScaleY());
				/*JFrame f = new JFrame(gs[j].getDefaultConfiguration());
				Canvas c = new Canvas(gconf);
				java.awt.Rectangle gcBounds = gconf.getBounds();
				int xoffs = gcBounds.x;
				int yoffs = gcBounds.y;
				f.getContentPane().add(c);
				f.setLocation((i * 50) + xoffs, (i * 60) + yoffs);
				f.setVisible(true);
				*/
				i++;
			}
		}
	}

	public static void clacVirtualBounds() {
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gdevices = ge.getScreenDevices();
		for (int j = 0; j < gdevices.length; j++) {
			GraphicsDevice gd = gdevices[j];
			GraphicsConfiguration[] gc = gd.getConfigurations();
			for (int i=0; i < gc.length; i++) {
				Rectangle bounds = gc[i].getBounds();
				System.out.println("graphics device/config-index: " + j + "/" + i + ", bounds=" + bounds);
				virtualBounds = virtualBounds.union(bounds);
			}
		} 
		System.out.printf("virtualBounds of %s screens = %s%n", gdevices.length, virtualBounds);
	}

	public static void clacDefaultVirtualBounds() {
		Rectangle virtualBounds = new Rectangle();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gs = ge.getDefaultScreenDevice();
		GraphicsConfiguration[] gc = gs.getConfigurations();
		for (int i=0; i < gc.length; i++) {
			Rectangle bounds = gc[i].getBounds();
			System.out.println("graphics config-" + i + ", bounds=" + bounds);
			virtualBounds = virtualBounds.union(bounds);
		}
		System.out.println("default screen virtualBounds=" + virtualBounds);
	}

	public static void createScreenShot(Rectangle rectangle) {
		try {
			Robot robot = new Robot();
			BufferedImage screenCapture = robot.createScreenCapture(rectangle);
			ImageIO.write(screenCapture, "png", new File("./screenshot.png"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static GraphicsDevice getDefaultDevice() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return ge.getDefaultScreenDevice();
	}

	public static GraphicsDevice getDeviceByIndex(int index) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screenDevices = ge.getScreenDevices();
		return screenDevices[index];
	}

	public static GraphicsDevice getDeviceById(String deviceId) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screenDevices = ge.getScreenDevices();
		for (GraphicsDevice device : screenDevices) {
			if (deviceId.toLowerCase().equals(device.getIDstring().toLowerCase())) {
				return device;
			}
		}
		return null;
	}

	public static List<RoboScreen> getAllScreens(RemoteWebDriver driver) {
		List<RoboScreen> result = new ArrayList<>();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screenDevices = ge.getScreenDevices();
		for (GraphicsDevice graphicsDevice : screenDevices) {
			result.add(RoboScreen.getInstance(graphicsDevice, driver));
		}
		return result;
	}

	public static void sendKeys(Robot robot, CharSequence[] keysToSend) {
		for (CharSequence charSeq : keysToSend) {
			for (int i = 0; i < charSeq.length(); i++) {
				char c = charSeq.charAt(i);
				try {
					int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
					robot.keyPress(keyCode);
					robot.keyRelease(keyCode);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	public static Robot getRobot(GraphicsDevice device) {
		if (robots.containsKey(device.getIDstring())) {
			return robots.get(device.getIDstring());
		} else {
			try {
				Robot r = new Robot(device);
				r.setAutoDelay(130);
				r.setAutoWaitForIdle(true);
				robots.put(device.getIDstring(), r);
				return r;
			} catch (AWTException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static Robot getDefaultRobot() {
		return getRobot(getDefaultDevice());
	}

	public static void mouseDown(GraphicsDevice device) {
		LOGGER.log(Level.FINE, ()->String.format("mouse down, device=%s", device)); 
		getRobot(device).mousePress(InputEvent.BUTTON1_DOWN_MASK);
	}

	public static void mouseUp(GraphicsDevice device) {
		LOGGER.log(Level.FINE, ()->String.format("mouse up, device=%s", device)); 
		getRobot(device).mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	public static void mouseMove(GraphicsDevice device, Long tickDuration, Integer movePosX, Integer movePosY) {
		LOGGER.log(Level.FINE, ()->String.format("move mouse to (%s,%s), tick duration=%s, device=%s", 
				movePosX, movePosY, tickDuration, device));
		// TODO implement move ticks
		getRobot(device).mouseMove(movePosX, movePosY);
	}

	public static int getKey(Keys key) {
		return webDriverKeyMap.get(key);
	}

	public static void main(String[] args) {
		showScreenDevices();
		clacVirtualBounds();
		clacDefaultVirtualBounds();
	}

}

