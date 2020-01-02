package com.nagainfomob.smartShowroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.youtube.player.internal.l;
import com.nagainfomob.database.DatabaseHandler;
import com.nagainfomob.slider.PatternGridAdapter;
import com.nagainfomob.smartShowroom.R;
import com.nagainfomob.smartShowroom.R.array;
import com.nagainfomob.smartShowroom.R.id;
import com.nagainfomob.smartShowroom.R.layout;
import com.nagainfomob.smartShowroom.R.menu;
import com.nagainfomob.update.PatternimgNameInterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CustomPatternActivity extends Activity implements
		PatternimgNameInterface,OnClickListener {
	private LinearLayout content_slider;
	View patternContent,patternContent2;
	Boolean isDrawerOpen = false;
	Boolean isOpening = false;
	Boolean shouldExit = false;
	float tLength, tWidth;
	int displayWidth;
	private Activity activity;
	String selectedUnit;
	DrawView drawView;
	String patternName;
	private boolean drawToolSelected;
	LinearLayout patternScrollView;
	int axis = 0;
	GridView gView;
	private static final String KEY_BRAND_NAME = "pro_brand";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_COMPANY = "pro_company";
	List<Integer> selectedBrands = new ArrayList<Integer>();
	List<Integer> selectedSize = new ArrayList<Integer>();
	List<Integer> selectedColor = new ArrayList<Integer>();
	List<Integer> selectedType = new ArrayList<Integer>();
	List<Integer> selectedCompany = new ArrayList<Integer>();

	private Button filterSearchButton2;
	private LayoutInflater inflater;
	String patternPath;
	String tileSize;
	private boolean isTileLoaded = false;
	private boolean isSavedAs = false;
	private int viewArea ;//= GlobalVariables.getDrawArea(getApplicationContext());
	private float rotation,layoutRotation;

	public CustomPatternActivity() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.custom_pattern_layout);
		this. viewArea = GlobalVariables.getDrawArea(getApplicationContext());
		drawView = new DrawView(this, patternName);
		drawView.setLayoutParams(new LayoutParams(viewArea, viewArea));
		drawView.setMode(true);
		drawView.setBackgroundColor(Color.WHITE);
		activity = this;
		
		intialize();

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				viewArea, viewArea);
		// params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
		// params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		params.leftMargin = 100;
		params.topMargin = 30;

		RelativeLayout rel = (RelativeLayout) findViewById(id.main_content);
		rel.addView(drawView);
		rel.setLayoutParams(params);

		patternName = getIntent().getStringExtra("name");
		patternPath = getIntent().getStringExtra("path");
		if (patternName != null && patternPath != null) {

			File patFile = new File(patternPath + patternName);
			if (patFile.exists()) {

				GlobalVariables.setUnit("Feet");
				loadPattern(patternPath + patternName);
				isTileLoaded = true;

			} else {

			}
		} else {

			customTilesDialog();
		}

	}

	public void mainLayoutPressed(View v) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.custom_pattern_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case id.tileInfo:
			getWallInfo();
			break;
		case id.saveAs:
			showSaveAsDialog();
			break;
		case id.actionDone:
			donePressed();
			finish();
			break;
		case id.custome_slider:
			addSlider();
			break;
		case id.actionPen:
			drawToolSelected = true;
			drawView.isDrawingSelected(drawToolSelected);
			drawView.selectTile(false);
			// Toast.makeText(getApplicationContext(), "pen selected",
			// Toast.LENGTH_SHORT).show();
			break;
		// action with ID action_settings was selected
		case id.actionHome:
			Intent i = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(i);
			finish();

			break;
		case id.actionPointer:

			drawToolSelected = false;
			drawView.isDrawingSelected(drawToolSelected);
			drawView.selectTile(false);
			// Toast.makeText(getApplicationContext(), "pointer selected",
			// Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

		return true;
	}

	void saveTileDetails() {
		RelativeLayout rel = (RelativeLayout) findViewById(id.main_content);
		rel.setDrawingCacheEnabled(true);
		rel.buildDrawingCache();
		Bitmap bitmap = rel.getDrawingCache();

		// String prjPath=
		// Environment.getExternalStorageDirectory().getAbsolutePath() +
		// "/SmartShowRoom/Custom Pattern/";

		String path;
		File patFile = new File(patternName);
		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/Custom Pattern/";

		String saveName;

		saveName = patFile.getName();

		boolean success = drawView.savelayout(rel, saveName, path);
		if (success) {
			saveTile(bitmap, (!isTileLoaded || isSavedAs));
			if (!isTileLoaded || isSavedAs) {
				if (isSavedAs)
					isSavedAs = false;
				final String xmlFile = path + saveName.replace(".xml", "")
						+ ".xml";
				DatabaseHandler db = new DatabaseHandler(
						getApplicationContext());
				db.insertCustomPattern(saveName.replace(".xml", "") + ".xml",
						path);
			}
		}
	}

	void donePressed() {
		saveTileDetails();
	}

	private class ViewHolder {

		ImageView leftPattern, rightPattern;
	}

	public void applyFilter() {
//		patternScrollView.removeAllViews();
//		selectedCompany.clear();
//		selectedBrands.clear();
//		selectedSize.clear();
//		selectedColor.clear();
//		selectedType.clear();
//		 loadTile();
		String cm_name, br_name, pro_size, pro_color, pro_type;
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> dbResult3 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> dbResult4 = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> filterResult = new ArrayList<HashMap<String, String>>();
		// String brand, size, color, type;
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
		DatabaseHandler db2 = new DatabaseHandler(getApplicationContext());
		DatabaseHandler db3 = new DatabaseHandler(getApplicationContext());
		DatabaseHandler db4 = new DatabaseHandler(getApplicationContext());
		dbResult = db.getAllCompany();
		dbResult1 = db1.getAllBrand();
		dbResult2 = db2.getDistinctSize();
		dbResult3 = db3.getDistinctColor();
		dbResult4 = db4.getDistinctType();
		cm_name = getStringFromCheckedList(dbResult, selectedCompany,
				KEY_COMPANY);
		br_name = getStringFromCheckedList(dbResult1, selectedBrands,
				KEY_BRAND_NAME);
		pro_size = getStringFromCheckedList(dbResult2, selectedSize, KEY_DIMEN);
		pro_color = getStringFromCheckedList(dbResult3, selectedColor,
				KEY_COLOR);
		pro_type = getStringFromCheckedList(dbResult4, selectedType, KEY_TYPE);
		DatabaseHandler db5 = new DatabaseHandler(getApplicationContext());
		filterResult = db5.getResultByFilter(cm_name, br_name, pro_size,
				pro_color, pro_type);
		if (filterResult.size() == 0) {
			Toast.makeText(getApplicationContext(), "No tiles found",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (filterResult.size() > 0) {
			selectedCompany.clear();
			selectedBrands.clear();
			selectedSize.clear();
			selectedColor.clear();
			selectedType.clear();
			
//			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//			patternContent2 = inflater.inflate(R.layout.layout_patters,null);
			
			gView = (GridView) patternContent2.findViewById(id.slider_pattern);
			patternScrollView.removeAllViews();
			
			patternScrollView.addView(patternContent2);
			showPattern(filterResult);
		}

//		System.out.println("test");

	}

	public void showPattern(ArrayList<HashMap<String, String>> dbResult) {

		PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
				CustomPatternActivity.this);
		gView.refreshDrawableState();
		pgAdapter.notifyDataSetChanged();
		// grid.setAdapter(adapter);
		gView.setAdapter(pgAdapter);

	}

	public void filterSelected() {
		patternScrollView.removeAllViews();
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//		View patternContent = inflater.inflate(R.layout.layout_filter, null);
//		selectedCompany.clear();
//		selectedBrands.clear();
//		selectedSize.clear();
//		selectedColor.clear();
//		selectedType.clear();
		
		addCompanyCheckBox(patternContent);
		addbrandCheckBox(patternContent);
		addSizeCheckBox(patternContent);
		addColorCheckBox(patternContent);
		addTypeCheckBox(patternContent);
		patternScrollView.addView(patternContent);

	}

	public void addbrandCheckBox(View patternContent) {
		View v = patternContent;
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(id.brandCheckBox);
//		ll.addView(null);
		ll.removeAllViews();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult1 = db.getAllBrand();
		List<String> brandNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult1.size(); i++) {
			Map<String, String> temp1 = dbResult1.get(i);
			brandNameList.add(temp1.get(KEY_BRAND_NAME));
		}

		for (int i = 0; i < brandNameList.size(); i++) {

			CheckBox cb = new CheckBox(getApplicationContext());
			cb.setText(brandNameList.get(i));
			cb.setTextColor(Color.BLACK);
			cb.setTag(i);
//			cb.setChecked(false);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
				
					if (isChecked) {
//						try {
							int t = (Integer) buttonView.getTag();
							selectedBrands.add(new Integer(t));
							
//						} catch (Exception e) {
//							Log.e("brand checked item error", "");
//							e.printStackTrace();
//						}

					} 
//					else if(!isChecked)
//					{
//						int t = (Integer) buttonView.getTag();
//						
//					}
					
					else {
						selectedBrands.clear();
					}
					if(!isChecked)
					{
						int t = (Integer) buttonView.getTag();
//						buttonView.setChecked(false);
						selectedBrands.remove(new Integer(t));	
					}
					
				}
			});
			ll.addView(cb);
		}

	}

	public void addCompanyCheckBox(View patternContent) {
		View v = patternContent;
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(id.companyCheckBox);
//		ll.addView(null);
		ll.removeAllViews();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult1 = db.getAllCompany();
		List<String> brandNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult1.size(); i++) {
			Map<String, String> temp1 = dbResult1.get(i);
			if (!temp1.get(KEY_COMPANY).equalsIgnoreCase(""))
				brandNameList.add(temp1.get(KEY_COMPANY));
		}

		for (int i = 0; i < brandNameList.size(); i++) {

			CheckBox cb = new CheckBox(getApplicationContext());
			cb.setText(brandNameList.get(i));
			cb.setTextColor(Color.BLACK);
//			cb.setChecked(false);
			cb.setTag(i);
//			int a=i;
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
//						try {
							
							int t = (Integer) buttonView.getTag();
							selectedCompany.add(new Integer(t));
//						} catch (Exception e) {
//							Log.e("company checked item error", "");
//							e.printStackTrace();
//						}
						
					}
					
					else {
						selectedCompany.clear();
					}
					
					if(!isChecked)
					{
						int t = (Integer) buttonView.getTag();
//						buttonView.setChecked(false);
						selectedCompany.remove(new Integer(t));
					}

				}
				
			});
			
			ll.addView(cb);
		}

	}

	OnCheckedChangeListener companyCheckListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
