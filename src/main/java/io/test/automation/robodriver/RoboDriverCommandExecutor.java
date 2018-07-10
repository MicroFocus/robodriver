package io.test.automation.robodriver;

import java.awt.GraphicsDevice;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.ErrorCodes;
import org.openqa.selenium.remote.Response;

import io.test.automation.robodriver.internal.ImageUtil;
import io.test.automation.robodriver.internal.LoggerUtil;
import io.test.automation.robodriver.internal.RoboImage;
import io.test.automation.robodriver.internal.RoboScreen;
import io.test.automation.robodriver.internal.RoboScreenRectangle;
import io.test.automation.robodriver.internal.RoboSequenceExecutor;
import io.test.automation.robodriver.internal.RoboUtil;
import io.test.automation.robodriver.internal.ScreenXpath;

public class RoboDriverCommandExecutor implements CommandExecutor {
	
	private static Logger LOGGER = LoggerUtil.get(RoboDriverCommandExecutor.class);
	
	private RoboDriver driver;

	private RoboUtil roboUtil;

	private Process appProcess;
			
	public void setDriver(RoboDriver driver) {
		this.driver = driver;
	}
	
	public RoboDriverCommandExecutor() {
		roboUtil = new RoboUtil();
	}
	
	@Override
	public Response execute(Command command) throws IOException {
		try {
			return executeImpl(command);
		} catch (Exception e) {
			e.printStackTrace(); // TODO error handling
			throw e;
		}
	}
	
	public Response executeImpl(Command command) throws IOException {
		LOGGER.log(Level.FINE, ()->String.format("command: '%s' - %s", command.getName(), command.toString()));
		Response response = new Response(command.getSessionId());
		switch(command.getName()) {
		case DriverCommand.SCREENSHOT:
			execScreenshot(response);
			break;
		case DriverCommand.SEND_KEYS_TO_ACTIVE_ELEMENT:
			execSendKeysToActiveElement(command);
			break;
		case DriverCommand.MOUSE_UP:
			execMouseUp(command);
			break;
		case DriverCommand.MOUSE_DOWN:
			execMouseDown(command);
			break;
		case DriverCommand.MOVE_TO:
			execMoveTo(command);
			break;
		case DriverCommand.FIND_ELEMENTS:
			execFindElements(response);
			break;
		case DriverCommand.FIND_ELEMENT:
			execFindElement(command, response);
			break;
		case DriverCommand.FIND_CHILD_ELEMENT:
			execFindChildElement(command, response);
			break;
		case DriverCommand.ELEMENT_SCREENSHOT:
			execElementScreenshot(command, response);
			break;
		case DriverCommand.NEW_SESSION:
			execNewSession(command, response);
			break;
		case DriverCommand.ACTIONS:
			execActionsW3C(command);
			break;
		case DriverCommand.CLICK_ELEMENT: 
			execClickElement(command);
			break;
		case DriverCommand.QUIT: 
			execQuit();
			break;
		default:
			LOGGER.log(Level.INFO, ()->String.format("ignored command: '%s' - %s", command.getName(), command.toString()));
		}
		response.setState("success");
		response.setStatus(ErrorCodes.SUCCESS);
		return response;
	}

	private void execClickElement(Command command) {
		Map<String, ?> parameters = command.getParameters();
		String id = parameters.get("id").toString();
		if (id != null && id.startsWith("rectangle")) {
			RoboScreenRectangle rectangle = RoboScreenRectangle.get(id);
			rectangle.click();
		} else if (id != null && id.startsWith("screen")) {
			RoboScreen screen = RoboScreen.getScreenById(id);
			screen.click();
		} else {
			throw new WebDriverException(String.format("invalid id '%s', cannot run command '%s'", id, command));
		}
	}

	private void execNewSession(Command command, Response response) {
		Map<String, ?> parameters = command.getParameters();
		startClient((Capabilities)parameters.get("desiredCapabilities"));
		String sessionId = UUID.randomUUID().toString();
		HashMap<String, Object> values = new HashMap<>();
		HashMap<String, Object> capabs = new HashMap<>();
		values.put("state", "success");
		values.put("sessionId", sessionId);
		values.put("capabilities", capabs);
		capabs.put("robo:screenCount", (new RoboUtil()).getGraphicsDevices().length);
		response.setValue(values);
	}

