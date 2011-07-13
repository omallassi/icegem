package com.googlecode.icegem.cacheutils.common;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.googlecode.icegem.cacheutils.monitor.controller.model.Node;
import com.googlecode.icegem.serialization.AutoSerializable;
import com.googlecode.icegem.serialization.HierarchyRegistry;

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

	public static void exitWithFailure(String message, Throwable t) {
		if (message != null) {
			System.err.println(message);
		}

		if (t != null) {
			t.printStackTrace(System.err);
		}

		System.exit(1);
	}

	public static void exitWithFailure(String message) {
		exitWithFailure(message, null);
	}

	public static void exitWithFailure() {
		exitWithFailure(null);
	}

	public static String stringListToCsv(List<String> list) {
		StringBuilder sb = new StringBuilder();

		sb.append("[");

		if (list != null) {
			Iterator<String> it = list.iterator();
			while (it.hasNext()) {
				sb.append(it.next());

				if (it.hasNext()) {
					sb.append(",");
				}
			}
		}

		sb.append("]");

		return sb.toString();
	}

	public static List<String> csvToStringList(String csv) {
		List<String> result = null;

		if (csv == null || csv.trim().length() == 0) {
			result = new ArrayList<String>();
		} else {
			csv = csv.substring(1, csv.length() - 1);

			result = Arrays.asList(csv.split(","));
		}

		return result;
	}

	public static void registerClasses(List<String> packages) throws Exception {
		if ((packages != null) && (packages.size() > 0)) {
			List<Class<?>> classesFromPackages = new ArrayList<Class<?>>();

			for (String pack : packages) {
				ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
					false);
				provider.addIncludeFilter(new AnnotationTypeFilter(
					AutoSerializable.class));
				Set<BeanDefinition> candidateComponents = provider
					.findCandidateComponents(pack);
				for (BeanDefinition beanDefinition : candidateComponents) {
					String className = beanDefinition.getBeanClassName();
					final Class<?> clazz = Class.forName(className);
					classesFromPackages.add(clazz);
				}
			}

			HierarchyRegistry.registerAll(Thread.currentThread()
				.getContextClassLoader(), classesFromPackages);
		}
	}
}
