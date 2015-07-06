package com.nagainfo.zipmanagement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compress {

	
	
	private static int BUFFER_SIZE=8192;

	public void zip(String[] files, String zipFile) throws IOException {
		BufferedInputStream origin = null;
		File file=new File(zipFile);
		if(!new File(file.getParent()).exists())
		{
			new File(file.getParent()).mkdirs();
		}
	
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile)));
		try {
			byte data[] = new byte[BUFFER_SIZE];

			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				try {
					ZipEntry entry = new ZipEntry(files[i].substring(files[i]
							.lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
						out.write(data, 0, count);
					}
				} finally {
					origin.close();
				}
			}
		} finally {
			out.close();
		}
	}
	
}
