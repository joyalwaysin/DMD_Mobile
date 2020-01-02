package com.nagainfomob.app.DisplayMyDesign;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.androidadvance.topsnackbar.TSnackbar;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.MyRecyclerViewAdapter;
import com.nagainfomob.app.adapter.RecyclerPaternAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.helpers.BadgedTabLayout;
import com.nagainfomob.app.helpers.PatternDetailsInterface;
import com.nagainfomob.app.helpers.RowItem;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
import com.nagainfomob.app.model.LoadTile.LoadTileResult;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.TileModel;
import com.nagainfomob.app.photoview.PhotoViewAttacher;
import com.nagainfomob.app.sql.DatabaseManager;
import com.nagainfomob.app.sromku.polygon.Polygon;
import com.nagainfomob.app.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_NO_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_SLOW_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_UNKNOWN;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_WRONG_JSON_FORMAT;


public class AmbienceBedroom extends AppCompatActivity implements OnClickListener,
		PatternimgNameInterface, PatternDetailsInterface {

	//	private String storageURL = "https://storage-vro.s3.amazonaws.com/";
	private String storageURL = "https://dmd-file.s3.us-east-1.amazonaws.com/";
	private static int downloadingFlag = 0;
	private SweetAlertDialog dDialog;
	DownloadTask downloadTask;
	private SessionManager session;

	private LayeredImageView.Layer layerFloor, layerFurniture, layerShadows, layerLeftWall,
			layerRightWall, layerRoof;
	private Bitmap floorBitmap, furnitureBitmap, shadowBitmap, leftWallBitmap,
			rightWallBitmap, roofBitmap;

	//	private ProgressDialog fillProgress;
	private LayeredImageView backImg;
	private RelativeLayout layout;
	private DrawerLayout leftDrawer;
	private LayoutInflater inflater;
	private View patternContent2;
	private EditText eText;
	private Button filterSearch_button;
	private View patternContent;
	private Button filterSearchButton2;
	private LinearLayout patternScrollView;
	private RelativeLayout main_layout;
	private LinearLayout content_slider;
	private RelativeLayout patternHeader;
	private ActionBarDrawerToggle mDrawerToggle;
	private RelativeLayout error_lay;

	private float scaleFactor;
	private Float density;

	private static float TouchPointX;
	private static float TouchPointY;

	private Boolean furnitureVisible = true;
	private BitmapDrawable furnitureDrawable;

	private GridView grid;
	private static final String KEY_BRAND_NAME = "pro_brand";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_COMPANY = "pro_company";
	private static final String KEY_CATEGORY = "tile_category";

	private Matrix rightWall, leftWall, floor;

	private final float rightWallHeightFinal = (float) 10;
	private final float leftWallHeightFinal = (float) 10;
	private final float rightWallWidthFinal = (float) 17.5;
	private final float leftWallWidthFinal = (float) 10;
	private final float floorHeightFinal = (float) 18;
	private final float floorWidthFinal = (float) 23;

	private float rightWallHeight, rightWallWidth, leftWallHeight,
			leftWallWidth, floorHeight, floorWidth;

	private final int FLOOR_REQUEST_CODE = 0, LEFT_WALL_REQUEST_CODE = 1,
			RIGHT_WALL_REQUEST_CODE = 2;

	private String currentProject;
	private String screenshotLocation;
	private String savedFolderName;
	private String viewType;

	protected ProgressDialog pDialog;
	private Handler hand = new Handler();

	private Boolean customEdit = false;
	private Boolean groovesOn = true;

	private RecyclerView tileView;
	private TextView pattern_name;
	private TextView brand_name;
	private TextView pattern_dim;
	private TextView backToView;
	private TextView error_text;
	private TextView syncLibrary;

	private ImageView rot_left;
	private ImageView pattern_img;
	private ImageView rot_right;
	private ImageView dmd_preview;
	private ImageView dmd_export;
	private ImageView dmd_eraser;

	private LinearLayout view_placeholder;

	List<Integer> selectedBrands = new ArrayList<Integer>();
	List<Integer> selectedSize = new ArrayList<Integer>();
	List<Integer> selectedColor = new ArrayList<Integer>();
	List<Integer> selectedType = new ArrayList<Integer>();
	List<Integer> selectedCategory = new ArrayList<Integer>();
	List<Integer> selectedCompany = new ArrayList<Integer>();

	ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult3 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> dbResult4 = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> filterResult = new ArrayList<HashMap<String, String>>();

	ArrayList<HashMap<String, String>> dbPaginatedList = new ArrayList<HashMap<String, String>>();
	private static int dbOffset = 0;
	private static int dbLimit = 50;
	private static int filterCount = 0;
	private static int searchFlag = 0;
	private MyRecyclerViewAdapter adapter;

	private Dialog dialog;
	private EditText txtSearch;
	private ImageView img_filter;
	private LinearLayout view_errorText;
	private BadgedTabLayout tabLayout;

	private Switch grooveView;
	Dialog previewDialog;
	PhotoViewAttacher mAttacher;
	BitmapDrawable leftWallDrawable;
	BitmapDrawable floorDrawable;
	BitmapDrawable rightWallDrawable;
	Matrix m;

	Boolean libraryTab = true;
	ArrayList<HashMap<String, String>> dbPatternList = new ArrayList<HashMap<String, String>>();
	private RecyclerPaternAdapter patternAdapter;
	Spinner tileSpinner;
	RecyclerView patternView;
	public TextView backToViewPatrn;
	public TextView namePatrn;
	public TextView brandPatrn;
	public TextView dimPatrn;
	public TextView tile_dtl_text;
	public TextView patrn_dtl_text;
	private LinearLayout view_errorText_Pattern;
	public LinearLayout tile_detailed_view;
	public LinearLayout pattern_detailed_view;
	public LinearLayout tileOptions;
	public LinearLayout patternOptions;
	private ImageView rot_left_patrn;
	private ImageView imgPatrn;
	private ImageView rot_right_patrn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		session = new SessionManager(AmbienceBedroom.this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getSupportActionBar().hide();

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.ambience_drawingroom);
		OpenCVLoader.initDebug();

		initialise();
		getAllTiles();
		getAllPatterns();
		clearTempFiles();

		tileView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int lastCompletelyVisibleItemPosition = 0;

				lastCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
				if (lastCompletelyVisibleItemPosition == dbPaginatedList.size() - 1 &&
						lastCompletelyVisibleItemPosition + 1 >= dbLimit) {
					if(searchFlag == 1) return;
					dbOffset = dbPaginatedList.size();
					paginatedbList(dbOffset);
				}
			}
		});

		currentProject = "Ambience";// GlobalVariables.getProjectName();
		savedFolderName = "Ambience Saved Views";
		viewType = "Bedroom01";
		screenshotLocation = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/SmartShowRoom/"
				+ savedFolderName
				+ "/"
				+ viewType;


		rightWallHeight = GlobalVariables.feetToMm(rightWallHeightFinal);
		rightWallWidth = GlobalVariables.feetToMm(rightWallWidthFinal);
		leftWallHeight = GlobalVariables.feetToMm(leftWallHeightFinal);
		leftWallWidth = GlobalVariables.feetToMm(leftWallWidthFinal);
		floorHeight = GlobalVariables.feetToMm(floorHeightFinal);
		floorWidth = GlobalVariables.feetToMm(floorWidthFinal);

		backImg.setImageResource(R.drawable.bedroom_01_nothing);

		floorBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bedroom_01_floor_color).copy(
				Bitmap.Config.ARGB_8888, true);
		furnitureBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bedroom_01_furniture_components);
		leftWallBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bedroom_01_wall_left_color).copy(
				Bitmap.Config.ARGB_8888, true);
		rightWallBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bedroom_01_wall_right_color).copy(
				Bitmap.Config.ARGB_8888, true);
		shadowBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bedroom_01_shadows);
		roofBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.bedroom_01_roof);

		leftWallDrawable = new BitmapDrawable(getResources(),
				warpLeftWall(leftWallBitmap));
		rightWallDrawable = new BitmapDrawable(getResources(),
				warpRightWall(rightWallBitmap));
		furnitureDrawable = new BitmapDrawable(getResources(), furnitureBitmap);
		floorDrawable = new BitmapDrawable(getResources(),
				warpFloor(floorBitmap));
		BitmapDrawable shadowDrawable = new BitmapDrawable(getResources(),
				shadowBitmap);
		BitmapDrawable roofDrawable = new BitmapDrawable(getResources(),
				roofBitmap);

		leftWallBitmap = null;
		rightWallBitmap = null;
		furnitureBitmap = null;
		floorBitmap = null;
		shadowBitmap = null;
		roofBitmap = null;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		m = new Matrix();

		density = dm.density;
		scaleFactor = (1 / (float) density.intValue());
		scaleFactor = 1;
		m.preScale(scaleFactor, scaleFactor);

		int height = dm.heightPixels;
		int width = (int) ((985 / 693) * height * 1.15);

		layout.setLayoutParams(new RelativeLayout.LayoutParams(width, height));

		floor = new Matrix();
		floor.preScale(scaleFactor, scaleFactor);
		floor.preTranslate(-436 * density, 535 * density);

		leftWall = new Matrix();
		leftWall.preScale(scaleFactor, scaleFactor);
		leftWall.preTranslate(0 * density, 83 * density);

		rightWall = new Matrix();
		rightWall.preScale(scaleFactor, scaleFactor);
		rightWall.preTranslate(263 * density, 51 * density);

		layerRoof = backImg.addLayer(0, roofDrawable, m);
		layerFloor = backImg.addLayer(1, floorDrawable, floor);
		layerLeftWall = backImg.addLayer(2, leftWallDrawable, leftWall);
		layerRightWall = backImg.addLayer(3, rightWallDrawable, rightWall);
		layerFurniture = backImg.addLayer(4, furnitureDrawable, m);
		layerShadows = backImg.addLayer(5, shadowDrawable, m);

		final Polygon leftWallPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								62 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								432 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								203 / scaleFactor, 413 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								203 / scaleFactor, 116 / scaleFactor)).build();

		final Polygon rightWallPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								203 / scaleFactor, 116 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								203 / scaleFactor, 413 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								775 / scaleFactor, 443 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								775 / scaleFactor, 41 / scaleFactor)).build();

		final Polygon floorPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								446 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								588 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								776 / scaleFactor, 586 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								776 / scaleFactor, 458 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								203 / scaleFactor, 424 / scaleFactor)).build();

		layout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {

			@Override
			public void onClick() {
				super.onClick();

				com.nagainfomob.app.sromku.polygon.Point touch = new com.nagainfomob.app.sromku.polygon.Point(
						TouchPointX, TouchPointY);

				if (tileSelected) {

					customEdit = false;

					if (floorPolygon.contains(touch)) {

						final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceBedroom.this, SweetAlertDialog.PROGRESS_TYPE)
								.setTitleText("Processing..");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
						pDialog.show();
						pDialog.setCancelable(false);

						Thread thread = new Thread(new Runnable() {

							public void run() {

								final BitmapDrawable floorDrawable1 = new BitmapDrawable(
										getResources(),
										warpFloor(fillTilesOnFloor(bitmap)));
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
												floorDrawable1, floor);
									}
								});

								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (pDialog.isShowing())
											pDialog.dismissWithAnimation();

									}

								});
							}

						});

						thread.start();

					} else if (leftWallPolygon.contains(touch)) {

						final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceBedroom.this, SweetAlertDialog.PROGRESS_TYPE)
								.setTitleText("Processing..");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
						pDialog.show();
						pDialog.setCancelable(false);

						Thread thread = new Thread(new Runnable() {

							public void run() {

								final BitmapDrawable leftWallDrawable1 = new BitmapDrawable(
										getResources(),
										warpLeftWall(fillTilesOnLeftWall(bitmap)));

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
										layerLeftWall = backImg.addLayer(2,
												leftWallDrawable1, leftWall);
									}
								});

								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (pDialog.isShowing())
											pDialog.dismissWithAnimation();

									}

								});
							}

						});

						thread.start();

					} else if (rightWallPolygon.contains(touch)) {

						final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceBedroom.this, SweetAlertDialog.PROGRESS_TYPE)
								.setTitleText("Processing..");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
						pDialog.show();
						pDialog.setCancelable(false);

						Thread thread = new Thread(new Runnable() {

							public void run() {

								final BitmapDrawable rightWallDrawable1 = new BitmapDrawable(
										getResources(),
										warpRightWall(fillTilesOnRightWall(bitmap)));

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
												3, rightWallDrawable1,
												rightWall);
									}
								});

								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										if (pDialog.isShowing())
											pDialog.dismissWithAnimation();

									}

								});
							}

						});

						thread.start();
					}
				}
				else{
					error_lay.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onDoubleClick() {
				super.onDoubleClick();
				com.nagainfomob.app.sromku.polygon.Point touch = new com.nagainfomob.app.sromku.polygon.Point(
						TouchPointX, TouchPointY);

				if (floorPolygon.contains(touch)) {
					GlobalVariables.setProjectName("Edit Wall");
					GlobalVariables.setUnit("Millimeter");
					GlobalVariables.setDesignerUnit(GlobalVariables.getUnit());
					GlobalVariables.setWallDim(Float.parseFloat(String.valueOf(floorHeight)),
							Float.parseFloat(String.valueOf(floorWidth)), 0, 0, 0);

					Intent intent = new Intent(AmbienceBedroom.this, CustomPatternActivity.class);
					intent.putExtra("name", "design");
					intent.putExtra("width", floorWidth + "");
					intent.putExtra("height", floorHeight + "");
					intent.putExtra("category", "");
					intent.putExtra("category_id", "");
					intent.putExtra("price", "");
					intent.putExtra("brand", "");
					intent.putExtra("type", "");
					intent.putExtra("type_id", "");
					intent.putExtra("is_edit", "2");

					intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, FLOOR_REQUEST_CODE);

				} else if (leftWallPolygon.contains(touch)) {

					GlobalVariables.setProjectName("Edit Wall");
					GlobalVariables.setUnit("Millimeter");
					GlobalVariables.setDesignerUnit(GlobalVariables.getUnit());
					GlobalVariables.setWallDim(Float.parseFloat(String.valueOf(leftWallHeight)),
							Float.parseFloat(String.valueOf(leftWallWidth)), 0, 0, 0);

					Intent intent = new Intent(AmbienceBedroom.this, CustomPatternActivity.class);
					intent.putExtra("name", "design");
					intent.putExtra("width", leftWallWidth + "");
					intent.putExtra("height", leftWallHeight + "");
					intent.putExtra("category", "");
					intent.putExtra("category_id", "");
					intent.putExtra("price", "");
					intent.putExtra("brand", "");
					intent.putExtra("type", "");
					intent.putExtra("type_id", "");
					intent.putExtra("is_edit", "2");

					intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, LEFT_WALL_REQUEST_CODE);

				} else if (rightWallPolygon.contains(touch)) {

					GlobalVariables.setProjectName("Edit Wall");
					GlobalVariables.setUnit("Millimeter");
					GlobalVariables.setDesignerUnit(GlobalVariables.getUnit());
					GlobalVariables.setWallDim(Float.parseFloat(String.valueOf(rightWallHeight)),
							Float.parseFloat(String.valueOf(rightWallWidth)), 0, 0, 0);

					Intent intent = new Intent(AmbienceBedroom.this, CustomPatternActivity.class);
					intent.putExtra("name", "design");
					intent.putExtra("width", rightWallWidth + "");
					intent.putExtra("height", rightWallHeight + "");
					intent.putExtra("category", "");
					intent.putExtra("category_id", "");
					intent.putExtra("price", "");
					intent.putExtra("brand", "");
					intent.putExtra("type", "");
					intent.putExtra("type_id", "");
					intent.putExtra("is_edit", "2");

					intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, RIGHT_WALL_REQUEST_CODE);

				}
			}

			@Override
			public void onLongClick() {
				super.onLongClick();
				// your on onLongClick here
			}

			@Override
			public void onSwipeUp() {
				super.onSwipeUp();
				// your swipe up here
			}

			@Override
			public void onSwipeDown() {
				super.onSwipeDown();
				// your swipe down here.
			}

			@Override
			public void onSwipeLeft() {
				super.onSwipeLeft();
				// your swipe left here.
			}

			@Override
			public void onSwipeRight() {
				super.onSwipeRight();
				// your swipe right here.
			}
		});


		/*layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				com.nagainfomob.app.sromku.polygon.Point touch = new com.nagainfomob.app.sromku.polygon.Point(
						event.getX(), event.getY());

				switch (event.getAction() & MotionEvent.ACTION_MASK) {

				case MotionEvent.ACTION_DOWN:

					if (tileSelected) {

						customEdit = false;

						if (floorPolygon.contains(touch)) {

							final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceBedroom.this, SweetAlertDialog.PROGRESS_TYPE)
									.setTitleText("Processing..");
							pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
							pDialog.show();
							pDialog.setCancelable(false);

							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable floorDrawable1 = new BitmapDrawable(
											getResources(),
											warpFloor(fillTilesOnFloor(bitmap)));
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
													floorDrawable1, floor);
										}
									});

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											if (pDialog.isShowing())
												pDialog.dismissWithAnimation();

										}

									});
								}

							});

							thread.start();

						} else if (leftWallPolygon.contains(touch)) {

							final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceBedroom.this, SweetAlertDialog.PROGRESS_TYPE)
									.setTitleText("Processing..");
							pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
							pDialog.show();
							pDialog.setCancelable(false);

							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable leftWallDrawable1 = new BitmapDrawable(
											getResources(),
											warpLeftWall(fillTilesOnLeftWall(bitmap)));

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
											layerLeftWall = backImg.addLayer(2,
													leftWallDrawable1, leftWall);
										}
									});

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											if (pDialog.isShowing())
												pDialog.dismissWithAnimation();

										}

									});
								}

							});

							thread.start();

						} else if (rightWallPolygon.contains(touch)) {

							final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceBedroom.this, SweetAlertDialog.PROGRESS_TYPE)
									.setTitleText("Processing..");
							pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
							pDialog.show();
							pDialog.setCancelable(false);

							Thread thread = new Thread(new Runnable() {

								public void run() {

									final BitmapDrawable rightWallDrawable1 = new BitmapDrawable(
											getResources(),
											warpRightWall(fillTilesOnRightWall(bitmap)));

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
													3, rightWallDrawable1,
													rightWall);
										}
									});

									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											if (pDialog.isShowing())
												pDialog.dismissWithAnimation();

										}

									});
								}

							});

							thread.start();

						}

					}
					else if (customEdit) {

						// if (doubleClick) {
						tileSelected = false;

						if (floorPolygon.contains(touch)) {
							Intent intent = new Intent(AmbienceBedroom.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", floorHeight + "");
							intent.putExtra("Width", floorWidth + "");
							intent.putExtra("FileName", "floor");
							intent.putExtra("Ratio", "1");
							startActivityForResult(intent, FLOOR_REQUEST_CODE);
						} else if (leftWallPolygon.contains(touch)) {
							Intent intent = new Intent(AmbienceBedroom.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", leftWallHeight + "");
							intent.putExtra("Width", leftWallWidth + "");
							intent.putExtra("FileName", "leftWall");
							intent.putExtra("Ratio", "1");
							startActivityForResult(intent,
									LEFT_WALL_REQUEST_CODE);
						} else if (rightWallPolygon.contains(touch)) {
							Intent intent = new Intent(AmbienceBedroom.this,
									EditAmbienceWall.class);
							intent.putExtra("Height", rightWallHeight + "");
							intent.putExtra("Width", rightWallWidth + "");
							intent.putExtra("FileName", "rightWall");

							startActivityForResult(intent,
									RIGHT_WALL_REQUEST_CODE);
						}

						// }

					}
					else{
						error_text.setVisibility(View.VISIBLE);
						error_text.setText("Please select Tile to apply");
					}

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
		});*/

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_CANCELED) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}

		if (requestCode == RIGHT_WALL_REQUEST_CODE) {
			backImg.removeLayer(layerRightWall);
			layerRightWall = null;
			Bitmap wallBitmap = CustomPatternActivity.bitmapforDisplay;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpRightWall(wallBitmap));
			wallBitmap.recycle();
			wallBitmap = null;

			layerRightWall = backImg.addLayer(3, draw, rightWall);

		} else if (requestCode == FLOOR_REQUEST_CODE) {
			backImg.removeLayer(layerFloor);
			layerFloor = null;
			Bitmap floorBitmap = CustomPatternActivity.bitmapforDisplay;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpFloor(floorBitmap));
			floorBitmap.recycle();
			floorBitmap = null;

			layerFloor = backImg.addLayer(1, draw, floor);

		} else if (requestCode == LEFT_WALL_REQUEST_CODE) {
			backImg.removeLayer(layerLeftWall);
			layerLeftWall = null;
			Bitmap floorBitmap = CustomPatternActivity.bitmapforDisplay;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpLeftWall(floorBitmap));
			floorBitmap.recycle();
			floorBitmap = null;

			layerLeftWall = backImg.addLayer(2, draw, leftWall);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		layout.setOnTouchListener(null);
		System.gc();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		clearTempFiles();
		clearMemory();
		finish();
	}

	private void clearTempFiles() {
		String currentProject = "Ambience";// GlobalVariables.getProjectName();

		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SmartShowRoom/" + currentProject + "/";
		DatabaseHandler.deleteAmbienceFiles(path);
	}

	private void initialise() {

		backImg = (LayeredImageView) findViewById(R.id.imageView);
		layout = (RelativeLayout) findViewById(R.id.viewV);
		view_placeholder = (LinearLayout) findViewById(R.id.view_placeholder);

		tileView = (RecyclerView) findViewById(R.id.view_pattern);
		pattern_name = (TextView) findViewById(R.id.pattern_name);
		brand_name = (TextView) findViewById(R.id.brand_name);
		pattern_dim = (TextView) findViewById(R.id.pattern_dim);
		error_text = (TextView) findViewById(R.id.error_text);
		error_lay = (RelativeLayout) findViewById(R.id.error_lay);

		pattern_img = (ImageView) findViewById(R.id.pattern_img);
		rot_left = (ImageView) findViewById(R.id.rot_left);
		rot_left.setOnClickListener(AmbienceBedroom.this);
		rot_right = (ImageView) findViewById(R.id.rot_right);
		rot_right.setOnClickListener(AmbienceBedroom.this);

		syncLibrary = (TextView) findViewById(R.id.syncLibrary);
		syncLibrary.setOnClickListener(AmbienceBedroom.this);

		pattern_detailed_view = (LinearLayout) findViewById(R.id.pattern_detailed_view);
		backToView = (TextView) findViewById(R.id.backToView);
		backToView.setOnClickListener(AmbienceBedroom.this);

		dmd_preview = (ImageView) findViewById(R.id.dmd_preview);
		dmd_preview.setOnClickListener(AmbienceBedroom.this);

		dmd_export = (ImageView) findViewById(R.id.dmd_export);
		dmd_export.setOnClickListener(AmbienceBedroom.this);

		dmd_eraser = (ImageView) findViewById(R.id.dmd_eraser);
		dmd_eraser.setOnClickListener(AmbienceBedroom.this);

		previewDialog = createPreviewDialog();

		grooveView = (Switch) findViewById(R.id.switchGroove);
		grooveView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					groovesOn = true;
				}
				else {
					groovesOn = false;
				}
			}
		});

		img_filter = (ImageView) findViewById(R.id.img_filter);
		img_filter.setOnClickListener(this);
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		view_errorText = (LinearLayout) findViewById(R.id.view_errorText);

		txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					hideSoftKeyboard();
					searchTiles(txtSearch.getText().toString());
					return true;
				}
				return false;
			}
		});

		txtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
										  int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {
				if(s.length() != 0) {
					searchTiles(s.toString());
				}
				else {
					searchFlag = 0;
					getAllTiles();
				}
			}
		});

		initTileSpinners();

		tile_detailed_view = (LinearLayout) findViewById(R.id.tile_detailed_view);
		pattern_detailed_view = (LinearLayout) findViewById(R.id.pattern_detailed_view);
		view_errorText_Pattern = (LinearLayout) findViewById(R.id.view_errorText_Pattern);
		tileOptions = (LinearLayout) findViewById(R.id.tileOptions);
		patternOptions = (LinearLayout) findViewById(R.id.patternOptions);
		imgPatrn = (ImageView) findViewById(R.id.imgPatrn);
		namePatrn = (TextView) findViewById(R.id.namePatrn);
		brandPatrn = (TextView) findViewById(R.id.brandPatrn);
		dimPatrn = (TextView) findViewById(R.id.dimPatrn);
		tile_dtl_text = (TextView) findViewById(R.id.tile_dtl_text);
		patrn_dtl_text = (TextView) findViewById(R.id.patrn_dtl_text);

		tileView = (RecyclerView) findViewById(R.id.view_tile);
		patternView = (RecyclerView) findViewById(R.id.view_pattern);

		backToViewPatrn = (TextView) findViewById(R.id.backToViewPatrn);
		backToViewPatrn.setOnClickListener(AmbienceBedroom.this);

		rot_left_patrn = (ImageView) findViewById(R.id.rot_left_patrn);
		rot_left_patrn.setOnClickListener(AmbienceBedroom.this);

		rot_right_patrn = (ImageView) findViewById(R.id.rot_right_patrn);
		rot_right_patrn.setOnClickListener(AmbienceBedroom.this);

		prepareFilterDialog();

	}

	private Bitmap fillTilesOnLeftWall(Bitmap tile) {

		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceBedroom.this);
		float screenWidthRatio = (float) 1;
		float wallWidthRatio = (float) (leftWallHeightFinal / leftWallWidthFinal);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / leftWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / leftWallWidthFinal) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / leftWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / leftWallWidthFinal) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / leftWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / leftWallWidthFinal) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / leftWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / leftWallWidthFinal) * wallWidthPixels);
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

		int viewArea = GlobalVariables.getDrawArea(AmbienceBedroom.this);
		float screenWidthRatio = (float) 1;
		float wallWidthRatio = (float) (rightWallHeightFinal / rightWallWidthFinal);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightWallWidthFinal) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightWallWidthFinal) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightWallWidthFinal) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightWallWidthFinal) * wallWidthPixels);
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

	private Bitmap fillTilesOnFloor(Bitmap tile) {

		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceBedroom.this);
		float screenWidthRatio = (float) 1;
		float wallWidthRatio = (float) (floorHeightFinal / floorWidthFinal);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / floorWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / floorWidthFinal) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / floorWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / floorWidthFinal) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / floorWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / floorWidthFinal) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / floorWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / floorWidthFinal) * wallWidthPixels);
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


	private String fileSaveName = "Default";



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	private Bitmap warpRightWall(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 265 * density, (int) 148 * density);
		Point leftP2 = new Point((int) 263 * density, (int) 535 * density);
		Point leftP3 = new Point((int) 1000 * density, (int) 577 * density);
		Point leftP4 = new Point((int) 1000 * density, (int) 51 * density);
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
		Point leftP1 = new Point((int) 0 * density, (int) 83 * density);
		Point leftP2 = new Point((int) 0 * density, (int) 562 * density);
		Point leftP3 = new Point((int) 263 * density, (int) 535 * density);
		Point leftP4 = new Point((int) 265 * density, (int) 148 * density);
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

		float aaa = leftWallImage.getWidth();
		float bbb = leftWallImage.getHeight();

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
		Point leftP1 = new Point((int) 263 * density, (int) 535 * density);
		Point leftP2 = new Point((int) -436 * density, (int) 606 * density);
		Point leftP3 = new Point((int) 748 * density, (int) 1050 * density);
		Point leftP4 = new Point((int) 1170 * density, (int) 584 * density);
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



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

			/*case R.id.backToView:
				tileView.setVisibility(View.VISIBLE);
				pattern_detailed_view.setVisibility(View.GONE);
				break;*/

			case R.id.backToView:
				tileSpinner.setVisibility(View.VISIBLE);
				tile_dtl_text.setVisibility(View.GONE);
				txtSearch.setVisibility(View.VISIBLE);
				img_filter.setVisibility(View.VISIBLE);
				tileView.setVisibility(View.VISIBLE);
				tile_detailed_view.setVisibility(View.GONE);
				break;
			case R.id.backToViewPatrn:
