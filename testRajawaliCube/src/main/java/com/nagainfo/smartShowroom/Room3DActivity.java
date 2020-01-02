package com.nagainfomob.smartShowroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.nagainfomob.database.DatabaseHandler;
import com.nagainfomob.smartShowroom.DrawView.LayoutDimensions;

import rajawali.Camera;
import rajawali.Object3D;
import rajawali.RajawaliActivity;
import rajawali.animation.RotateAnimation3D;
import rajawali.animation.Animation3D.RepeatMode;
import rajawali.bounds.IBoundingVolume;
import rajawali.math.vector.Vector3;
import rajawali.math.vector.Vector3.Axis;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;
import rajawali.primitives.Plane;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.DragEvent;
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
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Room3DActivity extends RajawaliActivity implements
		OnTouchListener, ScreenshotListener {
	private RoomRenderer mRenderer;
	Camera cam;
	float length, width, height;
	Object3D obj = null;
	private boolean isObjectSelected = false;
	Boolean stop = false, stopCam = false;
	Boolean isLiveView = true;
	static final double DEG_TO_RAD = 0.0174532925;

	private Handler handler = new Handler();
	static final int GET_WALL_REQUEST = 1; // The request code
	static final int MOVEMENT_SPEED = 75;
	int MAX_BACK = 495;
	int scale = 1000;
	Button upButton, downButton, leftButton, rightButton;
	Button uButton, dButton, lButton, rButton;
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

	// GestureDetector mGesDetect;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dialog12 = new ProgressDialog(Room3DActivity.this);
		isObjectSelected = false;
		String title = getResources().getString(R.string.app_name)
				+ "\t\t---\t\t" + GlobalVariables.getProjectName();
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.actionbar_bg));
		bar.setTitle(title);
		Intent intent = getIntent();

		/*String l = intent.getStringExtra("dimenA");
		length = Float.parseFloat(l);

		String w = intent.getStringExtra("dimenB");
		width = Float.parseFloat(w);

		String h = intent.getStringExtra("wallHeight");
		height = Float.parseFloat(h);*/

		length = 20;
		width = 20;
		height = 20;

