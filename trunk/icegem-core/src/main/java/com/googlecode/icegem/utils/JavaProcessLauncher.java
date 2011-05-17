package com.googlecode.icegem.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Platform independent java process launcher.
 *
 * @author Andrey Stepanov aka standy
 */
public class JavaProcessLauncher {
    /** Each process that starts with confirmation
     *  should write a startup completed string into it's standard output.
     *  Only after this command the process startup will be completed.
     */
    public static final String PROCESS_STARTUP_COMPLETED = "JavaProcessLauncher: startup complete";

    /** Field DEFAULT_PROCESS_STARTUP_SHUTDOWN_TIME  */
    public static final long DEFAULT_PROCESS_STARTUP_SHUTDOWN_TIME = 5000;

    /**
     * Constructor JavaProcessLauncher creates a new JavaProcessLauncher instance.
     */
    public JavaProcessLauncher() {
    }

    /**
     * Runs process based on a specified class in a separate VM.
     * To confirm that process completes startup it should write a startup completed string
     * into it's standard output.
     *
     * @param klass of type Class
     * @return Process
     * @throws IOException when
     * @throws InterruptedException when
     */
    public Process runWithConfirmation(Class klass) throws IOException, InterruptedException {
        return runWithConfirmation(klass, null);
    }

    /**
     * Runs process based on a specified class in a separate VM using array of arguments.
     * To confirm that process completes startup it should write a startup completed string
     * into it's standard output.
     *
     * @param klass of type Class
     * @param args start arguments
     * @return Process
     * @throws IOException when
     * @throws InterruptedException when
     */
    public Process runWithConfirmation(Class klass, String[] args) throws IOException, InterruptedException {
        Process process = startProcess(klass, args);
        System.out.println("Wait startup complete confirmation for process (" + klass.getCanonicalName() + ")...");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals(PROCESS_STARTUP_COMPLETED)) {
                return process;
            }
        }
        throw new InterruptedException("Process (" + klass.getCanonicalName() + ") " +
                "has been already finished without startup complete confirmation");
    }

    /**
     * Runs process based on a specified class in a separate VM.
     * Waits while process is working and returns exit code after process finished.
     *
     * @param klass of type Class
     * @return exit code of this process.
     * @throws IOException when
     * @throws InterruptedException when
     */
    public int run(Class klass) throws IOException, InterruptedException {
        Process process = startProcess(klass);
        process.waitFor();
        return process.exitValue();
    }

    /**
     * Runs process based on a specified class in a separate VM.
     * Waits DEFAULT_PROCESS_STARTUP_TIME before returns the created process to a caller.
     *
     * This method can be used for those processes that should spend
     * some time before they complete startup.
     *
     * @param klass of type Class
     * @return Process
     * @throws IOException when
     * @throws InterruptedException when
     * @throws TimeoutException if process startup is not completed in time.
     */
    public Process runWithStartupDelay(Class klass) throws IOException, InterruptedException, TimeoutException {
        return runWithStartupDelay(klass, DEFAULT_PROCESS_STARTUP_SHUTDOWN_TIME);
    }

    /**
     * Runs process based on a specified class in a separate VM.
     * Waits processStartupTime before returns the created process to a caller.
     *
     * @param klass of type Class
     * @param processStartupTime time in milliseconds that launcher spend on waiting process after it's start.
     * @return Process
     * @throws IOException when
     * @throws InterruptedException when
     * @throws TimeoutException if process startup is not completed in time.
     */
    public Process runWithStartupDelay(Class klass, long processStartupTime) throws IOException, InterruptedException, TimeoutException {
        Process process = startProcess(klass);
        if (processStartupTime > 0) {
            Thread.sleep(processStartupTime);
        }
        return process;
    }

    /**
     * Stops process by sending new line to it's output stream.
     *
     * The process can be stopped by calling destroy() method.
     *
     * @param process of type Process
     * @throws IOException when
     * @throws InterruptedException
     */
    public void stopBySendingNewLineIntoProcess(Process process) throws IOException, InterruptedException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        writer.newLine();
        writer.flush();
        process.waitFor();
    }

    /**
     * Stops process by sending new line to it's output stream.
     *
     * @param process of type Process
     * @throws IOException when
     */
    public void stop(Process process) throws IOException {
        process.destroy();
    }

    /**
     * Starts process based on specified class.
     * This process inherits a classpath from parent VM that starts it.
     *
     * @param klass of type Class
     * @return Process
     * @throws IOException when
     * @throws InterruptedException when
     */
    private Process startProcess(Class klass) throws IOException, InterruptedException {
        return startProcess(klass, null);
    }

    private Process startProcess(Class klass, String[] args) throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");

        List<String> arguments = new ArrayList<String>();
        arguments.add(javaBin);
        arguments.add("-cp");
        arguments.add(classpath);
        arguments.add(klass.getCanonicalName());
        if (args != null && args.length > 0) {
            arguments.addAll(Arrays.asList(args));
        }

        ProcessBuilder builder = new ProcessBuilder(arguments);

        Process process = builder.start();
        new StreamRedirector(process.getErrorStream(), klass.getSimpleName()).start();
        return process;
    }

    /**
     * Redirects process stream into parent standard output.
     *
     * @author Andrey Stepanov aka standy
     */
    private class StreamRedirector extends Thread {
        /** Field inputStream  */
        private InputStream inputStream;
        /** Field type  */
        private String type;

        /**
         * Constructor StreamRedirector creates a new StreamRedirector instance.
         *
         * @param inputStream of type InputStream
         * @param type of type String
         */
        public StreamRedirector(InputStream inputStream, String type){
            this.inputStream = inputStream;
            this.type = type;
        }

        /**
         * Method run.
         */
        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = br.readLine()) != null){
                    System.out.println(type + " > " + line);
                }
                br.close();
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }
}