//				titleText.setText("Tile Library");
				tileSpinner.setVisibility(View.VISIBLE);
				patrn_dtl_text.setVisibility(View.GONE);
				patternOptions.setVisibility(View.VISIBLE);
				patternView.setVisibility(View.VISIBLE);
				pattern_detailed_view.setVisibility(View.GONE);
				break;

			case R.id.dmd_preview:
				RelativeLayout rel = (RelativeLayout) findViewById(R.id.viewV);
				rel.invalidate();
				rel.setDrawingCacheEnabled(true);
				Bitmap bitmap = rel.getDrawingCache();
				showPreview(bitmap);
				break;
			case R.id.dmd_export:
				ExportImage();
				break;
			case R.id.dmd_eraser:
				doErase();
				break;
			case R.id.syncLibrary:
				syncLibrary();
				break;
			case R.id.img_filter:
				showFilterDialog();
				break;

			default:
				break;
		}

	}

	public void doErase(){

		backImg.removeLayer(layerFloor);
		layerFloor = null;
		layerFloor = backImg
				.addLayer(1,
						floorDrawable,
						floor);

		backImg.removeLayer(layerLeftWall);
		layerLeftWall = null;
		layerLeftWall = backImg.addLayer(2,
				leftWallDrawable, leftWall);

		backImg.removeLayer(layerRightWall);
		layerRightWall = null;
		layerRightWall = backImg.addLayer(
				3, rightWallDrawable,
				rightWall);

	}

	private Dialog createPreviewDialog() {
		Dialog pDialog = new Dialog(AmbienceBedroom.this);
		pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		pDialog.setContentView(R.layout.preview_dialog);// loads the layout we

		ImageView img = (ImageView) pDialog.findViewById(R.id.previewImageView);

		img.setScaleType(ScaleType.CENTER_INSIDE);
		mAttacher = new PhotoViewAttacher(img);
		return pDialog;

	}

	public void showPreview(Bitmap bmp) {

		ImageView img = (ImageView) previewDialog
				.findViewById(R.id.previewImageView);

		img.setScaleType(ScaleType.CENTER_INSIDE);
		img.setImageBitmap(bmp);
		mAttacher.update();
		previewDialog.show();
	}

	@Override
	public void patternName(String path, final String tileSize, String brand,
							String type, final String tile_id, final String tile_type, String price) {

		error_lay.setVisibility(View.GONE);

		if (tileSize != null) {

			String[] tileDim = tileSize.split("x");
			if (tileDim.length == 0) {
				tileDim = tileSize.split("X");
			}
			tileHeight = Float.valueOf(tileDim[0]);
			tileWidth = Float.valueOf(tileDim[1]);

		}

		final String filePath = path;
		bmp = BitmapFactory.decodeFile(filePath);
		this.tileSize = tileSize;
		axis = 0;

		txtSearch.setVisibility(View.GONE);
		img_filter.setVisibility(View.GONE);

		pattern_name.setText(new File(path).getName());
		brand_name.setText(brand);
		pattern_dim.setText(tileSize + " mm");
		pattern_img.setImageBitmap(bmp);

//		patternView.setVisibility(View.GONE);
//		pattern_detailed_view.setVisibility(View.VISIBLE);

		rot_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, 90);
				axis++;
				// bmp.recycle();
				bmp = rBmp;
				bitmap = bmp;
				pattern_img.setImageBitmap(bmp);
			}
		});
		rot_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, -90);
				axis--;
				// bmp.recycle();
				bmp = rBmp;
				bitmap = bmp;
				pattern_img.setImageBitmap(bmp);
			}
		});

		bitmap = bmp;
		path = filePath;
		tileSelected = true;

		tileSpinner.setVisibility(View.GONE);
		tile_dtl_text.setVisibility(View.VISIBLE);
		tileView.setVisibility(View.GONE);
		tile_detailed_view.setVisibility(View.VISIBLE);

	}

	public void getAllTiles() {

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		dbPaginatedList.clear();
		dbOffset = 0;
		dbResult = db.getAllPatternPaginated(dbLimit, dbOffset);

		if (dbResult.size() > 0) {
			dbPaginatedList.addAll(dbResult);
			view_errorText_Pattern.setVisibility(View.GONE);
			view_errorText.setVisibility(View.GONE);
			view_placeholder.setVisibility(View.GONE);
			tileView.setVisibility(View.VISIBLE);
		} else {
			view_errorText.setVisibility(View.GONE);
			view_placeholder.setVisibility(View.VISIBLE);
			tileView.setVisibility(View.GONE);
		}

		// set up the RecyclerView
		tileView.setLayoutManager(new GridLayoutManager(this, 3));
		adapter = new MyRecyclerViewAdapter(this, dbPaginatedList, AmbienceBedroom.this);
		tileView.setAdapter(adapter);

	}

	public void getAllPatterns() {

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
		dbPatternList.clear();
		dbResult = db.getPatterns();

		if(dbResult.size() > 0){
			dbPatternList.addAll(dbResult);
			view_errorText_Pattern.setVisibility(View.GONE);
			patternView.setVisibility(View.VISIBLE);
		}
		else{
			if(libraryTab == false)
				view_errorText_Pattern.setVisibility(View.VISIBLE);
			patternView.setVisibility(View.GONE);
		}

		patternView.setLayoutManager(new GridLayoutManager(this, 3));
		patternAdapter = new RecyclerPaternAdapter(this, dbPatternList, AmbienceBedroom.this);
		patternView.setAdapter(patternAdapter);

	}


	private void paginatedbList(int dbOffset){
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		dbResult = db.getAllPatternPaginated(dbLimit, dbOffset);

		if(dbResult.size() > 0){
			dbPaginatedList.addAll(dbResult);
		}

		adapter.notifyDataSetChanged();

	}

	private static Bitmap RotateBitmapRight(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	String tileSize;
	Bitmap bitmap;
	String path;
	int axis = 0;
	float tileHeight;
	float tileWidth;
	Boolean tileSelected = false;
	Bitmap bmp;

	private void clearMemory() {
		layerFloor = null;
		layerFurniture = null;
		layerShadows = null;
		layerLeftWall = null;
		layerRightWall = null;
		layerRoof = null;
		floorBitmap = null;
		furnitureBitmap = null;
		shadowBitmap = null;
		leftWallBitmap = null;
		rightWallBitmap = null;
		roofBitmap = null;

//		fillProgress = null;
		backImg = null;
//		layout = null;
		leftDrawer = null;
		inflater = null;
		patternContent2 = null;
		eText = null;
		filterSearch_button = null;
		patternContent = null;
		filterSearchButton2 = null;
		patternScrollView = null;
		main_layout = null;
		content_slider = null;
		patternHeader = null;
		previewDialog = null;
		mDrawerToggle = null;
		mAttacher = null;

		// scaleFactor=null;
		density = null;

		furnitureVisible = null;
		furnitureDrawable = null;

		grid = null;
		rightWall = null;
		leftWall = null;
		floor = null;


		pDialog = null;
		// hand =null//new Handler();

		customEdit = null;// = false;

		System.gc();
	}

	public void ExportImage(){

		final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("Preparing Device...");
		pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		pDialog.show();
		pDialog.setCancelable(false);

		layout.invalidate();
		layout.setDrawingCacheEnabled(true);
		final Bitmap previewBitmap = layout.getDrawingCache();

		File file = saveBitMap(getApplicationContext(), previewBitmap);

		if (file != null) {
			pDialog.setTitleText(getString(R.string.success))
					.setContentText(getString(R.string.export_ok))
					.setConfirmText("OK")
					.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
		} else {
			pDialog.setTitleText(getString(R.string.oops))
					.setContentText("Something went wrong; Try again.")
					.setConfirmText("OK")
					.changeAlertType(SweetAlertDialog.ERROR_TYPE);
		}

	}

	private File saveBitMap(Context context, Bitmap bitmap){
		File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"DMD_Screens");
		if (!pictureFileDir.exists()) {
			boolean isDirectoryCreated = pictureFileDir.mkdirs();
			if(!isDirectoryCreated)
				Log.i("TAG", "Can't create directory to save the image");
			return null;
		}
		String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
		File pictureFile = new File(filename);
		try {
			pictureFile.createNewFile();
			FileOutputStream oStream = new FileOutputStream(pictureFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
			oStream.flush();
			oStream.close();
		} catch (IOException e) {
			Log.i("TAG", "Can't save the image");
			e.printStackTrace();
		}
		scanGallery( context,pictureFile.getAbsolutePath());
		return pictureFile;
	}

	private void scanGallery(Context cntx, String path) {
		try {
			MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
				public void onScanCompleted(String path, Uri uri) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void syncLibrary() {

		String token;

		if(downloadingFlag != 1) {
			dDialog = new SweetAlertDialog(AmbienceBedroom.this, SweetAlertDialog.PROGRESS_TYPE)
					.setTitleText("Checking for updates...");
			dDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
			dDialog.setCancelable(false);
			dDialog.show();
		}
		token = "Bearer " + session.getAccessToken();
		getTiles(token);

	}

	public void getTiles(String access_token) {
		try {
			String filter = "";
			List<SettingsModel> settngs;
			settngs = DatabaseManager.getSettings(getApplicationContext());
			filter = settngs.get(0).getUpdated_at();

			ApiInterface apiService =
					ApiClient.getClient().create(ApiInterface.class);
			Call<LoadTileResult> call = apiService.getTiles(filter, access_token);

			call.enqueue(new retrofit2.Callback<LoadTileResult>() {
				@Override
				public void onResponse(Call<LoadTileResult> call, Response<LoadTileResult> response) {
					int statusCode = response.code();
					if (response.isSuccessful()) {
						if (statusCode == 200) {

							if (!response.body().getData().isEmpty()) {
								String name;
								String updated_at = "";
								String status = "0";
								ArrayList<String> tile_ids = new ArrayList<String>();
								downloadingFlag = 1;


								DatabaseHandler db = new DatabaseHandler(
										AmbienceBedroom.this);
								for (int i = 0; i < response.body().getData().size(); i++) {

									name = response.body().getData().get(i).getName();
//                                    dDialog.setTitleText("Downloading Tile Library...");

									TileModel tile = new TileModel();
									updated_at = response.body().getData().get(0).getUpdatedAt();


									String dim = Math.round(Float.parseFloat(response.body().getData().get(i).getWidth())) + "x" +
											Math.round(Float.parseFloat(response.body().getData().get(i).getHeight()));
									if (response.body().getData().get(i).getIsActive()) {
										status = "1";
									} else {
										status = "0";
									}

									tile.setTile_name(response.body().getData().get(i).getName());
									tile.setTile_color(response.body().getData().get(i).getColor());
									tile.setTile_dimen(dim);
									tile.setTile_type(response.body().getData().get(i).getTileTypeName());
									tile.setBarnd_id(response.body().getData().get(i).getBrandId());
									tile.setTile_brand(response.body().getData().get(i).getBrandName());
									tile.setType_id(response.body().getData().get(i).getTileType());
									tile.setCategory_id(response.body().getData().get(i).getCategory());
									tile.setTile_id(response.body().getData().get(i).getId());
									tile.setTile_category(response.body().getData().get(i).getCategoryName());
									tile.setTile_type(response.body().getData().get(i).getTileTypeName());
									tile.setColor_code(response.body().getData().get(i).getColorCode());
									tile.setModel_no(response.body().getData().get(i).getModelNo());
									tile.setTile_p_type("M");
									tile.setTile_price(response.body().getData().get(i).getPrice());
									tile.setImage_url(response.body().getData().get(i).getImageUrl());
									tile.setIs_active(status);

//                                    tile_ids[i] = response.body().getData().get(i).getId();
									tile_ids.add(response.body().getData().get(i).getId().trim());

									db.insertRecordNew(tile);

//                                    Log.d("DMD_Log", "URL from server - " + response.body().getData().get(i).getImageUrl());

								}

								SettingsModel settingsdata = new SettingsModel();
								settingsdata.setId(1);
								settingsdata.setDevice_type("A");
								settingsdata.setUpdated_at(updated_at);

								DatabaseManager.addSettingsInfo(AmbienceBedroom.this, settingsdata);

								dDialog.setTitleText("Syncing Tile Library...");

								downloadTask = new DownloadTask(AmbienceBedroom.this);
								downloadTask.execute("");


							} /*else {
                                dDialog.setTitleText(getString(R.string.oops))
                                        .setContentText("Could not complete Tile Library sync!")
                                        .setConfirmText("OK")
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }*/


						} else {
							JSONObject jObjError = null;
							try {
								dDialog.setTitleText("Syncing Tile Library...");

								downloadTask = new DownloadTask(AmbienceBedroom.this);
								downloadTask.execute("");

								jObjError = new JSONObject(response.errorBody().string());
//                                dDialog.dismissWithAnimation();
								TSnackbar.make(findViewById(android.R.id.content), jObjError.getString("error_message"),
										TSnackbar.LENGTH_LONG);
							} catch (JSONException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}


						}
					} else {
						JSONObject jObjError = null;
						try {
							jObjError = new JSONObject(response.errorBody().string());
							dDialog.dismissWithAnimation();
							if (jObjError.getString("error").equals("invalid_token")) {
								((DashboardActivity) getApplicationContext()).sessionOut();
							} else {
								TSnackbar.make(findViewById(android.R.id.content), jObjError.getString("error_message"),
										TSnackbar.LENGTH_LONG);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onFailure(Call<LoadTileResult> call, Throwable throwable) {

					Log.e("Error", throwable.toString());
					dDialog.dismissWithAnimation();
                    /*if (checkFlagView == 1) {
                        return;
                    }*/

					if (throwable instanceof SocketTimeoutException) {
						TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_SLOW_CONNECTION,
								TSnackbar.LENGTH_LONG).show();
					} else if (throwable instanceof IllegalStateException) {
						TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_WRONG_JSON_FORMAT,
								TSnackbar.LENGTH_LONG).show();
					} else if (throwable instanceof ConnectException) {
						TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_NO_CONNECTION,
								TSnackbar.LENGTH_LONG).show();
					} else if (throwable instanceof UnknownHostException) {
						TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_NO_CONNECTION,
								TSnackbar.LENGTH_LONG).show();
					} else {
						TSnackbar.make(findViewById(android.R.id.content), ERROR_MESSAGE_UNKNOWN,
								TSnackbar.LENGTH_LONG).show();
					}

				}
			});


		} catch (Exception e) {
			Log.e("Error", e.getMessage());
		}
	}

	private class DownloadTask extends AsyncTask<String, Integer, List<RowItem>> {
		ProgressDialog progressDialog;
		private Activity context;
		List<RowItem> rowItems;
		int noOfURLs;

		public DownloadTask(Activity context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {

		}


		@Override
		protected List<RowItem> doInBackground(String... value) {
			DatabaseHandler dh = new DatabaseHandler(context);
			List<TileModel> tileList;
			rowItems = new ArrayList<RowItem>();
			Bitmap map = null;

			tileList = dh.getTileDetailsforDownload();
			noOfURLs = tileList.size();

			if(tileList.size() > 0){
				String tile_id;
				String url_link;
				String tile_name;
				String brand_name;

				for(int i=0; i<tileList.size(); i++){

					url_link = storageURL+tileList.get(i).getImage_url();

//                    Log.d("DMD_Log", "Download Link - " + url_link);

					tile_id = tileList.get(i).getTile_id();
					tile_name = tileList.get(i).getTile_name();
					brand_name = tileList.get(i).getTile_brand();

					if(!url_link.isEmpty()) {
						map = downloadImage(url_link);

						if(map != null && !tile_name.isEmpty() && !brand_name.isEmpty() && !tile_id.isEmpty()) {
							writeStorate(context, map, tile_id, tile_name, brand_name);
//							rowItems.add(new RowItem(map));
							rowItems.add(new RowItem(""));
						}
					}
				}
			}

			return rowItems;
		}

		private Bitmap downloadImage(String urlString) {

			int count = 0;
			Bitmap bitmap = null;

			URL url;
			InputStream inputStream = null;
			BufferedOutputStream outputStream = null;

			try {
				url = new URL(urlString);
				URLConnection connection = url.openConnection();
				long lenghtOfFile = connection.getContentLength();

				inputStream = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream dataStream = new ByteArrayOutputStream();

				outputStream = new BufferedOutputStream(dataStream);

				byte data[] = new byte[4096];
				long total = 0;

				publishProgress(0);

				while ((count = inputStream.read(data)) != -1) {
					total += count;
//                    Log.d("Downloading", "total " + total + " Count " + count + "___" + lenghtOfFile);
					publishProgress((int) ((total * 100) / lenghtOfFile));
					outputStream.write(data, 0, count);
				}
				outputStream.flush();

				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inSampleSize = 1;

				byte[] bytes = dataStream.toByteArray();
				bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bmOptions);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				FileUtils.close(inputStream);
				FileUtils.close(outputStream);
			}
			return bitmap;
		}

		protected void onProgressUpdate(Integer... progress) {

			if (rowItems != null) {
				dDialog.setTitleText("Downloading Tile Library...")
						.setContentText("Loading " + (rowItems.size() + 1) + "/" + noOfURLs
								+ "  (" + progress[0] + "%)");

			}

		}

		@Override
		protected void onPostExecute(List<RowItem> rowItems) {

			downloadingFlag = 0;
			dDialog.setTitleText(getString(R.string.success))
					.setContentText("Tile Library sync completed Successfully")
					.setConfirmText("OK")
					.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
						@Override
						public void onClick(SweetAlertDialog sDialog) {
							sDialog.dismissWithAnimation();
							prepareFilterDialog();
							getAllTiles();
						}
					})
					.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
		}
	}

	private void writeStorate(Context context, Bitmap photo, String tile_id, String tile_name, String brand) {

		DatabaseHandler dh = new DatabaseHandler(context);
		String file_path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/DMD/" + brand + "/";

//        Log.d("DMD_Log", "Storage Link - " + file_path+tile_name + ".jpg");

		File dir = new File(file_path);
		if (!dir.exists())
			dir.mkdirs();
		File file = new File(dir, tile_name + ".jpg");

//        if (!file.exists()) {

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			fOut.close();
			dh.updateDownloadStatus(tile_id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        }
	}

	public void searchTiles(String str){
		DatabaseHandler db = new DatabaseHandler(this);
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		searchFlag = 1;
		clearFilter();

		dbResult = db.searchkeyword(str, "A");

		if(dbResult.size() > 0){
			view_errorText.setVisibility(View.GONE);
			tileView.setVisibility(View.VISIBLE);
			dbPaginatedList.clear();
			dbPaginatedList.addAll(dbResult);
			adapter.notifyDataSetChanged();
		}
		else{
			view_errorText.setVisibility(View.VISIBLE);
			tileView.setVisibility(View.GONE);
		}

	}

	public void prepareFilterDialog(){

		TextView dialog_title;
		ImageView dialog_close;
		final ViewFlipper viewFlipperDialog;
		final Button filterButton;
		final TextView clear_filter;
		final ImageView img_clear_filter;

		dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.dmd_dialog);
		dialog.getWindow()
				.getAttributes().windowAnimations = R.style.DialogAnimation;

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialog_title = (TextView) dialog.findViewById(R.id.dialog_title);
		dialog_close = (ImageView) dialog.findViewById(R.id.dialog_close);
		viewFlipperDialog = (ViewFlipper) dialog.findViewById(R.id.view_flipper);

		dialog_title.setText("Tile Filter");

		dialog_close.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				applyDialog();
//				dialog.cancel();
			}
		});

		tabLayout = (BadgedTabLayout) dialog.findViewById(R.id.tab_layout);

		tabLayout.addTab(tabLayout.newTab().setText("Brand"));
		tabLayout.addTab(tabLayout.newTab().setText("Category"));
		tabLayout.addTab(tabLayout.newTab().setText("Type"));
		tabLayout.addTab(tabLayout.newTab().setText("Size"));
		tabLayout.addTab(tabLayout.newTab().setText("Color"));

		tabLayout.setBadgeText(0,null);
		tabLayout.setBadgeText(1,null);
		tabLayout.setBadgeText(2,null);
		tabLayout.setBadgeText(3,null);
		tabLayout.setBadgeText(4,null);

		addbrandCheckBox();
		addcategoryCheckBox();
		addtileTypeCheckBox();
		addtileSizeCheckBox();
		addtileColorCheckBox();

		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				if(tab.getPosition() == 0){
					viewFlipperDialog.setDisplayedChild(0);
				}
				if(tab.getPosition() == 1){
					viewFlipperDialog.setDisplayedChild(1);
				}
				if(tab.getPosition() == 2){
					viewFlipperDialog.setDisplayedChild(2);
				}
				if(tab.getPosition() == 3){
					viewFlipperDialog.setDisplayedChild(3);
				}
				if(tab.getPosition() == 4){
					viewFlipperDialog.setDisplayedChild(4);
				}
