package io.test.automation.robodriver.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.util.Arrays;

import org.junit.Test;

public class ImageUtilTest {
	
	private ImageUtil imageUtil = new ImageUtil();
	
	@Test 
	public void testColorEquals() {
		int color1 = 0x550A0B0C;
		int color2 = 0x880A0B0C;
		
		assertTrue(imageUtil.equals(color1, color2, false));
		assertFalse(imageUtil.equals(color1, color2, true));
		assertTrue(imageUtil.equals(color1, color1, true));
	}
	
	@Test
	public void testFillLineHoriz() {
		int[] imgRgb6x3 = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				}; 
		int[] line = new int[3];
		
		Arrays.fill(line, 0);
		imageUtil.fillLineVert(imgRgb6x3, 0, line, 6);
		assertEquals(0x01, line[0]);
		assertEquals(0xA1, line[1]);
		assertEquals(0xB1, line[2]);
		
		Arrays.fill(line, 0);
		imageUtil.fillLineVert(imgRgb6x3, 3, line, 6);
		assertEquals(0x04, line[0]);
		assertEquals(0xA4, line[1]);
		assertEquals(0xB4, line[2]);
		
		Arrays.fill(line, 0);
		imageUtil.fillLineVert(imgRgb6x3, 5, line, 6);
		assertEquals(0x06, line[0]);
		assertEquals(0xA6, line[1]);
		assertEquals(0xB6, line[2]);
	}
	
	@Test
	public void testMatchLineHoriz1() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 
				}; 
		int[] imgRgbToFind = new int[] { 
				0x01, 0x02,
				0xA1, 0xA2,
		}; 
		int[] horizLine = new int[2];
		
		imageUtil.fillLineVert(imgRgbToFind, 1, horizLine, 2);
		// then
		assertTrue(imageUtil.matchLineVert(imgRgbSource, 0, 0, 1, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 3, 1, 1, horizLine, 6));
		
		imageUtil.fillLineVert(imgRgbToFind, 0, horizLine, 2);
		// then
		assertTrue(imageUtil.matchLineVert(imgRgbSource, 0, 0, 0, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 3, 1, 0, horizLine, 6));
	}
	
	@Test
	public void testMatchLineHoriz2() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xA3, 0xA4,
				0xB3, 0xB4,
		}; 
		int[] horizLine = new int[2];
		
		imageUtil.fillLineVert(imgRgbToFind, 1, horizLine, 2);
		// then
		assertTrue(imageUtil.matchLineVert(imgRgbSource, 2, 1, 1, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 0, 0, 1, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 3, 1, 1, horizLine, 6));
		
		imageUtil.fillLineVert(imgRgbToFind, 0, horizLine, 2);
		// then
		assertTrue(imageUtil.matchLineVert(imgRgbSource, 2, 1, 0, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 0, 0, 0, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 3, 1, 0, horizLine, 6));
	}
	
	@Test
	public void testMatchLineHoriz3() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xB5, 0xB6,
				0xC5, 0xC6,
		}; 
		int[] horizLine = new int[2];
		
		imageUtil.fillLineVert(imgRgbToFind, 1, horizLine, 2);
		// then
		assertTrue(imageUtil.matchLineVert(imgRgbSource, 4, 2, 1, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 0, 0, 1, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 3, 1, 1, horizLine, 6));
		
		imageUtil.fillLineVert(imgRgbToFind, 0, horizLine, 2);
		// then
		assertTrue(imageUtil.matchLineVert(imgRgbSource, 4, 2, 0, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 0, 0, 0, horizLine, 6));
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 3, 1, 0, horizLine, 6));
	}
	
	@Test
	public void testMatchLineHorizOutOfBounds() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xB5, 0xB6,
				0xC5, 0xC6,
		}; 
		int[] horizLine = new int[2];
		
		imageUtil.fillLineVert(imgRgbToFind, 1, horizLine, 2);
		assertFalse(imageUtil.matchLineVert(imgRgbSource, 5, 3, 1, horizLine, 6));
	}
	
	@Test
	public void testMatchFull1() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 
				}; 
		int[] imgRgbToFind = new int[] { 
				0x01, 0x02,
				0xA1, 0xA2,
		};  
		
		// then
		assertTrue(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 0, 0, 6, 2));
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 1, 0, 6, 2));
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 3, 2, 6, 2));
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 5, 2, 6, 2));
	}
	
	@Test
	public void testMatchFull2() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xA3, 0xA4,
				0xB3, 0xB4,
		};  
		
		// then
		assertTrue(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 2, 1, 6, 2));
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 1, 0, 6, 2));
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 3, 2, 6, 2));
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 5, 2, 6, 2));
	}
	
	@Test
	public void testMatchFullOutOfBounds() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xA3, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xA3, 0xA4,
				0xB3, 0xB4,
		};  
		
		// then
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 5, 3, 6, 2));
		assertFalse(imageUtil.matchFull(imgRgbSource, imgRgbToFind, 0, 3, 6, 2));
	}
	
	@Test
	public void testFindRectangle1() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xA3, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xA3, 0xA4, 0xA5,
				0xB3, 0xB4, 0xB5,
		};  
				
		Rectangle rectangle = imageUtil.findRectangle(imgRgbSource, imgRgbToFind, 6, 4, 3, 2);
		assertEquals(2, rectangle.x);
		assertEquals(1, rectangle.y);
		assertEquals(3, rectangle.width);
		assertEquals(2, rectangle.height);
	}
	
	@Test
	public void testFindRectangle2() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xA3, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0x01, 0x02, 0x03,
				0xA1, 0xA2, 0xA3,
		};  
		
		Rectangle rectangle = imageUtil.findRectangle(imgRgbSource, imgRgbToFind, 6, 4, 3, 2);
		assertEquals(0, rectangle.x);
		assertEquals(0, rectangle.y);
		assertEquals(3, rectangle.width);
		assertEquals(2, rectangle.height);
	}
	
	@Test
	public void testFindRectangle3() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xA3, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xB5, 0xB6, 
				0xC5, 0xA3, 
		};  
		
		Rectangle rectangle = imageUtil.findRectangle(imgRgbSource, imgRgbToFind, 6, 4, 2, 2);
		assertEquals(4, rectangle.x);
		assertEquals(2, rectangle.y);
		assertEquals(2, rectangle.width);
		assertEquals(2, rectangle.height);
	}
	
	@Test
	public void testFindRectangle4() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0xB4, 0xB5, 0x05, 0x06, 
				0xA1, 0xB5, 0xC4, 0xC5, 0xA5, 0xA6, 
				0xB1, 0xC5, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xA3, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xB4, 0xB5, 0xB6, 
				0xC4, 0xC5, 0xA3, 
		}; 
		
		Rectangle rectangle = imageUtil.findRectangle(imgRgbSource, imgRgbToFind, 6, 4, 3, 2);
		assertEquals(3, rectangle.x);
		assertEquals(2, rectangle.y);
		assertEquals(3, rectangle.width);
		assertEquals(2, rectangle.height);
	}
	
	@Test
	public void testFindRectangleNone() {
		int[] imgRgbSource = new int[] { 
				0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 
				0xA1, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6, 
				0xB1, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6, 
				0xC1, 0xC2, 0xC3, 0xC4, 0xC5, 0xA3, 
		}; 
		int[] imgRgbToFind = new int[] { 
				0xB5, 0xB6, 
				0xC5, 0xFF, 
		};  
		
		Rectangle rectangle = imageUtil.findRectangle(imgRgbSource, imgRgbToFind, 6, 4, 2, 2);
		assertNull(rectangle);
	}
}
