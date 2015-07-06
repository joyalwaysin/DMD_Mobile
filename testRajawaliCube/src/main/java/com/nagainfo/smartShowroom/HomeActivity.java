package com.nagainfo.smartShowroom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nagainfo.camera.CamCrop;
import com.nagainfo.database.DatabaseHandler;
import com.nagainfo.help.ActivityHelp;
import com.nagainfo.registration.GetDeviceId;
import com.nagainfo.registration.SessionManager;

public class HomeActivity extends Activity {
	Dialog activateDialog;
	DatabaseHandler db = new DatabaseHandler(this);
	Button camButton;

	private Uri mImageCaptureUri;
	Dialog infoDialog;
	ImageView thumbnail;
	private static AlertDialog dialog;

	private Activity activity;
	Context context;

	public static final String DEVELOPER_KEY = "AIzaSyAXiUzmdfFqUEolECMH60bx1_P0jIdtXKI";
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private String outputX;
	private String outputY;
	private String aspectX;
	private String aspecty;
	public static final String CROP_VERSION_SELECTED_KEY = "crop";

	public static final int VERSION_1 = 1;
	public static final int VERSION_2 = 2;

	Dialog d;
	private File file;
	AssetManager asmngr;
	static ArrayList<String> f = new ArrayList<String>();
	static ArrayList<String> g = new ArrayList<String>();


	
	int keyCount = 0;
	
	
	LinearLayout force;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		activity = this;
		context = getApplicationContext();
		ActivateApp.deligate = this;
		
		force = (LinearLayout)findViewById(R.id.click_to_activate);
		
		force.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if (!isNetworkAvailable(activity)) {

//					dialogActivate(1);
					Toast.makeText(getApplicationContext(), "Unable to Check for New Updates, Internet connectivity is required", Toast.LENGTH_LONG).show();
				} else {
//					dialogActivate(0);

					new ActivateApp(context)
							.execute(GlobalVariables.activateUrl
									+ GetDeviceId.getDeviceid(activity));
					Toast.makeText(getApplicationContext(), "Checking for New Updates", Toast.LENGTH_LONG).show();

				}
				return true;
			}
		});
		
		

		if (!checkAppActivated()) {
			try {
				if (!isNetworkAvailable(activity)) {

					dialogActivate(1);
				} else {
					dialogActivate(0);

					new ActivateApp(context)
							.execute(GlobalVariables.activateUrl
									+ GetDeviceId.getDeviceid(activity));

				}
			} catch (Exception e) {
				Log.e("ActivatedialogException", e.getMessage());
			}
		}

		// WindowManager wm = (WindowManager)
		// context.getSystemService(Context.WINDOW_SERVICE);
		// Display display = wm.getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// int a=display.getWidth();
		// int b=display.getWidth();
		// Log.d("TAG", a+"x"+b);
		// int height = size.y;
		// int width = size.x;
		// Toast.makeText(getApplicationContext(), "Size :" + height +"x" +
		// width, 10000).show();

		captureImageInitialization();
		// getSHA1();
		camButton = (Button) findViewById(R.id.camClickButton);
		Button buttOffer = (Button) findViewById(R.id.latestOffersButton);
		Button aboutSaifee = (Button) findViewById(R.id.aboutSaifeeButton);
		Button viewPatternsButton = (Button) findViewById(R.id.viewPatternsButton);
		Button bHelp = (Button) findViewById(R.id.browseLibraryButton);
		Button newProject = (Button) findViewById(R.id.newProjectButton);
		initInfoDialog();
		// String uniqueID =
		// GlobalVariables.getUniqueId(getApplicationContext());

		if (db.getCount() == 0) {
			/*
			 * Toast.makeText(this, "", Toast.LENGTH_LONG).show();
			 */
		}

		newProject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				newProjectClicked(v);
			}
		});

		viewPatternsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(),
						ViewPatternActivity.class);
				startActivity(i);
			}
		});
		bHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(checkHelpActivated()){
					Intent intent = new Intent(getApplicationContext(),
							ActivityHelp.class);
					startActivity(intent);
				} else{

					if(isNetworkAvailable(activity)){
						new ActivateApp(context)
								.execute(GlobalVariables.activateUrl
										+ GetDeviceId.getDeviceid(activity));
					}
				}



			}
		});

		camButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// getCropVale(v);
				// dialog.show();
				if (checkCamClickActivated()) {

					Intent i = new Intent(getApplicationContext(),
							CamCrop.class);
					startActivity(i);
				} else {
					showMessageDialog(GlobalVariables.featureLockedMessage);
					if(isNetworkAvailable(activity)){
						new ActivateApp(context)
						.execute(GlobalVariables.activateUrl
								+ GetDeviceId.getDeviceid(activity));
					}
				}
			}
		});

		aboutSaifee.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				infoDialog.show();

			}
		});

		buttOffer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * Intent i = new Intent(getApplicationContext(),
				 * ActivityOffers.class); startActivity(i);
				 */
				Toast.makeText(getApplicationContext(), "Coming Soon!",
						Toast.LENGTH_SHORT).show();

			}
		});

		new Runnable() {
			public void run() {
				getFromAssets();
			}
		}.run();

	}
	
	
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
//			if(keyCount==0){
//				timer.start();
//			}
//			
//			keyCount++;
//			
//			if(keyCount==3){
//				keyCount = 0;
//				if (!isNetworkAvailable(activity)) {
//
////					dialogActivate(1);
//					Toast.makeText(getApplicationContext(), "Please check your Network Connectivity", 1).show();
//				} else {
////					dialogActivate(0);
//
//					new ActivateApp(context)
//							.execute(GlobalVariables.activateUrl
//									+ GetDeviceId.getDeviceid(activity));
//					Toast.makeText(getApplicationContext(), "Forcing Activation Check", 1).show();
//
//				}
//			}
//			
//			
//		}
//		return true;
//	}
	
	
	
