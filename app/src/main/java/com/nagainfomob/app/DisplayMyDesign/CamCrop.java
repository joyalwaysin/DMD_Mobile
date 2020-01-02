package com.nagainfomob.app.DisplayMyDesign;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nagainfomob.app.R;
import com.nagainfomob.app.database.DatabaseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CamCrop extends Activity implements OnTouchListener {

	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	// directory name to store captured images and videos
	private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
	static File mediaFile;

	private Uri fileUri; // file url to store image/video

	// Static final constants
	private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
	private static final int ROTATE_NINETY_DEGREES = 90;
	private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
	private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
	private static final int ON_TOUCH = 1;

	// Instance variables
	private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
	private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

	Bitmap croppedImage;
	Bitmap bitmap;
	private File mediaStorageDir;
	static String clickName;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	// Saves the state upon rotating the screen/restarting the activity
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
		bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);

		bundle.putParcelable("file_uri", fileUri);

	}

	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type) {

		// External sdcard location

		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(),
				"SmartShowRoom/Cam Click");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());

		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ clickName + ".jpg");
			GlobalVariables.createNomediafile(mediaStorageDir.getPath()
					+ File.separator);
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	/*
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	private void captureImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = Uri.fromFile(getOutputMediaFile(1));

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if the result is capturing Image
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				previewCapturedImage();
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(),
						"User cancelled image capture", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else {
				// failed to capture image
				Toast.makeText(getApplicationContext(),
						"Sorry! Failed to capture image", Toast.LENGTH_SHORT)
						.show();
			}
		} else if (requestCode == 2 && resultCode == 2) {
			// String message=data.getStringExtra("MESSAGE");
			// textView1.setText(message);
//			Log.d("Test", "in");
			String path = data.getStringExtra("id");
//			Log.d("Test", path);

			// mediaFile = new File(mediaStorageDir.getPath() + File.separator
			// + clickName + ".jpg");
			previewSelectedImage(path);
		}
	}

	int scale = 1;

	private void previewSelectedImage(String path) {

		try {
			// hide video preview

			// bimatp factory

			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 3;
			try {
				bitmap = BitmapFactory.decodeFile(path, options);
			} catch (Exception e) {
				// TODO: handle exception
			}
			/*Typeface mFont = Typeface.createFromAsset(getAssets(),
					"Roboto-Thin.ttf");*/
			ViewGroup root = (ViewGroup) findViewById(R.id.mylayout);
//			setFont(root, mFont);

			Drawable drawable = new BitmapDrawable(getResources(), bitmap);

			// Initialize components of the app
			final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);

			if (bitmap.getHeight() <= 200 && bitmap.getHeight() > 50)
				scale = 4;

			else if (bitmap.getHeight() <= 50)
				scale = 8;

			else if (bitmap.getHeight() < 300 && bitmap.getHeight() > 200)
				scale = 2;

			else
				scale = 1;

			bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()
					* scale, bitmap.getHeight() * scale, false);
			cropImageView.setAspectRatio(5, 5);
			cropImageView.setImageBitmap(bitmap);
			cropImageView.setFixedAspectRatio(false);
