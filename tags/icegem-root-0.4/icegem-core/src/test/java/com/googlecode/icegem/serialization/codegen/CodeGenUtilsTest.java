package com.googlecode.icegem.serialization.codegen;

import org.fest.assertions.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.icegem.serialization.codegen.CodeGenUtils;

/**
 * @author igolovach
 */

public class CodeGenUtilsTest {

    @DataProvider
    public Object[][] data() {
        return new Object[][] {
                // Integer
                new Object[] {Integer.class, "java.lang.Integer"},
                new Object[] {Integer[].class, "java.lang.Integer[]"},
                new Object[] {Integer[][].class, "java.lang.Integer[][]"},
                new Object[] {Integer[][][].class, "java.lang.Integer[][][]"},
                // Integer
                new Object[] {boolean.class, "boolean"},
                new Object[] {boolean[].class, "boolean[]"},
                new Object[] {boolean[][].class, "boolean[][]"},
                new Object[] {boolean[][][].class, "boolean[][][]"},
                // Integer
                new Object[] {byte.class, "byte"},
                new Object[] {byte[].class, "byte[]"},
                new Object[] {byte[][].class, "byte[][]"},
                new Object[] {byte[][][].class, "byte[][][]"},
                // Integer
                new Object[] {short.class, "short"},
                new Object[] {short[].class, "short[]"},
                new Object[] {short[][].class, "short[][]"},
                new Object[] {short[][][].class, "short[][][]"},
                // Integer
                new Object[] {char.class, "char"},
                new Object[] {char[].class, "char[]"},
                new Object[] {char[][].class, "char[][]"},
                new Object[] {char[][][].class, "char[][][]"},
                // Integer
                new Object[] {int.class, "int"},
                new Object[] {int[].class, "int[]"},
                new Object[] {int[][].class, "int[][]"},
                new Object[] {int[][][].class, "int[][][]"},
                // Integer
                new Object[] {long.class, "long"},
                new Object[] {long[].class, "long[]"},
                new Object[] {long[][].class, "long[][]"},
                new Object[] {long[][][].class, "long[][][]"},
                // Integer
                new Object[] {float.class, "float"},
                new Object[] {float[].class, "float[]"},
                new Object[] {float[][].class, "float[][]"},
                new Object[] {float[][][].class, "float[][][]"},
                // Integer
                new Object[] {double.class, "double"},
                new Object[] {double[].class, "double[]"},
                new Object[] {double[][].class, "double[][]"},
                new Object[] {double[][][].class, "double[][][]"},
        };
    }
    
    @Test(dataProvider = "data")
    public void testClassName(Class<?> clazz, String name) {
        Assertions.assertThat(CodeGenUtils.className(clazz)).isEqualTo(name);
        // #1
        Assertions.assertThat(CodeGenUtils.className(char[].class)).isEqualTo("char[]");
        // #2
        Assertions.assertThat(CodeGenUtils.className(char[][].class)).isEqualTo("char[][]");
        // #10
        Assertions.assertThat(CodeGenUtils.className(char[][][][][][][][][][].class)).isEqualTo("char[][][][][][][][][][]");
    }
}
