package com.googlecode.icegem.serialization.codegen;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;


public class XClassTest {

	@Test
	public void testGetOrderedProperties() {
		XClass xClazz = new XClass(SortOrderStub2.class);
		
		List<XProperty> props = xClazz.getOrderedProperties();
		
		assertEquals(5, props.size());
		
		assertPropertyEquals(props.get(0), 1, SortOrderStub1.class, "field1");
		assertPropertyEquals(props.get(1), 1, SortOrderStub2.class, "field4");
		assertPropertyEquals(props.get(2), 2, SortOrderStub1.class, "field2");
		assertPropertyEquals(props.get(3), 2, SortOrderStub2.class, "field5");
		assertPropertyEquals(props.get(4), 3, SortOrderStub1.class, "field3");
	}

	@Test
	public void testBeanVersion() {
		XClass xClazz = new XClass(SortOrderStub2.class);
		assertEquals(3, xClazz.getVersionHistoryLength());
	}
	
	@Test
	public void testVersionHistory() {
		XClass xClazz = new XClass(SortOrderStub2.class);
		assertEquals(3, xClazz.getVersionHistoryLength());
	}
	
	private void assertPropertyEquals(XProperty prop, int version,
			Class<?> declared, String name) {
		assertEquals(prop.getPropertyVersion(), version);
		assertEquals(prop.getDeclaringClass(), declared);
		assertEquals(prop.getName(), name);
	}

}
