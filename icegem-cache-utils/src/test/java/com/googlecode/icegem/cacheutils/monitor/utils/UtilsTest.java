package com.googlecode.icegem.cacheutils.monitor.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Test;

import com.googlecode.icegem.cacheutils.monitor.controller.model.Node;

public class UtilsTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 40404;

	@Test
	public void testToKeyByNode() {
		Node node = new Node(HOST, PORT, null);
		String key = Utils.toKey(node);

		assertThat(key).isNotNull();
		assertThat(key).isEqualTo(HOST + ":" + PORT);
	}

	@Test
	public void testToKeyByHostAndPort() {
		String key = Utils.toKey(HOST, PORT);

		assertThat(key).isNotNull();
		assertThat(key).isEqualTo(HOST + ":" + PORT);
	}

	@Test
	public void testIsSocketAlive() throws IOException {
		final int port = 54321;

		boolean socketAlive = Utils.isSocketAlive("127.0.0.1", port);
		assertThat(socketAlive).isFalse();

		ServerSocket serverSocket = new ServerSocket(port);

		socketAlive = Utils.isSocketAlive("127.0.0.1", port);
		assertThat(socketAlive).isTrue();

		serverSocket.close();

		socketAlive = Utils.isSocketAlive("127.0.0.1", port);
		assertThat(socketAlive).isFalse();
	}

	@Test
	public void testExecute() {
		final long delay = 10 * 1000;
		final long timeout = 1000;

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					sleep(delay);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		};

		long testStartTime = System.currentTimeMillis();
		Utils.execute(thread, timeout);
		long testFinishTime = System.currentTimeMillis();
		long delta = testFinishTime - testStartTime;

		assertThat(delta).isLessThan(delay);
	}

	@Test
	public void testDateToStringByDate() {
		Date date = new Date();

		String actual = Utils.dateToString(date);

		assertThat(actual).isNotNull();

		String expected = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
			.format(date);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testDateToStringByLong() {
		long time = System.currentTimeMillis();

		String actual = Utils.dateToString(time);

		
		assertThat(actual).isNotNull();

		String expected = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
			.format(new Date(time));
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testCurrentDate() {
		String currentDate = Utils.currentDate();
		assertThat(currentDate).isNotNull();
	}
}
