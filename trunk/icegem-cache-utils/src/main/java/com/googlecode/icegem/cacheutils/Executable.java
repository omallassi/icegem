package com.googlecode.icegem.cacheutils;

/**
 * User: Artem Kondratyev e-mail: kondratevae@gmail.com
 */
public interface Executable {
    /**
     * 
     * @param args - Arguments.
     * @param debugEnabled - {@code true} if debug is enabled.
     * @param quiet - Quiet mode.
     */
    void execute(String[] args, boolean debugEnabled, boolean quiet);
}
