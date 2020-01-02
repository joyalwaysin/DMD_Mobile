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

import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.R;
import com.nagainfomob.smartShowroom.XMLParser;

import android.R.array;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class ActivityProduct extends Activity {

	Bundle getExtraVal;
	String brand_id;
	GridView gView;

	private static final String KEY_ITEM = "data";
	private static final String KEY_PRO_ID = "product_id";
	private static final String KEY_PRO_NAME = "product_name";
	private static final String KEY_PRO_COLOR = "product_color";
	private static final String KEY_PRO_DIMEN = "product_dimension";
	private static final String KEY_PRO_TYPE = "product_application";
	private static final String KEY_PRO_TECH = "product_technology";
	private static final String KEY_PRO_IMG = "product_image";
	private static final String KEY_BRAND_ID = "brand_id";
	private static final String KEY_BRAND_NAME = "brand_name";
	private static final String KEY_COMPANY = "pro_company";

	XMLParser parser = new XMLParser();
	private ArrayList<HashMap<String, String>> productList;
	private HashMap<String, String> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_product);
		getExtraVal = getIntent().getExtras();
		brand_id = getExtraVal.getString("brandId");
		gView = (GridView) findViewById(R.id.productGrid);
		getProduct.deligate = this;
		if (GlobalVariables.isNetworkAvailable(this)) {
			new getProduct(ActivityProduct.this,this).execute(GlobalVariables
					.getApiPath() + "products?brand_id=" + brand_id);
		} else {
			GlobalVariables.noNetAvailable(this);
		}

	}

	public void setGrid(String result) {
		// TODO Auto-generated method stub
		productList = new ArrayList<HashMap<String, String>>();
		Document doc = parser.getDomElement(result);
		NodeList nl = doc.getElementsByTagName(KEY_ITEM);
		for (int i = 0; i < nl.getLength(); i++) {
			map = new HashMap<String, String>();
			Element e = (Element) nl.item(i);
			map.put(KEY_PRO_ID, parser.getValue(e, KEY_PRO_ID));
			map.put(KEY_PRO_NAME, parser.getValue(e, KEY_PRO_NAME));
			map.put(KEY_PRO_COLOR, parser.getValue(e, KEY_PRO_COLOR));
			map.put(KEY_PRO_DIMEN, parser.getValue(e, KEY_PRO_DIMEN));
			map.put(KEY_PRO_TYPE, parser.getValue(e, KEY_PRO_TYPE));
			map.put(KEY_PRO_TECH, parser.getValue(e, KEY_PRO_TECH));
			map.put(KEY_PRO_IMG, parser.getValue(e, KEY_PRO_IMG));
			map.put(KEY_BRAND_ID, parser.getValue(e, KEY_BRAND_ID));
			map.put(KEY_BRAND_NAME, parser.getValue(e, KEY_BRAND_NAME));
			map.put(KEY_COMPANY, parser.getValue(e, KEY_COMPANY));
			productList.add(map);

		}
		loadProduct(productList);

	}

	private void loadProduct(ArrayList<HashMap<String, String>> productList2) {
		// TODO Auto-generated method stub
		LazyProductAdapter lpAdapter = new LazyProductAdapter(this,
				productList2);
		gView.setAdapter(lpAdapter);

	}

	public static class getProduct extends AsyncTask<String, String, String> {

		public static ActivityProduct deligate = null;
		private Context context;
		String responseCode;
		Activity activity;

		public getProduct(Context context, Activity activity) {
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
