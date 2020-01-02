package com.nagainfomob.app.DisplayMyDesign;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.TileRecyclerAdapter;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.model.ProjectTilesModel;
import com.nagainfomob.app.model.TileListModel;
import com.nagainfomob.app.model.TileModel;
import com.nagainfomob.app.sql.DatabaseManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import rajawali.Camera;
import rajawali.Object3D;
import rajawali.RajawaliActivity;
import rajawali.math.vector.Vector3;

public class Room3DActivity extends RajawaliActivity implements
        OnTouchListener, ScreenshotListener, OnClickListener {
    private RoomRenderer mRenderer;
    Camera cam;
    float length, width, height;
    private String projectId;
    Object3D obj = null;
    private boolean isObjectSelected = false;
    Boolean stop = false, stopCam = false;
    Boolean isLiveView = true;
    static final double DEG_TO_RAD = 0.0174532925;

    private String projectName;
    private String projectCreated;
    private String custName;
    private String custMob;

    private Handler handler = new Handler();
    static final int GET_WALL_REQUEST = 1; // The request code
    static final int MOVEMENT_SPEED = 85;
    int MAX_BACK = 495;
    int scale = 1000;
    ImageView upButton, downButton, leftButton, rightButton;
    ImageView uButton, dButton, lButton, rButton;
    private boolean isDrawerOpen;
    private boolean isOpening;
    private LinearLayout content_slider;
    private int displayWidth;
    private FrameLayout fl;
    Vector3 curPos, curRot;
    LinkedHashMap<String, Integer> tilesUsed = new LinkedHashMap<String, Integer>();
    private LayoutInflater inflater;
    private View patternContent2;
    private LinearLayout patternScrollView;
    private LinearLayout content_slider2;
    private EditText eText;
    ProgressDialog dialog12;
    private float MAX_FB, MAX_LR, MAX_TB;
    // int MAX_BACK;
    private int speed;
    private String objectStatus;

    private Switch camView;
    private TextView viewModeText;
    private LinearLayout leftButtonLayout;
    private LinearLayout rightButtonLayout;
    private RelativeLayout layAngles;

    private ImageView img_cam_tl;
    private ImageView img_cam_bl;
    private ImageView img_cam_center;
    private ImageView img_cam_br;
    private ImageView img_cam_tr;
    private ImageView img_screen;

    private RelativeLayout layMenuArrow;
    private RelativeLayout layMenu;
    private RelativeLayout layMenuArrowDown;
    private RelativeLayout wallInfo;

    private RecyclerView front_recycler_view;
    private RecyclerView right_recycler_view;
    private RecyclerView back_recycler_view;
    private RecyclerView left_recycler_view;
    private RecyclerView top_recycler_view;
    private RecyclerView bottom_recycler_view;

    private TextView frontTotal;
    private TextView rightTotal;
    private TextView backTotal;
    private TextView leftTotal;
    private TextView topTotal;
    private TextView bottomTotal;

    private LinearLayout layFront;
    private LinearLayout layRight;
    private LinearLayout layBack;
    private LinearLayout layLeft;
    private LinearLayout layTop;
    private LinearLayout layBottom;
    private LinearLayout layplaceholder;

    private TextView grand_total;
    private TextView tiles_used;
    private TextView proj_name;
    private TextView proj_time;
    private TextView cust_name;
    private TextView cust_mob;
    private TextView txt_wall_height;
    private TextView txt_wall_width;
    private TextView txt_wall_depth;



    // GestureDetector mGesDetect;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog12 = new ProgressDialog(Room3DActivity.this);
        isObjectSelected = false;
        String title = getResources().getString(R.string.app_name)
                + "\t\t---\t\t" + GlobalVariables.getProjectName();

		/*ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		bar.setTitle(title);*/

        Intent intent = getIntent();

        String l = intent.getStringExtra("depth");
        length = Float.parseFloat(l);

        String w = intent.getStringExtra("width");
        width = Float.parseFloat(w);

        String h = intent.getStringExtra("height");
        height = Float.parseFloat(h);

        projectId = intent.getStringExtra("projectId");
        projectName = intent.getStringExtra("projectName");
        projectCreated = intent.getStringExtra("projectCreated");
        custName = intent.getStringExtra("custName");
        custMob = intent.getStringExtra("custMob");

        GlobalVariables.setWallDim(height, width, length, 0 ,0);

        objectStatus = "no";

        //GlobalVariables.setUnit("feet");

//		if (intent.getExtras().containsKey("OBJECTS")) {
//			objectStatus = intent.getStringExtra("OBJECTS");
        invalidateOptionsMenu();
		/*} else {
			if (length / height <= 3 && width / height <= 3
					&& length / height >= 0.6 && width / height >= 0.6
					&& length / width <= 2 && width / length <= 2) {
				objectStatus = "yes";
			} else {
				objectStatus = "no";
				invalidateOptionsMenu();
				AlertDialog.Builder builder1 = new AlertDialog.Builder(
						Room3DActivity.this);
				builder1.setMessage("Due to View Limitations, PLACE OBJECTS functionality is disabled for this project.");
				builder1.setNegativeButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				AlertDialog alert = builder1.create();
				alert.setCanceledOnTouchOutside(false);
				alert.show();
			}
		}*/
        MAX_BACK = (int) (Math
                .min(Math.min(length * scale / height, width * scale / height),
                        1000) / 2);
        speed = 75;
        MAX_TB = (float) 499.9;
        MAX_LR = (float) (((length * scale / height) / 2)- 0.5);
        MAX_FB = (float) (((width * scale / height) / 2)- 0.5);

        loadTileCount();

        // mRenderer = new RoomRenderer(getApplicationContext(), length, width,
        // height);
        mRenderer = new RoomRenderer(getApplicationContext(), length, width,
                height);
        mSurfaceView.setOnTouchListener(new OnSwipeTouchListener(
                getApplicationContext()) {

        });
        cam = mRenderer.getCurrentCamera();
        cam.setZ(MAX_BACK-50);
        LayoutInflater inflater = LayoutInflater.from(this);
        fl = (FrameLayout) inflater.inflate(R.layout.activity_3d, null);

        intialize();
        initListeners();
        loadTileDetails();

        // objImg.setOnDragListener(new MyDragListener());

        // addContentView(ll, new LayoutParams(400, 400));

        mRenderer.setSurfaceView(mSurfaceView);

        super.setRenderer(mRenderer);

    }

    private void initListeners() {

        upButton = fl.findViewById(R.id.upButton);
        upButton.setTag("1");
        upButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (!isObjectSelected) {
                            stop = false;
                            handler.post(new MovementUpdater(upButton));
                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!isObjectSelected) {
                            stop = true;
                        } else {
                            int tag = Integer.parseInt((String) v.getTag());
                            rotateObject(tag);
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        downButton = fl.findViewById(R.id.downButton);
        downButton.setTag("2");
        downButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (!isObjectSelected) {
                            stop = false;
                            handler.post(new MovementUpdater(downButton));
                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!isObjectSelected) {
                            stop = true;
                        } else {
                            int tag = Integer.parseInt((String) v.getTag());
                            rotateObject(tag);
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        leftButton = fl.findViewById(R.id.leftButton);
        leftButton.setTag("3");
        leftButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (!isObjectSelected) {
                            stop = false;
                            handler.post(new MovementUpdater(leftButton));
                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!isObjectSelected) {
                            stop = true;
                        } else {
                            int tag = Integer.parseInt((String) v.getTag());
                            rotateObject(tag);
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        rightButton = fl.findViewById(R.id.rightButton);
        rightButton.setTag("4");
        rightButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (!isObjectSelected) {
                            stop = false;
                            handler.post(new MovementUpdater(rightButton));
                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (!isObjectSelected) {
                            stop = true;
                        } else {
                            int tag = Integer.parseInt((String) v.getTag());
                            rotateObject(tag);
                        }
                        return true;
                    }
                }
                return false;
            }
        });


        uButton = fl.findViewById(R.id.uButton);
        uButton.setTag("1");
        uButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        stopCam = false;
                        handler.post(new CameraUpdater(uButton));
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        stopCam = true;
                        return true;
                    }
                }
                return false;
            }
        });


        dButton = fl.findViewById(R.id.dButton);
        dButton.setTag("2");
        dButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        stopCam = false;
                        handler.post(new CameraUpdater(dButton));
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        stopCam = true;
                        return true;
                    }
                }
                return false;
            }
        });


        lButton = fl.findViewById(R.id.lButton);
        lButton.setTag("3");
        lButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        stopCam = false;
                        handler.post(new CameraUpdater(lButton));
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        stopCam = true;
                        return true;
                    }
                }
                return false;
            }
        });


        rButton = fl.findViewById(R.id.rButton);
        rButton.setTag("4");
        rButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (!isLiveView) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Please select LIVE camera to move around in the room",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        stopCam = false;
                        handler.post(new CameraUpdater(rButton));
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        stopCam = true;
                        return true;
                    }
                }
                return false;
            }
        });

        mLayout.addView(fl);

        layAngles = (RelativeLayout) findViewById(R.id.layAngles);

        viewModeText = (TextView) findViewById(R.id.viewModeText);
        camView = (Switch) findViewById(R.id.switchCam);
        camView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isLiveView = false;
                    viewModeText.setText("Cam View");

                    Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bottom_down);
                    Animation topIn = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.top_in);

                    ViewGroup hiddenPanel1 = (ViewGroup)findViewById(R.id.leftButtonLayout);
                    ViewGroup hiddenPanel2 = (ViewGroup)findViewById(R.id.rightButtonLayout);
                    ViewGroup hiddenPanel3 = (ViewGroup)findViewById(R.id.layAngles);

                    if (hiddenPanel1.getVisibility() == View.VISIBLE) {
                        hiddenPanel1.startAnimation(bottomDown);
                        hiddenPanel1.setVisibility(View.GONE);
                        hiddenPanel2.startAnimation(bottomDown);
                        hiddenPanel2.setVisibility(View.GONE);
                    }
                    hiddenPanel3.startAnimation(topIn);
                    hiddenPanel3.setVisibility(View.VISIBLE);

                    if (curPos == null || curRot == null) {

                        curPos = new Vector3(cam.getPosition().x, cam.getPosition().y,
                                cam.getPosition().z);
                        curRot = new Vector3(cam.getRotation().x, cam.getRotation().y,
                                cam.getRotation().z);
                    }
                }
                else {
                    isLiveView = true;
                    viewModeText.setText("Live View");

                    Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bottom_up);
                    Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bottom_down);
                    Animation topUp = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.top_out);

                    ViewGroup hiddenPanel1 = (ViewGroup)findViewById(R.id.leftButtonLayout);
                    ViewGroup hiddenPanel2 = (ViewGroup)findViewById(R.id.rightButtonLayout);
                    ViewGroup hiddenPanel3 = (ViewGroup)findViewById(R.id.layAngles);
                    ViewGroup hiddenPanel4 = (ViewGroup)findViewById(R.id.layMenuArrow);
                    ViewGroup hiddenPanel5 = (ViewGroup)findViewById(R.id.layMenu);

                    hiddenPanel1.startAnimation(bottomUp);
                    hiddenPanel1.setVisibility(View.VISIBLE);
                    hiddenPanel2.startAnimation(bottomUp);
                    hiddenPanel2.setVisibility(View.VISIBLE);
                    hiddenPanel3.startAnimation(topUp);
                    hiddenPanel3.setVisibility(View.GONE);

                    if (hiddenPanel5.getVisibility() == View.VISIBLE) {
                        hiddenPanel4.startAnimation(bottomUp);
                        hiddenPanel4.setVisibility(View.VISIBLE);
                    }

                    if (hiddenPanel5.getVisibility() == View.VISIBLE) {
                        hiddenPanel5.startAnimation(bottomDown);
                        hiddenPanel5.setVisibility(View.GONE);
                    }

                    try {
                        if (curPos != null && curRot != null
                                && curRot != new Vector3(90, 0, 0)) {
                            setCamera(curPos, curRot);
                            isLiveView = true;
                            curPos = null;
                            curRot = null;

                        } else if (curRot == new Vector3(90, 0, 0)) {
                            setCamera(new Vector3(0.0, 0.0, 499.9), new Vector3(0,
                                    0, 0));
                            isLiveView = true;
                            curPos = null;
                            curRot = null;
                        }
                        // cam.setLookAt(0, 0, 0);
                    } catch (Exception ex) {

                    }
                }
            }
        });

        img_cam_tl = (ImageView) findViewById(R.id.img_cam_tl);
        img_cam_bl = (ImageView) findViewById(R.id.img_cam_bl);
        img_cam_center = (ImageView) findViewById(R.id.img_cam_center);
        img_cam_br = (ImageView) findViewById(R.id.img_cam_br);
        img_cam_tr = (ImageView) findViewById(R.id.img_cam_tr);
        img_screen = (ImageView) findViewById(R.id.img_screen);

        layMenuArrow = (RelativeLayout) findViewById(R.id.layMenuArrow);
        layMenu = (RelativeLayout) findViewById(R.id.layMenu);
        layMenuArrowDown = (RelativeLayout) findViewById(R.id.layMenuArrowDown);
        wallInfo = (RelativeLayout) findViewById(R.id.wallInfo);

        /*linLibrary = (LinearLayout) findViewById(R.id.linLibrary);
        linDimension = (LinearLayout) findViewById(R.id.linDimension);
        linEstimate = (LinearLayout) findViewById(R.id.linEstimate);
        linScreenShot = (LinearLayout) findViewById(R.id.linScreenShot);
        linObject = (LinearLayout) findViewById(R.id.linObject);*/

        img_cam_tl.setOnClickListener(this);
        img_cam_bl.setOnClickListener(this);
        img_cam_center.setOnClickListener(this);
        img_cam_br.setOnClickListener(this);
        img_cam_tr.setOnClickListener(this);
        img_screen.setOnClickListener(this);

        layMenuArrow.setOnClickListener(this);
        layMenuArrowDown.setOnClickListener(this);

        /*linLibrary.setOnClickListener(this);
        linDimension.setOnClickListener(this);
        linEstimate.setOnClickListener(this);
        linScreenShot.setOnClickListener(this);
        linObject.setOnClickListener(this);*/




    }


    public void totalTileUsed(LinkedHashMap<String, Integer> tUsed) {
        if (tUsed.size() <= 0) {
            Toast.makeText(getApplicationContext(), "No Tiles Used",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Dialog tDialog = new Dialog(Room3DActivity.this);
        tDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        tDialog.setContentView(R.layout.tilecount_dilaog);
        ListView lView = (ListView) tDialog.findViewById(R.id.listTile);
        ListTileCountAdapter ltAdapter = new ListTileCountAdapter(this, tUsed);
        lView.setAdapter(ltAdapter);
        tDialog.show();

    }

    @SuppressLint("NewApi")
    private void intialize() {

        isDrawerOpen = false;
        isOpening = false;
        content_slider = (LinearLayout) fl.findViewById(R.id.objectSlider);
        patternScrollView = (LinearLayout) findViewById(R.id.additems);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;

        // isDrawerOpen = false;
        // isOpening = false;
        // inflater = (LayoutInflater)
        // getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //
        //
        // patternContent2 = inflater.inflate(R.layout.layout_patters, null);

        // private EditText eText;
        // eText = (EditText) patternContent2.findViewById(R.id.textSearch);
        // eText.setText(null);
        // filterSearch_button = (Button) patternContent2
        // .findViewById(R.id.filterButton);
        // filterSearch_button.setOnClickListener(PatternLibraryActivity.this);

        // Button search = (Button) patternContent2
        // .findViewById(R.id.searchButton);
        //
        // search.setOnClickListener(PatternLibraryActivity.this);
        //
        //
        // patternContent = inflater.inflate(R.layout.layout_filter, null);
        // filterSearchButton2 = (Button) patternContent
        // .findViewById(R.id.filterButton2);
        //
        // filterSearchButton2.setOnClickListener(PatternLibraryActivity.this);
        //
        // patternScrollView = (LinearLayout)
        // findViewById(R.id.patternScrollView);
        // main_layout = (RelativeLayout) findViewById(R.id.main_content);
        // content_slider2 = (LinearLayout) findViewById(R.id.content_slider);
        // patternHeader = (RelativeLayout) findViewById(R.id.patternHeader);
        // twod_layout = (LinearLayout) findViewById(R.id.twod_layout);
        // buttonLayout = (LinearLayout) findViewById(R.id.leftButtonLayout);
        // Display display1 = getWindowManager().getDefaultDisplay();
        // Point size = new Point();
        // display1.getSize(size);
        // displayWidth = size.x;
        final int left = displayWidth;
        final int top = content_slider.getTop();
        final int right = content_slider.getRight();
        final int bottom = content_slider.getBottom();
        content_slider.layout(left, top, right, bottom);
        // previewDialog = createPreviewDialog();
        // twod_layout.bringToFront();

		/*
		 * final int left = displayWidth; final int top =
		 * content_slider.getTop(); final int right = content_slider.getRight();
		 * final int bottom = content_slider.getBottom();
		 * content_slider.layout(left, top, right, bottom);
		 */

        leftButtonLayout = (LinearLayout) findViewById(R.id.leftButtonLayout);
        rightButtonLayout = (LinearLayout) findViewById(R.id.rightButtonLayout);

        front_recycler_view = (RecyclerView) fl.findViewById(R.id.front_recycler_view);
        right_recycler_view = (RecyclerView) fl.findViewById(R.id.right_recycler_view);
        back_recycler_view = (RecyclerView) fl.findViewById(R.id.back_recycler_view);
        left_recycler_view = (RecyclerView) fl.findViewById(R.id.left_recycler_view);
        top_recycler_view = (RecyclerView) fl.findViewById(R.id.top_recycler_view);
        bottom_recycler_view = (RecyclerView) fl.findViewById(R.id.bottom_recycler_view);

        frontTotal = fl.findViewById(R.id.frontTotal);
        rightTotal = fl.findViewById(R.id.rightTotal);
        backTotal = fl.findViewById(R.id.backTotal);
        leftTotal = fl.findViewById(R.id.leftTotal);
        topTotal = fl.findViewById(R.id.topTotal);
        bottomTotal = fl.findViewById(R.id.bottomTotal);

        layFront = fl.findViewById(R.id.layFront);
        layRight = fl.findViewById(R.id.layRight);
        layBack = fl.findViewById(R.id.layBack);
        layLeft = fl.findViewById(R.id.layLeft);
        layTop = fl.findViewById(R.id.layTop);
        layBottom = fl.findViewById(R.id.layBottom);
        layplaceholder = fl.findViewById(R.id.layplaceholder);

        grand_total = fl.findViewById(R.id.grand_total);
        tiles_used = fl.findViewById(R.id.tiles_used);

        proj_name = fl.findViewById(R.id.proj_name);
        proj_time = fl.findViewById(R.id.proj_time);
        cust_name = fl.findViewById(R.id.cust_name);
        cust_mob = fl.findViewById(R.id.cust_mob);
        txt_wall_height = fl.findViewById(R.id.txt_wall_height);
        txt_wall_width = fl.findViewById(R.id.txt_wall_width);
        txt_wall_depth = fl.findViewById(R.id.txt_wall_depth);


        proj_name.setText(projectName);
        proj_time.setText(projectCreated);
        cust_name.setText(custName);
        cust_mob.setText(custMob);

        if(GlobalVariables.getUnit().equals("Feet")){
            txt_wall_height.setText(GlobalVariables.mmToFeet(height)+"Ft");
            txt_wall_width.setText(GlobalVariables.mmToFeet(width)+"Ft");
            txt_wall_depth.setText(GlobalVariables.mmToFeet(length)+"Ft");
        }
        else if(GlobalVariables.getUnit().equals("Inches")){
            txt_wall_height.setText(GlobalVariables.mmToInches(height)+"In");
            txt_wall_width.setText(GlobalVariables.mmToInches(width)+"In");
            txt_wall_depth.setText(GlobalVariables.mmToInches(length)+"In");
        }
        else{
            txt_wall_height.setText(height+"mm");
            txt_wall_width.setText(width+"mm");
            txt_wall_depth.setText(length+"mm");
        }
    }

    protected void openDrawer() {
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
        }

    }

    private AnimationListener slideLeftRightAnimationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            setIsOpening(true);
            content_slider.bringToFront();
            if (!getIsDrawerOpen()) {
                // loadTile();
                // Toast.makeText(getApplicationContext(), "obj clicked",
                // Toast.LENGTH_SHORT).show();
                // filterSelected(content_slider);
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
                // removeTile();

            }
            setIsOpening(false);
            setIsDrawerOpen();
        }
    };

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

    private void loadSlider() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.camView:
                if (curPos == null || curRot == null) {

                    curPos = new Vector3(cam.getPosition().x, cam.getPosition().y,
                            cam.getPosition().z);
                    curRot = new Vector3(cam.getRotation().x, cam.getRotation().y,
                            cam.getRotation().z);

                }
                camDialog();
                break;

            // case R.id.actionbarTool:
            // openDrawer();
            // Toast.makeText(getApplicationContext(), "pointer selected",
            // Toast.LENGTH_SHORT).show();
            // break;
            // action with ID action_settings was selected

		/*
		 * case R.id.objectmode: isObjectSelected = !isObjectSelected; break;
		 */
            case R.id.costCalculation:
                totalTileUsed(tilesUsed);
                break;/*
		case R.id.img_screen:
			takeScreenShot();
			break;*/
            case R.id.freeze:

                // Freeze freeze = new Freeze(Room3DActivity.this);
                // freeze.execute();
                dialog12.setMessage("Rendering Views...Please wait...");
                dialog12.setCanceledOnTouchOutside(false);
                FrontWall fw = new FrontWall(this);
                dialog12.show();
                setCamera(new Vector3(0.0, 0.0, MAX_FB), new Vector3(0, 0.0, 0.0));
                fw.start();

                break;
            default:
                break;
        }

        return true;
    }

    public void camDialog() {
        final ArrayList<Vector3> vertices = mRenderer.getVertices();
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(Room3DActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setContentView(R.layout.camview_dialog);
        dialog.show();

        // front left
        Button b3 = (Button) dialog.findViewById(R.id.button3);
        Log.i("cam pos", cam.getPosition().toString());
        Log.i("CamRot ", cam.getRotation().toString());
        b3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {

                    setCamera(new Vector3(0.0, -390.0, 360.1), new Vector3(-20,
                            135.0, 20.0));
                    // cam.setLookAt(0, 0, 0);
                } catch (Exception ex) {

                }
            }
        });
        // front right
        Button b1 = (Button) dialog.findViewById(R.id.button1);

        b1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    // Log.i("camera look at", cam.getLookAt().toString());

                    setCamera(new Vector3(0.0, -390.0, 360.10), new Vector3(
                            -20, -135.0, -20.0));

                    // cam.setLookAt(0, 0, 0);
                } catch (Exception ex) {

                }
            }
        });
        // left back
        Button b4 = (Button) dialog.findViewById(R.id.button4);

        b4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    // Log.i("camera look at", cam.getLookAt().toString());
                    Log.i("camera init pos", cam.getPosition().toString());
                    Vector3 v = vertices.get(0);
                    Log.i("v1", vertices.get(0).toString());

                    setCamera(new Vector3(0.0, -390.0, 360.1), new Vector3(20,
                            45.0, 20.0));
                    // cam.setLookAt(0, 0, 0);
                } catch (Exception ex) {

                }
            }
        });

        // right back
        Button b2 = (Button) dialog.findViewById(R.id.button2);

        b2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {

                    setCamera(new Vector3(0.0, -390.0, 360.1), new Vector3(
                            20.0, -45.0, -20.0));
                    // cam.setLookAt(0, 0, 0);
                } catch (Exception ex) {

                }
            }
        });

        // top view
        Button b5 = (Button) dialog.findViewById(R.id.button5);

        b5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {

                    setCamera(new Vector3(0.0, 0.0, 499.9), new Vector3(90.0,
                            0.0, 0.0));
                    // cam.setLookAt(0, 0, 0);
                } catch (Exception ex) {

                }
            }
        });
        // live
        Button b6 = (Button) dialog.findViewById(R.id.button6);

        b6.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try {
                    if (curPos != null && curRot != null
                            && curRot != new Vector3(90, 0, 0)) {
                        setCamera(curPos, curRot);
                        isLiveView = true;
                        curPos = null;
                        curRot = null;

                    } else if (curRot == new Vector3(90, 0, 0)) {
                        setCamera(new Vector3(0.0, 0.0, 499.9), new Vector3(0,
                                0, 0));
                        isLiveView = true;
                        curPos = null;
                        curRot = null;
                    }
                    // cam.setLookAt(0, 0, 0);
                } catch (Exception ex) {

                }
            }
        });
    }

    void setCamera(Vector3 pos, Vector3 rot) {
        isLiveView = false;
        cam.setPosition(pos);
        cam.setRotation(rot);
    }

    void freezeshot(String path, String path1, int view,
                    ScreenshotListener listener) {

        ((RoomRenderer) mRenderer).takefreezeshot(path, path1,
                Room3DActivity.this, view, listener);

        try {

            Thread.sleep(250);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void takeScreenShot() {
        Log.d("Screen", "OK");
        ((RoomRenderer) mRenderer).takeScreenshot();

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("Screenshot has been saved to the Gallery")
                .show();
    }

    void rotateObject(int viewTag) {
        if (isObjectSelected) {

            switch (viewTag) {
                case 1:
                    // up arrow
				/*
				 * if (obj != null) obj.setPosition(obj.getX(), obj.getY(),
				 * obj.getZ() - MOVEMENT_SPEED);
				 */
                    break;
                case 2:
                    // down arrow
				/*
				 * if (obj != null) obj.setPosition(obj.getX(), obj.getY(),
				 * obj.getZ() + MOVEMENT_SPEED);
				 */
                    // Log.i("back Z ",cam.getZ()+"");
                    break;
                case 3:
                    // left arrow
                    if (obj != null)
                        obj.setRotY(obj.getRotY() + 90);
                    break;
                case 4:
                    // right arrow
                    if (obj != null)
                        obj.setRotY(obj.getRotY() - 90);
                    break;
            }

        }

    }

    @Override
    public void onClick(View view) {

        Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_up);
        Animation bottomDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_down);

        ViewGroup hiddenPanel1 = (ViewGroup)findViewById(R.id.leftButtonLayout);
        ViewGroup hiddenPanel2 = (ViewGroup)findViewById(R.id.rightButtonLayout);
        ViewGroup hiddenPanel3 = (ViewGroup)findViewById(R.id.layMenuArrow);
        ViewGroup hiddenPanel4 = (ViewGroup)findViewById(R.id.layMenu);
        ViewGroup hiddenPanel5 = (ViewGroup)findViewById(R.id.layMenuArrow);

        switch (view.getId()) {
            case R.id.img_cam_tl:
                try {
                    /*setCamera(new Vector3(10, -10.0, 360.10), new Vector3(
                            -20, -135.0, -20.0));*/
                    setCamera(new Vector3(-10.0, 10.0, 360.10), new Vector3(-20,
                            135.0, 20.0));
                } catch (Exception ex) {

                }
                break;
            case R.id.img_cam_bl:
                try {
                    /*setCamera(new Vector3(-10, 10, 360.1), new Vector3(-20,
                            135.0, 20.0));*/
                    setCamera(new Vector3(10, -10.0, 360.1), new Vector3(20,
                            45.0, 20.0));
                } catch (Exception ex) {

                }
                break;
            case R.id.img_cam_center:
                try {
                    setCamera(new Vector3(0.0, 0.0, 499.9), new Vector3(90.0,
                            0.0, 0.0));
                } catch (Exception ex) {

                }
                break;
            case R.id.img_cam_br:
                try {
                    /*setCamera(new Vector3(10, -10.0, 360.1), new Vector3(20,
                            45.0, 20.0));*/
                    setCamera(new Vector3(10.0, 10.0, 360.1), new Vector3(
                            20.0, -45.0, -20.0));
                } catch (Exception ex) {

                }
                break;
            case R.id.img_cam_tr:
                try {
                    /*setCamera(new Vector3(10.0, 10.0, 360.1), new Vector3(
                            20.0, -45.0, -20.0));*/
                    setCamera(new Vector3(-10, 10.0, 360.10), new Vector3(
                            -20, -135.0, -20.0));
                } catch (Exception ex) {

                }
                break;
            case R.id.layMenuArrow:
//                wallInfo.setVisibility(View.GONE);
                if(isLiveView == true) {

                    hiddenPanel1.startAnimation(bottomDown);
                    hiddenPanel1.setVisibility(View.GONE);
                    hiddenPanel2.startAnimation(bottomDown);
                    hiddenPanel2.setVisibility(View.GONE);

                }
                hiddenPanel3.setVisibility(View.GONE);
                hiddenPanel4.startAnimation(bottomUp);
                hiddenPanel4.setVisibility(View.VISIBLE);

                break;
            case R.id.layMenuArrowDown:

                hiddenPanel3.startAnimation(bottomDown);
                hiddenPanel3.setVisibility(View.GONE);
                hiddenPanel4.startAnimation(bottomDown);
                hiddenPanel4.setVisibility(View.GONE);
                hiddenPanel1.startAnimation(bottomUp);
                hiddenPanel1.setVisibility(View.VISIBLE);
                hiddenPanel2.startAnimation(bottomUp);
                hiddenPanel2.setVisibility(View.VISIBLE);
                hiddenPanel5.setVisibility(View.VISIBLE);

                break;

            case R.id.img_screen:
                takeScreenShot();
                break;

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
//                wallInfo.setVisibility(View.GONE);
                switch (viewTag) {
                    case 1:

                        if (cam.getZ() - MOVEMENT_SPEED > -MAX_BACK) {
                            cam.setZ(cam.getZ() - MOVEMENT_SPEED);

                        }

                        break;
                    case 2:
                        if (cam.getZ() + MOVEMENT_SPEED < MAX_BACK) {
                            cam.setZ(cam.getZ() + MOVEMENT_SPEED);

                        }

                        break;
                    case 3:
                        if (cam.getX() - MOVEMENT_SPEED > -MAX_BACK) {
                            cam.setX(cam.getX() - MOVEMENT_SPEED);

                        }

                        break;
                    case 4:
                        if (cam.getX() + MOVEMENT_SPEED < MAX_BACK) {
                            cam.setX(cam.getX() + MOVEMENT_SPEED);

                        }

                        break;

                }
                handler.postDelayed(new MovementUpdater(v), 85);
            }
        }

    }

    public class CameraUpdater implements Runnable {
        final int viewTag;
        View v;

        public CameraUpdater(View v) {
            this.v = v;
            viewTag = Integer.parseInt((String) v.getTag());

        }

        @Override
        public void run() {
            if (!stopCam) {
//                wallInfo.setVisibility(View.GONE);
                switch (viewTag) {
                    case 1:
                        if (cam.getY() + MOVEMENT_SPEED < MAX_TB) {
                            cam.setY(cam.getY() + (MOVEMENT_SPEED + 75));
                        }
                        break;
                    case 2:
                        if (cam.getY() - MOVEMENT_SPEED > -MAX_TB) {
                            cam.setY(cam.getY() - (MOVEMENT_SPEED + 75));
                        }
                        break;
                    case 3:
                        cam.setRotY(cam.getRotY() - 10);
                        break;
                    case 4:
                        cam.setRotY(cam.getRotY() + 10);
                        break;
                }

                handler.postDelayed(new CameraUpdater(v), 85);
            }
        }

    }

    int distanceToWall(int which) {
        Object3D myObject = null;
        int max_fb = 0, max_lr = 0, max = 0;
        int rott = (int) Math.abs(cam.getRotY() % 360);
        if ((Math.abs(cam.getRotY() % 360)) <= Math.atan((MAX_FB - Math.abs(cam
                .getZ())) / (MAX_LR - Math.abs(cam.getX()))))

        {
            max_fb = (int) ((int) (MAX_FB - Math.abs(cam.getZ())) / Math
                    .cos((Math.abs(cam.getRotY() % 360)) * 3.14159265 / 180.0));
            max_lr = (int) ((int) (MAX_LR - Math.abs(cam.getX())) / Math
                    .cos((Math.abs(cam.getRotY() % 360)) * 3.14159265 / 180.0));
            if (which == 0)
                max = max_fb;
            else if (which == 1)
                max = max_lr;

            // IBoundingVolume boundingBox =
            // myoObject.getGeometry().getBoundingBox();

        } else if ((Math.abs(cam.getRotY() % 360)) > Math.atan((MAX_FB - Math
                .abs(cam.getZ())) / (MAX_LR - Math.abs(cam.getX())))
                && (Math.abs(cam.getRotY() % 360)) <= (90 + Math
                .atan((MAX_FB - Math.abs(cam.getZ()))
                        / (MAX_LR - Math.abs(cam.getX()))))) {
            max_fb = (int) ((int) (MAX_LR - Math.abs(cam.getX())) / Math
                    .cos(((Math.abs(cam.getRotY() % 360)) - 90) * 3.14159265 / 180.0));
            max_lr = (int) ((int) (MAX_FB - cam.getZ()) / Math.cos(((Math
                    .abs(cam.getRotY() % 360)) - 90) * 3.14159265 / 180.0));
            if (which == 0)
                max = max_fb;
            else if (which == 1)
                max = max_lr;
        } else if ((Math.abs(cam.getRotY() % 360)) > (90 + Math
                .atan((MAX_FB - Math.abs(cam.getZ()))
                        / (MAX_LR - Math.abs(cam.getX()))))
                && (Math.abs(cam.getRotY() % 360)) <= (180 + Math
                .atan((MAX_FB - Math.abs(cam.getZ()))
                        / (MAX_LR - Math.abs(cam.getX()))))) {
            max_fb = (int) ((int) (MAX_FB - Math.abs(cam.getZ())) / Math
                    .cos(((Math.abs(cam.getRotY() % 360)) - 180) * 3.14159265 / 180.0));
            max_lr = (int) ((int) (MAX_LR - Math.abs(cam.getX())) / Math
                    .cos(((Math.abs(cam.getRotY() % 360)) - 180) * 3.14159265 / 180.0));
            if (which == 0)
                max = max_fb;
            else if (which == 1)
                max = max_lr;
        } else if ((Math.abs(cam.getRotY() % 360)) > (180 + Math
                .atan((MAX_FB - Math.abs(cam.getZ()))
                        / (MAX_LR - Math.abs(cam.getX()))))
                && (Math.abs(cam.getRotY() % 360)) <= (270 + Math
                .atan((MAX_FB - Math.abs(cam.getZ()))
                        / (MAX_LR - Math.abs(cam.getX()))))) {
            max_fb = (int) ((int) (MAX_LR - Math.abs(cam.getX())) / Math
                    .cos(((Math.abs(cam.getRotY() % 360)) - 270) * 3.14159265 / 180.0));
            max_lr = (int) ((int) (MAX_FB - Math.abs(cam.getZ())) / Math
                    .cos(((Math.abs(cam.getRotY() % 360)) - 270) * 3.14159265 / 180.0));
            if (which == 0)
                max = max_fb;
            else if (which == 1)
                max = max_lr;
        } else if ((Math.abs(cam.getRotY() % 360)) > (270 + Math
                .atan((MAX_FB - Math.abs(cam.getZ()))
                        / (MAX_LR - Math.abs(cam.getX()))))
                && (Math.abs(cam.getRotY() % 360)) <= 360) {
            max_fb = (int) ((int) (MAX_FB - Math.abs(cam.getZ())) / Math
                    .cos((Math.abs(cam.getRotY() % 360) - 360) * 3.14159265 / 180.0));
            max_lr = (int) ((int) (MAX_LR - Math.abs(cam.getX())) / Math
                    .cos((Math.abs(cam.getRotY() % 360) - 360) * 3.14159265 / 180.0));
            if (which == 0)
                max = max_fb;
            else if (which == 1)
                max = max_lr;
        }

        return max;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (objectStatus.equalsIgnoreCase("no")) {
            menu.findItem(R.id.freeze).setVisible(false);
            // invalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return true;
    }

    @Override
    public void onDestroy() {

        mRenderer.onSurfaceDestroyed();
        mRenderer = null;
        // mSurfaceView.onDestroy();
        super.onDestroy();
    }

    void loadObjectAt(int x, int y, int z) {

    }

    class DoubleTapGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // TODO Auto-generated method stub
            // Log.d("Single Tap", "Single Tap Detected ...");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            // TODO Auto-generated method stub
            // Log.d("TAG", "Double Tap Detected ...");
            if (!isObjectSelected) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((RoomRenderer) mRenderer).getObjectAt(e.getX(), e.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // ((RoomRenderer)
                        // mRenderer).moveSelectedObject(event.getX(),
                        // event.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        String wallName = null;

                        while (wallName == null) {
                            try {
                                wallName = ((RoomRenderer) mRenderer).getWallName();
                                if (wallName != null) {
                                    if(wallName.equals("top")){
                                        new SweetAlertDialog(Room3DActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Warning")
                                                .setContentText("Ceiling design not available!")
                                                .setConfirmText("OK")
                                                .showCancelButton(true)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation();
                                                    }
                                                })
                                                .show();
                                    }
                                    else{
                                        wallInfo.setVisibility(View.GONE);
                                        Intent i = new Intent(Room3DActivity.this,
                                                PatternLibraryActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.putExtra("wall", wallName);
                                        startActivityForResult(i, GET_WALL_REQUEST);
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                wallName = null;
                            }
                        }

                        // ((RoomRenderer) mRenderer).stopMovingSelectedObject();
                        break;
                }
            }
            return true;
        }

		/*
		 * @Override public boolean onDoubleTap(MotionEvent e) {
		 *
		 * }
		 */

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        if (requestCode == GET_WALL_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String wall_processed = data.getCharSequenceExtra("wall").toString();

                String result = data.getCharSequenceExtra("Location")
                        .toString();
                ((RoomRenderer) mRenderer).setWallTexture(result);

                LinkedHashMap<String, Integer> tiles = new LinkedHashMap<String, Integer>();
                @SuppressWarnings("unchecked")
                HashMap<String, Integer> tileMap = (HashMap<String, Integer>) data
                        .getSerializableExtra("tileMap1");
                for (HashMap.Entry<String, Integer> entry : tileMap.entrySet()) {
                    String tile = entry.getKey();
                    int used = entry.getValue();
                    tiles.put(tile, used);
                }
                tileCount(tiles);

                DatabaseManager.deleteProjectTiles(this, projectId, wall_processed);
                saveTileDetails(wall_processed);

            }
            ((RoomRenderer) mRenderer).resetSelectedObject();
        }
    }

    public void saveTileDetails(String wall_processed) {

        try {
            String data = null;
            String path;
            String currentProject = GlobalVariables.getProjectName();

            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/DMD/" + currentProject + "/";
            String saveName;

            saveName = wall_processed + "";

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
            int left = 0, top = 0, width = 0, height = 0, orientation = 0;
            String selectedTileName = null, unit = null, tile_id = null, tile_type = null, tiles_used = null,
                    sel_w = null, sel_h = null;
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
                    if (name.equalsIgnoreCase("width")) {
                        width = Integer.parseInt(readValue);
                    }
                    if (name.equalsIgnoreCase("height")) {
                        height = Integer.parseInt(readValue);
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
                    if (name.equalsIgnoreCase("tilesUsed")) {
                        tiles_used = readValue;
                    }
                    if (name.equalsIgnoreCase("selectedTile")) {
                        selectedTileName = readValue;
                    }
                    if (name.equalsIgnoreCase("mmW")) {
                        sel_w = readValue;
                    }
                    if (name.equalsIgnoreCase("mmH")) {
                        sel_h = readValue;
                    }

                    if (name.equalsIgnoreCase("layoutData")) {
                        ProjectTilesModel projectTiles = new ProjectTilesModel();

                        projectTiles.setProject_id(projectId);
                        projectTiles.setWall_type(wall_processed);
                        projectTiles.setTile_id(tile_id);
                        projectTiles.setTile_count(tiles_used);
                        projectTiles.setTile_type(tile_type);
                        projectTiles.setSel_w(sel_w);
                        projectTiles.setSel_h(sel_h);

                        if(tile_type.equals("P")){
                            projectTiles.setTile_id("P"+tile_id.trim());
                        }

                        DatabaseManager.addProjectTilesInfo(this, projectTiles);
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

            loadTileDetails();

        } catch (Exception e) {

        }
    }

    private void loadTileDetails(){

        DatabaseHandler db = new DatabaseHandler(this);

        Map<String,String> tileMap = new HashMap<>();

        Map<String,Integer> frontMap = new HashMap<>();
        Map<String,Integer> rightMap = new HashMap<>();
        Map<String,Integer> backMap = new HashMap<>();
        Map<String,Integer> leftMap = new HashMap<>();
        Map<String,Integer> topMap = new HashMap<>();
        Map<String,Integer> bottomMap = new HashMap<>();
        Map<String,Integer> distinctMap = new HashMap<>();

        float totalFront = 0;
        float totalRight = 0;
        float totalBack = 0;
        float totalLeft = 0;
        float totalTop = 0;
        float totalBottom = 0;
        float grandTotal = 0;

        int totalTiles = 0;
        int distinctTiles = 0;

        layFront.setVisibility(View.GONE);
        layRight.setVisibility(View.GONE);
        layBack.setVisibility(View.GONE);
        layLeft.setVisibility(View.GONE);
        layTop.setVisibility(View.GONE);
        layBottom.setVisibility(View.GONE);
        layplaceholder.setVisibility(View.VISIBLE);

        List<ProjectTilesModel> projectTilesModels;
        projectTilesModels = DatabaseManager.getProjectTiles(this, projectId);

        for(int i=0; i < projectTilesModels.size(); i++){

            List<TileModel> tileModel;
            float tileH = 0, tileW = 0, dimW = 0, dimH = 0;

            if(GlobalVariables.getUnit().equals("Feet")){
                dimW = GlobalVariables.feetToMm(Float.parseFloat(projectTilesModels.get(i).getSel_w()));
                dimH = GlobalVariables.feetToMm(Float.parseFloat(projectTilesModels.get(i).getSel_h()));
            }
            else if(GlobalVariables.getUnit().equals("Inches")){
                dimW = GlobalVariables.inchesToMm(Float.parseFloat(projectTilesModels.get(i).getSel_w()));
                dimH = GlobalVariables.inchesToMm(Float.parseFloat(projectTilesModels.get(i).getSel_h()));
            }
            else{
                dimW = Float.parseFloat(projectTilesModels.get(i).getSel_w());
                dimH = Float.parseFloat(projectTilesModels.get(i).getSel_h());
            }

            if(projectTilesModels.get(i).getTile_type().equals("P")) {
                String tmp_key = projectTilesModels.get(i).getTile_id().toString().substring(1,
                        projectTilesModels.get(i).getTile_id().length());
                tileModel = db.getTileDetails(tmp_key, "P");
            }
            else{
                tileModel = db.getTileDetails(projectTilesModels.get(i).getTile_id(), "T");
            }

            if(tileModel.size() > 0) {
                String tSize = "";
                tSize = tileModel.get(0).getTile_dimen();
                String[] tileDim = tSize.split("x");
                if (tileDim.length == 0) {
                    tileDim = tSize.split("X");
                }
                tileH = Float.parseFloat(tileDim[0]);
                tileW = Float.parseFloat(tileDim[1]);
            }

            if(tileMap.containsKey(projectTilesModels.get(i).getTile_id())){
                String[] tmp = tileMap.get(projectTilesModels.get(i).getTile_id()).toString().split("#");
                tileMap.put(projectTilesModels.get(i).getTile_id(), ( Float.parseFloat(tmp[0]) + (float) (dimW * dimH))
                        +"#"+(tileW * tileH));
            }
            else{
                tileMap.put(projectTilesModels.get(i).getTile_id(), (dimW * dimH)+"#"+(tileW * tileH));
            }

            /*if (frontMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                frontMap.put(projectTilesModels.get(i).getTile_id(),
                        Integer.valueOf(projectTilesModels.get(i).getTile_count()) +
                                frontMap.get(projectTilesModels.get(i).getTile_id()));
            } else {
                frontMap.put(projectTilesModels.get(i).getTile_id(),
                        Integer.valueOf(projectTilesModels.get(i).getTile_count()));
                if (frontMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                    distinctMap.put(projectTilesModels.get(i).getTile_id(), 0);
                }
            }*/

            /*if(projectTilesModels.get(i).getWall_type().trim().equalsIgnoreCase("front")) {
                if (frontMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                    frontMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()) +
                                    frontMap.get(projectTilesModels.get(i).getTile_id()));
                } else {
                    frontMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()));
                    if (frontMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                        distinctMap.put(projectTilesModels.get(i).getTile_id(), 0);
                    }
                }
            }

            if(projectTilesModels.get(i).getWall_type().trim().equalsIgnoreCase("right")) {
                if (rightMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                    rightMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()) +
                                    rightMap.get(projectTilesModels.get(i).getTile_id()));
                } else {
                    rightMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()));
                    if (rightMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                        distinctMap.put(projectTilesModels.get(i).getTile_id(), 0);
                    }
                }
            }

            if(projectTilesModels.get(i).getWall_type().trim().equalsIgnoreCase("back")) {
                if (backMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                    backMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()) +
                                    backMap.get(projectTilesModels.get(i).getTile_id()));
                } else {
                    backMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()));
                    if (backMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                        distinctMap.put(projectTilesModels.get(i).getTile_id(), 0);
                    }
                }
            }

            if(projectTilesModels.get(i).getWall_type().trim().equalsIgnoreCase("left")) {
                if (leftMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                    leftMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()) +
                                    leftMap.get(projectTilesModels.get(i).getTile_id()));
                } else {
                    leftMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()));
                    if (leftMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                        distinctMap.put(projectTilesModels.get(i).getTile_id(), 0);
                    }
                }
            }

            if(projectTilesModels.get(i).getWall_type().trim().equalsIgnoreCase("top")) {
                if (topMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                    topMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()) +
                                    topMap.get(projectTilesModels.get(i).getTile_id()));
                } else {
                    topMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()));
                    if (topMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                        distinctMap.put(projectTilesModels.get(i).getTile_id(), 0);
                    }
                }
            }

            if(projectTilesModels.get(i).getWall_type().trim().equalsIgnoreCase("bottom")) {
                if (bottomMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                    bottomMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()) +
                                    bottomMap.get(projectTilesModels.get(i).getTile_id()));
                } else {
                    bottomMap.put(projectTilesModels.get(i).getTile_id(),
                            Integer.valueOf(projectTilesModels.get(i).getTile_count()));
                    if (bottomMap.get(projectTilesModels.get(i).getTile_id()) != null) {
                        distinctMap.put(projectTilesModels.get(i).getTile_id(), 0);
                    }
                }
            }*/

        }

        if(tileMap.size() > 0) {

            Iterator it = tileMap.entrySet().iterator();
            ArrayList<TileListModel> tileList = new ArrayList<>();

            layFront.setVisibility(View.VISIBLE);

            while (it.hasNext()) {
                String[] tmp;
                int count = 0;
                String key="";
                Map.Entry pair = (Map.Entry) it.next();

                tmp = pair.getValue().toString().split("#");
                if(pair.getKey().toString().substring(0, 1).equals("P")){
                    String tmp_key = pair.getKey().toString().substring(1, pair.getKey().toString().length());
                    key = tmp_key;
                }
                else{
                    key = pair.getKey().toString();
                }


                count = (int) (Float.parseFloat(tmp[0]) / Float.parseFloat(tmp[1]));
                float fl;
                if ((Float.parseFloat(tmp[0]) % Float.parseFloat(tmp[1]) ) > 0) {
                    count++;
                }

                totalTiles += count;

                List<TileModel> tileModel;

                if(pair.getKey().toString().substring(0, 1).equals("P")) {
                    tileModel = db.getTileDetails(key, "P");
                }
                else{
                    tileModel = db.getTileDetails(key, "T");
                }

                if(tileModel.size() > 0) {

                    TileListModel tileListModels = new TileListModel();
                    float price = 0;
                    price = count * Float.valueOf(tileModel.get(0).getTile_price());

                    tileListModels.setTile_name(tileModel.get(0).getTile_name());
                    tileListModels.setTile_brand(tileModel.get(0).getTile_brand());
                    tileListModels.setTile_dimen(tileModel.get(0).getTile_dimen());
                    tileListModels.setTile_count(count+"");
                    tileListModels.setTile_rate(String.format("%.2f", Float.valueOf(tileModel.get(0).getTile_price())));
                    tileListModels.setTile_price(String.format("%.2f", price));
                    if(pair.getKey().toString().substring(0, 1).equals("P")) {
                        tileListModels.setType("P");
                    }
                    else{
                        tileListModels.setType("T");
                    }

                    tileList.add(tileListModels);

                    totalFront = totalFront + price;
                }
                distinctMap.put(key, 0);
                it.remove(); // avoids a ConcurrentModificationException
            }

            TileRecyclerAdapter adapter = new TileRecyclerAdapter(this, tileList);
            front_recycler_view.setHasFixedSize(false);
            front_recycler_view.setNestedScrollingEnabled(false);

            LinearLayoutManager frontLay = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            front_recycler_view.setLayoutManager(frontLay);
            front_recycler_view.setAdapter(adapter);

            frontTotal.setText("Rs."+String.format("%.2f", totalFront));
        }

        /*if(frontMap.size() > 0){

            ArrayList<TileListModel> tileList = new ArrayList<>();
            Iterator it = frontMap.entrySet().iterator();

            layFront.setVisibility(View.VISIBLE);

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                String key="", key_db="";
                List<TileModel> tileModel;

                key = pair.getKey().toString().substring(0, 1);
                key_db = pair.getKey().toString();

                if(key_db.length() >= 2) {
                    key_db = key_db.substring(1, key_db.length());
                }

                if(key.equals("P")) {
                    tileModel = db.getTileDetails(key_db, "P");
                }
                else{
                    tileModel = db.getTileDetails(key_db, "T");
                }

                totalTiles = totalTiles + (Integer) pair.getValue();

                if(tileModel.size() > 0) {

                    TileListModel tileListModels = new TileListModel();
                    float price = 0;
                    price = (Integer) pair.getValue() * Float.valueOf(tileModel.get(0).getTile_price());

                    tileListModels.setTile_name(tileModel.get(0).getTile_name());
                    tileListModels.setTile_brand(tileModel.get(0).getTile_brand());
                    tileListModels.setTile_dimen(tileModel.get(0).getTile_dimen());
                    tileListModels.setTile_count(pair.getValue().toString());
                    tileListModels.setTile_rate(String.format("%.2f", Float.valueOf(tileModel.get(0).getTile_price())));
                    tileListModels.setTile_price(String.format("%.2f", price));

                    tileList.add(tileListModels);

                    totalFront = totalFront + price;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            TileRecyclerAdapter adapter = new TileRecyclerAdapter(this, tileList);
            front_recycler_view.setHasFixedSize(false);
            front_recycler_view.setNestedScrollingEnabled(false);

            LinearLayoutManager frontLay = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            front_recycler_view.setLayoutManager(frontLay);
            front_recycler_view.setAdapter(adapter);

            frontTotal.setText("Rs."+String.format("%.2f", totalFront));
        }

        if(rightMap.size() > 0){

            ArrayList<TileListModel> tileList = new ArrayList<>();
            Iterator it = rightMap.entrySet().iterator();

            layRight.setVisibility(View.VISIBLE);

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                String key="", key_db="";
                List<TileModel> tileModel;

                key = pair.getKey().toString().substring(0, 1);
                key_db = pair.getKey().toString();

                if(key_db.length() >= 2) {
                    key_db = key_db.substring(1, key_db.length());
                }

                if(key.equals("P")) {
                    tileModel = db.getTileDetails(key_db, "P");
                }
                else{
                    tileModel = db.getTileDetails(key_db, "T");
                }

                totalTiles = totalTiles + (Integer) pair.getValue();

                if(tileModel.size() > 0) {

                    TileListModel tileListModels = new TileListModel();
                    float price = 0;
                    price = (Integer) pair.getValue() * Float.valueOf(tileModel.get(0).getTile_price());

                    tileListModels.setTile_name(tileModel.get(0).getTile_name());
                    tileListModels.setTile_brand(tileModel.get(0).getTile_brand());
                    tileListModels.setTile_dimen(tileModel.get(0).getTile_dimen());
                    tileListModels.setTile_count(pair.getValue().toString());
                    tileListModels.setTile_rate(String.format("%.2f", Float.valueOf(tileModel.get(0).getTile_price())));
                    tileListModels.setTile_price(String.format("%.2f", price));

                    tileList.add(tileListModels);

                    totalRight = totalRight + price;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            TileRecyclerAdapter adapter = new TileRecyclerAdapter(this, tileList);
            right_recycler_view.setHasFixedSize(false);
            right_recycler_view.setNestedScrollingEnabled(false);

            LinearLayoutManager rightLay = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            right_recycler_view.setLayoutManager(rightLay);
            right_recycler_view.setAdapter(adapter);

            rightTotal.setText("Rs."+String.format("%.2f", totalRight));
        }

        if(backMap.size() > 0){

            ArrayList<TileListModel> tileList = new ArrayList<>();
            Iterator it = backMap.entrySet().iterator();

            layBack.setVisibility(View.VISIBLE);

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                String key="", key_db="";
                List<TileModel> tileModel;

                key = pair.getKey().toString().substring(0, 1);
                key_db = pair.getKey().toString();

                if(key_db.length() >= 2) {
                    key_db = key_db.substring(1, key_db.length());
                }

                if(key.equals("P")) {
                    tileModel = db.getTileDetails(key_db, "P");
                }
                else{
                    tileModel = db.getTileDetails(key_db, "T");
                }

                totalTiles = totalTiles + (Integer) pair.getValue();

                if(tileModel.size() > 0) {

                    TileListModel tileListModels = new TileListModel();
                    float price = 0;
                    price = (Integer) pair.getValue() * Float.valueOf(tileModel.get(0).getTile_price());

                    tileListModels.setTile_name(tileModel.get(0).getTile_name());
                    tileListModels.setTile_brand(tileModel.get(0).getTile_brand());
                    tileListModels.setTile_dimen(tileModel.get(0).getTile_dimen());
                    tileListModels.setTile_count(pair.getValue().toString());
                    tileListModels.setTile_rate(String.format("%.2f", Float.valueOf(tileModel.get(0).getTile_price())));
                    tileListModels.setTile_price(String.format("%.2f", price));

                    tileList.add(tileListModels);

                    totalBack = totalBack + price;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            TileRecyclerAdapter adapter = new TileRecyclerAdapter(this, tileList);
            back_recycler_view.setHasFixedSize(false);
            back_recycler_view.setNestedScrollingEnabled(false);

            LinearLayoutManager backLay = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            back_recycler_view.setLayoutManager(backLay);
            back_recycler_view.setAdapter(adapter);

            backTotal.setText("Rs."+String.format("%.2f", totalBack));
        }

        if(leftMap.size() > 0){

            ArrayList<TileListModel> tileList = new ArrayList<>();
            Iterator it = leftMap.entrySet().iterator();

            layLeft.setVisibility(View.VISIBLE);

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                String key="", key_db="";
                List<TileModel> tileModel;

                key = pair.getKey().toString().substring(0, 1);
                key_db = pair.getKey().toString();

                if(key_db.length() >= 2) {
                    key_db = key_db.substring(1, key_db.length());
                }

                if(key.equals("P")) {
                    tileModel = db.getTileDetails(key_db, "P");
                }
                else{
                    tileModel = db.getTileDetails(key_db, "T");
                }

                totalTiles = totalTiles + (Integer) pair.getValue();

                if(tileModel.size() > 0) {

                    TileListModel tileListModels = new TileListModel();
                    float price = 0;
                    price = (Integer) pair.getValue() * Float.valueOf(tileModel.get(0).getTile_price());

                    tileListModels.setTile_name(tileModel.get(0).getTile_name());
                    tileListModels.setTile_brand(tileModel.get(0).getTile_brand());
                    tileListModels.setTile_dimen(tileModel.get(0).getTile_dimen());
                    tileListModels.setTile_count(pair.getValue().toString());
                    tileListModels.setTile_rate(String.format("%.2f", Float.valueOf(tileModel.get(0).getTile_price())));
                    tileListModels.setTile_price(String.format("%.2f", price));

                    tileList.add(tileListModels);

                    totalLeft = totalLeft + price;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            TileRecyclerAdapter adapter = new TileRecyclerAdapter(this, tileList);
            left_recycler_view.setHasFixedSize(false);
            left_recycler_view.setNestedScrollingEnabled(false);

            LinearLayoutManager leftLay = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            left_recycler_view.setLayoutManager(leftLay);
            left_recycler_view.setAdapter(adapter);

            leftTotal.setText("Rs."+String.format("%.2f", totalLeft));
        }

        if(topMap.size() > 0){

            ArrayList<TileListModel> tileList = new ArrayList<>();
            Iterator it = topMap.entrySet().iterator();

            layTop.setVisibility(View.VISIBLE);

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                String key="", key_db="";
                List<TileModel> tileModel;

                key = pair.getKey().toString().substring(0, 1);
                key_db = pair.getKey().toString();

                if(key_db.length() >= 2) {
                    key_db = key_db.substring(1, key_db.length());
                }

                if(key.equals("P")) {
                    tileModel = db.getTileDetails(key_db, "P");
                }
                else{
                    tileModel = db.getTileDetails(key_db, "T");
                }

                totalTiles = totalTiles + (Integer) pair.getValue();

                if(tileModel.size() > 0) {

                    TileListModel tileListModels = new TileListModel();
                    float price = 0;
                    price = (Integer) pair.getValue() * Float.valueOf(tileModel.get(0).getTile_price());

                    tileListModels.setTile_name(tileModel.get(0).getTile_name());
                    tileListModels.setTile_brand(tileModel.get(0).getTile_brand());
                    tileListModels.setTile_dimen(tileModel.get(0).getTile_dimen());
                    tileListModels.setTile_count(pair.getValue().toString());
                    tileListModels.setTile_rate(String.format("%.2f", Float.valueOf(tileModel.get(0).getTile_price())));
                    tileListModels.setTile_price(String.format("%.2f", price));

                    tileList.add(tileListModels);

                    totalTop = totalTop + price;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            TileRecyclerAdapter adapter = new TileRecyclerAdapter(this, tileList);
            top_recycler_view.setHasFixedSize(false);
            top_recycler_view.setNestedScrollingEnabled(false);

            LinearLayoutManager topLay = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            top_recycler_view.setLayoutManager(topLay);
            top_recycler_view.setAdapter(adapter);

            topTotal.setText("Rs."+String.format("%.2f", totalTop));
        }

        if(bottomMap.size() > 0){

            ArrayList<TileListModel> tileList = new ArrayList<>();
            Iterator it = bottomMap.entrySet().iterator();

            layBottom.setVisibility(View.VISIBLE);

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();

                String key="", key_db="";
                List<TileModel> tileModel;

                key = pair.getKey().toString().substring(0, 1);
                key_db = pair.getKey().toString();

                if(key_db.length() >= 2) {
                    key_db = key_db.substring(1, key_db.length());
                }

                if(key.equals("P")) {
                    tileModel = db.getTileDetails(key_db, "P");
                }
                else{
                    tileModel = db.getTileDetails(key_db, "T");
                }

                totalTiles = totalTiles + (Integer) pair.getValue();

                if(tileModel.size() > 0) {

                    TileListModel tileListModels = new TileListModel();
                    float price = 0;
                    price = (Integer) pair.getValue() * Float.valueOf(tileModel.get(0).getTile_price());

                    tileListModels.setTile_name(tileModel.get(0).getTile_name());
                    tileListModels.setTile_brand(tileModel.get(0).getTile_brand());
                    tileListModels.setTile_dimen(tileModel.get(0).getTile_dimen());
                    tileListModels.setTile_count(pair.getValue().toString());
                    tileListModels.setTile_rate(String.format("%.2f", Float.valueOf(tileModel.get(0).getTile_price())));
                    tileListModels.setTile_price(String.format("%.2f", price));

                    tileList.add(tileListModels);

                    totalBottom = totalBottom + price;
                }
                it.remove(); // avoids a ConcurrentModificationException
            }

            TileRecyclerAdapter adapter = new TileRecyclerAdapter(this, tileList);
            bottom_recycler_view.setHasFixedSize(false);
            bottom_recycler_view.setNestedScrollingEnabled(false);

            LinearLayoutManager bottomLay = new LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
            );
            bottom_recycler_view.setLayoutManager(bottomLay);
            bottom_recycler_view.setAdapter(adapter);

            bottomTotal.setText("Rs."+String.format("%.2f", totalBottom));
        }*/

        grandTotal = totalFront + totalRight + totalBack + totalLeft + totalTop + totalBottom;
        grand_total.setText("Rs."+String.format("%.2f", grandTotal));

        distinctTiles = distinctMap.size();

        if(distinctTiles > 0) {
            layplaceholder.setVisibility(View.GONE);
            tiles_used.setText(distinctTiles + " Tiles, " + totalTiles + " Pieces");
        }
        else {
            tiles_used.setText("No Tiles Used");
        }

    }


    void goback() {
        finish();
    }

//	double xpos, ypos, xd;

    /**
     * Detects left and right swipes across a view.
     */
    public class OnSwipeTouchListener implements OnTouchListener {
        final GestureDetector mGesDetect = new GestureDetector(
                getApplicationContext(), new DoubleTapGestureDetector());
        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context,
                    new GestureListener());
        }

        public void onSwipeLeft() {
        }

        public void onSwipeRight() {
        }

        public void onSwipeUp() {
        }

        public void onSwipeDown() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            mGesDetect.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            Log.d("Touch","Not null");
//			if (isObjectSelected) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//					Log.d("Touch"," DOWN");
					/*((RoomRenderer) mRenderer).get3DObjectAt(event.getX(),
							event.getY());
					obj = mRenderer.getPicked3D();
					if (obj != null) {
						obj.setShowBoundingVolume(true);
						obj.setPickingColor(Color.RED);
						obj.reload();
					}*/

					/*xpos = event.getX();
					ypos = event.getY();*/
                    break;
                case MotionEvent.ACTION_MOVE:
//					Log.d("Touch"," MOVE");
					/*xd = event.getX() - xpos;
					yd = event.getY() - ypos;

					xpos = event.getX();
					ypos = event.getY();

					if (xd < 0) {
						((RoomRenderer) mRenderer).up = true;
					} else {
						((RoomRenderer) mRenderer).down = true;
					}
					if (yd < 0) {
						((RoomRenderer) mRenderer).left = true;
					} else {
						((RoomRenderer) mRenderer).right = true;
					}*/
                    break;
                case MotionEvent.ACTION_UP:
//					Log.d("Touch"," UP");
					/*if (obj != null) {
						obj.setShowBoundingVolume(true);
						obj.setPickingColor(Color.RED);
						obj.reload();

					}*/
					/*xpos = -1;
					ypos = -1;
					((RoomRenderer) mRenderer).left = false;
					((RoomRenderer) mRenderer).right = false;
					((RoomRenderer) mRenderer).up = false;
					((RoomRenderer) mRenderer).down = false;*/
                    break;
            }
