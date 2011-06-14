package com.googlecode.icegem.cacheutils.common;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.icegem.cacheutils.monitor.controller.model.Node;

/**
 * The different utility operations related to the monitoring tool and its
 * components functionality.
 */
public class Utils {

	private static final SimpleDateFormat formatter = new SimpleDateFormat(
		"yyyy/MM/dd HH:mm:ss");

	/**
	 * Gets the string representation of the node's socket in format host:port.
	 * 
	 * @param node
	 *            - the node.
	 * @return - the string representation of the node's socket in format
	 *         host:port.
	 */
	public static String toKey(Node node) {
		return toKey(node.getHost(), node.getPort());
	}

	/**
	 * Gets the string representation of the host and port in format host:port.
	 * 
	 * @param host
	 *            - the host.
	 * @param port
	 *            - the port.
	 * @return - the string representation of the host and port in format
	 *         host:port.
	 */
	public static String toKey(String host, int port) {
		return host + ":" + port;
	}

	/**
	 * Checks if the socket of specified host and port is alive.
	 * 
	 * @param host
	 *            - the host.
	 * @param port
	 *            - the port.
	 * @return - true if alive, false otherwise.
	 */
	public static boolean isSocketAlive(String host, int port) {
		boolean socketAlive = false;
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			socketAlive = socket.isConnected();
		} catch (Throwable t) {
			// do nothing
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
		return socketAlive;
	}

	/**
	 * Executes the thread with specified timeout.
	 * 
	 * @param thread
	 *            - the thread to execute.
	 * @param timeout
	 *            - the timeout.
	 */
	public static void execute(Thread thread, long timeout) {
		thread.start();

		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			// should not be interrupted normally
		}

		if (thread.isAlive()) {
			thread.interrupt();
		}
	}

	public static void execute(Runnable runnable, long timeout) {
		execute(new Thread(runnable), timeout);
	}

	/**
	 * Formats date to string.
	 * 
	 * @param date
	 *            - the date.
	 * @return - the string representation of the Date object.
	 */
	public static String dateToString(Date date) {
		return formatter.format(date);
	}

	/**
	 * Formats millisecond time to string.
	 * 
	 * @param date
	 *            - the date in milliseconds.
	 * @return - the string representation of the date in milliseconds.
	 */
	public static String dateToString(long date) {
		return dateToString(new Date(date));
	}

	/**
	 * Formats the current date.
	 * 
	 * @return - the string representation of the current date.
	 */
	public static String currentDate() {
		return dateToString(System.currentTimeMillis());
	}

	public static void exitWithSuccess() {
		System.exit(0);
	}

	public static void exitWithFailure() {
		System.exit(1);
	}
}