//                viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});

		filterButton = ((Button) dialog.findViewById( R.id.filter_button ));
		filterButton.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				applyDialog();
			}

		});

		clear_filter = ((TextView) dialog.findViewById( R.id.clear_filter ));
		clear_filter.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchFlag = 0;
				clearFilter();
			}

		});

		img_clear_filter = ((ImageView) dialog.findViewById( R.id.img_clear_filter ));
		img_clear_filter.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchFlag = 0;
				clearFilter();
			}

		});
	}

//Brand

	public void addbrandCheckBox() {
		Dialog v = dialog;
		ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.brandCheckBox);
		ll.removeAllViews();

		DatabaseHandler db = new DatabaseHandler(this);
		dbResult1 = db.getAllBrand();
		List<String> brandNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult1.size(); i++) {
			Map<String, String> temp1 = dbResult1.get(i);
			if (!temp1.get(KEY_BRAND_NAME).equalsIgnoreCase(""))
				brandNameList.add(temp1.get(KEY_BRAND_NAME));
		}

		for (int i = 0; i < brandNameList.size(); i++) {

			CheckBox cb = new CheckBox(this);
			cb.setText(brandNameList.get(i));
			cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
			cb.setTag(i);
			cb.setOnCheckedChangeListener(brandItemCheckListener);
			ll.addView(cb);
		}

	}

	CompoundButton.OnCheckedChangeListener brandItemCheckListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedBrands.add(new Integer(t));
					tabLayout.setBadgeText(0, selectedBrands.size() + "");
					filterCount++;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}

			}
			if (!isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedBrands.remove(new Integer(t));
					filterCount--;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}
			}

			if(selectedBrands.size() == 0) {
				tabLayout.setBadgeText(0, null);
			}

		}

	};

