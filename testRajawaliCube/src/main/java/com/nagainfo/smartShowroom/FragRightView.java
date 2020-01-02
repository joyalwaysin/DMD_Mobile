package com.nagainfomob.smartShowroom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.nagainfomob.database.DatabaseHandler;
import com.nagainfomob.smartShowroom.FragFrontView.ImageAdapter;
import com.nagainfomob.smartShowroom.FragFrontView.LoadItems;
import com.nagainfomob.smartShowroom.FragFrontView.MyScaleGestures;
import com.nagainfomob.smartShowroom.FragFrontView.ViewHolder;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

public class FragRightView extends Fragment {

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

	private GridView gView;
	private String imgPath;
	ArrayList<String> pathlist = new ArrayList<String>();
	ArrayList<Integer> idlist = new ArrayList<Integer>();

	private int _xDelta;
	private int _yDelta;
	int flag = 0;
	private RelativeLayout view;
	private View view4;
	private ImageView img;
	ArrayList<String> names = new ArrayList<String>();
	ArrayList<String> paths = new ArrayList<String>();
	Button searchObjects;
	EditText objSearchField;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view4 = inflater.inflate(R.layout.sanitary_bg, container, false);
		setHasOptionsMenu(true);
		String title = GlobalVariables.getProjectName();
		ActionBar bar = getActivity().getActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		bar.setTitle(title);
		initialise();
		Resources res = getResources();
		Bitmap bitmap = BitmapFactory.decodeFile(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/FreezeShots/"
				+ GlobalVariables.getProjectName() + "-Right" + ".png");
		BitmapDrawable bd = new BitmapDrawable(res, bitmap);
		view = (RelativeLayout) view4.findViewById(R.id.container);
		view.setBackground(bd);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (flag == 1) {

					flag = 0;
					DisplayMetrics metrics = new DisplayMetrics();
					getActivity().getWindowManager().getDefaultDisplay()
							.getMetrics(metrics);

					// ImageView img;
					// count++;

					// if(isDrawerOpen)
					// {
					// addslider();
					// }
					img = new ImageView(getActivity().getApplicationContext());
					// img.setTag(count);

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					InputStream istr;
					Bitmap myBitmap;
					asmngr = getActivity().getAssets();
					try {
						istr = asmngr.open(s.replace(".png", "_tilt.png"));
						myBitmap = BitmapFactory.decodeStream(istr);
						img.setImageBitmap(myBitmap);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					view.addView(img);
					LayoutParams p = (LayoutParams) img.getLayoutParams();
					p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					img.setLayoutParams(p);
					img.setTag(true);

					img.setOnTouchListener(new MyScaleGestures(getActivity()));

				}
			}
		});
		//
		// new Runnable() {
		// public void run() {
		// getFromAssets();
		// }
		// }.run();
		return view4;
	}

	private void initialise() {
		// TODO Auto-generated method stub
		isDrawerOpen = false;
		isOpening = false;
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// frame = (FrameLayout) inflater.inflate(R.layout.activity_3d, null);
		patternContent = inflater.inflate(R.layout.fill_items, null);

		patternScrollView = (LinearLayout) view4.findViewById(R.id.ScrollView);
		content_slider = (LinearLayout) view4.findViewById(R.id.slider);
		objSearchField = (EditText) content_slider
				.findViewById(R.id.obj_Search);
		searchObjects = (Button) content_slider
				.findViewById(R.id.search_objects_button);
		// patternScrollView = (LinearLayout) frame.findViewById(R.id.additems);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		displayWidth = size.x;
		final int left = displayWidth;
		final int top = content_slider.getTop();
		final int right = content_slider.getRight();
		final int bottom = content_slider.getBottom();
		content_slider.layout(left, top, right, bottom);
		f = HomeActivity.f;
		g = HomeActivity.g;

		// String[] imgColumnID = { MediaStore.Images.Thumbnails._ID };
		// Uri uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
		// imageCursor = getApplicationContext().getContentResolver().query(uri,
		// imgColumnID, null, null, MediaStore.Images.Thumbnails.IMAGE_ID);
		// columnIndex = imageCursor
		// .getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inf) {
		// TODO Auto-generated method stub
		inf.inflate(R.menu.sanitary_menu, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.items:
			addslider();
			break;
		case R.id.save:
			saveView();
			break;
		}
		return true;
	}

	private void saveView() {
		// RelativeLayout rel = (RelativeLayout)
		// view4.findViewById(R.id.container);
		view4.setDrawingCacheEnabled(true);
		view4.buildDrawingCache();
		Bitmap bitmap = view4.getDrawingCache();

		File file;
		File myDir = new File(ViewSelector.foldername);
		if (!myDir.exists()) {
			myDir.mkdirs();
			// GlobalVariables.createNomediafile(Environment.getExternalStorageDirectory()
			// .getAbsolutePath()
			// + "/SmartShowRoom/FreezeShots/"
			// + GlobalVariables.getProjectName());
			// GlobalVariables.createNomediafile(ViewSelector.foldername);

		}
		file = new File(myDir, "RightWall_" + ViewSelector.time + ".jpg");
		// Log.i(TAG, "" + file);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
			final Dialog d = new Dialog(getActivity());
			d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			d.setContentView(R.layout.saved_location);
			TextView tv = (TextView) d.findViewById(R.id.save_loc);
			Button ok = (Button) d.findViewById(R.id.ok_button);
			tv.setText("View Saved at: " + myDir);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					d.dismiss();
				}
			});
			d.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
				animation = new TranslateAnimation(-displayWidth, 0, 0, 0);
				// isDrawerOpen = true;

			} else {
				animation = new TranslateAnimation(0, -displayWidth, 0, 0);
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

				searchObjects.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String str = objSearchField.getText().toString().trim();
						// if(str.length()>0)
						// {
						objSearchField.setText("");
						filterFromObjects(str);
						// }

					}

				});

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

	File[] listFile, listpics;
	ArrayList<String> f = new ArrayList<String>();
	ArrayList<String> g = new ArrayList<String>();
	private AssetManager asmngr;

	public void filterFromObjects(String string) {
		// TODO Auto-generated method stub
		names.clear();
		paths.clear();
		ImageAdapter imgAdapter;
		if (string.length() > 0) {
			for (int z = 0; z < f.size(); z++) {
				String fromList = g.get(z);
				if (fromList.toUpperCase().contains(string.toUpperCase())
						|| fromList.toLowerCase()
								.contains(string.toLowerCase())) {
					paths.add(f.get(z));
					names.add(g.get(z));
				}
			}
			imgAdapter = new ImageAdapter(paths, names);

		} else {
			imgAdapter = new ImageAdapter(f, g);
		}
		gView.setAdapter(null);
		gView.setAdapter(imgAdapter);
	}

	public void getFromSdcard() {
		File file = new File(
				Environment.getExternalStorageDirectory(),
				"IPICTURE");

		if (file.isDirectory()) {
			listFile = file.listFiles();
			if (!f.isEmpty()) {
				f.clear();
				g.clear();
			}

			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].getName().endsWith(".jpg")
						|| listFile[i].getName().endsWith(".png")) {
					f.add(listFile[i].getAbsolutePath());
				} else if (listFile[i].isDirectory()) {
					listpics = new File(listFile[i].getAbsolutePath())
							.listFiles();
					for (int j = 0; j < listpics.length; j++) {
						if (listpics[j].getName().endsWith(".png")
								&& !listpics[j].getName().endsWith("_tilt.png")) {

							f.add(listpics[j].getAbsolutePath());
							String ad = new File(listpics[j].getParent())
									.getParent().toString();
							String ad1 = listpics[j].getName()
									.replace(".png", "").toUpperCase();
							g.add(listpics[j].getParent().replace(ad + "/", "")
									.trim()
									+ " " + ad1);
						}
					}
				}
			}
		}
	}

	public void getFromAssets() {
		if (!f.isEmpty()) {
			f.clear();
			g.clear();
		}

		String[] assetfoldersList, assetItemsList;
		asmngr = getActivity().getAssets();

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

	String s;

	protected void fillitems() {
		// TODO Auto-generated method stub
		// Environment.getExternalStorageDirectory().getAbsolutePath()
		// MediaStore.Images.Media.DATA
		patternScrollView.removeAllViews();

		gView = (GridView) patternContent.findViewById(R.id.pattern);
		// getFromSdcard();
		imageAdapter = new ImageAdapter();
		gView.setAdapter(null);
		gView.setAdapter(imageAdapter);

		patternScrollView.addView(patternContent);

		gView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub

				// Toast.makeText(getActivity().getApplicationContext(),
				// f.get(position), 1000)
				// .show();
				flag = 1;
				s = setPath.get(position);
			}

		});

	}

	ImageAdapter imageAdapter;
	ArrayList<String> setPath;

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ArrayList<String> path, name;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.path = f;
			this.name = g;
			setPath=f;
		}

		public ImageAdapter(ArrayList<String> paths, ArrayList<String> names) {
			mInflater = (LayoutInflater) getActivity().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			this.path = paths;
			this.name = names;
			setPath=paths;
		}

		public int getCount() {
			return path.size();
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
				holder.text = (TextView) convertView
						.findViewById(R.id.item_name);
				holder.imageview.setAdjustViewBounds(true);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						200, 200);

				holder.imageview.setLayoutParams(layoutParams);
				// holder.imageview.set

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			new LoadItems(holder.imageview).execute(path.get(position));
			holder.text.setText(name.get(position));
