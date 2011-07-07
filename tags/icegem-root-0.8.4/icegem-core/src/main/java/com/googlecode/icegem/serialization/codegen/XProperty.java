package com.googlecode.icegem.serialization.codegen;

import java.util.Comparator;

/**
 * Wrapper class for java.lang.reflect.Field + useful methods for code
 * generation
 * 
 * @author igolovach
 */

public class XProperty { // todo: check field is serialized? (like in XClass
							// constructor)

	public static class NaturalOrder implements Comparator<XProperty> {

		public int compare(XProperty o1, XProperty o2) {
			if (o1.propertyVersion < o2.propertyVersion) {
				return -1;
			} else if (o1.propertyVersion > o2.propertyVersion) {
				return 1;
			}

			String thisSignature = o1.getDeclaringClass() + ":"
					+ o1.getName();
			String otherSignature = o1.getDeclaringClass() + ":"
					+ o2.getName();

			return thisSignature.compareTo(otherSignature);
		}

	}

	private final String name;
	private final boolean isBoolean;
	private final Class<?> type;
	private final Class<?> declaringClass;
	private final int propertyVersion;

	public XProperty(String name, Class<?> type, Class<?> declaringClass,
			int propVersion, boolean aBoolean) {
		this.name = name;
		this.type = type;
		this.declaringClass = declaringClass;
		this.propertyVersion = propVersion;
		isBoolean = aBoolean;
	}

	public boolean isBoolean() {
		return isBoolean;
	}

	public int getPropertyVersion() {
		return propertyVersion;
	}

	public Class<?> getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
}
