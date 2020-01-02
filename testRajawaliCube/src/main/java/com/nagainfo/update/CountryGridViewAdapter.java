package com.nagainfomob.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.R;
import com.nagainfomob.smartShowroom.R.id;
import com.nagainfomob.smartShowroom.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CountryGridViewAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> countryList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	private static final String KEY_ITEM = "data";
	private static final String KEY_COUNTRY_NAME = "country_name";
	private static final String KEY_COUNTRY_CODE = "country_code";
	private static final String KEY_COUNTRY_IMAGE = "country_image";

	public CountryGridViewAdapter(Activity a,
			ArrayList<HashMap<String, String>> countryList) {
		// TODO Auto-generated constructor stub
		activity = a;
		this.countryList = countryList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return countryList.size();
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

	class ViewHolder {
		ImageView cImage;
		TextView cText;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		View v1 = convertView;
		List<String> imageUrl = new ArrayList<String>();
		List<String> companyName = new ArrayList<String>();
		String[] name = new String[getlistArray(countryList, KEY_COUNTRY_NAME)
				.size()];
		String[] image_country = new String[getlistArray(countryList,
				KEY_COUNTRY_NAME).size()];
		String[] id_country = new String[getlistArray(countryList,
				KEY_COUNTRY_NAME).size()];
		if (convertView == null) {
			holder = new ViewHolder();
			v1 = inflater.inflate(layout.item1, null);
			holder.cImage = (ImageView) v1.findViewById(id.brand_image);
			holder.cText = (TextView) v1.findViewById(id.brand_name);
			v1.setTag(holder);

		} else {
			holder = (ViewHolder) v1.getTag();
		}
		for (int i = 0; i < getlistArray(countryList, KEY_COUNTRY_NAME).size(); i++) {
			name[i] = getlistArray(countryList, KEY_COUNTRY_NAME).get(i)
					.toString();

			image_country[i] = GlobalVariables.getCountryFlagPath()
					+ getlistArray(countryList, KEY_COUNTRY_IMAGE).get(i);
			id_country[i] = getlistArray(countryList, KEY_COUNTRY_CODE).get(i);

		}
		imageLoader.DisplayImage(image_country[position], holder.cImage);
		holder.cText.setText(name[position]);
		v1.refreshDrawableState();
		v1.setTag(holder);
		v1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getlistArray(countryList, KEY_COUNTRY_CODE).size()>0)
				{
				Intent i = new Intent(activity, ActivityCompany.class);
				i.putExtra("CountryID", getlistArray(countryList, KEY_COUNTRY_CODE)
						.get(position));
				activity.startActivity(i);
				}
				else
				{
					Toast.makeText(activity.getApplicationContext(), "No Country Found", Toast.LENGTH_SHORT).show();

				}
			}
		});
		return v1;

	}

	private List<String> getlistArray(
			ArrayList<HashMap<String, String>> companyList, String KEY) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < companyList.size(); i++) {
			Map<String, String> temp1 = companyList.get(i);
			result.add(temp1.get(KEY));
		}
		return result;

	}

	OnClickListener companyClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String pos = v.getTag().toString();
			Intent i = new Intent(activity, ActivityCompany.class);
			i.putExtra("CountryID", pos);
			activity.startActivity(i);

		}
	};

}
