package io.test.automation.robodriver.internal;

public class LoggerUtil {

	private static final String JAVA_UTIL_LOGGING_SIMPLE_FORMATTER_FORMAT = "java.util.logging.SimpleFormatter.format";

	static {
		if (! isLoggingFormatterDefined()) {
			// set logging formatter, see also:
			// https://docs.oracle.com/javase/8/docs/api/java/util/logging/SimpleFormatter.html#format(java.util.logging.LogRecord)
			System.setProperty(JAVA_UTIL_LOGGING_SIMPLE_FORMATTER_FORMAT, "[%1$tc] %4$.4s: %5$s - %2$s %6$s%n");
		}
	}

	private static boolean isLoggingFormatterDefined() {
		return System.getProperty("java.util.logging.config.file") != null
			|| System.getProperty(JAVA_UTIL_LOGGING_SIMPLE_FORMATTER_FORMAT) != null;
	}

	public static java.util.logging.Logger get(Class<?> c) {
		return java.util.logging.Logger.getLogger(c.getName());
	}

}