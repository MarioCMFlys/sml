package me.mariocmflys.nmc.launcher;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

public class Zipper {
	/**
	 * Extract a zipped archive
	 * @param source String absolute path to zipped archive
	 * @param dest Destination directory
	 * @throws IOException
	 */
	public static void extract(String source, String dest) throws IOException {
		java.util.zip.ZipFile zip = new java.util.zip.ZipFile(source);
		Enumeration<? extends ZipEntry> enumEntries = zip.entries();
		while (enumEntries.hasMoreElements()) {
		    java.util.zip.ZipEntry file = (java.util.zip.ZipEntry) enumEntries.nextElement();
		    java.io.File f = new java.io.File(dest + java.io.File.separator + file.getName());
		    System.out.println("[Zipper] Extracting " + file.getName());
		    if (file.isDirectory()) {
		        f.mkdir();
		        continue;
		    }
		    f.getParentFile().mkdirs();
		    java.io.InputStream is = zip.getInputStream(file);
		    java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
		    while (is.available() > 0) {
		        fos.write(is.read());
		    }
		    fos.close();
		    is.close();
		}
		zip.close();
	}
}
