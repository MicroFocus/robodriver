package io.test.automation.robodriver.internal;

public class ImageUtilException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImageUtilException(String msg, int arg1, int arg2) {
		super(String.format(msg, arg1, arg2));
	}

}
