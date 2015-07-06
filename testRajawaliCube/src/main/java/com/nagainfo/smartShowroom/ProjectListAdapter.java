package com.nagainfo.smartShowroom;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;

import com.nagainfo.database.DatabaseHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ProjectListAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> result;
	private LayoutInflater inflater;
	private static final String PROJ_NAME = "proj_name";
	public LoadprojectListener delegate = null;

	public ProjectListAdapter(Activity activity,
			ArrayList<HashMap<String, String>> result, LoadProject lp) {
		this.activity = activity;
		this.result = result;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.delegate = lp;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return result.size();
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
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		View v = convertView;
		// v.invalidate();
		if (convertView == null) {
			holder = new ViewHolder();
			v = inflater.inflate(R.layout.list_item, null);
			holder.tItem = (TextView) v.findViewById(R.id.item_text);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		holder.tItem.setText(result.get(position).get(PROJ_NAME));
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selProject(position);
			}
		});
		v.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				deletePrompt(v, position);
				return false;
			}
		});

		v.setTag(holder);
		return v;
	}

	public void selProject(int i) {
		// TODO Auto-generated method stub
		this.delegate.selectedProject(result, i);
	}

	/*OnLongClickListener deleteProjectListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			int pro_id = Integer.valueOf(v.getTag().toString());
			deletePrompt(v, pro_id);
			return false;
		}
	};*/
	private int deleteproId;
	private Dialog dialog;

	public void deletePrompt(View v, int pro_id) {
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.delete_prompt);

		deleteproId = pro_id;
		Button delete = (Button) dialog.findViewById(R.id.but_delete);
		Button cancel = (Button) dialog.findViewById(R.id.but_cancel);
		delete.setOnClickListener(deleteOkListener);
		cancel.setOnClickListener(cancelButListener);
		dialog.show();

	}

	OnClickListener deleteOkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			DatabaseHandler db = new DatabaseHandler(activity);
			db.deleteProject(result.get(deleteproId).get(PROJ_NAME));
			dialog.cancel();
			activity.recreate();

		}
	};
	OnClickListener cancelButListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog.cancel();
		}
	};

}

