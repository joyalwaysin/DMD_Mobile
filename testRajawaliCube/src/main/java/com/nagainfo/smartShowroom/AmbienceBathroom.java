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

import uk.co.senab.photoview.PhotoViewAttacher;

import com.nagainfo.database.DatabaseHandler;
import com.nagainfo.slider.PatternGridAdapter;
import com.nagainfo.smartShowroom.LayeredImageView.Layer;
import com.nagainfo.sromku.polygon.Polygon;
import com.nagainfo.update.PatternimgNameInterface;
import com.nagainfo.utils.FileUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
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
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;

public class AmbienceBathroom extends Activity implements
		PatternimgNameInterface, OnClickListener {

	private LayeredImageView backImg;
	private RelativeLayout layout;
	private Bitmap componentsBitmap, rightWallBitmap, backWallBitmap;
	private Bitmap leftWallBitmap, floorBitmap, shadowBitmap, tubFrontBitmap,
			tubSideBitmap;
	private Layer layerLeftWall, layerFloor, layerTubFront, layerShadows,
			layerTubSide, layerRightWall, layerBackWall, layerComponents;
	private float scaleFactor;
	private Float density;

	private float wallLength = (float) 10;
	private DrawerLayout leftDrawer;
	private LayoutInflater inflater;
	private View patternContent2;
	private EditText eText;
	private Button filterSearch_button;
	private View patternContent;
	private Button filterSearchButton2;
	private LinearLayout patternScrollView;
//	private RelativeLayout main_layout;
//	private LinearLayout content_slider;
//	private RelativeLayout patternHeader;
//	private Dialog previewDialog;
	private ActionBarDrawerToggle mDrawerToggle;
//	private PhotoViewAttacher mAttacher;
	private GridView grid;
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

	private float wallHeight = (float) 3048.0, wallWidth = (float) 6096.0,
			floorHeight = (float) 3048.0, floorWidth = (float) 6096.0;
	private String currentProject;
	private String screenshotLocation;
	private String savedFolderName;
	private String viewType;
	private float tubHeight, tubWidth;
	private BitmapDrawable componentsDrawable;

	private ProgressDialog dialog;
	private Handler hand = new Handler();

	private Boolean customEdit = false;

	private Boolean groovesOn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ambience_drawingroom);
		OpenCVLoader.initDebug();
		initialise();
		loadTile();
		clearTempFiles();

		currentProject = "Ambience";// GlobalVariables.getProjectName();
		savedFolderName = "Ambience Saved Views";
		viewType = "Bathroom01";
		screenshotLocation = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/SmartShowRoom/"
				+ savedFolderName
				+ "/"
				+ viewType;

		wallWidth = GlobalVariables.feetToMm(wallWidthFinal);
		wallHeight = GlobalVariables.feetToMm(wallHeightFinal);
		floorWidth = GlobalVariables.feetToMm(wallWidthFinal);
		floorHeight = GlobalVariables.feetToMm(wallWidthFinal);
		wallLength = GlobalVariables.feetToMm(wallLengthFinal);
		tubHeight = GlobalVariables.feetToMm(tubHeightFinal);
		tubWidth = GlobalVariables.feetToMm(tubWidthFinal);

		backImg.setImageResource(R.drawable.bathroom_01_nothing);
		// backImg.setImageResource(R.drawable.)

		rightWallBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_wall_right);
		backWallBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_wall_center);
		leftWallBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_wall_left);
		floorBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_floor);
		// R.drawable.bathroom_01_nothing);

		componentsBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_components);
		// R.drawable.bathroom_01_nothing);
		shadowBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_shadows);
		// R.drawable.bathroom_01_nothing);

		tubFrontBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_wall_bath_front);
		// R.drawable.bathroom_01_nothing);
		tubSideBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bathroom_01_wall_bath_side);
		// R.drawable.bathroom_01_nothing);

		BitmapDrawable leftWallDrawable = new BitmapDrawable(getResources(),
				leftWallBitmap);
		BitmapDrawable rightWallDrawable = new BitmapDrawable(getResources(),
				rightWallBitmap);
		BitmapDrawable backWallDrawable = new BitmapDrawable(getResources(),
				backWallBitmap);
		BitmapDrawable floorDawable = new BitmapDrawable(getResources(),
				floorBitmap);
		BitmapDrawable tubFrontDrawable = new BitmapDrawable(getResources(),
				tubFrontBitmap);
		BitmapDrawable shadowDrawable = new BitmapDrawable(getResources(),
				shadowBitmap);
		BitmapDrawable tubSideDrawable = new BitmapDrawable(getResources(),
				tubSideBitmap);
		componentsDrawable = new BitmapDrawable(getResources(),
				componentsBitmap);

		leftWallBitmap = null;
		rightWallBitmap = null;
		backWallBitmap = null;
		floorBitmap = null;
		tubFrontBitmap = null;
		shadowBitmap = null;
		componentsBitmap = null;
		tubSideBitmap = null;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Matrix m = new Matrix();

		density = dm.density;
		scaleFactor = (1 / (float) density.intValue());
		m.preScale(scaleFactor, scaleFactor);

		layerBackWall = backImg.addLayer(0, backWallDrawable, m);
		layerLeftWall = backImg.addLayer(1, leftWallDrawable, m);
		layerRightWall = backImg.addLayer(2, rightWallDrawable, m);

		layerFloor = backImg.addLayer(3, floorDawable, m);
		layerComponents = backImg.addLayer(4, componentsDrawable, m);
		layerTubSide = backImg.addLayer(5, tubSideDrawable, m);
		layerTubFront = backImg.addLayer(6, tubFrontDrawable, m);
		layerShadows = backImg.addLayer(7, shadowDrawable, m);

		final Polygon leftWallPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								167 / scaleFactor, 80 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								171 / scaleFactor, 342 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
								363 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
								0 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(81 / scaleFactor,
								0 / scaleFactor)).build();

		final Polygon backWallPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								171 / scaleFactor, 80 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								171 / scaleFactor, 342 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								443 / scaleFactor, 342 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								443 / scaleFactor, 432 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								699 / scaleFactor, 430 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								693 / scaleFactor, 78 / scaleFactor)).build();

		final Polygon rightWallPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								699 / scaleFactor, 430 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								693 / scaleFactor, 78 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								778 / scaleFactor, 0 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								883 / scaleFactor, 0 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								883 / scaleFactor, 532 / scaleFactor)).build();

		final Polygon tubPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(9 / scaleFactor,
								370 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								434 / scaleFactor, 382 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								434 / scaleFactor, 510 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(10 / scaleFactor,
								498 / scaleFactor)).build();

		final Polygon floorPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								704 / scaleFactor, 430 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								894 / scaleFactor, 532 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								894 / scaleFactor, 596 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
								596 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
								502 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(9 / scaleFactor,
								498 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								436 / scaleFactor, 510 / scaleFactor))
				.addVertex(
						new com.nagainfo.sromku.polygon.Point(
								450 / scaleFactor, 433 / scaleFactor)).build();

		layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				com.nagainfo.sromku.polygon.Point touch = new com.nagainfo.sromku.polygon.Point(
						event.getX(), event.getY());

				switch (event.getAction() & MotionEvent.ACTION_MASK) {

				case (MotionEvent.ACTION_DOWN):

					if (tileSelected) {

						customEdit = false;

						if (leftWallPolygon.contains(touch)) {

							dialog = ProgressDialog.show(AmbienceBathroom.this,
									"", "Loading...", true, false);
							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable leftWallDrawable = new BitmapDrawable(
											getResources(),
											warpLeftWall(fillTilesOnLeftWall(bitmap)));
									final Matrix mat = new Matrix();
									mat.preScale(scaleFactor, scaleFactor);
									mat.preTranslate(0 * density, -75 * density);

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											backImg.removeLayer(layerLeftWall);
											layerLeftWall = null;
										}
									});

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											layerLeftWall = backImg.addLayer(1,
													leftWallDrawable, mat);
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

						} else if (backWallPolygon.contains(touch)) {

							dialog = ProgressDialog.show(AmbienceBathroom.this,
									"", "Loading...", true, false);
							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable backWallDrawable = new BitmapDrawable(
											getResources(),
											warpBackWall(fillTilesOnBackWall(bitmap)));
									final Matrix matBack = new Matrix();
									matBack.preScale(scaleFactor, scaleFactor);
									matBack.preTranslate(191 * density,
											76 * density);

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											backImg.removeLayer(layerBackWall);
											layerBackWall = null;
										}
									});

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											layerBackWall = backImg.addLayer(0,
													backWallDrawable, matBack);
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

						} else if (rightWallPolygon.contains(touch)) {

							dialog = ProgressDialog.show(AmbienceBathroom.this,
									"", "Loading...", true, false);
							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable rightWallDrawable = new BitmapDrawable(
											getResources(),
											warpRightWall(fillTilesOnRightWall(bitmap)));
									final Matrix matRight = new Matrix();
									matRight.preScale(scaleFactor, scaleFactor);
									matRight.preTranslate(783 * density, -134
											* density);

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											backImg.removeLayer(layerRightWall);
											layerRightWall = null;
										}
									});

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											layerRightWall = backImg.addLayer(
													2, rightWallDrawable,
													matRight);
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

						} else if (floorPolygon.contains(touch)) {

							dialog = ProgressDialog.show(AmbienceBathroom.this,
									"", "Loading...", true, false);
							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable floorDrawable = new BitmapDrawable(
											getResources(),
											warpFloor(fillTilesOnFloor(bitmap)));
									final Matrix matFloor = new Matrix();
									matFloor.preScale(scaleFactor, scaleFactor);
									matFloor.preTranslate(-207 * density,
											482 * density);

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
											layerFloor = backImg.addLayer(3,
													floorDrawable, matFloor);
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

						} else if (tubPolygon.contains(touch)) {

							dialog = ProgressDialog.show(AmbienceBathroom.this,
									"", "Loading...", true, false);
							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable draw1 = new BitmapDrawable(
											getResources(),
											warpTubFront(fillTilesOnTub(bitmap)));
									final Matrix matTubFront = new Matrix();
									matTubFront.preScale(scaleFactor,
											scaleFactor);
									matTubFront.preTranslate(10 * density,
											412 * density);

									final BitmapDrawable draw2 = new BitmapDrawable(
											getResources(),
											warpTubSide(fillTilesOnTub(bitmap)));

									final Matrix matTubSide = new Matrix();

									matTubSide.preScale(scaleFactor,
											scaleFactor);
									matTubSide.preTranslate(490 * density,
											386 * density);

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub

											backImg.removeLayer(layerTubSide);
											backImg.removeLayer(layerTubFront);
											layerTubSide = null;
											layerTubFront = null;

										}
									});

									hand.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											layerTubSide = backImg.addLayer(5,
													draw2, matTubSide);
											layerTubFront = backImg.addLayer(6,
													draw1, matTubFront);
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

							// BitmapDrawable draw = new BitmapDrawable(
							// getResources(),
							// warpTubFront(fillTilesOnTub(bitmap)));
							// Matrix m = new Matrix();
							// m.preScale(scaleFactor, scaleFactor);
							// m.preTranslate(21 * density, 824 * density);
							//
							//
							//
							//
							//
							// draw = new BitmapDrawable(getResources(),
							// warpTubSide(fillTilesOnTub(bitmap)));
							//
							// m = new Matrix();
							// m.preScale(scaleFactor, scaleFactor);
							// m.preTranslate(981 * density, 772 * density);

						}
					}

					// else {

					else if (customEdit) {

						// if (doubleClick) {
						tileSelected = false;
						// Toast.makeText(getApplicationContext(),
						// event.getY()+"", 1).show();

						if (leftWallPolygon.contains(touch)) {
							// Toast.makeText(getApplicationContext(),
							// "Floor", 0).show();
							Intent intent = new Intent(AmbienceBathroom.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", wallHeight + "");
							intent.putExtra("Width", wallWidth + "");
							intent.putExtra("FileName", "leftWall");
							intent.putExtra("Ratio", "1.25");
							startActivityForResult(intent,
									LEFT_WALL_REQUEST_CODE);
						} else if (backWallPolygon.contains(touch)) {

							// Toast.makeText(getApplicationContext(),
							// "Wall", 0).show();
							Intent intent = new Intent(AmbienceBathroom.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", wallHeight + "");
							intent.putExtra("Width", wallWidth + "");
							intent.putExtra("FileName", "backWall");
							intent.putExtra("Ratio", "1.25");
							startActivityForResult(intent,
									BACK_WALL_REQUEST_CODE);
						} else if (rightWallPolygon.contains(touch)) {
							Intent intent = new Intent(AmbienceBathroom.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", wallHeight + "");
							intent.putExtra("Width", wallWidth + "");
							intent.putExtra("FileName", "rightWall");
							intent.putExtra("Ratio", "1.25");
							startActivityForResult(intent,
									RIGHT_WALL_REQUEST_CODE);
						} else if (floorPolygon.contains(touch)) {
							Intent intent = new Intent(AmbienceBathroom.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", floorHeight + "");
							intent.putExtra("Width", floorWidth + "");
							intent.putExtra("FileName", "floor");
							intent.putExtra("Ratio", "1");

							startActivityForResult(intent, FLOOR_REQUEST_CODE);
						} else if (tubPolygon.contains(touch)) {
							// Intent intent = new Intent(
							// Bathroom.this,
							// EditAmbienceWall.class);
							// intent.putExtra("Height", wallHeight +
							// "");
							// intent.putExtra("Width", wallWidth + "");
							// intent.putExtra("FileName", "wall");
							// startActivityForResult(intent,
							// RIGHT_WALL_REQUEST_CODE);
						}

						// }
						// touched = 0;
					}

					// }
					// Toast.makeText(getApplicationContext(), event.getX() +
					// ":" + event.getY(), 1).show();
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

		int height = dm.heightPixels;
		int width = (int) ((985 / 693) * height * 1.31);

		layout.setLayoutParams(new RelativeLayout.LayoutParams(width, height
				- (int) (85 * density)));

	}

	final int LEFT_WALL_REQUEST_CODE = 0, BACK_WALL_REQUEST_CODE = 1,
			RIGHT_WALL_REQUEST_CODE = 2, FLOOR_REQUEST_CODE = 3;

	private void clearTempFiles() {
		String currentProject = "Ambience";// GlobalVariables.getProjectName();

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/SmartShowRoom/" + currentProject + "/";
		DatabaseHandler.deleteAmbienceFiles(path);
	}

	Boolean furnitureVisible = true;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		layout.setOnTouchListener(null);
		System.gc();
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

	private void screenShotPreview() {

		layout.invalidate();
		layout.setDrawingCacheEnabled(true);
		final Bitmap previewBitmap = layout.getDrawingCache();
		final Dialog previewDialog = new Dialog(AmbienceBathroom.this);
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

	private String fileSaveName = "Default";

	private void showFileNameDialog(final Bitmap previewBitmap) {
		final Dialog fileNameDialog = new Dialog(AmbienceBathroom.this);
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
								+ " " + time + ".png");
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
						FileUtils.writeErrorMessageToFile(e, "Bathroom Save Error-IOException");
					} catch (Exception e){
						FileUtils.writeErrorMessageToFile(e, "Bathroom Save Error");
					}

					fileNameDialog.dismiss();
					showLocationDialog(screenshotLocation);
				} else {
					Toast.makeText(getApplicationContext(),
							"Field cannot be left Blank", 1).show();
				}
				}catch(Exception e){
					FileUtils.writeErrorMessageToFile(e, "Bathroom Save Error");
				}
			}
		});
		fileNameDialog.show();
	}

	private void showLocationDialog(String myDir) {
		final Dialog d = new Dialog(AmbienceBathroom.this);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_CANCELED) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		if (requestCode == LEFT_WALL_REQUEST_CODE) {
			backImg.removeLayer(layerLeftWall);
			layerLeftWall = null;
			Bitmap wallBitmap = EditAmbienceWall.bitmap;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpLeftWall(wallBitmap));
			wallBitmap.recycle();
			wallBitmap = null;
			Matrix wall = new Matrix();
			wall.preScale(scaleFactor, scaleFactor);
			wall.preTranslate(0 * density, -75 * density);
			layerLeftWall = backImg.addLayer(1, draw, wall);

		} else if (requestCode == FLOOR_REQUEST_CODE) {
			backImg.removeLayer(layerFloor);
			layerFloor = null;
			Bitmap floorBitmap = EditAmbienceWall.bitmap;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpFloor(floorBitmap));
			floorBitmap.recycle();
			floorBitmap = null;
			Matrix floor = new Matrix();
			floor.preTranslate(-207 * density * scaleFactor, 482 * density
					* scaleFactor);
			floor.preScale(scaleFactor, scaleFactor);
			layerFloor = backImg.addLayer(3, draw, floor);

		} else if (requestCode == RIGHT_WALL_REQUEST_CODE) {

			backImg.removeLayer(layerRightWall);
			layerRightWall = null;
			Bitmap rightWallBitmap = EditAmbienceWall.bitmap;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpRightWall(rightWallBitmap));
			rightWallBitmap.recycle();
			rightWallBitmap = null;

			Matrix right = new Matrix();
			right.preTranslate(783 * density * scaleFactor, -134 * density
					* scaleFactor);
			right.preScale(scaleFactor, scaleFactor);

			layerRightWall = backImg.addLayer(2, draw, right);

		} else if (requestCode == BACK_WALL_REQUEST_CODE) {

			backImg.removeLayer(layerBackWall);
			layerBackWall = null;
			Bitmap backWallBitmap = EditAmbienceWall.bitmap;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpBackWall(backWallBitmap));
			backWallBitmap.recycle();
			backWallBitmap = null;

			Matrix back = new Matrix();
			back.preTranslate(191 * density * scaleFactor, 76 * density
					* scaleFactor);
			back.preScale(scaleFactor, scaleFactor);

			layerBackWall = backImg.addLayer(0, draw, back);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Matrix m = new Matrix();

		density = dm.density;
		scaleFactor = (1 / (float) density.intValue());
		m.preScale(scaleFactor, scaleFactor);

		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {

		case R.id.furnitureVisiblity:
			if (!furnitureVisible) {

				layerComponents = backImg.addLayer(4, componentsDrawable, m);
				furnitureVisible = true;
				// item.setTitle("Hide Furniture");
				invalidateOptionsMenu();
			} else {
				backImg.removeLayer(layerComponents);
				layerComponents = null;
				furnitureVisible = false;
				// item.setTitle("Show Furniture");
				invalidateOptionsMenu();
			}
			break;

		case R.id.as_image:
			
			if(FileUtils.isStorageWritable(AmbienceBathroom.this)){
				if (FileUtils.isMemoryAvailable(AmbienceBathroom.this)) {
					screenShotPreview();
				}	
			}
			

			break;

		case R.id.custom_edit:

			tileSelected = false;
			customEdit = true;

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
	}

	private ProgressDialog fillProgress;

	private void initialise() {

		fillProgress = new ProgressDialog(AmbienceBathroom.this);
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
		filterSearch_button.setOnClickListener(AmbienceBathroom.this);

		Button search = (Button) patternContent2
				.findViewById(R.id.searchButton);

		search.setOnClickListener(AmbienceBathroom.this);

		patternContent = inflater.inflate(R.layout.layout_filter, null);
		filterSearchButton2 = (Button) patternContent
				.findViewById(R.id.filterButton2);

		filterSearchButton2.setOnClickListener(AmbienceBathroom.this);

		patternScrollView = (LinearLayout) findViewById(R.id.patternScrollView);
//		main_layout = (RelativeLayout) findViewById(R.id.main_content);
//		content_slider = (LinearLayout) findViewById(R.id.content_slider);
//		patternHeader = (RelativeLayout) findViewById(R.id.patternHeader);
//
//		previewDialog = createPreviewDialog();

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

	protected void loadTile() {
		patternScrollView.removeAllViews();

		grid = (GridView) patternContent2.findViewById(R.id.slider_pattern);
		patternScrollView.addView(patternContent2);
		getAllpattern();

	}

	private void getAllpattern() {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		dbResult = db.getAllPattern();

		PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
				AmbienceBathroom.this);
		grid.refreshDrawableState();
		pgAdapter.notifyDataSetChanged();
		// grid.setAdapter(adapter);
		grid.setAdapter(null);
		grid.setAdapter(pgAdapter);

	}

