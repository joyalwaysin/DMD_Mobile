package com.nagainfomob.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

public class FileUtils {

	
	/**
	 * Checks to see if the storage is available to be written to You should
	 * still check individual files .canWrite() methods
	 * 
	 * @return true if the external storage is writable
	 */

	public static boolean isStorageWritable(Context context) {
		boolean externalStorageAvailable;
		boolean externalStorageWriteable;

		// Get the external storage's state
		String state = Environment.getExternalStorageState();
		
		
		

		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// Storage is available and writeable
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			// Storage is only readable
			externalStorageAvailable = true;
			externalStorageWriteable = false;
			Toast.makeText(context, "SD Card is not Writable", Toast.LENGTH_LONG).show();
		} else {
			// Storage is neither readable nor writeable
			externalStorageAvailable = externalStorageWriteable = false;
			Toast.makeText(context, "SD Card is not Available", Toast.LENGTH_LONG).show();
		}
		return externalStorageAvailable && externalStorageWriteable;
	}
	
	public static void writeErrorMessageToFile(Exception e, String fileName) {

		PrintWriter pw = null;
		try {

			File file = new File(Environment.getExternalStorageDirectory()
					+ "/SmartShowroom/ErrorLog/" + fileName + ".txt");
			if (!file.getParentFile().exists())
				try {

					file.getParentFile().mkdirs();
					file.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated
					// catch block
					e1.printStackTrace();
				}
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch
			// block
			e1.printStackTrace();
		}
		e.printStackTrace(pw);
		pw.close();

	}
	
	// Check whether storage space is available, returns true if available
	
	public static boolean isMemoryAvailable(Context context){
		Boolean available=true;
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		long bytesAvailable = stat.getAvailableBytes();
		long megAvailable = bytesAvailable / 1048576;
		
		if(megAvailable>1){
			available = true;
		}else{
			available=false;
			Toast.makeText(context, "Storage Low on Memory", 1).show();
		}
		
		return available;
	}

}
