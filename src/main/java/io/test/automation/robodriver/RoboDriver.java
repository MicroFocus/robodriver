package io.test.automation.robodriver;

import java.awt.Robot;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Exposes a Selenium client API of native OS actions for the purposes of test automation, self-running demos, 
 * and other applications where control of the mouse and keyboard is needed.
 * This is backed by the Java standard class {@link Robot}, see also its documentation and limitations.
 */
public class RoboDriver extends RemoteWebDriver {

	public static final String BROWSER_NAME = "io.test.automation.robodriver";

	/**
	 * Capability that defines the command line to be executed on startup.
	 */
	public static String APP = "app";

	private static RoboDriverCommandExecutor executor;

	private static int ID = 1;

	public RoboDriver() {
		this(getDesiredCapabilities());
	}

	public RoboDriver(Capabilities capabilities) {
		super(executor = new RoboDriverCommandExecutor(), capabilities);
		executor.setDriver(this);
		setSessionId("robodriver-" + Integer.toString(ID++));
	}

	public static DesiredCapabilities getDesiredCapabilities() {
		return new DesiredCapabilities(BROWSER_NAME, null, Platform.ANY);
	}

public Process getAppProcess() {
		return executor.getAppProcess();
	}

}
