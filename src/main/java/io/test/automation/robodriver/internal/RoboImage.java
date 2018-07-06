package io.test.automation.robodriver.internal;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebDriverException;

public class RoboImage {

	private BufferedImage bufferedImage;

	/**
	 * Creates image object from file.
	 * 
	 * @param imgUriOrFile image file by path, http, file or data URI.
	 */
	public RoboImage(String imgUriOrFile) {
		try {
			URI u = new URI(imgUriOrFile);
			this.bufferedImage = ImageIO.read(u.toURL());
		} catch (URISyntaxException e) {
			// TODO try if it is a file path
			throw new WebDriverException(e);
		} catch (Exception e) {
			throw new WebDriverException(e);
		}
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}
}
