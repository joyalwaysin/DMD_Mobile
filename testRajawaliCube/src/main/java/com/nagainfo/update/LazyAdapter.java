package com.nagainfomob.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nagainfomob.database.*;
import com.nagainfomob.registration.GetDeviceId;
import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.R;
import com.nagainfomob.smartShowroom.XMLParser;

public class LazyAdapter extends BaseAdapter implements
		DownloadCompleteListener {

	private ArrayList<HashMap<String, String>> filePath;
	private Activity activity;
	private LayoutInflater inflater = null;
	private com.nagainfomob.update.ImageLoader imageLoader;

	private static final String KEY_ITEM = "data";
	private static final String KEY_BRAND_ID = "brand_id";
	private static final String KEY_BRAND_NAME = "brand_name";
	private static final String KEY_BRAND_IMAGE = "brand_image";
	Dialog dialog;

	private static final String KEY_COMPANY = "company_name";
	private static final String KEY_PRO_ID = "product_id";
	private static final String KEY_PRO_NAME = "product_name";
	private static final String KEY_PRO_COLOR = "product_color";
	private static final String KEY_PRO_DIMEN = "product_dimension";
	private static final String KEY_PRO_TYPE = "product_application";
	private static final String KEY_PRO_TECH = "product_technology";
	private static final String KEY_PRO_IMG = "product_image";
	ProgressBar downloadProgress;
	Button dialogCancel;
	Button dialogOk;

	XMLParser parser = new XMLParser();
	private ArrayList<HashMap<String, String>> productList;
	private HashMap<String, String> map;
	private ProgressDialog progress;
	private Object company_name;
	private Object company_id;

	public LazyAdapter(Activity a,
			ArrayList<HashMap<String, String>> brandList2, String company_name,String company_id) {
		filePath = brandList2;
		activity = a;
		this.company_name = company_name;
		this.company_id=company_id;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return filePath.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		List<String> imageUrl = new ArrayList<String>();
		List<String> brandName1 = new ArrayList<String>();
		List<String> mStringArray = new ArrayList<String>();
		List<String> brand_id = new ArrayList<String>();
		for (int i = 0; i < filePath.size(); i++) {
			Map<String, String> temp1 = filePath.get(i);
			imageUrl.add(temp1.get(KEY_BRAND_IMAGE));
			brandName1.add(temp1.get(KEY_BRAND_NAME));
			brand_id.add(temp1.get(KEY_BRAND_ID));
		}

		String[] imagePath = new String[imageUrl.size()];
		for (int i = 0; i < imageUrl.size(); i++) {
			imagePath[i] = GlobalVariables.getDownloadPath() + company_name + "/"
					+ imageUrl.get(i);
		}

		View v1 = convertView;
		if (convertView == null)
			v1 = inflater.inflate(R.layout.item1, null);
		ImageView brandImage = (ImageView) v1.findViewById(R.id.brand_image);

		String[] name = new String[imageUrl.size()];
		for (int i = 0; i < imageUrl.size(); i++) {
			name[i] = imageUrl.get(i).replace(".jpg", "");
		}
		String[] id = new String[brand_id.size()];
		for (int i = 0; i < brand_id.size(); i++) {
			id[i] = brand_id.get(i);
		}
		TextView brandName = (TextView) v1.findViewById(R.id.brand_name);
		brandName.setText(name[position]);
		imageLoader.DisplayImage(imagePath[position], brandImage);

		v1.setOnTouchListener(brandTouchListener);
		v1.setTag(id[position]);
		progress = new ProgressDialog(activity);
		return v1;

		// return null;

	}

	OnTouchListener brandTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				Dialog d = downloadPrompt(v);

				d.show();

				/*
				 * String id = v.getTag().toString(); Intent i = new
				 * Intent(activity, ActivityProduct.class);
				 * i.putExtra("brandId", id); activity.startActivity(i); return
				 * true;
				 */
			}
			return false;
		}
	};

	public Dialog downloadPrompt(View v) {

		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.download_prompt);
		dialogCancel = (Button) dialog.findViewById(R.id.prom_but_can);
		dialogOk = (Button) dialog.findViewById(R.id.prom_but_ok);
		downloadProgress = (ProgressBar) dialog
				.findViewById(R.id.DownloadProgress);
		dialogCancel.setOnClickListener(promCloseListener);
		dialogOk.setOnClickListener(promOkListener);
		dialogOk.setTag(v.getTag().toString());
		return dialog;
	}

	OnClickListener promCloseListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.cancel();

		}
	};

	OnClickListener promOkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String id = v.getTag().toString();
			// new GetProduct().execute(GlobalVariables.getApiPath() +
			// "brands");
			new GetProduct().execute(GlobalVariables.getApiPath()
					+ "products?brand_id=" + id + "&appId="
					+ GetDeviceId.getDeviceid(activity)+"&company_id="+company_id);
		}
	};

	public class GetProduct extends AsyncTask<String, String, String> {

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

			processResult(result);

		}
	}

	public void processResult(String result) {
		// TODO Auto-generated method stub
		try {
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
			DownloadProduct(this.productList);
		} catch (Exception e) {
			if (productList.size() <= 0)
				Toast.makeText(activity.getApplicationContext(),
						"No Tiles found", Toast.LENGTH_SHORT).show();
		}

	}

	private void DownloadProduct(ArrayList<HashMap<String, String>> productList2) {
		// TODO Auto-generated method stub
		dialog.cancel();
		progress.setMessage("Downloading Products...");
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setIndeterminate(false);
		progress.setCanceledOnTouchOutside(false);
		progress.setMax(productList2.size());
		progress.show();

		ProductDownloadThread pdRunnable = new ProductDownloadThread(
				productList2, activity);
		pdRunnable.Deligate = this;
		Thread th = new Thread(pdRunnable);
		th.start();

	}

	@Override
	public void brandDownloadComplete(final int id) {
		// TODO Auto-generated method stub

		activity.runOnUiThread(new Runnable() {
			public void run() {
				progress.setProgress(id + 1);

			}
		});
		if (id + 1 == productList.size()) {
			progress.cancel();
			activity.runOnUiThread(new Runnable() {
				public void run() {
					
					Toast.makeText(activity, "Download completed!",
							Toast.LENGTH_SHORT).show();

				}
			});

		}

		dialog.cancel();

	}
}
