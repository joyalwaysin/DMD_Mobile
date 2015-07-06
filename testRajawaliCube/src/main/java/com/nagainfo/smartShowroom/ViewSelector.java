package com.nagainfo.smartShowroom;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
//import android.support.v4.view
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

public class ViewSelector extends FragmentActivity implements TabListener {

	ActionBar bar;
	CustomViewPager pager = null;
	private AssetManager asmngr;
	public static String foldername,time;
	static ArrayList<String> f = new ArrayList<String>();
	static ArrayList<String> g = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.viewselector);
		foldername=Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/SmartShowRoom-Saved Views/"
				+ GlobalVariables.getProjectName();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		time=sdf.format(new Date());
		bar = getActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	
		pager = (CustomViewPager) findViewById(R.id.pager);
		
		pager.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		FragmentManager fm = getSupportFragmentManager();
		pager.setAdapter(new MyAdapter(fm, pager));
		pager.setOffscreenPageLimit(4);
		
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				bar.setSelectedNavigationItem(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		

		Tab tab1 = bar.newTab();
		tab1.setText("FRONT WALL");
		tab1.setTabListener(this);

		Tab tab2 = bar.newTab();
		tab2.setText("LEFT WALL");
		tab2.setTabListener(this);

		Tab tab3 = bar.newTab();
		tab3.setText("RIGHT WALL");
		tab3.setTabListener(this);

		Tab tab4 = bar.newTab();
		tab4.setText("BACK WALL");
		tab4.setTabListener(this);

		bar.addTab(tab1);
		bar.addTab(tab2);
		bar.addTab(tab3);
		bar.addTab(tab4);
		
		
		
//		new Runnable() {
//			public void run() {
//				getFromAssets();
//			}
//		}.run();

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final Dialog dialog=new Dialog(ViewSelector.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.exit_object_page);
		Button exit=(Button)dialog.findViewById(R.id.go_back);
		Button stay=(Button)dialog.findViewById(R.id.stay);
		
		exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
			}
		});
		
		stay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	
	public void getFromAssets() {
		if (!f.isEmpty()) {
			f.clear();
			g.clear();
		}
		
		String[] assetfoldersList, assetItemsList;
		asmngr = getAssets();

		try {
			assetfoldersList = asmngr.list("Sanitary");
			for (int i = 0; i < assetfoldersList.length; i++) {
				assetItemsList = asmngr.list("Sanitary/" + assetfoldersList[i]);
				for (int j = 0; j < assetItemsList.length; j++) {
					String string = "Sanitary/" + assetfoldersList[i] + "/"
							+ assetItemsList[j];
					if (!string.endsWith("_tilt.png")) {
						f.add(string);
						g.add(assetfoldersList[i]
								+ " "
								+ assetItemsList[j].replace(".png", "")
										.toUpperCase());

					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // TODO Auto-generated method stub
	// getMenuInflater().inflate(R.menu.sanitary_menu, menu);
	//
	// return true;
	// }

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		pager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

}

class MyAdapter extends FragmentStatePagerAdapter {
	ViewPager viewPager;

	public MyAdapter(FragmentManager fm, ViewPager viewPager) {
		super(fm);
		this.viewPager = viewPager;

		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		Fragment fragment = null;
		if (arg0 == 0) {
			fragment = new FragFrontView();
		}
		if (arg0 == 1) {
			fragment = new FragLeftView();
		}
		if (arg0 == 2) {
			fragment = new FragRightView();
		}
		if (arg0 == 3) {
			fragment = new FragBackView();
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}
}