//Brand

//Category

	public void addcategoryCheckBox() {
		Dialog v = dialog;
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.categoryCheckBox);
		ll.removeAllViews();

		DatabaseHandler db = new DatabaseHandler(this);
		dbResult = db.getDistinctCategory();
		List<String> categoryNameList = new ArrayList<String>();

		for (int i = 0; i < dbResult.size(); i++) {
			Map<String, String> temp1 = dbResult.get(i);
			if (!temp1.get(KEY_CATEGORY).equalsIgnoreCase(""))
				categoryNameList.add(temp1.get(KEY_CATEGORY));
		}

		for (int i = 0; i < categoryNameList.size(); i++) {

			CheckBox cb = new CheckBox(this);
			cb.setText(categoryNameList.get(i));
			cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
			cb.setTag(i);
			cb.setOnCheckedChangeListener(categoryCheckListener);
			ll.addView(cb);
		}

	}

	CompoundButton.OnCheckedChangeListener categoryCheckListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedCategory.add(new Integer(t));
					tabLayout.setBadgeText(1, selectedCategory.size() + "");
					filterCount++;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}

			}
			if (!isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedCategory.remove(new Integer(t));
					filterCount--;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}
			}

			if(selectedCategory.size() == 0) {
				tabLayout.setBadgeText(1, null);
			}
		}

	};

