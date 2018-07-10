package io.test.automation.robodriver.internal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebDriverException;

import com.github.ooxi.jdatauri.DataUri_July2018;
import com.google.common.io.Files;

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
			if ("data".equals(u.getScheme())) {
				DataUri_July2018 imgDataUri = DataUri_July2018.parse(u.toString(), Charset.forName("UTF-8"));
				File imgTempFile = File.createTempFile("robodriver-image", "temp");
				Files.write(imgDataUri.getData(), imgTempFile);
				this.bufferedImage = ImageIO.read(imgTempFile);
			} else {
				this.bufferedImage = ImageIO.read(u.toURL());
			}
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
