package com.googlecode.icegem.serialization.cyclicobectjref;

import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.codegen.MethodFrameCounter;
import com.googlecode.icegem.serialization.primitive.TestParent;

import javassist.CannotCompileException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InvalidClassException;

/**
 * @author igolovach
 */

public class CyclicObjectRef3Test extends TestParent {

    @BeforeClass
    public void before() throws InvalidClassException, CannotCompileException {
        // register
        HierarchyRegistry.registerAll(getContextClassLoader(), CyclicObjectRef3BeanA.class, CyclicObjectRef3BeanB.class, CyclicObjectRef3BeanC.class);
    }
    
    @Test
    public void test(){
        // create test bean
        CyclicObjectRef3BeanA expectedA = new CyclicObjectRef3BeanA();
        CyclicObjectRef3BeanB expectedB = new CyclicObjectRef3BeanB();
        CyclicObjectRef3BeanC expectedC = new CyclicObjectRef3BeanC();
        expectedA.setNext(expectedB);
        expectedB.setNext(expectedC);
        expectedC.setNext(expectedA);
        
        try {
            // Register / Serialize / Deserialize
            CyclicObjectRef3BeanA actualA = (CyclicObjectRef3BeanA) serializeAndDeserialize(expectedA);
            throw new AssertionError("Must be StackOverflowError");
        } catch (StackOverflowError e) {
            final String message = e.getMessage();
            if (!message.contains(MethodFrameCounter.MSG)) {
                throw new AssertionError("Must be StackOverflowError with message: '" + MethodFrameCounter.MSG + "'");
            }
        }
    }
}
