package com.nagainfo.update;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;

public class CustomeGridViewTileAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> dbResult;

	public CustomeGridViewTileAdapter(Activity activity,
			ArrayList<HashMap<String, String>> dbResult) {
		this.activity = activity;
		this.dbResult = dbResult;

	}
	
}
