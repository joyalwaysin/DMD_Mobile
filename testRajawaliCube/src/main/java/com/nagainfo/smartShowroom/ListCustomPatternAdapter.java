package com.nagainfomob.smartShowroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class ListCustomPatternAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> dbresult;
	private LayoutInflater inflater;
	List<String> patName = new ArrayList<String>();
	List<String> patPath = new ArrayList<String>();
	// private static final String PAT_ID = "pattern_id";
	private static final String PAT_NAME = "pattern_name";
	private static final String PAT_PATH = "pattern_path";

	public ListCustomPatternAdapter(Activity a,
			ArrayList<HashMap<String, String>> result) {
		activity = a;
		dbresult = result;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dbresult.size();
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
		TextView nameTextView;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		patName.clear();
		patPath.clear();
		ViewHolder holder;
		for (int i = 0; i < dbresult.size(); i++) {
			Map<String, String> temp1 = dbresult.get(i);
			patName.add(temp1.get(PAT_NAME));
			patPath.add(temp1.get(PAT_PATH));
		}
		String[] pat_Name = new String[patName.size()];
		for (int i = 0; i < patName.size(); i++) {
			pat_Name[i] = patName.get(i);
		}

		String[] pat_Path = new String[patPath.size()];
		for (int i = 0; i < patPath.size(); i++) {
			pat_Path[i] = patPath.get(i);
		}

		View v1 = convertView;
		if (v1 == null) {
			holder = new ViewHolder();
			v1 = inflater.inflate(R.layout.list_item, null);
			holder.nameTextView = (TextView) v1.findViewById(R.id.item_text);
			v1.setTag(holder);
		} else {
			holder = (ViewHolder) v1.getTag();
		}
		holder.nameTextView.setText(pat_Name[position].replace(".xml", "").toUpperCase());
//		holder.nameTextView.setTextColor(R.color.white);  

		v1.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {

					//int position = Integer.parseInt(v.getTag().toString());
					String path = patPath.get(position);
					String name = patName.get(position);
					Intent intent = new Intent(activity.getApplicationContext(),
							CustomPatternActivity.class);
					intent.putExtra("name", name);
					intent.putExtra("path", path);
					activity.startActivity(intent);

					return false;
				}
				return true;
			}
		});
		//v1.setTag(position + "");
		return v1;
	}

	
}
