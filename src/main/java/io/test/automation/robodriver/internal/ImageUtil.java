package io.test.automation.robodriver.internal;

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class ImageUtil {

	public Rectangle findRectangle(RoboScreen screen, RoboImage img) {
		BufferedImage source = screen.getScreenCapture();
		BufferedImage tofind = img.getBufferedImage();
		checkImages(source, tofind);
		int[] sourceRgb = getRgb(source);
		int[] tofindRgb = getRgb(tofind);
		
		int tofindWidth = tofind.getWidth();
		int tofindHeight = tofind.getHeight();
		int sourceWidth = source.getWidth();
		int screenHeight = source.getHeight();
		return findRectangle(sourceRgb, tofindRgb, sourceWidth, screenHeight, tofindWidth, tofindHeight);
	}

	public Rectangle findRectangle(int[] sourceRgb, int[] tofindRgb, int sourceWidth, int screenHeight, int tofindWidth,
			int tofindHeight) {
		int xLineOffset = tofindWidth / 2;
		int x = 0, y = 0;
		int line[] = new int[tofindHeight];
		fillLineVert(tofindRgb, xLineOffset, line, tofindWidth);
		
		while (y <= screenHeight - tofindHeight) {
			boolean matches = matchLineVert(sourceRgb, x, y, xLineOffset, line, sourceWidth);
			if (matches) {
				if (matchFull(sourceRgb, tofindRgb, x, y, sourceWidth, tofindWidth)) {
					return new Rectangle(x, y, tofindWidth, tofindHeight);
				}
			}
			x += 1;
			if (x > sourceWidth - tofindWidth) {
				x = 0;
				y += 1;
			}
		}
		
		return null;
	}

	public boolean matchFull(int[] sourceRgb, int[] tofindRgb, int sx, int sy, int sourceScansize, int tofindScansize) {
		try {
			for (int fidx = 0; fidx < tofindRgb.length; fidx++) {
				int fy = fidx / tofindScansize;
				int fx = fidx % tofindScansize;
				int sidx = sx + fx + ((sy + fy) * sourceScansize);
				if (sourceRgb[sidx] != tofindRgb[fidx]) {
					return false;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	public boolean matchLineVert(int[] imgRgb, int x, int y, int xoffset, int[] line, int scansize) {
		try {
			for (int i = 0; i < line.length; i++) {
				if (line[i] != imgRgb[x + xoffset + ((y + i) * scansize)]) {
					return false;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

	public void fillLineVert(int[] imgRgb, int xoffset, int[] line, int scansize) {
		for (int i = 0; i < line.length; i++) {
			line[i] = imgRgb[xoffset + (i * scansize)];
		}
	}

	private void checkImages(BufferedImage source, BufferedImage tofind) {
		if (tofind.getWidth() > source.getWidth()) {
			throw new ImageUtilException("The find-image width %d exceeds the screen width %d.", tofind.getWidth(), source.getWidth());
		}
		if (tofind.getHeight() > source.getHeight()) {
			throw new ImageUtilException("The find-image height %d exceeds the screen height %d.", tofind.getHeight(), source.getHeight());
		}
	}

	public int[] getRgb(BufferedImage tofind) {
		return tofind.getRGB(0, 0, tofind.getWidth(), tofind.getHeight(), null, 0, tofind.getWidth());
	}
	
	public boolean equals(int color1, int color2, boolean includeAlpha) {
		return includeAlpha ? 
				color1 == color2 : (color1 << 8) == (color2 << 8);
	}
	
	public int removeAlpha(int color) {
		return color << 8 >> 8;
	}
	
	public int getAlpha(int[] tofindRgb, int x, int y, int width) {
		return (tofindRgb[x + (y * width)] >> 24) & 0xFF;
	}
	
	public int getRed(int[] tofindRgb, int x, int y, int width) {
		return (tofindRgb[x + (y * width)] >> 16) & 0xFF; 
	}

	public int getGreen(int[] tofindRgb, int x, int y, int width) {
		return (tofindRgb[x + (y * width)] >> 8) & 0xFF; 
	}
	
	public int getBlue(int[] tofindRgb, int x, int y, int width) {
		return (tofindRgb[x + (y * width)]) & 0xFF;
	}
	
	public static void main(String[] args) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gds = ge.getScreenDevices();
		
		int screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
		System.out.printf("Screen resolution: %d%n", screenResolution);

		for (int i = 0; i < gds.length; i++) {
			
			GraphicsDevice gd = gds[i];
			boolean fullScreenSupported = gd.isFullScreenSupported();
				
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			DisplayMode dm = gd.getDisplayMode();

			int refreshRate = dm.getRefreshRate();

			int bitDepth = dm.getBitDepth();
			int numColors = (int) Math.pow(2, bitDepth);
			
			AffineTransform transform = gc.getDefaultTransform();
			ColorModel cm = gc.getColorModel();
			ColorSpace cs = cm.getColorSpace();
			
			System.out.printf("Screen: id = '%s', width = %d, height = %d%n", gd.getIDstring(), dm.getWidth(), dm.getHeight());
			System.out.printf("  fullScreenSupported = %b, refreshRate = %d, bitDepth = %d, numColors = %d%n",
					fullScreenSupported, refreshRate, bitDepth, numColors);
			System.out.printf("  ColorModel:      %s%n", cm.toString());
			System.out.printf("  AffineTransform: %s%n", transform.toString());
			System.out.printf("  ColorSpace:      %s%n", cs.getClass().getName());
			for (int j = 0; j < cs.getNumComponents(); j++) {
				System.out.printf("                   component-%d: %s, min=%f, max=%f%n", j, cs.getName(j), cs.getMinValue(j), cs.getMaxValue(j));
			}
		}
	}
}