//	@Override
//	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode == KeyEvent.KEYCODE_VOLUME_UP && repeatCount==2){
//			new ActivateApp(context)
//			.execute(GlobalVariables.activateUrl
//					+ GetDeviceId.getDeviceid(activity));
//			Toast.makeText(getApplicationContext(), "Forcing Activation Check", 1).show();
//		}
//		return true;
//	}
	
//	@Override
//	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
//			new ActivateApp(context)
//			.execute(GlobalVariables.activateUrl
//					+ GetDeviceId.getDeviceid(activity));
//			Toast.makeText(getApplicationContext(), "Volume Up", 1).show();
//		}
//		return false;
//	}
	
	public void showMessageDialog(String message) {
		final Dialog d = new Dialog(HomeActivity.this);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.saved_location);
		TextView tv = (TextView) d.findViewById(R.id.save_loc);
		Button ok = (Button) d.findViewById(R.id.ok_button);
		tv.setText(message);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				d.dismiss();
			}
		});
		d.show();
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

	public void loadProject(View v) {
		Intent i = new Intent(getApplicationContext(), LoadProject.class);
		startActivity(i);
	}

	public void newProjectClicked(View v) {
		// final Dialog projectTypeDialog = new Dialog(HomeActivity.this);
		// projectTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// projectTypeDialog.setContentView(R.layout.project_type_select);
		// final Button threeD = (Button) projectTypeDialog
		// .findViewById(R.id.three_d_project);
		// final Button ambience = (Button) projectTypeDialog
		// .findViewById(R.id.ambience);
		//HashMap<String,ArrayList<String>> map;
		// final Dialog selectViewDialog = new Dialog(HomeActivity.this);
		// selectViewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// selectViewDialog.setContentView(R.layout.ambience_select_view);
		// final Button drawingRoom = (Button) selectViewDialog
		// .findViewById(R.id.drawingRoom);
		// final Button kitchen = (Button) selectViewDialog
		// .findViewById(R.id.kitchen);
		// final Button bedroom = (Button) selectViewDialog
		// .findViewById(R.id.bedroom);
		// threeD.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (projectTypeDialog.isShowing()) {
		// projectTypeDialog.dismiss();
		// }
		// Intent i = new Intent(getApplicationContext(),
		// NewProjectActivity.class);
		// startActivity(i);
		// }
		// });
		//
		// ambience.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// if (projectTypeDialog.isShowing()) {
		// projectTypeDialog.dismiss();
		// }
		//
		// selectViewDialog.show();
		//
		// // Intent intent = new
		// // Intent(getApplicationContext(),Ambience.class);
		// // startActivity(intent);
		//
		// }
		// });
		//

		// projectTypeDialog.show();

		Intent intent = new Intent(HomeActivity.this, ProjectTypeSelect.class);
		startActivity(intent);

	}

	public void notImplemented(View v) {
		Toast.makeText(getApplicationContext(),
				"Functionality not implemented", Toast.LENGTH_LONG).show();
	}

	public void browseWeb(View v) {
		// Intent i = new Intent(getApplicationContext(),
		// com.naga.update.ActivityUpdate.class);
		Intent i = new Intent(getApplicationContext(),
				com.nagainfo.update.ActivityCountry.class);

		startActivity(i);

	}

	private void captureImageInitialization() {
		/**
		 * a selector dialog to display two image source options, from camera
		 * �Take from camera� and from existing files �Select from gallery�
		 */
		final String[] items = new String[] { "Take from camera" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					/**
					 * To take a photo from camera, pass intent action
					 * �MediaStore.ACTION_IMAGE_CAPTURE� to open the camera app.
					 */
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					/**
					 * Also specify the Uri to save the image on specified path
					 * and file name. Note that this Uri variable also used by
					 * gallery app to hold the selected image path.
					 */

					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "SmartPattern_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg"));

					/*
					 * mImageCaptureUri = Uri.fromFile(new File(GlobalVariables
					 * .getPatternRootPath() + "Cam Click/" +
					 * String.valueOf(System.currentTimeMillis()) + ".jpg"));
					 */

					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					// pick from file
					/**
					 * To select an image from existing files, use
					 * Intent.createChooser to open image chooser. Android will
					 * automatically display a list of supported applications,
					 * such as image gallery or file manager.
					 */
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				}
			}
		});

		dialog = builder.create();
	}

	public class CropOptionAdapter extends ArrayAdapter<CropOption> {
		private ArrayList<CropOption> mOptions;
		private LayoutInflater mInflater;

		public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
			super(context, R.layout.crop_selector, options);

			mOptions = options;

			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup group) {
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.crop_selector, null);

			CropOption item = mOptions.get(position);

			if (item != null) {
				((ImageView) convertView.findViewById(R.id.iv_icon))
						.setImageDrawable(item.icon);
				((TextView) convertView.findViewById(R.id.tv_name))
						.setText(item.title);

				return convertView;
			}

			return null;
		}
	}

	public class CropOption {
		public CharSequence title;
		public Drawable icon;
		public Intent appIntent;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			/**
			 * After taking a picture, do the crop
			 */
			// doCrop();
			testcrop();
			// getCropVale();

			break;

		case PICK_FROM_FILE:
			/**
			 * After selecting image from files, save the selected path
			 */
			mImageCaptureUri = data.getData();

			doCrop();

			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			/**
			 * After cropping the image, get the bitmap of the cropped image and
			 * display it on imageview.
			 */
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");

				String file_path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/SmartShowRoom/Cam Click/";
				File dir = new File(file_path);
				if (!dir.exists())
					dir.mkdirs();
				file = new File(dir, "SmartPattern"
						+ String.valueOf(System.currentTimeMillis()) + ".jpg");
				DatabaseHandler db = new DatabaseHandler(
						getApplicationContext());
				db.insertRecord(
						"SmartPattern"
								+ String.valueOf(System.currentTimeMillis()),
						"", "", "", "", "", "Cam Click", "");
				FileOutputStream fOut = null;
				try {
					fOut = new FileOutputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				photo.compress(Bitmap.CompressFormat.PNG, 85, fOut);
				try {
					fOut.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					fOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * try { mImageView.setImageBitmap(photo); } catch (Exception e)
				 * { Log.e("setImageBitmap error!", e.getMessage()); }
				 */
				Toast.makeText(this, "Capture pattern success",
						Toast.LENGTH_LONG).show();
			}

			File f = new File(mImageCaptureUri.getPath());
			/**
			 * Delete the temporary image
			 */
			if (f.exists())
				f.delete();

			break;

		}
	}

	void initInfoDialog() {

		infoDialog = new Dialog(HomeActivity.this);
		infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		infoDialog.setContentView(R.layout.about_saifee);

		TextView app_info = (TextView) infoDialog.findViewById(R.id.app_info);
		app_info.setText("Device Id : "
				+ GetDeviceId.getDeviceid(HomeActivity.this));

	}

	private void testcrop() {

		/*
		 * Intent i = new Intent(getApplicationContext(), CropActivity.class);
		 * i.putExtra(CROP_VERSION_SELECTED_KEY, VERSION_1);
		 * i.setData(mImageCaptureUri); startActivity(i);
		 */
	}

	private void doCrop() {
		d.cancel();
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
		/**
		 * Open image crop app by starting an intent
		 * �com.android.camera.action.CROP�.
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		/**
		 * Check if there is image cropper app installed.
		 */
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		/**
		 * If there is no image cropper app, display warning message
		 */
		if (size == 0) {

			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			/**
			 * Specify the image path, crop dimension and scale
			 */
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", outputX);
			intent.putExtra("outputY", outputY);
			intent.putExtra("aspectX", aspectX);
			intent.putExtra("aspectY", aspecty);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			/**
			 * There is posibility when more than one image cropper app exist,
			 * so we have to check for it first. If there is only one app, open
			 * then app.
			 */

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				/**
				 * If there are several app exist, create a custom chooser to
				 * let user selects the app.
				 */
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	public void getCropVale(View V) {
		d = new Dialog(HomeActivity.this);
		d.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		d.setContentView(R.layout.crop_values);
		Button cr_but = (Button) d.findViewById(R.id.cropValue_buttom);
		final EditText h_value = (EditText) d.findViewById(R.id.HeightValue);
		final EditText w_value = (EditText) d.findViewById(R.id.widthValue);
		cr_but.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (h_value.getText().length() > 0

				&& w_value.getText().length() > 0) {

					outputX = w_value.getText().toString();
					outputY = h_value.getText().toString();
					String ratio = getRatio(Double.parseDouble(outputX),
							Double.parseDouble(outputY));
					String[] separated = ratio.split(":");
					aspectX = separated[0]; // this will contain "Fruit"
					aspecty = separated[1];
					dialog.show();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please specify the values", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		d.show();

	}

	public String getRatio(Double a, Double b) {
		Double aRatio;
		Double bRatio;
		String aRat = null;
		String bRat = null;
		if (a < b) {
			aRatio = a / b;
			aRat = String.valueOf(aRatio).toString();
		}
		if (b < a) {
			aRatio = b / a;
			aRat = String.valueOf(aRatio).toString();
		}
		if (a < b) {
			bRatio = b / a;
			bRat = String.valueOf(bRatio).toString();
		}
		if (b < a) {
			bRatio = a / b;
			bRat = String.valueOf(bRatio).toString();
		}
		String val = aRat + ":" + bRat;
		return val;
	}

	public Boolean checkAppActivated() {
		Boolean status = null;
		SimpleDateFormat sdf = new SimpleDateFormat("DDD-yyyy");
		String currentDateandTime = sdf.format(new Date());
		SessionManager session = new SessionManager(getApplicationContext());
		status = session.isActivated(currentDateandTime);
		// return false;
		return status;
	}

	public Boolean checkCamClickActivated() {
		Boolean status = null;
		SessionManager session = new SessionManager(getApplicationContext());
		status = session.isCamClickActivated();

		return status;
	}

	public Boolean checkHelpActivated(){
		Boolean status = null;
		SessionManager session = new SessionManager(getApplicationContext());
		status = session.isHelpActivated();

		return status;

	}

	@SuppressLint("NewApi")
	public void dialogActivate(int i) {
		activateDialog = new Dialog(HomeActivity.this);
		activateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// dialog.setCancelable(false);

		activateDialog.setCanceledOnTouchOutside(false);
		activateDialog.setContentView(R.layout.activate_dialog);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		// LinearLayout.LayoutParams param = new
		// LinearLayout.LayoutParams(width, height);
		TextView link, device_id = null;
		try {
			link = (TextView) activateDialog.findViewById(R.id.textView2);
			link.setMovementMethod(LinkMovementMethod.getInstance());
			device_id = (TextView) activateDialog.findViewById(R.id.device_id);
			if (i == 0) {
				device_id.setText(GetDeviceId.getDeviceid(
						getApplicationContext()).toString());
			} else if (i == 1) {
				device_id.setText("Network not available to activate! ");
			}
			device_id.setTextSize(35);
			// Log.i("anshad", 1 + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

		activateDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				activateDialog.dismiss();
				activity.finish();
				return false;

			}
		});

		activateDialog.getWindow().setLayout(width, height);
		activateDialog.show();
	}

//	private void stripUnderlines(TextView textView) {
//
//		Spannable s = new SpannableString(textView.getText());
//		URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
//		for (URLSpan span : spans) {
//			int start = s.getSpanStart(span);
//			int end = s.getSpanEnd(span);
//			s.removeSpan(span);
//			span = new URLSpanNoUnderline(span.getURL());
//			s.setSpan(span, start, end, 0);
//		}
//		textView.setText(s);
//	}

	public class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);
		}
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					Log.i("Class", info[i].getState().toString());
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						
//						if()

						return true;

					}
				}
			}
		}
		return false;
	}

