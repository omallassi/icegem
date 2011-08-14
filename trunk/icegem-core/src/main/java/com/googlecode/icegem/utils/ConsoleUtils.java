package com.googlecode.icegem.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Console utils.
 *
 * @author Andrey Stepanov aka standy
 */
public class ConsoleUtils {
    /** Field bufferedReader  */
    private static BufferedReader bufferedReader;

    /**
     * Method getReader returns the reader of this ConsoleUtils object.
     *
     * @return the reader (type BufferedReader) of this ConsoleUtils object.
     */
    private static BufferedReader getReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        
        return bufferedReader;
    }
    
    /**
     * Waits for enter.
     */
    public static void waitForEnter() {
        waitForEnter("Press 'Enter' to continue");
    }

    /**
     * Waits for enter with specified message.
     *
     * @param message of type String
     */
    public static void waitForEnter(String message) {
        System.out.println(message);
        
        try {
            getReader().readLine();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}