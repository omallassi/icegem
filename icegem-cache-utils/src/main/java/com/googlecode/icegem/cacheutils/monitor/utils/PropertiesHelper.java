package com.googlecode.icegem.cacheutils.monitor.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Loads the Properties from property file and allows to retrieve properties in
 * more advanced way
 */
public class PropertiesHelper {

	private Properties properties;

	public PropertiesHelper(String filename) throws FileNotFoundException,
			IOException {
		properties = new Properties();
		properties.load(new FileInputStream(filename));
	}

	public String getStringProperty(String key) {
		return (String) properties.get(key);
	}

	public String getStringProperty(String key, Object... parameters) {
		String value = (String) properties.get(key);
		MessageFormat.format(value, parameters);
		return MessageFormat.format(value, parameters);
	}

	public int getIntProperty(String key) {
		String value = properties.getProperty(key);
		return Integer.parseInt(value);
	}

	public long getLongProperty(String key) {
		String value = properties.getProperty(key);
		return Long.parseLong(value);
	}

	public Properties getProperties() {
		return properties;
	}
}
