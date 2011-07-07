package com.googlecode.icegem.serialization.cyclicobectjref;

import java.io.InvalidClassException;

import javassist.CannotCompileException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import com.googlecode.icegem.serialization.primitive.TestParent;

/**
 * @author igolovach
 */

public class CyclicObjectRef1WithEnabledMethodFrameCounterTest extends
		TestParent {
	@BeforeClass
	public static void before() throws InvalidClassException,
			CannotCompileException {
		// enable method frame counter by setting property
		MethodFrameCounter.ENABLED = true;
	}

	@AfterClass
	public static void after() {
		MethodFrameCounter.ENABLED = false;
	}

	@Test(expected = RuntimeException.class)
	public void test() {
		// create test bean
		CyclicObjectRef1Bean expected = new CyclicObjectRef1Bean();
		expected.setNext(expected);
		// Register / Serialize / Deserialize
		registerSerializeAndDeserialize(expected);
	}
}
