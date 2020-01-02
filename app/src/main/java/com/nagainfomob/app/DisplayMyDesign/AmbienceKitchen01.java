package com.nagainfomob.app.DisplayMyDesign;

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
import android.widget.Toast;
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
import com.nagainfomob.app.slider.PatternGridAdapter;
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

public class AmbienceKitchen01 extends AppCompatActivity implements
		PatternimgNameInterface, PatternDetailsInterface, OnClickListener {

	private String storageURL = "https://dmd-file.s3.us-east-1.amazonaws.com/";
	private static int downloadingFlag = 0;
	private SweetAlertDialog dDialog;
	DownloadTask downloadTask;
	private SessionManager session;

	private RelativeLayout layout;
	private LayeredImageView backImg;
	private RelativeLayout error_lay;

	private LayeredImageView.Layer layerFloor, layerFurniture, layerCenterWall, layerRightWall,
			layerCentreSlab,  layerRightSlab,layerRoof;
	private LayeredImageView.Layer layerFloorCabin01, layerFloorCabin02, layerFloorCabin03,
			layerFloorCabin04, layerFloorCabin05, layerFloorCabin06,
			layerFloorCabin07, layerFloorCabin08, layerFloorCabin09,
			layerFloorCabin10, layerFloorCabin11;
	private LayeredImageView.Layer layerWallCabin01, layerWallCabin02, layerWallCabin03,
			layerWallCabin04, layerWallCabin05, layerWallCabin06,
			layerWallCabin07, layerWallCabin08, layerWallCabin09,
			layerWallCabin10;
	private LayeredImageView.Layer layerMask;
	private Bitmap floorBitmap, furnitureBitmap, centerWallBitmap,
			rightWallBitmap, slabBitmap, roofBitmap;
	private Bitmap wallCabinBitmap01, wallCabinBitmap02, wallCabinBitmap10;

	private Bitmap maskBitmap, nothing;

	private Matrix floorCabin1, floorCabin2, floorCabin3, floorCabin4,
			floorCabin5, floorCabin6, floorCabin7, floorCabin8, floorCabin9,
			floorCabin10, floorCabin11;

	private Matrix wallCabin1, wallCabin2, wallCabin3, wallCabin4, wallCabin5,
			wallCabin6, wallCabin7, wallCabin8, wallCabin9, wallCabin10;

	private BitmapDrawable maskDrawable;
	private BitmapDrawable wallCabinDrawable01, wallCabinDrawable02,
			wallCabinDrawable03, wallCabinDrawable04, wallCabinDrawable05,
			wallCabinDrawable06, wallCabinDrawable07, wallCabinDrawable08,
			wallCabinDrawable09, wallCabinDrawable10;
	private BitmapDrawable floorCabinDrawable01, floorCabinDrawable02,
			floorCabinDrawable03, floorCabinDrawable04, floorCabinDrawable05,
			floorCabinDrawable06, floorCabinDrawable07, floorCabinDrawable08,
			floorCabinDrawable09, floorCabinDrawable10, floorCabinDrawable11;
	private BitmapDrawable centreSlabDrawable, rightSlabDrawable;
	
	private float scaleFactor;
	private Float density;
//	private PhotoViewAttacher mAttacher;
	private DrawerLayout leftDrawer;
	private LayoutInflater inflater;
	private View patternContent2;
	private EditText eText;
	private Button filterSearch_button;
	private View patternContent;
	private Button filterSearchButton2;
	private LinearLayout patternScrollView;
	private ActionBarDrawerToggle mDrawerToggle;
	private GridView grid;
	private Bitmap bmp;
	private boolean tileSelected = false;
	private static final String KEY_BRAND_NAME = "pro_brand";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_COMPANY = "pro_company";
	private static final String KEY_CATEGORY = "tile_category";

	private Handler hand = new Handler();

	private Matrix floor;
	private Matrix centreWall;
	private Matrix rightWall;

	private Matrix centreSlab, rightSlab;

	private final float centreWallHeightFinal = (float) 10;
	private final float centreWallWidthFinal = (float) 15;
	private final float floorHeightFinal = (float) 10;
	private final float floorWidthFinal = (float) 15;
	private final float rightWallHeightFinal = (float) 10;
	private final float rightWallWidthFinal = (float) 10;

	private float centreWallHeight, centreWallWidth, floorHeight, floorWidth,
			rightWallHeight, rightWallWidth;

	private final int FLOOR_REQUEST_CODE = 0, CENTRE_WALL_REQUEST_CODE = 1,
			RIGHT_WALL_REQUEST_CODE = 2;

	final float slabHeightFinal = (float) 1.2;
	final float centreSlabWidth = (float) 6;
	final float rightSlabWidth = (float) 4;
	
	private String currentProject;
	private String screenshotLocation;
	private String savedFolderName;
	private String viewType;
	private ProgressDialog fillProgress;
	private BitmapDrawable furnitureDrawable;
	private Boolean customEdit = false;

	private ProgressDialog pDialog;
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

	private static float TouchPointX;
	private static float TouchPointY;

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
		session = new SessionManager(AmbienceKitchen01.this);

		final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("Rendering..");
		pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		pDialog.show();
		pDialog.setCancelable(false);

		OpenCVLoader.initDebug();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getSupportActionBar().hide();

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.ambience_drawingroom);

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
		viewType = "LivingRoom02";
		screenshotLocation = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/DMD/"
				+ savedFolderName
				+ "/"
				+ viewType;

		centreWallHeight = GlobalVariables.feetToMm(centreWallHeightFinal);
		centreWallWidth = GlobalVariables.feetToMm(centreWallWidthFinal);
		floorHeight = GlobalVariables.feetToMm(floorHeightFinal);
		floorWidth = GlobalVariables.feetToMm(floorWidthFinal);
		rightWallHeight = GlobalVariables.feetToMm(rightWallHeightFinal);
		rightWallWidth = GlobalVariables.feetToMm(rightWallWidthFinal);

		backImg.setImageResource(R.drawable.kitchen_01_nothing);
		nothing = BitmapFactory.decodeResource(getResources(), R.drawable.kitchen_01_nothing).copy(
				Bitmap.Config.ARGB_8888, true);



		wallCabinBitmap01 = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_cabins_color).copy(
				Bitmap.Config.ARGB_8888, true);
		wallCabinBitmap02 = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_cabins_wall_mounted_02).copy(
				Bitmap.Config.ARGB_8888, true);

		wallCabinBitmap10 = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_cabins_wall_mounted_10).copy(
				Bitmap.Config.ARGB_8888, true);

		maskBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_mask).copy(Bitmap.Config.ARGB_8888, true);
		floorBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_floor_color).copy(
				Bitmap.Config.ARGB_8888, true);
		centerWallBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_centre_wall_color).copy(
				Bitmap.Config.ARGB_8888, true);
		rightWallBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_right_wall_color).copy(
				Bitmap.Config.ARGB_8888, true);
		furnitureBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_furniture).copy(Bitmap.Config.ARGB_8888,
				true);
		slabBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_slab_color).copy(Bitmap.Config.ARGB_8888,
				true);
		roofBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.kitchen_01_ceiling).copy(Bitmap.Config.ARGB_8888,
				true);

		wallCabinDrawable01 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 1));
		wallCabinDrawable02 = new BitmapDrawable(getResources(),
				wallCabinBitmap02);
		wallCabinDrawable03 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 3));
		wallCabinDrawable04 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 4));
		wallCabinDrawable05 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 5));
		wallCabinDrawable06 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 6));
		wallCabinDrawable07 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 7));
		wallCabinDrawable08 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 8));
		wallCabinDrawable09 = new BitmapDrawable(getResources(), warpWallCabin(
				wallCabinBitmap01, 9));
		wallCabinDrawable10 = new BitmapDrawable(getResources(),
				wallCabinBitmap10);

		floorCabinDrawable01 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 1));
		floorCabinDrawable02 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 2));
		floorCabinDrawable03 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 3));
		floorCabinDrawable04 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 4));
		floorCabinDrawable05 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 5));
		floorCabinDrawable06 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 6));
		floorCabinDrawable07 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 7));
		floorCabinDrawable08 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 8));
		floorCabinDrawable09 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 9));
		floorCabinDrawable10 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 10));
		floorCabinDrawable11 = new BitmapDrawable(getResources(),
				warpFloorCabin(wallCabinBitmap01, 11));

		BitmapDrawable floorDrawable = new BitmapDrawable(getResources(),
				warpFloor(floorBitmap));
		BitmapDrawable centerWallDrawable = new BitmapDrawable(getResources(),
				warpCentreWall(centerWallBitmap));
		BitmapDrawable rightWallDrawable = new BitmapDrawable(getResources(),
				warpRightWall(rightWallBitmap));
		furnitureDrawable = new BitmapDrawable(getResources(), furnitureBitmap);
		centreSlabDrawable = new BitmapDrawable(getResources(),
				warpCentreSlab(slabBitmap));
		rightSlabDrawable = new BitmapDrawable(getResources(),
				warpRightSlab(slabBitmap));
		BitmapDrawable roofDrawable = new BitmapDrawable(getResources(),
				roofBitmap);
		maskDrawable = new BitmapDrawable(getResources(), maskBitmap);

		floorBitmap = null;
		centerWallBitmap = null;
		rightWallBitmap = null;
		furnitureBitmap = null;
		slabBitmap = null;
		roofBitmap = null;
		maskBitmap = null;
		wallCabinBitmap01 = null;
		wallCabinBitmap02 = null;
		wallCabinBitmap10 = null;

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		Matrix m = new Matrix();

		density = dm.density;
		scaleFactor = (1 / (float) density.intValue());
		scaleFactor = 1;
		m.preScale(scaleFactor, scaleFactor);

		int height = dm.heightPixels;
		int width = (int) ((985 / 693) * height * 1.15);

		layout.setLayoutParams(new RelativeLayout.LayoutParams(width, height));


		/*density = dm.density;
		scaleFactor = (1 / (float) density.intValue());
		m.preScale(scaleFactor, scaleFactor);

		int height = dm.heightPixels;
		int width = (int) ((985 / 693) * height * 1.1699);

		layout.setLayoutParams(new RelativeLayout.LayoutParams(width, height
				- (int) (85 * density)));*/

		floor = new Matrix();
		floor.preScale(scaleFactor * resolutionMultiplier, scaleFactor
				* resolutionMultiplier);
		floor.preTranslate(-212 * density, 484 * density);

		centreWall = new Matrix();
		centreWall.preScale(scaleFactor, scaleFactor);
		centreWall.preTranslate(0 * density, 83 * density);

		rightWall = new Matrix();
		rightWall.preScale(scaleFactor, scaleFactor);
		rightWall.preTranslate(713 * density, -45 * density);

		centreSlab = new Matrix();
		centreSlab.preScale(scaleFactor * slabResMultiplier, scaleFactor
				* slabResMultiplier);
		centreSlab.preTranslate((97 / slabResMultiplier) * density,
				(339 / slabResMultiplier) * density);

		rightSlab = new Matrix();
		rightSlab.preScale(scaleFactor, scaleFactor);
		rightSlab.preTranslate(637 * density, 370 * density);

		wallCabin1 = new Matrix();
		wallCabin1.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin1.preTranslate((0 / resolutionMultiplierCabin) * density,
				(105 / resolutionMultiplierCabin) * density);

		wallCabin2 = new Matrix();
		wallCabin2.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin2.preTranslate(0 * density, 0 * density);

		wallCabin3 = new Matrix();
		wallCabin3.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin3.preTranslate((133 / resolutionMultiplierCabin) * density,
				(118 / resolutionMultiplierCabin) * density);

		wallCabin4 = new Matrix();
		wallCabin4.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin4.preTranslate((373 / resolutionMultiplierCabin) * density,
				(118 / resolutionMultiplierCabin) * density);

		wallCabin5 = new Matrix();
		wallCabin5.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin5.preTranslate((494 / resolutionMultiplierCabin) * density,
				(119 / resolutionMultiplierCabin) * density);

		wallCabin6 = new Matrix();
		wallCabin6.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin6.preTranslate((614 / resolutionMultiplierCabin) * density,
				(120 / resolutionMultiplierCabin) * density);

		wallCabin7 = new Matrix();
		wallCabin7.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin7.preTranslate((689 / resolutionMultiplierCabin) * density,
				(107 / resolutionMultiplierCabin) * density);

		wallCabin8 = new Matrix();
		wallCabin8.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin8.preTranslate((719 / resolutionMultiplierCabin) * density,
				(241 / resolutionMultiplierCabin) * density);

		wallCabin9 = new Matrix();
		wallCabin9.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin9.preTranslate((878 / resolutionMultiplierCabin) * density,
				(-4 / resolutionMultiplierCabin) * density);

		wallCabin10 = new Matrix();
		wallCabin10.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		wallCabin10.preTranslate(0 * density, 0 * density);

		floorCabin1 = new Matrix();
		floorCabin1.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin1.preTranslate((907 / resolutionMultiplierCabin) * density,
				(504 / resolutionMultiplierCabin) * density);

		floorCabin2 = new Matrix();
		floorCabin2.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin2.preTranslate((766 / resolutionMultiplierCabin) * density,
				(436 / resolutionMultiplierCabin) * density);

		floorCabin3 = new Matrix();
		floorCabin3.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin3.preTranslate((686 / resolutionMultiplierCabin) * density,
				(395 / resolutionMultiplierCabin) * density);

		floorCabin4 = new Matrix();
		floorCabin4.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin4.preTranslate((640 / resolutionMultiplierCabin) * density,
				(373 / resolutionMultiplierCabin) * density);

		floorCabin5 = new Matrix();
		floorCabin5.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin5.preTranslate((505 / resolutionMultiplierCabin) * density,
				(371 / resolutionMultiplierCabin) * density);

		floorCabin6 = new Matrix();
		floorCabin6.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin6.preTranslate((375 / resolutionMultiplierCabin) * density,
				(466 / resolutionMultiplierCabin) * density);

		floorCabin7 = new Matrix();
		floorCabin7.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin7.preTranslate((375 / resolutionMultiplierCabin) * density,
				(407 / resolutionMultiplierCabin) * density);

		floorCabin8 = new Matrix();
		floorCabin8.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin8.preTranslate((375 / resolutionMultiplierCabin) * density,
				(372 / resolutionMultiplierCabin) * density);

		floorCabin9 = new Matrix();
		floorCabin9.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin9.preTranslate((244 / resolutionMultiplierCabin) * density,
				(499 / resolutionMultiplierCabin) * density);

		floorCabin10 = new Matrix();
		floorCabin10.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin10.preTranslate((112 / resolutionMultiplierCabin) * density,
				(371 / resolutionMultiplierCabin) * density);

		floorCabin11 = new Matrix();
		floorCabin11.preScale(scaleFactor * resolutionMultiplierCabin,
				scaleFactor * resolutionMultiplierCabin);
		floorCabin11.preTranslate((0 / resolutionMultiplierCabin) * density,
				(370 / resolutionMultiplierCabin) * density);

		layerFloor = backImg.addLayer(0, floorDrawable, floor);
		layerRoof = backImg.addLayer(1, roofDrawable, m);
		layerCenterWall = backImg.addLayer(2, centerWallDrawable, centreWall);
		layerRightWall = backImg.addLayer(3, rightWallDrawable, rightWall);
		layerMask = backImg.addLayer(4, maskDrawable, m);

		layerFloorCabin01 = backImg.addLayer(5, floorCabinDrawable01,
				floorCabin1);
		layerFloorCabin02 = backImg.addLayer(6, floorCabinDrawable02,
				floorCabin2);
		layerFloorCabin03 = backImg.addLayer(7, floorCabinDrawable03,
				floorCabin3);
		layerFloorCabin04 = backImg.addLayer(8, floorCabinDrawable04,
				floorCabin4);
		layerFloorCabin05 = backImg.addLayer(9, floorCabinDrawable05,
				floorCabin5);
		layerFloorCabin06 = backImg.addLayer(10, floorCabinDrawable06,
				floorCabin6);
		layerFloorCabin07 = backImg.addLayer(11, floorCabinDrawable07,
				floorCabin7);
		layerFloorCabin08 = backImg.addLayer(12, floorCabinDrawable08,
				floorCabin8);
		layerFloorCabin09 = backImg.addLayer(13, floorCabinDrawable09,
				floorCabin9);
		layerFloorCabin10 = backImg.addLayer(14, floorCabinDrawable10,
				floorCabin10);
		layerFloorCabin11 = backImg.addLayer(15, floorCabinDrawable11,
				floorCabin11);

		layerWallCabin01 = backImg
				.addLayer(16, wallCabinDrawable01, wallCabin1);
		layerWallCabin02 = backImg.addLayer(17, wallCabinDrawable02, m);
		layerWallCabin03 = backImg
				.addLayer(18, wallCabinDrawable03, wallCabin3);
		layerWallCabin04 = backImg
				.addLayer(19, wallCabinDrawable04, wallCabin4);
		layerWallCabin05 = backImg
				.addLayer(20, wallCabinDrawable05, wallCabin5);
		layerWallCabin06 = backImg
				.addLayer(21, wallCabinDrawable06, wallCabin6);
		layerWallCabin07 = backImg
				.addLayer(22, wallCabinDrawable07, wallCabin7);
		layerWallCabin08 = backImg
				.addLayer(23, wallCabinDrawable08, wallCabin8);
		layerWallCabin09 = backImg
				.addLayer(24, wallCabinDrawable09, wallCabin9);
		layerWallCabin10 = backImg.addLayer(25, wallCabinDrawable10, m);

		layerCentreSlab = backImg.addLayer(26, centreSlabDrawable, centreSlab);
		layerRightSlab = backImg.addLayer(27, rightSlabDrawable, rightSlab);
		layerFurniture = backImg.addLayer(28, furnitureDrawable, m);

		final Polygon slabPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								116 / scaleFactor, 271 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								560 / scaleFactor, 271 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								785 / scaleFactor, 340 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								785 / scaleFactor, 394 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								716 / scaleFactor, 394 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								499 / scaleFactor, 290 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(86 / scaleFactor,
								288 / scaleFactor)).build();

		final Polygon floorPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								431 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								509 / scaleFactor, 431 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								644 / scaleFactor, 586 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								586 / scaleFactor)).build();

		final Polygon centreWallPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								67 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								77 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(86 / scaleFactor,
								77 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								104 / scaleFactor, 85 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								195 / scaleFactor, 85 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								205 / scaleFactor, 97 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								205 / scaleFactor, 210 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								118 / scaleFactor, 210 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								116 / scaleFactor, 271 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								560 / scaleFactor, 271 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								560 / scaleFactor, 214 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								289 / scaleFactor, 210 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								290 / scaleFactor, 86 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								235 / scaleFactor, 88 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								560 / scaleFactor, 77 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								560 / scaleFactor, 67 / scaleFactor)).build();

		final Polygon rightWallPolygon = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								560 / scaleFactor, 214 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								560 / scaleFactor, 271 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								785 / scaleFactor, 388 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								785 / scaleFactor, 233 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								684 / scaleFactor, 223 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								680 / scaleFactor, 192 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								613 / scaleFactor, 192 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								613 / scaleFactor, 215 / scaleFactor)).build();

		final Polygon cabinPolygon01 = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(0 / scaleFactor,
								85 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								196 / scaleFactor, 94 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								194 / scaleFactor, 202 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(82 / scaleFactor,
								205 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(82 / scaleFactor,
								294 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								496 / scaleFactor, 298 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								706 / scaleFactor, 400 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								706 / scaleFactor, 586 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								667 / scaleFactor, 580 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								503 / scaleFactor, 413 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								177 / scaleFactor, 409 / scaleFactor)).build();

		final Polygon cabinPolygon02 = Polygon
				.Builder()
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								295 / scaleFactor, 94 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								534 / scaleFactor, 98 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								784 / scaleFactor, 0 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								784 / scaleFactor, 217 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								691 / scaleFactor, 211 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								692 / scaleFactor, 182 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								608 / scaleFactor, 185 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								610 / scaleFactor, 203 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								542 / scaleFactor, 203 / scaleFactor))
				.addVertex(
						new com.nagainfomob.app.sromku.polygon.Point(
								295 / scaleFactor, 202 / scaleFactor)).build();


		layout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {

			@Override
			public void onClick() {
				super.onClick();

				com.nagainfomob.app.sromku.polygon.Point touch = new com.nagainfomob.app.sromku.polygon.Point(
						TouchPointX, TouchPointY);

				if (tileSelected) {

					customEdit = false;

					if (floorPolygon.contains(touch)) {


						final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceKitchen01.this, SweetAlertDialog.PROGRESS_TYPE)
								.setTitleText("Processing..");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
						pDialog.show();
						pDialog.setCancelable(false);

						Thread thread = new Thread(new Runnable() {

							public void run() {

								final BitmapDrawable floorDrawable = new BitmapDrawable(
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
										layerFloor = backImg.addLayer(0,
												floorDrawable, floor);
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

					} else if (centreWallPolygon.contains(touch)) {


						final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceKitchen01.this, SweetAlertDialog.PROGRESS_TYPE)
								.setTitleText("Processing..");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
						pDialog.show();
						pDialog.setCancelable(false);

						Thread thread = new Thread(new Runnable() {

							public void run() {

								final BitmapDrawable centreWallDrawable = new BitmapDrawable(
										getResources(),
										warpCentreWall(fillTilesOnCentreWall(bitmap)));

								hand.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										backImg.removeLayer(layerCenterWall);
										layerCenterWall = null;
									}
								});

								hand.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										layerCenterWall = backImg.addLayer(
												2, centreWallDrawable,
												centreWall);
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


						final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceKitchen01.this, SweetAlertDialog.PROGRESS_TYPE)
								.setTitleText("Processing..");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
						pDialog.show();
						pDialog.setCancelable(false);

						Thread thread = new Thread(new Runnable() {

							public void run() {

								final BitmapDrawable rightWallDrawable = new BitmapDrawable(
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
												3, rightWallDrawable,
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
					} else if (slabPolygon.contains(touch)) {


						final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceKitchen01.this, SweetAlertDialog.PROGRESS_TYPE)
								.setTitleText("Processing..");
						pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
						pDialog.show();
						pDialog.setCancelable(false);

						Thread thread = new Thread(new Runnable() {

							public void run() {

								centreSlabDrawable = new BitmapDrawable(
										getResources(),
										warpCentreSlab(fillTilesOnCentreSlab(bitmap)));
								rightSlabDrawable = new BitmapDrawable(
										getResources(),
										warpRightSlab(fillTilesOnRightSlab(bitmap)));

								hand.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										backImg.removeLayer(layerCentreSlab);
										backImg.removeLayer(layerRightSlab);
										layerCentreSlab = null;
										layerRightSlab = null;
									}
								});

								hand.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										layerCentreSlab = backImg.addLayer(
												25, centreSlabDrawable,
												centreSlab);
										layerRightSlab = backImg.addLayer(
												26, rightSlabDrawable,
												rightSlab);
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
					} /*else if (cabinPolygon01.contains(touch)
							|| cabinPolygon02.contains(touch)) {
						replaceCabin();
					}*/
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
							Float.parseFloat(String.valueOf(floorWidth)), 0, 0 ,0);

					Intent intent = new Intent(AmbienceKitchen01.this, CustomPatternActivity.class);
					intent.putExtra("name", "design");
					intent.putExtra("width", floorWidth+"");
					intent.putExtra("height", floorHeight+"");
					intent.putExtra("category", "");
					intent.putExtra("category_id", "");
					intent.putExtra("price", "");
					intent.putExtra("brand", "");
					intent.putExtra("type", "");
					intent.putExtra("type_id", "");
					intent.putExtra("is_edit", "2");

					intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, FLOOR_REQUEST_CODE);

				} else if (centreWallPolygon.contains(touch)) {

					GlobalVariables.setProjectName("Edit Wall");
					GlobalVariables.setUnit("Millimeter");
					GlobalVariables.setDesignerUnit(GlobalVariables.getUnit());
					GlobalVariables.setWallDim(Float.parseFloat(String.valueOf(centreWallHeight)),
							Float.parseFloat(String.valueOf(centreWallWidth)), 0, 0 ,0);

					Intent intent = new Intent(AmbienceKitchen01.this, CustomPatternActivity.class);
					intent.putExtra("name", "design");
					intent.putExtra("width", centreWallWidth+"");
					intent.putExtra("height", centreWallHeight+"");
					intent.putExtra("category", "");
					intent.putExtra("category_id", "");
					intent.putExtra("price", "");
					intent.putExtra("brand", "");
					intent.putExtra("type", "");
					intent.putExtra("type_id", "");
					intent.putExtra("is_edit", "2");

					intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, CENTRE_WALL_REQUEST_CODE);
				} else if (rightWallPolygon.contains(touch)) {

					GlobalVariables.setProjectName("Edit Wall");
					GlobalVariables.setUnit("Millimeter");
					GlobalVariables.setDesignerUnit(GlobalVariables.getUnit());
					GlobalVariables.setWallDim(Float.parseFloat(String.valueOf(rightWallHeight)),
							Float.parseFloat(String.valueOf(rightWallWidth)), 0, 0 ,0);

					Intent intent = new Intent(AmbienceKitchen01.this, CustomPatternActivity.class);
					intent.putExtra("name", "design");
					intent.putExtra("width", rightWallWidth+"");
					intent.putExtra("height", rightWallHeight+"");
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

		pDialog.dismissWithAnimation();

	}

	private void replaceCabin() {
		// TODO Auto-generated method stub

		dialog = ProgressDialog.show(AmbienceKitchen01.this, "", "Loading...",
				true, false);
		Thread thread = new Thread(new Runnable() {

			public void run() {

				floorCabinDrawable01 = new BitmapDrawable(getResources(),
						warpFloorCabin(fillTilesOnCabin(bitmap, 3, (float) 1),
								1));
				floorCabinDrawable02 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, 3, (float) 2.4), 2));
				floorCabinDrawable03 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, 3, (float) 2.4), 3));
				floorCabinDrawable04 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, 3, (float) 2.4), 4));
				floorCabinDrawable05 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, 3, (float) 2.4), 5));
				floorCabinDrawable06 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, (float) 0.6,
										(float) 2.4), 6));
				floorCabinDrawable07 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, (float) 1.2,
										(float) 2.4), 7));
				floorCabinDrawable08 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, (float) 1.2,
										(float) 2.4), 8));
				floorCabinDrawable09 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, (float) 0.6,
										(float) 2.4), 9));
				floorCabinDrawable10 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, 3, (float) 2.4), 10));
				floorCabinDrawable11 = new BitmapDrawable(getResources(),
						warpFloorCabin(
								fillTilesOnCabin(bitmap, 3, (float) 2.4), 11));

				wallCabinDrawable01 = new BitmapDrawable(getResources(),
						warpWallCabin(fillTilesOnCabin(bitmap, 5, (float) 2.4),
								1));
				wallCabinDrawable03 = new BitmapDrawable(getResources(),
						warpWallCabin(fillTilesOnCabin(bitmap, 3, (float) 2.4),
								3));
				wallCabinDrawable04 = new BitmapDrawable(getResources(),
						warpWallCabin(fillTilesOnCabin(bitmap, 3, (float) 2.4),
								4));
				wallCabinDrawable05 = new BitmapDrawable(getResources(),
						warpWallCabin(fillTilesOnCabin(bitmap, 3, (float) 2.4),
								5));
				wallCabinDrawable06 = new BitmapDrawable(
						getResources(),
						warpWallCabin(fillTilesOnCabin(bitmap, 3, (float) 1), 6));
				wallCabinDrawable07 = new BitmapDrawable(
						getResources(),
						warpWallCabin(fillTilesOnCabin(bitmap, 3, (float) 1), 7));
				wallCabinDrawable08 = new BitmapDrawable(
						getResources(),
						warpWallCabin(
								fillTilesOnCabin(bitmap, (float) 0.5, (float) 2),
								8));
				wallCabinDrawable09 = new BitmapDrawable(getResources(),
						warpWallCabin(fillTilesOnCabin(bitmap, 3, (float) 2.4),
								9));

				hand.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						backImg.removeLayer(layerFloorCabin01);
						backImg.removeLayer(layerFloorCabin02);
						backImg.removeLayer(layerFloorCabin03);
						backImg.removeLayer(layerFloorCabin04);
						backImg.removeLayer(layerFloorCabin05);
						backImg.removeLayer(layerFloorCabin06);
						backImg.removeLayer(layerFloorCabin07);
						backImg.removeLayer(layerFloorCabin08);
						backImg.removeLayer(layerFloorCabin09);
						backImg.removeLayer(layerFloorCabin10);
						backImg.removeLayer(layerFloorCabin11);

						backImg.removeLayer(layerWallCabin01);
						backImg.removeLayer(layerWallCabin03);
						backImg.removeLayer(layerWallCabin04);
						backImg.removeLayer(layerWallCabin05);
						backImg.removeLayer(layerWallCabin06);
						backImg.removeLayer(layerWallCabin07);
						backImg.removeLayer(layerWallCabin08);
						backImg.removeLayer(layerWallCabin09);

						layerWallCabin01 = null;
						layerWallCabin03 = null;
						layerWallCabin04 = null;
						layerWallCabin05 = null;
						layerWallCabin06 = null;
						layerWallCabin07 = null;
						layerWallCabin08 = null;
						layerWallCabin09 = null;

						layerFloorCabin01 = null;
						layerFloorCabin02 = null;
						layerFloorCabin03 = null;
						layerFloorCabin04 = null;
						layerFloorCabin05 = null;
						layerFloorCabin06 = null;
						layerFloorCabin07 = null;
						layerFloorCabin08 = null;
						layerFloorCabin09 = null;
						layerFloorCabin10 = null;
						layerFloorCabin11 = null;

					}
				});

				hand.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						layerFloorCabin01 = backImg.addLayer(5,
								floorCabinDrawable01, floorCabin1);
						layerFloorCabin02 = backImg.addLayer(6,
								floorCabinDrawable02, floorCabin2);
						layerFloorCabin03 = backImg.addLayer(7,
								floorCabinDrawable03, floorCabin3);
						layerFloorCabin04 = backImg.addLayer(8,
								floorCabinDrawable04, floorCabin4);
						layerFloorCabin05 = backImg.addLayer(9,
								floorCabinDrawable05, floorCabin5);
						layerFloorCabin06 = backImg.addLayer(10,
								floorCabinDrawable06, floorCabin6);
						layerFloorCabin07 = backImg.addLayer(11,
								floorCabinDrawable07, floorCabin7);
						layerFloorCabin08 = backImg.addLayer(12,
								floorCabinDrawable08, floorCabin8);
						layerFloorCabin09 = backImg.addLayer(13,
								floorCabinDrawable09, floorCabin9);
						layerFloorCabin10 = backImg.addLayer(14,
								floorCabinDrawable10, floorCabin10);
						layerFloorCabin11 = backImg.addLayer(15,
								floorCabinDrawable11, floorCabin11);

						layerWallCabin01 = backImg.addLayer(16,
								wallCabinDrawable01, wallCabin1);

						layerWallCabin03 = backImg.addLayer(18,
								wallCabinDrawable03, wallCabin3);
						layerWallCabin04 = backImg.addLayer(19,
								wallCabinDrawable04, wallCabin4);
						layerWallCabin05 = backImg.addLayer(20,
								wallCabinDrawable05, wallCabin5);
						layerWallCabin06 = backImg.addLayer(21,
								wallCabinDrawable06, wallCabin6);
						layerWallCabin07 = backImg.addLayer(22,
								wallCabinDrawable07, wallCabin7);
						layerWallCabin08 = backImg.addLayer(23,
								wallCabinDrawable08, wallCabin8);
						layerWallCabin09 = backImg.addLayer(24,
								wallCabinDrawable09, wallCabin9);
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

			layerFloor = backImg.addLayer(0, draw, floor);

		} else if (requestCode == CENTRE_WALL_REQUEST_CODE) {
			backImg.removeLayer(layerCenterWall);
			layerCenterWall = null;
			Bitmap wallBitmap = CustomPatternActivity.bitmapforDisplay;
			BitmapDrawable draw = new BitmapDrawable(getResources(),
					warpCentreWall(wallBitmap));
			wallBitmap.recycle();
			wallBitmap = null;

			layerCenterWall = backImg.addLayer(2, draw, centreWall);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void clearTempFiles() {
		String currentProject = "Ambience";// GlobalVariables.getProjectName();

		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/DMD/" + currentProject + "/";
		DatabaseHandler.deleteAmbienceFiles(path);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		clearTempFiles();
		super.onBackPressed();
		clearMemory();

		System.gc();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		layout.setOnTouchListener(null);
		System.gc();
	}


	private Bitmap fillTilesOnCabin(Bitmap tile, float height, float width) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen01.this);
		float screenWidthRatio = (float) 1;
		float wallWidthRatio = (float) (height / width);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / width) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / width) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / width) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / width) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / width) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / width) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / width) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / width) * wallWidthPixels);
			}

		}

		// NinePatchDrawable npd = (NinePatchDrawable)
		// getResources().getDrawable(
		// R.drawable.imgbckgnd);

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
				// Rect npdBounds = new Rect((int) left, (int) top,
				// (int) (left + tileWidthPixels),
				// (int) (top + tileHeightPixels));
				// npd.setBounds(npdBounds);
				// npd.draw(canvas);
				top += tileHeightPixels;
			}
			left += tileWidthPixels;
			top = 0;
		}

		System.gc();
		return Bitmap.createScaledBitmap(outputBitmap, (int) 1000, (int) 500,
				true);
	}

	private Bitmap fillTilesOnCentreSlab(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen01.this);
		float screenWidthRatio = (float) 1.75;
		float wallWidthRatio = (float) (slabHeightFinal / centreSlabWidth);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreSlabWidth) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreSlabWidth) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreSlabWidth) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreSlabWidth) * wallWidthPixels);
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

	private Bitmap fillTilesOnRightSlab(Bitmap tile) {
		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen01.this);
		float screenWidthRatio = (float) 1.75;
		float wallWidthRatio = (float) (slabHeightFinal / rightSlabWidth);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightSlabWidth) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightSlabWidth) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightSlabWidth) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / rightSlabWidth) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / rightSlabWidth) * wallWidthPixels);
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

	private Bitmap fillTilesOnCentreWall(Bitmap tile) {

		// included only for example sake

		int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen01.this);
		float screenWidthRatio = (float) 1.75;
		float wallWidthRatio = (float) (centreWallHeightFinal / centreWallWidthFinal);

		float wallWidthPixels = viewArea * screenWidthRatio;
		float wallHeightPixels = viewArea * screenWidthRatio * wallWidthRatio;

		float left = 0, top = 0;
		int tileHeightPixels = 0;
		int tileWidthPixels = 0;
		if (tile.getWidth() > tile.getHeight()) {

			if (tileWidth > tileHeight) {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreWallWidthFinal) * wallWidthPixels);
			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreWallWidthFinal) * wallWidthPixels);
			}

		} else {

			if (tileWidth > tileHeight) {

				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreWallWidthFinal) * wallWidthPixels);

			} else {
				tileHeightPixels = (int) ((GlobalVariables.mmToFeet(tileHeight) / centreWallWidthFinal) * wallWidthPixels);
				tileWidthPixels = (int) ((GlobalVariables.mmToFeet(tileWidth) / centreWallWidthFinal) * wallWidthPixels);
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

		int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen01.this);
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

		Bitmap bit = Bitmap.createScaledBitmap(tile, (int) tileWidthPixels,
				(int) tileHeightPixels, true);

		Bitmap outputBitmap = Bitmap.createBitmap((int) wallWidthPixels,
				(int) wallHeightPixels, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(outputBitmap);

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

		int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen01.this);
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

		// Bitmap groove = BitmapFactory.decodeResource(getResources(),
		// R.drawable.imgbckgnd);
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

	final float resolutionMultiplierCabin = 2;

	private Bitmap warpWallCabin(Bitmap leftWallImage, int number) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = null, leftP2 = null, leftP3 = null, leftP4 = null;

		if (number == 1) {
			leftP1 = new Point((int) (0 / resolutionMultiplierCabin) * density,
					(int) (105 / resolutionMultiplierCabin) * density);
			leftP2 = new Point((int) (0 / resolutionMultiplierCabin) * density,
					(int) (369 / resolutionMultiplierCabin) * density);
			leftP3 = new Point((int) (106 / resolutionMultiplierCabin)
					* density, (int) (370 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (103 / resolutionMultiplierCabin)
					* density, (int) (105 / resolutionMultiplierCabin)
					* density);
		} else if (number == 2) {

		} else if (number == 3) {
			leftP1 = new Point((int) (133 / resolutionMultiplierCabin)
					* density, (int) (118 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (133 / resolutionMultiplierCabin)
					* density, (int) (259 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (252 / resolutionMultiplierCabin)
					* density, (int) (259 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (252 / resolutionMultiplierCabin)
					* density, (int) (118 / resolutionMultiplierCabin)
					* density);
		} else if (number == 4) {
			leftP1 = new Point((int) (373 / resolutionMultiplierCabin)
					* density, (int) (118 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (375 / resolutionMultiplierCabin)
					* density, (int) (259 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (493 / resolutionMultiplierCabin)
					* density, (int) (259 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (493 / resolutionMultiplierCabin)
					* density, (int) (118 / resolutionMultiplierCabin)
					* density);
		} else if (number == 5) {
			leftP1 = new Point((int) (495 / resolutionMultiplierCabin)
					* density, (int) (119 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (494 / resolutionMultiplierCabin)
					* density, (int) (259 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (612 / resolutionMultiplierCabin)
					* density, (int) (260 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (613 / resolutionMultiplierCabin)
					* density, (int) (120 / resolutionMultiplierCabin)
					* density);
		} else if (number == 6) {
			leftP1 = new Point((int) (615 / resolutionMultiplierCabin)
					* density, (int) (120 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (614 / resolutionMultiplierCabin)
					* density, (int) (260 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (671 / resolutionMultiplierCabin)
					* density, (int) (260 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (672 / resolutionMultiplierCabin)
					* density, (int) (120 / resolutionMultiplierCabin)
					* density);
		} else if (number == 7) {
			leftP1 = new Point((int) (689 / resolutionMultiplierCabin)
					* density, (int) (117 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (689 / resolutionMultiplierCabin)
					* density, (int) (260 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (715 / resolutionMultiplierCabin)
					* density, (int) (262 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (716 / resolutionMultiplierCabin)
					* density, (int) (107 / resolutionMultiplierCabin)
					* density);
		} else if (number == 8) {
			leftP1 = new Point((int) (719 / resolutionMultiplierCabin)
					* density, (int) (241 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (719 / resolutionMultiplierCabin)
					* density, (int) (262 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (779 / resolutionMultiplierCabin)
					* density, (int) (262 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (779 / resolutionMultiplierCabin)
					* density, (int) (241 / resolutionMultiplierCabin)
					* density);
		} else if (number == 9) {
			leftP1 = new Point((int) (880 / resolutionMultiplierCabin)
					* density, (int) (44 / resolutionMultiplierCabin) * density);
			leftP2 = new Point((int) (878 / resolutionMultiplierCabin)
					* density, (int) (273 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (999 / resolutionMultiplierCabin)
					* density, (int) (280 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (999 / resolutionMultiplierCabin)
					* density, (int) (-4 / resolutionMultiplierCabin) * density);
		}

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

		// leftWallImage.recycle();
		// leftWallImage = null;

		Mat inputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Utils.bitmapToMat(tempLeftCanvasImage, inputMatLeft);

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage = null;

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

	private Bitmap warpFloorCabin(Bitmap leftWallImage, int number) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = null, leftP2 = null, leftP3 = null, leftP4 = null;

		if (number == 1) {
			leftP1 = new Point((int) (910 / resolutionMultiplierCabin)
					* density, (int) (504 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (907 / resolutionMultiplierCabin)
					* density, (int) (747 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (999 / resolutionMultiplierCabin)
					* density, (int) (747 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (999 / resolutionMultiplierCabin)
					* density, (int) (505 / resolutionMultiplierCabin)
					* density);
		} else if (number == 2) {
			leftP1 = new Point((int) (768 / resolutionMultiplierCabin)
					* density, (int) (436 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (766 / resolutionMultiplierCabin)
					* density, (int) (659 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (903 / resolutionMultiplierCabin)
					* density, (int) (807 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (904 / resolutionMultiplierCabin)
					* density, (int) (502 / resolutionMultiplierCabin)
					* density);
		} else if (number == 3) {
			leftP1 = new Point((int) (687 / resolutionMultiplierCabin)
					* density, (int) (395 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (686 / resolutionMultiplierCabin)
					* density, (int) (574 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (766 / resolutionMultiplierCabin)
					* density, (int) (657 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (767 / resolutionMultiplierCabin)
					* density, (int) (434 / resolutionMultiplierCabin)
					* density);
		} else if (number == 4) {
			leftP1 = new Point((int) (641 / resolutionMultiplierCabin)
					* density, (int) (373 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (640 / resolutionMultiplierCabin)
					* density, (int) (526 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (685 / resolutionMultiplierCabin)
					* density, (int) (572 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (686 / resolutionMultiplierCabin)
					* density, (int) (395 / resolutionMultiplierCabin)
					* density);
		} else if (number == 5) {
			leftP1 = new Point((int) (505 / resolutionMultiplierCabin)
					* density, (int) (372 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (506 / resolutionMultiplierCabin)
					* density, (int) (524 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (633 / resolutionMultiplierCabin)
					* density, (int) (522 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (634 / resolutionMultiplierCabin)
					* density, (int) (371 / resolutionMultiplierCabin)
					* density);
		} else if (number == 6) {
			leftP1 = new Point((int) (375 / resolutionMultiplierCabin)
					* density, (int) (467 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (375 / resolutionMultiplierCabin)
					* density, (int) (524 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (504 / resolutionMultiplierCabin)
					* density, (int) (524 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (504 / resolutionMultiplierCabin)
					* density, (int) (466 / resolutionMultiplierCabin)
					* density);
		} else if (number == 7) {
			leftP1 = new Point((int) (375 / resolutionMultiplierCabin)
					* density, (int) (407 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (375 / resolutionMultiplierCabin)
					* density, (int) (465 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (504 / resolutionMultiplierCabin)
					* density, (int) (464 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (504 / resolutionMultiplierCabin)
					* density, (int) (407 / resolutionMultiplierCabin)
					* density);
		} else if (number == 8) {
			leftP1 = new Point((int) (375 / resolutionMultiplierCabin)
					* density, (int) (372 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (375 / resolutionMultiplierCabin)
					* density, (int) (405 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (504 / resolutionMultiplierCabin)
					* density, (int) (405 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (504 / resolutionMultiplierCabin)
					* density, (int) (372 / resolutionMultiplierCabin)
					* density);
		} else if (number == 9) {
			leftP1 = new Point((int) (244 / resolutionMultiplierCabin)
					* density, (int) (499 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (244 / resolutionMultiplierCabin)
					* density, (int) (524 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (374 / resolutionMultiplierCabin)
					* density, (int) (524 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (374 / resolutionMultiplierCabin)
					* density, (int) (499 / resolutionMultiplierCabin)
					* density);
		} else if (number == 10) {
			leftP1 = new Point((int) (112 / resolutionMultiplierCabin)
					* density, (int) (371 / resolutionMultiplierCabin)
					* density);
			leftP2 = new Point((int) (113 / resolutionMultiplierCabin)
					* density, (int) (522 / resolutionMultiplierCabin)
					* density);
			leftP3 = new Point((int) (242 / resolutionMultiplierCabin)
					* density, (int) (522 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (241 / resolutionMultiplierCabin)
					* density, (int) (372 / resolutionMultiplierCabin)
					* density);
		} else if (number == 11) {
			leftP1 = new Point((int) (0 / resolutionMultiplierCabin) * density,
					(int) (370 / resolutionMultiplierCabin) * density);
			leftP2 = new Point((int) (0 / resolutionMultiplierCabin) * density,
					(int) (523 / resolutionMultiplierCabin) * density);
			leftP3 = new Point((int) (107 / resolutionMultiplierCabin)
					* density, (int) (523 / resolutionMultiplierCabin)
					* density);
			leftP4 = new Point((int) (106 / resolutionMultiplierCabin)
					* density, (int) (370 / resolutionMultiplierCabin)
					* density);
		}

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

		// leftWallImage.recycle();
		// leftWallImage = null;

		Mat inputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Utils.bitmapToMat(tempLeftCanvasImage, inputMatLeft);

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage = null;

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

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage=null;
		// leftWallImage.recycle();
		// leftWallImage=null;
		System.gc();
		return outputLeft;

	}

	private Bitmap warpRightSlab(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 783 * density, (int) 370 * density);
		Point leftP2 = new Point((int) 637 * density, (int) 370 * density);
		Point leftP3 = new Point((int) 906 * density, (int) 504 * density);
		Point leftP4 = new Point((int) 1221 * density, (int) 504 * density);
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

		// leftWallImage.recycle();
		// leftWallImage = null;

		Mat inputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Utils.bitmapToMat(tempLeftCanvasImage, inputMatLeft);

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage = null;

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

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage=null;
		// leftWallImage.recycle();
		// leftWallImage=null;
		System.gc();
		return outputLeft;

	}

	final float slabResMultiplier = (float) 2;

	private Bitmap warpCentreSlab(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) (150 / slabResMultiplier) * density,
				(int) (339 / slabResMultiplier) * density);
		Point leftP2 = new Point((int) (97 / slabResMultiplier) * density,
				(int) (370 / slabResMultiplier) * density);
		Point leftP3 = new Point((int) (783 / slabResMultiplier) * density,
				(int) (370 / slabResMultiplier) * density);
		Point leftP4 = new Point((int) (713 / slabResMultiplier) * density,
				(int) (343 / slabResMultiplier) * density);
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

		// leftWallImage.recycle();
		// leftWallImage = null;

		Mat inputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Utils.bitmapToMat(tempLeftCanvasImage, inputMatLeft);

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage = null;

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

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage=null;
		// leftWallImage.recycle();
		// leftWallImage=null;
		System.gc();
		return outputLeft;

	}

	final float resolutionMultiplier = (float) 1;

	private Bitmap warpFloor(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) (0 / resolutionMultiplier) * density,
				(int) (484 / resolutionMultiplier) * density);
		Point leftP2 = new Point((int) (-212 / resolutionMultiplier) * density,
				(int) (747 / resolutionMultiplier) * density);
		Point leftP3 = new Point((int) (1050 / resolutionMultiplier) * density,
				(int) (747 / resolutionMultiplier) * density);
		Point leftP4 = new Point((int) (731 / resolutionMultiplier) * density,
				(int) (484 / resolutionMultiplier) * density);
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

		// leftWallImage.recycle();
		// leftWallImage = null;

		Mat inputMatLeft = new Mat((int) leftBBWidth, maxLeftY - minLeftY,
				CvType.CV_32FC1);

		Utils.bitmapToMat(tempLeftCanvasImage, inputMatLeft);

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage = null;

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

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage=null;
		// leftWallImage.recycle();
		// leftWallImage=null;
		System.gc();
		return outputLeft;

	}

	private Bitmap warpRightWall(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 713 * density, (int) 83 * density);
		Point leftP2 = new Point((int) 713 * density, (int) 484 * density);
		Point leftP3 = new Point((int) 999 * density, (int) 705 * density);
		Point leftP4 = new Point((int) 999 * density, (int) -45 * density);
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

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage=null;
		// leftWallImage.recycle();
		// leftWallImage=null;
		System.gc();
		return outputLeft;

	}

	private Bitmap warpCentreWall(Bitmap leftWallImage) {

		OpenCVLoader.initDebug();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		float density = dm.density;

		// Step 1: Find points of the wall in the source image
		Point leftP1 = new Point((int) 0 * density, (int) 83 * density);
		Point leftP2 = new Point((int) 0 * density, (int) 484 * density);
		Point leftP3 = new Point((int) 713 * density, (int) 484 * density);
		Point leftP4 = new Point((int) 713 * density, (int) 83 * density);
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

		// tempLeftCanvasImage.recycle();
		// tempLeftCanvasImage=null;
		// leftWallImage.recycle();
		// leftWallImage=null;
		System.gc();
		return outputLeft;

	}




	
	public void ExportImage(){

		final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceKitchen01.this, SweetAlertDialog.PROGRESS_TYPE)
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

	private String fileSaveName = "Default";

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void initialise() {

		fillProgress = new ProgressDialog(AmbienceKitchen01.this);
		fillProgress.setMessage("Rendering...");
		fillProgress.setCanceledOnTouchOutside(false);

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
		rot_left.setOnClickListener(AmbienceKitchen01.this);
		rot_right = (ImageView) findViewById(R.id.rot_right);
		rot_right.setOnClickListener(AmbienceKitchen01.this);

		syncLibrary = (TextView) findViewById(R.id.syncLibrary);
		syncLibrary.setOnClickListener(AmbienceKitchen01.this);

		pattern_detailed_view = (LinearLayout) findViewById(R.id.pattern_detailed_view);
		backToView = (TextView) findViewById(R.id.backToView);
		backToView.setOnClickListener(AmbienceKitchen01.this);

		dmd_preview = (ImageView) findViewById(R.id.dmd_preview);
		dmd_preview.setOnClickListener(AmbienceKitchen01.this);

		dmd_export = (ImageView) findViewById(R.id.dmd_export);
		dmd_export.setOnClickListener(AmbienceKitchen01.this);

		dmd_eraser = (ImageView) findViewById(R.id.dmd_eraser);
		dmd_eraser.setOnClickListener(AmbienceKitchen01.this);

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
		backToViewPatrn.setOnClickListener(AmbienceKitchen01.this);

		rot_left_patrn = (ImageView) findViewById(R.id.rot_left_patrn);
		rot_left_patrn.setOnClickListener(AmbienceKitchen01.this);

		rot_right_patrn = (ImageView) findViewById(R.id.rot_right_patrn);
		rot_right_patrn.setOnClickListener(AmbienceKitchen01.this);

		prepareFilterDialog();

	}

	/*public void getAllTiles() {

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		dbPaginatedList.clear();
		dbOffset = 0;
		dbResult = db.getAllPatternPaginated(dbLimit, dbOffset);

		if(dbResult.size() > 0){
			dbPaginatedList.addAll(dbResult);
			view_errorText.setVisibility(View.GONE);
			view_placeholder.setVisibility(View.GONE);
			tileView.setVisibility(View.VISIBLE);
		}
		else{
			view_errorText.setVisibility(View.GONE);
			view_placeholder.setVisibility(View.VISIBLE);
			tileView.setVisibility(View.GONE);
		}

		// set up the RecyclerView
		tileView.setLayoutManager(new GridLayoutManager(this, 3));
		adapter = new MyRecyclerViewAdapter(this, dbPaginatedList, AmbienceLivingRoom02.this);
		tileView.setAdapter(adapter);

	}*/


	private void paginatedbList(int dbOffset){
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		dbResult = db.getAllPatternPaginated(dbLimit, dbOffset);

		if(dbResult.size() > 0){
			dbPaginatedList.addAll(dbResult);
		}

		adapter.notifyDataSetChanged();

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
		dbResult = db.searchkeyword(keyString, "A");
		if (dbResult.size() > 0) {
			PatternGridAdapter pgAdapter = new PatternGridAdapter(this,
					dbResult, AmbienceKitchen01.this);
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

		final SweetAlertDialog pDialog = new SweetAlertDialog(AmbienceKitchen01.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("Processing..");
		pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		pDialog.show();
		pDialog.setCancelable(false);

		Thread thread = new Thread(new Runnable() {

			public void run() {

				wallCabinBitmap01 = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_cabins_color).copy(
						Bitmap.Config.ARGB_8888, true);
				wallCabinBitmap02 = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_cabins_wall_mounted_02).copy(
						Bitmap.Config.ARGB_8888, true);

				wallCabinBitmap10 = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_cabins_wall_mounted_10).copy(
						Bitmap.Config.ARGB_8888, true);

				maskBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_mask).copy(Bitmap.Config.ARGB_8888, true);
				floorBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_floor_color).copy(
						Bitmap.Config.ARGB_8888, true);
				centerWallBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_centre_wall_color).copy(
						Bitmap.Config.ARGB_8888, true);
				rightWallBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_right_wall_color).copy(
						Bitmap.Config.ARGB_8888, true);
				furnitureBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_furniture).copy(Bitmap.Config.ARGB_8888,
						true);
				slabBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_slab_color).copy(Bitmap.Config.ARGB_8888,
						true);
				roofBitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.kitchen_01_ceiling).copy(Bitmap.Config.ARGB_8888,
						true);

				wallCabinDrawable01 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 1));
				wallCabinDrawable02 = new BitmapDrawable(getResources(),
						wallCabinBitmap02);
				wallCabinDrawable03 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 3));
				wallCabinDrawable04 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 4));
				wallCabinDrawable05 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 5));
				wallCabinDrawable06 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 6));
				wallCabinDrawable07 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 7));
				wallCabinDrawable08 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 8));
				wallCabinDrawable09 = new BitmapDrawable(getResources(), warpWallCabin(
						wallCabinBitmap01, 9));
				wallCabinDrawable10 = new BitmapDrawable(getResources(),
						wallCabinBitmap10);


				hand.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated
						// method
						// stub
						backImg.removeAllLayers();

					}
				});

				hand.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated
						// method
						// stub
						floorCabinDrawable01 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 1));
						floorCabinDrawable02 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 2));
						floorCabinDrawable03 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 3));
						floorCabinDrawable04 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 4));
						floorCabinDrawable05 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 5));
						floorCabinDrawable06 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 6));
						floorCabinDrawable07 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 7));
						floorCabinDrawable08 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 8));
						floorCabinDrawable09 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 9));
						floorCabinDrawable10 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 10));
						floorCabinDrawable11 = new BitmapDrawable(getResources(),
								warpFloorCabin(wallCabinBitmap01, 11));

						BitmapDrawable floorDrawable = new BitmapDrawable(getResources(),
								warpFloor(floorBitmap));
						BitmapDrawable centerWallDrawable = new BitmapDrawable(getResources(),
								warpCentreWall(centerWallBitmap));
						BitmapDrawable rightWallDrawable = new BitmapDrawable(getResources(),
								warpRightWall(rightWallBitmap));
						furnitureDrawable = new BitmapDrawable(getResources(), furnitureBitmap);
						centreSlabDrawable = new BitmapDrawable(getResources(),
								warpCentreSlab(slabBitmap));
						rightSlabDrawable = new BitmapDrawable(getResources(),
								warpRightSlab(slabBitmap));
						BitmapDrawable roofDrawable = new BitmapDrawable(getResources(),
								roofBitmap);
						maskDrawable = new BitmapDrawable(getResources(), maskBitmap);

						floorBitmap = null;
						centerWallBitmap = null;
						rightWallBitmap = null;
						furnitureBitmap = null;
						slabBitmap = null;
						roofBitmap = null;
						maskBitmap = null;
						wallCabinBitmap01 = null;
						wallCabinBitmap02 = null;
						wallCabinBitmap10 = null;

						DisplayMetrics dm = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);

						Matrix m = new Matrix();

						density = dm.density;
						scaleFactor = (1 / (float) density.intValue());
						scaleFactor = 1;
						m.preScale(scaleFactor, scaleFactor);

						int height = dm.heightPixels;
						int width = (int) ((985 / 693) * height * 1.15);

						layout.setLayoutParams(new RelativeLayout.LayoutParams(width, height));

						floor = new Matrix();
						floor.preScale(scaleFactor * resolutionMultiplier, scaleFactor
								* resolutionMultiplier);
						floor.preTranslate(-212 * density, 484 * density);

						centreWall = new Matrix();
						centreWall.preScale(scaleFactor, scaleFactor);
						centreWall.preTranslate(0 * density, 83 * density);

						rightWall = new Matrix();
						rightWall.preScale(scaleFactor, scaleFactor);
						rightWall.preTranslate(713 * density, -45 * density);

						centreSlab = new Matrix();
						centreSlab.preScale(scaleFactor * slabResMultiplier, scaleFactor
								* slabResMultiplier);
						centreSlab.preTranslate((97 / slabResMultiplier) * density,
								(339 / slabResMultiplier) * density);

						rightSlab = new Matrix();
						rightSlab.preScale(scaleFactor, scaleFactor);
						rightSlab.preTranslate(637 * density, 370 * density);

						wallCabin1 = new Matrix();
						wallCabin1.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin1.preTranslate((0 / resolutionMultiplierCabin) * density,
								(105 / resolutionMultiplierCabin) * density);

						wallCabin2 = new Matrix();
						wallCabin2.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin2.preTranslate(0 * density, 0 * density);

						wallCabin3 = new Matrix();
						wallCabin3.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin3.preTranslate((133 / resolutionMultiplierCabin) * density,
								(118 / resolutionMultiplierCabin) * density);

						wallCabin4 = new Matrix();
						wallCabin4.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin4.preTranslate((373 / resolutionMultiplierCabin) * density,
								(118 / resolutionMultiplierCabin) * density);

						wallCabin5 = new Matrix();
						wallCabin5.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin5.preTranslate((494 / resolutionMultiplierCabin) * density,
								(119 / resolutionMultiplierCabin) * density);

						wallCabin6 = new Matrix();
						wallCabin6.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin6.preTranslate((614 / resolutionMultiplierCabin) * density,
								(120 / resolutionMultiplierCabin) * density);

						wallCabin7 = new Matrix();
						wallCabin7.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin7.preTranslate((689 / resolutionMultiplierCabin) * density,
								(107 / resolutionMultiplierCabin) * density);

						wallCabin8 = new Matrix();
						wallCabin8.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin8.preTranslate((719 / resolutionMultiplierCabin) * density,
								(241 / resolutionMultiplierCabin) * density);

						wallCabin9 = new Matrix();
						wallCabin9.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin9.preTranslate((878 / resolutionMultiplierCabin) * density,
								(-4 / resolutionMultiplierCabin) * density);

						wallCabin10 = new Matrix();
						wallCabin10.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						wallCabin10.preTranslate(0 * density, 0 * density);

						floorCabin1 = new Matrix();
						floorCabin1.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin1.preTranslate((907 / resolutionMultiplierCabin) * density,
								(504 / resolutionMultiplierCabin) * density);

						floorCabin2 = new Matrix();
						floorCabin2.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin2.preTranslate((766 / resolutionMultiplierCabin) * density,
								(436 / resolutionMultiplierCabin) * density);

						floorCabin3 = new Matrix();
						floorCabin3.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin3.preTranslate((686 / resolutionMultiplierCabin) * density,
								(395 / resolutionMultiplierCabin) * density);

						floorCabin4 = new Matrix();
						floorCabin4.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin4.preTranslate((640 / resolutionMultiplierCabin) * density,
								(373 / resolutionMultiplierCabin) * density);

						floorCabin5 = new Matrix();
						floorCabin5.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin5.preTranslate((505 / resolutionMultiplierCabin) * density,
								(371 / resolutionMultiplierCabin) * density);

						floorCabin6 = new Matrix();
						floorCabin6.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin6.preTranslate((375 / resolutionMultiplierCabin) * density,
								(466 / resolutionMultiplierCabin) * density);

						floorCabin7 = new Matrix();
						floorCabin7.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin7.preTranslate((375 / resolutionMultiplierCabin) * density,
								(407 / resolutionMultiplierCabin) * density);

						floorCabin8 = new Matrix();
						floorCabin8.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin8.preTranslate((375 / resolutionMultiplierCabin) * density,
								(372 / resolutionMultiplierCabin) * density);

						floorCabin9 = new Matrix();
						floorCabin9.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin9.preTranslate((244 / resolutionMultiplierCabin) * density,
								(499 / resolutionMultiplierCabin) * density);

						floorCabin10 = new Matrix();
						floorCabin10.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin10.preTranslate((112 / resolutionMultiplierCabin) * density,
								(371 / resolutionMultiplierCabin) * density);

						floorCabin11 = new Matrix();
						floorCabin11.preScale(scaleFactor * resolutionMultiplierCabin,
								scaleFactor * resolutionMultiplierCabin);
						floorCabin11.preTranslate((0 / resolutionMultiplierCabin) * density,
								(370 / resolutionMultiplierCabin) * density);

						layerFloor = backImg.addLayer(0, floorDrawable, floor);
						layerRoof = backImg.addLayer(1, roofDrawable, m);
						layerCenterWall = backImg.addLayer(2, centerWallDrawable, centreWall);
						layerRightWall = backImg.addLayer(3, rightWallDrawable, rightWall);
						layerMask = backImg.addLayer(4, maskDrawable, m);

						layerFloorCabin01 = backImg.addLayer(5, floorCabinDrawable01,
								floorCabin1);
						layerFloorCabin02 = backImg.addLayer(6, floorCabinDrawable02,
								floorCabin2);
						layerFloorCabin03 = backImg.addLayer(7, floorCabinDrawable03,
								floorCabin3);
						layerFloorCabin04 = backImg.addLayer(8, floorCabinDrawable04,
								floorCabin4);
						layerFloorCabin05 = backImg.addLayer(9, floorCabinDrawable05,
								floorCabin5);
						layerFloorCabin06 = backImg.addLayer(10, floorCabinDrawable06,
								floorCabin6);
						layerFloorCabin07 = backImg.addLayer(11, floorCabinDrawable07,
								floorCabin7);
						layerFloorCabin08 = backImg.addLayer(12, floorCabinDrawable08,
								floorCabin8);
						layerFloorCabin09 = backImg.addLayer(13, floorCabinDrawable09,
								floorCabin9);
						layerFloorCabin10 = backImg.addLayer(14, floorCabinDrawable10,
								floorCabin10);
						layerFloorCabin11 = backImg.addLayer(15, floorCabinDrawable11,
								floorCabin11);

						layerWallCabin01 = backImg
								.addLayer(16, wallCabinDrawable01, wallCabin1);
						layerWallCabin02 = backImg.addLayer(17, wallCabinDrawable02, m);
						layerWallCabin03 = backImg
								.addLayer(18, wallCabinDrawable03, wallCabin3);
						layerWallCabin04 = backImg
								.addLayer(19, wallCabinDrawable04, wallCabin4);
						layerWallCabin05 = backImg
								.addLayer(20, wallCabinDrawable05, wallCabin5);
						layerWallCabin06 = backImg
								.addLayer(21, wallCabinDrawable06, wallCabin6);
						layerWallCabin07 = backImg
								.addLayer(22, wallCabinDrawable07, wallCabin7);
						layerWallCabin08 = backImg
								.addLayer(23, wallCabinDrawable08, wallCabin8);
						layerWallCabin09 = backImg
								.addLayer(24, wallCabinDrawable09, wallCabin9);
						layerWallCabin10 = backImg.addLayer(25, wallCabinDrawable10, m);

						layerCentreSlab = backImg.addLayer(26, centreSlabDrawable, centreSlab);
						layerRightSlab = backImg.addLayer(27, rightSlabDrawable, rightSlab);
						layerFurniture = backImg.addLayer(28, furnitureDrawable, m);
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

	private Dialog createPreviewDialog() {
		Dialog pDialog = new Dialog(AmbienceKitchen01.this);
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
		adapter = new MyRecyclerViewAdapter(this, dbPaginatedList, AmbienceKitchen01.this);
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
		patternAdapter = new RecyclerPaternAdapter(this, dbPatternList, AmbienceKitchen01.this);
		patternView.setAdapter(patternAdapter);

	}


	private static Bitmap RotateBitmapRight(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	Bitmap bitmap;
	String path;
	int axis = 0;
	float tileHeight;
	float tileWidth;
	String tileSize;

	private void clearMemory() {
		/*backImg = null;
		floorBitmap = null;
		shadowBitmap = null;
		layerFloor = null;
		layerShadows = null;
		topWallBitmap = null;
		baseViewBitmap = null;
		leftWallBitmap = null;
		leftWallBitmap = null;
		density = null;
		leftDrawer = null;
		inflater = null;
		patternContent2 = null;
		eText = null;
		filterSearch_button = null;
		patternContent = null;
		filterSearchButton2 = null;
		patternScrollView = null;
		mDrawerToggle = null;
		grid = null;
		bmp = null;

		furnitureDrawable = null;
		layerBaseView = null;
		layerTopWall = null;
		layerLeftWall = null;
		layerFloor = null;
		layerShadows = null;*/
	}

	public void syncLibrary() {

		String token;

		if(downloadingFlag != 1) {
			dDialog = new SweetAlertDialog(AmbienceKitchen01.this, SweetAlertDialog.PROGRESS_TYPE)
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
										AmbienceKitchen01.this);
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

								DatabaseManager.addSettingsInfo(AmbienceKitchen01.this, settingsdata);

								dDialog.setTitleText("Syncing Tile Library...");

								downloadTask = new DownloadTask(AmbienceKitchen01.this);
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

								downloadTask = new DownloadTask(AmbienceKitchen01.this);
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

		dialog_close.setOnClickListener(new OnClickListener(){
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
		filterButton.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				applyDialog();
			}

		});

		clear_filter = ((TextView) dialog.findViewById( R.id.clear_filter ));
		clear_filter.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchFlag = 0;
				clearFilter();
			}

		});

		img_clear_filter = ((ImageView) dialog.findViewById( R.id.img_clear_filter ));
		img_clear_filter.setOnClickListener( new OnClickListener() {

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
			gestureDetector = new GestureDetector(c, new GestureListener());
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
