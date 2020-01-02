package com.nagainfomob.smartShowroom;

import java.util.ArrayList;
import java.util.HashMap;

import com.nagainfomob.update.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ViewPatternAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, String>> filePath;
	private Activity activity;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public ViewPatternAdapter(Activity a,
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
		
		return null;
	}

}
