package io.test.automation.robodriver.internal.server;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.server.DriverProvider;

import io.test.automation.robodriver.RoboDriver;

public class RoboDriverProvider implements DriverProvider {

	@Override
	public Capabilities getProvidedCapabilities() {
		return RoboDriver.getDesiredCapabilities();
	}

	@Override
	public boolean canCreateDriverInstances() {
		return true;
	}

	@Override
	public boolean canCreateDriverInstanceFor(Capabilities capabilities) {
		return RoboDriver.BROWSER_NAME.equals(capabilities.getCapability(CapabilityType.BROWSER_NAME));
	}

	@Override
	public WebDriver newInstance(Capabilities capabilities) {
		return new RoboDriver();
	}
}
