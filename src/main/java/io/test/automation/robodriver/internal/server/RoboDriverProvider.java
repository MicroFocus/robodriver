package io.test.automation.robodriver.internal.server;

import java.util.logging.Logger;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.server.DriverProvider;

import io.test.automation.robodriver.RoboDriver;
import io.test.automation.robodriver.RoboDriverCommandExecutor;
import io.test.automation.robodriver.internal.LoggerUtil;

public class RoboDriverProvider implements DriverProvider {
	
	private static Logger LOGGER = LoggerUtil.get(RoboDriverCommandExecutor.class);

	public RoboDriverProvider() {
		LOGGER.info(()->String.format("%s created.", RoboDriverProvider.class.getSimpleName()));
	}
	
	@Override
	public Capabilities getProvidedCapabilities() {
		return RoboDriver.getDesiredCapabilities();
	}

	@Override
	public boolean canCreateDriverInstanceFor(Capabilities capabilities) {
		return RoboDriver.BROWSER_NAME.equals(capabilities.getCapability(CapabilityType.BROWSER_NAME));
	}

	@Override
	public WebDriver newInstance(Capabilities capabilities) {
		LOGGER.info(()->String.format("%s instantiated by server.", RoboDriver.class.getSimpleName()));
		return new RoboDriver();
	}
}
