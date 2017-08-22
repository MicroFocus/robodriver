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

	private static Map<Character, Integer> webDriverKeyToOsKeyMap = new HashMap<>();
	
	static {
		webDriverKeyToOsKeyMap.put(Keys.SHIFT.charAt(0), KeyEvent.VK_SHIFT);
		webDriverKeyToOsKeyMap.put(Keys.ALT.charAt(0), KeyEvent.VK_ALT);
		webDriverKeyToOsKeyMap.put(Keys.CONTROL.charAt(0), KeyEvent.VK_CONTROL);
		webDriverKeyToOsKeyMap.put(Keys.RETURN.charAt(0), KeyEvent.VK_ENTER);
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("mac")) {
			webDriverKeyToOsKeyMap.put(Keys.COMMAND.charAt(0), KeyEvent.VK_META);
		} else {
			webDriverKeyToOsKeyMap.put(Keys.COMMAND.charAt(0), KeyEvent.VK_WINDOWS);
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
				Integer keyCode = getOsKeyCode(c);
				if (keyCode != null) {
					robot.keyPress(keyCode);
					robot.keyRelease(keyCode);
				} else {
					LOGGER.log(Level.INFO, ()->String.format("sendKeys: no key code found for character, numeric/type = %d/%d", 
							Character.getNumericValue(c), Character.getType(c)));
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
		LOGGER.log(Level.FINEST, ()->String.format("mouse down, device=%s", device)); 
		getRobot(device).mousePress(InputEvent.BUTTON1_DOWN_MASK);
	}

	public static void mouseUp(GraphicsDevice device) {
		LOGGER.log(Level.FINEST, ()->String.format("mouse up, device=%s", device)); 
		getRobot(device).mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	public static void mouseMove(GraphicsDevice device, Long tickDuration, Integer movePosX, Integer movePosY) {
		LOGGER.log(Level.FINEST, ()->String.format("move mouse to (%s,%s), tick duration=%s, device=%s", 
				movePosX, movePosY, tickDuration, device));
		// TODO implement move ticks
		getRobot(device).mouseMove(movePosX, movePosY);
	}

	public static void keyDown(GraphicsDevice device, char c) {
		LOGGER.log(Level.FINEST, ()->String.format("key down, c=%c, device=%s", c, device)); 
		Integer osKey = getOsKeyCode(c);
		if (osKey != null) {
			getRobot(device).keyPress(osKey);
		} else {
			LOGGER.log(Level.INFO, ()-> String.format("key down: no key code found for character, numeric/type = %d/%d", 
					Character.getNumericValue(c), Character.getType(c)));
		}
	}

	public static void keyUp(GraphicsDevice device, char c) {
		LOGGER.log(Level.FINEST, ()->String.format("key up, c=%c, device=%s", c, device)); 
		Integer osKey = getOsKeyCode(c);
		if (osKey != null) {
			getRobot(device).keyRelease(osKey);
		} else {
			LOGGER.log(Level.INFO, ()->String.format("key up: no key code found for character, numeric/type = %d/%d", 
					Character.getNumericValue(c), Character.getType(c)));
		}
	}

	private static Integer getOsKeyCode(Character c) {
		int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
		if (keyCode != KeyEvent.VK_UNDEFINED) {
			return keyCode;
		} else {
			return webDriverKeyToOsKeyMap.get(c);
		}
	}
	
	public static void main(String[] args) {
		showScreenDevices();
		clacVirtualBounds();
		clacDefaultVirtualBounds();
	}

}