//Category

//Tile Type

	public void addtileTypeCheckBox() {
		Dialog v = dialog;
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.tileTypeCheckBox);
		ll.removeAllViews();

		DatabaseHandler db = new DatabaseHandler(this);
		dbResult = db.getDistinctType();
		List<String> tileTypeList = new ArrayList<String>();

		for (int i = 0; i < dbResult.size(); i++) {
			Map<String, String> temp1 = dbResult.get(i);
			if (!temp1.get(KEY_TYPE).equalsIgnoreCase(""))
				tileTypeList.add(temp1.get(KEY_TYPE));
		}

		for (int i = 0; i < tileTypeList.size(); i++) {

			CheckBox cb = new CheckBox(this);
			cb.setText(tileTypeList.get(i));
			cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
			cb.setTag(i);
			cb.setOnCheckedChangeListener(titeTypeCheckListener);
			ll.addView(cb);
		}

	}

	CompoundButton.OnCheckedChangeListener titeTypeCheckListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedType.add(new Integer(t));
					tabLayout.setBadgeText(2, selectedType.size() + "");
					filterCount++;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}

			}
			if (!isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedType.remove(new Integer(t));
					filterCount--;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}
			}

			if(selectedType.size() == 0) {
				tabLayout.setBadgeText(2, null);
			}
		}

	};

