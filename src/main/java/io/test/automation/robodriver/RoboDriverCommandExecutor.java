package io.test.automation.robodriver;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.Response;

import io.test.automation.robodriver.internal.*;

public class RoboDriverCommandExecutor implements CommandExecutor {
	
	private static Logger LOGGER = LoggerUtil.get(RoboDriverCommandExecutor.class);
	
	private Pattern xpathWithIndex = Pattern.compile("/*screen\\[(\\d+)\\]");

	private RoboDriver driver;
			
	public void setDriver(RoboDriver driver) {
		this.driver = driver;
	}
	
	// TODO use XPath API instead of simple parsing
	@Override
	public Response execute(Command command) throws IOException {
		LOGGER.log(Level.FINE, ()->String.format("command: '%s' - %s", command.getName(), command.toString()));
		Response response = new Response();
		if (DriverCommand.SEND_KEYS_TO_ACTIVE_ELEMENT.equals(command.getName())) { 
			GraphicsDevice device = RoboUtil.getDefaultDevice(); // TODO use active screen
			Robot robot = RoboUtil.getRobot(device);
			CharSequence[] keysToSend = (CharSequence[]) command.getParameters().get("value");
			RoboUtil.sendKeys(robot, keysToSend);
		} else if (DriverCommand.MOUSE_UP.equals(command.getName())) {
			GraphicsDevice device = getDevice(command.getParameters());
			RoboUtil.mouseUp(device);
		} else if (DriverCommand.MOUSE_DOWN.equals(command.getName())) {
			GraphicsDevice device = getDevice(command.getParameters());
			RoboUtil.mouseDown(device);
		} else if (DriverCommand.MOVE_TO.equals(command.getName())) {
			Map<String, ?> parameters = command.getParameters();
			if (parameters.containsKey("element")) {
				String deviceId = (String) parameters.get("element");
				GraphicsDevice device = RoboUtil.getDeviceById(deviceId);
				Robot robot = RoboUtil.getRobot(device);
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
				Robot robot = RoboUtil.getRobot(RoboUtil.getDefaultDevice());
				Point location = MouseInfo.getPointerInfo().getLocation();
				Long xoffset = (Long) parameters.get("xoffset");
				Long yoffset = (Long) parameters.get("yoffset");
				int x = location.x + new BigDecimal(xoffset).intValueExact();
				int y = location.y + new BigDecimal(yoffset).intValueExact();
				robot.mouseMove(x, y);
			}
		} else if (DriverCommand.FIND_ELEMENTS.equals(command.getName())) {
			List<RoboScreen> allElements = RoboScreen.getAllScreens(driver);
			response.setValue(allElements);
		} else if (DriverCommand.FIND_ELEMENT.equals(command.getName())) {
			Map<String, ?> parameters = command.getParameters();
			String value = parameters.get("value").toString().toLowerCase();
			if (! value.contains("screen")) {
				throw new WebDriverException("connot find '" + value + "'");
			}
			if (value.contains("default")) {
				RoboScreen screen = RoboScreen.getDefaultScreen(driver);
				response.setValue(screen);
			} else if (value.endsWith("screen")) {
				RoboScreen screen = RoboScreen.getScreen(0, driver);
				response.setValue(screen);
			} else {
				Matcher matcher = xpathWithIndex.matcher(value);
				if (matcher.find()) {
					String index = matcher.group(1);
					try {
						RoboScreen screen = RoboScreen.getScreen(Integer.parseInt(index), driver);
						response.setValue(screen);
					} catch (Exception e) {
						throw new IOException("Cannot find screen with index = '" + index + "'");
					}
				}
			}
		} else if (DriverCommand.FIND_CHILD_ELEMENT.equals(command.getName())) {
			Map<String, ?> parameters = command.getParameters();
			String id = parameters.get("id").toString();
			RoboScreen screen = RoboScreen.getScreenById(id);
			assert "xpath".equals(parameters.get("using").toString().toLowerCase());
			String value = parameters.get("value").toString().toLowerCase();
			if (! value.contains("rectangle")) {
				throw new WebDriverException("connot find child '" + value + "' from device ID '" + id + "'");
			}
			if (value.contains("dim")) {
				String t1 = value.substring(value.indexOf("dim")).replace("dim='", "");
				String t2 = t1.substring(0, t1.indexOf('\''));
				String[] dimensionValues = t2.split(",");
				if (dimensionValues.length != 4) {
					throw new RuntimeException("invalid rectangle dimension: " + value);
				}
				int x = Integer.parseInt(dimensionValues[0]);
				int y = Integer.parseInt(dimensionValues[1]);
				int widht = Integer.parseInt(dimensionValues[2]);
				int height = Integer.parseInt(dimensionValues[3]);
				response.setValue(new RoboScreenRectangle(screen, x, y, widht, height));
			} else {
				throw new RuntimeException("no dimension attribute defining x,y,widht,height found, example '//rectangle[@dim='70,80,100,200']'");
			}
		} else if (DriverCommand.NEW_SESSION.equals(command.getName())) {
			response.setValue(new HashMap<>());
			response.setSessionId(Long.toString(System.currentTimeMillis()));
		} else if (DriverCommand.ACTIONS.equals(command.getName())) {
			handleActionsW3C_Selenium_3_4(command);
		} else {
			LOGGER.log(Level.INFO, ()->String.format("ignored command: '%s' - %s", command.getName(), command.toString()));
		}
		return response;
	}

	private GraphicsDevice getDevice(Map<String, ?> parameters) {
		if (parameters.containsKey("element")) {
			return RoboUtil.getDeviceById((String)parameters.get("element"));
		} else {
			return RoboUtil.getDefaultDevice();
		}
	}

	private void handleActionsW3C_Selenium_3_4(Command command) {
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

	private void startSequenceExecutors(Collection<?> actions, List<RoboSequenceExecutor> sequences) {
		for (Object action : actions) {
			if (action instanceof Sequence) {
				sequences.add(new RoboSequenceExecutor((Sequence)action));
			} else {
				LOGGER.log(Level.SEVERE, ()->String.format("unknown action: %s", action));
			}
		}
		for (RoboSequenceExecutor roboSequence : sequences) {
			roboSequence.start();
		}
	}

}
