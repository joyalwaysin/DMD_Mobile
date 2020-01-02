package com.nagainfomob.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nagainfomob.smartShowroom.GlobalVariables;
import com.nagainfomob.smartShowroom.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CompanyGridViewAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> companyList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	private static final String KEY_ITEM = "data";
	private static final String KEY_COMPANY_ID = "company_id";
	private static final String KEY_COMPANY_NAME = "company_name";
	private static final String KEY_COMPANY_IMAGE = "company_image";

	public CompanyGridViewAdapter(Activity a,
			ArrayList<HashMap<String, String>> compantList) {
		// TODO Auto-generated constructor stub
		activity = a;
		companyList = compantList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return companyList.size();
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
		TextView cName;
		ImageView cImage;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		View v1 = convertView;
		List<String> imageUrl = new ArrayList<String>();
		List<String> companyName = new ArrayList<String>();
		String[] name = new String[getlistArray(companyList, KEY_COMPANY_NAME)
				.size()];
		String[] image_company = new String[getlistArray(companyList,
				KEY_COMPANY_NAME).size()];
		String[] id_company = new String[getlistArray(companyList,
				KEY_COMPANY_NAME).size()];
		if (convertView == null) {

			holder = new ViewHolder();
			v1 = inflater.inflate(R.layout.item1, null);
			holder.cImage = (ImageView) v1.findViewById(R.id.brand_image);
			holder.cName = (TextView) v1.findViewById(R.id.brand_name);
			v1.setTag(holder);

		} else {
			holder = (ViewHolder) v1.getTag();
		}

		for (int i = 0; i < getlistArray(companyList, KEY_COMPANY_NAME).size(); i++) {
			name[i] = getlistArray(companyList, KEY_COMPANY_NAME).get(i)
					.toString();

			image_company[i] = GlobalVariables.getCompanyImagePath()
					+ getlistArray(companyList, KEY_COMPANY_IMAGE).get(i);
			id_company[i] = getlistArray(companyList, KEY_COMPANY_ID).get(i);

		}
		imageLoader.DisplayImage(image_company[position], holder.cImage);
		holder.cName.setText(name[position]);
		v1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getlistArray(companyList, KEY_COMPANY_NAME).size()>0)
				{
				Intent i = new Intent(activity, ActivityUpdate.class);
				i.putExtra("brandId", getlistArray(companyList, KEY_COMPANY_ID)
						.get(position));
				i.putExtra(
						"compnayName",
						getlistArray(companyList, KEY_COMPANY_NAME).get(
								position));
				activity.startActivity(i);
				}
				else
				{
					Toast.makeText(activity.getApplicationContext(), "Brands Coming Soon!", Toast.LENGTH_SHORT).show();
				}

			}
		});

		v1.refreshDrawableState();
		v1.setTag(holder);
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
			Intent i = new Intent(activity, ActivityUpdate.class);
			i.putExtra("brandId", pos);

			activity.startActivity(i);

		}
	};

}
