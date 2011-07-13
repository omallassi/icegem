package com.googlecode.icegem.cacheutils.comparator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileService {

	public static void write(String filename, String value) throws IOException {
		File file = new File(filename);

		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(value);
		out.close();
	}

	public static String read(String filename) throws IOException {
		File file = new File(filename);

		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		in.close();
		
		return line;
	}


	public static void delete(String filename) {
		File file = new File(filename);
		file.delete();
	}
	
}
