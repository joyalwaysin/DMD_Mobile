package com.nagainfomob.smartShowroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.nagainfomob.database.DatabaseHandler;
import com.nagainfomob.zipmanagement.Decompress;
import com.nagainfomob.zipmanagement.Unzipper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LoadProject extends Activity implements LoadprojectListener,UnzipCompletedListener {
	private static Activity activity;
	ArrayList<HashMap<String, String>> dbResult;
	ListView lView;
	private static final String PROJ_ID = "proj_id";
	private static final String PROJ_NAME = "proj_name";
	private static final String PROJ_UNIT = "proj_unit";
	private static final String PROJ_LENGTH = "proj_len";
	private static final String PROJ_WIDTH = "proj_wid";
	private static final String PROJ_HEIGHT = "proj_hgt";
	private static final String PROJ_C = "proj_c";
	private static final String PROJ_D = "proj_d";
	View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_project);
		
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		getActionBar().setTitle("LOAD PROJECT");
		
		getActionBar().setDisplayShowHomeEnabled(false);
//		LoadProject.this.getActionBar().setBackgroundDrawable(
//				getResources().getDrawable(R.drawable.actionbar_bg));
//		
//		LoadProject.this.getActionBar().setTitle("Load Project");
//		LoadProject.this.
//		this.getActionBar().setTitle("Load Project");
		activity = this;
		lView = (ListView) findViewById(R.id.list_project);
		getProjects();

	}
	public void getProjectDetails(String path) {

		String data = null;

		final String xmlFile = path;

		// final String xmlFile="layoutData";
//		ArrayList<String> userData = new ArrayList<String>();
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
		String projectName = null, projectUnit = null, projectLength = null, projectWidth = null, projectHeight = null, projectC = null, projectD = null,drawArea=null;
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
				if (name.equalsIgnoreCase("ViewArea"))
				{
					drawArea = readValue;
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
		DatabaseHandler db = new DatabaseHandler(LoadProject.this);
		db.insertProject(projectName, projectUnit, projectLength, projectWidth,
				projectHeight, projectC, projectD,drawArea);

//		if (dialog.isShowing())
//			dialog.dismiss();

	}
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		activity.recreate();
//		super.onResume();
//	}
	ProgressDialog progdlg;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1)
		{
			if(resultCode==1)
			{

				String unzloc=data.getStringExtra("UNZIPLOC");
				String par=data.getStringExtra("PARSE");
				String unzfile=data.getStringExtra("UNZIPFILE");

				new RunUnzipandProjectDetails(unzloc,unzfile,par).execute();

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.import_project_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.importProject:
				Intent in=new Intent(LoadProject.this,Imports.class);
				startActivityForResult(in, 1);
			break;
		default:
			break;
		}

		return true;
	}

	private void getProjects() {
		// TODO Auto-generated method stub
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		lView.invalidate();
		dbResult = new ArrayList<HashMap<String, String>>();
		dbResult = db.getProject();
		ProjectListAdapter plAdapter = new ProjectListAdapter(activity,
				dbResult, this);
		lView.setAdapter(plAdapter);
		lView.invalidate();
	}

	public void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	void showLoadNameDialog(final HashMap<String, String> map) {
		final Dialog dialog = new Dialog(LoadProject.this);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.load_as_dialog);
		
//		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.gravity = Gravity.CENTER;

