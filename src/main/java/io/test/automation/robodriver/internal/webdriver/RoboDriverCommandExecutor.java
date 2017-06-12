package io.test.automation.robodriver.internal.webdriver;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.Response;

import io.test.automation.robodriver.internal.RoboScreen;
import io.test.automation.robodriver.internal.RoboUtil;

public class RoboDriverCommandExecutor implements CommandExecutor {
	
	Pattern xpathWithIndex = Pattern.compile("/*screen\\[(\\d+)\\]");
			
	@Override
	public Response execute(Command command) throws IOException {
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
			List<RoboScreen> allElements = RoboScreen.getAllScreens();
			response.setValue(allElements);
		} else if (DriverCommand.FIND_ELEMENT.equals(command.getName())) {
			Map<String, ?> parameters = command.getParameters();
			String value = parameters.get("value").toString().toLowerCase();
			if (value.contains("screen")) {
				if (value.contains("default")) {
					RoboScreen screen = RoboScreen.getDefaultScreen();
					response.setValue(screen);
				} else if (value.endsWith("screen")) {
					RoboScreen screen = RoboScreen.getScreen(0);
					response.setValue(screen);
				} else {
					Matcher matcher = xpathWithIndex.matcher(value);
					if (matcher.find()) {
						try {
							RoboScreen screen = RoboScreen.getScreen(Integer.parseInt(matcher.group(1)));
							response.setValue(screen);
						} catch (Exception e) {
							// ignore
						}
					}
				}
			}
			else {
				throw new WebDriverException("connot find '" + value + "'");
			}
        } else if (DriverCommand.NEW_SESSION.equals(command.getName())) {
			response.setValue(new HashMap<>());
			response.setSessionId(Long.toString(System.currentTimeMillis()));
		} else if (DriverCommand.ACTIONS.equals(command.getName())) {
			System.out.println(command);
			command.getParameters().get("actions");
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

}
