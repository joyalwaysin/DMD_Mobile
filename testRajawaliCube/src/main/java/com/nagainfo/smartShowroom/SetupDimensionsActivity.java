package com.nagainfo.smartShowroom;

import java.io.Serializable;

import rajawali.RajawaliActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SetupDimensionsActivity extends Activity implements
		OnTouchListener {
	private LinearLayout shapeContainer;
	private LinearLayout content_box;
	private EditText dimenB;
	private Context context;
	ImageView shapeImage;
	String shapeName;
	TextView dimCTextView, dimDTextView;
	EditText dimenC, dimenD;
	float maxLength = 0, maxWidth = 0, minLength = 0, minWidth = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setupdimensions);
		shapeImage = (ImageView) findViewById(R.id.imageWall);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		shapeContainer = (LinearLayout) findViewById(R.id.shapeContainer);
		// shapeContainer.addView(new myView(this, 100, 100));
		content_box = (LinearLayout) findViewById(R.id.content_box);
		dimCTextView = (TextView) findViewById(R.id.textViewDimesionC);
		dimDTextView = (TextView) findViewById(R.id.textViewDimesionD);
		dimenC = (EditText) findViewById(R.id.dimenC);
		dimenD = (EditText) findViewById(R.id.dimenD);

		context = this;
		String[] unitArray = getResources().getStringArray(
				R.array.unitSpinnerArray);
		content_box.setOnTouchListener(this);
		dimenB = (EditText) findViewById(R.id.dimenB);
		// dimenB.setOnFocusChangeListener(dimenBFocusChangedListener);
		Spinner unitSpinner = (Spinner) findViewById(R.id.unitSpinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, unitArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitSpinner.setAdapter(spinnerArrayAdapter);
		unitSpinner.setOnItemSelectedListener(unitSpinnerListener);
		shapeName = getIntent().getStringExtra("shapeName");
		if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
			shapeImage.setImageResource(R.drawable.custom_shape_1_dim);

		} else {
			dimCTextView.setVisibility(View.GONE);

			dimDTextView.setVisibility(View.GONE);

			TextView blankTextView1 = (TextView) findViewById(R.id.textViewblank1);
			blankTextView1.setVisibility(View.GONE);

			TextView blankTextView2 = (TextView) findViewById(R.id.textViewblank2);
			blankTextView2.setVisibility(View.GONE);

			TextView blankTextView3 = (TextView) findViewById(R.id.textViewblank3);
			// blankTextView3.setVisibility(View.GONE);

			dimenC.setVisibility(View.GONE);
			dimenD.setVisibility(View.GONE);
		}
	}

	float feetToMm(float value) {

		float mmValue = value * 304.8f;
		String mmValues = String.format("%1.2f", mmValue);
		mmValue = Float.parseFloat(mmValues);
		return mmValue;

	}

	float inchesToMm(float value) {
		float mmValue = value * 25.4f;
		String mmValues = String.format("%1.2f", mmValue);
		mmValue = Float.parseFloat(mmValues);
		return (float) mmValue;

	}

	float metersToMm(float value) {
		float mmValue = value * 1000;
		String mmValues = String.format("%1.2f", mmValue);
		mmValue = Float.parseFloat(mmValues);
		return (float) mmValue;
	}

	/*
	 * @Override protected void onRestart() { finish(); super.onRestart();
	 * this.recreate(); startActivity(getIntent()); finish();
	 * 
	 * }
	 */

	/*
	 * OnFocusChangeListener dimenBFocusChangedListener = new
	 * OnFocusChangeListener() {
	 * 
	 * @Override public void onFocusChange(View v, boolean hasFocus) { // TODO
	 * Auto-generated method stub try { int width =
	 * Integer.valueOf(dimenB.getText().toString());
	 * 
	 * //shapeContainer.addView(new myView(context, 100, width)); } catch
	 * (Exception e) { // TODO: handle exception Log.e("edit test value error!",
	 * e.getMessage()); }
	 * 
	 * } };
	 */

	OnItemSelectedListener unitSpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			String[] unitArray = getResources().getStringArray(
					R.array.unitSpinnerArray);
			GlobalVariables.setUnit(unitArray[arg2]);
			// System.out.println("hai");

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	public void nextPressed(View v) {
		final Intent intent;
		EditText dimenA = (EditText) findViewById(R.id.dimenA);
		EditText dimenB = (EditText) findViewById(R.id.dimenB);
		EditText wallHeight = (EditText) findViewById(R.id.wallHeight);
		String sA = null, sB = null, sH = null;
		if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
			intent = new Intent(getApplicationContext(), Room3DActivity2.class);
			sA = dimenA.getText().toString();
			sB = dimenB.getText().toString();
			sH = wallHeight.getText().toString();
		} else {
			intent = new Intent(getApplicationContext(), Room3DActivity.class);
			sA = dimenA.getText().toString();
			sB = dimenB.getText().toString();
			sH = wallHeight.getText().toString();
		}
		if (sA.length() > 0 && sB.length() > 0 && sH.length() > 0) {
			// if (Float.parseFloat(dimenA.getText().toString())
			// / Float.parseFloat(wallHeight.getText().toString()) <= 3
			// && Float.parseFloat(dimenB.getText().toString())
			// / Float.parseFloat(wallHeight.getText().toString()) <= 3
			// && Float.parseFloat(dimenA.getText().toString())
			// / Float.parseFloat(wallHeight.getText().toString()) >= 0.75
			// && Float.parseFloat(dimenB.getText().toString())
			// / Float.parseFloat(wallHeight.getText().toString()) >= 0.75
			// && Float.parseFloat(dimenA.getText().toString())
			// / Float.parseFloat(dimenB.getText().toString()) <= 2.5
			// && Float.parseFloat(dimenB.getText().toString())
			// / Float.parseFloat(dimenA.getText().toString()) <= 2.5) {
			if (dimenA.getText().toString().equalsIgnoreCase("")
					|| dimenB.getText().toString().equalsIgnoreCase("")
					|| wallHeight.getText().toString().equalsIgnoreCase("")) {
				if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
					if (dimenC.getText().toString().equalsIgnoreCase("")
							|| dimenD.getText().toString().equalsIgnoreCase("")) {
						Toast.makeText(this,
								"Please enter dimensions before proceeding!",
								Toast.LENGTH_SHORT).show();
						return;
					}

				}
				Toast.makeText(this,
						"Please enter dimensions before proceeding!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Feet")) {
				if (Float.valueOf(dimenA.getText().toString()) > 50) {

					Toast.makeText(this,
							"Please enter dimensions bellow 50 feet!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Inches")) {
				if (Float.valueOf(dimenA.getText().toString()) > 600) {

					Toast.makeText(this,
							"Please enter dimensions bellow 600 Inches!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Meters")) {
				if (Float.valueOf(dimenA.getText().toString()) > 15) {

					Toast.makeText(this,
							"Please enter dimensions bellow 15 Meters!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (dimenA.getText().toString().equalsIgnoreCase("")
					|| dimenB.getText().toString().equalsIgnoreCase("")
					|| wallHeight.getText().toString().equalsIgnoreCase("")) {

				Toast.makeText(this,
						"Please enter dimensions before proceeding!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Feet")) {
				if (Float.valueOf(dimenB.getText().toString()) > 50) {

					Toast.makeText(this,
							"Please enter dimensions bellow 50 feet!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Inches")) {
				if (Float.valueOf(dimenB.getText().toString()) > 600) {

					Toast.makeText(this,
							"Please enter dimensions bellow 600 Inches!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Meters")) {
				if (Float.valueOf(dimenB.getText().toString()) > 15) {

					Toast.makeText(this,
							"Please enter dimensions bellow 15 Meters!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if (dimenA.getText().toString().equalsIgnoreCase("")
					|| dimenB.getText().toString().equalsIgnoreCase("")
					|| wallHeight.getText().toString().equalsIgnoreCase("")) {

				Toast.makeText(this,
						"Please enter dimensions before proceeding!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Feet")) {
				if (Float.valueOf(wallHeight.getText().toString()) > 25) {

					Toast.makeText(this,
							"Please enter dimensions bellow 25 feet!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Inches")) {
				if (Float.valueOf(dimenA.getText().toString()) > 300) {

					Toast.makeText(this,
							"Please enter dimensions bellow 300 Inches!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (GlobalVariables.getUnit().equalsIgnoreCase("Meters")) {
				if (Float.valueOf(dimenA.getText().toString()) > 8) {

					Toast.makeText(this,
							"Please enter dimensions bellow 8 Meters!",
							Toast.LENGTH_SHORT).show();
					return;
				}
			}

			float dimA = (float) Float.parseFloat(dimenA.getText().toString());
			float dimB = (float) Float.parseFloat(dimenB.getText().toString());
			float dimC = 0, dimD = 0;
			if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
				dimC = (float) Float.parseFloat(dimenC.getText().toString());
				dimD = (float) Float.parseFloat(dimenD.getText().toString());

			}
			float wallH = (float) Float.parseFloat(wallHeight.getText()
					.toString());

			if (dimA == 0 || dimB == 0 || wallH == 0) {

				if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
					if (dimC == 0 || dimD == 0) {
						Toast.makeText(this,
								"Please enter dimensions before proceeding!",
								Toast.LENGTH_SHORT).show();
						return;
					}

				}
				Toast.makeText(this,
						"Please enter dimensions before proceeding!",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String unitString = GlobalVariables.getUnit();
			float wH = (float) (Float.parseFloat(wallHeight.getText()
					.toString()));
			float dA = (float) (Float.parseFloat(dimenA.getText().toString()));
			float dB = (float) (Float.parseFloat(dimenB.getText().toString()));
			float dC = 0, dD = 0;
			if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
				dC = (float) (Float.parseFloat(dimenC.getText().toString()));
				dD = (float) (Float.parseFloat(dimenD.getText().toString()));

			}
			if (unitString.equalsIgnoreCase("feet")) {

				GlobalVariables.setWallDim(GlobalVariables.feetToMm(wH),
						GlobalVariables.feetToMm(dA),
						GlobalVariables.feetToMm(dB),
						GlobalVariables.feetToMm(dC),
						GlobalVariables.feetToMm(dD));
			} else if (unitString.equalsIgnoreCase("inches")) {
				GlobalVariables.setWallDim(GlobalVariables.inchesToMm(wH),
						GlobalVariables.inchesToMm(dA),
						GlobalVariables.inchesToMm(dB),
						GlobalVariables.feetToMm(dC),
						GlobalVariables.feetToMm(dD));
			} else {
				GlobalVariables.setWallDim(GlobalVariables.metersToMm(wH),
						GlobalVariables.metersToMm(dA),
						GlobalVariables.metersToMm(dB),
						GlobalVariables.feetToMm(dC),
						GlobalVariables.feetToMm(dD));
			}

			intent.putExtra("dimenA", dimenA.getText().toString());
			intent.putExtra("dimenB", dimenB.getText().toString());
			if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
				intent.putExtra("dimenC", dimenC.getText().toString());
				intent.putExtra("dimenD", dimenD.getText().toString());

			}
			intent.putExtra("shapeName", shapeName);
			intent.putExtra("wallHeight", wallHeight.getText().toString());
			try {
				if (Float.parseFloat(dimenA.getText().toString())
						/ Float.parseFloat(wallHeight.getText().toString()) <= 3
						&& Float.parseFloat(dimenB.getText().toString())
								/ Float.parseFloat(wallHeight.getText()
										.toString()) <= 3
						&& Float.parseFloat(dimenA.getText().toString())
								/ Float.parseFloat(wallHeight.getText()
										.toString()) >= 0.6
						&& Float.parseFloat(dimenB.getText().toString())
								/ Float.parseFloat(wallHeight.getText()
										.toString()) >= 0.6
						&& Float.parseFloat(dimenA.getText().toString())
								/ Float.parseFloat(dimenB.getText().toString()) <= 2
						&& Float.parseFloat(dimenB.getText().toString())
								/ Float.parseFloat(dimenA.getText().toString()) <= 2) {
					String projectName = shapeName + "_"
							+ GlobalVariables.getProjectName();
					GlobalVariables.setProjectName(projectName);
					GlobalVariables.saveProject(getApplicationContext());
					intent.putExtra("OBJECTS", "yes");
					startActivity(intent);
					finish();
				}

				else {
					float a = Float.parseFloat(dimenA.getText().toString());
					float b = Float.parseFloat(dimenB.getText().toString());
					float h = Float.parseFloat(wallHeight.getText().toString());
					maxLength = 3 * h;
					maxWidth = 3 * h;
					minLength = (float) (0.6 * h);
					minWidth = (float) (0.6 * h);
					AlertDialog.Builder builder1 = new AlertDialog.Builder(
							context);
					builder1.setMessage(
							Html.fromHtml("With the Current Dimensions, Place Objects Functionality cannot function properly due to View Limitations. For the current Height, maximum value of A and B can be "+"<b>" + maxLength  
							+ " "
							+ GlobalVariables.getUnit() + "</b>"
							+ " each, and minimum value of A and B can be "
							+"<b>" + minLength
							+ " "
							+ GlobalVariables.getUnit()+ "</b>"+ " each.")
							);
//					Log.d("message", msg)
					builder1.setCancelable(true);
					

					builder1.setPositiveButton(
							"Continue with Current Dimensions",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									String projectName = shapeName + "_"
											+ GlobalVariables.getProjectName();
									GlobalVariables.setProjectName(projectName);
									GlobalVariables
											.saveProject(getApplicationContext());
									intent.putExtra("OBJECTS", "no");
									startActivity(intent);
									finish();
									dialog.dismiss();
								}
							});
					builder1.setNegativeButton("Change Dimensions",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

									dialog.cancel();
								}
							});

					AlertDialog alert11 = builder1.create();
					alert11.setCanceledOnTouchOutside(false);
					alert11.show();
				}

			} catch (Exception e) {
				Log.e("Error", e.getMessage());
			}
		}
		// else {
		// if (shapeName.equalsIgnoreCase("Custom Shape 1")) {
		//
		// } else {
		// // Toast.makeText(
		// // getApplicationContext(),
		// // "Please enter Dimensions in the ratio between 1:1:1 and 3:3:1",
		// // Toast.LENGTH_LONG).show();
		//
		// AlertDialog.Builder builder1 = new AlertDialog.Builder(
		// context);
		// builder1.setMessage("Enter the Dimensions in the ratio DimenA : DimenB : WallHeight between 0.75:0.75:1 and 3:3:1");
		// builder1.setCancelable(true);
		//
		// builder1.setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog,
		// int id) {
		// dialog.cancel();
		// }
		// });
		//
		// AlertDialog alert11 = builder1.create();
		// alert11.show();
		// }
		//
		// }
		// }
		else {
			Toast.makeText(getApplicationContext(),
					"Enter Dimensions to Continue..", Toast.LENGTH_LONG).show();
		}
	}

	public void homePressed(View v) {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	private class myView extends View {

		private float dWidth;
		private float dHeight;

		public myView(Context context, float sHeight, float sWidth) {
			super(context);
			dWidth = sWidth;
			dHeight = sHeight;

		}

		@Override
		protected void onDraw(Canvas canvas) {

			Paint myPaint = new Paint();
			myPaint.setColor(Color.BLACK);
			myPaint.setStyle(Paint.Style.STROKE);
			myPaint.setStrokeWidth(3);

			canvas.drawRect(10, 10, dWidth, dHeight, myPaint);
			this.invalidate();
		}

		protected void reDraw() {
			this.invalidate();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			EditText dimenA = (EditText) findViewById(R.id.dimenA);
			EditText dimenB = (EditText) findViewById(R.id.dimenB);
			EditText wallHeight = (EditText) findViewById(R.id.wallHeight);
			imm.hideSoftInputFromWindow(dimenA.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(dimenB.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(wallHeight.getWindowToken(), 0);
			return true;
		}
		return false;
	}
}