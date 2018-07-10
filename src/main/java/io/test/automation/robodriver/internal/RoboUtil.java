package io.test.automation.robodriver.internal;

import java.awt.AWTException;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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

	private static Map<Character, Integer> webDriverKeyToVirtualKeyMap = new HashMap<>();
	
	private static Map<Integer, Character> virtualKeyToWebDriverKeyMap = new HashMap<>();

	private static Map<String, Integer> virtualKeyNameToKeyCodeMap = new HashMap<>();

	private static Map<Integer, String> virtualKeyCodeToNameCodeMap = new HashMap<>();

	private static volatile boolean isInitializedKeyMappingsDone;

	private synchronized static void initializeKeyMappings() { 
		if (isInitializedKeyMappingsDone) {
			return;
		}
		addWebDriverKeyToMaps(Keys.NULL, KeyEvent.VK_UNDEFINED);
		addWebDriverKeyToMaps(Keys.RETURN, KeyEvent.VK_ENTER);
		addWebDriverKeyToMaps(Keys.ZENKAKU_HANKAKU, KeyEvent.VK_FULL_WIDTH);
		
		String osName = System.getProperty("os.name", "").toLowerCase();
		if (osName.contains("mac")) {
			addWebDriverKeyToMaps(Keys.META, KeyEvent.VK_META);
			addWebDriverKeyToMaps(Keys.COMMAND, KeyEvent.VK_META);
		} else {
			addWebDriverKeyToMaps(Keys.META, KeyEvent.VK_WINDOWS);
			addWebDriverKeyToMaps(Keys.COMMAND, KeyEvent.VK_WINDOWS);
		}
		
		for (Keys k: Keys.values()) {
			if (! webDriverKeyToVirtualKeyMap.containsKey(k.charAt(0))) {
				try {
					Integer keyEvent = (Integer) KeyEvent.class.getField("VK_" +  k.name()).get(null);
					if (keyEvent == null)
						System.out.println(k.name());
					addWebDriverKeyToMaps(k, keyEvent);
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
					virtualKeyCodeToNameCodeMap.put(vkValue, f.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		isInitializedKeyMappingsDone = true;
	}
	
	public RoboUtil() {
		if (!isInitializedKeyMappingsDone) {
			initializeKeyMappings();
		}
		assert isInitializedKeyMappingsDone;
	}
	
	private static void addWebDriverKeyToMaps(Keys webDriverKey, Integer virtualKey) {
		webDriverKeyToVirtualKeyMap.put(webDriverKey.charAt(0), virtualKey);
		virtualKeyToWebDriverKeyMap.put(virtualKey, webDriverKey.charAt(0));
	}

	public void showScreenDevices() {
		GraphicsDevice[] gs = getGraphicsDevices();
		GraphicsDevice defaultScreenDevice = getDefaultDevice();
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

	public void clacVirtualBounds() {
		Rectangle virtualBounds = new Rectangle();
		GraphicsDevice[] gdevices = getGraphicsDevices();
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

	public void clacDefaultVirtualBounds() {
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

	public void createScreenShot(Rectangle rectangle) {
		try {
			Robot robot = new Robot();
			BufferedImage screenCapture = robot.createScreenCapture(rectangle);
			ImageIO.write(screenCapture, "png", new File("./screenshot.png"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GraphicsDevice getDefaultDevice() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return ge.getDefaultScreenDevice();
	}

	public GraphicsDevice getDeviceByIndex(int index) {
		GraphicsDevice[] screenDevices = getGraphicsDevices();
		return screenDevices[index];
	}

	public GraphicsDevice getDeviceById(String deviceId) {
		GraphicsDevice[] screenDevices = getGraphicsDevices();
		for (GraphicsDevice device : screenDevices) {
			if (deviceId.toLowerCase().equals(device.getIDstring().toLowerCase())) {
				return device;
			}
		}
		return null;
	}

	public List<RoboScreen> getAllScreens(RemoteWebDriver driver) {
		List<RoboScreen> result = new ArrayList<>();
		GraphicsDevice[] screenDevices = getGraphicsDevices();
		for (GraphicsDevice graphicsDevice : screenDevices) {
			result.add(RoboScreen.getInstance(graphicsDevice, driver));
		}
		return result;
	}

	public GraphicsDevice[] getGraphicsDevices() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screenDevices = ge.getScreenDevices();
		return screenDevices;
	}

	public void sendKeys(Robot robot, CharSequence[] keysToSend) {
		for (CharSequence charSeq : keysToSend) {
			CharSequence charSeqToProcess; 
			if ((charSeqToProcess = getVirtualKeyCharSeq(charSeq.toString())) == null) {
				charSeqToProcess = charSeq;
			}
			sendKeysOfCharSeq(robot, charSeqToProcess);
		}
	}

	private void sendKeysOfCharSeq(Robot robot, CharSequence charSeqToProcess) {
		for (int i = 0; i < charSeqToProcess.length(); i++) {
			char c = charSeqToProcess.charAt(i);
			try {
				Integer keyCode = getVirtualKeyCode(c);
				if (keyCode != null) {
					robot.keyPress(keyCode);
					robot.keyRelease(keyCode);
				} else {
					LOGGER.log(Level.INFO, ()->String.format("sendKeys: no key code found for character, numeric/type = %d/%d", 
							Character.getNumericValue(c), Character.getType(c)));
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, ()->String.format("send key '%c' (%h) failed: " + e.getMessage(), c, (int)c));
				throw new RuntimeException(e);
			}
		}
	}

	public void sendKeys(Robot robot, String string) {
		sendKeys(robot, new CharSequence[] { string } );
	}

	public void sendKeys(Robot robot, CharSequence charSeq) {
		sendKeys(robot, new CharSequence[] { charSeq } );
	}

	public Robot getRobot(GraphicsDevice device) {
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
	
	public Robot getDefaultRobot() {
		return getRobot(getDefaultDevice());
	}

	public void mouseDown(GraphicsDevice device, Integer button) {
		LOGGER.log(Level.FINEST, ()->String.format("mouse down, device=%s", device)); 
		int buttonMask = getInputEventMouseButtonMask(button);
		getRobot(device).mousePress(buttonMask);
	}

	private int getInputEventMouseButtonMask(Integer webDriverButtonId) {
		int buttonMask = InputEvent.BUTTON1_DOWN_MASK;
		if (webDriverButtonId == 2) { // right mouse button
			buttonMask = InputEvent.BUTTON3_DOWN_MASK;
		}
		return buttonMask;
	}

	public void mouseUp(GraphicsDevice device, Integer button) {
		LOGGER.log(Level.FINEST, ()->String.format("mouse up, device=%s", device)); 
		int buttonMask = getInputEventMouseButtonMask(button);
		getRobot(device).mouseRelease(buttonMask);
	}

	// TODO throw exception if position is out of screen
	public void mouseMove(GraphicsDevice device, int moveDurationInMs, int movePosX, int movePosY) {
		LOGGER.log(Level.FINEST, ()->String.format("move mouse to (%s,%s), tick duration=%s, device=%s", 
				movePosX, movePosY, moveDurationInMs, device));
		getRobot(device).mouseMove(movePosX, movePosY);
		sleep(moveDurationInMs);
	}

	public void keyDown(GraphicsDevice device, char c) {
		LOGGER.log(Level.FINEST, ()->String.format("key down, c=%c, device=%s", c, device)); 
		Integer osKey = getVirtualKeyCode(c);
		if (osKey != null) {
			getRobot(device).keyPress(osKey);
		} else {
			LOGGER.log(Level.INFO, ()-> String.format("key down: no key code found for character, numeric/type = %d/%d", 
					Character.getNumericValue(c), Character.getType(c)));
		}
	}

	public void keyUp(GraphicsDevice device, char c) {
		LOGGER.log(Level.FINEST, ()->String.format("key up, c=%c, device=%s", c, device)); 
		Integer osKey = getVirtualKeyCode(c);
		if (osKey != null) {
			getRobot(device).keyRelease(osKey);
		} else {
			LOGGER.log(Level.INFO, ()->String.format("key up: no key code found for character, numeric/type = %d/%d", 
					Character.getNumericValue(c), Character.getType(c)));
		}
	}
	
	Integer getVirtualKeyCode(Character c) {
		Integer keyCode = webDriverKeyToVirtualKeyMap.get(c);
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

	public String getWebDriverKeyName(int virtualKeyCode) {
		Character webDriverKeyUnicode = virtualKeyToWebDriverKeyMap.get(virtualKeyCode);
		if (webDriverKeyUnicode == null) {
			return "<NO VK>"; 
		}
		Keys keyFromUnicode = Keys.getKeyFromUnicode(webDriverKeyUnicode);
		return (keyFromUnicode == null ? "<NO NAME>" : keyFromUnicode.name());
	}
	
	public List<String> getVirtualKeyNames() {
		List<String> names = new ArrayList<>(virtualKeyNameToKeyCodeMap.keySet());
		Collections.sort(names);
		return names;
	}

	public String getVirtualKeyName(int extKeyCode) {
		String vkName = virtualKeyCodeToNameCodeMap.get(extKeyCode);
		if (vkName == null) {
			return String.format("Unknown VK name for %h", extKeyCode);
		}
		return vkName;
	}

	/**
	 * Retrieves Java virtual key character by name.
	 * @param virtualKeyName valid names are VK_XXX constants from {@link KeyEvent}.
	 * @return virtual key character that can be used with webdriver sendKeys() methods, or null if not found VK
	 */
	public CharSequence getVirtualKeyCharSeq(String virtualKeyName) {
		Integer vk = virtualKeyNameToKeyCodeMap.get(virtualKeyName);
		if (vk == null) {
			return null;
		}
		return Character.valueOf((char)vk.intValue()).toString();
	}
	
	public String getScreenshot(GraphicsDevice device) throws IOException {
		Rectangle screenRectangle = device.getDefaultConfiguration().getBounds();
		return getScreenshot(device, screenRectangle);
	}

	public String getScreenshot(GraphicsDevice device, Rectangle rectangle) throws IOException {
		Robot robot = getRobot(device);
		BufferedImage capture = robot.createScreenCapture(rectangle);
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		OutputStream output = Base64.getEncoder().wrap(result);
		ImageIO.write(capture, "png", output);
		return result.toString();
	}

	public void click(GraphicsDevice device, Rectangle rectangle) {
		Robot robot = getRobot(device);
		int x = rectangle.x + rectangle.width/2;
		int y = rectangle.y + rectangle.height/2;
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	/**
	 * Compares images exactly, dimensions and color of every pixel must match.
	 * 
	 * @param image1
	 * @param image2
	 * @return true if images match exactly
	 * @throws IOException
	 */
	public boolean matchImages(File image1, File image2) throws IOException {
		BufferedImage i1 = ImageIO.read(image1);
		BufferedImage i2 = ImageIO.read(image2);
		return matchImages(i1, i2);
	}

	public boolean matchImages(BufferedImage i1, BufferedImage i2) {
		boolean matchSize = i1.getWidth() == i2.getWidth() && i1.getHeight() == i2.getHeight();
		if (!matchSize) {
			return false;
		}
		// compare pixels
		for (int x = 0; x < i1.getWidth(); x++) {
			for (int y = 0; y < i1.getHeight(); y++) {
				if (i1.getRGB(x,y) != i2.getRGB(x, y)) { 
					return false;
				}
			}
		}
		return true;
	}

	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		RoboUtil roboUtil = new RoboUtil();
		roboUtil.showScreenDevices();
		roboUtil.clacVirtualBounds();
		roboUtil.clacDefaultVirtualBounds();
		
		/*
		 * UTF note:
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
	}

}

