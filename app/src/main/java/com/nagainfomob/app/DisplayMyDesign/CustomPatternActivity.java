package com.nagainfomob.app.DisplayMyDesign;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.androidadvance.topsnackbar.TSnackbar;
import com.nagainfomob.app.DisplayMyDesign.DrawView.LayoutDimensions;
import com.nagainfomob.app.MainActivity;
import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.MyRecyclerViewAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.helpers.BadgedTabLayout;
import com.nagainfomob.app.helpers.ChangeViewInterface;
import com.nagainfomob.app.helpers.MessageViewInterface;
import com.nagainfomob.app.helpers.MyCountInterface;
import com.nagainfomob.app.helpers.MyDrawingViewInterface;
import com.nagainfomob.app.helpers.RowItem;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.main.DashboardActivity;
import com.nagainfomob.app.model.LoadTile.LoadTileResult;
import com.nagainfomob.app.model.PatternModel;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.TileModel;
import com.nagainfomob.app.photoview.PhotoViewAttacher;
import com.nagainfomob.app.slider.PatternGridAdapter;
import com.nagainfomob.app.sql.DatabaseManager;
import com.nagainfomob.app.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_NO_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_SLOW_CONNECTION;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_UNKNOWN;
import static com.nagainfomob.app.api.ErrorUtils.ERROR_MESSAGE_WRONG_JSON_FORMAT;

@SuppressLint("NewApi")
public class CustomPatternActivity extends AppCompatActivity implements
		PatternimgNameInterface, MyDrawingViewInterface, ChangeViewInterface,
		MessageViewInterface, MyCountInterface, OnClickListener {

	//	private String storageURL = "https://storage-vro.s3.amazonaws.com/";
	private String storageURL = "https://dmd-file.s3.us-east-1.amazonaws.com/";
	private static int downloadingFlag = 0;
	private SweetAlertDialog dDialog;
	private SweetAlertDialog pDialog;
	private SessionManager session;
	DownloadTask downloadTask;
	private Handler hand = new Handler();

	PhotoViewAttacher mAttacher;
	LinearLayout patternScrollView;
	private LinearLayout content_slider;
	Boolean isDrawerOpen = false;
	Boolean isOpening = false;
	Boolean shouldExit = false;
	int displayWidth;
	RelativeLayout patternHeader;
	RelativeLayout main_layout;
	GridView gView;
	private static final String KEY_BRAND_NAME = "pro_brand";
	private static final String KEY_DIMEN = "pro_dimen";
	private static final String KEY_COLOR = "pro_color";
	private static final String KEY_TYPE = "pro_type";
	private static final String KEY_COMPANY = "pro_company";
	private static final String KEY_CATEGORY = "tile_category";
	public static String DrawArea;
	public static Bitmap bitmapforDisplay;

	private Handler handler = new Handler();
	DrawView drawView;
	String fileName = null;
	Boolean drawToolSelected = false;
	Boolean gridToolSelected = false;
	Boolean tileSelected = false;
	Dialog previewDialog;
	int axis = 0;
	LinkedHashMap<String, Integer> tiles;
	int viewArea;
	String tileSize;
	String string;
	private LayoutInflater inflater;
	private View patternContent;
	private Button filterSearchButton;
	private Button filterSearch_button;
	private Button filterSearchButton2;
	private View patternContent2;
	private float rotation,layoutRotation;

	private ImageView dmd_pointer;
	private ImageView dmd_selection;
	private ImageView dmd_preview;
	private ImageView dmd_save;
	private ImageView dmd_tick;
	private ImageView dmd_delete;
	private ImageView rot_left;
	private ImageView pattern_img;
	private ImageView rot_right;
	private ImageView dmd_grid;

	RecyclerView patternView;

	public EditText txt_sel_h;
	public EditText txt_sel_w;
	public EditText txt_pos_x;
	public EditText txt_pos_y;

	static Context context;
	String unit;

	public TextView txt_wall_height;
	public TextView txt_wall_width;
	public TextView draw_info;
	public TextView error_mesg;
	public TextView backToView;
	public TextView pattern_name;
	public TextView brand_name;
	public TextView pattern_dim;
	public TextView applyPattern;
	public TextView txt_tiles_used;
	public TextView syncLibrary;
	public TextView wTitle;
	public TextView unit_text;
	public TextView error_text;

	public LinearLayout tick_view;
	public LinearLayout delete_view;
	public LinearLayout pattern_detailed_view;
	public LinearLayout view_placeholder;

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
	private static float wPassG = 0;
	private static float hPassG = 0;
	private MyRecyclerViewAdapter adapter;

	private String pWidth;
	private String pHeight;

	private Dialog dialog;
	private Dialog gridDialog;
	private EditText txtSearch;
	private ImageView img_filter;
	private LinearLayout view_errorText;
	private BadgedTabLayout tabLayout;

	private String ptrn_category;
	private String ptrn_category_id;
	private String ptrn_type;
	private String ptrn_type_id;
	private String price;
	private String brand;
	private String is_edit;

	private String un = "Feet";

	private RelativeLayout fitLay;
	private RelativeLayout fillLay;
	private RelativeLayout positionLay;
	private ImageView fitXY;
	private ImageView fitX;
	private ImageView fitY;

	private ImageView fillXY;
	private ImageView fillY;
	private ImageView fillX;
	private ImageView grid_clear;
	private ImageView grid_reset;

	private ImageView b_down;
	private ImageView b_right;
	private ImageView b_left;
	private ImageView b_top;

	Boolean stop = false;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.viewArea = GlobalVariables.getDrawArea(getApplicationContext());
		shouldExit = false;
		session = new SessionManager(CustomPatternActivity.this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getSupportActionBar().hide();

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.activity_cutom_pattern);
		fileName = getIntent().getStringExtra("name");
		pWidth = getIntent().getStringExtra("width");
		pHeight = getIntent().getStringExtra("height");
		ptrn_category = getIntent().getStringExtra("category");
		ptrn_category_id = getIntent().getStringExtra("category_id");
		ptrn_type = getIntent().getStringExtra("type");
		ptrn_type_id = getIntent().getStringExtra("type_id");
		price = getIntent().getStringExtra("price");
		brand = getIntent().getStringExtra("brand");
		is_edit = getIntent().getStringExtra("is_edit");

		intialize();
		initListeners();

		drawView = new DrawView(this, fileName);
		drawView.setMode(true);

		float[] sizes = new float[2];
		sizes[0] = Float.parseFloat(pWidth);
		sizes[1] = Float.parseFloat(pHeight);

		getAllpatterns();

		patternView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

		RelativeLayout.LayoutParams params ;
		RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);

		if(sizes[0]==sizes[1])
		{
			drawView.setLayoutParams(new LayoutParams(viewArea, viewArea));
			params =(RelativeLayout.LayoutParams) rel
					.getLayoutParams();
			params.height = (int) viewArea;
			params.width = (int) viewArea;

		}


		else if(sizes[0]>sizes[1]){
			drawView.setLayoutParams(new LayoutParams(viewArea, (int) (viewArea*(sizes[1]/sizes[0]))));
			params =(RelativeLayout.LayoutParams) rel
					.getLayoutParams();
			params.height = (int) (viewArea*(sizes[1]/sizes[0]));
			params.width = (int) viewArea;
		}
		else{
			drawView.setLayoutParams(new LayoutParams((int) (viewArea*(sizes[0]/sizes[1])), viewArea));
			params =(RelativeLayout.LayoutParams) rel
					.getLayoutParams();
			params.height = (int) viewArea;
			params.width = (int) (viewArea*(sizes[0]/sizes[1]));
		}
		drawView.setDimensions(GlobalVariables.getUnit(), sizes[1], sizes[0]);

		String path;
		String currentProject = GlobalVariables.getProjectName();

		/*path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/DMD/Custom Pattern/";*/
		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/DMD/Custom__Pattern/" + brand + "/";

		String filePath = path + fileName + ".jpg";

		drawView.setBackgroundColor(Color.parseColor("#EEEEEE"));
		rel.addView(drawView);
		rel.setLayoutParams(params);

		if (new File(filePath).exists()) {

			GlobalVariables.setUnit("Feet");
			loadWall();

		}
		tiles = calculateInitialCost();
