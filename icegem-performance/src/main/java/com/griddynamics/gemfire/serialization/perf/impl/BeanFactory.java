package com.griddynamics.gemfire.serialization.perf.impl;

/**
 * @author igolovach
 */

public class BeanFactory {

    public static FlatSlimPerfBean createFlatSlim() {
        final FlatSlimPerfBean result = new FlatSlimPerfBean();

        result.setId(1234567890);
        result.setName("Hello from bean! How are you doing?");

        return result;
    }

    public static FlatFatPerfBean createFlatFat() {
        final FlatFatPerfBean result = new FlatFatPerfBean();

        result.setD0(123.567d);
        result.setD1(123.567d);
        result.setD2(123.567d);

        result.setIp0(1);
        result.setIp0(12345);
        result.setIp0(1234567890);

        result.setLp0(1L);
        result.setLp1(1234567890L);
        result.setLp2(1234567890123456789L);

        result.setIw0(1);
        result.setIw0(12345);
        result.setIw0(1234567890);

        result.setLw0(1L);
        result.setLw1(1234567890L);
        result.setLw2(1234567890123456789L);

        result.setIntArr0(new int[]{1});
        result.setIntArr1(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
        result.setIntArr2(new int[]{
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
        });

        result.setString0("0");
        result.setString1("1, 2, 3, 4, 5, 6, 7, 8, 9, 0,");
        result.setString2(
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0," +
                "1, 2, 3, 4, 5, 6, 7, 8, 9, 0,"
        );

        return result;
    }
}
