package com.nagainfomob.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.nagainfomob.database.DatabaseHandler;
import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.XMLParser;

public class ProductDownloadThread implements Runnable {

	private ArrayList<HashMap<String, String>> filePath;
	private Activity activity;
	private LayoutInflater inflater = null;
	private com.nagainfomob.update.ImageLoader imageLoader;

	private static final String KEY_ITEM = "data";
	private static final String KEY_BRAND_ID = "brand_id";
	private static final String KEY_BRAND_NAME = "brand_name";
	private static final String KEY_BRAND_IMAGE = "brand_image";
	Dialog dialog;

	private static final String KEY_PRO_ID = "product_id";
	private static final String KEY_PRO_NAME = "product_name";
	private static final String KEY_PRO_COLOR = "product_color";
	private static final String KEY_PRO_DIMEN = "product_dimension";
	private static final String KEY_PRO_TYPE = "product_application";
	private static final String KEY_PRO_TECH = "product_technology";
	private static final String KEY_PRO_IMG = "product_image";
	private static final String KEY_COMPANY = "company_name";

	XMLParser parser = new XMLParser();
	private ArrayList<HashMap<String, String>> productList;
	private HashMap<String, String> map;

	public DownloadCompleteListener Deligate = null;

	public ProductDownloadThread(
			ArrayList<HashMap<String, String>> productList, Activity activity) {
		this.productList = productList;
		this.activity = activity;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String path;

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/";
		File directoryPath = new File(path);
		if (!directoryPath.exists()) {
			directoryPath.mkdirs();
			GlobalVariables.createNomediafile(path);
		}

		for (int i = 0; i < productList.size(); i++) {
			Log.e("thread", "thrad Start");
			download(i, path);
			this.Deligate.brandDownloadComplete(i);
		}

	}

	private boolean checkForStorage() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = GlobalVariables.getPatternRootPath();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageAvailable;
	}

	private void download(int count, String rootPath) {
		HashMap<String, String> map = productList.get(count);
		Log.e("thread", "thrad Start");
		String brandPath = rootPath + "/" + map.get(KEY_BRAND_NAME);

		if (!new File(brandPath).exists()) {
			
			new File(brandPath).mkdirs();
			GlobalVariables.createNomediafile(brandPath+"/");
		}
		String filePath = brandPath + "/" + map.get(KEY_PRO_NAME) + ".jpg";
		String downloadPath = GlobalVariables.getDownloadPath()
				+ map.get(KEY_COMPANY) + "/" + map.get(KEY_BRAND_NAME) + "/"
				+ map.get(KEY_PRO_IMG);
		Boolean status = downloadFromUrl(downloadPath, filePath, brandPath);
		
		if (status) {
			DatabaseHandler db = new DatabaseHandler(activity);
			List<File> files = getList(new File(brandPath), brandPath); 
			File file1 = new File(filePath);
//			Log.d("TAG", files.get(0).toString());
			if(!files.contains(file1.toString()))
			{
			db.insertRecord(map.get(KEY_PRO_NAME), map.get(KEY_PRO_COLOR),
					map.get(KEY_PRO_DIMEN), map.get(KEY_PRO_TYPE),
					map.get(KEY_PRO_TECH), map.get(KEY_BRAND_ID),
					map.get(KEY_BRAND_NAME), map.get(KEY_COMPANY));
			}
			db.close();
		}
	}
	
	private List<File> getList(File parentDir, String pathToParentDir) {

	    ArrayList<File> inFiles = new ArrayList<File>();
	    File[] fileNames = parentDir.listFiles();

	    for (File fileName : fileNames) {
	        
	            inFiles.add(fileName);
//	        } else {
//	            File file = new File(parentDir.getPath() + "/" + fileName);
//	            if (file.isDirectory()) {
//	                inFiles.addAll(getList(file, pathToParentDir + fileName + "/"));
//	            }
//	        }
	    }

	    return inFiles;
	}
	
	private Boolean downloadFromUrl(String downloadPath, String fileName, String path) {
		Boolean status = false;
		try {
			URL url = new URL(downloadPath.replace(" ", "")); // you can write
																// here any link
			
			
			File file = new File(fileName);
			
//			if(!files.contains(file))
//			{
				
				
				URLConnection ucon = url.openConnection();
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				/*
				 * Read bytes to the Buffer until there is nothing more to read(-1).
				 */
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();
				status = true;	
//			}
//			else
//			{
////				Toast.makeText(, text, duration)
//			}
			
		} catch (IOException e) {
			//Log.e("Error: ", e.getMessage());
			status = false;
		}
		return status;
	}

}
