package io.test.automation.robodriver.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebDriverException;

public class ScreenXpathTest {

	@Test
	public void testDefaultScreen() {
		ScreenXpath xpath;
		xpath = new ScreenXpath("//screen[@default=true]");
		assertTrue(xpath.isDefaultScreen());
		xpath = new ScreenXpath("/screen[@default=true]");
		assertTrue(xpath.isDefaultScreen());
		xpath = new ScreenXpath("screen[@default=true]");
		assertTrue(xpath.isDefaultScreen());
		xpath = new ScreenXpath("//SCREEN[@DEFAULT=on]");
		assertTrue(xpath.isDefaultScreen());
		xpath = new ScreenXpath("//screen[@default]");
		assertTrue(xpath.isDefaultScreen());
		xpath = new ScreenXpath("/screen[@default]/rectangle");
		assertTrue(xpath.isDefaultScreen());
		xpath = new ScreenXpath("/screen[0]/rectangle");
		assertFalse(xpath.isDefaultScreen());
		xpath = new ScreenXpath("/screen");
		assertFalse(xpath.isDefaultScreen());
	}

	@Test
	public void testRectangle() {
		ScreenXpath xpath;
		xpath = new ScreenXpath("//screen[@default=true]//rectangle");
		assertTrue(xpath.isRectangle());
		xpath = new ScreenXpath("rectangle");
		assertTrue(xpath.isRectangle());	
		xpath = new ScreenXpath("/screen/rectangle");
		assertTrue(xpath.isRectangle());	
		xpath = new ScreenXpath("//screen//rectangle");
		assertTrue(xpath.isRectangle());	
		xpath = new ScreenXpath("/screen/rectangle[@dim'1,1,1,1']");
		assertTrue(xpath.isRectangle());	
		xpath = new ScreenXpath("/screen/RECTANGLE");
		assertTrue(xpath.isRectangle());	
		xpath = new ScreenXpath("/screen");
		assertFalse(xpath.isRectangle());	
	}
	
	@Test
	public void testScreenIndex() {
		ScreenXpath xpath;
		xpath = new ScreenXpath("//screen");
		assertEquals(0, xpath.getScreenIndex());
		xpath = new ScreenXpath("/screen");
		assertEquals(0, xpath.getScreenIndex());
		xpath = new ScreenXpath("screen");
		assertEquals(0, xpath.getScreenIndex());
		xpath = new ScreenXpath("ScrEEN");
		assertEquals(0, xpath.getScreenIndex());
		xpath = new ScreenXpath("/screen[0]");
		assertEquals(0, xpath.getScreenIndex());
		xpath = new ScreenXpath("/screen[1]");
		assertEquals(1, xpath.getScreenIndex());	
		xpath = new ScreenXpath("//screen[99]");
		assertEquals(99, xpath.getScreenIndex());	
		xpath = new ScreenXpath("/screen[1]/rectangle[@dim='11,21,31,41']");
		assertEquals(1, xpath.getScreenIndex());	
		xpath = new ScreenXpath("screen[99]");
		assertEquals(99, xpath.getScreenIndex());	
	}
	
	@Test
	public void testRectangleDimension() {
		ScreenXpath xpath;
		xpath = new ScreenXpath("//rectangle[@dim='1,2,3,4']");
		assertEquals("x=1,y=2,w=3,h=4", xpath.getRectangle().toString());
		xpath = new ScreenXpath("rectAngle[@dim='1,2,3,4']");
		assertEquals("x=1,y=2,w=3,h=4", xpath.getRectangle().toString());
		xpath = new ScreenXpath("//screen[0]//rectangle[@dim='11,21,31,41']");
		assertEquals("x=11,y=21,w=31,h=41", xpath.getRectangle().toString());
		xpath = new ScreenXpath("/screen[0]/rectangle[@DIM='11,  21,	31, 41']");
		assertEquals("x=11,y=21,w=31,h=41", xpath.getRectangle().toString());
		xpath = new ScreenXpath("//rectangle");
		assertNull(xpath.getRectangle());
	}
	
	@Test(expected = WebDriverException.class)
	public void testInvalidRectangleDimension() {
		ScreenXpath xpath;
		xpath = new ScreenXpath("//rectangle[@dim='1,2,3']");
		xpath.getRectangle();
	}
}
