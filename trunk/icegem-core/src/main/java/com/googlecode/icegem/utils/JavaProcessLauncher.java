package com.googlecode.icegem.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	/** Field PROCESS_STDOUT_STREAM_PREFIX */
	private static final String PROCESS_STDOUT_STREAM_PREFIX = " out>";
	/** Field PROCESS_ERROR_STREAM_PREFIX */
	private static final String PROCESS_ERROR_STREAM_PREFIX = " error>";
	/**
	 * Each process that starts with confirmation must write a startup completed
	 * string into it's standard output. Only after this command the process
	 * startup will be completed.
	 */
	public static final String PROCESS_STARTUP_COMPLETED = "JavaProcessLauncher: startup complete";
	/** Field DEFAULT_PROCESS_STARTUP_SHUTDOWN_TIME */
	public static final long DEFAULT_PROCESS_STARTUP_SHUTDOWN_TIME = 5000;
	/**
	 * Indicates that an input stream for a started process must be redirected
	 * to astandard out of a parent process
	 */
	private boolean redirectProcessInputStreamToParentProcessStdOut;
	/**
	 * Indicates that an error stream for a started process must be redirected
	 * to a standard out of a parent process
	 */
	/** Field redirectProcessErrorStreamToParentProcessStdOut */
	private boolean redirectProcessErrorStreamToParentProcessStdOut;

	/**
	 * Constructor JavaProcessLauncher creates a new JavaProcessLauncher
	 * instance.
	 */
	public JavaProcessLauncher() {
		this.redirectProcessInputStreamToParentProcessStdOut = false;
		this.redirectProcessErrorStreamToParentProcessStdOut = true;
	}

	/**
	 * Constructor JavaProcessLauncher creates a new JavaProcessLauncher
	 * instance.
	 * 
	 * @param redirectProcessInputStreamToParentProcessStdOut
	 *            of type boolean
	 * @param redirectProcessErrorStreamToParentProcessStdOut
	 *            of type boolean
	 */
	public JavaProcessLauncher(
		boolean redirectProcessInputStreamToParentProcessStdOut,
		boolean redirectProcessErrorStreamToParentProcessStdOut) {
		this.redirectProcessInputStreamToParentProcessStdOut = redirectProcessInputStreamToParentProcessStdOut;
		this.redirectProcessErrorStreamToParentProcessStdOut = redirectProcessErrorStreamToParentProcessStdOut;
	}

    /**
     * Sets the redirectProcessErrorStreamToParentProcessStdOut of this JavaProcessLauncher object.
     *
     * @param redirectProcessErrorStreamToParentProcessStdOut boolean flag.
     *
     */
    public void setRedirectProcessErrorStreamToParentProcessStdOut(boolean redirectProcessErrorStreamToParentProcessStdOut) {
        this.redirectProcessErrorStreamToParentProcessStdOut = redirectProcessErrorStreamToParentProcessStdOut;
    }

    /**
     * Sets the redirectProcessInputStreamToParentProcessStdOut of this JavaProcessLauncher object.
     *
     * @param redirectProcessInputStreamToParentProcessStdOut boolean flag.
     *
     */
    public void setRedirectProcessInputStreamToParentProcessStdOut(boolean redirectProcessInputStreamToParentProcessStdOut) {
        this.redirectProcessInputStreamToParentProcessStdOut = redirectProcessInputStreamToParentProcessStdOut;
    }

    /**
     * Runs process with arguments based on a specified class in a separate VM. Waits while
	 * process is working and returns exit code after process finished.
     *
     * @param klass of type Class
     * @param javaArguments arguments for java
     * @param processArguments arguments for process
     * @return int
     * @throws IOException when
     * @throws InterruptedException when
     */
    public int runAndWaitProcessExitCode(Class klass, String[] javaArguments, String[] processArguments) throws IOException, InterruptedException {
		Process process = startProcess(klass, javaArguments, processArguments, false);
		process.waitFor();
		return process.exitValue();
	}

	/**
	 * Runs a process and returns the Process object.
	 * 
	 * @param clazz
	 *            - the class to run
     *
	 * @param javaArguments arguments for java
     * @param processArguments arguments for process
     * @return - the Process object representing running process
	 * @throws IOException
     * @throws InterruptedException
	 */
	public Process runWithoutConfirmation(Class<?> clazz, String[] javaArguments, String[] processArguments)
            throws IOException, InterruptedException {
        return startProcess(clazz, javaArguments, processArguments, false);
	}

	/**
	 * Runs process based on a specified class in a separate VM using array of
	 * arguments. To confirm that process completes startup it should write a
	 * startup completed string into it's standard output.
	 * 
	 * @param klass
	 *            of type Class
	 * @param javaArguments arguments for java
     * @param processArguments arguments for process
	 * @return Process
	 * @throws IOException
	 *             when
	 * @throws InterruptedException
	 *             when
	 */
	public Process runWithConfirmation(Class klass, String[] javaArguments, String[] processArguments)
		throws IOException, InterruptedException {
		Process process = startProcess(klass, javaArguments, processArguments, true);
		waitConfirmation(klass.getSimpleName(), process);
		new StreamRedirector(process.getInputStream(), klass.getSimpleName()
			+ PROCESS_STDOUT_STREAM_PREFIX,
			redirectProcessInputStreamToParentProcessStdOut).start();
		return process;
	}

    /**
     * Runs process with arguments based on a specified class in a separate VM. Waits
	 * DEFAULT_PROCESS_STARTUP_TIME before returns the created process to a
	 * caller.
     *
     * @param klass of type Class
     * @param javaArguments arguments for java
     * @param processArguments arguments for process
     * @return Process
     * @throws IOException when
     * @throws InterruptedException when
     * @throws TimeoutException when
     */
    public Process runWithStartupDelay(Class klass, String[] javaArguments, String[] processArguments) throws IOException,
		InterruptedException, TimeoutException {
		return runWithStartupDelay(klass, javaArguments, processArguments, DEFAULT_PROCESS_STARTUP_SHUTDOWN_TIME);
	}

	/**
	 * Runs process with arguments based on a specified class in a separate VM. Waits
	 * processStartupTime before returns the created process to a caller.
	 * 
	 * @param klass
	 *            of type Class
	 * @param processStartupTime
	 *            time in milliseconds that launcher spend on waiting process
	 *            after it's start.
	 * @param javaArguments arguments for java
     * @param processArguments arguments for process
     * @return Process
	 * @throws IOException
	 *             when
	 * @throws InterruptedException
	 *             when
	 * @throws TimeoutException
	 *             if process startup is not completed in time.
	 */
	public Process runWithStartupDelay(Class klass, String[] javaArguments, String[] processArguments, long processStartupTime)
		throws IOException, InterruptedException, TimeoutException {
		Process process = runWithConfirmation(klass, javaArguments, processArguments);
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
	 * @param process
	 *            of type Process
	 * @throws IOException
	 *             when
	 * @throws InterruptedException
	 */
	public void stopBySendingNewLineIntoProcess(Process process)
		throws IOException, InterruptedException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
			process.getOutputStream()));
		writer.newLine();
		writer.flush();
		process.waitFor();
	}

	/**
	 * Stops process by destroying process.
	 * 
	 * @param process
	 *            of type Process
	 * @throws IOException
	 *             when
	 */
	public void stopByDestroyingProcess(Process process) throws IOException {
		process.destroy();
	}

    /**
     * Starts process based on specified class using command line arguments.
     * This process inherits a classpath from parent VM that starts it.
     *
     * @param klass of type Class
     * @param javaArguments of type String[]
     * @param processArguments of type String[]
     * @param withConfirmation of type boolean
     * @return Process
     * @throws IOException when
     * @throws InterruptedException when
     */
    private Process startProcess(Class klass, String[] javaArguments, String[] processArguments, boolean withConfirmation) throws IOException, InterruptedException {
        List<String> arguments = createCommandLineForProcess(klass, javaArguments, processArguments);
        Process process = new ProcessBuilder(arguments).start();
        redirectProcessStreams(klass, process, !withConfirmation);
        return process;
    }

    /**
     * Redirects process standard output and error streams into parent process standard output.
     *
     * @param klass of type Class
     * @param process of type Process
     * @param redirectProcessStdOut of type boolean
     */
    private void redirectProcessStreams(Class klass, Process process, boolean redirectProcessStdOut) {
        new StreamRedirector(process.getErrorStream(), klass.getSimpleName()
                + PROCESS_ERROR_STREAM_PREFIX,
                redirectProcessErrorStreamToParentProcessStdOut).start();
        if (redirectProcessStdOut) {
            new StreamRedirector(process.getInputStream(),
                    klass.getSimpleName() + PROCESS_STDOUT_STREAM_PREFIX,
                    redirectProcessInputStreamToParentProcessStdOut).start();
        }
    }

    /**
     * Builds command line for starting java process based on specified arguments.
     *
     * @param klazz
     * @param processArguments of type String[]
     * @return List<String>
     */
    private List<String> createCommandLineForProcess(Class klazz, String[] processArguments) {
		return createCommandLineForProcess(klazz, null, processArguments);
	}

	/**
     * Builds command line for starting java process based on specified arguments.
     *
     * @param klazz
     * @param javaArguments of type String[]
     * @param processArguments of type String[]
     * @return List<String>
     */
    private List<String> createCommandLineForProcess(Class klazz, String[] javaArguments, String[] processArguments) {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator
			+ "java";
		String classpath = System.getProperty("java.class.path");

        List<String> argimentsList = new ArrayList<String>();
		argimentsList.add(javaBin);
		argimentsList.add("-cp");
		argimentsList.add(classpath);

        if (javaArguments != null && javaArguments.length > 0) {
			argimentsList.addAll(Arrays.asList(javaArguments));
		}

        argimentsList.add(klazz.getCanonicalName());

		if (processArguments != null && processArguments.length > 0) {
			argimentsList.addAll(Arrays.asList(processArguments));
		}
		return argimentsList;
	}

	/**
	 * Waits startup complete confirmation from process.
	 * 
	 * @param className
	 *            of type String
	 * @param process
	 *            of type Process
	 * @throws IOException
	 *             when
	 * @throws InterruptedException
	 *             when
	 */
	private void waitConfirmation(String className, Process process)
		throws IOException, InterruptedException {
		System.out
			.println("Waiting startup complete confirmation for a process ("
				+ className + ")...");

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.equals(PROCESS_STARTUP_COMPLETED)) {
				System.out.println("The process (" + className
					+ ") has been started successfully");
				return;
			} else if (redirectProcessInputStreamToParentProcessStdOut) {
				System.out.println(className + PROCESS_STDOUT_STREAM_PREFIX
					+ line);
			}
		}
		throw new InterruptedException("Process (" + className + ") "
			+ "has been already finished without startup complete confirmation");
	}

    /**
	 * Redirects process stream into parent standard output.
	 * 
	 * @author Andrey Stepanov aka standy
	 */
	private class StreamRedirector extends Thread {
		/** Field inputStream */
		private InputStream inputStream;
		/** Field type */
		private String type;
		/**
		 * Field redirectToParentProcessStdOut - if true than this stream will
		 * be redirected to parent process standard output.
		 */
		private boolean redirectToParentProcessStdOut;

		/**
		 * Constructor StreamRedirector creates a new StreamRedirector instance.
		 * 
		 * @param inputStream
		 *            of type InputStream
		 * @param type
		 *            of type String
		 */
		public StreamRedirector(InputStream inputStream, String type) {
			this.inputStream = inputStream;
			this.type = type;
			this.redirectToParentProcessStdOut = false;
		}

		/**
		 * Constructor StreamRedirector creates a new StreamRedirector instance.
		 * 
		 * @param inputStream
		 *            of type InputStream
		 * @param type
		 *            of type String
		 * @param redirectToParentProcessStdOut
		 */
		public StreamRedirector(InputStream inputStream, String type,
			boolean redirectToParentProcessStdOut) {
			this.inputStream = inputStream;
			this.type = type;
			this.redirectToParentProcessStdOut = redirectToParentProcessStdOut;
		}

		/**
		 * Method run.
		 */
		@Override
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));
				String line;
				while ((line = br.readLine()) != null) {
					if (redirectToParentProcessStdOut) {
						System.out.println(type + line);
					}
				}
				br.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}