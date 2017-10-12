package io.test.automation.robodriver.tools;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import io.test.automation.robodriver.internal.RoboUtil;

public class KeyListenerExt implements KeyListener {
	private String infoText;
	private int extKeyCode;
	private boolean recordNextEvent;
	private RoboUtil roboUtil = new RoboUtil();
	
	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("keyTyped:    " + e.toString());
	}

	@Override
	public void keyPressed(KeyEvent e) {
		char keyChar = e.getKeyChar();
		String keyText = KeyEvent.getKeyText(e.getKeyCode());
		extKeyCode = e.getExtendedKeyCode();
		infoText = String.format("Keys.%-15s %-30s (key=%s, char='%c', ext-code=0x%h)", 
				roboUtil.getWebDriverKeyName(extKeyCode), 
				roboUtil.getVirtualKeyName(extKeyCode),
				keyText, keyChar, extKeyCode)
				;
		recordNextEvent = false;
		
		System.out.println(infoText);
		System.out.println("keyPressed:  " + e.toString());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println("keyReleased: " + e.toString());
	}

	public String getInfoText() {
		return infoText;
	}

	public boolean isRecordNextEvent() {
		return recordNextEvent;
	}
	
	public void startRecordNextEvent() {
		recordNextEvent = true;
	}
	
	public void cancelRecordNextEvent() {
		recordNextEvent = false;
	}
	
	public int getExtKeyCode() {
		return extKeyCode;
	}
}

//keyPressed:  java.awt.event.KeyEvent[KEY_PRESSED,keyCode=18,keyText=Alt,keyChar=Undefined keyChar,modifiers=Alt,extModifiers=Alt,keyLocation=KEY_LOCATION_LEFT,rawCode=18,primaryLevelUnicode=0,scancode=56,extendedKeyCode=0x12] on text0
//keyReleased: java.awt.event.KeyEvent[KEY_RELEASED,keyCode=18,keyText=Alt,keyChar=Undefined keyChar,keyLocation=KEY_LOCATION_LEFT,rawCode=18,primaryLevelUnicode=0,scancode=56,extendedKeyCode=0x12] on text0

//keyPressed:  java.awt.event.KeyEvent[KEY_PRESSED,keyCode=65,keyText=A,keyChar='a',keyLocation=KEY_LOCATION_STANDARD,rawCode=65,primaryLevelUnicode=97,scancode=30,extendedKeyCode=0x41] on text0
//keyTyped:    java.awt.event.KeyEvent[KEY_TYPED,keyCode=0,keyText=Unknown keyCode: 0x0,keyChar='a',keyLocation=KEY_LOCATION_UNKNOWN,rawCode=0,primaryLevelUnicode=0,scancode=0,extendedKeyCode=0x0] on text0
//keyReleased: java.awt.event.KeyEvent[KEY_RELEASED,keyCode=65,keyText=A,keyChar='a',keyLocation=KEY_LOCATION_STANDARD,rawCode=65,primaryLevelUnicode=97,scancode=30,extendedKeyCode=0x41] on text0


