package com.nagainfo.update;

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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.GridView;

import com.nagainfo.registration.GetDeviceId;
import com.nagainfo.smartShowroom.GlobalVariables;
import com.nagainfo.smartShowroom.R;
import com.nagainfo.smartShowroom.XMLParser;

public class ActivityOffers extends Activity {
	GridView gView;
	String[] fileUrl;
	XMLParser parser = new XMLParser();
	private HashMap<String, String> map;
	private ArrayList<HashMap<String, String>> brandList;

	private static final String KEY_ITEM = "data";
	private static final String KEY_OFFER_ID = "offer_id";
	private static final String KEY_OFFER_NAME = "offer_name";
	private static final String KEY_OFFER_DETAILS = "offer_details";
	private static final String KEY_OFFER_IMAGE = "offer_image";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_offer_activity);
		webresult.deligate = this;
		gView = (GridView) findViewById(R.id.offerGrid);
		if (GlobalVariables.isNetworkAvailable(this)) {
			new webresult().execute(GlobalVariables.getApiPath()
					+ "offers?appId="
					+ GetDeviceId.getDeviceid(getApplicationContext()));
		} else {
			GlobalVariables.noNetAvailable(this);
		}
	}

	public void setGrid(String responseXml) {
		brandList = new ArrayList<HashMap<String, String>>();
		Document doc = parser.getDomElement(responseXml);
		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		for (int i = 0; i < nl.getLength(); i++) {
			map = new HashMap<String, String>();
			Element e = (Element) nl.item(i);
			map.put(KEY_OFFER_ID, parser.getValue(e, KEY_OFFER_ID));
			map.put(KEY_OFFER_NAME, parser.getValue(e, KEY_OFFER_NAME));
			map.put(KEY_OFFER_DETAILS, parser.getValue(e, KEY_OFFER_DETAILS));
			map.put(KEY_OFFER_IMAGE, parser.getValue(e, KEY_OFFER_IMAGE));
			brandList.add(map);
		}
		showBrandView(brandList);
	}

	private void showBrandView(ArrayList<HashMap<String, String>> brandList2) {
		// TODO Auto-generated method stub

		OfferGridViewAdapter lAdapter = new OfferGridViewAdapter(this,
				brandList2);
		gView.setAdapter(lAdapter);

	}

	public static class webresult extends AsyncTask<String, String, String> {

		public static ActivityOffers deligate = null;

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
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
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			deligate.setGrid(result);
		}

	}

}