//Tile Type

//Tile Size

	public void addtileSizeCheckBox() {
		Dialog v = dialog;
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.sizeCheckBox);
		ll.removeAllViews();

		DatabaseHandler db = new DatabaseHandler(this);
		dbResult = db.getDistinctSize();
		List<String> tileSizeList = new ArrayList<String>();

		for (int i = 0; i < dbResult.size(); i++) {
			Map<String, String> temp1 = dbResult.get(i);
			if (!temp1.get(KEY_DIMEN).equalsIgnoreCase(""))
				tileSizeList.add(temp1.get(KEY_DIMEN)+"mm");
		}

		for (int i = 0; i < tileSizeList.size(); i++) {

			CheckBox cb = new CheckBox(this);
			cb.setText(tileSizeList.get(i));
			cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
			cb.setTag(i);
			cb.setOnCheckedChangeListener(titeSizeCheckListener);
			ll.addView(cb);
		}

	}

	CompoundButton.OnCheckedChangeListener titeSizeCheckListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedSize.add(new Integer(t));
					tabLayout.setBadgeText(3, selectedSize.size() + "");
					filterCount++;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}

			}
			if (!isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedSize.remove(new Integer(t));
					filterCount--;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}
			}

			if(selectedSize.size() == 0) {
				tabLayout.setBadgeText(3, null);
			}
		}

	};

