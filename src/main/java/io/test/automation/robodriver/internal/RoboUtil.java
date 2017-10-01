package io.test.automation.robodriver.internal;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
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

	private static Map<String, Integer> virtualKeyNameToKeyCodeMap = new HashMap<>();
	
	static { // TODO use instance no statics
		webDriverKeyToOsKeyMap.put(Keys.NULL.charAt(0), 0);
		webDriverKeyToOsKeyMap.put(Keys.RETURN.charAt(0), KeyEvent.VK_ENTER);
		webDriverKeyToOsKeyMap.put(Keys.ZENKAKU_HANKAKU.charAt(0), KeyEvent.VK_FULL_WIDTH);
		
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("mac")) {
			webDriverKeyToOsKeyMap.put(Keys.META.charAt(0), KeyEvent.VK_META);
			webDriverKeyToOsKeyMap.put(Keys.COMMAND.charAt(0), KeyEvent.VK_META);
		} else {
			webDriverKeyToOsKeyMap.put(Keys.META.charAt(0), KeyEvent.VK_WINDOWS);
			webDriverKeyToOsKeyMap.put(Keys.COMMAND.charAt(0), KeyEvent.VK_WINDOWS);
		}
		
		for (Keys k: Keys.values()) {
			if (! webDriverKeyToOsKeyMap.containsKey(k.charAt(0))) {
				try {
					Integer keyEvent = (Integer) KeyEvent.class.getField("VK_" +  k.name()).get(null);
					if (keyEvent == null)
						System.out.println(k.name());
					webDriverKeyToOsKeyMap.put(k.charAt(0), keyEvent);
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.log(Level.SEVERE, "key mapping error in static initializer of RoboUtil", e);
				}
			}
		}
		Field[] keyEventFields = KeyEvent.class.getFields();
		for (Field f : keyEventFields) {
			if (f.getName().startsWith("VK_")) {
				try {
					Integer vkValue = (Integer) f.get(null);
					virtualKeyNameToKeyCodeMap.put(f.getName(), vkValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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
				Integer keyCode = getVirtualKeyCode(c);
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

	public static void sendKeys(Robot robot, String string) {
		sendKeys(robot, new CharSequence[] { string } );
	}

	public static void sendKeys(Robot robot, CharSequence virtualKey) {
		sendKeys(robot, new CharSequence[] { virtualKey } );
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

	// TODO throw exception if position is out of screen
	public static void mouseMove(GraphicsDevice device, Long tickDuration, Integer movePosX, Integer movePosY) {
		LOGGER.log(Level.FINEST, ()->String.format("move mouse to (%s,%s), tick duration=%s, device=%s", 
				movePosX, movePosY, tickDuration, device));
		getRobot(device).mouseMove(movePosX, movePosY);
	}

	public static void keyDown(GraphicsDevice device, char c) {
		LOGGER.log(Level.FINEST, ()->String.format("key down, c=%c, device=%s", c, device)); 
		Integer osKey = getVirtualKeyCode(c);
		if (osKey != null) {
			getRobot(device).keyPress(osKey);
		} else {
			LOGGER.log(Level.INFO, ()-> String.format("key down: no key code found for character, numeric/type = %d/%d", 
					Character.getNumericValue(c), Character.getType(c)));
		}
	}

	public static void keyUp(GraphicsDevice device, char c) {
		LOGGER.log(Level.FINEST, ()->String.format("key up, c=%c, device=%s", c, device)); 
		Integer osKey = getVirtualKeyCode(c);
		if (osKey != null) {
			getRobot(device).keyRelease(osKey);
		} else {
			LOGGER.log(Level.INFO, ()->String.format("key up: no key code found for character, numeric/type = %d/%d", 
					Character.getNumericValue(c), Character.getType(c)));
		}
	}

	static Integer getVirtualKeyCode(Character c) {
		Integer keyCode = webDriverKeyToOsKeyMap.get(c);
		if (keyCode != null) {
			return keyCode;
		}
		keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
		if (keyCode != KeyEvent.VK_UNDEFINED) {
			return keyCode;
		} else {
			return (int)c.charValue();
		}
	}
	
	/**
	 * Retrieves Java virtual key character by name.
	 * @param virtualKeyName valid names are VK_XXX constants from {@link KeyEvent}.
	 * @return virtual key character that can be used with webdriver sendKeys() methods. 
	 */
	public static CharSequence getVK(String virtualKeyName) {
		Integer vk = virtualKeyNameToKeyCodeMap.get(virtualKeyName);
		if (vk == null) {
			throw new RuntimeException(String.format("virtual key name '%s' not found", virtualKeyName));
		}
		return new Character((char)vk.intValue()).toString();
	}

	public static void main(String[] args) {
		showScreenDevices();
		clacVirtualBounds();
		clacDefaultVirtualBounds();
		
		/*
		 * The range of legal code points is now U+0000 to U+10FFFF, known as Unicode scalar value. 
		 * (Refer to the  definition of the U+n notation in the Unicode Standard.) 
		 * The set of characters from U+0000 to U+FFFF is sometimes referred to as the 
		 * Basic Multilingual Plane (BMP). Characters whose code points are greater than U+FFFF 
		 * are called supplementary characters. The Java platform uses the UTF-16 representation 
		 * in char arrays and in the String and StringBuffer classes. 
		 * In this representation, supplementary characters are represented as a pair of char values, 
		 * the first from the high-surrogates range, (\uD800-\uDBFF), 
		 * the second from the low-surrogates range (\uDC00-\uDFFF). 
		 * A char value, therefore, represents Basic Multilingual Plane (BMP) code points, 
		 * including the surrogate code points, or code units of the UTF-16 encoding. 
		 * An int value represents all Unicode code points, including supplementary code points. 
		 * The lower (least significant) 21 bits of int are used to represent Unicode code points 
		 * and the upper (most significant) 11 bits must be zero. Unless otherwise specified, 
		 * the behavior with respect to supplementary characters and surrogate char values is as follows:
		 * ...
		 * http.//www.unicode.org/glossary 
		 */
		char shiftChar = Keys.SHIFT.charAt(0);
	}

}