//			setPath.add(path.get(position));
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;
		TextView text;

	}

	Handler handler = new Handler();

	public class LoadItems extends AsyncTask<String, Integer, Bitmap> {

		ImageView image;

		public LoadItems(ImageView image) {
			this.image = image;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			InputStream istr;
			Bitmap myBitmap = null;
			asmngr = getActivity().getAssets();
			try {
				istr = asmngr.open(params[0]);
				myBitmap = BitmapFactory.decodeStream(istr);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return myBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			image.setImageBitmap(result);
		}

	}

	public class MyScaleGestures implements OnTouchListener,
			OnScaleGestureListener {

		private View view;
		private ScaleGestureDetector gestureScale;
		private float scaleFactor = 1;
		boolean longclick = false;

		public MyScaleGestures(Context c) {
			gestureScale = new ScaleGestureDetector(c, this);
		}

		CountDownTimer timer = new CountDownTimer(1000, 1) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

			}

			Dialog dialog;

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub

				if (longclick) {

					longclick = false;
					dialog = new Dialog(getActivity());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

					dialog.setContentView(R.layout.remove_item);
					Button delete = (Button) dialog
							.findViewById(R.id.but_delete);
					Button cancel = (Button) dialog
							.findViewById(R.id.but_cancel);
					delete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							view.setVisibility(View.GONE);
							dialog.dismiss();
							Toast.makeText(getActivity(), "Disposed..",
									Toast.LENGTH_LONG).show();

						}
					});
					cancel.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							// Toast.makeText(getActivity(), "Cancel",
							// Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			}
		};
		private int preX;
		private int preY;

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			this.view = view;
			gestureScale.onTouchEvent(event);
			view.bringToFront();
			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				preX = (int) event.getX();
				preY = (int) event.getY();
				timer.start();
				longclick = true;
				LayoutParams lParams = (LayoutParams) view
						.getLayoutParams();
				if ((Boolean) this.view.getTag()) {
					LayoutParams lParams1 = (LayoutParams) view
							.getLayoutParams();
					WindowManager wm = (WindowManager) getActivity()
							.getSystemService(Context.WINDOW_SERVICE);
					Display display = wm.getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);

					int height = size.y;
					int width = size.x;
					lParams1.leftMargin = width - view.getWidth();
					lParams1.topMargin = 0;
					this.view.setTag(false);
					lParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					view.setLayoutParams(lParams1);
					// flag12=0;
				}
				_xDelta = X - lParams.leftMargin;
				_yDelta = Y - lParams.topMargin;
				break;
			case MotionEvent.ACTION_UP:
				longclick = false;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				longclick = false;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				longclick = false;
				break;
			case MotionEvent.ACTION_MOVE:
				int movX = (int) event.getX();
				int movY = (int) event.getY();
				if (((movX - preX)*(movX - preX))+((movY - preY)*(movY - preY)) > 100)
					longclick = false;
				LayoutParams layoutParams = (LayoutParams) view
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
			// scaleFactor = (scaleFactor < 1 ? 1 : scaleFactor);
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
