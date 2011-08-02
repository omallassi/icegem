package com.googlecode.icegem.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Loads the Properties from property file and allows to retrieve properties in
 * more advanced way
 */
public class PropertiesHelper {

	private static final String KEY_VALUE_SEPARATOR = "=";
	private static final String PROPERTIES_SEPARATOR = ";";
	private Properties properties;

	public PropertiesHelper(String filename) throws	IOException {
		properties = new Properties();
		properties.load(getClass().getResourceAsStream(filename));
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

	public static String propertiesToString(Properties properties) {
		StringBuilder sb = new StringBuilder();

		Iterator<Object> it = properties.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) properties.get(key);

			sb.append(key).append(KEY_VALUE_SEPARATOR).append(value);

			if (it.hasNext()) {
				sb.append(PROPERTIES_SEPARATOR);
			}
		}

		return sb.toString();
	}

	public static Properties stringToProperties(String s) {
		Properties properties = new Properties();

		String[] keyValues = s.split(PROPERTIES_SEPARATOR);

		for (String keyValue : keyValues) {
			String[] keyAndValue = keyValue.split(KEY_VALUE_SEPARATOR);

			properties.put(keyAndValue[0], keyAndValue[1]);
		}

		return properties;
	}

	public static Properties filterProperties(Properties properties,
		String prefix) {
		Properties result = new Properties();

		for (Object keyObject : properties.keySet()) {
			String key = (String) keyObject;
			String value = properties.getProperty(key);

			if (key.startsWith(prefix)) {
				result.put(key, value);
			}
		}

		return result;
	}

	public static String[] propertiesToVMOptions(Properties properties) {
		List<String> result = new ArrayList<String>();

		for (Object keyObject : properties.keySet()) {
			String key = (String) keyObject;
			String value = properties.getProperty(key);

			result.add("-D" + key + "=" + value);
		}

		return result.toArray(new String[result.size()]);
	}

}
