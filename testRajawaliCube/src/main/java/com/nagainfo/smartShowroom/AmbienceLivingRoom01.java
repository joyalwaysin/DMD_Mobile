package com.nagainfo.smartShowroom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import rajawali.postprocessing.passes.ClearMaskPass;

import uk.co.senab.photoview.PhotoViewAttacher;

import com.nagainfo.database.DatabaseHandler;
import com.nagainfo.slider.PatternGridAdapter;
import com.nagainfo.smartShowroom.LayeredImageView.Layer;
import com.nagainfo.update.PatternimgNameInterface;
import com.nagainfo.utils.FileUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AmbienceLivingRoom01 extends Activity implements
		PatternimgNameInterface, OnClickListener {

	private ActionBarDrawerToggle mDrawerToggle;

	private LayoutInflater inflater;
	private View view;

	// private Dialog wallOrFloorDialog;

	private int touched = 0;
	private final int FLOOR_REQUEST_CODE = 0, WALL_REQUEST_CODE = 1;
	private float wallHeight = (float) 3048.0, wallWidth = (float) 6096.0,
			floorHeight = (float) 3048.0, floorWidth = (float) 6096.0;
//	private LayeredImageView v;
	private Layer layerWall, layerFloor, layerRoomShadows, layerFurniture;
//	private ImageView wallImage, floorImage;
	private LayeredImageView backImg;
	private Bitmap wallBitmap;
	private Bitmap floorBitmap;
	private float scaleFactor;
	private Float density;
	private RelativeLayout layout;

	private GridView grid;

	private Bitmap bmp;
	private AmbienceEditView editView;
	private String tileSize;
	private View patternContent2;
	private EditText eText;
	private Button filterSearch_button;
	private View patternContent;
	private Button filterSearchButton2;
	private LinearLayout patternScrollView;
	private RelativeLayout main_layout;
	private LinearLayout content_slider;
	private RelativeLayout patternHeader;
	private Dialog previewDialog;
	private PhotoViewAttacher mAttacher;
	private static final String KEY_BRAND_NAME = "pro_brand";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_COMPANY = "pro_company";
	private List<Integer> selectedBrands = new ArrayList<Integer>();
	private List<Integer> selectedSize = new ArrayList<Integer>();
	private List<Integer> selectedColor = new ArrayList<Integer>();
	private List<Integer> selectedType = new ArrayList<Integer>();
	private List<Integer> selectedCompany = new ArrayList<Integer>();
	private DrawerLayout leftDrawer;
	private Boolean tileSelected = false;
	private final float wallWidthFinal = (float) 20.5;
	private final float wallHeightFinal = (float) 8.93;
	private String currentProject;
	private String screenshotLocation;
	private String savedFolderName;
	private String viewType;

	private ProgressDialog dialog;
	private Handler hand = new Handler();

	private Boolean customEdit = false;
	private Boolean groovesOn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		OpenCVLoader.initDebug();
		setContentView(R.layout.ambience_drawingroom);
		currentProject = "Ambience";// GlobalVariables.getProjectName();
		savedFolderName = "Ambience Saved Views";
		viewType = "LivingRoom01";
		screenshotLocation = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/SmartShowRoom/"
				+ savedFolderName
				+ "/"
				+ viewType;

		wallWidth = GlobalVariables.feetToMm(wallWidthFinal);
		wallHeight = GlobalVariables.feetToMm(wallHeightFinal);
		floorWidth = GlobalVariables.feetToMm(wallWidthFinal);
		floorHeight = GlobalVariables.feetToMm(wallHeightFinal);

		initialise();
		loadTile();
		clearTempFiles();

		backImg.setImageResource(R.drawable.nothing);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Resources r = getResources();
		Bitmap inputBitmap = BitmapFactory.decodeResource(r,
				R.drawable.living_room_01_wall);
		wallBitmap = inputBitmap.copy(Bitmap.Config.ARGB_8888, true);

		inputBitmap.recycle();
		inputBitmap = null;

		floorBitmap = BitmapFactory.decodeResource(r,
				R.drawable.living_room_01_floor).copy(Bitmap.Config.ARGB_8888,
				true);
		BitmapDrawable wallDrawable = new BitmapDrawable(getResources(),
				warpWall(wallBitmap));
		BitmapDrawable floorDawable = new BitmapDrawable(getResources(),
				warpFloor(floorBitmap));

		// BitmapDrawable draw = new BitmapDrawable(res, inputBitmap)

		wallBitmap.recycle();
		wallBitmap = null;

		floorBitmap.recycle();
		floorBitmap = null;

		Matrix m = new Matrix();

		density = dm.density;
		scaleFactor = (1 / (float) density.intValue());
		m.preScale(scaleFactor, scaleFactor);
		// BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
		// R.drawable.furnitures);

		layerRoomShadows = backImg.addLayer(
				getResources()
						.getDrawable(R.drawable.living_room_01_room_layer), m);

		layerFurniture = backImg.addLayer(
				getResources().getDrawable(
						R.drawable.living_room_01_furniture_layer), m);

		int height = dm.heightPixels;
		int width = (int) ((985 / 693) * height * 1.45);

		layout.setLayoutParams(new RelativeLayout.LayoutParams(width, height));

		Matrix wall = new Matrix();
		wall.preScale(scaleFactor, scaleFactor);
		// wall.preTranslate(dx, dy);
		layerWall = backImg.addLayer(0, wallDrawable, wall);

		Matrix floor = new Matrix();
		floor.preTranslate(-811 * density * scaleFactor, 711 * density
				* scaleFactor);
		floor.preScale(resolutionMultiplier * scaleFactor, resolutionMultiplier
				* scaleFactor);
		layerFloor = backImg.addLayer(1, floorDawable, floor);

		backImg.bringToFront();

		// wallOrFloorDialog.setContentView(layoutResID);

		// final GestureDoubleTap gestureDoubleTap = new GestureDoubleTap();
		// gestureDetector = new GestureDetector(AmbienceDrawingRoom.this,
		// gestureDoubleTap);

		layout.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// return gestureDetector.onTouchEvent(motionEvent);
				// return gestureDoubleTap.onDoubleTap(motionEvent);

				switch (event.getAction() & MotionEvent.ACTION_MASK) {

				case (MotionEvent.ACTION_DOWN):

					if (tileSelected) {

						customEdit = false;

						if (event.getY() < (int) (435 / scaleFactor)) {

							dialog = ProgressDialog.show(
									AmbienceLivingRoom01.this, "",
									"Loading...", true, false);
							Thread thread = new Thread(new Runnable() {

								public void run() {

									final Matrix wall = new Matrix();
									wall.preScale(scaleFactor, scaleFactor);

									final BitmapDrawable wallDrawable = new BitmapDrawable(
											getResources(),
											warpWall(fillTiles(bitmap)));

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											backImg.removeLayer(layerWall);
											layerWall = null;
										}
									});

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											layerWall = backImg.addLayer(0,
													wallDrawable, wall);
										}
									});

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											if (dialog.isShowing())
												dialog.dismiss();

										}

									});
								}

							});

							thread.start();

						} else {

							dialog = ProgressDialog.show(
									AmbienceLivingRoom01.this, "",
									"Loading...", true, false);
							Thread thread = new Thread(new Runnable() {

								public void run() {

									final Matrix floor = new Matrix();
									floor.preTranslate(-811 * density
											* scaleFactor, 711 * density
											* scaleFactor);
									floor.preScale(resolutionMultiplier
											* scaleFactor, resolutionMultiplier
											* scaleFactor);
									final BitmapDrawable floorDrawable = new BitmapDrawable(
											getResources(),
											warpFloor(fillTiles(bitmap)));

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											backImg.removeLayer(layerFloor);
											layerFloor = null;
										}
									});

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											layerFloor = backImg.addLayer(1,
													floorDrawable, floor);
										}
									});

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											if (dialog.isShowing())
												dialog.dismiss();

										}

									});
								}

							});

							thread.start();

						}

					}
					// else {

					else if (customEdit) {

						// if (doubleClick) {
						tileSelected = false;

						// Toast.makeText(getApplicationContext(),
						// event.getY()+"", 1).show();

						if (event.getY() > (int) (435 / scaleFactor)) {

							Intent intent = new Intent(
									AmbienceLivingRoom01.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", floorHeight + "");
							intent.putExtra("Width", floorWidth + "");
							intent.putExtra("FileName", "floor");
							startActivityForResult(intent, FLOOR_REQUEST_CODE);
						} else {

							Intent intent = new Intent(
									AmbienceLivingRoom01.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", wallHeight + "");
							intent.putExtra("Width", wallWidth + "");
							intent.putExtra("FileName", "wall");
							startActivityForResult(intent, WALL_REQUEST_CODE);
						}

						// }
						// touched = 0;
					}

					// }

					return true;
				case (MotionEvent.ACTION_MOVE):

					return true;
				case (MotionEvent.ACTION_UP):
					customEdit = false;
					return true;
				case (MotionEvent.ACTION_CANCEL):

					return true;
				case (MotionEvent.ACTION_OUTSIDE):

					return true;

				default:
					break;
				}

				return true;
			}

		});

		// layout.setOnTouchListener(new View.OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// Log.d("", event.getX() + " : " + event.getY() + " " + touch);
		//
		//
		// return true;
		// }
		// });

		// final DrawerLayout drawer = new
		// android.support.v4.widget.DrawerLayout(this);
		// final FrameLayout fl = new FrameLayout(this);
		// fl.setId(CONTENT_VIEW_ID);
		// final ListView navList = new ListView (this);
		//
		// DrawerLayout.LayoutParams lp = new DrawerLayout.LayoutParams(
		// 100 , LinearLayout.LayoutParams.MATCH_PARENT);
		//
		// lp.gravity=Gravity.START;
		//
		//
		// navList.setLayoutParams(lp);
		//
		// drawer.addView(fl, new
		// FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.MATCH_PARENT));
		// drawer.addView(navList);
		//
		// setContentView(drawer);

		// backImg.setDrawingCacheEnabled(true);
		// Bitmap bmp=backImg.getDrawingCache();
		//
		// String filePath =
		// Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartShowroom/Ambience/";
		//
		// File file = new File(filePath);
		// if(!file.exists()){
		// file.mkdirs();
		// }
		// try {
		// file = new File(filePath+"Screen.png");
		// if(!file.exists()){
		// file.createNewFile();
		// }
		// FileOutputStream fout = new FileOutputStream(file);
		// bmp.compress(Bitmap.CompressFormat.PNG, 100, fout);
		// fout.flush();
		// fout.close();
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void initialise() {

		fillProgress = new ProgressDialog(AmbienceLivingRoom01.this);
		fillProgress.setMessage("Rendering...");
		fillProgress.setCanceledOnTouchOutside(false);

		backImg = (LayeredImageView) findViewById(R.id.imageView);
		layout = (RelativeLayout) findViewById(R.id.viewV);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		leftDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		patternContent2 = inflater.inflate(R.layout.layout_patters, null);
		eText = (EditText) patternContent2.findViewById(R.id.textSearch);

		filterSearch_button = (Button) patternContent2
				.findViewById(R.id.filterButton);
		filterSearch_button.setOnClickListener(AmbienceLivingRoom01.this);

		Button search = (Button) patternContent2
				.findViewById(R.id.searchButton);

		search.setOnClickListener(AmbienceLivingRoom01.this);

		patternContent = inflater.inflate(R.layout.layout_filter, null);
		filterSearchButton2 = (Button) patternContent
				.findViewById(R.id.filterButton2);

		filterSearchButton2.setOnClickListener(AmbienceLivingRoom01.this);

		patternScrollView = (LinearLayout) findViewById(R.id.patternScrollView);
		main_layout = (RelativeLayout) findViewById(R.id.main_content);
		content_slider = (LinearLayout) findViewById(R.id.content_slider);
		patternHeader = (RelativeLayout) findViewById(R.id.patternHeader);

		previewDialog = createPreviewDialog();

		mDrawerToggle = new ActionBarDrawerToggle(this, leftDrawer,
				R.drawable.ic_drawer, R.string.ambience_drawer_open,
				R.string.ambience_drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle("Smart Showroom");
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				// clearAllViews();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle("Select Tiles");
				// loadTile();
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()

			}
		};

		leftDrawer.setDrawerListener(mDrawerToggle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	protected void loadTile() {
		patternScrollView.removeAllViews();

		grid = (GridView) patternContent2.findViewById(R.id.slider_pattern);
		patternScrollView.addView(patternContent2);
		getAllpattern();

	}

	protected void clearAllViews() {
		patternScrollView.removeAllViews();
	}

	private Dialog createPreviewDialog() {
		Dialog dialog = new Dialog(AmbienceLivingRoom01.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// dialog.setTitle("New Contact");//sets the title of the dialog box

		dialog.setContentView(R.layout.preview_dialog);// loads the layout we
														// have

		// dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.custom_title_dialog_box);// sets our custom

		/*
		 * Button closeButton=(Button)dialog.findViewById(R.id.dialog_close);
		 * closeButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub dialog.dismiss(); } });
		 */

		ImageView img = (ImageView) dialog.findViewById(R.id.previewImageView);

		img.setScaleType(ScaleType.CENTER_INSIDE);
		mAttacher = new PhotoViewAttacher(img);
		return dialog;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ambience_menu, menu);
		if (!furnitureVisible)
			menu.getItem(0).setTitle("Show Furniture");
		else
			menu.getItem(0).setTitle("Hide Furniture");

		if (groovesOn) {
			menu.getItem(2).setTitle("GROOVES ON");
		} else {
			menu.getItem(2).setTitle("GROOVES OFF");
		}

		return true;
	}

	Boolean furnitureVisible = true;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		Matrix m = new Matrix();
		m.preScale(scaleFactor, scaleFactor);

		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setDuration(1000);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setDuration(500);

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.furnitureVisiblity:
			if (!furnitureVisible) {
				layerFurniture = backImg.addLayer(
						getResources().getDrawable(
								R.drawable.living_room_01_furniture_layer), m);

				furnitureVisible = true;
				// item.setTitle("Hide Furniture");
				invalidateOptionsMenu();
			} else {
				// layerFurniture.startLayerAnimation(fadeOut);
				backImg.removeLayer(layerFurniture);
				layerFurniture = null;
				furnitureVisible = false;
				// item.setTitle("Show Furniture");
				invalidateOptionsMenu();
			}
			break;
		case R.id.custom_edit:

			tileSelected = false;
			customEdit = true;

			break;

		case R.id.as_image:

			if(FileUtils.isStorageWritable(AmbienceLivingRoom01.this)){
				if(FileUtils.isMemoryAvailable(AmbienceLivingRoom01.this)){
					screenShotPreview();
				}
			}

			break;

		case R.id.grooveSwap:

			if (groovesOn) {
				groovesOn = false;
				invalidateOptionsMenu();
			} else {
				groovesOn = true;
				invalidateOptionsMenu();
			}

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
		// }
	}

	public void screenShotPreview() {

		layout.invalidate();
		layout.setDrawingCacheEnabled(true);
		final Bitmap previewBitmap = layout.getDrawingCache();
		final Dialog previewDialog = new Dialog(AmbienceLivingRoom01.this);
		previewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		previewDialog.setTitle(null);
		previewDialog.setContentView(R.layout.ambience_screenshot_preview);
		Button discard = (Button) previewDialog.findViewById(R.id.discard_shot);
		Button save = (Button) previewDialog.findViewById(R.id.save_shot);
		ImageView preview = (ImageView) previewDialog
				.findViewById(R.id.preview);
		preview.setImageBitmap(previewBitmap);
		

		discard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				previewDialog.dismiss();
			}
		});
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showFileNameDialog(previewBitmap);

				previewDialog.dismiss();

			}
		});
		previewDialog.show();

	}

	public String fileSaveName = "Default";

	public void showFileNameDialog(final Bitmap previewBitmap) {
		final Dialog fileNameDialog = new Dialog(AmbienceLivingRoom01.this);
		fileNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		fileNameDialog.setContentView(R.layout.enter_save_name);
		Button ok = (Button) fileNameDialog.findViewById(R.id.ok_button);
		final EditText fileNameText = (EditText) fileNameDialog
				.findViewById(R.id.fileName);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		final String time = sdf.format(new Date());

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fileSaveName = fileNameText.getText().toString().trim();
				try{
				if (fileSaveName.length() > 0) {
					File file = new File(screenshotLocation);
					if (!file.exists()) {
						file.mkdirs();
					}
					if (fileSaveName.equals("Default")) {
						file = new File(screenshotLocation + "/" + "View at "
								+ time + ".png");
					} else {
						file = new File(screenshotLocation + "/" + fileSaveName
								+" "+ time + ".png");
					}

					if (!file.exists()) {
						try {
							file.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					try {
						FileOutputStream fout = new FileOutputStream(file);
						previewBitmap.compress(Bitmap.CompressFormat.PNG, 100,
								fout);
						fout.flush();
						fout.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FileUtils.writeErrorMessageToFile(e, "LivingRoom01 Save Error-IOException");
					} catch (Exception e){
						FileUtils.writeErrorMessageToFile(e, "LivingRoom01 Save Error");
					}

					fileNameDialog.dismiss();
					showLocationDialog(screenshotLocation);
				} else {
					Toast.makeText(getApplicationContext(),
							"Field cannot be left Blank", Toast.LENGTH_LONG).show();
				}
				}catch(Exception e){
					FileUtils.writeErrorMessageToFile(e, "LivingRoom01 Save Error");
				}
			}
		});
		fileNameDialog.show();
	}

	public void showLocationDialog(String myDir) {
		final Dialog d = new Dialog(AmbienceLivingRoom01.this);
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
	}

	public Bitmap fillTiles(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceLivingRoom01.this);
		float screenWidthRatio = (float) 1.75;
		float wallWidthRatio = (float) (wallHeightFinal / wallWidthFinal);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / wallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / wallWidthFinal) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / wallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / wallWidthFinal) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / wallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / wallWidthFinal) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / wallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / wallWidthFinal) * wallWidthPixels);
			}

		}

		NinePatchDrawable npd = (NinePatchDrawable) getResources().getDrawable(
				R.drawable.imgbckgnd);

		// NinePatchDrawable patch =
		// (NinePatchDrawable)Resources.getDrawable(R.drawable.groove1px);
		// NinePatchDrawable ninePatchGroove = new Nine
		// Bitmap groove = BitmapFactory.decodeResource(getResources(),
		// R.drawable.groove1px);
		// Bitmap scaledGroove = Bitmap.createScaledBitmap(groove,
		// (int) tileWidthPixels, (int) tileHeightPixels, true);

		Bitmap bit = Bitmap.createScaledBitmap(tile, (int) tileWidthPixels,
				(int) tileHeightPixels, true);

		// paint.setColor(Color.RED);
		Bitmap outputBitmap = Bitmap.createBitmap((int) wallWidthPixels,
				(int) wallHeightPixels, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(outputBitmap);
		// canvas.drawRect(0, 0, 10, 10, paint);
		while (left < wallWidthPixels) {

			while (top < wallHeightPixels) {
				canvas.drawBitmap(bit, left, top, null);
				Rect npdBounds = new Rect((int) left, (int) top,
						(int) (left + tileWidthPixels),
						(int) (top + tileHeightPixels));
				npd.setBounds(npdBounds);
				if (groovesOn)
					npd.draw(canvas);
				top += tileHeightPixels;
			}
			left += tileWidthPixels;
			top = 0;
		}

		return outputBitmap;
	}

	public Bitmap warpWall(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point(0, (int) 0);
		Point leftP2 = new Point(0, (int) 690 * density);
		Point leftP3 = new Point((int) 1584 * density, (int) 690 * density);
		Point leftP4 = new Point((int) 1584 * density, (int) 0);
		// Point leftP1 = new Point(0, (int) 19 * density);
		// Point leftP2 = new Point(0, (int) 231 * density);
		// Point leftP3 = new Point((int) 195 * density, (int) 211 * density);
		// Point leftP4 = new Point((int) 195 * density, (int) 75 * density);

		// Step 2: Bounding Box Calculation
		int minLeftX = (int) Math.min(Math.min(leftP1.x, leftP2.x),
				Math.min(leftP3.x, leftP4.x));
		int minLeftY = (int) Math.min(Math.min(leftP1.y, leftP2.y),
				Math.min(leftP3.y, leftP4.y));
		int maxLeftX = (int) Math.max(Math.max(leftP1.x, leftP2.x),
				Math.max(leftP3.x, leftP4.x));
		int maxLeftY = (int) Math.max(Math.max(leftP1.y, leftP2.y),
				Math.max(leftP3.y, leftP4.y));

		// Step 3: Match ratios of Bounding Box and Canvas Image
		float leftImageRatio = leftWallImage.getWidth()
				/ leftWallImage.getHeight();
		float leftBBWidth = (maxLeftX - minLeftX) * leftImageRatio;
		Point leftbbP1 = new Point(minLeftX, minLeftY);
		Point leftbbP2 = new Point(minLeftX, maxLeftY);
		Point leftbbP3 = new Point(minLeftX + leftBBWidth, maxLeftY);
		Point leftbbP4 = new Point(minLeftX + leftBBWidth, minLeftY);

		// Step 4: Offset to Zero base
		leftbbP1 = new Point(0, 0);
		leftbbP2 = new Point(0, maxLeftY - minLeftY);
		leftbbP3 = new Point(leftBBWidth, maxLeftY - minLeftY);
		leftbbP4 = new Point(leftBBWidth, 0);

		// Step 5: Warp Image
		Bitmap tempLeftCanvasImage = Bitmap.createScaledBitmap(leftWallImage,
				(int) leftBBWidth, maxLeftY - minLeftY, true);

		leftWallImage.recycle();
		leftWallImage = null;

		Mat inputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Utils.bitmapToMat(tempLeftCanvasImage, inputMatLeft);

		tempLeftCanvasImage.recycle();
		tempLeftCanvasImage = null;

		Mat outputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Point ocvPLeftIn1 = new Point(0, 0);
		Point ocvPLeftIn2 = new Point(0, maxLeftY - minLeftY);
		Point ocvPLeftIn3 = new Point(leftBBWidth, maxLeftY - minLeftY);
		Point ocvPLeftIn4 = new Point(leftBBWidth, 0);
		List<Point> sourceLeft = new ArrayList<Point>();
		sourceLeft.add(ocvPLeftIn1);
		sourceLeft.add(ocvPLeftIn2);
		sourceLeft.add(ocvPLeftIn3);
		sourceLeft.add(ocvPLeftIn4);
		Mat startMLeft = Converters.vector_Point2f_to_Mat(sourceLeft);

		Point ocvPLeftOut1 = new Point((int) (leftP1.x) - minLeftX,
				(int) (leftP1.y) - minLeftY);
		Point ocvPLeftOut2 = new Point((int) (leftP2.x) - minLeftX,
				(int) (leftP2.y) - minLeftY);
		Point ocvPLeftOut3 = new Point((int) (leftP3.x) - minLeftX,
				(int) (leftP3.y) - minLeftY);
		Point ocvPLeftOut4 = new Point((int) (leftP4.x) - minLeftX,
				(int) (leftP4.y) - minLeftY);
		List<Point> destLeft = new ArrayList<Point>();
		destLeft.add(ocvPLeftOut1);
		destLeft.add(ocvPLeftOut2);
		destLeft.add(ocvPLeftOut3);
		destLeft.add(ocvPLeftOut4);
		Mat endMLeft = Converters.vector_Point2f_to_Mat(destLeft);

		Mat perspectiveTransformLeft = Imgproc.getPerspectiveTransform(
				startMLeft, endMLeft);

		Imgproc.warpPerspective(inputMatLeft, outputMatLeft,
				perspectiveTransformLeft, new Size((int) leftBBWidth, maxLeftY
						- minLeftY), Imgproc.INTER_CUBIC);

		Bitmap outputLeft = Bitmap.createBitmap((int) leftBBWidth, maxLeftY
				- minLeftY, Bitmap.Config.ARGB_8888);

		Utils.matToBitmap(outputMatLeft, outputLeft);
		// leftWall.setImageBitmap(outputLeft);

		// saveImage(outputRight);

		System.gc();
		return outputLeft;

	}

	ProgressDialog fillProgress;
	final float resolutionMultiplier = (float) 1.75;

	public Bitmap warpFloor(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 0, (int) (712 / resolutionMultiplier)
				* density);
		Point leftP2 = new Point((int) (-811 / resolutionMultiplier) * density,
				(int) (950 / resolutionMultiplier) * density);
		Point leftP3 = new Point((int) (2393 / resolutionMultiplier) * density,
				(int) (950 / resolutionMultiplier) * density);
		Point leftP4 = new Point((int) (1582 / resolutionMultiplier) * density,
				(int) (712 / resolutionMultiplier) * density);

		// Point leftP1 = new Point((int) 0, (int) 356 * density);
		// Point leftP2 = new Point((int) -406 * density, (int) 475 * density);
		// Point leftP3 = new Point((int) 1197 * density, (int) 475 * density);
		// Point leftP4 = new Point((int) 791 * density, (int) 356 * density);

		// Step 2: Bounding Box Calculation
		int minLeftX = (int) Math.min(Math.min(leftP1.x, leftP2.x),
				Math.min(leftP3.x, leftP4.x));
		int minLeftY = (int) Math.min(Math.min(leftP1.y, leftP2.y),
				Math.min(leftP3.y, leftP4.y));
		int maxLeftX = (int) Math.max(Math.max(leftP1.x, leftP2.x),
				Math.max(leftP3.x, leftP4.x));
		int maxLeftY = (int) Math.max(Math.max(leftP1.y, leftP2.y),
				Math.max(leftP3.y, leftP4.y));

		// Step 3: Match ratios of Bounding Box and Canvas Image
		float leftImageRatio = leftWallImage.getWidth()
				/ leftWallImage.getHeight();
		float leftBBWidth = (maxLeftX - minLeftX) * leftImageRatio;
		Point leftbbP1 = new Point(minLeftX, minLeftY);
		Point leftbbP2 = new Point(minLeftX, maxLeftY);
		Point leftbbP3 = new Point(minLeftX + leftBBWidth, maxLeftY);
		Point leftbbP4 = new Point(minLeftX + leftBBWidth, minLeftY);

		// Step 4: Offset to Zero base
		leftbbP1 = new Point(0, 0);
		leftbbP2 = new Point(0, maxLeftY - minLeftY);
		leftbbP3 = new Point(leftBBWidth, maxLeftY - minLeftY);
		leftbbP4 = new Point(leftBBWidth, 0);

		// Step 5: Warp Image
		Bitmap tempLeftCanvasImage = Bitmap.createScaledBitmap(leftWallImage,
				(int) leftBBWidth, maxLeftY - minLeftY, true);

		leftWallImage.recycle();
		leftWallImage = null;

		Mat inputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Utils.bitmapToMat(tempLeftCanvasImage, inputMatLeft);

		tempLeftCanvasImage.recycle();
		tempLeftCanvasImage = null;

		Mat outputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Point ocvPLeftIn1 = new Point(0, 0);
		Point ocvPLeftIn2 = new Point(0, maxLeftY - minLeftY);
		Point ocvPLeftIn3 = new Point(leftBBWidth, maxLeftY - minLeftY);
		Point ocvPLeftIn4 = new Point(leftBBWidth, 0);
		List<Point> sourceLeft = new ArrayList<Point>();
		sourceLeft.add(ocvPLeftIn1);
		sourceLeft.add(ocvPLeftIn2);
		sourceLeft.add(ocvPLeftIn3);
		sourceLeft.add(ocvPLeftIn4);
		Mat startMLeft = Converters.vector_Point2f_to_Mat(sourceLeft);

		Point ocvPLeftOut1 = new Point((int) (leftP1.x) - minLeftX,
				(int) (leftP1.y) - minLeftY);
		Point ocvPLeftOut2 = new Point((int) (leftP2.x) - minLeftX,
				(int) (leftP2.y) - minLeftY);
		Point ocvPLeftOut3 = new Point((int) (leftP3.x) - minLeftX,
				(int) (leftP3.y) - minLeftY);
		Point ocvPLeftOut4 = new Point((int) (leftP4.x) - minLeftX,
				(int) (leftP4.y) - minLeftY);
		List<Point> destLeft = new ArrayList<Point>();
		destLeft.add(ocvPLeftOut1);
		destLeft.add(ocvPLeftOut2);
		destLeft.add(ocvPLeftOut3);
		destLeft.add(ocvPLeftOut4);
		Mat endMLeft = Converters.vector_Point2f_to_Mat(destLeft);

		Mat perspectiveTransformLeft = Imgproc.getPerspectiveTransform(
				startMLeft, endMLeft);

		Imgproc.warpPerspective(inputMatLeft, outputMatLeft,
				perspectiveTransformLeft, new Size((int) leftBBWidth, maxLeftY
						- minLeftY), Imgproc.INTER_CUBIC);

		Bitmap outputLeft = Bitmap.createBitmap((int) leftBBWidth, maxLeftY
				- minLeftY, Bitmap.Config.ARGB_8888);

		Utils.matToBitmap(outputMatLeft, outputLeft);
		// leftWall.setImageBitmap(outputLeft);

		// saveImage(outputRight);
		System.gc();
		return outputLeft;

	}

	// final int RESULT_CANCELED = -1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_CANCELED) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		if (requestCode == WALL_REQUEST_CODE) {
			backImg.removeLayer(layerWall);
			layerWall = null;
			Bitmap wallBitmap = EditAmbienceWall.bitmap;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpWall(wallBitmap));
			wallBitmap.recycle();
			wallBitmap = null;
			Matrix wall = new Matrix();
			wall.preScale(scaleFactor, scaleFactor);
			layerWall = backImg.addLayer(0, draw, wall);

		} else if (requestCode == FLOOR_REQUEST_CODE) {
			backImg.removeLayer(layerFloor);
			layerFloor = null;
			Bitmap floorBitmap = EditAmbienceWall.bitmap;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpFloor(floorBitmap));
			floorBitmap.recycle();
			floorBitmap = null;
			Matrix floor = new Matrix();
			floor.preTranslate(-811 * density * scaleFactor, 711 * density
					* scaleFactor);
			floor.preScale(resolutionMultiplier * scaleFactor,
					resolutionMultiplier * scaleFactor);
			layerFloor = backImg.addLayer(1, draw, floor);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	private class GestureDoubleTap extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// some logic
			showToast(e.getX() + " : " + e.getY());
			return true;
		}

	}

	private void getAllpattern() {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		dbResult = db.getAllPattern();

		PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
				AmbienceLivingRoom01.this);
		grid.refreshDrawableState();
		pgAdapter.notifyDataSetChanged();
		// grid.setAdapter(adapter);
		grid.setAdapter(null);
		grid.setAdapter(pgAdapter);

	}

	@Override
	public void patternName(String path, final String tileSize, String brand,
			String type) {

		if (tileSize != null) {

			String[] tileDim = tileSize.split("x");
			if (tileDim.length == 0) {
				tileDim = tileSize.split("X");
			}
			tileHeight = Float.valueOf(tileDim[0]);
			tileWidth = Float.valueOf(tileDim[1]);

		}
		// TODO Auto-generated method stub

		// System.out.println("hai");
		final String filePath = path;
		bmp = BitmapFactory.decodeFile(filePath);
		// drawToolSelected = false;
		// editView.isDrawingSelected(drawToolSelected);
		// editView.selectTile(true);
		this.tileSize = tileSize;
		final Dialog dialog = new Dialog(AmbienceLivingRoom01.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialog.setCanceledOnTouchOutside(false);
		dialog.setTitle("Rotate");// sets the title of the dialog box

		dialog.setContentView(R.layout.tile_rotation_layout);// loads the layout
		// we have

		dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title_dialog_box);// sets our custom
		TextView tileNameView = (TextView) dialog.findViewById(R.id.tileName);
		TextView tileSizeView = (TextView) dialog.findViewById(R.id.tDim);
		TextView tileBrandNameView = (TextView) dialog
				.findViewById(R.id.brandname);
		TextView tileTypeView = (TextView) dialog.findViewById(R.id.tileType);

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
				.findViewById(R.id.tileImageView);
		imgV.setImageBitmap(bmp);
		Button rightRotButton = (Button) dialog
				.findViewById(R.id.rotRightButton);
		rightRotButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, 90);
				axis++;
				// bmp.recycle();
				bmp = rBmp;
				imgV.setImageBitmap(bmp);
				bitmap = bmp;
				// editView.setSelectedTile(bmp, filePath, axis, tileSize);
				// rBmp.recycle();
			}
		});
		Button leftRotButton = (Button) dialog.findViewById(R.id.rotLeftButton);
		leftRotButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, -90);
				axis--;
				// bmp.recycle();
				bmp = rBmp;
				imgV.setImageBitmap(bmp);
				bitmap = bmp;
				// editView.setSelectedTile(bmp, filePath, axis, tileSize);
				// rBmp.recycle();
				// imgV.setImageBitmap(RotateBitmapRight(bmp, 90));
			}
		});

		dialog.show();

		// editView.setSelectedTile(bmp, filePath, axis, tileSize);
		axis = 0;

		bitmap = bmp;
		path = filePath;

		tileSelected = true;

	}

	Bitmap bitmap;
	String path;
	int axis = 0;
	float tileHeight;
	float tileWidth;

	private static Bitmap RotateBitmapRight(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	private void tileSearch() {
		// TODO Auto-generated method stub
		searchpattern(eText.getText().toString());
		eText.setText("");
	}

	private void searchpattern(String keyString) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dbResult = db.searchkeyword(keyString);
		if (dbResult.size() > 0) {
			PatternGridAdapter pgAdapter = new PatternGridAdapter(this,
					dbResult, AmbienceLivingRoom01.this);
			grid.refreshDrawableState();
			pgAdapter.notifyDataSetChanged();
			// grid.setAdapter(adapter);
			grid.setAdapter(null);
			grid.setAdapter(pgAdapter);
		} else {
			grid.setAdapter(null);
			Toast.makeText(this, "No Tile found !", Toast.LENGTH_LONG).show();
		}
		// System.out.println("test");
	}

	private void filterSelected() {
		patternScrollView.removeAllViews();
		// LayoutInflater inflater = (LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// selectedCompany.clear();
		// selectedBrands.clear();
		// selectedSize.clear();
		// selectedColor.clear();
		// selectedType.clear();
		addCompanyCheckBox(patternContent);
		addbrandCheckBox(patternContent);
		addSizeCheckBox(patternContent);
		addColorCheckBox(patternContent);
		addTypeCheckBox(patternContent);
		patternScrollView.addView(patternContent);

	}

	private void addbrandCheckBox(View patternContent) {
		View v = patternContent;
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.brandCheckBox);
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
			cb.setOnCheckedChangeListener(brandCheckListener);
			ll.addView(cb);
		}

	}

	private void addCompanyCheckBox(View patternContent) {
		View v = patternContent;
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.companyCheckBox);
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
				try {
					int t = (Integer) buttonView.getTag();
					selectedCompany.add(new Integer(t));
				} catch (Exception e) {
					Log.e("company chkd item err", "");
					e.printStackTrace();
				}

			} else {
				selectedCompany.clear();
			}

			if (!isChecked) {
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
				try {
					Integer t = (Integer) buttonView.getTag();
					selectedBrands.add(t);
				} catch (Exception e) {
					Log.e("brand checkd item error", "");
					e.printStackTrace();
				}

			} else {
				selectedBrands.clear();
			}
			if (!isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedBrands.remove(new Integer(t));
			}

		}

	};

	private void addSizeCheckBox(View v) {
		LinearLayout l = (LinearLayout) v.findViewById(R.id.sizeCheckbox);
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
				selectedSize.add(new Integer(t));
			} else {
				selectedSize.clear();
			}
			if (!isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedSize.remove(new Integer(t));
			}

		}
	};

	private void addColorCheckBox(View v) {
		LinearLayout l = (LinearLayout) v.findViewById(R.id.colorCheckBox);
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
				selectedColor.add(new Integer(t));

			} else {
				selectedColor.clear();
			}
			if (!isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedColor.remove(new Integer(t));
			}

		}
	};

	private void addTypeCheckBox(View v) {
		LinearLayout l = (LinearLayout) v.findViewById(R.id.typeCheckBox);
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
			if (!isChecked) {
				int t = (Integer) buttonView.getTag();
				selectedType.remove(new Integer(t));
			}

		}
	};

	private void applyFilter() {
		// patternScrollView.removeAllViews();
		// selectedCompany.clear();
		// selectedBrands.clear();
		// selectedSize.clear();
		// selectedColor.clear();
		// selectedType.clear();
		// loadTile();
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
			Toast.makeText(getApplicationContext(), "No tiles found! ",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (filterResult.size() > 0) {
			selectedCompany.clear();
			selectedBrands.clear();
			selectedSize.clear();
			selectedColor.clear();
			selectedType.clear();

			// View patternContent3 = inflater.inflate(R.layout.layout_patters,
			// null);

			grid = (GridView) patternContent2.findViewById(R.id.slider_pattern);
			patternScrollView.removeAllViews();
			patternScrollView.addView(patternContent2);
			showPattern(filterResult);
		}

		// System.out.println("test");

	}

	private void showPattern(ArrayList<HashMap<String, String>> dbResult) {

		PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
				AmbienceLivingRoom01.this);
		grid.refreshDrawableState();
		pgAdapter.notifyDataSetChanged();
		// grid.setAdapter(adapter);
		grid.setAdapter(null);
		grid.setAdapter(pgAdapter);

	}

	private String getStringFromCheckedList(
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
					Log.e("getStringFrmChkdLst!", e.getMessage());
					return null;

				}
			}
		}
		return resString;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.searchButton: {
			tileSearch();
			break;
		}
		case R.id.filterButton: {
			filterSelected();
			break;
		}
		case R.id.filterButton2: {
			applyFilter();
			break;
		}

		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		clearTempFiles();

		finish();

	}

	private void clearTempFiles() {
		String currentProject = "Ambience";// GlobalVariables.getProjectName();

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/" + currentProject + "/";
		DatabaseHandler.deleteAmbienceFiles(path);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		layout.setOnTouchListener(null);
		System.gc();

	}

}