//		Log.d("test", length+"_"+width+"_"+height);

		if (intent.getExtras().containsKey("OBJECTS")) {
			objectStatus = intent.getStringExtra("OBJECTS");
			invalidateOptionsMenu();
		} else {
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
		}
		MAX_BACK = (int) (Math
				.min(Math.min(length * scale / height, width * scale / height),
						1000) / 2);
		speed = 75;
		MAX_TB = (float) 499.9;
		MAX_LR = (float) (((length * scale / height) / 2)- 0.5);
		MAX_FB = (float) (((width * scale / height) / 2)- 0.5);
		// MAX_BACK=(int) MAX_FB;
		// } catch (Exception e) {
		// // Log.e("Dimension",e.getMessage());
		// length = 1;
		// width = 1;
		// height = 1;
		// }
		loadTileCount();

		// mRenderer = new RoomRenderer(getApplicationContext(), length, width,
		// height);
		mRenderer = new RoomRenderer(getApplicationContext(), length, width,
				height);
		mSurfaceView.setOnTouchListener(new OnSwipeTouchListener(
				getApplicationContext()) {

		});
		cam = mRenderer.getCurrentCamera();
		LayoutInflater inflater = LayoutInflater.from(this);
		fl = (FrameLayout) inflater.inflate(R.layout.activity_3d, null);
		intialize();
		// ImageView objImg = (ImageView) fl.findViewById(R.id.objimage);
		// objImg.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		// objImg.setOnDragListener(new MyDragListener());
		upButton = (Button) fl.findViewById(R.id.upButton);
		upButton.setTag("1");
		upButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
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
				return false;
			}
		});
		upButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					if (!isObjectSelected) {
						stop = true;
					} else {
						int tag = Integer.parseInt((String) arg0.getTag());
						rotateObject(tag);
					}

				}
				return false;
			}
		});

		downButton = (Button) fl.findViewById(R.id.downButton);
		downButton.setTag("2");
		downButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
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
				return false;

			}
		});

		downButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					if (!isObjectSelected) {
						stop = true;
					} else {
						int tag = Integer.parseInt((String) arg0.getTag());
						rotateObject(tag);
					}
				}
				return false;
			}
		});

		leftButton = (Button) fl.findViewById(R.id.leftButton);
		leftButton.setTag("3");
		leftButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
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
				return false;
			}
		});
		leftButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					if (!isObjectSelected) {
						stop = true;
					} else {
						int tag = Integer.parseInt((String) arg0.getTag());
						rotateObject(tag);
					}
				}
				return false;
			}
		});
		rightButton = (Button) fl.findViewById(R.id.rightButton);
		rightButton.setTag("4");
		rightButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
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
				return false;
			}
		});
		rightButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					if (!isObjectSelected) {
						stop = true;
					} else {
						int tag = Integer.parseInt((String) arg0.getTag());
						rotateObject(tag);
					}
				}
				return false;
			}
		});
		uButton = (Button) fl.findViewById(R.id.uButton);
		uButton.setTag("1");
		uButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if (!isLiveView) {
					Toast.makeText(
							getApplicationContext(),
							"Please select LIVE camera to move around in the room",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				stopCam = false;
				handler.post(new CameraUpdater(uButton));
				return false;
			}
		});
		uButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					stopCam = true;

				}
				return false;
			}
		});

		dButton = (Button) fl.findViewById(R.id.dButton);
		dButton.setTag("2");
		dButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if (!isLiveView) {
					Toast.makeText(
							getApplicationContext(),
							"Please select LIVE camera to move around in the room",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				stopCam = false;
				handler.post(new CameraUpdater(dButton));
				return false;
			}
		});
		dButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					stopCam = true;

				}
				return false;
			}
		});

		lButton = (Button) fl.findViewById(R.id.lButton);
		lButton.setTag("3");
		lButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if (!isLiveView) {
					Toast.makeText(
							getApplicationContext(),
							"Please select LIVE camera to move around in the room",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				stopCam = false;
				handler.post(new CameraUpdater(lButton));
				return false;
			}
		});
		lButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					stopCam = true;

				}
				return false;
			}
		});

		rButton = (Button) fl.findViewById(R.id.rButton);
		rButton.setTag("4");
		rButton.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				if (!isLiveView) {
					Toast.makeText(
							getApplicationContext(),
							"Please select LIVE camera to move around in the room",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				stopCam = false;
				handler.post(new CameraUpdater(rButton));
				return false;
			}
		});
		rButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if ((event.getAction() == MotionEvent.ACTION_UP || event
						.getAction() == MotionEvent.ACTION_CANCEL)) {
					stopCam = true;

				}
				return false;
			}
		});

		mLayout.addView(fl);

		// addContentView(ll, new LayoutParams(400, 400));

		mRenderer.setSurfaceView(mSurfaceView);

		super.setRenderer(mRenderer);

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
			break;
		case R.id.screenShot:
			takeScreenShot();
			break;
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
		((RoomRenderer) mRenderer).takeScreenshot();
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/SmartShowRoom/ScreenShots/"
				+ GlobalVariables.getProjectName() + "/";
		Toast.makeText(getApplicationContext(),
				"Screenshot has been saved to gallery", Toast.LENGTH_SHORT)
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
				handler.postDelayed(new MovementUpdater(v), 100);
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
				if (isObjectSelected) {

					// switch (viewTag) {
					// case 1:
					// // up arrow
					// if (obj != null)
					// obj.setPosition(obj.getX(), obj.getY(), obj.getZ()
					// - MOVEMENT_SPEED);
					// break;
					// case 2:
					// // down arrow
					// if (obj != null)
					// obj.setPosition(obj.getX(), obj.getY(), obj.getZ()
					// + MOVEMENT_SPEED);
					// // Log.i("back Z ",cam.getZ()+"");
					// break;
					// case 3:
					// // left arrow
					// if (obj != null)
					// obj.setPosition(obj.getX() - MOVEMENT_SPEED,
					// obj.getY(), obj.getZ());
					// break;
					// case 4:
					// // right arrow
					// if (obj != null)
					// obj.setPosition(obj.getX() + MOVEMENT_SPEED,
					// obj.getY(), obj.getZ());
					// break;
					// }

				} else {
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
						// Log.i("back Z ",cam.getZ()+"");
						break;
					case 3:
						// setCamera(new Vector3(0.0, 0.0, 499.9), new
						// Vector3(0,0,0));
						// isLiveView = true;
						// cam.setX(0);
						// cam.setY(0);
						// cam.setZ(0);
						// isLiveView = true;
						cam.setRotY(cam.getRotY() - 10);
						// Log.i("Left X",cam.getX()+"");
						break;
					case 4:
						// setCamera(new Vector3(0.0, 0.0, 499.9), new
						// Vector3(0,0,0));
						// isLiveView = true;
						// cam.setX(0);
						// cam.setY(0);
						// cam.setZ(0);
						// isLiveView = true;
						cam.setRotY(cam.getRotY() + 10);
						// Log.i("Right X",cam.getX()+"");
						break;

					}
				}

				handler.postDelayed(new CameraUpdater(v), 100);
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

	class DoubleTapGestureDetector extends
			SimpleOnGestureListener {
		/*
		 * @Override public boolean onSingleTapUp(MotionEvent e) { // TODO
		 * Auto-generated method stub super.onSingleTapUp(e); /*((RoomRenderer)
		 * mRenderer).getObjectAt(e.getX(), e.getY()); Plane p = ((RoomRenderer)
		 * mRenderer).getPickedObject(); Log.i("Picked x", p.getX() + "");
		 * Log.i("Picked y", p.getY() + ""); Log.i("Picked z", p.getZ() + "");
		 * 
		 * 
		 * LoaderOBJ objParser = new LoaderOBJ(((RoomRenderer)
		 * mRenderer).context.getResources(), ((RoomRenderer)
		 * mRenderer).getTextureManager(), R.raw.light_switch); try {
		 * objParser.parse(); mObjectGroup = objParser.getParsedObject();
		 * ((RoomRenderer) mRenderer).addChild(mObjectGroup);
		 * 
		 * 
		 * } catch (ParsingException ex) { ex.printStackTrace(); }
		 * 
		 * return true; }
		 */
		/*
		 * @Override public boolean onSingleTapUp(MotionEvent e) { // TODO
		 * Auto-generated method stub Log.d("Single Tap Up",
		 * "Single Tap Up Detected ..."); return super.onSingleTapUp(e); }
		 */
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
								// Log.i("Wall name ", wallName);
								Intent i = new Intent(getApplicationContext(),
										PatternLibraryActivity.class);
								// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								i.putExtra("wall", wallName);

								startActivityForResult(i, GET_WALL_REQUEST);

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
				// The user picked a contact.
				// The Intent's data Uri identifies which contact was selected.

				// Do something with the contact here (bigger example below)
				String result = data.getCharSequenceExtra("Location")
						.toString();
				// Toast.makeText(getApplicationContext(), result,
				// Toast.LENGTH_SHORT).show();
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

			}
			((RoomRenderer) mRenderer).resetSelectedObject();
		}
	}

	void goback() {
		finish();
	}

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
			if (isObjectSelected) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					((RoomRenderer) mRenderer).get3DObjectAt(event.getX(),
							event.getY());
					obj = mRenderer.getPicked3D();
					if (obj != null) {
						obj.setShowBoundingVolume(true);
						obj.setPickingColor(Color.RED);
						obj.reload();

						Log.i("Not null", "");
					}
					/*
					 * if (obj != null) Log.i("Picked Object", obj.getName());
					 */
					break;
				case MotionEvent.ACTION_MOVE:
					/*
					 * if (obj != null) obj.setPosition(event.getX(),
					 * obj.getY(), event.getY());
					 */
					Log.i("Move", "");
					break;
				case MotionEvent.ACTION_UP:
					if (obj != null) {
						obj.setShowBoundingVolume(true);
						obj.setPickingColor(Color.RED);
						obj.reload();

						Log.i("Not null", "");
					}
					break;
				}
			}
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final Dialog d = new Dialog(Room3DActivity.this);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.delete_prompt);
		Button yButton = (Button) d.findViewById(R.id.but_delete);
		yButton.setText("Save & Exit");
		Button nButton = (Button) d.findViewById(R.id.but_cancel);
		TextView textView = (TextView) d.findViewById(R.id.delete_promt_text);
		textView.setText("Do you want to exit the 3d Room?");
		yButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		nButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				d.dismiss();
			}
		});
		d.show();
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
					+ "/SmartShowRoom/" + currentProject + "/";
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
				+ "/SmartShowRoom/" + currentProject + "/";
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
	// .getAbsolutePath() + "/SmartShowRoom/FreezeShots/";
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
		Intent in = new Intent(Room3DActivity.this, ViewSelector.class);
		startActivity(in);
	}

	public class FrontWall extends Thread {
		ScreenshotListener listener;

		public FrontWall(ScreenshotListener listener) {
			this.listener = listener;

		}

		ProgressDialog dialog1;
		Context context;
		String path1 = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/SmartShowRoom/FreezeShots/";

		public void run() {
			setCamera(new Vector3(0.0, 0.0, MAX_FB), new Vector3(0, 0.0, 0.0));
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/SmartShowRoom/FreezeShots/"
					+ GlobalVariables.getProjectName() + "-Front";
			// path1= Environment.getExternalStorageDirectory()
			// .getAbsolutePath()
			// + "/SmartShowRoom/FreezeShots/";

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
				.getAbsolutePath() + "/SmartShowRoom/FreezeShots/";

		public void run() {
			setCamera(new Vector3(0.0, 0.0, MAX_LR), new Vector3(0, -90.0, 0.0));
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/SmartShowRoom/FreezeShots/"
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
				.getAbsolutePath() + "/SmartShowRoom/FreezeShots/";

		public void run() {
			setCamera(new Vector3(0.0, 0.0, MAX_LR), new Vector3(0, 90.0, 0.0));
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/SmartShowRoom/FreezeShots/"
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
				.getAbsolutePath() + "/SmartShowRoom/FreezeShots/";

		public void run() {
			setCamera(new Vector3(0.0, 0.0, MAX_FB), new Vector3(0, 180.0, 0.0));
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/SmartShowRoom/FreezeShots/"
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

}