//		resetDimens(sizes[0],sizes[1]);
	}

	private void resetDimens(Float hori,Float verti) {
		// TODO Auto-generated method stub
		float ratio = hori / verti;
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
		RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);

		// drawView.setLayoutParams(new LayoutParams(rlength,rwidth));
		FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) rel
				.getLayoutParams();
		param.height = (int) rlength;
		param.width = (int) rwidth;
		rel.requestLayout();

	}

	private float[] getWallDimensions() {
		// TODO Auto-generated method stub

		String wall = fileName;
		String wallCode;
		float[] sizes=new float[2];

		float wallWidth = GlobalVariables.getWallWidth();
		float wallHeight = GlobalVariables.getWallHeight();
		float wallLength = GlobalVariables.getWallLength();
		float wallC = GlobalVariables.getWallC();
		float wallD = GlobalVariables.getWallD();

		String currentProject = GlobalVariables.getProjectName();

		float horiDim = wallWidth, vertDim = wallHeight;
		if (wall.equalsIgnoreCase("front")) {
			horiDim =  wallWidth;
			vertDim = wallHeight;

		} else if (wall.equalsIgnoreCase("back")) {
			horiDim = wallWidth;
			vertDim = wallHeight;

		} else if (wall.equalsIgnoreCase("left")) {
			horiDim =  wallWidth;
			vertDim = wallHeight;

			vertDim = wallHeight;
		} else if (wall.equalsIgnoreCase("right")) {
			horiDim = wallLength;
			vertDim = wallHeight;

		} else if (wall.equalsIgnoreCase("top")
				|| wall.equalsIgnoreCase("bottom")) {
			horiDim = wallWidth;
			vertDim = wallLength;

			// calculate area of remaining triangle and calculate tiles
			// needed for that .then subtract this from total tiles

		} else if (wall.equalsIgnoreCase("frontleft")) {
			horiDim = (float) (Math.sqrt(Math.pow((wallWidth - wallC), 2)
					+ Math.pow((wallLength - wallD), 2)));
			vertDim = wallHeight;
		}


		sizes[0]=horiDim;
		sizes[1]=vertDim;

		return sizes;
	}

	LinkedHashMap<String, Integer> calculateInitialCost() {
		RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
		LinkedHashMap<String, Integer> tiles1 = new LinkedHashMap<String, Integer>();
		for (int i = 1; i < rel.getChildCount(); i++) {
			View v = rel.getChildAt(i);
			LayoutDimensions dim = (LayoutDimensions) v.getTag();

			String tile = dim.selectedTile;
			int used = dim.tilesUsed;
			if (tiles1.containsKey(tile)) {
				int currentUsed = tiles1.get(tile);
				currentUsed = Math.abs(currentUsed);
				used += currentUsed;
				tiles1.remove(tile);
			}
			tiles1.put(tile, used * -1);

			Log.i("Tile Use", tile + ":::" + used);

		}

		return tiles1;

	}

	public void addLayout(int left, int top, int width, int height,
						  final String selectedTileName, final int orientation, String unit, final Float rot,
						  Float layoutRotation, final String tile_id, final String tile_type, float xx, float yy,
						  float mm_w, float mm_h, float mm_l, float mm_t) {
		try {
			RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
			{

				final LinearLayout linearLayout = new LinearLayout(
						getApplicationContext());
				linearLayout.setOrientation(LinearLayout.VERTICAL);
//				linearLayout.setBackgroundColor(Color.YELLOW);
//				Random random=new Random();
				Random randomService = new Random();
				StringBuilder sb = new StringBuilder();
				while (sb.length() < 6) {
					sb.append(Integer.toHexString(randomService.nextInt()));
				}
				sb.setLength(6);
				String s = "#" + sb.toString();
				linearLayout.setBackgroundColor(Color.parseColor(s));
				int thisDeviceDrawArea = GlobalVariables.getDrawArea(CustomPatternActivity.this);
//				int projectDrawArea=Integer.parseInt(new DatabaseHandler(PatternLibraryActivity.this).getViewAreaOfProject(GlobalVariables.getProjectName()));
				String string = new DatabaseHandler(CustomPatternActivity.this).getViewAreaOfProject(GlobalVariables.getProjectName());
				if (!string.equals("0")) {
					int projectDrawArea = Integer.parseInt(new DatabaseHandler(CustomPatternActivity.this).getViewAreaOfProject(GlobalVariables.getProjectName()));
					//				LayoutParams param = null;
//				RelativeLayout.LayoutParams params = null;
					if (thisDeviceDrawArea == projectDrawArea) {
						LayoutParams param = new LayoutParams(width, height);
						linearLayout.setLayoutParams(param);
						linearLayout.setClickable(false);
						linearLayout.setOnTouchListener(drawView.layoutTouched);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								width, height);
						params.leftMargin = left;
						params.topMargin = top;

						LayoutDimensions dim = drawView.new LayoutDimensions();
						dim.x = left;
						dim.y = top;
						dim.xx = xx;
						dim.yy = yy;
						dim.width = width;
						dim.height = height;
						dim.selectedTile = selectedTileName;
						dim.orientation = orientation;
						dim.rot = rot;
						dim.layRot = layoutRotation;
						dim.w = mm_w;
						dim.h = mm_h;
						dim.l = mm_l;
						dim.t = mm_t;

						linearLayout.setTag(dim);
						linearLayout.setRotation(layoutRotation);
						rel.setClickable(true);
						rel.addView(linearLayout, params);
						linearLayout.bringToFront();
					} else if (thisDeviceDrawArea > projectDrawArea) {
						LayoutParams param = new LayoutParams(width * 2, height * 2);
						linearLayout.setLayoutParams(param);
						linearLayout.setClickable(false);
						linearLayout.setOnTouchListener(drawView.layoutTouched);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								width * 2, height * 2);
						params.leftMargin = left * 2;
						params.topMargin = top * 2;

						LayoutDimensions dim = drawView.new LayoutDimensions();
						dim.x = left * 2;
						dim.y = top * 2;
						dim.xx = xx * 2;
						dim.yy = yy * 2;
						dim.width = width * 2;
						dim.height = height * 2;
						dim.selectedTile = selectedTileName;
						dim.orientation = orientation;
						dim.rot = rot;
						dim.layRot = layoutRotation;
						dim.w = mm_w * 2;
						dim.h = mm_h * 2;
						dim.l = mm_l * 2;
						dim.t = mm_t * 2;

						linearLayout.setTag(dim);
						linearLayout.setRotation(layoutRotation);
						rel.setClickable(true);
						rel.addView(linearLayout, params);
						linearLayout.bringToFront();
					} else if (thisDeviceDrawArea < projectDrawArea) {
						LayoutParams param = new LayoutParams(width / 2, height / 2);
						linearLayout.setLayoutParams(param);
						linearLayout.setClickable(false);
						linearLayout.setOnTouchListener(drawView.layoutTouched);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								width / 2, height / 2);
						params.leftMargin = left / 2;
						params.topMargin = top / 2;

						LayoutDimensions dim = drawView.new LayoutDimensions();
						dim.x = left / 2;
						dim.y = top / 2;
						dim.xx = xx / 2;
						dim.yy = yy / 2;
						dim.width = width / 2;
						dim.height = height / 2;
						dim.selectedTile = selectedTileName;
						dim.orientation = orientation;
						dim.rot = rot;
						dim.layRot = layoutRotation;
						dim.w = mm_w / 2;
						dim.h = mm_h / 2;
						dim.l = mm_l / 2;
						dim.t = mm_t /2;

						linearLayout.setTag(dim);
						linearLayout.setRotation(layoutRotation);
						rel.setClickable(true);
						rel.addView(linearLayout, params);
						linearLayout.bringToFront();
					}
				} else {
					LayoutParams param = new LayoutParams(width, height);
					linearLayout.setLayoutParams(param);
					linearLayout.setClickable(false);
					linearLayout.setOnTouchListener(drawView.layoutTouched);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							width, height);
					params.leftMargin = left;
					params.topMargin = top;

					LayoutDimensions dim = drawView.new LayoutDimensions();
					dim.x = left;
					dim.y = top;
					dim.xx = xx;
					dim.yy = yy;
					dim.width = width;
					dim.height = height;
					dim.selectedTile = selectedTileName;
					dim.orientation = orientation;
					dim.rot = rot;
					dim.layRot = layoutRotation;
					dim.w = mm_w;
					dim.h = mm_h;
					dim.l = mm_l;
					dim.t = mm_t;
					linearLayout.setTag(dim);
					linearLayout.setRotation(layoutRotation);
					rel.setClickable(true);
					rel.addView(linearLayout, params);
					linearLayout.bringToFront();
				}

				final String filePath = selectedTileName;
				try {
					final Bitmap bitmap = BitmapFactory.decodeFile(filePath);
					final Bitmap rBmp = RotateBitmapRight(bitmap, 90 * orientation);

					final SweetAlertDialog pDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.PROGRESS_TYPE)
							.setTitleText("Processing..");
					pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
					pDialog.show();
					pDialog.setCancelable(false);

					Thread thread = new Thread(new Runnable() {

						public void run() {

							drawView.setSelectedTile(rBmp, filePath, orientation,
									tileSize, tile_id, tile_type);

							hand.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									drawView.fillTileInLayout(linearLayout, true, rBmp, rot, selectedTileName, 0);
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

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {

		}
	}

	void loadWall() {
		try {
			String data = null;
			String path;
			String currentProject = GlobalVariables.getProjectName();

			/*path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/DMD/Custom Pattern/";*/
			path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/DMD/Custom__Pattern/" + brand + "/";
			String saveName;

			saveName = fileName + "";

			final String xmlFile = path + saveName + ".xml";

			// final String xmlFile="layoutData";
			// ArrayList<String> userData = new ArrayList<String>();
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
			} catch (@SuppressWarnings("hiding") IOException e) {
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
			float xx = 0, yy = 0;
			int left = 0, top = 0, width = 0, height = 0, orientation = 0;
			float mm_w = 0, mm_h = 0, mm_l = 0, mm_t = 0;
			String selectedTileName = null, unit = null, tile_id = null, tile_type = null;
			String readValue = null;
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
					if (name.equalsIgnoreCase("top")) {
						top = Integer.parseInt(readValue);
					}
					if (name.equalsIgnoreCase("xx")) {
						xx = Float.parseFloat(readValue);
					}
					if (name.equalsIgnoreCase("yy")) {
						yy = Float.parseFloat(readValue);
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
						/*Toast.makeText(getApplicationContext(),
								"orientation" + orientation, Toast.LENGTH_SHORT)
								.show();*/
					}
					if (name.equalsIgnoreCase("tile_id")) {
						tile_id = readValue;
					}

					if (name.equalsIgnoreCase("tile_type")) {
						tile_type = readValue;
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
					if (name.equalsIgnoreCase("mmW")) {
						mm_w = Float.parseFloat(readValue);
					}
					if (name.equalsIgnoreCase("mmH")) {
						mm_h = Float.parseFloat(readValue);
					}
					if (name.equalsIgnoreCase("mmL")) {
						mm_l = Float.parseFloat(readValue);
					}
					if (name.equalsIgnoreCase("mmT")) {
						mm_t = Float.parseFloat(readValue);
					}

					if (name.equalsIgnoreCase("layoutData")) {
						/*addLayout(left, top, width, height, selectedTileName,
								orientation, unit, rotation , layoutRotation, tile_id, tile_type, xx, yy);*/
						addLayout(left, top, width, height, selectedTileName,
								orientation, unit, rotation, layoutRotation, tile_id, tile_type, xx, yy,
								mm_w, mm_h, mm_l, mm_t);
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
		} catch (Exception e) {

		}
		// String userName=userData.get(0);
		// String password=userData.get(1);
	}

	void intialize() {

		isDrawerOpen = false;
		isOpening = false;
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		patternContent2 = inflater.inflate(R.layout.layout_patters, null);

		dDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("Processing...");
		dDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		dDialog.setCancelable(false);

		eText = (EditText) patternContent2.findViewById(R.id.textSearch);
		filterSearch_button = (Button) patternContent2
				.findViewById(R.id.filterButton);
		filterSearch_button.setOnClickListener(CustomPatternActivity.this);

		Button search = (Button) patternContent2
				.findViewById(R.id.searchButton);

		search.setOnClickListener(CustomPatternActivity.this);

		patternContent = inflater.inflate(R.layout.layout_filter, null);
		filterSearchButton2 = (Button) patternContent
				.findViewById(R.id.filterButton2);

		filterSearchButton2.setOnClickListener(CustomPatternActivity.this);

		wTitle = findViewById(R.id.wTitle);
		if(is_edit.equals("2")) {
			wTitle.setText("Custom Wall Designer");
		}

		patternScrollView = (LinearLayout) findViewById(R.id.patternScrollView);
		main_layout = (RelativeLayout) findViewById(R.id.main_content);
		content_slider = (LinearLayout) findViewById(R.id.content_slider);
		patternHeader = (RelativeLayout) findViewById(R.id.patternHeader);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		displayWidth = size.x;
		final int left = displayWidth;
		final int top = content_slider.getTop();
		final int right = content_slider.getRight();
		final int bottom = content_slider.getBottom();
		content_slider.layout(left, top, right, bottom);
		previewDialog = createPreviewDialog();

//		initUnitSpinners();

		unit = GlobalVariables.getUnit();

		dmd_pointer = (ImageView) findViewById(R.id.dmd_pointer);
		dmd_pointer.setOnClickListener(CustomPatternActivity.this);

		dmd_selection = (ImageView) findViewById(R.id.dmd_selection);
		dmd_selection.setOnClickListener(CustomPatternActivity.this);

		dmd_grid = (ImageView) findViewById(R.id.dmd_grid);
		dmd_grid.setOnClickListener(CustomPatternActivity.this);

		dmd_preview = (ImageView) findViewById(R.id.dmd_preview);
		dmd_preview.setOnClickListener(CustomPatternActivity.this);

		dmd_save = (ImageView) findViewById(R.id.dmd_save);
		dmd_save.setOnClickListener(CustomPatternActivity.this);

		patternView = (RecyclerView) findViewById(R.id.view_pattern);

		txt_sel_h = (EditText) findViewById(R.id.txt_sel_h);
		txt_sel_w = (EditText) findViewById(R.id.txt_sel_w);
		txt_pos_x = (EditText) findViewById(R.id.txt_pos_x);
		txt_pos_y = (EditText) findViewById(R.id.txt_pos_y);

		txt_sel_h.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					drawView.checkEditSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
							Float.parseFloat(txt_pos_x.getText().toString()),
							Float.parseFloat(txt_pos_y.getText().toString()), "h");
					return true;
				}
				return false;
			}
		});

		txt_sel_w.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					drawView.checkEditSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
							Float.parseFloat(txt_pos_x.getText().toString()),
							Float.parseFloat(txt_pos_y.getText().toString()), "w");
					return true;
				}
				return false;
			}
		});

		txt_pos_x.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					drawView.checkEditSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
							Float.parseFloat(txt_pos_x.getText().toString()),
							Float.parseFloat(txt_pos_y.getText().toString()), "l");
					return true;
				}
				return false;
			}
		});

		txt_pos_y.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					drawView.checkEditSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
							Float.parseFloat(txt_pos_x.getText().toString()),
							Float.parseFloat(txt_pos_y.getText().toString()), "t");
					return true;
				}
				return false;
			}
		});

		txt_wall_height = (TextView) findViewById(R.id.txt_wall_height);
		txt_wall_width = (TextView) findViewById(R.id.txt_wall_width);

		draw_info = (TextView) findViewById(R.id.draw_info);
		error_mesg = (TextView) findViewById(R.id.error_mesg);
		pattern_name = (TextView) findViewById(R.id.pattern_name);
		brand_name = (TextView) findViewById(R.id.brand_name);
		pattern_dim = (TextView) findViewById(R.id.pattern_dim);
		view_placeholder = (LinearLayout) findViewById(R.id.view_placeholder);

		dmd_tick = (ImageView) findViewById(R.id.dmd_tick);
		tick_view = (LinearLayout) findViewById(R.id.tick_view);
		tick_view.setOnClickListener(CustomPatternActivity.this);

		dmd_delete = (ImageView) findViewById(R.id.dmd_delete);
		delete_view = (LinearLayout) findViewById(R.id.delete_view);
		delete_view.setOnClickListener(CustomPatternActivity.this);

		pattern_detailed_view = (LinearLayout) findViewById(R.id.pattern_detailed_view);
		backToView = (TextView) findViewById(R.id.backToView);
		backToView.setOnClickListener(CustomPatternActivity.this);

		rot_left = (ImageView) findViewById(R.id.rot_left);
		rot_left.setOnClickListener(CustomPatternActivity.this);

		rot_right = (ImageView) findViewById(R.id.rot_right);
		rot_right.setOnClickListener(CustomPatternActivity.this);

		applyPattern = (TextView) findViewById(R.id.applyPattern);
		applyPattern.setOnClickListener(CustomPatternActivity.this);

		syncLibrary = (TextView) findViewById(R.id.syncLibrary);
		syncLibrary.setOnClickListener(CustomPatternActivity.this);

		pattern_img = (ImageView) findViewById(R.id.pattern_img);

		setDimesions();

		fitLay = (RelativeLayout) findViewById(R.id.fitLay);
		fillLay = (RelativeLayout) findViewById(R.id.fillLay);
		positionLay = (RelativeLayout) findViewById(R.id.positionLay);

		txt_tiles_used = (TextView) findViewById(R.id.txt_tiles_used);

		fitXY = (ImageView) findViewById(R.id.fitXY);
		fitXY.setOnClickListener(CustomPatternActivity.this);

		fitX = (ImageView) findViewById(R.id.fitX);
		fitX.setOnClickListener(CustomPatternActivity.this);

		fitY = (ImageView) findViewById(R.id.fitY);
		fitY.setOnClickListener(CustomPatternActivity.this);

		fillXY = (ImageView) findViewById(R.id.fillXY);
		fillXY.setOnClickListener(CustomPatternActivity.this);

		fillY = (ImageView) findViewById(R.id.fillY);
		fillY.setOnClickListener(CustomPatternActivity.this);

		fillX = (ImageView) findViewById(R.id.fillX);
		fillX.setOnClickListener(CustomPatternActivity.this);

		grid_clear = (ImageView) findViewById(R.id.grid_clear);
		grid_clear.setOnClickListener(CustomPatternActivity.this);

		grid_reset = (ImageView) findViewById(R.id.grid_reset);
		grid_reset.setOnClickListener(CustomPatternActivity.this);

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
					searchFlag = 0;
					searchTiles(s.toString());
				}
				else {
					getAllpatterns();
				}
			}
		});

		prepareFilterDialog();



	}

	private void initListeners() {

		b_down = findViewById(R.id.b_down);
		b_down.setTag("1");
		b_down.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						stop = false;
						handler.post(new MovementUpdater(b_down));
						return true;
					}
					case MotionEvent.ACTION_UP: {
						stop = true;
						return true;
					}
				}
				return false;
			}
		});

		b_right = findViewById(R.id.b_right);
		b_right.setTag("2");
		b_right.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						stop = false;
						handler.post(new MovementUpdater(b_right));
						return true;
					}
					case MotionEvent.ACTION_UP: {
						stop = true;
						return true;
					}
				}
				return false;
			}
		});

		b_left = findViewById(R.id.b_left);
		b_left.setTag("3");
		b_left.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						stop = false;
						handler.post(new MovementUpdater(b_left));
						return true;
					}
					case MotionEvent.ACTION_UP: {
						stop = true;
						return true;
					}
				}
				return false;
			}
		});

		b_top = findViewById(R.id.b_top);
		b_top.setTag("4");
		b_top.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: {
						stop = false;
						handler.post(new MovementUpdater(b_top));
						return true;
					}
					case MotionEvent.ACTION_UP: {
						stop = true;
						return true;
					}
				}
				return false;
			}
		});

		unit_text = findViewById(R.id.unit);
		unit_text.setText(GlobalVariables.getUnit());

	}

	public void mainLayoutPressed(View v) {
		/*
		 * if (twod_layout.getVisibility() == View.GONE) {
		 * //twod_layout.setVisibility(View.VISIBLE);
		 * //buttonLayout.setVisibility(View.GONE);
		 *
		 * } else { //twod_layout.setVisibility(View.GONE);
		 * //buttonLayout.setVisibility(View.VISIBLE); }
		 */
	}

	private EditText eText;

	void removeTile() {
		patternScrollView.removeAllViews();
	}

	void loadTile() {
		patternScrollView.removeAllViews();
		gView = (GridView) patternContent2.findViewById(R.id.slider_pattern);
		patternScrollView.addView(patternContent2);
		getAllpattern();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// action with ID action_refresh was selected
			case R.id.wallInfo:
				getWallInfo();
				break;
			case R.id.actionPen:
				drawToolSelected = true;
				drawView.isDrawingSelected(drawToolSelected);
//				drawView.selectTile(false);
				break;
			// action with ID action_settings was selected
			case R.id.actionHome:
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
				finish();

				break;
			case R.id.actionPointer:

				drawToolSelected = false;
				drawView.isDrawingSelected(drawToolSelected);
				unsetTrasnparency();
				drawView.setPoints();
				drawView.addLayoutView();
				drawView.resetPoint();

				break;
			case R.id.actionDone:
				donePressed();
				break;
			case R.id.actionbarTool:

				RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
				rel.invalidate();
				rel.setDrawingCacheEnabled(true);
				Bitmap bitmap = rel.getDrawingCache();
				showPreview(bitmap);

				break;

			default:
				break;
		}

		return true;
	}

	LinkedHashMap<String, Integer> calculateCost() {
		RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
		LinkedHashMap<String,Integer> tiles=new LinkedHashMap<String,
				Integer>();
		for (int i = 1; i < rel.getChildCount(); i++) {
			View v = rel.getChildAt(i);
			LayoutDimensions dim = (LayoutDimensions) v.getTag();

			String tile = dim.selectedTile;
			int used = dim.tilesUsed;
			/*if (tiles.containsKey(tile)) {
				int currentUsed = tiles.get(tile);
				used += currentUsed;
				tiles.remove(tile);
			}*/
			tiles.put(tile, used);



		}
		// totalTileUsed(tiles);
		// delegate.tileCount(tiles);
		LinkedHashMap<String, Integer> tiles1 = new LinkedHashMap<String, Integer>();
		for (int i = 1; i < rel.getChildCount(); i++) {
			View v = rel.getChildAt(i);
			LayoutDimensions dim = (LayoutDimensions) v.getTag();

			String tile = dim.selectedTile;
			int used = dim.tilesUsed;
			if (tiles1.containsKey(tile)) {
				int currentUsed = tiles1.get(tile);
				currentUsed = Math.abs(currentUsed);
				used += currentUsed;
				tiles1.remove(tile);
			}
			tiles1.put(tile, used * -1);

			Log.d("Tile Used from", tile + ":::" + used);

		}
		// totalTileUsed(tiles);
		// delegate.tileCount(tiles);

		return tiles1;

	}

	Dialog createPreviewDialog() {
		Dialog dialog = new Dialog(CustomPatternActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		dialog.setContentView(R.layout.preview_dialog);// loads the layout we

		ImageView img = (ImageView) dialog.findViewById(R.id.previewImageView);

		img.setScaleType(ScaleType.CENTER_INSIDE);
		mAttacher = new PhotoViewAttacher(img);
		return dialog;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		menu.getItem(0).setVisible(false);
		return true;
	}

	public void getAllpattern() {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		int countRec = db.getCount();
		dbResult = db.getAllPattern();

		PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
				CustomPatternActivity.this);
		gView.refreshDrawableState();
		pgAdapter.notifyDataSetChanged();
		// grid.setAdapter(adapter);
		gView.setAdapter(null);
		gView.setAdapter(pgAdapter);

	}

	void donePressed() {

		if(is_edit.equals("2")) {
			RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
			drawView.setBgforCurrentLay("", rel);
			rel.setDrawingCacheEnabled(true);
			bitmapforDisplay = rel.getDrawingCache();

			Intent i = new Intent();
			setResult(1, i);
			finish();
		}
		else {
			session.setBackStackId("2");
			RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
			drawView.setBgforCurrentLay("", rel);
			rel.setDrawingCacheEnabled(true);
			tiles = calculateCost();
		/*String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/DMD/Custom Pattern/";*/

			String path = Environment.getExternalStorageDirectory().getAbsolutePath()
					+ "/DMD/Custom__Pattern/" + brand + "/";

			boolean success = drawView.savelayout(rel, fileName, path);
			if (success) {
				Bitmap bitmap = rel.getDrawingCache();
				String filePath = saveWall(bitmap);

				if (is_edit.equals("0")) {
					savedb();
				}

				dDialog.dismissWithAnimation();

				Intent i = new Intent();
				setResult(0, i);
				finish();
			}
		}
	}

	private void savedb(){
		DatabaseHandler db = new DatabaseHandler(
				this);
		PatternModel patternModel = new PatternModel();

		String dimen = pHeight.trim() + " x " + pWidth.trim();

		patternModel.setPattern_name(fileName);
		patternModel.setPattern_category(ptrn_category);
		patternModel.setCategory_id(ptrn_category_id);
		patternModel.setPattern_type(ptrn_type);
		patternModel.setType_id(ptrn_type_id);
		patternModel.setPattern_dimen(dimen);
		patternModel.setPattern_price(price);
		patternModel.setPattern_brand(brand);

		db.insertPattern(patternModel);
	}

	@Override
	public void onBackPressed() {

		new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("Are you sure?")
				.setContentText("All your progress will be lost if leave the section without saving!")
				.setCancelText("Exit")
				.setConfirmText("Save & Exit")
				.showCancelButton(true)
				.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						sDialog.dismissWithAnimation();
						exitWithSave();
					}
				})
				.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						sDialog.dismissWithAnimation();
						exitWithoutSave();
					}
				})
				.show();

	}

	public void exitWithSave(){
		dDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("Processing...");
		dDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
		dDialog.setCancelable(false);
		dDialog.show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				donePressed();
			}
		}, 1000);
	}

	public void exitWithoutSave(){
		if(is_edit.equals("0")) {
			android.app.FragmentManager fm = getFragmentManager();
			if (fm.getBackStackEntryCount() > 0) {
				fm.popBackStack();
			}
			Intent i = new Intent();
			setResult(0, i);
			finish();
		}
		if(is_edit.equals("1") || is_edit.equals("2")) {
			Intent i = new Intent();
			setResult(0, i);
			finish();
		}
	}

	private String saveWall(Bitmap bmp) {
		String path;
		String currentProject = GlobalVariables.getProjectName();

		/*path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/DMD/Custom Pattern/";*/
		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/DMD/Custom__Pattern/" + brand + "/";

		File directoryPath = new File(path);
		if (!directoryPath.exists()) {
			directoryPath.mkdirs();
			GlobalVariables.createNomediafile(path);
		}

		String filePath = path + fileName + ".jpg";
		try {
			File file = new File(filePath);
			FileOutputStream fOut = new FileOutputStream(file);
			bmp = Bitmap.createScaledBitmap(bmp, this.viewArea, this.viewArea,
					true);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
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
							String type, String tile_id, String tile_type, String price) {

		addLayout();

		final String filePath = path;
		String tname;
		bmp = BitmapFactory.decodeFile(filePath);
		drawToolSelected = false;
		drawView.isDrawingSelected(drawToolSelected);
		drawView.selectTile(true);
		this.tileSize = tileSize;
		drawView.setSelectedTile(bmp, filePath, axis, tileSize, "", "");
		axis = 0;

		String[] tName = new File(path).getName().split("/");
		tname = tName[tName.length - 1].replace(".jpg", "");

		pattern_name.setText(tname);
		brand_name.setText(brand);
		pattern_dim.setText(tileSize + " mm");
		pattern_img.setImageBitmap(bmp);

		rot_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Bitmap rBmp = RotateBitmapRight(bmp, 90);
				axis++;
				// bmp.recycle();
				bmp = rBmp;
				pattern_img.setImageBitmap(bmp);
				drawView.setSelectedTile(bmp, filePath, axis, tileSize, "", "");
				// rBmp.recycle();
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
				pattern_img.setImageBitmap(bmp);
				drawView.setSelectedTile(bmp, filePath, axis, tileSize, "", "");
			}
		});

		applyPattern.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				fitLay.setVisibility(View.GONE);

				final SweetAlertDialog pDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.PROGRESS_TYPE)
						.setTitleText("Processing..");
				pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
				pDialog.show();
				pDialog.setCancelable(false);

				Thread thread = new Thread(new Runnable() {

					public void run() {

//						addLayout();
						hand.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
//								drawView.applyPattern();

								if(gridToolSelected){
									drawView.applyPatternGrid();
								} else{
									fitLay.setVisibility(View.GONE);
									addLayout();
									drawView.applyPattern();
								}
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
		});

		patternView.setVisibility(View.GONE);
		pattern_detailed_view.setVisibility(View.VISIBLE);

	}


	public static Bitmap RotateBitmapRight(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}

	void getWallInfo() {
		String wall = fileName;
		String wallCode;

		float wallWidth = GlobalVariables.getWallWidth();
		float wallHeight = GlobalVariables.getWallHeight();
		float wallLength = GlobalVariables.getWallLength();
		float wallC = GlobalVariables.getWallC();
		float wallD = GlobalVariables.getWallD();

		String currentProject = GlobalVariables.getProjectName();

		float horiDim = wallWidth, vertDim = wallHeight;
		if (wall.equalsIgnoreCase("front")) {
			horiDim =  wallWidth;
			vertDim = wallHeight;
		} else if (wall.equalsIgnoreCase("back")) {
			horiDim = wallWidth;
			vertDim = wallHeight;

		} else if (wall.equalsIgnoreCase("left")) {
			horiDim =  wallWidth;
			vertDim = wallHeight;
		} else if (wall.equalsIgnoreCase("right")) {
			horiDim = wallLength;
			vertDim = wallHeight;

		} else if (wall.equalsIgnoreCase("top")
				|| wall.equalsIgnoreCase("bottom")) {
			horiDim = wallWidth;
			vertDim = wallLength;

			// calculate area of remaining triangle and calculate tiles
			// needed for that .then subtract this from total tiles

		} else if (wall.equalsIgnoreCase("frontleft")) {
			horiDim = (float) (Math.sqrt(Math.pow((wallWidth - wallC), 2)
					+ Math.pow((wallLength - wallD), 2)));
			vertDim = wallHeight;
		}

		Log.i("wall name", wall + " wall");

		final Dialog dialog = new Dialog(CustomPatternActivity.this);
		dialog.setTitle("Wall Info");
		dialog.setContentView(R.layout.wall_info_dialog);
		TextView widthTextView = (TextView) dialog
				.findViewById(R.id.widthTextView);
		TextView lengthTextView = (TextView) dialog
				.findViewById(R.id.lengthTextView);
		TextView nameTextView = (TextView) dialog
				.findViewById(R.id.wallNameField);

		wallCode = GlobalVariables.getWallCode(wall, currentProject);

		nameTextView.setText(wallCode);

		widthTextView.setText("Width = " + vertDim + " mm / "
				+ GlobalVariables.mmToFeet(vertDim) + " feet / "
				+ GlobalVariables.mmToInches(vertDim) + " inches");
		lengthTextView.setText("Length = " + horiDim + " mm / "
				+ GlobalVariables.mmToFeet(horiDim) + " feet / "
				+ GlobalVariables.mmToInches(horiDim) + " inches");
		dialog.show();

	}

	public void setTrasnparency(){

		RelativeLayout rel = findViewById(R.id.main_content);

		boolean doBreak = false;
		while (!doBreak) {
			int childCount = rel.getChildCount();
			int i;
			for(i=0; i<childCount; i++) {
				View currentChild = rel.getChildAt(i);
				if (currentChild instanceof LinearLayout) {
					currentChild.setAlpha((float) 0.3);
				}
			}
			if (i == childCount) {
				doBreak = true;
			}
		}
	}

	public void unsetTrasnparency(){

		RelativeLayout rel = findViewById(R.id.main_content);

		boolean doBreak = false;
		while (!doBreak) {
			int childCount = rel.getChildCount();
			int i;
			for(i=0; i<childCount; i++) {
				View currentChild = rel.getChildAt(i);
				if (currentChild instanceof LinearLayout) {
					currentChild.setAlpha((float) 1.0);
				}
			}
			if (i == childCount) {
				doBreak = true;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.dmd_pointer:
				/*fitLay.setVisibility(View.GONE);
				positionLay.setVisibility(View.GONE);
				addLayout();*/

				gridToolSelected = false;
				drawView.isAutoGridSelected(gridToolSelected);
				fitLay.setVisibility(View.GONE);
				fillLay.setVisibility(View.GONE);
				positionLay.setVisibility(View.GONE);
				if(gridToolSelected == true) {
					clearUnused();
				}
				addLayout();
				changeColor(dmd_pointer);
				break;
			case R.id.dmd_grid:
				addLayout();
				changeColor(dmd_grid);
				fitLay.setVisibility(View.GONE);
				positionLay.setVisibility(View.GONE);
				if(gridToolSelected == true){
					fillLay.setVisibility(View.GONE);
					gridToolSelected = false;
					drawView.isAutoGridSelected(gridToolSelected);
					dmd_grid.setColorFilter(getApplicationContext().getResources().getColor(R.color.white));
					clearUnused();
				}
				else{
					final RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
					String phText = "Enter Grid Size (in Millimeter)";
					if (GlobalVariables.getDesignerUnit().equalsIgnoreCase("Feet")) {
						phText = "Enter Grid Size (in Feet)";
					} else if (GlobalVariables.getDesignerUnit().equalsIgnoreCase("Inches")) {
						phText = "Enter Grid Size (in Inches)";
					}
					prepareDialog();
					if(rel.getChildCount() > 1){
						final SweetAlertDialog alertDialog;
						alertDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.WARNING_TYPE);
						final String finalPhText = phText;
						alertDialog
								.setTitleText("Are you sure?")
								.setContentText("Your current area markings and designs will be removed if you use the AutoGrid tool. Do you wish to continue?")
								.setCancelText("Cancel")
								.setConfirmText("Proceed")
								.showCancelButton(true)
								.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {

										sDialog.dismiss();
										gridDialog.show();
									}
								})
								.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
									@Override
									public void onClick(SweetAlertDialog sDialog) {
										sDialog.dismissWithAnimation();
									}
								})
								.show();

						Button btn_confirm = (Button) alertDialog.findViewById(R.id.confirm_button);
						btn_confirm.setBackgroundResource(R.drawable.button_background_red);
						Button btn_cancel = (Button) alertDialog.findViewById(R.id.cancel_button);
						btn_cancel.setBackgroundResource(R.drawable.button_background_grey);
					} else{
						gridDialog.show();
					}
				}
				break;
			case R.id.dmd_selection:
				/*clearText();
				changeColor(dmd_selection);
				if(drawToolSelected == true){
					drawToolSelected = false;
					drawView.resetPoint();
					fitLay.setVisibility(View.GONE);
					unsetTrasnparency();
					drawView.isDrawingSelected(drawToolSelected);
					drawView.clearSelection();
					dmd_selection.setColorFilter(getApplicationContext().getResources().getColor(R.color.white));
				}
				else {
					drawToolSelected = true;
					setTrasnparency();
					positionLay.setVisibility(View.GONE);
					drawView.isDrawingSelected(drawToolSelected);
					drawView.clearSelection();

					dmd_delete.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
					TypedValue outValue = new TypedValue();
					getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
					dmd_delete.setBackgroundResource(outValue.resourceId);
				}*/
				fillLay.setVisibility(View.GONE);
				clearText();
				if(gridToolSelected == true) {
					clearUnused();
				}
				drawView.resetPoint();
				changeColor(dmd_selection);
				gridToolSelected = false;
				drawView.isAutoGridSelected(gridToolSelected);
				if(drawToolSelected == true){
					drawToolSelected = false;
					drawView.resetPoint();
					fitLay.setVisibility(View.GONE);
					unsetTrasnparency();
					drawView.isDrawingSelected(drawToolSelected);
					drawView.clearSelection();
					dmd_selection.setColorFilter(getApplicationContext().getResources().getColor(R.color.white));
				}
				else {
					drawToolSelected = true;
					setTrasnparency();
					positionLay.setVisibility(View.GONE);
					drawView.isDrawingSelected(drawToolSelected);
					drawView.clearSelection();

					dmd_delete.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.white));
					TypedValue outValue = new TypedValue();
					getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
					dmd_delete.setBackgroundResource(outValue.resourceId);

					changeMessage("Please drag on the wall to mark your area", 1);
				}
				break;
			case R.id.dmd_preview:
				/*RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
				rel.invalidate();
				rel.setDrawingCacheEnabled(true);
				Bitmap bitmap = rel.getDrawingCache();
				showPreview(bitmap);*/
				if(gridToolSelected == true) {
					clearUnused();
				}
				gridToolSelected = false;
				drawView.isAutoGridSelected(gridToolSelected);
				drawView.clearSelection();
				RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
				rel.invalidate();
				rel.setDrawingCacheEnabled(true);
				Bitmap bitmap = rel.getDrawingCache();
				showPreview(bitmap);
				break;
			case R.id.dmd_save:
				changeColor(dmd_save);

				dDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.PROGRESS_TYPE)
						.setTitleText("Processing...");
				dDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
				dDialog.setCancelable(false);
				dDialog.show();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						donePressed();
					}
				}, 1000);
				break;
			case R.id.tick_view:
				fitLay.setVisibility(View.GONE);
				addLayout();
				break;
			case R.id.delete_view:
				fitLay.setVisibility(View.GONE);
				drawView.deleteLayout();
				break;
			case R.id.backToView:
				patternView.setVisibility(View.VISIBLE);
				pattern_detailed_view.setVisibility(View.GONE);
				break;
			case R.id.syncLibrary:
				syncLibrary();
				break;
			case R.id.img_filter:
				showFilterDialog();
				break;
			case R.id.fitXY:
				drawView.fitXY();
				break;
			case R.id.fitX:
				drawView.fitX();
				break;
			case R.id.fitY:
				drawView.fitY();
				break;
			case R.id.fillXY:
				drawView.fillCell("xy", (RelativeLayout) findViewById(R.id.main_content));
				break;
			case R.id.fillY:
				drawView.fillCell("y", (RelativeLayout) findViewById(R.id.main_content));
				break;
			case R.id.fillX:
				drawView.fillCell("x", (RelativeLayout) findViewById(R.id.main_content));
				break;
			case R.id.grid_reset:
				final SweetAlertDialog alertDialog;
				alertDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.WARNING_TYPE);
				alertDialog
						.setTitleText("Are you sure?")
						.setContentText("Your current area markings and designs will be removed if you use the AutoGrid tool. Do you wish to continue?")
						.setCancelText("Cancel")
						.setConfirmText("Proceed")
						.showCancelButton(true)
						.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {

								RelativeLayout rel1 = findViewById(R.id.main_content);
								drawView.clearDrawView(rel1);
								gridToolSelected = true;
								drawView.isAutoGridSelected(gridToolSelected);
								drawView.fillGrid(wPassG, hPassG);
								fitLay.setVisibility(View.GONE);
								positionLay.setVisibility(View.GONE);
								fillLay.setVisibility(View.VISIBLE);
								changeMessage("", 1);
								sDialog.dismiss();
							}
						})
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.dismissWithAnimation();
							}
						})
						.show();
				Button btn_confirm = (Button) alertDialog.findViewById(R.id.confirm_button);
				btn_confirm.setBackgroundResource(R.drawable.button_background_red);
				Button btn_cancel = (Button) alertDialog.findViewById(R.id.cancel_button);
				btn_cancel.setBackgroundResource(R.drawable.button_background_grey);
				break;
			case R.id.grid_clear:
				drawView.deleteGridTile();
				break;

			default:
				break;
		}
	}

	public void clearUnused(){
		RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
		drawView.setBgforCurrentLay(txt_tiles_used.getText().toString(), rel);
	}

	public void changeColor(ImageView imageiew){
		dmd_pointer.setColorFilter(getApplicationContext().getResources().getColor(R.color.white));
		dmd_selection.setColorFilter(getApplicationContext().getResources().getColor(R.color.white));
		dmd_preview.setColorFilter(getApplicationContext().getResources().getColor(R.color.white));
		dmd_save.setColorFilter(getApplicationContext().getResources().getColor(R.color.white));

		imageiew.setColorFilter(getApplicationContext().getResources().getColor(R.color.app_orange));
	}

	public void getAllpatterns() {

		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		dbPaginatedList.clear();
		dbOffset = 0;
		dbResult = db.getAllPatternPaginated(dbLimit, dbOffset);

		if(dbResult.size() > 0){
			dbPaginatedList.addAll(dbResult);
			view_errorText.setVisibility(View.GONE);
			view_placeholder.setVisibility(View.GONE);
			patternView.setVisibility(View.VISIBLE);
		}
		else{
			view_errorText.setVisibility(View.GONE);
			view_placeholder.setVisibility(View.VISIBLE);
			patternView.setVisibility(View.GONE);
		}

		// set up the RecyclerView
		patternView.setLayoutManager(new GridLayoutManager(this, 3));
		adapter = new MyRecyclerViewAdapter(this, dbPaginatedList, CustomPatternActivity.this);
		patternView.setAdapter(adapter);

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

	@Override
	public void setCurrentSelection(String height, String width, int count, float x_pos, float y_pos) {
		if(!width.equals("")) txt_sel_w.setText(width);
		if(!height.equals("")) txt_sel_h.setText(height);
		txt_pos_x.setText(x_pos+"");
		txt_pos_y.setText(y_pos+"");
	}

	AdapterView.OnItemSelectedListener unitSpinnerListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								   long arg3) {
			// TODO Auto-generated method stub
			String[] unitArray = getResources().getStringArray(
					R.array.unitSpinnerArray);

			final float wallWidth = GlobalVariables.getWallWidth();
			final float wallHeight = GlobalVariables.getWallHeight();
			final float wallLength = GlobalVariables.getWallLength();

			String selH = txt_sel_h.getText().toString();
			String selW = txt_sel_w.getText().toString();
			String selX = txt_pos_x.getText().toString();
			String selY = txt_pos_y.getText().toString();

			if (unitArray[arg2].equalsIgnoreCase("Feet")) {

				txt_wall_height.setText(String.format("%1.2f", (wallHeight) * 0.00328084f) + "");
				txt_wall_width.setText(String.format("%1.2f", (wallWidth) * 0.00328084f) + "");

				if (unit.equalsIgnoreCase("Millimeter")) {

					txt_sel_h.setText(String.format("%1.2f", (Double.parseDouble(selH) * 0.00328084f)) + "");
					txt_sel_w.setText(String.format("%1.2f", (Double.parseDouble(selW) * 0.00328084f)) + "");
					txt_pos_x.setText(String.format("%1.2f", (Double.parseDouble(selX) * 0.00328084f)) + "");
					txt_pos_y.setText(String.format("%1.2f", (Double.parseDouble(selY) * 0.00328084f)) + "");

				} else if (unit.equalsIgnoreCase("Inches")) {

					txt_sel_h.setText(String.format("%1.2f", (Double.parseDouble(selH) * 0.0833333f)) + "");
					txt_sel_w.setText(String.format("%1.2f", (Double.parseDouble(selW) * 0.0833333f)) + "");
					txt_pos_x.setText(String.format("%1.2f", (Double.parseDouble(selX) * 0.0833333f)) + "");
					txt_pos_y.setText(String.format("%1.2f", (Double.parseDouble(selY) * 0.0833333f)) + "");
				}

			} else if (unitArray[arg2].equalsIgnoreCase("Inches")) {

				txt_wall_height.setText(String.format("%1.2f", (wallHeight) * 0.0393701) + "");
				txt_wall_width.setText(String.format("%1.2f", (wallWidth) * 0.0393701) + "");

				if (unit.equalsIgnoreCase("Feet")) {

					txt_sel_h.setText(String.format("%1.2f", (Double.parseDouble(selH) * 12f)) + "");
					txt_sel_w.setText(String.format("%1.2f", (Double.parseDouble(selW) * 12f)) + "");
					txt_pos_x.setText(String.format("%1.2f", (Double.parseDouble(selX) * 12f)) + "");
					txt_pos_y.setText(String.format("%1.2f", (Double.parseDouble(selY) * 12f)) + "");

				} else if (unit.equalsIgnoreCase("Millimeter")) {

					txt_sel_h.setText(String.format("%1.2f", (Double.parseDouble(selH) * 0.0393701f)) + "");
					txt_sel_w.setText(String.format("%1.2f", (Double.parseDouble(selW) * 0.0393701f)) + "");
					txt_pos_x.setText(String.format("%1.2f", (Double.parseDouble(selX) * 0.0393701f)) + "");
					txt_pos_y.setText(String.format("%1.2f", (Double.parseDouble(selY) * 0.0393701f)) + "");
				}

			} else {//mm

				txt_wall_height.setText(String.format("%1.2f", wallHeight) + "");
				txt_wall_width.setText(String.format("%1.2f", wallWidth) + "");

				if (unit.equalsIgnoreCase("Feet")) {

					txt_sel_h.setText(String.format("%1.2f", (Double.parseDouble(selH) * 304.8f)) + "");
					txt_sel_w.setText(String.format("%1.2f", (Double.parseDouble(selW) * 304.8f)) + "");
					txt_pos_x.setText(String.format("%1.2f", (Double.parseDouble(selX) * 304.8f)) + "");
					txt_pos_y.setText(String.format("%1.2f", (Double.parseDouble(selY) * 304.8f)) + "");

				} else if (unit.equalsIgnoreCase("Inches")) {

					txt_sel_h.setText(String.format("%1.2f", (Double.parseDouble(selH) * 25.4f)) + "");
					txt_sel_w.setText(String.format("%1.2f", (Double.parseDouble(selW) * 25.4f)) + "");
					txt_pos_x.setText(String.format("%1.2f", (Double.parseDouble(selX) * 25.4f)) + "");
					txt_pos_y.setText(String.format("%1.2f", (Double.parseDouble(selY) * 25.4f)) + "");

				}
			}

			unit = unitArray[arg2];
			GlobalVariables.setUnit(unit);
			GlobalVariables.setDesignerUnit(unit);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	public void setDimesions(){

		txt_wall_height.setText(String.format("%1.2f", Float.parseFloat(pHeight)));
		txt_wall_width.setText(String.format("%1.2f", Float.parseFloat(pWidth)));

	}

	public void clearText(){
		draw_info.setVisibility(View.GONE);
		draw_info.setVisibility(View.INVISIBLE);
		error_mesg.setVisibility(View.GONE);
		error_mesg.setVisibility(View.INVISIBLE);
	}

	@Override
	public void changeView(boolean tickFlag, boolean deleteFlag) {
		if(tickFlag){
			clearText();
			dmd_tick.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.appGreen));
			dmd_tick.setBackgroundResource(R.color.white);
			fitLay.setVisibility(View.VISIBLE);
			positionLay.setVisibility(View.GONE);
		}
		else{
			clearText();
			dmd_tick.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
			TypedValue outValue = new TypedValue();
			getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
			dmd_tick.setBackgroundResource(outValue.resourceId);
		}

		if(deleteFlag){
			clearText();
			dmd_delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.appGreen));
			dmd_delete.setBackgroundResource(R.color.white);
			fitLay.setVisibility(View.GONE);
			positionLay.setVisibility(View.VISIBLE);
		}
		else{
			clearText();
			dmd_delete.setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.white));
			TypedValue outValue = new TypedValue();
			getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
			dmd_delete.setBackgroundResource(outValue.resourceId);
		}
	}

	public void addLayout(){
		if(drawToolSelected == true) {
			changeColor(dmd_pointer);
			drawToolSelected = false;
			drawView.isDrawingSelected(drawToolSelected);
			fitLay.setVisibility(View.GONE);
			unsetTrasnparency();
			drawView.setPoints();
			drawView.addLayoutView();
			drawView.resetPoint();
		}
	}

	@Override
	public void changeMessage(String mesg, int type) {
		draw_info.setVisibility(View.VISIBLE);
		draw_info.setText(mesg);

		if(type == 0){
			draw_info.setTextColor(getResources().getColor(R.color.errorText));
		}
		else{
			draw_info.setTextColor(getResources().getColor(R.color.textColorBright));
		}
	}

	public void syncLibrary() {

		String token;

		if(downloadingFlag != 1) {
			dDialog = new SweetAlertDialog(CustomPatternActivity.this, SweetAlertDialog.PROGRESS_TYPE)
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
										CustomPatternActivity.this);
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

								DatabaseManager.addSettingsInfo(CustomPatternActivity.this, settingsdata);

								dDialog.setTitleText("Syncing Tile Library...");

								downloadTask = new DownloadTask(CustomPatternActivity.this);
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

								downloadTask = new DownloadTask(CustomPatternActivity.this);
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

	@Override
	public void setTotalCount(int count) {

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
							getAllpatterns();
						}
					})
					.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
		}
	}

	private void writeStorate(Context context, Bitmap photo, String tile_id, String tile_name, String brand) {

		DatabaseHandler dh = new DatabaseHandler(context);
		String file_path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/DMD/Custom__Pattern/" + brand + "/";

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


	//***************************************************************//

	public void searchTiles(String str){
		DatabaseHandler db = new DatabaseHandler(this);
		ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

		searchFlag = 1;
		clearFilter();

		dbResult = db.searchkeyword(str, "A");

		if(dbResult.size() > 0){
			view_errorText.setVisibility(View.GONE);
			patternView.setVisibility(View.VISIBLE);
			dbPaginatedList.clear();
			dbPaginatedList.addAll(dbResult);
			adapter.notifyDataSetChanged();
		}
		else{
			view_errorText.setVisibility(View.VISIBLE);
			patternView.setVisibility(View.GONE);
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

	OnCheckedChangeListener brandItemCheckListener = new OnCheckedChangeListener() {

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

	OnCheckedChangeListener categoryCheckListener = new OnCheckedChangeListener() {

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

	OnCheckedChangeListener titeTypeCheckListener = new OnCheckedChangeListener() {

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

	OnCheckedChangeListener titeSizeCheckListener = new OnCheckedChangeListener() {

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

	OnCheckedChangeListener titeColorCheckListener = new OnCheckedChangeListener() {

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
			patternView.setVisibility(View.VISIBLE);

			searchFlag = 1;
			dbPaginatedList.clear();
			dbPaginatedList.addAll(filterResult);
			adapter.notifyDataSetChanged();
		}
		else{
			view_errorText.setVisibility(View.VISIBLE);
			patternView.setVisibility(View.GONE);
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

		getAllpatterns();

		/*LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.brandCheckBox);
		ll.removeAllViews();*/

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

	public class MovementUpdater implements Runnable {
		final int viewTag;
		View v;

		public MovementUpdater(View v) {
			this.v = v;
			viewTag = Integer.parseInt((String) v.getTag());

		}

		@Override
		public void run() {
			if (!stop) {

				float x_pos = 0;
				float y_pos = 0;

				x_pos = Float.parseFloat(txt_pos_x.getText().toString());
				y_pos = Float.parseFloat(txt_pos_y.getText().toString());

				switch (viewTag) {
					case 1:
						drawView.checkMoveSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
								x_pos, y_pos, 1);
						break;
					case 2:
						drawView.checkMoveSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
								x_pos, y_pos, 2);
						break;
					case 3:
						if (x_pos >= 0) {
							drawView.checkMoveSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
									x_pos, y_pos, 3);
						}
						break;
					case 4:
						if (y_pos >= 0) {
							drawView.checkMoveSelection(txt_sel_w.getText().toString(), txt_sel_h.getText().toString(),
									x_pos, y_pos, 4);
						}
						break;
				}
				handler.postDelayed(new MovementUpdater(v), 0);
			}
		}
	}

	public void prepareDialog(){

		ImageView dialog_close;
		final Spinner unitSpinner;
		CheckBox chkTileSize;
		final EditText width;
		final EditText height;
		final Button proceedButton;
		final RelativeLayout rel = findViewById(R.id.main_content);

		gridDialog = new Dialog(CustomPatternActivity.this);
		gridDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		gridDialog.setCancelable(false);
		gridDialog.setContentView(R.layout.grid_dialog);
		gridDialog.getWindow()
				.getAttributes().windowAnimations = R.style.DialogAnimation;

		gridDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialog_close = gridDialog.findViewById(R.id.dialog_close);
		unitSpinner = gridDialog.findViewById(R.id.unitSpinner);
		width = gridDialog.findViewById(R.id.input_width);
		height = gridDialog.findViewById(R.id.input_height);
		error_text = gridDialog.findViewById(R.id.error_text);

		String[] unitArray = getResources().getStringArray(
				R.array.unitSpinnerArray);

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, R.layout.grid_spinner_item, unitArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitSpinner.setAdapter(spinnerArrayAdapter);
		unitSpinner.setOnItemSelectedListener(gridSpinnerListener);
		unitSpinner.setSelection(0,true);

		chkTileSize = (CheckBox) gridDialog.findViewById(R.id.chkTileSize);

		String size = "0x0";
		size = drawView.getSelectedTile();
		final String finalSize = size;

		chkTileSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (((CheckBox) v).isChecked()) {

					unitSpinner.setSelection(2,true);

					String[] tileDim = finalSize.split("x");
					if (tileDim.length == 0) {
						tileDim = finalSize.split("X");
					}
					width.setText(tileDim[0].toString().trim());
					height.setText(tileDim[1].toString().trim());
				} else{
					width.setText("");
					height.setText("");
				}
			}
		});

		if(drawView.chkTileSelected()){
			chkTileSize.setVisibility(View.VISIBLE);
		}
		else{
			chkTileSize.setVisibility(View.GONE);
		}

		dialog_close.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				gridDialog.cancel();
			}
		});

		proceedButton = ((Button) gridDialog.findViewById( R.id.proceed_button ));
		proceedButton.setOnClickListener( new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int min = 0, max = 0;
				Float w, h, wPass, hPass, wMM, hMM;
				String err_mesg;
				error_text.setText("");
				if(width.getText().toString().isEmpty() || height.getText().toString().isEmpty()){
					error_text.setText("Width and Height Fields are mandatory!");
					error_text.setVisibility(View.VISIBLE);
					return;
				}

				w = Float.parseFloat(width.getText().toString().trim());
				h = Float.parseFloat(height.getText().toString().trim());

				if(un.equalsIgnoreCase("millimetre")){
					err_mesg = "Size should be min 127 and max 3048 Millimetre";
					min = 127;
					max = 3048;
					wMM = w;
					hMM = h;
				}
				else if (un.equalsIgnoreCase("feet")){
					err_mesg = "Size should be min 1 and max 10 Feet";
					min = 1;
					max = 10;
					wMM = GlobalVariables.feetToMm(w);
					hMM = GlobalVariables.feetToMm(h);
				}
				else{
					err_mesg = "Size should be min 5 and max 120 Inches";
					min = 5;
					max = 120;
					wMM = GlobalVariables.inchesToMm(w);
					hMM = GlobalVariables.inchesToMm(h);
				}

				if(w < min ||  w > max){
					error_text.setText(err_mesg);
					error_text.setVisibility(View.VISIBLE);
					return;
				}

				if(h < min ||  h > max){
					error_text.setText(err_mesg);
					error_text.setVisibility(View.VISIBLE);
					return;
				}

				if (unit.equalsIgnoreCase("Feet")) {
					wPass = GlobalVariables.mmToFeet(wMM);
					hPass = GlobalVariables.mmToFeet(hMM);
				} else if (unit.equalsIgnoreCase("Inches")) {
					wPass = GlobalVariables.mmToInches(wMM);
					hPass = GlobalVariables.mmToInches(hMM);
				} else{
					wPass = wMM;
					hPass = hMM;
				}

				wPassG = wPass;
				hPassG = hPass;

				drawView.clearDrawView(rel);
				gridToolSelected = true;
				drawView.isAutoGridSelected(gridToolSelected);
				drawView.fillGrid(wPass, hPass);
				fitLay.setVisibility(View.GONE);
				positionLay.setVisibility(View.GONE);
				fillLay.setVisibility(View.VISIBLE);
				changeMessage("", 1);

				gridDialog.cancel();
			}

		});

	}

	AdapterView.OnItemSelectedListener gridSpinnerListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								   long arg3) {
			// TODO Auto-generated method stub
			error_text.setVisibility(View.GONE);
			String[] tileArray = getResources().getStringArray(
					R.array.tileSpinnerArray);
			un = arg0.getItemAtPosition(arg2).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

}