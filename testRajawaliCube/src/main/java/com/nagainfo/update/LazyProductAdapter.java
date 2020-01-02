package com.nagainfomob.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;


import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.R;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyProductAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> filePath;
	private Activity activity;
	private LayoutInflater inflater = null;
	private com.nagainfomob.update.ImageLoader imageLoader;

	private static final String KEY_PRO_ID = "product_id";
	private static final String KEY_PRO_NAME = "product_name";
	private static final String KEY_PRO_COLOR = "product_color";
	private static final String KEY_PRO_DIMEN = "product_dimension";
	private static final String KEY_PRO_TYPE = "product_application";
	private static final String KEY_PRO_TECH = "product_technology";
	private static final String KEY_PRO_IMG = "product_image";
	private static final String KEY_BRAND_ID = "brand_id";
	private static final String KEY_BRAND_NAME = "brand_name";

	public LazyProductAdapter(Activity a,
			ArrayList<HashMap<String, String>> brandList2) {
		filePath = brandList2;
		activity = a;

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
		List<String> productName = new ArrayList<String>();
		List<String> brand_id = new ArrayList<String>();
		for (int i = 0; i < filePath.size(); i++) {
			Map<String, String> temp1 = filePath.get(i);
			imageUrl.add(temp1.get(KEY_PRO_IMG));
			brandName1.add(temp1.get(KEY_BRAND_NAME));
			brand_id.add(temp1.get(KEY_PRO_ID));
			productName.add(temp1.get(KEY_PRO_NAME));
		}

		String[] imagePath = new String[imageUrl.size()];
		for (int i = 0; i < imageUrl.size(); i++) {
			imagePath[i] = imageUrl.get(i);
		}

		View v1 = convertView;
		if (convertView == null)
			v1 = inflater.inflate(R.layout.item1, null);
		ImageView brandImage = (ImageView) v1.findViewById(R.id.brand_image);

		String[] brandName = new String[brandName1.size()];
		for (int i = 0; i < brandName1.size(); i++) {
			brandName[i] = brandName1.get(i);
		}
		String[] pro_name = new String[productName.size()];
		for (int i = 0; i < productName.size(); i++) {
			pro_name[i] = productName.get(i);
		}
		String[] id = new String[brand_id.size()];
		for (int i = 0; i < brand_id.size(); i++) {
			id[i] = brand_id.get(i);
		}
		TextView pr_Name = (TextView) v1.findViewById(R.id.brand_name);
		pr_Name.setText(pro_name[position]);
		imageLoader.DisplayImage(GlobalVariables.getDownloadPath()
				+brandName[position]+"/"+ imagePath[position], brandImage);

		v1.setOnTouchListener(brandTouchListener);
		v1.setTag(id[position]);
		return v1;

		// return null;

	}

	OnTouchListener brandTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				/*
				 * String id = v.getTag().toString(); Intent i = new
				 * Intent(activity, ActivityProduct.class);
				 * i.putExtra("brandId", id); activity.startActivity(i);
				 */
				return true;
			}
			return false;
		}
	};

}
