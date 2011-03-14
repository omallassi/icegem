package com.griddynamics.gemfire.serialization._jdktypes;

import com.griddynamics.gemfire.serialization.HierarchyRegistry;
import com.griddynamics.gemfire.serialization.primitive.TestParent;
import javassist.CannotCompileException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InvalidClassException;
import java.sql.Date;

/**
 * @author igolovach
 */

public class DisallowedJdkTypesTest extends TestParent {

    @DataProvider(name = "data")
    public Object[][] data() {
        return new Object[][]{
                new Object[]{DisallowedSqlDateBean.class},
        };
    }

    @Test(dataProvider = "data", enabled = false) //todo: 1)enable 2)compile error not rt
    public void test(Class<?> clazz) throws InvalidClassException, CannotCompileException {
        // try register
//        DataSerializerGenerator.registerCodeGenerationListener(new SOUTCodeGenerationListener());
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), clazz);

        final DisallowedSqlDateBean expected = new DisallowedSqlDateBean();
        expected.setDate(new Date(4));
        DisallowedSqlDateBean actual = serializeAndDeserialize(expected);
    }
}

