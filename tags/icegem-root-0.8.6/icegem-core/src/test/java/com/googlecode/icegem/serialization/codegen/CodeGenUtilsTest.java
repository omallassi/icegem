package com.googlecode.icegem.serialization.codegen;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.gemstone.bp.edu.emory.mathcs.backport.java.util.Arrays;
import com.googlecode.icegem.serialization.primitive.TestParent;
import static org.junit.Assert.*;

/**
 * @author igolovach
 */
@RunWith(Parameterized.class)
public class CodeGenUtilsTest extends TestParent {

	String name;
	Class<?> clazz; 
	
	public CodeGenUtilsTest(Class<?> clazz, String name) {
		this.clazz = clazz;
		this.name = name;
	}
	
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
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
        });
    }
    
    @Test
    public void testClassName() {
        assertEquals(CodeGenUtils.className(clazz),name);
        // #1
        assertEquals(CodeGenUtils.className(char[].class),"char[]");
        // #2
        assertEquals(CodeGenUtils.className(char[][].class),"char[][]");
        // #10
        assertEquals(CodeGenUtils.className(char[][][][][][][][][][].class),"char[][][][][][][][][][]");
    }
}
