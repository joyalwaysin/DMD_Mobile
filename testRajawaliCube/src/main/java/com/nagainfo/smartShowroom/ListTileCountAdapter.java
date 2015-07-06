package com.nagainfo.smartShowroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nagainfo.smartShowroom.ProjectListAdapter.ViewHolder;
import com.nagainfo.update.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListTileCountAdapter extends BaseAdapter {
	Activity activity;
	LinkedHashMap<String, Integer> tList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private LinkedHashMap<String, Integer> map;

	// ArrayList<HashMap<String,Integer>> list;

	public ListTileCountAdapter(Activity a, LinkedHashMap<String, Integer> tList) {
		this.activity = a;
		this.tList = tList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tList.size();
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
		TextView tItem;
		ImageView iView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		View v = convertView;
		if (convertView == null) {
			holder = new ViewHolder();
			v = inflater.inflate(R.layout.tilelist_layout, null);
			holder.tItem = (TextView) v.findViewById(R.id.tileImageCount);
			holder.iView = (ImageView) v.findViewById(R.id.tileImageview);
			v.setTag(holder);

			// for (HashMap.Entry<String, Integer> entry : tList.entrySet()) {
			// imageLoader.DisplayImage(entry.getKey(), iView);

		} else {
			holder = (ViewHolder) v.getTag();
		}
		String key = (new ArrayList<String>(tList.keySet()).get(position));
		Integer value = (new ArrayList<Integer>(tList.values()).get(position));
		Bitmap bmp = BitmapFactory.decodeFile(key);

		holder.iView.setImageBitmap(bmp);
		holder.tItem.setText(value + " tiles");
		// }
		v.setTag(holder);
		return v;
	}

}
