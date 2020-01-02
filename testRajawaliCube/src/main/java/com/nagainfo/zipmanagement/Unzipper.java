package com.nagainfomob.zipmanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import com.nagainfomob.smartShowroom.LoadProject;
import com.nagainfomob.smartShowroom.UnzipCompletedListener;

import android.util.Log;

public class Unzipper{

	private static String LOG_TAG = Unzipper.class.getSimpleName();
	String path;
	UnzipCompletedListener listener;

	public Unzipper(String path,UnzipCompletedListener listener) {
		this.path = path;
		this.listener=listener;
	}

	public void unzip(final File file, final File destination)
			throws ZipException, IOException {
		new Thread() {
			public void run() {
				Log.i("path", path);
				long START_TIME = System.currentTimeMillis();
				long FINISH_TIME = 0;
				long ELAPSED_TIME = 0;
				try {
					ZipInputStream zin = new ZipInputStream(
							new FileInputStream(file));
					String workingDir = destination.getAbsolutePath();

					byte buffer[] = new byte[8192];
					int bytesRead;
					ZipEntry entry = null;
					while ((entry = zin.getNextEntry()) != null) {
						if (entry.isDirectory()) {
							File dir = new File(workingDir, entry.getName());
							if (!dir.exists()) {
								dir.mkdir();
							}
							Log.i(LOG_TAG, "[DIR] " + entry.getName());
						} else {
							 File file1=new File(
										workingDir + entry.getName());
					          if(!file1.exists())
					          {
					        	  new File(file1.getParent()).mkdirs();
					        	  file1.createNewFile();
					          }
							
							FileOutputStream fos = new FileOutputStream(
									workingDir + entry.getName());
							while ((bytesRead = zin.read(buffer)) != -1) {
								fos.write(buffer, 0, bytesRead);
							}
							fos.close();
							Log.i(LOG_TAG, "[FILE] " + entry.getName());
						}
					}
					zin.close();

					FINISH_TIME = System.currentTimeMillis();
					ELAPSED_TIME = FINISH_TIME - START_TIME;
					Log.i(LOG_TAG, "COMPLETED in " + (ELAPSED_TIME / 1000)
							+ " seconds.");
//					new LoadProject().getProjectDetails(path);
					listener.onUnzipCompletion(path);
				} catch (Exception e) {
					Log.e(LOG_TAG, "FAILED");
				}
			};
		}.start();
	}

	

}