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

	private Process appProcess;

	public RoboDriver() {
		this(getDesiredCapabilities());
	}

	public RoboDriver(Capabilities capabilities) {
		super(executor = new RoboDriverCommandExecutor(), capabilities);
		executor.setDriver(this);
	}

	public static DesiredCapabilities getDesiredCapabilities() {
		return new DesiredCapabilities(BROWSER_NAME, null, Platform.ANY);
	}

	@Override
	protected void startClient(Capabilities desiredCapabilities) {
		if (desiredCapabilities == null) {
			return;
		}

		String app = (String)desiredCapabilities.getCapability(APP);
		if (app != null) {
			ProcessBuilder processBuilder = new ProcessBuilder(app.split("\\s+"));
			try {
				appProcess = processBuilder.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}	

	@Override
	protected void stopClient() {
		if (getAppProcess() != null && getAppProcess().isAlive()) {
			getAppProcess().destroyForcibly();
		}
	}
	
	public Process getAppProcess() {
		return appProcess;
	}

}
