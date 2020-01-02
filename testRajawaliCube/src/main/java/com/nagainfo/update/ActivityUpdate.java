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

public class ActivityUpdate extends Activity {
	GridView gView;
	String[] fileUrl;
	XMLParser parser = new XMLParser();
	private HashMap<String, String> map;
	private ArrayList<HashMap<String, String>> brandList;
	private Bundle getExtraVal;
	static Context context;

	private String company_id;
	private String company_name;

	private static final String KEY_ITEM = "data";
	private static final String KEY_BRAND_ID = "brand_id";
	private static final String KEY_BRAND_NAME = "brand_name";
	private static final String KEY_BRAND_IMAGE = "brand_image";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_upload_activity);
		webresult.deligate = this;
		getExtraVal = getIntent().getExtras();
		company_id = getExtraVal.getString("brandId");
		company_name = getExtraVal.getString("compnayName");
		gView = (GridView) findViewById(R.id.brandGrid);
		if (GlobalVariables.isNetworkAvailable(this)) {
			new webresult(ActivityUpdate.this,this).execute(GlobalVariables
					.getApiPath()
					+ "brands?appId="
					+ GetDeviceId.getDeviceid(getApplicationContext())
					+ "&company_id=" + company_id);
		} else {
			GlobalVariables.noNetAvailable(this);
		}
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		bar.setTitle("Download Tile Catalogs");
		context = getApplicationContext();

	}

	public void setGrid(String responseXml) {
		try {
			brandList = new ArrayList<HashMap<String, String>>();
			if (responseXml != null && !responseXml.equalsIgnoreCase("")) {
				Document doc = parser.getDomElement(responseXml);
				NodeList nl = doc.getElementsByTagName(KEY_ITEM);
				for (int i = 0; i < nl.getLength(); i++) {
					map = new HashMap<String, String>();
					Element e = (Element) nl.item(i);
					map.put(KEY_BRAND_ID, parser.getValue(e, KEY_BRAND_ID));
					map.put(KEY_BRAND_NAME, parser.getValue(e, KEY_BRAND_NAME));
					map.put(KEY_BRAND_IMAGE,
							parser.getValue(e, KEY_BRAND_IMAGE));
					brandList.add(map);
				}
				showBrandView(brandList);
			} else {
				finish();
				Toast.makeText(getApplicationContext(), "Brands Coming Soon",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception ex) {

		}
	}

	private void showBrandView(ArrayList<HashMap<String, String>> brandList2) {
		// TODO Auto-generated method stub

		LazyAdapter lAdapter = new LazyAdapter(this, brandList2, company_name,company_id);
		gView.setAdapter(lAdapter);

	}

	public static class webresult extends AsyncTask<String, String, String> {

		public static ActivityUpdate deligate = null;
		private ProgressDialog dialog;
		private Context context;
		Activity activity;
		String responseCode;

		public webresult(Context context, Activity activity) {
			this.context = context;
			this.activity = activity;
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
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
				deligate.setGrid(result);
				
			}

		}

		public void showMessageDialog(String message) {
			final Dialog d = new Dialog(context);
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			d.setContentView(R.layout.saved_location);
			d.setCanceledOnTouchOutside(false);
			TextView tv = (TextView) d.findViewById(R.id.save_loc);
			Button ok = (Button) d.findViewById(R.id.ok_button);
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

}
