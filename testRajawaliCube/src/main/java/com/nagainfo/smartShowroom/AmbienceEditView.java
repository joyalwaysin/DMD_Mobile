package com.nagainfomob.smartShowroom;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Random;

import org.xmlpull.v1.XmlSerializer;

import com.nagainfomob.database.DatabaseHandler;
import com.nagainfomob.smartShowroom.DrawView.LayoutDimensions;

import android.R.drawable;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;

public class AmbienceEditView extends View implements OnTouchListener {

	Paint paint = new Paint();
	Point startPoint, endPoint, lastEndPoint, moveStart;
	Float sx, sy, ex, ey;
	Bitmap lastCanvas;
	Boolean autoFillHori = false;
	Boolean autoFillVerti = true;
	Context context;
	RelativeLayout rel;
	LinearLayout touchedView;
	Boolean isTileSelected = false, isMoved = false;
	Bitmap selectedTile;
	Boolean isDrawingSelected = false;
	String wall;
	int leftMoveStart, topMoveStart;
	String selectedFilePath;
	Boolean isTilePatterning = false;
	String unit;
	Float length, width;
	EditText leftText;
	EditText topText;
	EditText widthText;
	EditText heightText;
	EditText rotText;
	EditText layRotText;
	String origUnit;
	int axis = 0;
	int viewArea;// = GlobalVariables.getDrawArea(getContext());
	float tileW, tileH;
	float screenWidthRatio;

	public AmbienceEditView(Context context, float screenWidthRatio) {
		super(context);
		this.context = context;
		this.setOnTouchListener(this);
		this.viewArea = (int) (GlobalVariables.getDrawArea(getContext()));
		startPoint = endPoint = lastEndPoint = new Point();

		isTileSelected = false;
		isDrawingSelected = false;
		this.wall = "front";
		this.screenWidthRatio = screenWidthRatio;

		origUnit = "Feet";
		// this.setBackgroundResource(R.color.transparent_full);

	}

	void setUnit(String unit) {
		origUnit = unit;
		this.unit = unit;
	}

	void setAxis(int axis) {
		this.axis = axis;
	}

	void setDimensions(String unit, Float length, Float width) {
		this.unit = unit;
		this.length = length;
		this.width = width;
		GlobalVariables.setWallDim(length, 0, width, 0, 0);

	}

	public void setMode(Boolean mode) {
		isTilePatterning = mode;
	}

	public void selectTile(Boolean select) {
		isTileSelected = select;
	}

	public void isDrawingSelected(Boolean select) {

		isDrawingSelected = select;
	}

	public void setSelectedTile(Bitmap tile, String filePath, int orientation,
			String tileSize) {
		if (tileSize != null) {
			String[] tileDim = tileSize.split("x");
			if (tileDim.length == 0) {
				tileDim = tileSize.split("X");
			}
			tileH = Float.valueOf(tileDim[0]);
			tileW = Float.valueOf(tileDim[1]);
		} else {
			tileH = 0;
			tileW = 0;
		}
		selectedTile = tile;
		selectedFilePath = filePath;
		this.axis = orientation;
	}

	@Override
	public void onDraw(Canvas canvas) {
		Log.i("Width dv", this.getWidth() + "");
		Log.i("Height dv", this.getHeight() + "");
		/*
		 * paint.setColor(Color.BLACK); paint.setStrokeWidth(3);
		 * canvas.drawRect(30, 30, 80, 80, paint); paint.setStrokeWidth(0);
		 * paint.setColor(Color.CYAN); canvas.drawRect(33, 60, 77, 77, paint );
		 * paint.setColor(Color.YELLOW); canvas.drawRect(33, 33, 77, 60, paint
		 * );
		 */

		// paint.setColor(Color.BLUE);

		paint.setColor(Color.rgb(0, 0, 0));

		paint.setStrokeWidth(2);
		paint.setStyle(Style.STROKE);

		float startX = 0, startY = 0, endX = 0, endY = 0;
		/*
		 * startX=startPoint.x<endPoint.x?startPoint.x:endPoint.x;
		 * endX=startPoint.x>endPoint.x?startPoint.x:endPoint.x;
		 * 
		 * startY=startPoint.y<endPoint.y?startPoint.y:endPoint.y;
		 * endY=startPoint.y>endPoint.y?startPoint.y:endPoint.y;
		 */
		startX = startPoint.x;
		endX = endPoint.x;

		startY = startPoint.y;
		endY = endPoint.y;

		/*
		 * if(startPoint.x>lastEndPoint.x) { startX=lastEndPoint.x; startY=0;
		 * endX=endPoint.x-(Math.abs(lastEndPoint.x-startX));
		 * endY=endPoint.y-(startPoint.y); }
		 */
		// if()
		/*
		 * if(lastCanvas!=null) canvas.drawBitmap(lastCanvas, 0, 0, paint);
		 */
		if (isTileSelected || isDrawingSelected) {
			canvas.drawRect(startX, startY, endX, endY, paint);
		}
		if (!isTileSelected && !isDrawingSelected) {
			canvas.drawRect(0, 0, 0, 0, paint);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!isDrawingSelected) {
			Toast.makeText(context, "Please select the drawing tool to draw !",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		// TODO Auto-generated method stub
		// Toast.makeText(context, "Touched", Toast.LENGTH_SHORT).show();
		int biasX = 0, biasY = 0;
		if (touchedView != null) {
			LayoutDimensions dim = (LayoutDimensions) touchedView.getTag();
			biasX = dim.x;
			biasY = dim.y;
			touchedView = null;
		}
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			startPoint = new Point((int) event.getX() + biasX,
					(int) event.getY() + biasY);
			sx = event.getX() + biasX;
			sy = event.getY() + biasY;
			break;
		case MotionEvent.ACTION_MOVE:
			endPoint = new Point((int) event.getX() + biasX, (int) event.getY()
					+ biasY);
			ex = event.getX() + biasX;
			ey = event.getY() + biasY;
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			endPoint = new Point((int) event.getX() + biasX, (int) event.getY()
					+ biasY);
			ex = event.getX() + biasX;
			ey = event.getY() + biasY;
			int endX = (int) event.getX() + biasX,
			endY = (int) event.getY() + biasY;
			if (endPoint.x > getX() + getWidth()) {
				endX = (int) getX() + getWidth();

			}
			if (endPoint.y > getY() + getHeight()) {
				endY = (int) getY() + getHeight();
			}
			endPoint = new Point(endX, endY);
			lastEndPoint = endPoint;
			lastCanvas = saveSignature();
			if (startPoint.x < endPoint.x && startPoint.y < endPoint.y) {
				addLayout();

			}
			// this.bringToFront();
			// startPoint=endPoint;
			break;
		}

		return true;
	}

	public void addLayout() {
		rel = (RelativeLayout) this.getParent();
		{

			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			Random randomService = new Random();
			StringBuilder sb = new StringBuilder();
			while (sb.length() < 6) {
				sb.append(Integer.toHexString(randomService.nextInt()));
			}
			sb.setLength(6);
			String s = "#" + sb.toString();
			linearLayout.setBackgroundColor(Color.parseColor(s));
			// linearLayout.setBackgroundColor(Color.YELLOW);
			// int ax=Math.abs(startPoint.x- endPoint.x);
			// int ay=Math.abs(startPoint.y - endPoint.y);

			LayoutParams param = new LayoutParams(Math.abs(startPoint.x
					- endPoint.x), Math.abs(startPoint.y - endPoint.y));
			linearLayout.setLayoutParams(param);
			linearLayout.setClickable(false);
			linearLayout.setOnTouchListener(layoutTouched);

			/*
			 * ImageView img = new ImageView(context);
			 * img.setBackgroundColor(Color.CYAN);
			 */
			LayoutParams params = new LayoutParams(
					Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y
							- endPoint.y));
			// params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
			params.leftMargin = startPoint.x;
			params.topMargin = startPoint.y;
			LayoutDimensions dim = new LayoutDimensions();
			dim.x = startPoint.x;
			dim.y = startPoint.y;
			dim.width = Math.abs(startPoint.x - endPoint.x);
			dim.height = Math.abs(startPoint.y - endPoint.y);
			dim.rot = 0;
			dim.layRot = 0;

			// Log.d("TAGGG", ""+dim.width+" x "+dim.height);

			linearLayout.setTag(dim);
			rel.setClickable(true);
			rel.addView(linearLayout, params);
			linearLayout.bringToFront();
			if (!isTilePatterning) {
				// if (unit != null) {
				// origUnit = unit;
				// // GlobalVariables.setUnit(unit);
				// }
				showDialog(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
						linearLayout, dim.rot,dim.layRot,null);
			} else {
				// showTileDialog(startPoint.x, startPoint.y, endPoint.x,
				// endPoint.y, linearLayout,dim.rot);
			}
		}
	}

