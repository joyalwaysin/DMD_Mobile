package com.nagainfo.smartShowroom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.Toast;

import com.nagainfo.database.DatabaseHandler;
import com.nagainfo.slider.PatternGridAdapter;
import com.nagainfo.slider.PatternGridAdapter1;

public class ViewPatternActivity extends Activity implements OnItemClickListener {

	private static final String KEY_BRAND_NAME = "pro_brand";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_COMPANY = "pro_company";
	private static final String KEY_BRAND = "pro_brand";
	private static final String KEY_NAME = "pro_name";
	
	List<Integer> selectedBrands = new ArrayList<Integer>();
	List<Integer> selectedSize = new ArrayList<Integer>();
	List<Integer> selectedColor = new ArrayList<Integer>();
	List<Integer> selectedType = new ArrayList<Integer>();
	List<Integer> selectedCompany = new ArrayList<Integer>();
	private GridView gView;
	ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult3 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult4 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> filterResult = new ArrayList<HashMap<String, String>>();
	ArrayList<String> names= new ArrayList<String>();
	ArrayList<String> brands= new ArrayList<String>();
	String tname,tBrand;

	private LinearLayout patternScrollView;

	private ViewPatternActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_viewpattern);
		this.getActionBar().setBackgroundDrawable(
				this.getResources().getDrawable(R.drawable.patten_page_header));
		this.getActionBar().setTitle(null);
		this.getActionBar().setDisplayShowHomeEnabled(false);

		gView = (GridView) findViewById(R.id.patternGrid);
		gView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		gView.setMultiChoiceModeListener(new MultiChoiceModeListener());
		gView.setOnItemClickListener(this);
		
		// gView.setOnItemLongClickListener(new OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
		// int arg2, long arg3) {
		// // TODO Auto-generated method stub
		// gView.showContextMenu();
		// return false;
		// }
		// });

		activity = this;

		if (!patternFilterDialog()) {
			Toast.makeText(getApplicationContext(), "Use filter to continue!",
					Toast.LENGTH_LONG).show();
			return;
		}

	}

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// // TODO Auto-generated method stub
	// super.onCreateContextMenu(menu, v, menuInfo);
	// if(v.getId()==R.id.patternGrid)
	// {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.delete_tiles, menu);
	// }
	// }
	//
	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// // TODO Auto-generated method stub
	// AdapterContextMenuInfo info = (AdapterContextMenuInfo)
	// item.getMenuInfo();
	// switch(item.getItemId()) {
	// // case R.id.add:
	// // add stuff here
	// // return true;
	// // case R.id.edit:
	// // edit stuff here
	// // return true;
	// case R.id.delete:
	// // remove stuff here
	//
	// return true;
	// default:
	// }
	// return super.onContextItemSelected(item);
	// }

	private boolean patternFilterDialog() {
		selectedCompany.clear();
		selectedBrands.clear();
		selectedSize.clear();
		selectedColor.clear();
		selectedType.clear();
		// TODO Auto-generated method stub
		final Dialog fDialog = new Dialog(ViewPatternActivity.this);
		fDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		fDialog.setContentView(R.layout.filter_dialog);
		Button fApply = (Button) fDialog.findViewById(R.id.fApply);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(fDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.FILL_PARENT;
		fDialog.getWindow().setAttributes(lp);
		// filterSelected();

		addcompanyCheckBox(fDialog);
		addbrandCheckBox(fDialog);
		addSizeCheckBox(fDialog);
		addColorCheckBox(fDialog);
		addTypeCheckBox(fDialog);
		fDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				Toast.makeText(activity, "Use filter to refine your search !",
						Toast.LENGTH_LONG).show();
				return false;

			}
		});
		fDialog.show();
		fApply.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {

					applyDialog(fDialog);
					return true;
				}
				return false;
			}
		});

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pattern_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// action with ID action_refresh was selected
		case R.id.filter: {

			patternFilterDialog();
		}
			break;
		case R.id.create_pattern: {
			Intent intent = new Intent(this, CustomPatternActivity.class);
			startActivity(intent);
			finish();
		}
			break;
		case R.id.load_pattern: {
			Intent intent = new Intent(this, ListCustomPatternActivity.class);
			startActivity(intent);
		}
			break;
		default:
			break;
		}

		return true;
	}

	public void addcompanyCheckBox(Dialog fDialog) {
		Dialog v = fDialog;
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.companyCheckBox);

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult1 = db.getAllCompany();
		List<String> companyNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult1.size(); i++) {

			Map<String, String> temp1 = dbResult1.get(i);
			if (!temp1.get(KEY_COMPANY).equalsIgnoreCase(""))
				companyNameList.add(temp1.get(KEY_COMPANY));
		}

		for (int i = 0; i < companyNameList.size(); i++) {

			CheckBox cb = new CheckBox(getApplicationContext());
			cb.setText(companyNameList.get(i));
			cb.setTextColor(Color.WHITE);
			cb.setTag(i);
			cb.setOnCheckedChangeListener(companyCheckListener);
			ll.addView(cb);
		}

	}

	OnCheckedChangeListener companyCheckListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				int z = 0;
				Log.d("TAG", z + "");
				try {
					int t = (Integer) buttonView.getTag();
					selectedCompany.add(new Integer(t));
					// selectedBrands.add(new Integer(t));
				} catch (Exception e) {
					Log.e("brand checked item error", e.getMessage());
				}

			}

			if (!isChecked) {
				// int z=1;
				// Log.d("TAG", z+"");
				try {
					int t = (Integer) buttonView.getTag();
					selectedCompany.remove(new Integer(t));
					// selectedBrands.add(new Integer(t));
				} catch (Exception e) {
					Log.e("brand checked item error", e.getMessage());
				}
			}

		}

	};

	public void addbrandCheckBox(Dialog fDialog) {
		Dialog v = fDialog;
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.brandCheckBox);

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult1 = db.getAllBrand();
		List<String> brandNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult1.size(); i++) {
			Map<String, String> temp1 = dbResult1.get(i);
			if (!temp1.get(KEY_BRAND_NAME).equalsIgnoreCase(""))
				brandNameList.add(temp1.get(KEY_BRAND_NAME));
		}

		for (int i = 0; i < brandNameList.size(); i++) {

			CheckBox cb = new CheckBox(getApplicationContext());
			cb.setText(brandNameList.get(i));
			cb.setTextColor(Color.WHITE);
			cb.setTag(i);
			cb.setOnCheckedChangeListener(brandCheckListener);
			ll.addView(cb);
		}

	}

	OnCheckedChangeListener brandCheckListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedBrands.add(new Integer(t));
				} catch (Exception e) {
					Log.e("brand checked item error", e.getMessage());
				}

			}
			if (!isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedBrands.remove(new Integer(t));
				} catch (Exception e) {
					Log.e("brand checked item error", e.getMessage());
				}
			}

		}

	};

	public void addSizeCheckBox(Dialog fDialog) {
		LinearLayout l = (LinearLayout) fDialog.findViewById(R.id.sizeCheckbox);
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
			cb.setTextColor(Color.WHITE);
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

				selectedSize.add(new Integer(t));
			}

			if (!isChecked) {
				int t = (Integer) buttonView.getTag();

				selectedSize.remove(new Integer(t));
			}

		}
	};

	public void addColorCheckBox(Dialog fDialog) {
		LinearLayout l = (LinearLayout) fDialog
				.findViewById(R.id.colorCheckBox);
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
			cb.setTextColor(Color.WHITE);
			// cb.setButtonDrawable(getApplicationContext().getResources().getDrawable(R.drawable.border_blue));
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
				selectedColor.add(new Integer(t));

			}
			if (!isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedColor.remove(new Integer(t));
			}

		}
	};

	public void addTypeCheckBox(Dialog fDialog) {
		LinearLayout l = (LinearLayout) fDialog.findViewById(R.id.typeCheckBox);
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
			cb.setTextColor(Color.WHITE);
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

			}
			if (!isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedType.remove(new Integer(t));
			}

		}
	};
	
	private void deletepattern(String spath, ArrayList<String> names,
			ArrayList<String> brand, String tDimn, String tType) {
		final Dialog delDialog = new Dialog(activity);
		delDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		delDialog.setContentView(R.layout.delete_pattern);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(delDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		Button delbtn = (Button) delDialog.findViewById(R.id.but_delete);
		Button cancel = (Button) delDialog.findViewById(R.id.but_cancel);
		final String name = tname;
		final ArrayList<String> brands=brand;
		final ArrayList<String> patternname=names;
		
		delbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			for(int i=0;i<patternname.size();i++)	{
				
//				if (brands.get(i).equalsIgnoreCase("cam click")) {
					DatabaseHandler db = new DatabaseHandler(activity);
					db.deletepattern(patternname.get(i).replace(".jpg", ""));
					activity.recreate();
//				} else {
//					Toast.makeText(activity,
//							"Only Custom Patterns Can be Deleted!!", 1000)
//							.show();
//				}
				delDialog.dismiss();
			
			}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				delDialog.cancel();

				// delDialog.dismiss();
			}
		});

		delDialog.show();
	}

	public void applyDialog(Dialog dialog) {

		String cr_company = null, br_name = null, pro_size = null, pro_color = null, pro_type = null;
//		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
//		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();
//		ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
//		ArrayList<HashMap<String, String>> dbResult3 = new ArrayList<HashMap<String, String>>();
//		ArrayList<HashMap<String, String>> dbResult4 = new ArrayList<HashMap<String, String>>();
//		ArrayList<HashMap<String, String>> filterResult = new ArrayList<HashMap<String, String>>();

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

		if (selectedCompany.size() > 0)
			cr_company = getStringFromCheckedList(dbResult, selectedCompany,
					KEY_COMPANY);
		if (selectedBrands.size() > 0)
			br_name = getStringFromCheckedList(dbResult1, selectedBrands,
					KEY_BRAND_NAME);
		if (selectedSize.size() > 0)
			pro_size = getStringFromCheckedList(dbResult2, selectedSize,
					KEY_DIMEN);
		if (selectedColor.size() > 0)
			pro_color = getStringFromCheckedList(dbResult3, selectedColor,
					KEY_COLOR);
		if (selectedType.size() > 0)
			pro_type = getStringFromCheckedList(dbResult4, selectedType,
					KEY_TYPE);
		DatabaseHandler db5 = new DatabaseHandler(getApplicationContext());
		filterResult = db5.getResultByFilter(cr_company, br_name, pro_size,
				pro_color, pro_type);
		if (filterResult.size() > 0) {
			selectedCompany.clear();
			selectedBrands.clear();
			selectedSize.clear();
			selectedColor.clear();
			selectedType.clear();
			// patternScrollView.removeAllViews();
			dialog.dismiss();
			padapeter = new PatternGridAdapter1(activity, filterResult);
			gView.setAdapter(padapeter);

			// registerForContextMenu(gView);

		} else {
			selectedCompany.clear();
			selectedBrands.clear();
			selectedSize.clear();
			selectedColor.clear();
			selectedType.clear();
			// Intent in=new
			// Intent(getApplicationContext(),ViewPatternActivity.class);

			Toast.makeText(activity, "No Tile found", Toast.LENGTH_LONG).show();
			gView.setAdapter(null);
			dialog.cancel();
			// startActivity(in);

		}

	}

	PatternGridAdapter1 padapeter;

	public String getStringFromCheckedList(
			ArrayList<HashMap<String, String>> dbResult,
			List<Integer> selectedBrands2, String ketItem) {
		List<String> itemString = new ArrayList<String>();
		String resString = "";
		if (selectedBrands2.size() == 0) {
			return null;
		}
		for (int i = 0; i < dbResult.size(); i++) {
			String temp = dbResult.get(i).get(ketItem);
			if (temp.contains("mm")) {
				// itemString.add(temp.replace("mm", ""));
				itemString.add(temp);
			} else {
				itemString.add(temp);
			}
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
			System.out.println(selectedBrands2.size());
			for (int i = 1; i < selectedBrands2.size(); i++) {
				try {
					int itemId = selectedBrands2.get(i);
					resString += ",'" + itemString.get(selectedBrands2.get(i))
							+ "'";
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("getStringFromCheckedList error!", e.getMessage());
					return null;

				}
			}
		}
		return resString;

	}

	public class MultiChoiceModeListener implements
			GridView.MultiChoiceModeListener {

		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.setTitle("Select Items");
			mode.setSubtitle("One item selected");
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			menu.add("DELETE");
			return true;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if(item.getTitle().equals("DELETE"))
			{
				mode.finish();
				deletepattern(null, names, brands, null, null);
			}
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) {
		}

		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			// gView.setItemChecked(4, checked);
			int selectCount = gView.getCheckedItemCount();
			switch (selectCount) {
			case 1:
				mode.setSubtitle("One item selected");
				break;
			default:
				mode.setSubtitle("" + selectCount + " items selected");
				break;
			}
			String tBrand = filterResult.get(position).get(KEY_BRAND);
			String tname = filterResult.get(position).get(KEY_NAME);
			if(checked)
			{
				names.add(tname);
				brands.add(tBrand);
			}
			if(!checked)
			{
				names.remove(tname);
				brands.remove(tBrand);
			}
		}

	}

//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		switch (v.getId()) {
//		case R.id.patternGrid:
//			Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_LONG).show();
//			break;
//
//		default:
//			break;
//		}
//		
//	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		// TODO Auto-generated method stub
//		Object pos = arg1.getTag();
		String path = GlobalVariables.getPatternRootPath()
				+ filterResult.get(position).get(KEY_BRAND) + "/"
				+ filterResult.get(position).get(KEY_NAME);
		String tname = filterResult.get(position).get(KEY_NAME);
		String tBrand = filterResult.get(position).get(KEY_BRAND);
		String tDim = null;
		if (filterResult.get(position).get(KEY_DIMEN) != null) {
			tDim = filterResult.get(position).get(KEY_DIMEN)
					.replaceAll("mm", "");
		}
		String tType = filterResult.get(position).get(KEY_TYPE);
		PatternGridAdapter pd=new PatternGridAdapter(activity, filterResult);
		pd.showTileDialog(v, path, tname.replace(".jpg", ""), tBrand,
				tDim, tType);
//		Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_LONG).show();
	}
	

}
