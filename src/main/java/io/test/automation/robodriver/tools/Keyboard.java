package io.test.automation.robodriver.tools;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import io.test.automation.robodriver.internal.RoboUtil;

public class Keyboard {

	private static Frame mainFrame;
	private static Panel controlPanel;
	private static TextArea mainTextOutput;
	private static KeyListenerExt keyListener = new KeyListenerExt();
	private static RoboUtil roboUtil = new RoboUtil();

	public static void main(String[] args) {
		prepareGUI();
		printlnMessage("Initializing...");
		printlnMessage("");
		if (args.length > 0 && args[0].equals("-vkcheck")) {
			runKeyChecksThread();
		} else {
			runLogTypeKeyThread();
		}
	}
	
	private static void runLogTypeKeyThread() {
		new Thread() {
			@Override
			public void run() {
				mainTextOutput.setText("");
				printlnMessage("Type key to see corresponding VK-id:");
				printlnMessage();
				while(1==1) {
					keyListener.startRecordNextEvent();
					String keyInfo = getKeyInfo();
					printlnMessage();
					printlnMessage(keyInfo);
				}
			}
		}.start();
	};

	private static void runKeyChecksThread() {
		new Thread() {
			@Override
			public void run() {
				pause(3000);
				roboUtil.getDefaultRobot().setAutoDelay(350);
				List<String> virtualKeyNames = roboUtil.getVirtualKeyNames();
				for (String keyName : virtualKeyNames) {
					String keyInfo = "";
					roboUtil.sendKeys(roboUtil.getDefaultRobot(), roboUtil.getVirtualKeyCharSeq("VK_SPACE"));
					try {
						keyListener.startRecordNextEvent();
						System.out.println("type key: " + keyName);
						roboUtil.sendKeys(roboUtil.getDefaultRobot(), roboUtil.getVirtualKeyCharSeq(keyName));
						keyInfo = getKeyInfo();
						roboUtil.sendKeys(roboUtil.getDefaultRobot(), roboUtil.getVirtualKeyCharSeq(keyName));
					} catch (Exception e) {
						e.printStackTrace();
						keyListener.cancelRecordNextEvent();
						keyInfo = String.format("ERROR send key '%s': %s", keyName, e.getMessage());
					}
					roboUtil.sendKeys(roboUtil.getDefaultRobot(), roboUtil.getVirtualKeyCharSeq("VK_SPACE"));
					printlnMessage();
					printlnMessage(keyInfo);			
					printlnMessage();
				}
			}
		}.start();
	}

	protected static void printKeyInfo(String info) {
		printlnMessage();
		printlnMessage(info);
	}

	protected static String getKeyInfo() {
		while(keyListener.isRecordNextEvent()) { pause(50); }
		return keyListener.getInfoText();
	}
	
	private static void prepareGUI() {
		System.out.println(getToolUsageInfo());

		mainFrame = new Frame("robodriver - Keyboard Tool");
		mainFrame.setBounds(300, 300, 800, 500);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}
		});

		controlPanel = new Panel();
		controlPanel.setLayout(new BorderLayout());
		mainTextOutput = new TextArea();
		mainTextOutput.addKeyListener(keyListener);
		mainTextOutput.setFont(new Font("Courier", Font.PLAIN, 12));
		controlPanel.add("Center", mainTextOutput);

		mainFrame.add("Center", controlPanel);
		mainFrame.setVisible(true);
	}

	private static void pause(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static String getToolUsageInfo() {
		return "use option '-vkcheck' to test all VK-ids";
	}

	private static void printMessage(String text) {
		mainTextOutput.append(text);
	}
	
	private static void printlnMessage(String text) {
		mainTextOutput.append(text + "\n");
	}

	private static void printlnMessage() {
		mainTextOutput.append("\n");
	}}