	OnTouchListener layoutTouched = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			rel = (RelativeLayout) AmbienceEditView.this.getParent();
			switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if (!isTileSelected && !isDrawingSelected) {
					int currentX = startPoint.x - (int) event.getRawX();
					currentX *= -1;

					int currentY = startPoint.y - (int) event.getRawY();
					currentY *= -1;
					LayoutParams params = (LayoutParams) v
							.getLayoutParams();

					Rect rect = new Rect();
					getHitRect(rect);
					// Rect r=new
					// Rect(leftMoveStart+currentX,topMoveStart+currentY,leftMoveStart+currentX+params.width,topMoveStart+currentY+params.height);

					if (rect.contains(leftMoveStart + currentX + v.getWidth(),
							topMoveStart + currentY + v.getHeight())
							&& leftMoveStart + currentX >= 0
							&& topMoveStart + currentY >= 0) {
						params.leftMargin = leftMoveStart + currentX;
						params.topMargin = topMoveStart + currentY;
						v.setLayoutParams(params);
						LayoutDimensions dim = (LayoutDimensions) v.getTag();
						dim.x = Math.round(leftMoveStart + currentX);
						dim.y = Math.round(topMoveStart + currentY);
						dim.width = Math.round(v.getWidth());
						dim.height = Math.round(v.getHeight());

						v.setTag(dim);

						// invalidate();
					}
					// startPoint=new
					// Point((int)event.getX(),(int)event.getY());

				}
				break;
			case MotionEvent.ACTION_DOWN:
				if (!isTileSelected && isDrawingSelected) {
					touchedView = (LinearLayout) v;
					AmbienceEditView.this.onTouch(v, event);

				} else if (!isTileSelected && !isDrawingSelected) {
					startPoint = new Point((int) event.getRawX(),
							(int) event.getRawY());
					LayoutParams params = (LayoutParams) v
							.getLayoutParams();
					leftMoveStart = params.leftMargin;
					topMoveStart = params.topMargin;
					invalidate();
				}
				break;

