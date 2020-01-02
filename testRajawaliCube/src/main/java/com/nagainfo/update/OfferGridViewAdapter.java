package com.nagainfomob.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.R;

public class OfferGridViewAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> filePath;
	private Activity activity;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	private static final String KEY_ITEM = "data";
	private static final String KEY_OFFER_ID = "offer_id";
	private static final String KEY_OFFER_NAME = "offer_name";
	private static final String KEY_OFFER_DETAILS = "offer_details";
	private static final String KEY_OFFER_IMAGE = "offer_image";

	public OfferGridViewAdapter(Activity a,
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
		View v = convertView;
		List<String> imageUrl = new ArrayList<String>();
		List<String> offerName1 = new ArrayList<String>();
		List<String> mStringArray = new ArrayList<String>();
		List<String> offer_id = new ArrayList<String>();
		for (int i = 0; i < filePath.size(); i++) {
			Map<String, String> temp1 = filePath.get(i);
			imageUrl.add(temp1.get(KEY_OFFER_IMAGE));
			offerName1.add(temp1.get(KEY_OFFER_NAME));
			offer_id.add(temp1.get(KEY_OFFER_ID));
			
		}
		String[] imagePath = new String[imageUrl.size()];
		for (int i = 0; i < imageUrl.size(); i++) {
			imagePath[i] = GlobalVariables.getOfferImagePath() + imageUrl.get(i);
			
		}
		if (convertView == null)
			v = inflater.inflate(R.layout.item1, null);
		
		String[] name = new String[offerName1.size()];
		for (int i = 0; i < offerName1.size(); i++) {
			name[i] = offerName1.get(i);
		}
		String[] id = new String[offer_id.size()];
		for (int i = 0; i < offer_id.size(); i++) {
			id[i] = offer_id.get(i);
			
		}
		ImageView offerImage = (ImageView) v.findViewById(R.id.brand_image);
		TextView offerName = (TextView) v.findViewById(R.id.brand_name);
		offerName.setText(name[position]);
		v.setOnTouchListener(offerTouchListener);
		v.setTag(position);
		
		
		imageLoader.DisplayImage(imagePath[position], offerImage);
		return v;
	}
	
	OnTouchListener offerTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				Dialog d = offerDetails(v);

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
	private Dialog dialog;

	public Dialog offerDetails(View v) {
		// TODO Auto-generated method stub
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.offer_details_dialog);
		//dialog.getWindow().setLayout(400, 400);
		ImageView offer_image = (ImageView)dialog.findViewById(R.id.offer_image);
		TextView offer_name = (TextView) dialog.findViewById(R.id.offer_name);
		TextView offer_details = (TextView) dialog.findViewById(R.id.offer_details);
		int position =  Integer.parseInt(v.getTag().toString());
		offer_name.setText(filePath.get(position).get(KEY_OFFER_NAME));
		offer_details.setText(filePath.get(position).get(KEY_OFFER_DETAILS));
		imageLoader.DisplayImage(GlobalVariables.getOfferImagePath() + filePath.get(position).get(KEY_OFFER_IMAGE), offer_image);
		
		return dialog;
	}

}