	private void execElementScreenshot(Command command, Response response) throws IOException {
		Map<String, ?> parameters = command.getParameters();
		String id = parameters.get("id").toString();
		if (id != null && id.startsWith("rectangle")) {
			RoboScreenRectangle rectangle = RoboScreenRectangle.get(id);
			response.setValue(rectangle.getScreenshot());
		} else if (id != null && id.startsWith("screen")) {
			RoboScreen screen = RoboScreen.getScreenById(id);
			String screenshot = roboUtil.getScreenshot(screen.getDevice());
			response.setValue(screenshot);
		} else {
			throw new WebDriverException(String.format("invalid id '%s', cannot run command '%s'", id, command));
		}
	}

	private void execFindChildElement(Command command, Response response) {
		Map<String, ?> parameters = command.getParameters();
		String id = parameters.get("id").toString();
		RoboScreen screen = RoboScreen.getScreenById(id);
		assert "xpath".equals(parameters.get("using").toString().toLowerCase());
		String value = parameters.get("value").toString().toLowerCase();
		ScreenXpath xpath = new ScreenXpath(value);
		if (! xpath.isRectangle()) {
			throw new WebDriverException("cannot find child '" + value + "' from device ID '" + id + "'");
		}
		Rectangle rectangle = xpath.getRectangle();
		if (rectangle == null) {
			rectangle = screen.getRectAwt();
		}
		response.setValue(new RoboScreenRectangle(screen, rectangle.x, rectangle.y, rectangle.width, rectangle.height));
	}

	private void execFindElement(Command command, Response response) {
		Map<String, ?> parameters = command.getParameters();
		String value = parameters.get("value").toString();
		ScreenXpath xpath = new ScreenXpath(value);
		RoboScreen screen;
		if (xpath.isDefaultScreen()) {
			screen = RoboScreen.getDefaultScreen(driver);
		} else {
			int screenIdx = xpath.getScreenIndex();
			try {
				screen = RoboScreen.getScreen(screenIdx, driver);
			} catch (Exception e) {
				throw new NoSuchElementException("cannot find screen with index = '" + screenIdx + "'");
			}
		}
		if (xpath.isRectangleByDim()) {
			Rectangle rectangle = xpath.getRectangle();
			response.setValue(new RoboScreenRectangle(screen, rectangle));
		} else if (xpath.isRectangleByImg()) {
			RoboImage i = new RoboImage(xpath.getImgUriOrFile());
			Rectangle rectangle = new ImageUtil().findRectangle(screen, i);
			if (rectangle == null) {
				throw new NoSuchElementException("cannot find image '" + xpath.getImgUriOrFile() + "' on screen.");
			}
			response.setValue(new RoboScreenRectangle(screen, rectangle));
		} else {
			response.setValue(screen);
		}
	}

	private void execFindElements(Response response) {
		List<RoboScreen> allElements = RoboScreen.getAllScreens(driver);
		response.setValue(allElements);
	}

	private void execMoveTo(Command command) {
		Map<String, ?> parameters = command.getParameters();
		if (parameters.containsKey("element")) {
			String deviceId = (String) parameters.get("element");
			GraphicsDevice device = roboUtil.getDeviceById(deviceId);
			Robot robot = roboUtil.getRobot(device);
			if (parameters.containsKey("xoffset")) {
				int xoffset =  new BigDecimal((Long)parameters.get("xoffset")).intValueExact();
				int yoffset = new BigDecimal((Long)parameters.get("yoffset")).intValueExact();
				robot.mouseMove(xoffset, yoffset);
			} else {
				// move to center
				Rectangle bounds = device.getDefaultConfiguration().getBounds();
				robot.mouseMove((int) bounds.getCenterX(), (int) bounds.getCenterY());
			}
		} else if (parameters.containsKey("xoffset")) {
			Robot robot = roboUtil.getRobot(roboUtil.getDefaultDevice());
			Point location = MouseInfo.getPointerInfo().getLocation();
			Long xoffset = (Long) parameters.get("xoffset");
			Long yoffset = (Long) parameters.get("yoffset");
			int x = location.x + new BigDecimal(xoffset).intValueExact();
			int y = location.y + new BigDecimal(yoffset).intValueExact();
			robot.mouseMove(x, y);
		}
	}