//        dialog.getWindow().setAttributes(lp);
		
		dialog.setTitle("Open As");
		final EditText loadAsNameField = (EditText) dialog
				.findViewById(R.id.loadAsNameField);
		final String oldName = map.get(PROJ_NAME);
		loadAsNameField.setText(map.get(PROJ_NAME));
		Button openButton = (Button) dialog.findViewById(R.id.loadButton);
		openButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DatabaseHandler db = new DatabaseHandler(
						getApplicationContext());
				String newName = loadAsNameField.getText().toString();
				if ((newName.trim()).length() > 0) {

					if ((newName.trim()).length() > 30) {

						Toast.makeText(getApplicationContext(),
								"Project name too large!", Toast.LENGTH_SHORT)
								.show();
						return;

					}
					if (!db.isProjectExist(newName)) {
						map.remove(PROJ_NAME);
						if (oldName.startsWith("Custom Shape")) {
							map.put(PROJ_NAME,
									"Custom Shape 1_"
											+ newName.replace(
													"Custom Shape 1_", ""));

						} else if (oldName.startsWith("Rectangle")) {
							map.put(PROJ_NAME,
									"Rectangle_"
											+ newName.replace("Rectangle_", ""));

						}
						String sourcePath = Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/SmartShowRoom/" + oldName + "/";
						String destPath = Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/SmartShowRoom/" + map.get(PROJ_NAME) + "/";
						try {
							copyDirectory(new File(sourcePath), new File(
									destPath));
							loadProject(map, true);
							dialog.dismiss();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						Toast.makeText(getApplicationContext(),
								"Project with same name exists!",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
		dialog.show();
	}

	void showLoadDialog(final HashMap<String, String> map) {
		final Dialog dialog = new Dialog(LoadProject.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.load_project_as);
//		dialog.setTitle("Open Project...");
		final Dialog dialog1 = new Dialog(LoadProject.this);
		dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog1.setContentView(R.layout.saved_location);
		
		Button ok= (Button) dialog1.findViewById(R.id.ok_button);
		final TextView tv= (TextView)dialog1.findViewById(R.id.save_loc);

		Button openButton = (Button) dialog.findViewById(R.id.open_project);
		openButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loadProject(map, false);

			}
		});

		Button openAsButton = (Button) dialog
				.findViewById(R.id.open_project_as);
		openAsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				showLoadNameDialog(map);
			}
		});

		Button exportButton = (Button) dialog.findViewById(R.id.export);

		exportButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatabaseHandler db = new DatabaseHandler(LoadProject.this);
				db.exportProject(map.get(PROJ_NAME),
						Environment.getExternalStorageDirectory()
								+ "/SmartShowRoom/Exported Projects");
				dialog.dismiss();
				tv.setText("Project imported to "+Environment.getExternalStorageDirectory()
						+ "/SmartShowRoom/Exported Projects/"+map.get(PROJ_NAME)+".zip");
				dialog1.show();
			}
		});

		dialog.show();
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog1.dismiss();
			}
		});
	}

	void loadProject(HashMap<String, String> map, Boolean status) {
		GlobalVariables.setProjectName(map.get(PROJ_NAME));
		GlobalVariables.setUnit(map.get(PROJ_UNIT));

		Intent intent;
		if (map.get(PROJ_NAME).startsWith("Custom Shape")) {
			intent = new Intent(getApplicationContext(), Room3DActivity2.class);
			GlobalVariables.setWallDim(Float.valueOf(map.get(PROJ_HEIGHT)),
					Float.valueOf(map.get(PROJ_WIDTH)),
					Float.valueOf(map.get(PROJ_LENGTH)),
					Float.valueOf(map.get(PROJ_C)),
					Float.valueOf(map.get(PROJ_D)));
			intent.putExtra("dimenC", map.get(PROJ_C));
			intent.putExtra("dimenD", map.get(PROJ_D));
		} else {
			intent = new Intent(getApplicationContext(), Room3DActivity.class);
			GlobalVariables.setWallDim(Float.valueOf(map.get(PROJ_HEIGHT)),
					Float.valueOf(map.get(PROJ_WIDTH)),
					Float.valueOf(map.get(PROJ_LENGTH)), 0, 0);

		}
		intent.putExtra("dimenA", map.get(PROJ_WIDTH)+"");
		intent.putExtra("dimenB", map.get(PROJ_LENGTH)+"");

		intent.putExtra("wallHeight", map.get(PROJ_HEIGHT)+"");
		if (status) {
			GlobalVariables.saveProject(getApplicationContext());
		}
		startActivity(intent);
		finish();
	}

	@Override
	public void selectedProject(ArrayList<HashMap<String, String>> result,
			int pos) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = result.get(pos);

		showLoadDialog(map);
	}
	ProgressDialog dialog1234;
	public class RunUnzipandProjectDetails extends AsyncTask<String, String, String>
	{
		String unzloc,unzfile,par;
		
		public RunUnzipandProjectDetails(String unzloc, String unzfile,String par)
		{
			this.unzfile=unzfile;
			this.unzloc=unzloc;
			this.par=par;
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				new Decompress(unzfile, unzloc).unzip();
				
//				new Unzipper(par,LoadProject.this).unzip(new File(unzfile),new File(unzloc));
				getProjectDetails(par);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			getProjectDetails(par);
			
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog1234=new ProgressDialog(LoadProject.this);
			dialog1234.setMessage("Importing Project..Please Wait..");
			dialog1234.setCanceledOnTouchOutside(false);
			dialog1234.show();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(dialog1234.isShowing())dialog1234.dismiss();
			getProjects();
		}
		
	}

	@Override
	public void onUnzipCompletion(String path) {
		// TODO Auto-generated method stub
		getProjectDetails(path);
		if(dialog1234.isShowing())dialog1234.dismiss();
		getProjects();
	}
}
