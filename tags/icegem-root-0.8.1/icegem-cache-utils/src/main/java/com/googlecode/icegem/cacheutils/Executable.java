package com.googlecode.icegem.cacheutils;


/**
 * User: Artem Kondratyev e-mail: kondratevae@gmail.com
 */
public interface Executable {
	
    void execute(String[] args, boolean debugEnabled, boolean quiet);
    
}
