package com.nagainfo.smartShowroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.nagainfo.database.DatabaseHandler;
import com.nagainfo.zipmanagement.Decompress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Imports extends Activity {

	// Stores names of traversed directories
	ArrayList<String> str = new ArrayList<String>();

	// Check if the first level of the directory structure is the one showing
	private Boolean firstLvl = true;

	private static final String TAG = "F_PATH";

	private Item[] fileList;
	private File path = new File(Environment.getExternalStorageDirectory() + "");
	private String chosenFile;
	private static final int DIALOG_LOAD_FILE = 1000;

	ListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		loadFileList();

		showDialog(DIALOG_LOAD_FILE);
		Log.d(TAG, path.getAbsolutePath());
		// dialog1234 = new ProgressDialog(Imports.this);

	}

	// ProgressDialog dialog1234;

	public void getProjectDetails(String path) {

		String data = null;

		final String xmlFile = path;

		// final String xmlFile="layoutData";
		// ArrayList<String> userData = new ArrayList<String>();
		try {
			// FileInputStream fis =
			// getApplicationContext().openFileInput(xmlFile);
			// InputStreamReader isr = new InputStreamReader(fis);
			FileReader fr = new FileReader(xmlFile);
			StringBuilder text = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(fr);
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			data = new String(text);

		} catch (FileNotFoundException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlPullParserFactory factory = null;
		try {
			factory = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		factory.setNamespaceAware(true);
		XmlPullParser xpp = null;
		try {
			xpp = factory.newPullParser();
		} catch (XmlPullParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			xpp.setInput(new StringReader(data));
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int eventType = 0;
		try {
			eventType = xpp.getEventType();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String name = null;
		// int left = 0, top = 0, width = 0, height = 0, orientation = 0;
		// String selectedTileName = null, unit = null;
		String readValue = null;
		String projectName = null, projectUnit = null, projectLength = null, projectWidth = null, projectHeight = null, projectC = null, projectD = null, CanvasSize=null;
		// float tileLength = 0, tileWidth = 0, tileHeight = 0;
		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_DOCUMENT) {
				System.out.println("Start document");
			} else if (eventType == XmlPullParser.START_TAG) {
				name = xpp.getName();
				// if (name == "layoutData") {
				//
				// }
			} else if (eventType == XmlPullParser.END_TAG) {
				name = xpp.getName();
				if (name.equalsIgnoreCase("proj_name")) {
					// left = Integer.parseInt(readValue);
					projectName = readValue;
				}
				if (name.equalsIgnoreCase("proj_unit")) {
					// tileLength = Float.parseFloat(readValue);
					projectUnit = readValue;
				}
				if (name.equalsIgnoreCase("proj_len")) {
					// tileWidth = Float.parseFloat(readValue);
					projectLength = readValue;
				}
				if (name.equalsIgnoreCase("proj_wid")) {
					// tileHeight = Float.parseFloat(readValue);
					projectWidth = readValue;
				}
				if (name.equalsIgnoreCase("proj_hgt")) {
					// top = Integer.parseInt(readValue);
					projectHeight = readValue;
				}
				if (name.equalsIgnoreCase("proj_c")) {
					// width = Integer.parseInt(readValue);
					projectC = readValue;
				}
				if (name.equalsIgnoreCase("proj_d")) {
					// height = Integer.parseInt(readValue);
					projectD = readValue;
				}
				if (name.equalsIgnoreCase("ViewArea")) {
					// height = Integer.parseInt(readValue);
					CanvasSize = readValue;
				}

				// if (name.equalsIgnoreCase("layoutData")) {}
			} else if (eventType == XmlPullParser.TEXT) {
				// userData.add(xpp.getText());
				readValue = xpp.getText();
				Log.i("value", xpp.getText());
			}
			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// String userName=userData.get(o);
		// String password=userData.get(1);
		DatabaseHandler db = new DatabaseHandler(Imports.this);
		db.insertProject(projectName, projectUnit, projectLength, projectWidth,
				projectHeight, projectC, projectD, CanvasSize);

		// if (dialog.isShowing())
		// dialog.dismiss();

	}

	private void loadFileList() {
		try {
			path.mkdirs();
		} catch (SecurityException e) {
			Log.e(TAG, "unable to write to the sd card ");
		}

		// Checks whether path exists
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					// Filters based on whether the file is hidden or not
					return ((sel.isFile() && sel.toString().endsWith(".zip")) || sel
							.isDirectory());

				}
			};

			String[] fList = path.list(filter);
			fileList = new Item[fList.length];
			for (int i = 0; i < fList.length; i++) {
				fileList[i] = new Item(fList[i], R.drawable.file_icon);

				// Convert into file path
				File sel = new File(path, fList[i]);

				// Set drawables
				if (sel.isDirectory()) {
					fileList[i].icon = R.drawable.directory_icon;
					Log.d("DIRECTORY", fileList[i].file);
				} else {
					Log.d("FILE", fileList[i].file);
				}
			}

			if (!firstLvl) {
				Item temp[] = new Item[fileList.length + 1];
				for (int i = 0; i < fileList.length; i++) {
					temp[i + 1] = fileList[i];
				}
				temp[0] = new Item("Up", R.drawable.directory_up);
				fileList = temp;
			}
		} else {
			Log.e(TAG, "path does not exist");
		}

		adapter = new ArrayAdapter<Item>(this,
				android.R.layout.select_dialog_item, android.R.id.text1,
				fileList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// creates view
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				// put the image on the text view
				textView.setCompoundDrawablesWithIntrinsicBounds(
						fileList[position].icon, 0, 0, 0);

				// add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				textView.setCompoundDrawablePadding(dp5);

				return view;
			}
		};

	}

	private class Item {
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		Builder builder = new Builder(this);

		if (fileList == null) {
			Log.e(TAG, "No files loaded");
			dialog = builder.create();
			return dialog;
		}

		switch (id) {
		case DIALOG_LOAD_FILE:
			builder.setTitle("Choose your file");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chosenFile = fileList[which].file;

					File sel = new File(path + "/" + chosenFile);
					if (sel.isDirectory()) {
						firstLvl = false;

						// Adds chosen directory to list
						str.add(chosenFile);
						fileList = null;
						path = new File(sel + "");

						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}

					// Checks if 'up' was clicked
					else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

						// present directory removed from list
						String s = str.remove(str.size() - 1);

						// path modified to exclude present directory
						path = new File(path.toString().substring(0,
								path.toString().lastIndexOf(s)));
						fileList = null;

						// if there are no more directories in the list, then
						// its the first level
						if (str.isEmpty()) {
							firstLvl = true;
						}
						loadFileList();

						removeDialog(DIALOG_LOAD_FILE);
						showDialog(DIALOG_LOAD_FILE);
						Log.d(TAG, path.getAbsolutePath());

					}
					// File picked
					else {
						// // Perform action with file picked

						// in.putExtra("id", sel.toString());
						// ProgressDialog dialog1234=new
						// ProgressDialog(Imports.this);
						// dialog1234.setMessage("Importing Project..Please Wait..");
						// dialog1234.setProgressStyle(0);
						// dialog1234.show();
						if (chosenFile.startsWith("Rectangle_")
								&& chosenFile.endsWith(".zip")) {
							// new Decompress(sel.toString(), Environment
							// .getExternalStorageDirectory()
							// + "/SmartShowroom/"
							// + chosenFile.replace(".zip", "") + "/")
							// .unzip();
							//
							// getProjectDetails(Environment
							// .getExternalStorageDirectory()
							// + "/SmartShowroom/"
							// + chosenFile.replace(".zip", "")
							// + "/"
							// + chosenFile.replace(".zip", ".xml"));

							Intent in = new Intent();
							in.putExtra(
									"UNZIPLOC",
									Environment.getExternalStorageDirectory()
											+ "/SmartShowroom/"
											+ chosenFile.replace(".zip", "/"));
							in.putExtra("UNZIPFILE", sel.toString());
							in.putExtra(
									"PARSE",
									Environment.getExternalStorageDirectory()
											+ "/SmartShowroom/"
											+ chosenFile.replace(".zip", "")
											+ "/"
											+ chosenFile
													.replace(".zip", ".xml"));
							setResult(1, in);
							// if(dialog1234.isShowing())
							// dialog1234.dismiss();

							finish();
						} else {
							Toast.makeText(getApplicationContext(),
									"Not a SmartShowroom Imported File",
									Toast.LENGTH_LONG).show();
						}

					}
				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}

}
