package com.googlecode.icegem.cacheutils.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class FileService {

	public static void writeObject(String filename, Object o)
		throws IOException {

		delete(filename);

		OutputStream file = new FileOutputStream(filename);
		OutputStream buffer = new BufferedOutputStream(file);
		ObjectOutput output = new ObjectOutputStream(buffer);
		try {
			output.writeObject(o);
		} finally {
			output.close();
		}
	}

	public static Object readObject(String filename) throws IOException,
		ClassNotFoundException {

		InputStream file = new FileInputStream(filename);
		InputStream buffer = new BufferedInputStream(file);
		ObjectInput input = new ObjectInputStream(buffer);

		Object o = null;

		try {
			o = input.readObject();
		} finally {
			input.close();
			delete(filename);
		}

		return o;
	}

	private static void delete(String filename) {
		File file = new File(filename);
		file.delete();
	}

}
