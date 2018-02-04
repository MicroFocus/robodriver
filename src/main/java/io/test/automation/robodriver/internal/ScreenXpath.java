package io.test.automation.robodriver.internal;

import java.awt.Rectangle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriverException;

public class ScreenXpath {

	private Pattern xpathScreenElemWithIndex = Pattern.compile("/*screen\\[(\\d+)\\]");
	private Pattern xpathRectangle = Pattern.compile("/*rectangle\\[@dim='([\\s\\d,]+)'\\]");

	private String path;

	public ScreenXpath(String path) {
		if (! path.toLowerCase().contains("screen") && ! path.toLowerCase().contains("rectangle")) {
			throw new WebDriverException("connot find '" + path + "'");
		}
		this.path = path;
	}

	public boolean isDefaultScreen() {
		return path.toLowerCase().contains("default");
	}

	public boolean isRectangle() {
		return path.toLowerCase().contains("rectangle");
	}

	public int getScreenIndex() {
		Matcher matcher = xpathScreenElemWithIndex.matcher(path.toLowerCase());
		if (matcher.find()) {
			try {
				return Integer.parseInt(matcher.group(1));
			} catch (Exception e) {
				throw new WebDriverException("Cannot parse screen index of xpath '" + path+ "'");
			}
		}
		return 0;
	}

	public Rectangle getRectangle() {
		Matcher matcher = xpathRectangle.matcher(path.toLowerCase());
		if (matcher.find()) {
			try {
				String group = matcher.group(1);
				String[] dim = group.split("[,\\s]+");
				if (dim.length != 4) {
					throw new WebDriverException(
							String.format("Invalid rectangle dimension '%s', expected format: rectangle[@dim='x,y,width,height']", 
									group));
				}
				int x = Integer.parseInt(dim[0]);
				int y = Integer.parseInt(dim[1]);
				int w = Integer.parseInt(dim[2]);
				int h = Integer.parseInt(dim[3]);
				return new Rectangle(x, y, w, h) {
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return String.format("x=%s,y=%s,w=%s,h=%s", this.x, this.y, this.width, this.height);
					}
				};
			} catch (Exception e) {
				throw new WebDriverException(
						String.format("Cannot parse dimension of rectangle '%s', expected format: rectangle[@dim='x,y,width,height']", 
								path));
			}
		}
		return null;
	}

}
