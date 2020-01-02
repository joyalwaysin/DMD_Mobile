package com.nagainfomob.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nagainfomob.registration.GetDeviceId;
import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.R;
import com.nagainfomob.smartShowroom.XMLParser;
import com.nagainfomob.smartShowroom.R.id;
import com.nagainfomob.smartShowroom.R.layout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityCountry extends Activity {

	private GridView gView;
	private ArrayList<HashMap<String, String>> countryList;
	private static final String KEY_ITEM = "data";
	private static final String KEY_COUNTRY_NAME = "country_name";
	private static final String KEY_COUNTRY_CODE = "country_code";
	private static final String KEY_COUNTRY_IMAGE = "country_image";
	XMLParser parser = new XMLParser();
	private HashMap<String, String> map;
	static Context context;
	Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(layout.layout_upload_activity);
		intialize();
		GetCountry.deligate = this;
		if (GlobalVariables.isNetworkAvailable(this)) {
			new GetCountry(ActivityCountry.this,this).execute(GlobalVariables
					.getApiPath()
					+ "countries?appId="
					+ GetDeviceId.getDeviceid(getApplicationContext()));

		} else {
			GlobalVariables.noNetAvailable(this);
		}
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		bar.setTitle("Download Tile Catalogs");
		context = getApplicationContext();
		activity = this;

	}

	void intialize() {
		gView = (GridView) findViewById(id.brandGrid);
	}

	public static class GetCountry extends AsyncTask<String, String, String> {

		public static ActivityCountry deligate = null;
		private ProgressDialog dialog;
		private Context context;
		String responseCode;
		Activity activity;

		public GetCountry(Context context,Activity activity) {
			this.context = context;
			this.activity = activity;
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			HttpResponse execute = null;
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					execute = client.execute(httpGet);
					responseCode = String.valueOf(execute.getStatusLine()
							.getStatusCode());
					if (responseCode.equals("204"))
						return responseCode;

					InputStream content = execute.getEntity().getContent();

					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (response.equalsIgnoreCase("")) {
				response = "Code"
						+ String.valueOf(execute.getStatusLine()
								.getStatusCode());

			}
			// Log.d("TAG", response);
			return response;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setMessage("Loading...");
			// dialog.setIndeterminate(false);
			// dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// dialog.setProgress(0);
			dialog.show();
			// progressDialog = ProgressDialog.show(ActivityCompany.this,
			// "Wait", "Downloading...");
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			dialog.setProgress(Integer.parseInt(values[0]));
		}

		@Override
		protected void onPostExecute(String result) {

			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			if (result.equals("204")) {
				showMessageDialog("You do not have permissions to access this Catalog. Kindly contact the administrator for access/more information.");
			} else if (result.length() > 0) {
				if (result.startsWith("Code")) {
					result = result.replace("Code", "");
					deligate.setGrid(result, true);
				} else {
					deligate.setGrid(result, false);
				}

			}
		}
		
		public void showMessageDialog(String message) {
			final Dialog d = new Dialog(context);
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			d.setContentView(layout.saved_location);
			d.setCanceledOnTouchOutside(false);
			TextView tv = (TextView) d.findViewById(id.save_loc);
			Button ok = (Button) d.findViewById(id.ok_button);
			tv.setText(message);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					d.dismiss();
					activity.finish();
					
				}
			});
			d.show();
		}

	}

	public void setGrid(String responseXml, Boolean status) {
		try {
			if (status) {
				int mStatusCode = Integer.valueOf(responseXml);
				if (mStatusCode == 204) {
					Toast.makeText(getApplicationContext(),
							"Products Coming Soon! ", Toast.LENGTH_SHORT)
							.show();
					finish();
					return;

				}
			} else {
				countryList = new ArrayList<HashMap<String, String>>();
				Document doc = parser.getDomElement(responseXml);
				NodeList nl = doc.getElementsByTagName(KEY_ITEM);
				for (int i = 0; i < nl.getLength(); i++) {
					map = new HashMap<String, String>();
					Element e = (Element) nl.item(i);
					map.put(KEY_COUNTRY_CODE,
							parser.getValue(e, KEY_COUNTRY_CODE));
					map.put(KEY_COUNTRY_NAME,
							parser.getValue(e, KEY_COUNTRY_NAME));
					map.put(KEY_COUNTRY_IMAGE,
							parser.getValue(e, KEY_COUNTRY_IMAGE));
					countryList.add(map);
				}
				showCountryView(countryList);
			}
		} catch (Exception e) {

		}
	}

	private void showCountryView(ArrayList<HashMap<String, String>> countryList2) {
		// TODO Auto-generated method stub
		CountryGridViewAdapter cAdapter = new CountryGridViewAdapter(this,
				countryList2);
		gView.setAdapter(cAdapter);

	}
}