//Tile Size

//Tile Color

	public void addtileColorCheckBox() {
		Dialog v = dialog;
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		LinearLayout ll = (LinearLayout) v.findViewById(R.id.colorCheckBox);
		ll.removeAllViews();

		DatabaseHandler db = new DatabaseHandler(this);
		dbResult = db.getDistinctColor();
		List<String> tileColorList = new ArrayList<String>();

		for (int i = 0; i < dbResult.size(); i++) {
			Map<String, String> temp1 = dbResult.get(i);
			if (!temp1.get(KEY_COLOR).equalsIgnoreCase(""))
				tileColorList.add(temp1.get(KEY_COLOR));
		}

		for (int i = 0; i < tileColorList.size(); i++) {

			CheckBox cb = new CheckBox(this);
			cb.setText(tileColorList.get(i));
			cb.setTextColor(getResources().getColor(R.color.itemTitleColorLight));
			cb.setTag(i);
			cb.setOnCheckedChangeListener(titeColorCheckListener);
			ll.addView(cb);
		}

	}

	CompoundButton.OnCheckedChangeListener titeColorCheckListener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
									 boolean isChecked) {
			// TODO Auto-generated method stub
			if (isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedColor.add(new Integer(t));
					tabLayout.setBadgeText(4, selectedColor.size() + "");
					filterCount++;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}

			}
			if (!isChecked) {
				try {
					int t = (Integer) buttonView.getTag();
					selectedColor.remove(new Integer(t));
					filterCount--;
				} catch (Exception e) {
					Log.e("Filter", e.getMessage());
				}
			}

			if(selectedColor.size() == 0) {
				tabLayout.setBadgeText(4, null);
			}
		}

	};

