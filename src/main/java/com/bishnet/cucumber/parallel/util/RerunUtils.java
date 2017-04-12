package com.bishnet.cucumber.parallel.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;


public class RerunUtils {
	public static int countScenariosInRerunFile (Path path) throws IOException {
		byte[] encoded = Files.readAllBytes(path);
		String[] strings = new String(encoded, Charset.defaultCharset()).split("[ ]+");
		return strings.length;
	}
}
