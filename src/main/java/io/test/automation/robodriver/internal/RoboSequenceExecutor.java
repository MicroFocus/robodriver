package io.test.automation.robodriver.internal;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.interactions.Sequence;

import io.test.automation.robodriver.RoboDriverCommandExecutor;

public class RoboSequenceExecutor extends Thread {

	private static AtomicInteger CNT = new AtomicInteger(0);

	private static Logger LOGGER = LoggerUtil.get(RoboDriverCommandExecutor.class);

	private Map<String, Object> sequenceActionsMap;
	private Object tickLock = new Object();
	private boolean allTicksCompleted;
	private boolean nextTickCompleted;
	private RoboUtil roboUtil = new RoboUtil();

	public RoboSequenceExecutor(Sequence seq) {
		this(seq.encode());
	}

	public RoboSequenceExecutor(Map<String, Object> seqMap) {
		super("robo-sequence-" + CNT.incrementAndGet());
		this.sequenceActionsMap = seqMap;
	}

	@Override
	public void run() {
		try {
			executeTickByTick();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			assert nextTickCompleted;
			allTicksCompleted = true;
		}
	}

	@Override
	public void start() {
		synchronized (tickLock) {
			super.start();
			try {
				tickLock.wait(); // until thread is ready to execute ticks
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 
	 * @return true if all ticks of this sequence are executed
	 */
	public boolean startNextTickAndIsAllExecuted() {
		if (!allTicksCompleted) {
			synchronized (tickLock) {
				nextTickCompleted = false;
				tickLock.notify();
			}
		}
		return allTicksCompleted;
	}

	public void waitForNextTickCompleted() throws InterruptedException {
		synchronized (tickLock) {
			while (!nextTickCompleted) {
				tickLock.wait(1000);
			}
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private void executeTickByTick() throws Exception {
		synchronized (tickLock) {
			LOGGER.log(Level.FINE, () -> String.format("ACTION sequence raw data: %s", sequenceActionsMap));
			String seqType = (String) sequenceActionsMap.get("type");
			List<Object> sequenceActionList = (List<Object>) sequenceActionsMap.get("actions");
			GraphicsDevice device = null; // target device must be defined by one of the following actions
			LastTickElementScreenCoordinates state = new LastTickElementScreenCoordinates();
			tickLock.notify(); // ready to execute first tick
			for (Object actionObject : sequenceActionList) {
				try { // TODO extract method for single tick execution
					tickLock.wait();
					Map<String, Object> actionDetails = (Map<String, Object>) actionObject;
					LOGGER.log(Level.FINE, () -> {
						return String.format("[%s] action_details list: %s", seqType, actionDetails);
					});

					Object target = actionDetails.get("origin");
					if (target == null) {
						LOGGER.log(Level.FINEST,
								() -> String.format("[%s] no screen device defined, using default screen.", seqType));
						device = roboUtil.getDefaultDevice();
					} else if (target instanceof RoboScreen) {
						device = ((RoboScreen) target).getDevice();
						Rectangle screenRectangle = device.getDefaultConfiguration().getBounds();
						state.setElementCoordinates(screenRectangle);
					} else if (target instanceof RoboScreenRectangle) {
						RoboScreenRectangle targetRectangle = (RoboScreenRectangle) target;
						state.setElementCoordinates(targetRectangle.getRectAwt());
						device = targetRectangle.getScreen().getDevice();
					} else if (target instanceof Map && ((Map<String, Object>) target).containsKey("ELEMENT")) {
						// needed for Actions targets, see also
						// org.openqa.selenium.remote.server.handler.FindElement.call()
						String elementId = (String) ((Map<String, Object>) target).get("ELEMENT");
						RoboElement targetElement = RoboElement.getByElementId(elementId);
						if (targetElement != null) {
							state.setElementCoordinates(targetElement.getRectAwt());
							device = targetElement.getDevice();
						} else {
							LOGGER.log(Level.SEVERE,
									String.format("[%s] no element found for ID = '%s'.", seqType, elementId));
						}
					}
					if (device == null) { // expected that device was determined by the 'origin' of a previous
											// action
						LOGGER.log(Level.SEVERE, String.format(
								"[%s] no device defined, maybe invalid target element type '%s', '%s' is needed.",
								seqType, target.toString(), RoboScreen.class.getName()));
						return;
					}
					final String actionDetailType = (String) actionDetails.get("type");
					LOGGER.log(Level.FINEST, () -> String.format("[%s] action_type = '%s'", seqType, actionDetailType));
					switch (actionDetailType) {
					// pointer actions
					case "pointerMove":
						int moveDuration = toInt(actionDetails.get("duration"));
						int offsetX = toInt(actionDetails.get("x"));
						int offsetY = toInt(actionDetails.get("y"));
						if ("pointer".equals(target)) {
							roboUtil.mouseMoveOffset(device, moveDuration, offsetX, offsetY);
						} else if (offsetX != 0 || offsetY != 0) {
							// work around: used to indicate relative move to target element upper left
							// corner
							int movePosX = state.xElementLeftUpper + offsetX;
							int movePosY = state.yElementLeftUpper + offsetY;
							roboUtil.mouseMove(device, moveDuration, movePosX, movePosY);
						} else { // W3C-default is target element center
							roboUtil.mouseMove(device, moveDuration, state.xElementCenter, state.yElementCenter);
						}
						break;
					case "pointerDown":
						int button = toInt(actionDetails.get("button"));
						roboUtil.mouseDown(device, button);
						break;
					case "pointerUp":
						button = toInt(actionDetails.get("button"));
						roboUtil.mouseUp(device, button);
						break;
					// key actions
					case "pause":
						int durationInMs = toInt(actionDetails.get("duration"));
						if (durationInMs > 0) {
							roboUtil.sleep(durationInMs);
						}
						break;
					case "keyDown":
						String value = (String) actionDetails.get("value");
						roboUtil.keyDown(device, value.charAt(0));
						break;
					case "keyUp":
						value = (String) actionDetails.get("value");
						roboUtil.keyUp(device, value.charAt(0));
						break;
					default:
						LOGGER.log(Level.FINE, () -> {
							return String.format("[%s] unknown_action type '%s'", seqType, actionDetailType);
						});
					}
				} finally {
					nextTickCompleted = true;
					tickLock.notify(); // ready to execute next tick (keep in finally block!)
				}
			}
		}
	}

	private int toInt(Object number) {
		if (number instanceof String) {
			return Integer.parseInt((String) number);
		}
		if (number instanceof Number) {
			return ((Number) number).intValue();
		}
		throw new RuntimeException(String.format("Invalid number %s", number));
	}

	private class LastTickElementScreenCoordinates {
		int xElementCenter = 0;
		int yElementCenter = 0;
		int xElementLeftUpper = 0;
		int yElementLeftUpper = 0;

		public void setElementCoordinates(Rectangle rectAwt) {
			this.xElementCenter = rectAwt.x + toInt(roboUtil.calcPixelCenter(rectAwt.width));
			this.yElementCenter = rectAwt.y + toInt(roboUtil.calcPixelCenter(rectAwt.height));
			this.xElementLeftUpper = rectAwt.x;
			this.yElementLeftUpper = rectAwt.y;
		}
	}
}