//	private Dialog createPreviewDialog() {
//		Dialog dialog = new Dialog(AmbienceBathroom.this);
//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		// dialog.setTitle("New Contact");//sets the title of the dialog box
//
//		dialog.setContentView(R.layout.preview_dialog);// loads the layout we
//														// have
//
//		// dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
//		// R.layout.custom_title_dialog_box);// sets our custom
//
//		/*
//		 * Button closeButton=(Button)dialog.findViewById(R.id.dialog_close);
//		 * closeButton.setOnClickListener(new OnClickListener() {
//		 *
//		 * @Override public void onClick(View arg0) { // TODO Auto-generated
//		 * method stub dialog.dismiss(); } });
//		 */
//
//		ImageView img = (ImageView) dialog.findViewById(R.id.previewImageView);
//
//		img.setScaleType(ScaleType.CENTER_INSIDE);
//		mAttacher = new PhotoViewAttacher(img);
//		return dialog;
//
//	}

	// public Bitmap fillTiles(Bitmap tile) {
	// // included only for example sake
	//
	// int viewArea = GlobalVariables.getDrawArea(AmbienceDrawingRoom.this);
	// float screenWidthRatio = (float) 1.75;
	// float wallWidthRatio = (float) (wallHeightFinal / wallWidthFinal);
	//
	// float wallWidthPixels = viewArea * screenWidthRatio;
	// float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;
	//
	// float left = 0, top = 0;
	// int tileHeightPixels = 0;
	// int tileWidthPixels = 0;
	// if (axis % 2 == 0) {
	// tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) /
	// wallWidthFinal) * wallWidthPixels);
	// tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) /
	// wallWidthFinal) * wallWidthPixels);
	// } else {
	// tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) /
	// wallWidthFinal) * wallWidthPixels);
	// tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) /
	// wallWidthFinal) * wallWidthPixels);
	// }
	//
	// // NinePatchDrawable ninePatchGroove = new Nine
	// Bitmap groove = BitmapFactory.decodeResource(getResources(),
	// R.drawable.groove1px);
	// Bitmap scaledGroove = Bitmap.createScaledBitmap(groove,
	// (int) tileWidthPixels, (int) tileHeightPixels, true);
	//
	// Bitmap bit = Bitmap.createScaledBitmap(tile, (int) tileWidthPixels,
	// (int) tileHeightPixels, true);
	//
	// // paint.setColor(Color.RED);
	// Bitmap outputBitmap = Bitmap.createBitmap((int) wallWidthPixels,
	// (int) wallHeightPixels, Bitmap.Config.ARGB_8888);
	// Canvas canvas = new Canvas(outputBitmap);
	// // canvas.drawRect(0, 0, 10, 10, paint);
	// while (left < wallWidthPixels) {
	//
	// while (top < wallHeightPixels) {
	// canvas.drawBitmap(bit, left, top, null);
	// canvas.drawBitmap(scaledGroove, left, top, null);
	// top += tileHeightPixels;
	// }
	// left += tileWidthPixels;
	// top = 0;
	// }
	//
	// return outputBitmap;
	// }

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		clearTempFiles();
		super.onBackPressed();
		System.gc();
	}

	private Bitmap warpBackWall(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 194 * density, (int) 79 * density);
		Point leftP2 = new Point((int) 191 * density, (int) 490 * density);
		Point leftP3 = new Point((int) 790 * density, (int) 493 * density);
		Point leftP4 = new Point((int) 785 * density, (int) 76 * density);
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

	private Bitmap warpLeftWall(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 0 * density, (int) -75 * density);
		Point leftP2 = new Point((int) 0 * density, (int) 560 * density);
		Point leftP3 = new Point((int) 203 * density, (int) 490 * density);
		Point leftP4 = new Point((int) 200 * density, (int) 81 * density);
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

	private Bitmap warpTubSide(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 490 * density, (int) 414 * density);
		Point leftP2 = new Point((int) 491 * density, (int) 568 * density);
		Point leftP3 = new Point((int) 501 * density, (int) 482 * density);
		Point leftP4 = new Point((int) 500 * density, (int) 386 * density);
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

	private Bitmap warpTubFront(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 10 * density, (int) 412 * density);
		Point leftP2 = new Point((int) 10 * density, (int) 556 * density);
		Point leftP3 = new Point((int) 491 * density, (int) 568 * density);
		Point leftP4 = new Point((int) 490 * density, (int) 414 * density);
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

	private Bitmap warpRightWall(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 783 * density, (int) 76 * density);
		Point leftP2 = new Point((int) 789 * density, (int) 485 * density);
		Point leftP3 = new Point((int) 1000 * density, (int) 594 * density);
		Point leftP4 = new Point((int) 1000 * density, (int) -134 * density);
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

	private Bitmap warpFloor(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 165 * density, (int) 484 * density);
		Point leftP2 = new Point((int) -190 * density, (int) 668 * density);
		Point leftP3 = new Point((int) 1166 * density, (int) 668 * density);
		Point leftP4 = new Point((int) 807 * density, (int) 482 * density);
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
					dbResult, AmbienceBathroom.this);
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
					Log.e("company checked item error", "");
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
					Log.e("brand checked item error", "");
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
				AmbienceBathroom.this);
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
					Log.e("getStringFromCheckedList error!", e.getMessage());
					return null;

				}
			}
		}
		return resString;

	}

	private final float wallWidthFinal = (float) 10;
	private final float wallHeightFinal = (float) 8;
	private final float wallLengthFinal = (float) 10;
	private final float tubHeightFinal = (float) 2;
	private final float tubWidthFinal = (float) 5;

	private Bitmap fillTilesOnFloor(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceBathroom.this);
		float screenWidthRatio = (float) 1.75;
		float wallWidthRatio = (float) (wallLengthFinal / wallWidthFinal);

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

	private Bitmap fillTilesOnBackWall(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceBathroom.this);
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

	private Bitmap fillTilesOnLeftWall(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceBathroom.this);
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

	private Bitmap fillTilesOnTub(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceBathroom.this);
		float screenWidthRatio = (float) 1.75;
		float wallWidthRatio = (float) (tubHeightFinal / tubWidthFinal);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / tubWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / tubWidthFinal) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / tubWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / tubWidthFinal) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / tubWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / tubWidthFinal) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / tubWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / tubWidthFinal) * wallWidthPixels);
			}

		}

		NinePatchDrawable npd = (NinePatchDrawable) getResources().getDrawable(
				R.drawable.imgbckgnd);

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

	private Bitmap fillTilesOnRightWall(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceBathroom.this);
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
	public void patternName(String path, String tileSize, String brand,
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
		final Dialog dialog = new Dialog(AmbienceBathroom.this);
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

	private static Bitmap RotateBitmapRight(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	private String tileSize;
	private Bitmap bitmap;
	private String path;
	private int axis = 0;
	private float tileHeight;
	private float tileWidth;
	private Boolean tileSelected = false;
	private Bitmap bmp;
}
