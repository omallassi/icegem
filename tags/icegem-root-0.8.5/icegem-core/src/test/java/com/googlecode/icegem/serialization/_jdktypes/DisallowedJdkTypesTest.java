package com.googlecode.icegem.serialization._jdktypes;

import java.io.InvalidClassException;
import java.sql.Date;
import java.util.Collection;

import javassist.CannotCompileException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.gemstone.bp.edu.emory.mathcs.backport.java.util.Arrays;
import com.googlecode.icegem.serialization.HierarchyRegistry;
import com.googlecode.icegem.serialization.primitive.TestParent;

/**
 * @author igolovach
 */
@RunWith(Parameterized.class)
public class DisallowedJdkTypesTest extends TestParent {

	private Class<?> clazz;

	public DisallowedJdkTypesTest(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                new Object[]{DisallowedSqlDateBean.class},
        });
    }

    @Test //todo: 1)enable 2)compile error not rt
    @Ignore
    public void test() throws InvalidClassException, CannotCompileException {
        // try register
//        DataSerializerGenerator.registerCodeGenerationListener(new SOUTCodeGenerationListener());
        HierarchyRegistry.registerAll(Thread.currentThread().getContextClassLoader(), clazz);

        final DisallowedSqlDateBean expected = new DisallowedSqlDateBean();
        expected.setDate(new Date(4));
        DisallowedSqlDateBean actual = serializeAndDeserialize(expected);
    }
}

