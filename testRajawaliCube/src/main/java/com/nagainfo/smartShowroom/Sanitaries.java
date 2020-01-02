package com.nagainfomob.smartShowroom;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Sanitaries extends Activity {
	static Cursor imageCursor;
	static int columnIndex;
	private LayoutInflater inflater;
	private View patternContent;
	private LinearLayout patternScrollView;
	private LinearLayout content_slider;
	private FrameLayout frame;
	private int displayWidth;
	private boolean isDrawerOpen;
	private boolean isOpening;
	private RelativeLayout view;
	private GridView gView;
	private String imgPath;
	ArrayList<String> pathlist = new ArrayList<String>();
	ArrayList<Integer> idlist = new ArrayList<Integer>();

	private int _xDelta;
	private int _yDelta;



	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sanitary_bg);
		String title = getResources().getString(R.string.app_name)
				+ "\t\t---\t\t" + GlobalVariables.getProjectName();
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		bar.setTitle(title);

		Intent intent = getIntent();

		String path = intent.getStringExtra("PATH");

		// Log.d("TAG", path + ".png");
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(path + ".png");
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		view = (RelativeLayout) findViewById(R.id.container);
		view.setBackground(bd);
		initialise();

		// imgHolder = (RelativeLayout) findViewById(R.id.image_view_holder);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (flag == 1) {
					
					Toast.makeText(getApplicationContext(), s, 1000).show();
					flag = 0;
					DisplayMetrics metrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(metrics);

					
					ImageView img;
					img = new ImageView(getApplicationContext());
					
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					Bitmap bitmap = BitmapFactory.decodeFile(s, options);

					img.setImageBitmap(bitmap);

					
					view.addView(img);
					

					img.setOnTouchListener(new MyScaleGestures(Sanitaries.this));

				}
			}
		});

		// imgHolder.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// if (event.getPointerCount() == 2) {
		// return sdetectr.onTouchEvent(event);
		// }
		// final int X = (int) event.getRawX();
		// final int Y = (int) event.getRawY();
		// switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// case MotionEvent.ACTION_DOWN:
		// RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v
		// .getLayoutParams();
		// _xDelta = X - lParams.leftMargin;
		// _yDelta = Y - lParams.topMargin;
		// break;
		// case MotionEvent.ACTION_UP:
		// break;
		// case MotionEvent.ACTION_POINTER_DOWN:
		// break;
		// case MotionEvent.ACTION_POINTER_UP:
		// break;
		// case MotionEvent.ACTION_MOVE:
		// RelativeLayout.LayoutParams layoutParams =
		// (RelativeLayout.LayoutParams) v
		// .getLayoutParams();
		// layoutParams.leftMargin = X - _xDelta;
		// layoutParams.topMargin = Y - _yDelta;
		// layoutParams.rightMargin = -250;
		// layoutParams.bottomMargin = -250;
		// v.setLayoutParams(layoutParams);
		// break;
		// }
		// v.invalidate();
		// return true;
		// }
		// });

	}

	

	

	private void initialise() {
		// TODO Auto-generated method stub
		isDrawerOpen = false;
		isOpening = false;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// frame = (FrameLayout) inflater.inflate(R.layout.activity_3d, null);
		patternContent = inflater.inflate(R.layout.fill_items, null);

		patternScrollView = (LinearLayout) findViewById(R.id.ScrollView);
		content_slider = (LinearLayout) findViewById(R.id.slider);
		// patternScrollView = (LinearLayout) frame.findViewById(R.id.additems);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		displayWidth = size.x;
		final int left = displayWidth;
		final int top = content_slider.getTop();
		final int right = content_slider.getRight();
		final int bottom = content_slider.getBottom();
		content_slider.layout(left, top, right, bottom);

		// String[] imgColumnID = { MediaStore.Images.Thumbnails._ID };
		// Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
		// imageCursor = getApplicationContext().getContentResolver().query(uri,
		// imgColumnID, null, null, MediaStore.Images.Thumbnails.IMAGE_ID);
		// columnIndex = imageCursor
		// .getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.sanitary_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.items:
			addslider();
			break;
		}
		return true;
	}

	Boolean getIsDrawerOpen() {
		return this.isDrawerOpen;
	}

	void setIsOpening(Boolean value) {
		this.isOpening = value;
	}

	void setIsDrawerOpen() {
		this.isDrawerOpen = !this.isDrawerOpen;
	}

	private void addslider() {
		// TODO Auto-generated method stub

		if (!isOpening) {

			Animation animation;
			if (!isDrawerOpen) {
				animation = new TranslateAnimation(displayWidth, 0, 0, 0);
				// isDrawerOpen = true;

			} else {
				animation = new TranslateAnimation(0, displayWidth, 0, 0);
				// isDrawerOpen = false;
			}

			animation.setDuration(500);
			animation.setAnimationListener(slideLeftRightAnimationListener);

			content_slider.setVisibility(View.VISIBLE);
			content_slider.bringToFront();

			// content_slider.getBackground().setAlpha(1000);

			content_slider.setAnimation(animation);
			content_slider.startAnimation(animation);
			content_slider.requestLayout();
			// animation.start();
		}

	}

	private AnimationListener slideLeftRightAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			setIsOpening(true);
			content_slider.bringToFront();
			if (!getIsDrawerOpen()) {

				fillitems();
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
				patternScrollView.removeAllViews();

			}
			setIsOpening(false);
			setIsDrawerOpen();
		}

	};
	private Cursor cursor;
	private int columnIndex1;
	File[] listFile;
	ArrayList<String> f = new ArrayList<String>();

	public void getFromSdcard() {
		File file = new File(
				android.os.Environment.getExternalStorageDirectory(),
				"IPICTURE");

		if (file.isDirectory()) {
			listFile = file.listFiles();
			if (!f.isEmpty()) {
				f.clear();
			}

			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].getName().endsWith(".jpg")) {
					f.add(listFile[i].getAbsolutePath());
				}
			}
		}
	}

	String s;
	int flag = 0;

	protected void fillitems() {
		// TODO Auto-generated method stub
		// Environment.getExternalStorageDirectory().getAbsolutePath()
		// MediaStore.Images.Media.DATA
		patternScrollView.removeAllViews();

		gView = (GridView) patternContent.findViewById(R.id.pattern);
		getFromSdcard();
		imageAdapter = new ImageAdapter();
		gView.setAdapter(null);
		gView.setAdapter(imageAdapter);

		patternScrollView.addView(patternContent);

		gView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				Toast.makeText(getApplicationContext(), f.get(position), 1000)
						.show();
				flag = 1;
				s = f.get(position);
			}

		});

	}

	ImageAdapter imageAdapter;

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return f.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.grid_item, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.item_img);
				holder.imageview.setAdjustViewBounds(true);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						200, 200);

				holder.imageview.setLayoutParams(layoutParams);
				// holder.imageview.set

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position));
			holder.imageview.setImageBitmap(myBitmap);
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;

	}

	

	public class MyScaleGestures implements OnTouchListener,
			OnScaleGestureListener {

		private View view;
		private ScaleGestureDetector gestureScale;
		private float scaleFactor = 1;

		public MyScaleGestures(Context c) {
			gestureScale = new ScaleGestureDetector(c, this);
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			this.view = view;
			gestureScale.onTouchEvent(event);

			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view
						.getLayoutParams();
				_xDelta = X - lParams.leftMargin;
				_yDelta = Y - lParams.topMargin;
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
						.getLayoutParams();
				layoutParams.leftMargin = X - _xDelta;
				layoutParams.topMargin = Y - _yDelta;
				layoutParams.rightMargin = -250;
				layoutParams.bottomMargin = -250;
				view.setLayoutParams(layoutParams);
				break;
			}
			view.invalidate();

			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();
			scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor);
			scaleFactor = ((float) ((int) (scaleFactor * 100))) / 100;
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);
			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {

			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector arg0) {
			// TODO Auto-generated method stub

		}
	}
}