//	/*
//	 * public void activateApp() { int responseCode = 0; try { DefaultHttpClient
//	 * httpClient = new DefaultHttpClient(); HttpGet httpGet = new HttpGet(
//	 * "http://crossrangetalk.com/api/appStatus?appId=08606e34251d");
//	 *
//	 * HttpResponse httpResponse = httpClient.execute(httpGet); responseCode =
//	 * httpResponse.getStatusLine().getStatusCode();
//	 *
//	 * } catch (Exception e) { Log.e("response code error", e.getMessage());
//	 *
//	 * } SessionManager session = new SessionManager(getApplicationContext());
//	 * if (responseCode == 200) { session.createActivateSession("true"); } //
//	 * return responseCode; }
//	 */N

	public static class ActivateApp extends AsyncTask<String, String, String> {

		public static HomeActivity deligate = null;
		Context context;

		public ActivateApp(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... urls) {

			String responseCode = null;
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					responseCode = String.valueOf(execute.getStatusLine()
							.getStatusCode());
					
					InputStream in = null;
					try {
						in = execute.getEntity().getContent();
					} catch (IllegalStateException e) {
						// callback.onConnctionFailed();
						e.printStackTrace();
						return null;

					} catch (IOException e) {
						e.printStackTrace();
						// callback.onConnctionFailed();
						return null;
					} // Get the data in the entity
					String s = convertStreamToString(in);
					xmlParse(s);


					/*
					 * InputStream content = execute.getEntity().getContent();
					 * 
					 * BufferedReader buffer = new BufferedReader( new
					 * InputStreamReader(content)); String s = ""; while ((s =
					 * buffer.readLine()) != null) { response += s; }
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return responseCode;
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(result!=null){
				deligate.setAppActivated(result);
				GlobalVariables.setActChecked();
			}
			
			// if (dialog.isShowing()) {
			// dialog.dismiss();
			// }
		}

		// @Override
		// protected void onPreExecute() {
		// // TODO Auto-generated method stub
		// super.onPreExecute();
		// ProgressDialog dialog=new ProgressDialog(context);
		// dialog.setMessage("Connecting to Server...");
		// dialog.show();
		// }

	}
	
	private static void xmlParse(String response){



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
			xpp.setInput(new StringReader(response));
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

		String setting = null;
		int value = 0;
		String readValue = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_DOCUMENT) {
				System.out.println("Start document");
			} else if (eventType == XmlPullParser.START_TAG) {
				name = xpp.getName();
				if (name == "data") {

				}
			} else if (eventType == XmlPullParser.END_TAG) {
				name = xpp.getName();
				if (name.equalsIgnoreCase("setting")) {
					setting = readValue;
				}
				if (name.equalsIgnoreCase("value")) {
					value = Integer.parseInt(readValue);
				}

				if (name.equalsIgnoreCase("data")) {
					GlobalVariables.setSettingActivated(setting,value);
				}
			} else if (eventType == XmlPullParser.TEXT) {
				// userData.add(xpp.getText());
				readValue = xpp.getText();
//				Log.i("value", xpp.getText());
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
	}



	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		Log.i("stream", sb.toString());
		return sb.toString();
	}
	// @Override
	// protected void onPreExecute() {
	// // TODO Auto-generated method stub
	// super.onPreExecute();
	// ProgressDialog dialog=new ProgressDialog(context);
	// dialog.setMessage("Connecting to Server...");
	// dialog.show();
	// }



	public void setAppActivated(String result) {
		// TODO Auto-generated method stub
		int mStatusCode = Integer.valueOf(result);
		SimpleDateFormat sdf = new SimpleDateFormat("DDD-yyyy");
		String currentDateandTime = sdf.format(new Date());

		if (mStatusCode == 200) {
			SessionManager sManager = new SessionManager(
					getApplicationContext());
			sManager.createActivateSession(true, currentDateandTime);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(activateDialog!=null ){
				if(activateDialog.isShowing())
					activateDialog.dismiss();
			}
			
			

		} else  {
			SessionManager sManager = new SessionManager(
					getApplicationContext());
			sManager.createActivateSession(false, currentDateandTime);
			if(activateDialog==null){
//				if(!activateDialog.isShowing())
					dialogActivate(0);
			}
			// dialogActivate(0);
		}

	}
}
