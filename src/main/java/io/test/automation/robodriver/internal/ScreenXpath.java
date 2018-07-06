package io.test.automation.robodriver.internal;

import java.awt.Rectangle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriverException;

public class ScreenXpath {

	private final static Pattern xpathScreenElemWithIndex = Pattern.compile("/*screen\\[(\\d+)\\]");
	private final static Pattern xpathRectangle = Pattern.compile("/*rectangle\\[@dim='([\\s\\d,]+)'\\]");
	private final static Pattern xpathRectangleImg = Pattern.compile("/*rectangle\\[@img='(.+?)'\\]");

	private final String path;
	private String lowerCasePath;

	public ScreenXpath(String path) {
		if (! path.toLowerCase().contains("screen") && ! path.toLowerCase().contains("rectangle")) {
			throw new WebDriverException("connot find '" + path + "'");
		}
		this.path = path;
		this.lowerCasePath = path.toLowerCase();
	}

	public boolean isDefaultScreen() {
		return lowerCasePath.contains("default");
	}

	public boolean isRectangle() {
		return lowerCasePath.contains("rectangle");
	}

	public boolean isRectangleByDim() {
		return lowerCasePath.contains("rectangle")
				&& lowerCasePath.contains("@dim=");
	}

	public boolean isRectangleByImg() {
		return lowerCasePath.contains("rectangle")
				&& lowerCasePath.contains("@img=");
	}

	public int getScreenIndex() {
		Matcher matcher = xpathScreenElemWithIndex.matcher(getPath().toLowerCase());
		if (matcher.find()) {
			try {
				return Integer.parseInt(matcher.group(1));
			} catch (Exception e) {
				throw new WebDriverException("Cannot parse screen index of xpath '" + getPath()+ "'");
			}
		}
		return 0;
	}

	public Rectangle getRectangle() {
		Matcher matcher = xpathRectangle.matcher(getPath().toLowerCase());
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
				return createRectangle(x, y, w, h);
			} catch (Exception e) {
			}
		}
		throw new WebDriverException(
				String.format("Cannot parse dimension of rectangle '%s', expected format: rectangle[@dim='x,y,width,height']", 
						getPath()));
	}

	public String getImgUriOrFile() {
		Matcher matcher = xpathRectangleImg.matcher(getPath().toLowerCase());
		if (matcher.find()) {
			try {
				return matcher.group(1);
			} catch (Exception e) {
			}
		}
		throw new WebDriverException(
				String.format("Cannot parse image URI of rectangle '%s', expected format: rectangle[@img='http://xxx/img.png']", 
						getPath()));
	}

	private Rectangle createRectangle(int x, int y, int w, int h) {
		return new Rectangle(x, y, w, h) {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return String.format("x=%s,y=%s,w=%s,h=%s", this.x, this.y, this.width, this.height);
			}
		};
	}

	public String getPath() {
		return path;
	}

}