			// TODO Auto-generated method stub
			case MotionEvent.ACTION_UP: {
				if (!isTileSelected && isDrawingSelected) {
					touchedView = (LinearLayout) v;
					AmbienceEditView.this.onTouch(v, event);

				}

				else {
					final LinearLayout layout = (LinearLayout) v;
					if (isTileSelected) {
						/*
						 * Bitmap bm =
						 * BitmapFactory.decodeResource(getResources(),
						 * R.drawable.wall_tile_sample);
						 */

						Bitmap bm = selectedTile;
						final LayoutDimensions dim = (LayoutDimensions) layout
								.getTag();
						// BitmapFactory.decodeFile("/sdcard/test2.png");
						if (!isTilePatterning) {
							fillTileInLayout(layout, true, bm,
									GlobalVariables.getGrooveStatus(), dim.rot,selectedFilePath);
						} else {
							fillCustomTileInLayout(layout, true, bm);
						}

					} else {
						if (Math.abs(startPoint.x - (int) event.getRawX()) > 5
								&& Math.abs(startPoint.y
										- (int) event.getRawY()) > 5) {
							LayoutDimensions dim = (LayoutDimensions) v
									.getTag();

							startPoint = new Point(dim.x, dim.y);
							endPoint = new Point(dim.x + dim.width, dim.y
									+ dim.height);

							return true;
						}
						origUnit = unit;
						GlobalVariables.setUnit(unit);
						final Dialog dialog = new Dialog(context);
						final LayoutDimensions dim = (LayoutDimensions) layout
								.getTag();
						// dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
						dialog.setTitle("Options");
						dialog.setContentView(R.layout.choose_activity);
						Button closeButton = (Button) dialog
								.findViewById(R.id.close_button);
						Button deleteButton = (Button) dialog
								.findViewById(R.id.delete_button);
						Button editButton = (Button) dialog
								.findViewById(R.id.edit_button);

						closeButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
						deleteButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								rel.removeView(layout);
								dialog.dismiss();
								startPoint = new Point();
								endPoint = new Point();
								invalidate();
							}
						});
						editButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (!isTilePatterning) {
									showDialog(dim.x, dim.y, dim.x + dim.width,
											dim.y + dim.height, layout, dim.rot,dim.layRot, dim.selectedTile);
								} else {
									showTileDialog(dim.x, dim.y, dim.x
											+ dim.width, dim.y + dim.height,
											layout, dim.rot);
								}
								dialog.dismiss();
							}
						});
						startPoint = new Point(dim.x, dim.y);
						endPoint = new Point(dim.x + dim.width, dim.y
								+ dim.height);
						invalidate();
						dialog.show();
					}
					// TODO Auto-generated method stub

				}
			}
				break;
			}
			return true;

		}
	};

	OnItemSelectedListener unitSpinnerListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			String[] unitArray = getResources().getStringArray(
					R.array.unitSpinnerArray);

			if (unitArray[arg2].equalsIgnoreCase("Feet")) {
				// leftString = leftString / 304.8f;
				// topString = topString / 304.8f;
				// widthString = widthString / 304.8f;
				// heightString = heightString / 304.8f;
				double currentValue;
				if (unit.equalsIgnoreCase("Meters")) {
					String test = leftText.getText().toString();
					currentValue = Double.parseDouble(leftText.getText()
							.toString());
					double newValue = currentValue * 3.28084f;
					leftText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(topText.getText()
							.toString());
					newValue = currentValue * 3.28084f;
					topText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(widthText.getText()
							.toString());
					newValue = currentValue * 3.28084f;
					widthText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(heightText.getText()
							.toString());
					newValue = currentValue * 3.28084f;
					heightText.setText(String.format("%1.2f", newValue) + "");
					length *= 3.28084f;
					width *= 3.28084f;

				} else if (unit.equalsIgnoreCase("Inches")) {
					currentValue = Double.parseDouble(leftText.getText()
							.toString());
					double newValue = currentValue * 0.0833333;
					leftText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(topText.getText()
							.toString());
					newValue = currentValue * 0.0833333;
					topText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(widthText.getText()
							.toString());
					newValue = currentValue * 0.0833333;
					widthText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(heightText.getText()
							.toString());
					newValue = currentValue * 0.0833333;
					heightText.setText(String.format("%1.2f", newValue) + "");
					length *= 0.0833333f;
					width *= 0.0833333f;
				}

			} else if (unitArray[arg2].equalsIgnoreCase("Meters")) {
				// leftString = leftString / 1000;
				// topString = topString / 1000;
				// widthString = widthString / 1000;
				// heightString = heightString / 1000;

				double currentValue;
				if (unit.equalsIgnoreCase("Feet")) {
					String test = leftText.getText().toString();
					currentValue = Double.parseDouble(leftText.getText()
							.toString());
					double newValue = currentValue * 0.3048;
					leftText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(topText.getText()
							.toString());
					newValue = currentValue * 0.3048;
					topText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(widthText.getText()
							.toString());
					newValue = currentValue * 0.3048;
					widthText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(heightText.getText()
							.toString());
					newValue = currentValue * 0.3048;
					heightText.setText(String.format("%1.2f", newValue) + "");
					length *= 0.3048f;
					width *= 0.3048f;

				} else if (unit.equalsIgnoreCase("Inches")) {
					currentValue = Double.parseDouble(leftText.getText()
							.toString());
					double newValue = currentValue * 0.0254;
					leftText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(topText.getText()
							.toString());
					newValue = currentValue * 0.0254;
					topText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(widthText.getText()
							.toString());
					newValue = currentValue * 0.0254;
					widthText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(heightText.getText()
							.toString());
					newValue = currentValue * 0.0254;
					heightText.setText(String.format("%1.2f", newValue) + "");
					length *= 0.0254f;
					width *= 0.0254f;
				}
			} else {
				// leftString = (leftString / 25.4f);
				// topString = (topString / 25.4f);
				// widthString = (widthString / 25.4f);
				// heightString = (heightString / 25.4f);

				double currentValue;
				if (unit.equalsIgnoreCase("Feet")) {
					currentValue = Double.parseDouble(leftText.getText()
							.toString());
					float newValue = (float) (currentValue * 12);
					leftText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(topText.getText()
							.toString());
					newValue = (float) (currentValue * 12);
					topText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(widthText.getText()
							.toString());
					newValue = (float) (currentValue * 12);
					widthText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(heightText.getText()
							.toString());
					newValue = (float) currentValue * 12;
					heightText.setText(String.format("%1.2f", newValue) + "");
					length *= 12f;
					width *= 12f;

				} else if (unit.equalsIgnoreCase("Meters")) {
					currentValue = Double.parseDouble(leftText.getText()
							.toString());
					float newValue = (float) ((float) currentValue * 39.3701);
					leftText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(topText.getText()
							.toString());
					newValue = (float) (currentValue * 39.3701);
					topText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(widthText.getText()
							.toString());
					newValue = (float) (currentValue * 39.3701);
					widthText.setText(String.format("%1.2f", newValue) + "");

					currentValue = Double.parseDouble(heightText.getText()
							.toString());
					newValue = (float) (currentValue * 39.3701);
					heightText.setText(String.format("%1.2f", newValue) + "");
					length *= 39.3701f;
					width *= 39.3701f;
				}
			}

			unit = unitArray[arg2];
			setUnitText(unit);
			GlobalVariables.setUnit(unit);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	void setUnitText(String unitText) {
		unitText1.setText(unitText);
		unitText2.setText(unitText);
		unitText3.setText(unitText);
		unitText4.setText(unitText);
	}

	TextView unitText1, unitText2, unitText3, unitText4;

	void showTileDialog(int left, int top, int right, int bottom,
			final LinearLayout layout, float rot) {
		final Dialog dialog = new Dialog(context);
		rel = (RelativeLayout) AmbienceEditView.this.getParent();
		// dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		dialog.setTitle("Set Dimensions");
		dialog.setContentView(R.layout.set_dimensions_dialog);
		dialog.getWindow().setBackgroundDrawableResource(
				R.color.translucent_white);

		leftText = (EditText) dialog.findViewById(R.id.xposition);
		topText = (EditText) dialog.findViewById(R.id.yposition);
		widthText = (EditText) dialog.findViewById(R.id.width);
		heightText = (EditText) dialog.findViewById(R.id.height);
		rotText = (EditText) dialog.findViewById(R.id.rotation);


		unitText1 = (TextView) dialog.findViewById(R.id.unit1);
		unitText2 = (TextView) dialog.findViewById(R.id.unit2);
		unitText3 = (TextView) dialog.findViewById(R.id.unit3);
		unitText4 = (TextView) dialog.findViewById(R.id.unit4);

		String[] unitArray = { "millimeters" };

		Spinner unitSpinner;
		unitSpinner = (Spinner) dialog.findViewById(R.id.DimDialog_unitSpinner);
		unitSpinner.setClickable(false);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, unitArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitSpinner.setAdapter(spinnerArrayAdapter);
		// unitSpinner.setOnItemSelectedListener(unitSpinnerListener);
		int position = 0;
		/*
		 * for (int i = 0; i < unitArray.length; i++) { if
		 * (unitArray[i].equalsIgnoreCase(unit)) { position = i; break;
		 * 
		 * } }
		 */
		unitSpinner.setSelection(position);

		float leftString = 0, topString = 0, widthString = 0, heightString = 0;

		final float wallWidth = Math.abs(width);

		final float wallLength = Math.abs(length);

		float ratio = width / length;
		float rlength, rwidth;
		if (ratio > 1) {
			ratio = 1 / ratio;
			// rwidth = (ratio * viewArea);
			rlength = (ratio * viewArea);
			rwidth = viewArea;
		} else {
			rwidth = (ratio * viewArea);
			rlength = viewArea;
		}
		final float screenWidth = rwidth;
		final float screenHeight = rlength;

		leftString = (left * ((float) (wallWidth / screenWidth)));
		topString = (top * ((float) (wallLength / screenHeight)));
		widthString = (Math.abs(left - right) * ((float) (wallWidth / screenWidth)));
		heightString = Math
				.round((Math.abs(top - bottom) * ((float) (wallLength / screenHeight))));
		/*
		 * if (unit.equalsIgnoreCase("Feet")) { // leftString = leftString /
		 * 304.8f; // topString = topString / 304.8f; // widthString =
		 * widthString / 304.8f; // heightString = heightString / 304.8f;
		 * 
		 * unitText1.setText("Feet"); unitText2.setText("Feet");
		 * unitText3.setText("Feet"); unitText4.setText("Feet"); } else if
		 * (unit.equalsIgnoreCase("Meters")) { // leftString = leftString /
		 * 1000; // topString = topString / 1000; // widthString = widthString /
		 * 1000; // heightString = heightString / 1000;
		 * 
		 * unitText1.setText("Meters"); unitText2.setText("Meters");
		 * unitText3.setText("Meters"); unitText4.setText("Meters"); } else { //
		 * leftString = (leftString / 25.4f); // topString = (topString /
		 * 25.4f); // widthString = (widthString / 25.4f); // heightString =
		 * (heightString / 25.4f);
		 * 
		 * unitText1.setText("Inches"); unitText2.setText("Inches");
		 * unitText3.setText("Inches"); unitText4.setText("Inches"); }
		 */
		unitText1.setText("mm");
		unitText2.setText("mm");
		unitText3.setText("mm");
		unitText4.setText("mm");

		leftText.setText(leftString + "");
		topText.setText(topString + "");
		widthText.setText(widthString + "");
		heightText.setText(heightString + "");



		Button applyButton = (Button) dialog.findViewById(R.id.apply_button);
		applyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Apply layout params

				float l, t, w, h, r, layRot;
				String ls, ts, ws, hs, rs, layRots;
				String lss, tss, wss, hss, rss, layRotss;
				lss = leftText.getText().toString();
				tss = topText.getText().toString();
				wss = widthText.getText().toString();
				hss = heightText.getText().toString();
				rss = heightText.getText().toString();

				if (lss.length() > 0 && tss.length() > 0 && wss.length() > 0
						&& hss.length() > 0 && rss.length() > 0 ) {
					l = Float.parseFloat(leftText.getText().toString());

					t = Float.parseFloat(topText.getText().toString());

					w = Float.parseFloat(widthText.getText().toString());

					h = Float.parseFloat(heightText.getText().toString());

					r = Float.parseFloat(rotText.getText().toString());



					float ratio = width / length;
					float rlength, rwidth;
					if (ratio > 1) {
						ratio = 1 / ratio;
						// rwidth = (ratio * viewArea);
						rlength = (ratio * viewArea);
						rwidth = viewArea;
					} else {
						rwidth = (ratio * viewArea);
						rlength = viewArea;
					}

					final float screenWidth = rwidth;
					final float screenHeight = rlength;

					l = (float) ((screenWidth / (float) width) * l);
					ls = String.format("%1.2f", l);
					l = Float.parseFloat(ls);
					t = (float) ((screenHeight / (float) length) * t);
					ts = String.format("%1.2f", t);
					t = Float.parseFloat(ts);
					w = (float) ((screenWidth / (float) width) * w);
					ws = String.format("%1.2f", w);
					w = Float.parseFloat(ws);
					h = (float) ((screenHeight / (float) length) * h);
					hs = String.format("%1.2f", h);
					h = Float.parseFloat(hs);
					rs = String.format("%1.2f", r);
					r = Float.parseFloat(rs);



					Log.i("Length", length + "");
					Log.i("width", width + "");

					if (l + w > screenWidth || t + h > screenHeight) {
						Toast.makeText(getContext(),
								"Specified dimensions are not valid",
								Toast.LENGTH_SHORT).show();
						return;
					}
					int position = 0;
					for (int i = 0; i < rel.getChildCount(); i++) {
						if (rel.getChildAt(i).equals(layout)) {
							position = i;
						}
					}
					rel.removeView(layout);
					/*
					 * if (unit.equalsIgnoreCase("Feet")) { l = Math.round(l *
					 * 304.8f); t = Math.round(t * 304.8f); w = Math.round(w *
					 * 304.8f); h = Math.round(h * 304.8f);
					 * 
					 * } else if (unit.equalsIgnoreCase("Meters")) { l =
					 * Math.round(l * 1000); t = Math.round(t * 1000); w =
					 * Math.round(w * 1000); h = Math.round(h * 1000);
					 * 
					 * } else { l = Math.round(l * 25.4f); t = Math.round(t *
					 * 25.4f); w = Math.round(w * 25.4f); h = Math.round(h *
					 * 25.4f);
					 * 
					 * }
					 */
					LayoutParams param = new LayoutParams(Math.round(w), Math
							.round(h));
					layout.setLayoutParams(param);

					LayoutParams params = new LayoutParams(
							Math.round(w), Math.round(h));
					// params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
					params.leftMargin = (int) l;
					params.topMargin = (int) t;
					LayoutDimensions dim = (LayoutDimensions) layout.getTag();
					dim.x = Math.round(l);
					dim.y = Math.round(t);
					dim.width = Math.round(w);
					dim.height = Math.round(h);
					layout.setTag(dim);

					layout.setOnTouchListener(layoutTouched);
					rel.addView(layout, position, params);
					startPoint = new Point(Math.round(l), Math.round(t));
					endPoint = new Point(Math.round(l + w), Math.round(t + h));
					invalidate();
				} else {
					Toast.makeText(context, "Fields Cannot be left Blank..",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		Button dismissButton = (Button) dialog
				.findViewById(R.id.dismiss_button);
		dismissButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});
		dialog.show();

	}

	void showDialog(int left, int top, int right, int bottom,
			final LinearLayout layout, float rot, float layoutRotation, String tilePath) {
		final Dialog dialog = new Dialog(context);

		// dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		dialog.setTitle("Set Dimensions");
		dialog.setContentView(R.layout.set_dimensions_dialog);
		dialog.getWindow().setBackgroundDrawableResource(
				R.color.translucent_white);

		leftText = (EditText) dialog.findViewById(R.id.xposition);
		topText = (EditText) dialog.findViewById(R.id.yposition);
		widthText = (EditText) dialog.findViewById(R.id.width);
		heightText = (EditText) dialog.findViewById(R.id.height);
		rotText = (EditText) dialog.findViewById(R.id.rotation);
		layRotText = (EditText)dialog.findViewById(R.id.layout_rotation);

		unitText1 = (TextView) dialog.findViewById(R.id.unit1);
		unitText2 = (TextView) dialog.findViewById(R.id.unit2);
		unitText3 = (TextView) dialog.findViewById(R.id.unit3);
		unitText4 = (TextView) dialog.findViewById(R.id.unit4);
		float leftString = 0, topString = 0, widthString = 0, heightString = 0;
		String leftStrings, topStrings, widthStrings, heightStrings;

		// unit = GlobalVariables.getUnit();

		String[] unitArray = getResources().getStringArray(
				R.array.unitSpinnerArray);

		Spinner unitSpinner;
		unitSpinner = (Spinner) dialog.findViewById(R.id.DimDialog_unitSpinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_spinner_item, unitArray);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitSpinner.setAdapter(spinnerArrayAdapter);
		unitSpinner.setOnItemSelectedListener(unitSpinnerListener);
		int position = 0;
		for (int i = 0; i < unitArray.length; i++) {
			if (unitArray[i].equalsIgnoreCase(unit)) {
				position = i;
				break;

			}
		}
		unitSpinner.setSelection(position);
		// int wallWidth = 0;
		// int wallHeight = 0;
		// int wallLength = 0;

		final float wallWidth = width;
		final float wallHeight = length;
		// final float wallLength = GlobalVariables.getWallLength();
		// final float wallC = GlobalVariables.getWallC();
		// final float wallD = GlobalVariables.getWallD();

		String currentProject = "Ambience";

		if (wall.equalsIgnoreCase("front")) {

			// if (wall.equalsIgnoreCase("front")) {
			// if (!currentProject.startsWith("Rectangle")) {
			// width = wallC;
			// } else {
			width = wallWidth;
		}
		// } else {
		// width = wallWidth;
		// }

		length = wallHeight;

		float ratio = width / length;
		float rlength, rwidth;
		if (ratio > 1) {
			ratio = 1 / ratio;
			// rwidth = (ratio * viewArea);
			rlength = (ratio * viewArea);
			rwidth = viewArea;
		} else {
			rwidth = (ratio * viewArea);
			rlength = viewArea;
		}

		final float screenWidth = rwidth;
		final float screenHeight = rlength;

		leftString = (left * ((float) (width / (screenWidth * screenWidthRatio))));
		topString = top
				* (((float) (length / (screenHeight * screenWidthRatio))));
		widthString = (Math.abs(left - right) * ((float) (width / (screenWidth * screenWidthRatio))));
		heightString = (Math.abs(top - bottom) * ((float) (length / (screenHeight * screenWidthRatio))));
		/*
		 * leftText.setText(); topText.setText(); widthText.setText();
		 * heightText.setText();
		 */

		// }
		// else if (wall.equalsIgnoreCase("left")
		// || wall.equalsIgnoreCase("right")) {
		//
		// if (wall.equalsIgnoreCase("left")) {
		// if (!currentProject.startsWith("Rectangle")) {
		// width = wallD;
		// } else {
		// width = wallLength;
		// }
		// } else {
		// width = wallLength;
		// }
		//
		// // width = wallHeight;
		// length = wallHeight;
		// float ratio = width / length;
		// float rlength, rwidth;
		// if (ratio > 1) {
		// ratio = 1 / ratio;
		// // rwidth = (ratio * viewArea);
		// rlength = (ratio * viewArea);
		// rwidth = viewArea;
		// } else {
		// rwidth = (ratio * viewArea);
		// rlength = viewArea;
		// }
		//
		// final float screenWidth = rwidth;
		// final float screenHeight = rlength;
		//
		// leftString = (left * ((float) (width / screenWidth)));
		// topString = (top * ((float) (length / screenHeight)));
		// widthString = (Math.abs(left - right) * ((float) (width /
		// screenWidth)));
		// heightString = (Math.abs(top - bottom) * ((float) (length /
		// screenHeight)));
		//
		// /*
		// * leftText.setText(); topText.setText(); widthText .setText();
		// * heightText .setText();
		// */
		//
		// } else if (wall.equalsIgnoreCase("frontleft")) {
		//
		// float fullBack = wallWidth;
		// float fullFront = wallC;
		//
		// float fullRight = wallHeight;
		// float fullLeft = wallD;
		//
		// float remainFront = fullBack - fullFront;
		// float remainLeft = fullRight - fullLeft;
		//
		// double sumOfPow = Math.pow(remainFront, 2)
		// + Math.pow(remainLeft, 2);
		// width = (float) Math.sqrt(sumOfPow);
		//
		// // width = wallHeight;
		// length = wallHeight;
		//
		// float ratio = width / length;
		// float rlength, rwidth;
		// if (ratio > 1) {
		// ratio = 1 / ratio;
		// // rwidth = (ratio * viewArea);
		// rlength = (ratio * viewArea);
		// rwidth = viewArea;
		// } else {
		// rwidth = (ratio * viewArea);
		// rlength = viewArea;
		// }
		//
		// final float screenWidth = rwidth;
		// final float screenHeight = rlength;
		//
		// leftString = (left * ((float) (length / screenWidth)));
		// topString = (top * ((float) (width / screenHeight)));
		// widthString = (Math.abs(left - right) * ((float) (width /
		// screenWidth)));
		// heightString = (Math.abs(top - bottom) * ((float) (length /
		// screenHeight)));
		//
		// /*
		// * leftText.setText(); topText.setText(); widthText .setText();
		// * heightText .setText();
		// */
		//
		// } else /*
		// * (wall.equalsIgnoreCase("top") ||
		// * wall.equalsIgnoreCase("bottom"))
		// */{
		//
		// width = wallWidth;
		// length = wallLength;
		//
		// float ratio = width / length;
		// float rlength, rwidth;
		// if (ratio > 1) {
		// ratio = 1 / ratio;
		// // rwidth = (ratio * viewArea);
		// rlength = (ratio * viewArea);
		// rwidth = viewArea;
		// } else {
		// rwidth = (ratio * viewArea);
		// rlength = viewArea;
		// }
		//
		// final float screenWidth = rwidth;
		// final float screenHeight = rlength;
		//
		// leftString = (left * ((float) wallWidth) / screenWidth);
		// topString = (top * ((float) wallLength) / screenHeight);
		// widthString = (Math.abs(left - right) * ((float) wallWidth) /
		// screenWidth);
		// heightString = (Math.abs(top - bottom) * ((float) wallLength) /
		// screenHeight);
		//
		// /*
		// * leftText.setText(); topText.setText(); widthText.setText();
		// * heightText.setText();
		// */
		// }

		// String testUnit = "Feet";
		// if (GlobalVariables.getUnit().equalsIgnoreCase("Feet")) {
		// width/=304.8f;
		// length/=304.8f;

		leftString = leftString / 304.8f;
		leftStrings = String.format("%1.2f", leftString);
		topString = topString / 304.8f;
		topStrings = String.format("%1.2f", topString);
		widthString = widthString / 304.8f;
		widthStrings = String.format("%1.2f", widthString);
		heightString = heightString / 304.8f;
		heightStrings = String.format("%1.2f", heightString);

		unitText1.setText("Feet");
		unitText2.setText("Feet");
		unitText3.setText("Feet");
		unitText4.setText("Feet");
		// } else if (GlobalVariables.getUnit().equalsIgnoreCase("Meters")) {
		//
		// // width/=1000;
		// // length/=1000;
		//
		// leftString = leftString / 1000;
		// leftStrings=String.format("%1.2f", leftString);
		// topString = topString / 1000;
		// topStrings=String.format("%1.2f", topString);
		// widthString = widthString / 1000;
		// widthStrings= String.format("%1.2f", widthString);
		// heightString = heightString / 1000;
		// heightStrings= String.format("%1.2f", heightString);
		//
		// unitText1.setText("Meters");
		// unitText2.setText("Meters");
		// unitText3.setText("Meters");
		// unitText4.setText("Meters");
		// } else {
		//
		// // width/=25.4f;
		// // length/=25.4f;
		//
		// leftString = (leftString / 25.4f);
		// leftStrings=String.format("%1.2f", leftString);
		// topString = (topString / 25.4f);
		// topStrings=String.format("%1.2f", topString);
		// widthString = (widthString / 25.4f);
		// widthStrings= String.format("%1.2f", widthString);
		// heightString = (heightString / 25.4f);
		// heightStrings= String.format("%1.2f", heightString);
		//
		// unitText1.setText("Inches");
		// unitText2.setText("Inches");
		// unitText3.setText("Inches");
		// unitText4.setText("Inches");
		// }

		leftText.setText(leftStrings + "");
		topText.setText(topStrings + "");
		widthText.setText(widthStrings + "");
		heightText.setText(heightStrings + "");
		rotText.setText(rot + "");
		layRotText.setText(layoutRotation+"");

		Button applyButton = (Button) dialog.findViewById(R.id.apply_button);
		applyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Apply layout params
				rel = (RelativeLayout) AmbienceEditView.this.getParent();

				float l, t, w, h, r, layRot;
				String ls, ts, ws, hs, rs, layRots;
				String lss, tss, wss, hss, rss, layRotss;
				lss = leftText.getText().toString();
				tss = topText.getText().toString();
				wss = widthText.getText().toString();
				hss = heightText.getText().toString();
				rss = rotText.getText().toString();
				layRotss = layRotText.getText().toString();

				if (lss.length() > 0 && tss.length() > 0 && wss.length() > 0
						&& hss.length() > 0 && rss.length() > 0 && layRotss.length()>0) {
					l = Float.parseFloat(leftText.getText().toString());

					t = Float.parseFloat(topText.getText().toString());

					w = Float.parseFloat(widthText.getText().toString());

					h = Float.parseFloat(heightText.getText().toString());

					r = Float.parseFloat(rotText.getText().toString());

					layRot = Float.parseFloat(layRotss);

					float ratio = width / length;
					float rlength, rwidth;
					if (ratio > 1) {
						ratio = 1 / ratio;
						// rwidth = (ratio * viewArea);
						rlength = (ratio * viewArea * screenWidthRatio);
						rwidth = viewArea * screenWidthRatio;
					} else {
						rwidth = (ratio * viewArea * screenWidthRatio);
						rlength = viewArea * screenWidthRatio;
					}

					final float screenWidth = Math.round(rwidth);
					final float screenHeight = Math.round(rlength);

					// if (wall.equalsIgnoreCase("front")
					// ) {

					float tmp;
					tmp = (screenWidth / (float) width) * l;
					l = tmp;
					t = (screenHeight / (float) length * t);
					w = (screenWidth / (float) width) * w;
					h = (screenHeight / (float) length) * h;
					if (l + w > screenWidth || t + h > screenHeight) {
						Toast.makeText(getContext(),
								"Specified dimensions are not valid",
								Toast.LENGTH_SHORT).show();
						return;
					}

					// } else if (wall.equalsIgnoreCase("left")
					// || wall.equalsIgnoreCase("right")) {
					//
					// l = (screenWidth / (float) width) * l;
					// t = (screenHeight / (float) length * t);
					// w = (screenWidth / (float) width) * w;
					// h = (screenHeight / (float) length) * h;
					// if (l + w > screenWidth || t + h > screenHeight) {
					// Toast.makeText(getContext(),
					// "Specified dimensions are not valid",
					// Toast.LENGTH_SHORT).show();
					// return;
					// }
					//
					// } else if (wall.equalsIgnoreCase("top")
					// || wall.equalsIgnoreCase("bottom")) {
					//
					// l = (screenWidth / (float) width) * l;
					// t = (screenHeight / (float) length * t);
					// w = (screenWidth / (float) width) * w;
					// h = (screenHeight / (float) length) * h;
					// if (l + w > screenWidth || t + h > screenHeight) {
					// Toast.makeText(getContext(),
					// "Specified dimensions are not valid",
					// Toast.LENGTH_SHORT).show();
					// return;
					// }
					// }

					// if (origUnit.equalsIgnoreCase("Feet")) {
					l = Math.round(l * 304.8f);
					ls = String.format("%.2f", l);
					l = Float.parseFloat(ls);
					t = Math.round(t * 304.8f);
					ts = String.format("%.2f", t);
					t = Float.parseFloat(ts);
					w = Math.round(w * 304.8f);
					ws = String.format("%.2f", w);
					w = Float.parseFloat(ws);
					h = Math.round(h * 304.8f);
					hs = String.format("%.2f", h);
					h = Float.parseFloat(hs);
					layRot = Float.parseFloat(String.format("%1.2f",layRot));

					// } else if (origUnit.equalsIgnoreCase("Meters")) {
					// l = Math.round(l * 1000);
					// ls=String.format("%.2f", l);
					// l=Float.parseFloat(ls);
					// t = Math.round(t * 1000);
					// ts=String.format("%.2f", t);
					// t=Float.parseFloat(ts);
					// w = Math.round(w * 1000);
					// ws=String.format("%.2f", w);
					// w=Float.parseFloat(ws);
					// h = Math.round(h * 1000);
					// hs=String.format("%.2f", h);
					// h=Float.parseFloat(hs);
					//
					// } else {
					// l = Math.round(l * 25.4f);
					// ls=String.format("%.2f", l);
					// l=Float.parseFloat(ls);
					// t = Math.round(t * 25.4f);
					// ts=String.format("%.2f", t);
					// t=Float.parseFloat(ts);
					// w = Math.round(w * 25.4f);
					// ws=String.format("%.2f", w);
					// w=Float.parseFloat(ws);
					// h = Math.round(h * 25.4f);
					// hs=String.format("%.2f", h);
					// h=Float.parseFloat(hs);

					// }
					if (l + w > screenWidth || t + h > screenHeight) {
						Toast.makeText(getContext(),
								"Specified dimensions are not valid",
								Toast.LENGTH_SHORT).show();
						return;
					}
//					rel.removeView(layout);

					LayoutParams param = new LayoutParams(Math.round(w), Math
							.round(h));
					layout.setLayoutParams(param);

					LayoutParams params = new LayoutParams(
							Math.round(w), Math.round(h));
					// params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
					params.leftMargin = (int) l;
					params.topMargin = (int) t;
					LayoutDimensions dim = (LayoutDimensions) layout.getTag();
					float oldArea = dim.height * dim.width;
					int tilesUsed = dim.tilesUsed;
					int tileArea = (int) (oldArea / tilesUsed);
					dim.x = Math.round(l);
					dim.y = Math.round(t);
					dim.width = Math.round(w);
					dim.height = Math.round(h);
					dim.tilesUsed = (dim.width * dim.height) / tileArea;
					dim.rot = r;
					dim.layRot = layRot;
					layout.setTag(dim);
					layout.setRotation(layRot);
					layout.setOnTouchListener(layoutTouched);
					layout.setLayoutParams(params);
//					rel.addView(layout, params);
					startPoint = new Point(Math.round(l), Math.round(t));
					endPoint = new Point(Math.round(l + w), Math.round(t + h));
					// origUnit=unit;
					invalidate();
					if (dim.selectedTile != null && dim.selectedTile.length()>0) {
						Bitmap bm ;//= selectedTile;

						bm= BitmapFactory.decodeFile(dim.selectedTile).copy(Bitmap.Config.ARGB_8888, true);

						fillTileInLayout(layout, true, bm,
								GlobalVariables.getGrooveStatus(), dim.rot,dim.selectedTile);
					}
				} else {
					Toast.makeText(context, "Fields Cannot be left Blank..",
							Toast.LENGTH_LONG).show();
				}
			}
		});
		Button dismissButton = (Button) dialog
				.findViewById(R.id.dismiss_button);
		dismissButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});
		dialog.show();
	}

	void fillCustomTileInLayout(LinearLayout layout, Boolean shouldFill,
			Bitmap bitmap) {
		if (shouldFill) {
			/*
			 * int w = bitmap.getWidth() / 2; int h = bitmap.getHeight() / 2;
			 */
			int w, h;
			if (tileW > 0 && tileH > 0) {
				w = (int) tileW;
				h = (int) tileH;
				if (bitmap.getWidth() > bitmap.getHeight()) {
					if (tileW > tileH) {
						w = (int) tileW;
						h = (int) tileH;
					} else {
						w = (int) tileH;
						h = (int) tileW;
					}
					// w = tileW > tileH ? (int) tileW : (int) tileH;
					// h = tileW > tileH ? (int) tileH : (int) tileW;
				} else {

					if (tileW < tileH) {
						w = (int) tileW;
						h = (int) tileH;
					} else {
						w = (int) tileH;
						h = (int) tileW;
					}

					// w = tileW < tileH ? (int) tileW : (int) tileH;
					// h = tileW < tileH ? (int) tileH : (int) tileW;
				}
			} else {
				w = bitmap.getWidth() / 2;
				h = bitmap.getHeight() / 2;
			}

			float ratio = width / length;
			float rlength, rwidth;
			if (ratio > 1) {
				ratio = 1 / ratio;
				// rwidth = (ratio * viewArea);
				rlength = (ratio * viewArea);
				rwidth = viewArea;
			} else {
				rwidth = (ratio * viewArea);
				rlength = viewArea;
			}
			float screenWidth = rwidth;
			float screenHeight = rlength;

			float wallWidth = Math.abs(width);
			float wallLength = Math.abs(length);
			// convert to mm
			/*
			 * if (unit.equalsIgnoreCase("feet")) { wallWidth *= 304.8;
			 * wallLength *= 304.8; } else if (unit.equalsIgnoreCase("inches"))
			 * { wallWidth *= 25.4; wallLength *= 25.4; } else { wallWidth *=
			 * 1000; wallLength *= 1000; }
			 */

			float tileWidth = screenWidth * w / (float) wallWidth;
			// float tileWidth = screenWidth;
			float tileHeight = screenHeight * h / (float) wallLength;
			// float tileHeight = screenHeight;
			LayoutDimensions dim = (LayoutDimensions) layout.getTag();
			dim.selectedTile = selectedFilePath;
			dim.orientation = axis;
			layout.setTag(dim);

			float horiDim = wallWidth, vertDim = wallLength;

			float dimW = dim.width * horiDim / (float) screenWidth;
			float dimH = dim.height * vertDim / (float) screenHeight;

			int fillCountHori = (int) (dimW / tileWidth);
			float asd, asdf;
			if (dimW % tileWidth > 0)
				// if((asd=(dimW % (tileWidth*fillCountHori))/dimW)>0.1)
				// {
				fillCountHori++;
			// Log.d("TAGG", ""+asd+":::"+tileWidth+" "+dimW);
			// }

			int fillCountVerti = (int) (dimH / tileHeight);
			if (dimH % tileHeight > 0)
				// if((asdf=(dimH % (tileHeight*fillCountVerti))/dimH)>0.1)
				// {
				fillCountVerti++;
			// Log.d("TAGG", ""+asdf+":::"+tileHeight+" "+dimH);
			// }
			int count = (int) ((dimW * dimH) / (tileWidth * tileHeight));
			float fl;
			if ((dimW * dimH) % (tileWidth * tileHeight) > 0) {

				if ((fl = (dimW * dimH) % ((tileWidth * tileHeight) * count))
						/ (dimW * dimH) > 0.1) {
					count++;
				}
			}

			tileWidth = screenWidth * w / (float) horiDim;
			tileHeight = screenHeight * h / (float) vertDim;
			layout.removeAllViews();
			for (int i = 0; i < fillCountVerti; i++) {
				layout.setOrientation(LinearLayout.VERTICAL);
				LinearLayout linearLayout = new LinearLayout(context);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout.setBackgroundColor(Color.GREEN);
				linearLayout.setClickable(false);
				LayoutParams param = new LayoutParams(dim.width,
						(int) tileHeight);
				linearLayout.setLayoutParams(param);
				layout.addView(linearLayout);
				for (int j = 0; j < fillCountHori; j++) {
					float newWidth = tileWidth;
					float scaleFactor = (float) newWidth / (float) w;
					float newHeight = (h * scaleFactor);

					// Bitmap bmp = Bitmap.createScaledBitmap(bitmap, newWidth,
					// newHeight, true);

					ImageView img = new ImageView(context);
					img.setBackgroundColor(Color.RED);
					// img.setBackgroundDrawable(background);
					img.setScaleType(ScaleType.FIT_XY);
					img.setImageBitmap(bitmap);
					img.setLayoutParams(new LayoutParams((int) tileWidth,
							(int) tileHeight));
					img.setClickable(false);
					linearLayout.addView(img);
				}
				if (tileWidth * fillCountHori < viewArea) {
					float newWidth = tileWidth;
					float scaleFactor = (float) newWidth / (float) w;
					int newHeight = (int) (h * scaleFactor);

					// Bitmap bmp = Bitmap.createScaledBitmap(bitmap, newWidth,
					// newHeight, true);

					ImageView img = new ImageView(context);
					img.setBackgroundColor(Color.RED);
					img.setScaleType(ScaleType.FIT_XY);
					img.setImageBitmap(bitmap);
					img.setLayoutParams(new LayoutParams((int) tileWidth,
							(int) tileHeight));
					img.setClickable(false);
					linearLayout.addView(img);
				}
			}
			if (tileHeight * fillCountVerti < viewArea) {
				for (int i = 0; i < fillCountVerti; i++) {
					layout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout linearLayout = new LinearLayout(context);
					linearLayout.setOrientation(LinearLayout.HORIZONTAL);
					linearLayout.setBackgroundColor(Color.GREEN);
					linearLayout.setClickable(false);
					LayoutParams param = new LayoutParams((int) dim.width,
							(int) tileHeight);
					linearLayout.setLayoutParams(param);
					layout.addView(linearLayout);
					for (int j = 0; j < fillCountHori; j++) {
						int newWidth = (int) tileWidth;
						float scaleFactor = (float) newWidth / (float) w;
						int newHeight = (int) (h * scaleFactor);

						// Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
						// newWidth,
						// newHeight, true);

						ImageView img = new ImageView(context);
						img.setBackgroundColor(Color.RED);
						img.setScaleType(ScaleType.FIT_XY);
						img.setImageBitmap(bitmap);
						img.setLayoutParams(new LayoutParams((int) tileWidth,
								(int) tileHeight));
						img.setClickable(false);
						linearLayout.addView(img);

					}
					if (tileWidth * fillCountHori < viewArea) {
						int newWidth = (int) tileWidth;
						float scaleFactor = (float) newWidth / (float) w;
						int newHeight = (int) (h * scaleFactor);

						// Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
						// newWidth,
						// newHeight, true);

						ImageView img = new ImageView(context);
						img.setBackgroundColor(Color.RED);
						img.setScaleType(ScaleType.FIT_XY);
						img.setImageBitmap(bitmap);
						img.setLayoutParams(new LayoutParams((int) tileWidth,
								(int) tileHeight));
						img.setClickable(false);
						linearLayout.addView(img);
					}
				}
			}
		}
	}

	void fillTileInLayout(LinearLayout layout, Boolean shouldFill,
			Bitmap bitmap, Boolean groovesOn, float rotation, String tilePath) {
		if (shouldFill) {
			/*
			 * int w = bitmap.getWidth() / 2;// in mm int h = bitmap.getHeight()
			 * / 2;
			 */
			LayoutDimensions dim = (LayoutDimensions) layout.getTag();
			DatabaseHandler dh = new DatabaseHandler(context);

			String size = "0x0";

			if (tilePath != null) {
				size = dh.getTileSize(tilePath);
				if (size.equals("")) {
					size = "0x0";
				}
			}

			String[] tileDim = size.split("x");
			if (tileDim.length == 0) {
				tileDim = size.split("X");
			}
			dim.tileHeight = Float.valueOf(tileDim[0]);
			dim.tileWidth = Float.valueOf(tileDim[1]);

			float tileW = dim.tileWidth;
			float tileH = dim.tileHeight;
			int w, h;
			if (tileW > 0 && tileH > 0) {
				w = (int) tileW;
				h = (int) tileH;
				if (bitmap.getWidth() > bitmap.getHeight()) {
					if (tileW > tileH) {
						w = (int) tileW;
						h = (int) tileH;
					} else {
						w = (int) tileH;
						h = (int) tileW;
					}
					// w = tileW > tileH ? (int) tileW : (int) tileH;
					// h = tileW > tileH ? (int) tileH : (int) tileW;
				} else {

					if (tileW < tileH) {
						w = (int) tileW;
						h = (int) tileH;
					} else {
						w = (int) tileH;
						h = (int) tileW;
					}

					// w = tileW < tileH ? (int) tileW : (int) tileH;
					// h = tileW < tileH ? (int) tileH : (int) tileW;
				}
			} else {
				w = bitmap.getWidth() / 2;
				h = bitmap.getHeight() / 2;
			}

			float ratio = width / length;
			float rlength, rwidth;

			if (ratio > 1) {
				ratio = 1 / ratio;
				// rwidth = (ratio * viewArea);
				rlength = (ratio * viewArea);
				rwidth = viewArea;
			} else {
				rwidth = (ratio * viewArea);
				rlength = viewArea;
			}
			final float screenWidth = rwidth;
			final float screenHeight = rlength;

			// float screenWidth = viewArea;
			float wallWidth = GlobalVariables.getWallLength();
			// int tileWidth = screenWidth * w / wallWidth;
			// int tileWidth = wallWidth*w/screenWidth;
			float tileWidth = w;
			// float screenHeight = viewArea;
			float wallHeight = GlobalVariables.getWallHeight();
			// float wallLength =
			// GlobalVariables.feetToMm(20);//6096;//GlobalVariables.getWallLength();
			// float wallC = 0;//GlobalVariables.getWallC();
			// float wallD = 0;//GlobalVariables.getWallD();

			// int tileHeight = screenHeight * h / wallHeight;
			// int tileHeight = h * wallHeight/screenHeight ;
			float tileHeight = h;


			if (dim.selectedTile == null
					|| dim.selectedTile.equalsIgnoreCase("")
					|| dim.selectedTile.equalsIgnoreCase("null")) {
				dim.selectedTile = selectedFilePath;
			}
			if (dim.orientation == 0) {
				dim.orientation = axis;
			}
			// LayoutDimensions dim = (LayoutDimensions) layout.getTag();
			dim.selectedTile = selectedFilePath;
			dim.orientation = axis;

			// layout.setTag(dim);
			String currentProject = GlobalVariables.getProjectName();
			int extraTiles = 0;
			float horiDim = wallWidth, vertDim = wallHeight;
			if (wall.equalsIgnoreCase("front")) {

				horiDim = wallWidth;

				vertDim = wallHeight;

			}

			// tileWidth = screenWidth * w / horiDim;
			// tileHeight = screenHeight * h / vertDim;
			// tileWidth = w * horiDim/screenWidth;
			// tileHeight = h * vertDim/screenHeight;

			float dimW = (dim.width) * horiDim / (float) screenWidth;
			// float dimW = (dim.width);
			String dimWs = String.format("%1.2f", dimW);
			dimW = Float.parseFloat(dimWs);
			float dimH = (dim.height) * vertDim / (float) screenHeight;
			// float dimH = (dim.height);
			String dimHs = String.format("%1.2f", dimH);
			dimH = Float.parseFloat(dimHs);

			int fillCountHori = (int) (dimW / tileWidth);
			Float asd;
			if (dimW % tileWidth > 0) {
				// if((asd=(dimW % (tileWidth*fillCountHori))/dimW)>0.1)
				// {
				fillCountHori++;
				// Log.d("TAGG", ""+asd+":::"+tileWidth+" "+dimW);
				// }

			}

			int fillCountVerti = (int) (dimH / tileHeight);
			// Float asdf;
			if (dimH % tileHeight > 0) {
				// if((asdf=(dimH % (tileHeight*fillCountVerti))/dimH)>0.1)
				// {
				fillCountVerti++;
				// Log.d("TAGG", ""+asdf+":::"+tileHeight+" "+dimH);
				// }
			}
			int count = (int) ((dimW * dimH) / (tileWidth * tileHeight));
			float fl;
			if ((dimW * dimH) % (tileWidth * tileHeight) > 0) {
				//
				if ((fl = (dimW * dimH) % ((tileWidth * tileHeight) * count))
						/ (dimW * dimH) > 0.1
						|| count == 0) {
					count++;
				}
			}
			tileWidth = screenWidthRatio * screenWidth * w / (float) (horiDim);
			String tileWidths = String.format("%.2f", tileWidth);
			tileWidth = Float.parseFloat(tileWidths);

			tileHeight = screenWidthRatio * screenHeight * h / (float) vertDim;
			String tileHeights = String.format("%.2f", tileHeight);
			tileHeight = Float.parseFloat(tileHeights);

			int tilesUsed = 0;
			layout.removeAllViews();


			if (rotation % 360 == 0) {

				for (int i = 0; i < fillCountVerti; i++) {
					layout.setOrientation(LinearLayout.VERTICAL);
					LinearLayout linearLayout = new LinearLayout(context);
					linearLayout.setOrientation(LinearLayout.HORIZONTAL);
					linearLayout.setBackgroundColor(Color.GREEN);
					linearLayout.setClickable(false);
					LayoutParams param = new LayoutParams((int) (dim.width),
							(int) (tileHeight));
					linearLayout.setLayoutParams(param);
					layout.addView(linearLayout);
					for (int j = 0; j < fillCountHori; j++) {
						float newWidth = tileWidth;
						float scaleFactor = (float) newWidth / (float) w;
						int newHeight = (int) (h * scaleFactor);

						// Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
						// newWidth,
						// newHeight, true);

						ImageView img = new ImageView(context);
						BitmapDrawable ob = new BitmapDrawable(getResources(),
								bitmap);
						img.setBackground(ob);
						img.setScaleType(ScaleType.FIT_XY);
						// img.setImageBitmap(bitmap);
						if (groovesOn)
							img.setImageResource(R.drawable.imgbckgnd);
						img.setLayoutParams(new LayoutParams((int) tileWidth,
								(int) tileHeight));
						img.setClickable(false);
						linearLayout.addView(img);
						tilesUsed++;
					}
					if (tileWidth * fillCountHori < viewArea) {
						float newWidth = tileWidth;
						float scaleFactor = (float) newWidth / (float) w;
						int newHeight = (int) (h * scaleFactor);

						// Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
						// newWidth,
						// newHeight, true);

						ImageView img = new ImageView(context);
						BitmapDrawable ob = new BitmapDrawable(getResources(),
								bitmap);
						img.setBackground(ob);
						img.setScaleType(ScaleType.FIT_XY);
						// img.setImageBitmap(bitmap);
						if (groovesOn)
							img.setImageResource(R.drawable.imgbckgnd);
						img.setLayoutParams(new LayoutParams((int) tileWidth,
								(int) tileHeight));
						img.setClickable(false);
						linearLayout.addView(img);
					}
				}
				if (tileHeight * fillCountVerti < viewArea) {
					for (int i = 0; i < fillCountVerti; i++) {
						layout.setOrientation(LinearLayout.VERTICAL);
						LinearLayout linearLayout = new LinearLayout(context);
						linearLayout.setOrientation(LinearLayout.HORIZONTAL);
						linearLayout.setBackgroundColor(Color.GREEN);
						linearLayout.setClickable(false);
						LayoutParams param = new LayoutParams((int) dim.width,
								(int) tileHeight);
						linearLayout.setLayoutParams(param);
						layout.addView(linearLayout);
						for (int j = 0; j < fillCountHori; j++) {
							float newWidth = tileWidth;
							float scaleFactor = (float) newWidth / (float) w;
							int newHeight = (int) (h * scaleFactor);

							// Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
							// newWidth,
							// newHeight, true);

							ImageView img = new ImageView(context);
							BitmapDrawable ob = new BitmapDrawable(
									getResources(), bitmap);
							img.setBackground(ob);
							img.setScaleType(ScaleType.FIT_XY);
							// img.setImageBitmap(bitmap);
							if (groovesOn)
								img.setImageResource(R.drawable.imgbckgnd);
							img.setLayoutParams(new LayoutParams(
									(int) tileWidth, (int) tileHeight));
							img.setClickable(false);
							linearLayout.addView(img);

						}
						if (tileWidth * fillCountHori < viewArea) {
							float newWidth = tileWidth;
							float scaleFactor = (float) newWidth / (float) w;
							float newHeight = (float) (h * scaleFactor);

							// Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
							// newWidth,
							// newHeight, true);

							ImageView img = new ImageView(context);
							BitmapDrawable ob = new BitmapDrawable(
									getResources(), bitmap);
							img.setBackground(ob);
							img.setScaleType(ScaleType.FIT_XY);
							// img.setImageBitmap(bitmap);
							if (groovesOn)
								img.setImageResource(R.drawable.imgbckgnd);
							img.setLayoutParams(new LayoutParams(
									(int) tileWidth, (int) tileHeight));
							img.setClickable(false);
							linearLayout.addView(img);
						}
					}
				}
			} else {

				float layWidthmm, layHeightmm;
				layWidthmm = horiDim
						* ((float) dim.width / (screenWidthRatio*(float) GlobalVariables
								.getDrawArea(context)));
				layHeightmm = horiDim
						* ((float) dim.height / (screenWidthRatio*(float) GlobalVariables
								.getDrawArea(context)));
				Bitmap bm = GlobalVariables.getRotatedPattern(bitmap,
						dim.tileWidth, dim.tileHeight, layWidthmm, layHeightmm,
						dim.rot, context, groovesOn);
				layout.setOrientation(LinearLayout.VERTICAL);
				LinearLayout linearLayout = new LinearLayout(context);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout.setBackgroundColor(Color.GREEN);
				linearLayout.setClickable(false);
				LayoutParams param = new LayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				linearLayout.setLayoutParams(param);
				layout.addView(linearLayout);

				ImageView imgV = new ImageView(context);
				imgV.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				imgV.setScaleType(ScaleType.FIT_XY);
				imgV.setClickable(false);
				imgV.setImageBitmap(bm);
				linearLayout.addView(imgV);

			}
			dim.tilesUsed = count;
			dim.area = dimW * dimH;
			layout.setTag(dim);

		}

	}

	class LayoutDimensions {
		int x, y, height;
		int width;
		float area;
		float rot;
		float layRot;
		String selectedTile;
		int orientation;
		int tilesUsed;
		float tileWidth = 0, tileHeight = 0;
	}

	public Bitmap saveSignature() {

		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getWidth(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		this.draw(canvas);

		/*
		 * File file = new File(Environment.getExternalStorageDirectory() +
		 * "/sign.png");
		 * 
		 * try { //bitmap.compress(Bitmap.CompressFormat.PNG, 100, new
		 * FileOutputStream(file)); } catch (Exception e) { e.printStackTrace();
		 * }
		 */

		return bitmap;
	}

	Boolean savelayout(RelativeLayout layout, String prjName, String prjPath) {
		try {
			String path;

			// path =
			// Environment.getExternalStorageDirectory().getAbsolutePath() +
			// "/SmartShowRoom/"+currentProject+"/";
			path = prjPath;
			String saveName;
			saveName = prjName.replace(".xml", "") + "";
			// File directoryPath = new File(path);
			File directoryPath = new File(path);
			if (!directoryPath.exists()) {
				directoryPath.mkdirs();
				File ff = new File(path);
				// Toast.makeText(context, resId, duration)
				GlobalVariables.createNomediafile(path);
			}

			// }
			// String filePath=path+saveName+".xml";

			final String xmlFile = path + saveName + ".xml";
			File f = new File(xmlFile);
			if (f.exists()) {
				f.delete();
			}

			FileWriter out = new FileWriter(new File(xmlFile));

			// FileOutputStream fileos = getContext().openFileOutput("test.xml",
			// Context.MODE_PRIVATE);
			XmlSerializer xmlSerializer = Xml.newSerializer();
			StringWriter writer = new StringWriter();
			xmlSerializer.setOutput(writer);
			xmlSerializer.startDocument("UTF-8", true);

			for (int i = 1; i < layout.getChildCount(); i++) {
				View v = layout.getChildAt(i);
				LayoutDimensions dim = (LayoutDimensions) v.getTag();

				DatabaseHandler dh = new DatabaseHandler(context);
				String size = "0x0";

				if (dim.selectedTile != null) {
					size = dh.getTileSize(dim.selectedTile);
					if (size.equals("")) {
						size = "0x0";
					}
				}

				String[] tileDim = size.split("x");
				if (tileDim.length == 0) {
					tileDim = size.split("X");
				}
				dim.tileHeight = Float.valueOf(tileDim[0]);
				dim.tileWidth = Float.valueOf(tileDim[1]);

				xmlSerializer.startTag(null, "layoutData");

				xmlSerializer.startTag(null, "left");
				xmlSerializer.text(dim.x + "");
				xmlSerializer.endTag(null, "left");

				xmlSerializer.startTag(null, "top");
				xmlSerializer.text(dim.y + "");
				xmlSerializer.endTag(null, "top");

				xmlSerializer.startTag(null, "width");
				xmlSerializer.text(dim.width + "");
				xmlSerializer.endTag(null, "width");

				xmlSerializer.startTag(null, "height");
				xmlSerializer.text(dim.height + "");
				xmlSerializer.endTag(null, "height");

				xmlSerializer.startTag(null, "selectedTile");
				xmlSerializer.text(dim.selectedTile + "");
				xmlSerializer.endTag(null, "selectedTile");

				xmlSerializer.startTag(null, "tileSize");
				xmlSerializer.text(dim.tileHeight + "x" + dim.tileWidth);
				xmlSerializer.endTag(null, "tileSize");

				xmlSerializer.startTag(null, "orientation");
				xmlSerializer.text(dim.orientation + "");
				xmlSerializer.endTag(null, "orientation");

				xmlSerializer.startTag(null, "rotation");
				xmlSerializer.text(dim.rot + "");
				xmlSerializer.endTag(null, "rotation");

				xmlSerializer.startTag(null, "layoutRotation");
				xmlSerializer.text(dim.layRot + "");
				xmlSerializer.endTag(null, "layoutRotation");

				xmlSerializer.startTag(null, "unit");
				xmlSerializer.text(unit + "");
				xmlSerializer.endTag(null, "unit");

				xmlSerializer.startTag(null, "tileLength");
				xmlSerializer.text(length + "");
				xmlSerializer.endTag(null, "tileLength");

				xmlSerializer.startTag(null, "tileWidth");
				xmlSerializer.text(width + "");
				xmlSerializer.endTag(null, "tileWidth");

				xmlSerializer.endTag(null, "layoutData");

			}
			xmlSerializer.endDocument();
			xmlSerializer.flush();
			String dataWrite = writer.toString();
			out.write(dataWrite);
			out.close();
			return true;
			// fileos.write();
			// fileos.close();
		} catch (Exception e) {
			Toast.makeText(getContext(), "Could not save file!",
					Toast.LENGTH_SHORT).show();
			// Log.i("Save Layout Error", e.printStackTrace());
			// Log.e("Save Layout Error", e.printStackTrace());
			e.printStackTrace();
			return false;
		}
	}

}
