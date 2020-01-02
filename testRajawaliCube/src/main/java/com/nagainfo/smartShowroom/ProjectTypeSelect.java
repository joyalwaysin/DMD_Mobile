package com.nagainfomob.smartShowroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.nagainfomob.registration.GetDeviceId;
import com.nagainfomob.registration.SessionManager;
import com.nagainfomob.smartShowroom.HomeActivity.ActivateApp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ProjectTypeSelect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_type_select);
		final Button threeD = (Button) findViewById(R.id.three_d_project);
		final Button ambience = (Button) findViewById(R.id.ambience);
		
//		if(!GlobalVariables.getActCheckStatus()){
//			if (isNetworkAvailable(ProjectTypeSelect.this)) {
//				new HomeActivity.ActivateApp(ProjectTypeSelect.this).execute(GlobalVariables.activateUrl
//						+ GetDeviceId
//								.getDeviceid(ProjectTypeSelect.this));
//				
//			}
//		}

		threeD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check3DActivated()) {
					Intent i = new Intent(getApplicationContext(),
							NewProjectActivity.class);
					startActivity(i);

					finish();
				} else {
					showMessageDialog(GlobalVariables.featureLockedMessage);
//					if(!GlobalVariables.getActCheckStatus()){
//						if (isNetworkAvailable(ProjectTypeSelect.this)) {
//							new HomeActivity.ActivateApp(ProjectTypeSelect.this).execute(GlobalVariables.activateUrl
//									+ GetDeviceId
//											.getDeviceid(ProjectTypeSelect.this));
//							
//						}
//					}
				}
			}
		});

		ambience.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkAmbienceActivated()) {
					Intent intent = new Intent(getApplicationContext(),
							AmbienceViewSelector.class);
					startActivity(intent);
					finish();
				} else {
					showMessageDialog(GlobalVariables.featureLockedMessage);
//					if(!GlobalVariables.getActCheckStatus()){
//						if (isNetworkAvailable(ProjectTypeSelect.this)) {
//							new HomeActivity.ActivateApp(ProjectTypeSelect.this).execute(GlobalVariables.activateUrl
//									+ GetDeviceId
//											.getDeviceid(ProjectTypeSelect.this));
//							
//						}
//					}
				}
			}
		});
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					Log.i("Class", info[i].getState().toString());
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {

						return true;

					}
				}
			}
		}
		return false;
	}

	public Boolean check3DActivated() {
		Boolean status = null;
		SessionManager session = new SessionManager(getApplicationContext());
		status = session.is3DActivated();

		return status;

	}

	public Boolean checkAmbienceActivated() {
		Boolean status = null;
		SessionManager session = new SessionManager(getApplicationContext());
		status = session.isAmbienceActivated();

		return status;
	}

	public void showMessageDialog(String message) {
		final Dialog d = new Dialog(ProjectTypeSelect.this);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.saved_location);
		TextView tv = (TextView) d.findViewById(R.id.save_loc);
		Button ok = (Button) d.findViewById(R.id.ok_button);
		tv.setText(message);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				d.dismiss();
			}
		});
		d.show();
	}

}