	private void execMouseDown(Command command) {
		GraphicsDevice device = getDevice(command.getParameters());
		roboUtil.mouseDown(device, 1);
	}

	private void execMouseUp(Command command) {
		GraphicsDevice device = getDevice(command.getParameters());
		roboUtil.mouseUp(device, 1);
	}

	private void execSendKeysToActiveElement(Command command) {
		GraphicsDevice device = roboUtil.getDefaultDevice(); // TODO use active screen
		Robot robot = roboUtil.getRobot(device);
		CharSequence[] keysToSend = (CharSequence[]) command.getParameters().get("value");
		roboUtil.sendKeys(robot, keysToSend);
	}

	private void execActionsW3C(Command command) {
		try {
			Object actionsObject = command.getParameters().get("actions");
			if (actionsObject instanceof Collection<?>) {
				Collection<?> actions = (Collection<?>) actionsObject;
				List<RoboSequenceExecutor> sequences = new ArrayList<>();
				startSequenceExecutors(actions, sequences);
				executeSequencesTickByTick(sequences);
			} else {
				LOGGER.log(Level.SEVERE, ()->String.format("unknown actions type: %s", actionsObject));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void execQuit() {
		if (getAppProcess() != null && getAppProcess().isAlive()) {
			getAppProcess().destroyForcibly();
		}
	}

	private void execScreenshot(Response response) throws IOException {
		GraphicsDevice device = roboUtil.getDefaultDevice(); // TODO use active screen
		String screenshot = roboUtil.getScreenshot(device);
		response.setValue(screenshot);
	}

	private GraphicsDevice getDevice(Map<String, ?> parameters) {
		if (parameters.containsKey("element")) {
			return roboUtil.getDeviceById((String)parameters.get("element"));
		} else {
			return roboUtil.getDefaultDevice();
		}
	}

	private void executeSequencesTickByTick(List<RoboSequenceExecutor> sequences) throws Exception {
		boolean completedAllTicks; 
		LOGGER.log(Level.FINE, ()->String.format("TICK - start execution for %d sequences", sequences.size()));
		do {
			// start next tick for every sequence
			LOGGER.log(Level.FINE, "TICK");
			completedAllTicks = true;
			for (RoboSequenceExecutor roboSequence : sequences) {
				completedAllTicks &= roboSequence.startNextTickAndIsAllExecuted();
			}
			if (completedAllTicks) {
				LOGGER.log(Level.FINE, "TICK - all completed");
			}
			for (RoboSequenceExecutor roboSequence : sequences) {
				roboSequence.waitForNextTickCompleted();
			}
		} while (!completedAllTicks);
	}

	@SuppressWarnings("unchecked")
	private void startSequenceExecutors(Collection<?> actions, List<RoboSequenceExecutor> sequences) {
		for (Object action : actions) {
			if (action instanceof Map) {
				sequences.add(new RoboSequenceExecutor((Map<String, Object>) action));
			} else if (action instanceof Sequence) {
				sequences.add(new RoboSequenceExecutor((Sequence) action));
			} else {
				LOGGER.log(Level.SEVERE, ()->String.format("unknown action: %s", action));
			}
		}
		for (RoboSequenceExecutor roboSequence : sequences) {
			roboSequence.start();
		}
	}

	private void startClient(Capabilities desiredCapabilities) {
		if (desiredCapabilities == null) {
			return;
		}
	
		String app = (String)desiredCapabilities.getCapability(RoboDriver.APP);
		if (app != null) {
			ProcessBuilder processBuilder = new ProcessBuilder(app.split("\\s+"));
			try {
				appProcess = processBuilder.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Process getAppProcess() {
		return appProcess;
	}
}
