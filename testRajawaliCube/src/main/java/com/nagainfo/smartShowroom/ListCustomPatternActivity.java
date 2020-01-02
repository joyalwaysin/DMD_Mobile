package com.nagainfomob.smartShowroom;

import java.util.ArrayList;
import java.util.HashMap;

import com.nagainfomob.database.DatabaseHandler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

public class ListCustomPatternActivity extends Activity {

	Activity context;
	private ArrayList<HashMap<String, String>> dbResult;
	private ListView lView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listcustomepattern_layout);
		this.context = this;
		intializeValues();
		loadCustomPatter();

	}

	public void loadCustomPatter() {
		// TODO Auto-generated method stub
		DatabaseHandler db = new DatabaseHandler(context);
		dbResult = db.getCustomePattern();
		ListCustomPatternAdapter lAdapter = new ListCustomPatternAdapter(context,
				dbResult);
		lView.setAdapter(lAdapter);

	}

	private void intializeValues() {
		lView = (ListView) findViewById(R.id.custom_pat_list);
	}

}
