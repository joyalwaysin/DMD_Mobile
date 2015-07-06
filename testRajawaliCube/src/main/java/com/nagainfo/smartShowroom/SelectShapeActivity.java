package com.nagainfo.smartShowroom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import rajawali.RajawaliActivity;



import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectShapeActivity extends Activity {

	GetXmlfromAsset gAsset = new GetXmlfromAsset();
	XMLParser parser = new XMLParser();

	private String defaultShapes;
	ArrayList<HashMap<String, String>> shapeItems;
	HashMap<String, String> map;
	LinearLayout shapeContainer;
	// asset folder xml fields

	static final String KEY_ITEM = "shape";
	static final String KEY_SHAPE_NAME = "name";
	static final String KEY_SHAPE_WIDTH = "width";
	static final String KEY_SHAPE_HEIGHT = "height";
	static final String KEY_SHAPE_RESOURCE = "resource";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectshape);

		defaultShapes = gAsset.getXml("shape.xml", getApplicationContext());
		
		parseXml(defaultShapes);
	}

	OnClickListener rectSelected = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getApplicationContext(),
					SetupDimensionsActivity.class);
			startActivity(intent);
			finish();
		}
	};
	private View rowView;

	public void notImplemented(View v) {
		Toast.makeText(getApplicationContext(),
				"Functionality not implemented", Toast.LENGTH_LONG).show();
	}

	public class ShapeHolder {

		ImageView shapeImage;
		TextView shapename;

	}

	public void parseXml(String defaultShapes2) {
		// TODO Auto-generated method stub
		shapeItems = new ArrayList<HashMap<String, String>>();
		shapeItems.clear();
		try {
			Document doc = parser.getDomElement(defaultShapes2);

			NodeList nl = doc.getElementsByTagName(KEY_ITEM);

			for (int i = 0; i < nl.getLength(); i++) {
				map = new HashMap<String, String>();
				Element e = (Element) nl.item(i);

				map.put(KEY_SHAPE_NAME, parser.getValue(e, KEY_SHAPE_NAME));
				map.put(KEY_SHAPE_HEIGHT, parser.getValue(e, KEY_SHAPE_HEIGHT));
				map.put(KEY_SHAPE_WIDTH, parser.getValue(e, KEY_SHAPE_WIDTH));
				map.put(KEY_SHAPE_RESOURCE,
						parser.getValue(e, KEY_SHAPE_RESOURCE));

				// add map to hashmap array

				shapeItems.add(map);
			}

			populateShape(shapeItems);
		} catch (Exception e) {
			Log.e("XML parser error!", e.getMessage());
		}
	}

	void populateShape(ArrayList<HashMap<String, String>> shapeItems2) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		ShapeHolder holder;

		shapeContainer = (LinearLayout) findViewById(R.id.shapeContainer);
		((ViewGroup) shapeContainer).removeAllViews();

		for (int i = 0; i < shapeItems2.size(); i++) {
			rowView = inflater.inflate(R.layout.shapes, null);
			holder = new ShapeHolder();

			holder.shapeImage = (ImageView) rowView.findViewById(R.id.imgShape);
			holder.shapename = (TextView) rowView.findViewById(R.id.textShape);
			rowView.setTag(i);
			rowView.setOnTouchListener(shapeTouchListner);
			try {
				if (holder.shapeImage != null) {
					try {
						// get input stream
						InputStream ims = getAssets().open(
								shapeItems2.get(i).get(KEY_SHAPE_RESOURCE));
						// load image as Drawable
						Drawable d = Drawable.createFromStream(ims, null);
						// set image to ImageView
						holder.shapeImage.setImageDrawable(d);
					} catch (IOException ex) {
						return;
					}
				}
				if (holder.shapename != null) {
					holder.shapename.setText(shapeItems2.get(i).get(
							KEY_SHAPE_NAME));
				}

			} catch (Exception e) {
				Log.e("method populateShape", e.getMessage());
			}
			((ViewGroup) shapeContainer).addView(rowView);
		}
		((ViewGroup) shapeContainer).refreshDrawableState();
	}

	OnTouchListener shapeTouchListner = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				Integer tag = Integer.valueOf(v.getTag().toString());
				String height = shapeItems.get(tag).get(KEY_SHAPE_HEIGHT);
				String width = shapeItems.get(tag).get(KEY_SHAPE_WIDTH);
				String name = shapeItems.get(tag).get(KEY_SHAPE_NAME);
//				if(name.equals("Ambience"))
//				{
//					
//					Intent intent = new Intent(getApplicationContext(),Ambience.class);
//					
//					startActivity(intent);
//					finish();
//				
//				}
//				else
//				{
				Intent intent = new Intent(getApplicationContext(),
						SetupDimensionsActivity.class);
				intent.putExtra("shapeName", shapeItems.get(tag).get(KEY_SHAPE_NAME));
				intent.putExtra("height", height);
				intent.putExtra("width", width);
				startActivity(intent);
				finish();
//				}
 
				return true;
			}
			return false;
		}
	};

}