//Tile Color

	public void applyDialog() {

		String cr_category = null, br_name = null, pro_size = null, pro_color = null, pro_type = null;

		DatabaseHandler db = new DatabaseHandler(this);
		DatabaseHandler db1 = new DatabaseHandler(this);
		DatabaseHandler db2 = new DatabaseHandler(this);
		DatabaseHandler db3 = new DatabaseHandler(this);
		DatabaseHandler db4 = new DatabaseHandler(this);

		dbResult = db.getDistinctCategory();
		dbResult1 = db1.getAllBrand();
		dbResult2 = db2.getDistinctSize();
		dbResult3 = db3.getDistinctColor();
		dbResult4 = db4.getDistinctType();

		txtSearch.setText("");

		if (selectedCategory.size() > 0)
			cr_category = getStringFromCheckedList(dbResult, selectedCategory,
					KEY_CATEGORY);
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

		if (filterCount >  0){
			img_filter.setImageResource(R.drawable.dmd_filter_active);
		}
		else{
			img_filter.setImageResource(R.drawable.dmd_filter);
		}

		DatabaseHandler db5 = new DatabaseHandler(this);
		filterResult = db5.getResultByFilterNew(cr_category, br_name, pro_size,
				pro_color, pro_type, "A");

		if(filterResult.size() > 0){
			view_errorText.setVisibility(View.GONE);
			tileView.setVisibility(View.VISIBLE);

			searchFlag = 1;
			dbPaginatedList.clear();
			dbPaginatedList.addAll(filterResult);
			adapter.notifyDataSetChanged();
		}
		else{
			view_errorText.setVisibility(View.VISIBLE);
			tileView.setVisibility(View.GONE);
		}

		dialog.dismiss();

	}


	public String getStringFromCheckedList(
			ArrayList<HashMap<String, String>> dbResult,
			List<Integer> selectedItems, String ketItem) {
		List<String> itemString = new ArrayList<String>();
		String resString = "";
		if (selectedItems.size() == 0) {
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
		if (selectedItems.size() == 1) {
			try {
				int index = selectedItems.get(0);
				resString = "'" + itemString.get(index) + "'";
			} catch (Exception ex) {
				Log.e("error", ex.getMessage());

			}
		} else {
			resString = "'" + itemString.get(selectedItems.get(0)) + "'";
			System.out.println(selectedItems.size());
			for (int i = 1; i < selectedItems.size(); i++) {
				try {
					int itemId = selectedItems.get(i);
					resString += ",'" + itemString.get(selectedItems.get(i))
							+ "'";
				} catch (Exception e) {
					// TODO: handle exception
					Log.e("Filter", "getStringFromCheckedList error!_"+e.getMessage());
					return null;

				}
			}
		}
		return resString;

	}

	private void clearFilter(){

		filterCount = 0;
		img_filter.setImageResource(R.drawable.dmd_filter);

		selectedCategory.clear();
		selectedBrands.clear();
		selectedSize.clear();
		selectedColor.clear();
		selectedType.clear();

		tabLayout.setBadgeText(0,null);
		tabLayout.setBadgeText(1,null);
		tabLayout.setBadgeText(2,null);
		tabLayout.setBadgeText(3,null);
		tabLayout.setBadgeText(4,null);

		getAllTiles();

		LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.brandCheckBox);
		ll.removeAllViews();

		addbrandCheckBox();
		addcategoryCheckBox();
		addtileTypeCheckBox();
		addtileSizeCheckBox();
		addtileColorCheckBox();

		dialog.dismiss();
	}

	private void showFilterDialog(){
		TabLayout.Tab tab = tabLayout.getTabAt(0);
		tab.select();
		dialog.show();
	}

	public void hideSoftKeyboard() {
		View view = getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public class OnSwipeTouchListener implements View.OnTouchListener {

		private GestureDetector gestureDetector;

		public OnSwipeTouchListener(Context c) {
			gestureDetector = new GestureDetector(c, new OnSwipeTouchListener.GestureListener());
		}

		public boolean onTouch(final View view, final MotionEvent motionEvent) {
			TouchPointX = motionEvent.getX();
			TouchPointY = motionEvent.getY();

			return gestureDetector.onTouchEvent(motionEvent);
		}

		private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

			private static final int SWIPE_THRESHOLD = 100;
			private static final int SWIPE_VELOCITY_THRESHOLD = 100;

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {

				return super.onSingleTapUp(e);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				onClick();
				return super.onSingleTapConfirmed(e);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				onDoubleClick();
				return super.onDoubleTap(e);
			}

			@Override
			public void onLongPress(MotionEvent e) {
				onLongClick();
				super.onLongPress(e);
			}

			// Determines the fling velocity and then fires the appropriate swipe event accordingly
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				boolean result = false;
				try {
					float diffY = e2.getY() - e1.getY();
					float diffX = e2.getX() - e1.getX();
					if (Math.abs(diffX) > Math.abs(diffY)) {
						if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
							if (diffX > 0) {
								onSwipeRight();
							} else {
								onSwipeLeft();
							}
						}
					} else {
						if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
							if (diffY > 0) {
								onSwipeDown();
							} else {
								onSwipeUp();
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return result;
			}
		}

		public void onSwipeRight() {
		}

		public void onSwipeLeft() {
		}

		public void onSwipeUp() {
		}

		public void onSwipeDown() {
		}

		public void onClick() {

		}

		public void onDoubleClick() {

		}

		public void onLongClick() {

		}
	}

	@Override
	public void viewPattern(String path, final String tileSize, String brand,
							String type, final String tile_id, final String tile_type) {

		error_text.setVisibility(View.GONE);

		if (tileSize != null) {

			String[] tileDim = tileSize.split("x");
			if (tileDim.length == 0) {
				tileDim = tileSize.split("X");
			}
			tileHeight = Float.valueOf(tileDim[0]);
			tileWidth = Float.valueOf(tileDim[1]);

		}

		final String filePath = path;
		String tname;
		bmp = BitmapFactory.decodeFile(filePath);
		this.tileSize = tileSize;
		axis = 0;

		String[] tName = new File(path).getName().split("/");
		tname = tName[tName.length - 1].replace(".jpg", "");

		patternOptions.setVisibility(View.GONE);

		namePatrn.setText(tname);
		brandPatrn.setText(brand);
		dimPatrn.setText(tileSize + " mm");
		imgPatrn.setImageBitmap(bmp);

		rot_right_patrn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, 90);
				axis++;
				// bmp.recycle();
				bmp = rBmp;
				bitmap = bmp;
				imgPatrn.setImageBitmap(bmp);
			}
		});
		rot_left_patrn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, -90);
				axis--;
				// bmp.recycle();
				bmp = rBmp;
				bitmap = bmp;
				imgPatrn.setImageBitmap(bmp);
			}
		});

		bitmap = bmp;
		path = filePath;
		tileSelected = true;

		tileSpinner.setVisibility(View.GONE);
		patrn_dtl_text.setVisibility(View.VISIBLE);
		patternView.setVisibility(View.GONE);
		pattern_detailed_view.setVisibility(View.VISIBLE);

	}

	public void initTileSpinners() {

		String[] unitArray = getResources().getStringArray(
				R.array.tileSpinnerArray);

		tileSpinner = (Spinner) findViewById(R.id.tileSpinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, R.layout.tile_spinner_item, unitArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tileSpinner.setAdapter(spinnerArrayAdapter);
		tileSpinner.setOnItemSelectedListener(tileSpinnerListener);
		int position = 0;
		tileSpinner.setSelection(position);
	}

	AdapterView.OnItemSelectedListener tileSpinnerListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								   long arg3) {
			// TODO Auto-generated method stub
			String[] tileArray = getResources().getStringArray(
					R.array.tileSpinnerArray);

			if (tileArray[arg2].equalsIgnoreCase("Tile Library")) {
				patternView.setVisibility(View.GONE);
				tileView.setVisibility(View.VISIBLE);
				patternOptions.setVisibility(View.GONE);
				tileOptions.setVisibility(View.VISIBLE);
				view_errorText_Pattern.setVisibility(View.GONE);
				libraryTab = true;
				txtSearch.setText("");
				clearFilter();

			} else if (tileArray[arg2].equalsIgnoreCase("Pattern Library")) {

				view_errorText.setVisibility(View.GONE);
				patternView.setVisibility(View.VISIBLE);
				tileView.setVisibility(View.GONE);
				tileOptions.setVisibility(View.GONE);
				patternOptions.setVisibility(View.VISIBLE);
				libraryTab = false;
				getAllPatterns();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};
}