//				try {
					
					int t = (Integer) buttonView.getTag();
					selectedCompany.add(new Integer(t));
//				} catch (Exception e) {
//					Log.e("company checked item error", "");
//					e.printStackTrace();
//				}
				
			}
			
			else {
				selectedCompany.clear();
			}
			if(!isChecked)
			{
				int t = (Integer) buttonView.getTag();
				selectedCompany.remove(new Integer(t));
			}

		}

	};

	
	
	OnCheckedChangeListener brandCheckListener = new OnCheckedChangeListener() {

		@SuppressLint("UseValueOf")
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
//				try {
					int t = (Integer) buttonView.getTag();
					selectedBrands.add(new Integer(t));
					
//				} catch (Exception e) {
//					Log.e("brand checked item error", "");
//					e.printStackTrace();
//				}

			} 
//			else if(!isChecked)
//			{
//				int t = (Integer) buttonView.getTag();
//				
//			}
			
			else {
				selectedBrands.clear();
			}
			if(!isChecked)
			{
				int t = (Integer) buttonView.getTag();
				selectedBrands.remove(new Integer(t));	
			}
		}

	};

	public void addSizeCheckBox(View v) {
		LinearLayout l = (LinearLayout) v.findViewById(id.sizeCheckbox);
//		l.addView(null);
		l.removeAllViews();
		ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult2 = db.getDistinctSize();
		List<String> brandNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult2.size(); i++) {
			Map<String, String> temp1 = dbResult2.get(i);
			if (!temp1.get(KEY_DIMEN).equalsIgnoreCase(""))
				brandNameList.add(temp1.get(KEY_DIMEN));
		}

		for (int i = 0; i < brandNameList.size(); i++) {

			CheckBox cb = new CheckBox(getApplicationContext());
			cb.setText(brandNameList.get(i));
			cb.setTextColor(Color.BLACK);
			cb.setTag(i);
			cb.setOnCheckedChangeListener(sizeChangeListener);
			l.addView(cb);
		}

	}

	OnCheckedChangeListener sizeChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedSize.add(new Integer(new Integer(t)));
			} else {
				selectedSize.clear();
			}
			if(!isChecked)
			{
				int t = (Integer) buttonView.getTag();
				selectedSize.remove(new Integer(t));
			}

		}
	};

	public void addColorCheckBox(View v) {
		LinearLayout l = (LinearLayout) v.findViewById(id.colorCheckBox);
//		l.addView(null);
		l.removeAllViews();
		ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult2 = db.getDistinctColor();
		List<String> brandNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult2.size(); i++) {
			Map<String, String> temp1 = dbResult2.get(i);
			if (!temp1.get(KEY_COLOR).equalsIgnoreCase(""))
				brandNameList.add(temp1.get(KEY_COLOR));
		}

		for (int i = 0; i < brandNameList.size(); i++) {

			CheckBox cb = new CheckBox(getApplicationContext());
			cb.setText(brandNameList.get(i));
			cb.setTextColor(Color.BLACK);
			cb.setTag(i);
			cb.setOnCheckedChangeListener(colorChangeListener);
			l.addView(cb);
		}

	}

	OnCheckedChangeListener colorChangeListener = new OnCheckedChangeListener() {

		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedColor.add(new Integer(new Integer(t)));

			} else {
				selectedColor.clear();
			}
			if(!isChecked)
			{
				int t = (Integer) buttonView.getTag();
				selectedColor.remove(new Integer(new Integer(t)));
			}


		}
	};

	public void addTypeCheckBox(View v) {
		LinearLayout l = (LinearLayout) v.findViewById(id.typeCheckBox);
//		l.addView(null);
		l.removeAllViews();
		ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult2 = db.getDistinctType();
		List<String> brandNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult2.size(); i++) {
			Map<String, String> temp1 = dbResult2.get(i);
			if (!temp1.get(KEY_TYPE).equalsIgnoreCase(""))
				brandNameList.add(temp1.get(KEY_TYPE));
		}

		for (int i = 0; i < brandNameList.size(); i++) {

			CheckBox cb = new CheckBox(getApplicationContext());
			cb.setText(brandNameList.get(i));
			cb.setTextColor(Color.BLACK);
			cb.setTag(i);
			cb.setOnCheckedChangeListener(typeChangeListener);
			l.addView(cb);
		}

	}

	OnCheckedChangeListener typeChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedType.add(new Integer(t));

			} else {
				selectedType.clear();
			}
			if(!isChecked)
			{
				int t = (Integer) buttonView.getTag();
				selectedType.remove(new Integer(t));
			}
			


		}
	};
	private EditText eText;

	void loadTile() {
		// loadPatterHeader();
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View menuLayout = inflater.inflate(R.layout.layout_patters,
		// patternScrollView, false);
//		patternContent = inflater.inflate(R.layout.layout_patters, null);
		patternScrollView.removeAllViews();
		gView = (GridView) patternContent2.findViewById(id.slider_pattern);
//		eText = (EditText) patternContent.findViewById(R.id.textSearch);
		patternScrollView.addView(patternContent2);
		getAllpattern();

	}

	public void getAllpattern() {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		int countRec = db.getCount();
		dbResult = db.getAllPattern();

		PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,CustomPatternActivity.this);
		gView.refreshDrawableState();
		pgAdapter.notifyDataSetChanged();
		// grid.setAdapter(adapter);
		gView.setAdapter(pgAdapter);

	}

	public String getStringFromCheckedList(
			ArrayList<HashMap<String, String>> dbResult,
			List<Integer> selectedBrands2, String ketItem) {
		List<String> itemString = new ArrayList<String>();
		String resString = "";
		if (selectedBrands2.size() == 0) {
			return null;
		}
		for (int i = 0; i < dbResult.size(); i++) {

			itemString.add(dbResult.get(i).get(ketItem));
		}
		if (selectedBrands2.size() == 1) {
			try {
				int index = selectedBrands2.get(0);
				resString = "'" + itemString.get(index) + "'";
			} catch (Exception ex) {
				Log.e("error", ex.getMessage());

			}
		} else {
			resString = "'" + itemString.get(selectedBrands2.get(0)) + "'";
			for (int i = 1; i < itemString.size(); i++) {
				resString += ",'" + itemString.get(selectedBrands2.indexOf(i))
						+ "'";
			}
		}
		return resString;

	}

	void removeTile() {
		patternScrollView.removeAllViews();
	}

	void setIsOpening(Boolean value) {
		this.isOpening = value;
	}

	Boolean getIsOpening() {
		return this.isOpening;
	}

	void setIsDrawerOpen() {
		this.isDrawerOpen = !this.isDrawerOpen;
	}

	Boolean getIsDrawerOpen() {
		return this.isDrawerOpen;
	}

	public void addSlider() {
		if (!isOpening) {

			Animation animation;
			if (!isDrawerOpen) {
				animation = new TranslateAnimation(displayWidth, 0, 0, 0);

			} else {
				animation = new TranslateAnimation(0, displayWidth, 0, 0);
			}

			animation.setDuration(500);
			try {
				animation.setAnimationListener(slideLeftRightAnimationListener);
			} catch (Exception e) {
				Log.e("animation error", e.getMessage());
			}

			content_slider.setVisibility(View.VISIBLE);

			// content_slider.getBackground().setAlpha(1000);

			content_slider.setAnimation(animation);
			content_slider.startAnimation(animation);
			content_slider.requestLayout();
		}

	}

	private AnimationListener slideLeftRightAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			setIsOpening(true);
			content_slider.bringToFront();
			if (!getIsDrawerOpen()) {
				loadTile();
			}

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (!getIsDrawerOpen()) {
				final int left = content_slider.getLeft();
				final int top = content_slider.getTop();
				final int right = content_slider.getRight();
				final int bottom = content_slider.getBottom();
				content_slider.layout(left, top, right, bottom);
			} else {
				final int left = displayWidth;
				final int top = content_slider.getTop();
				final int right = content_slider.getRight();
				final int bottom = content_slider.getBottom();
				content_slider.layout(left, top, right, bottom);
				content_slider.setVisibility(View.GONE);
				removeTile();
			}
			setIsOpening(false);
			setIsDrawerOpen();
		}
	};
	private Button filterSearch_button;
	private Button search;
	
	
	@SuppressLint("NewApi")
	void intialize() {
		isDrawerOpen = false;
		isSavedAs = false;
		isOpening = false;
		patternScrollView = (LinearLayout) findViewById(id.custome_patternScrollView);
		content_slider = (LinearLayout) findViewById(id.custome_content_slider);
		// twod_layout = (LinearLayout) findViewById(R.id.twod_layout);
		// buttonLayout = (LinearLayout) findViewById(R.id.leftButtonLayout);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//
		//
		patternContent2 = inflater.inflate(layout.layout_patters, null);

		// private EditText eText;
		eText = (EditText) patternContent2.findViewById(id.textSearch);
		// eText.setText(null);
		filterSearch_button = (Button) patternContent2
				.findViewById(id.filterButton);
		filterSearch_button.setOnClickListener(CustomPatternActivity.this);

		search = (Button) patternContent2
				.findViewById(id.searchButton);

		search.setOnClickListener(CustomPatternActivity.this);
		//
		//
		patternContent = inflater.inflate(layout.layout_filter, null);
		filterSearchButton2 = (Button) patternContent
				.findViewById(id.filterButton2);

		filterSearchButton2.setOnClickListener(CustomPatternActivity.this);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		displayWidth = size.x;
		final int left = displayWidth;
		final int top = content_slider.getTop();
		final int right = content_slider.getRight();
		final int bottom = content_slider.getBottom();
		content_slider.layout(left, top, right, bottom);
		String[] unitArray = getResources().getStringArray(
				array.unitSpinnerArray);
		selectedUnit = unitArray[0];
		// twod_layout.bringToFront();
	}

	void customTilesDialog() {
		final Dialog dialog = new Dialog(CustomPatternActivity.this);
		dialog.setCancelable(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(layout.cropactivity_dialog);
		String[] unitArray = { "Millimeters" };
		Button cancel, ok;
		Spinner unitSpinner;
		unitSpinner = (Spinner) dialog.findViewById(id.Dialog_unitSpinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, unitArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitSpinner.setAdapter(spinnerArrayAdapter);
		// unitSpinner.setOnItemSelectedListener(unitSpinnerListener);
		unitSpinner.setClickable(false);
		cancel = (Button) dialog.findViewById(id.crop_cancel);
		ok = (Button) dialog.findViewById(id.crop_okay);
		final EditText dimenA = (EditText) dialog.findViewById(id.dimenA);
		final EditText dimenB = (EditText) dialog.findViewById(id.dimenB);
		final EditText patternNameText = (EditText) dialog
				.findViewById(id.patternNameField);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity.finish();
				dialog.dismiss();

			}
		});
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// String[] unitArray = getResources().getStringArray(
				// R.array.unitSpinnerArray);
				// selectedUnit = unitArray[0];
				if (patternNameText.getText().toString().length() <= 0
						|| dimenA.getText().length() <= 0
						|| dimenB.getText().length() <= 0) {
					Toast.makeText(getApplicationContext(),
							"Required fields missing!", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				tLength = Float.valueOf(dimenA.getText().toString());
				tWidth = Float.valueOf(dimenB.getText().toString());
				drawView.setDimensions(selectedUnit, tLength, tWidth);

				float ratio = tWidth / tLength;
				float rlength, rwidth;
				if (ratio > 1) {
					ratio = 1 / ratio;
					rlength = (ratio * viewArea);
					rwidth = viewArea;
				} else {
					rwidth = (ratio * viewArea);
					rlength = viewArea;
				}

				patternName = patternNameText.getText().toString();

				LayoutParams params = drawView.getLayoutParams();
				params.height = (int) rlength;
				params.width = (int) rwidth;
				drawView.requestLayout();
				RelativeLayout rel = (RelativeLayout) findViewById(id.main_content);

				// drawView.setLayoutParams(new LayoutParams(rlength,rwidth));
				FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) rel
						.getLayoutParams();
				param.height = (int) rlength;
				param.width = (int) rwidth;
				rel.requestLayout();
				dialog.dismiss();
			}
		});
		dialog.show();

	}

	OnItemSelectedListener unitSpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			String[] unitArray = getResources().getStringArray(
					array.unitSpinnerArray);
			selectedUnit = unitArray[arg2];
			GlobalVariables.setUnit(selectedUnit);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private String saveTile(Bitmap bmp, boolean isLoaded) {
		String path;
		String currentProject = GlobalVariables.getProjectName();

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/Custom Pattern/";
		File directoryPath = new File(path);
		if (!directoryPath.exists()) {
			directoryPath.mkdirs();
			GlobalVariables.createNomediafile(path);
		}
		File patFile = new File(patternName);
		String filePath = path + patFile.getName().replace(".xml", "") + ".jpg";
		try {
			File file = new File(filePath);
			FileOutputStream fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			/*
			 * if (selectedUnit.equalsIgnoreCase("feet")) {
			 * 
			 * dimW = GlobalVariables.feetToMm(Integer.valueOf(String
			 * .valueOf((int) tWidth))); dimH =
			 * GlobalVariables.feetToMm(Integer.valueOf(String .valueOf((int)
			 * tLength)));
			 * 
			 * } else if (selectedUnit.equalsIgnoreCase("inches")) { dimW =
			 * GlobalVariables.inchesToMm(Integer.valueOf(String .valueOf((int)
			 * tWidth))); dimH =
			 * GlobalVariables.inchesToMm(Integer.valueOf(String .valueOf((int)
			 * tLength))); } else { dimW =
			 * GlobalVariables.metersToMm(Integer.valueOf(String .valueOf((int)
			 * tWidth))); dimH =
			 * GlobalVariables.metersToMm(Integer.valueOf(String .valueOf((int)
			 * tLength))); }
			 */
			if (isLoaded) {
				db.insertRecord(patFile.getName().replace(".xml", ""), "Custom",
						String.format("%.0f",tWidth) + "x" + String.format("%.0f",tLength), "Custom", "Custom",
						"Custom", "Custom Pattern", "Custom");
			}
			// db.insertCustomPattern(patternName, filePath);
			fOut.flush();
			fOut.close();
			Toast.makeText(getApplicationContext(), "Pattern has been saved!",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(null, "Save file error!");

		}
		// Toast.makeText(getApplicationContext(), "asd",
		// Toast.LENGTH_SHORT).show();
		return filePath;
	}

	Bitmap bmp;

	@Override
	public void patternName(String path, final String tileSize, String brand,
			String type) {
		// System.out.println("hai");
		final String filePath = path;
		bmp = BitmapFactory.decodeFile(filePath);
		drawToolSelected = false;
		drawView.isDrawingSelected(drawToolSelected);
		drawView.selectTile(true);
		this.tileSize = tileSize;
		final Dialog dialog = new Dialog(CustomPatternActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setTitle("Rotate");// sets the title of the dialog box

		dialog.setContentView(layout.tile_rotation_layout);// loads the layout
		// we have

		dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				layout.custom_title_dialog_box);// sets our custom

		// String[] pathArray = tileName.split("/");
		TextView tileNameView = (TextView) dialog.findViewById(id.tileName);
		TextView tileSizeView = (TextView) dialog.findViewById(id.tDim);
		TextView tileBrandNameView = (TextView) dialog
				.findViewById(id.brandname);
		TextView tileTypeView = (TextView) dialog.findViewById(id.tileType);

		tileSizeView.setText(tileSize + " mm");
		tileNameView.setText(new File(path).getName());
		tileBrandNameView.setText(brand);
		tileTypeView.setText(type);

		/*
		 * Button closeButton=(Button)dialog.findViewById(R.id.dialog_close);
		 * closeButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub dialog.dismiss(); } });
		 */
		final ImageView imgV = (ImageView) dialog
				.findViewById(id.tileImageView);
		imgV.setImageBitmap(bmp);
		Button rightRotButton = (Button) dialog
				.findViewById(id.rotRightButton);
		rightRotButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, 90);
				axis++;
				// bmp.recycle();
				bmp = rBmp;
				imgV.setImageBitmap(bmp);
				drawView.setSelectedTile(bmp, filePath, axis, tileSize);
				// rBmp.recycle();
			}
		});
		Button leftRotButton = (Button) dialog.findViewById(id.rotLeftButton);
		leftRotButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, -90);
				// bmp.recycle();
				bmp = rBmp;
				axis--;
				imgV.setImageBitmap(bmp);

				drawView.setSelectedTile(bmp, filePath, axis, tileSize);
				// rBmp.recycle();
				// imgV.setImageBitmap(RotateBitmapRight(bmp, 90));
			}
		});

		dialog.show();

		drawView.setSelectedTile(bmp, filePath, axis, tileSize);
		axis = 0;

	}

	public static Bitmap RotateBitmapRight(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	public void addLayout(int left, int top, int width, int height,
			String selectedTileName, int orientation, String unit, Float rot, Float layoutRotation) {
		RelativeLayout rel = (RelativeLayout) findViewById(id.main_content);
		{

			LinearLayout linearLayout = new LinearLayout(
					getApplicationContext());
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.setBackgroundColor(Color.YELLOW);
			LayoutParams param = new LayoutParams(width, height);
			linearLayout.setLayoutParams(param);
			linearLayout.setClickable(false);
			linearLayout.setOnTouchListener(drawView.layoutTouched);

			/*
			 * ImageView img = new ImageView(context);
			 * img.setBackgroundColor(Color.CYAN);
			 */
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					width, height);
			// params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
			params.leftMargin = left;
			params.topMargin = top;
			DrawView.LayoutDimensions dim = drawView.new LayoutDimensions();
			dim.x = left;
			dim.y = top;
			dim.width = width;
			dim.height = height;
			dim.selectedTile = selectedTileName;
			dim.orientation = orientation;
			dim.rot=rot;
			dim.layRot = layoutRotation;

			linearLayout.setTag(dim);
			rel.setClickable(true);
			rel.addView(linearLayout, params);
			linearLayout.bringToFront();
			final String filePath = selectedTileName;
			try {
				Bitmap bitmap = BitmapFactory.decodeFile(filePath);
				Bitmap rBmp = RotateBitmapRight(bitmap, 90 * orientation);
				// bmp.recycle();

				// imgV.setImageBitmap(bmp);
				drawView.setSelectedTile(rBmp, filePath, orientation, tileSize);
				drawView.fillCustomTileInLayout(linearLayout, true, rBmp, rot, selectedTileName);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	void loadPattern(String patternPath) {
		String data = null;

		final String xmlFile = patternPath;

		// final String xmlFile="layoutData";
		ArrayList<String> userData = new ArrayList<String>();
		try {
			// FileInputStream fis =
			// getApplicationContext().openFileInput(xmlFile);
			// InputStreamReader isr = new InputStreamReader(fis);
			FileReader fr = new FileReader(xmlFile);
			StringBuilder text = new StringBuilder();

			try {
				BufferedReader br = new BufferedReader(fr);
				String line;

				while ((line = br.readLine()) != null) {
					text.append(line);
					text.append('\n');
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			data = new String(text);

		} catch (FileNotFoundException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlPullParserFactory factory = null;
		try {
			factory = XmlPullParserFactory.newInstance();
		} catch (XmlPullParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		factory.setNamespaceAware(true);
		XmlPullParser xpp = null;
		try {
			xpp = factory.newPullParser();
		} catch (XmlPullParserException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			xpp.setInput(new StringReader(data));
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int eventType = 0;
		try {
			eventType = xpp.getEventType();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String name = null;
		int left = 0, top = 0, width = 0, height = 0, orientation = 0;
		String selectedTileName = null, unit = null;
		String readValue = null;
		float tileLength = 0, tileWidth = 0, tileHeight = 0;
		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_DOCUMENT) {
				System.out.println("Start document");
			} else if (eventType == XmlPullParser.START_TAG) {
				name = xpp.getName();
				if (name == "layoutData") {

				}
			} else if (eventType == XmlPullParser.END_TAG) {
				name = xpp.getName();
				if (name.equalsIgnoreCase("left")) {
					left = Integer.parseInt(readValue);
				}
				if (name.equalsIgnoreCase("tileLength")) {
					tileLength = Float.parseFloat(readValue);
				}
				if (name.equalsIgnoreCase("tileWidth")) {
					tileWidth = Float.parseFloat(readValue);
				}
				if (name.equalsIgnoreCase("tileHeight")) {
					tileHeight = Float.parseFloat(readValue);
				}
				if (name.equalsIgnoreCase("top")) {
					top = Integer.parseInt(readValue);
				}
				if (name.equalsIgnoreCase("width")) {
					width = Integer.parseInt(readValue);
				}
				if (name.equalsIgnoreCase("height")) {
					height = Integer.parseInt(readValue);
				}
				if (name.equalsIgnoreCase("tileSize")) {
					tileSize = readValue;
				}
				if (name.equalsIgnoreCase("orientation")) {
					orientation = Integer.parseInt(readValue);

				}
				if (name.equalsIgnoreCase("selectedTile")) {
					selectedTileName = readValue;
				}
				if (name.equalsIgnoreCase("unit")) {
					unit = readValue;
					GlobalVariables.setUnit(unit);
					drawView.setUnit(unit);
				}
				if (name.equalsIgnoreCase("rotation"))
				{
					rotation=Float.parseFloat(readValue);
				}
				if (name.equalsIgnoreCase("layoutRotation"))
				{
					layoutRotation=Float.parseFloat(readValue);
				}
				if (name.equalsIgnoreCase("layoutData")) {
					tLength = tileLength;
					tWidth = tileWidth;
					float ratio = tWidth / tLength;
					float rlength, rwidth;
					if (ratio > 1) {
						ratio = 1 / ratio;
						rlength = (ratio * viewArea);
						rwidth = viewArea;
					} else {
						rwidth = (ratio * viewArea);
						rlength = viewArea;
					}

					LayoutParams params = drawView.getLayoutParams();
					params.height = (int) rlength;
					params.width = (int) rwidth;
					drawView.requestLayout();
					RelativeLayout rel = (RelativeLayout) findViewById(id.main_content);

					// drawView.setLayoutParams(new
					// LayoutParams(rlength,rwidth));
					FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) rel
							.getLayoutParams();
					param.height = (int) rlength;
					param.width = (int) rwidth;
					rel.requestLayout();

					drawView.setDimensions(unit, tileLength, tileWidth);
					addLayout(left, top, width, height, selectedTileName,
							orientation, unit, rotation,layoutRotation);
				}
			} else if (eventType == XmlPullParser.TEXT) {
				// userData.add(xpp.getText());
				readValue = xpp.getText();
				Log.i("value", xpp.getText());
			}
			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// String userName=userData.get(o);
		// String password=userData.get(1);
	}

	void getWallInfo() {

		final Dialog dialog = new Dialog(CustomPatternActivity.this);
		dialog.setTitle("Tile Info");
		dialog.setContentView(layout.wall_info_dialog);
		TextView widthTextView = (TextView) dialog
				.findViewById(id.widthTextView);
		TextView lengthTextView = (TextView) dialog
				.findViewById(id.lengthTextView);

		lengthTextView.setText("Width = " + tWidth + " mm / "
				+ GlobalVariables.mmToFeet(tWidth) + " feet / "
				+ GlobalVariables.mmToInches(tWidth) + " inches");
		widthTextView.setText("Length = " + tLength + " mm / "
				+ GlobalVariables.mmToFeet(tLength) + " feet / "
				+ GlobalVariables.mmToInches(tLength) + " inches");
		dialog.show();

	}

	void showSaveAsDialog() {
		Dialog dialog = new Dialog(CustomPatternActivity.this);
		dialog.setContentView(layout.save_as_dialog);
		dialog.setTitle("Save As");
		final EditText saveAsNameField = (EditText) dialog
				.findViewById(id.saveAsNameField);
		final File patFile = new File(patternName);
		saveAsNameField.setText(patFile.getName());
		Button saveButton = (Button) dialog.findViewById(id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				patternName = patFile.getAbsolutePath() + "/"
						+ saveAsNameField.getText().toString();
				isSavedAs = true;
				saveTileDetails();
				finish();
			}
		});
		dialog.show();
	}
	private void tileSearch() {
		// TODO Auto-generated method stub
		searchpattern(eText.getText().toString());
		eText.setText("");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case id.searchButton:
			{
				tileSearch();
				break;
			}
		case id.filterButton2:
			{
				applyFilter();
				
				break;
			}
		case id.filterButton:
			{
				filterSelected();
				break;
			}
		}
	}

//	public void tileSearch(View v) {
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View menuLayout = inflater.inflate(R.layout.layout_patters,
		// patternScrollView, false);

//		System.out.println("edittext" + eText.getText().toString());
//		if(!eText.getText().toString().equals("")||!eText.getText().toString().equals(null))
//		{
//		searchpattern(eText.getText().toString());
//		eText.setText("");
//		}
//		else
//		{
//			Toast.makeText(getApplicationContext(), "Enter Keyword to Search", 1000).show();
//		}
//	}

	protected void searchpattern(String keyString) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
//		eText.setText("");
		dbResult = db.searchkeyword(keyString);
		
		if (dbResult.size() > 0) {
			PatternGridAdapter pgAdapter = new PatternGridAdapter(this,
					dbResult, CustomPatternActivity.this);
			gView.refreshDrawableState();
			pgAdapter.notifyDataSetChanged();
			// grid.setAdapter(adapter);
			gView.setAdapter(null);
			gView.setAdapter(pgAdapter);
		} else {
			gView.setAdapter(null);
			Toast.makeText(this, "No Tile found !", Toast.LENGTH_LONG).show();
		}
		// System.out.println("test");
	}

}