//			}
            return true;
        }

        private final class GestureListener extends SimpleOnGestureListener {

            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // TODO Auto-generated method stub
                // Log.d("Click Listener", "Click Detected ...");
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY)
                        && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                } else {
                    if (distanceY < 0)
                        onSwipeUp();
                    else
                        onSwipeDown();
                    return true;
                }
                // return false;
            }
        }
    }

    public void tileCount(LinkedHashMap<String, Integer> tiles) {
        // TODO Auto-generated method stub
        for (LinkedHashMap.Entry<String, Integer> entry : tiles.entrySet()) {
            String tile = entry.getKey();
            int used = entry.getValue();
            if (tilesUsed.containsKey(tile)) {
                int currentUsed = tilesUsed.get(tile);
                used += currentUsed;
                tilesUsed.remove(tile);
            }
            if (used > 0)
                tilesUsed.put(tile, used);
        }
        saveTileCount(tilesUsed);
    }

    void saveTileCount(LinkedHashMap<String, Integer> tiles) {
        try {
            String path;
            String currentProject = GlobalVariables.getProjectName();

            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/DMD/" + currentProject + "/";
            File directoryPath = new File(path);
            if (!directoryPath.exists()) {
                directoryPath.mkdirs();
                GlobalVariables.createNomediafile(path);
            }
            String saveName;

            saveName = "tiles";

            final String xmlFile = path + saveName + ".xml";
            File f = new File(xmlFile);
            if (f.exists()) {
                f.delete();
            }

            FileWriter out = new FileWriter(new File(xmlFile));

            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);

            for (LinkedHashMap.Entry<String, Integer> entry : tiles.entrySet()) {
                String tile = entry.getKey();
                int used = entry.getValue();

                xmlSerializer.startTag(null, "tile");

                xmlSerializer.startTag(null, "name");
                xmlSerializer.text(tile + "");
                xmlSerializer.endTag(null, "name");

                xmlSerializer.startTag(null, "count");
                xmlSerializer.text(used + "");
                xmlSerializer.endTag(null, "count");

                xmlSerializer.endTag(null, "tile");

            }
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            out.write(dataWrite);
            out.close();
            // fileos.write();
            // fileos.close();
        } catch (Exception e) {
            // Log.e("Save Layout Error", e.printStackTrace());
            e.printStackTrace();
        }
    }

    void loadTileCount() {
        String data = null;
        String path;
        String currentProject = GlobalVariables.getProjectName();

        path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/DMD/" + currentProject + "/";
        File directoryPath = new File(path);
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
            GlobalVariables.createNomediafile(path);
        }
        String saveName;

        saveName = "tiles";

        final String xmlFile = path + saveName + ".xml";
        if (!new File(xmlFile).exists())
            return;

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
        } catch (Exception e) {
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
        String tileName = null, readValue = null;
        int count = 0;
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                name = xpp.getName();
                if (name == "layoutData") {

                }
            } else if (eventType == XmlPullParser.END_TAG) {
                name = xpp.getName();
                if (name.equalsIgnoreCase("name")) {
                    tileName = readValue;
                }
                if (name.equalsIgnoreCase("count")) {
                    count = Integer.parseInt(readValue);
                }

                if (name.equalsIgnoreCase("tile")) {
                    tilesUsed.put(tileName, count);

                }
            } else if (eventType == XmlPullParser.TEXT) {
                // userData.add(xpp.getText());
                readValue = xpp.getText();
                // Log.i("value",xpp.getText());
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
        // String userName=userData.get(0);
        // String password=userData.get(1);
    }

    class MyDragListener implements OnDragListener {
        Drawable enterShape = getResources().getDrawable(
                R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @SuppressWarnings("deprecation")
        @Override
        public boolean onDrag(View v, DragEvent event) {
            // int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }

    // public class Fhreeze extends AsyncTask<String, String, String> implements
    // ScreenshotListener {
    // ProgressDialog dialog1;
    // Context context;
    // String path1 = Environment.getExternalStorageDirectory()
    // .getAbsolutePath() + "/DMD/FreezeShots/";
    //
    // public Freeze(Context context) {
    //
    // this.context = context;
    // }
    //
    // @Override
    // protected String doInBackground(String... params) {
    // // TODO Auto-generated method stub
    //
    // FrontWall fw = new FrontWall(this);
    // // LeftWall lw=new LeftWall();
    // // RightWall rw=new RightWall();
    // // BackWall bw=new BackWall();
    //
    // // try {
    // fw.start();
    // // fw.join();
    // // Thread.sleep(3000);
    //
    // // lw.start();
    // // lw.join();
    // // Thread.sleep(4000);
    // //
    // // bw.start();
    // // bw.join();
    // // Thread.sleep(3000);
    // //
    // // rw.start();
    // // rw.join();
    // // Thread.sleep(3000);
    //
    // // } catch (InterruptedException e) {
    // // // TODO Auto-generated catch block
    // // e.printStackTrace();
    // // }
    //
    // return null;
    // }
    //
    // @Override
    // protected void onPreExecute() {
    // // TODO Auto-generated method stub
    // // DatabaseHandler.deleteallshots(path1);
    // dialog1 = new ProgressDialog(context);
    // dialog1.setMessage("Rendering Views...Please Wait...");
    // dialog1.show();
    // super.onPreExecute();
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    // // TODO Auto-generated method stub
    // super.onPostExecute(result);
    // if (dialog1.isShowing()) {
    // dialog1.dismiss();
    // }
    //
    // Intent in = new Intent(Room3DActivity.this, ViewSelector.class);
    // startActivity(in);
    // }
    //
    // @Override
    // public void onFrontshotTaken() {
    // // TODO Auto-generated method stub
    // Log.i("screeenshot", "frnt");
    // BackWall bw = new BackWall(this);
    // bw.start();
    // }
    //
    // @Override
    // public void onBackshotTaken() {
    // // TODO Auto-generated method stub
    // Log.i("screeenshot", "back");
    // LeftWall lw = new LeftWall(this);
    // lw.start();
    // }
    //
    // @Override
    // public void onLeftshotTaken() {
    // // TODO Auto-generated method stub
    // Log.i("screeenshot", "left");
    // RightWall bw = new RightWall(this);
    // bw.start();
    // }
    //
    // @Override
    // public void onRightshotTaken() {
    // // TODO Auto-generated method stub
    // Log.i("screeenshot", "back");
    // // BackWall bw = new BackWall(this);
    // // bw.start();
    // }
    //
    // }
    @Override
    public void onFrontshotTaken() {
        // TODO Auto-generated method stub
        Log.i("screeenshot", "frnt");
        BackWall bw = new BackWall(this);
        bw.start();
    }

    @Override
    public void onBackshotTaken() {
        // TODO Auto-generated method stub
        Log.i("screeenshot", "back");
        LeftWall lw = new LeftWall(this);
        lw.start();
    }

    @Override
    public void onLeftshotTaken() {
        // TODO Auto-generated method stub
        Log.i("screeenshot", "left");
        RightWall bw = new RightWall(this);
        bw.start();
    }

    @Override
    public void onRightshotTaken() {
        // TODO Auto-generated method stub
        Log.i("screeenshot", "back");
        if (dialog12.isShowing()) {
            dialog12.dismiss();
        }
        isLiveView=true;
//		Intent in = new Intent(Room3DActivity.this, ViewSelector.class);
//		startActivity(in);
    }

    public class FrontWall extends Thread {
        ScreenshotListener listener;

        public FrontWall(ScreenshotListener listener) {
            this.listener = listener;

        }

        ProgressDialog dialog1;
        Context context;
        String path1 = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/DMD/FreezeShots/";

        public void run() {
            setCamera(new Vector3(0.0, 0.0, MAX_FB), new Vector3(0, 0.0, 0.0));
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/DMD/FreezeShots/"
                    + GlobalVariables.getProjectName() + "-Front";
            // path1= Environment.getExternalStorageDirectory()
            // .getAbsolutePath()
            // + "/DMD/FreezeShots/";

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            freezeshot(path, path1, ScreenshotListener.VIEW_FRONT, listener);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // listener.onFrontshotTaken();
        }
    }

    public class LeftWall extends Thread {
        ScreenshotListener listener;

        public LeftWall(ScreenshotListener listener) {
            this.listener = listener;

        }

        ProgressDialog dialog1;
        Context context;
        String path1 = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/DMD/FreezeShots/";

        public void run() {
            setCamera(new Vector3(0.0, 0.0, MAX_LR), new Vector3(0, -90.0, 0.0));
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/DMD/FreezeShots/"
                    + GlobalVariables.getProjectName() + "-Left";
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            freezeshot(path, path1, ScreenshotListener.VIEW_LEFT, listener);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public class RightWall extends Thread {
        ScreenshotListener listener;

        public RightWall(ScreenshotListener listener) {
            this.listener = listener;

        }

        ProgressDialog dialog1;
        Context context;
        String path1 = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/DMD/FreezeShots/";

        public void run() {
            setCamera(new Vector3(0.0, 0.0, MAX_LR), new Vector3(0, 90.0, 0.0));
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/DMD/FreezeShots/"
                    + GlobalVariables.getProjectName() + "-Right";
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            freezeshot(path, path1, ScreenshotListener.VIEW_RIGHT, listener);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public class BackWall extends Thread {
        ScreenshotListener listener;

        public BackWall(ScreenshotListener listener) {
            this.listener = listener;

        }

        ProgressDialog dialog1;
        Context context;
        String path1 = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/DMD/FreezeShots/";

        public void run() {
            setCamera(new Vector3(0.0, 0.0, MAX_FB), new Vector3(0, 180.0, 0.0));
            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + "/DMD/FreezeShots/"
                    + GlobalVariables.getProjectName() + "-Back";
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            freezeshot(path, path1, ScreenshotListener.VIEW_BACK, listener);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}