//			cropImageView.setGuidelines(1);

			Button cropButton = (Button) findViewById(R.id.Button_crop);

			cropImageView.setFocusableInTouchMode(false);
			// cropImageView.setAspectRatio(20, 30);
			cropButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					croppedImage = cropImageView.getCroppedImage();
					croppedImage = Bitmap.createScaledBitmap(croppedImage,
							croppedImage.getWidth() / scale,
							croppedImage.getHeight() / scale, false);
					saveDialog(croppedImage, scale);
					// ImageView croppedImageView = (ImageView)
					// findViewById(R.id.croppedImageView);
					// croppedImageView.setImageBitmap(croppedImage);
				}
			});

		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	private void previewCapturedImage() {
		try {
			// hide video preview

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// downsizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 3;
			try {
				bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
			} catch (Exception e) {
				// TODO: handle exception
			}

			// imgPreview.setImageBitmap(bitmap);

			// Sets fonts for all
			/*Typeface mFont = Typeface.createFromAsset(getAssets(),
					"Roboto-Thin.ttf");*/
			ViewGroup root = (ViewGroup) findViewById(R.id.mylayout);
//			setFont(root, mFont);

			Drawable drawable = new BitmapDrawable(getResources(), bitmap);

			// Initialize components of the app
			final CropImageView cropImageView = findViewById(R.id.CropImageView);
			cropImageView.setImageBitmap(bitmap);
			cropImageView.setAspectRatio(5, 5);
			cropImageView.setFixedAspectRatio(false);
//			cropImageView.setGuidelines(1);

			final Button cropButton = (Button) findViewById(R.id.Button_crop);

			cropImageView.setFocusableInTouchMode(false);
			// cropImageView.setAspectRatio(20, 30);
			cropButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					croppedImage = cropImageView.getCroppedImage();
					saveDialog(croppedImage, 1);
					// ImageView croppedImageView = (ImageView)
					// findViewById(R.id.croppedImageView);
					// croppedImageView.setImageBitmap(croppedImage);
				}
			});

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	// Restores the state upon rotating the screen/restarting the activity
	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
		mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);

		fileUri = bundle.getParcelable("file_uri");
	}

	private boolean isDeviceSupportCamera() {
		if (getApplicationContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return true;
		}
	}

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main1);

		// Checking camera availability
		if (!isDeviceSupportCamera()) {
			Toast.makeText(getApplicationContext(),
					"Sorry! Your device doesn't support camera",
					Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		} else {
			nameTheClick();

		}
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	public void selectSource() {
		final Dialog selectDialog = new Dialog(CamCrop.this);
		selectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		selectDialog.setContentView(R.layout.select_cam_image);
		selectDialog.setCancelable(false);
		// selectDialog.getWindow().setLayout(500, 300);
		Button cambutton = (Button) selectDialog.findViewById(R.id.but_cam);
		Button sdcard = (Button) selectDialog.findViewById(R.id.but_sdcard);

		cambutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectDialog.dismiss();
				captureImage();
			}
		});

		sdcard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectDialog.dismiss();
				File mediaStorageDir = new File(Environment
						.getExternalStorageDirectory(),
						"SmartShowRoom/Cam Click");
				if (!mediaStorageDir.exists()) {
					mediaStorageDir.mkdirs();
				}

				mediaFile = new File(mediaStorageDir.getPath() + File.separator
						+ clickName + ".jpg");
				Intent intent = new Intent(getApplicationContext(),
						FileExplore.class);
				startActivityForResult(intent, 2);


			}
		});

		selectDialog.show();

	}

	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data)
	// {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// // check if the request code is same as what is passed here it is 2
	// if(requestCode==2)
	// {
	// // String message=data.getStringExtra("MESSAGE");
	// // textView1.setText(message);
	// String path=data.getStringExtra("id");
	// Log.d("TAG", path);
	// }
	//
	// }

	private void nameTheClick() {
		// TODO Auto-generated method stub
		Boolean ret_msg = false;

		final Dialog nDialog = new Dialog(CamCrop.this);
		nDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		nDialog.setCancelable(false);
		nDialog.setContentView(R.layout.camname_dialog);
		nDialog.getWindow().setLayout(500, 300);

		final EditText nEdit = (EditText) nDialog.findViewById(R.id.nEdit);
		Button nButton = (Button) nDialog.findViewById(R.id.nButton);

		nButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if ((nEdit.getText().toString()).length() > 0) {
					clickName = nEdit.getText().toString();
//					Log.d("TAG", clickName);
					nDialog.dismiss();
					selectSource();
					// captureImage();
				} else {
					Toast.makeText(getApplicationContext(),
							"Field can not be blank! ", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		nDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
								 KeyEvent event) {
				// TODO Auto-generated method stub
				if ((keyCode == KeyEvent.KEYCODE_BACK)) {
					nDialog.dismiss();
					finish();
//					return false;// I have tried here true also
				}
				return false;

			}
		});

		nDialog.show();


	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/*
	 * Sets the font on all TextViews in the ViewGroup. Searches recursively for
	 * all inner ViewGroups as well. Just add a check for any other views you
	 * want to set as well (EditText, etc.)
	 */
	public void setFont(ViewGroup group, Typeface font) {
		int count = group.getChildCount();
		View v;
		for (int i = 0; i < count; i++) {
			v = group.getChildAt(i);
			if (v instanceof TextView || v instanceof EditText
					|| v instanceof Button) {
				((TextView) v).setTypeface(font);
			} else if (v instanceof ViewGroup)
				setFont((ViewGroup) v, font);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void saveDialog(final Bitmap b, int scale) {
		final Dialog dialog = new Dialog(CamCrop.this);
		final DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.save_prompt);
		dialog.setCancelable(false);
		Button preview_Save, Preview_Cancel;
		preview_Save = (Button) dialog.findViewById(R.id.preview_Save);
		Preview_Cancel = (Button) dialog.findViewById(R.id.preview_Cancel);
		TextView textValues = (TextView) dialog.findViewById(R.id.textValues);
		ImageView savePreview = (ImageView) dialog
				.findViewById(R.id.cropedImage);
		savePreview.setImageBitmap(Bitmap.createScaledBitmap(b, b.getWidth()
				* scale, b.getHeight() * scale, false));
		int bHeight = b.getHeight();
		int bWidth = b.getWidth();
		textValues.setText("Croped Image height = " + bHeight + "mm width = "
				+ bWidth + "mm ");

		final TextView cHeight = (TextView) dialog.findViewById(R.id.tHieght);
		final TextView cWidth = (TextView) dialog.findViewById(R.id.tWidth);
		preview_Save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// db.insertRow(null, , null, null, null, null,);

				// Log.d("TAG", mediaFile.getName());
				db.insertRecord(mediaFile.getName().replace(".jpg", ""),
						"Custom", cHeight.getText().toString() + "x"
								+ cWidth.getText().toString(), "Custom",
						"Custom", "Custom", "Cam Click", "Custom", "", "", "", "", "", "", "");
				overWriteCropedFile(b);

				dialog.dismiss();
				finish();
			}
		});
		Preview_Cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteDir(mediaFile);
				dialog.dismiss();
			}
		});
		dialog.show();

	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	public void overWriteCropedFile(Bitmap b) {
		// FileOutputStream fOut;
		try {
			FileOutputStream fOut = new FileOutputStream(mediaFile);

			// FileOutputStream fOut = new FileOutputStream(new File(
			// Environment.getExternalStorageDirectory()
			// + "/SmartShowRoom/test.jpg"));
			b.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// try {
		// fOut = new FileOutputStream(mediaFile);
		// } catch (FileNotFoundException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

	}

	OnTouchListener cropTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);
				cropImageView.getCropRect();
				System.out.println("hai");
			}

			return false;
		}
	};

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
			final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);
			cropImageView.getCropRect();
			System.out.println("hai");
		}
		return false;
	}

	void ratio(int a, int b) {
		int min = Math.min(a, b);
		int max = Math.max(a, b);
		if (max % min == 0) {
			System.out.println("1" + " " + max / min);
			return;
		}
		for (int i = 2; i <= min; i++) {
			while (max % i == 0 && min % i == 0) {
				max /= i;
				min /= i;
			}
		}
		System.out.println(max + " " + min);
	}

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	public Action getIndexApiAction() {
		Thing object = new Thing.Builder()
				.setName("CamCrop Page") // TODO: Define a title for the content shown.
				// TODO: Make sure this auto-generated URL is correct.
				.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
				.build();
		return new Action.Builder(Action.TYPE_VIEW)
				.setObject(object)
				.setActionStatus(Action.STATUS_TYPE_COMPLETED)
				.build();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		AppIndex.AppIndexApi.start(client, getIndexApiAction());
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		AppIndex.AppIndexApi.end(client, getIndexApiAction());
		client.disconnect();
	}
}
