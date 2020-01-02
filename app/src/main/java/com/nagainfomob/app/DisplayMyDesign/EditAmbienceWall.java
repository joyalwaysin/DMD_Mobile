package com.nagainfomob.app.DisplayMyDesign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
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
import android.widget.TextView;
import android.widget.Toast;

import com.nagainfomob.app.R;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.photoview.PhotoViewAttacher;
import com.nagainfomob.app.slider.PatternGridAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EditAmbienceWall extends Activity implements OnClickListener,
        PatternimgNameInterface {

    PhotoViewAttacher mAttacher;
    LinearLayout patternScrollView;
    private LinearLayout content_slider;
    Boolean isDrawerOpen = false;
    Boolean isOpening = false;
    Boolean shouldExit = false;
    int displayWidth;
    RelativeLayout patternHeader;
    RelativeLayout main_layout;
    // LinearLayout twod_layout;
    // LinearLayout buttonLayout;
    GridView gView;
    private static final String KEY_BRAND_NAME = "pro_brand";
    private static final String KEY_DIMEN = "pro_dimen";
    private static final String KEY_COLOR = "pro_color";
    private static final String KEY_TYPE = "pro_type";
    private static final String KEY_COMPANY = "pro_company";
    public static String DrawArea;
    List<Integer> selectedBrands = new ArrayList<Integer>();
    List<Integer> selectedSize = new ArrayList<Integer>();
    List<Integer> selectedColor = new ArrayList<Integer>();
    List<Integer> selectedType = new ArrayList<Integer>();
    List<Integer> selectedCompany = new ArrayList<Integer>();
    // DrawView drawView;
    AmbienceEditView editView;
    String fileName = null;
    Boolean drawToolSelected = false;
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
    private float rotation, layoutRotation;
    private EditText eText;
    public static Bitmap bitmap;
    float horiDim = 0, vertiDim = 0;
    float screenWidthRatio = (float) 1.75;

    private Boolean groovesOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patternlibrary);

        intialize();
        Intent intent = getIntent();
        if (intent.hasExtra("Height")) {
            String string = intent.getStringExtra("Height");
            vertiDim = Float.parseFloat(string);
            string = null;
        }
        if (intent.hasExtra("Width")) {
            String string = intent.getStringExtra("Width");
            horiDim = Float.parseFloat(string);
            string = null;
        }
        if (intent.hasExtra("FileName")) {
            String string = intent.getStringExtra("FileName");
            fileName = string;
            string = null;
        }
        if (intent.hasExtra("Ratio")) {
            String string = intent.getStringExtra("Ratio");
            float ratio = Float.parseFloat(string);
            screenWidthRatio = ratio;
        }

        viewArea = GlobalVariables.getDrawArea(EditAmbienceWall.this);
        this.getActionBar().setBackgroundDrawable(
                this.getResources().getDrawable(R.drawable.actionbar_bg));
        this.getActionBar().setTitle(null);
        this.getActionBar().setDisplayShowHomeEnabled(false);
        editView = new AmbienceEditView(this, screenWidthRatio);
        // editView.setLayoutParams(new LayoutParams(viewArea, viewArea));
        // editView.setDimensions("Feet",(float) 10,(float) 10);
        FrameLayout.LayoutParams params;
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
        // params = (FrameLayout.LayoutParams) rel.getLayoutParams();
        // params.height = viewArea;
        // params.width = viewArea;

        if (horiDim == vertiDim) {
            editView.setLayoutParams(new LayoutParams(viewArea, viewArea));
            params = (FrameLayout.LayoutParams) rel.getLayoutParams();
            params.height = (int) viewArea;
            params.width = (int) viewArea;

        }
        // drawView.setMode(true);

        else if (horiDim > vertiDim) {

            editView.setLayoutParams(new LayoutParams(
                    (int) (screenWidthRatio * viewArea),
                    (int) (screenWidthRatio * viewArea * (vertiDim / horiDim))));
            params = (FrameLayout.LayoutParams) rel.getLayoutParams();
            params.height = (int) (screenWidthRatio * viewArea * (vertiDim / horiDim));
            params.width = (int) (screenWidthRatio * viewArea);
        } else {
            editView.setLayoutParams(new LayoutParams(
                    (int) (viewArea * (horiDim / vertiDim)), viewArea));
            params = (FrameLayout.LayoutParams) rel.getLayoutParams();
            params.height = (int) viewArea;
            params.width = (int) (viewArea * (horiDim / vertiDim));
        }

        editView.setDimensions("Feet", (float) vertiDim, (float) horiDim);
        params.leftMargin = 100;
        params.topMargin = 30;
        String path;
        String currentProject = "Ambience";// GlobalVariables.getProjectName();

        path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/SmartShowRoom/" + currentProject + "/";

        String filePath = path + fileName + ".png";
        editView.setBackgroundResource(R.drawable.wall);

        Boolean pathExists = new File(filePath).exists();

        rel.addView(editView);
        rel.setLayoutParams(params);

        if (new File(filePath).exists()) {

            GlobalVariables.setUnit("Feet");
            loadWall();

        }

        // bitmap = BitmapFactory.decodeResource(getResources(),
        // R.drawable.right).copy(Bitmap.Config.ARGB_8888, true);

        // finish();
    }

    void loadWall() {
        try {
            String data = null;
            String path;
            String currentProject = "Ambience";

            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/SmartShowRoom/" + currentProject + "/";
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
            int left = 0, top = 0, width = 0, height = 0, orientation = 0;
            String selectedTileName = null, unit = null;
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
                    if (name.equalsIgnoreCase("tileSize")) {
                        tileSize = readValue;
                    }
                    if (name.equalsIgnoreCase("orientation")) {
                        orientation = Integer.parseInt(readValue);
                        Toast.makeText(getApplicationContext(),
                                "orientation" + orientation, Toast.LENGTH_SHORT)
                                .show();
                    }
                    if (name.equalsIgnoreCase("selectedTile")) {
                        selectedTileName = readValue;
                    }
                    if (name.equalsIgnoreCase("unit")) {
                        unit = readValue;
                        GlobalVariables.setUnit(unit);
                        editView.setUnit(unit);
                    }
                    if (name.equalsIgnoreCase("rotation")) {
                        rotation = Float.parseFloat(readValue);
                    }
                    if (name.equalsIgnoreCase("layoutRotation")) {
                        layoutRotation = Float.parseFloat(readValue);
                    }
                    if (name.equalsIgnoreCase("layoutData")) {
                        addLayout(left, top, width, height, selectedTileName,
                                orientation, unit, rotation, layoutRotation);
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

    public void addLayout(int left, int top, int width, int height,
                          String selectedTileName, int orientation, String unit, Float rot, Float layRot) {
        try {
            RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
            {

                LinearLayout linearLayout = new LinearLayout(
                        getApplicationContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                // linearLayout.setBackgroundColor(Color.YELLOW);
                // Random random=new Random();
                Random randomService = new Random();
                StringBuilder sb = new StringBuilder();
                while (sb.length() < 6) {
                    sb.append(Integer.toHexString(randomService.nextInt()));
                }
                sb.setLength(6);
                String s = "#" + sb.toString();
                linearLayout.setBackgroundColor(Color.parseColor(s));
                // int
                // thisDeviceDrawArea=GlobalVariables.getDrawArea(EditAmbienceWall.this);
                // int projectDrawArea=Integer.parseInt(new
                // DatabaseHandler(PatternLibraryActivity.this).getViewAreaOfProject(GlobalVariables.getProjectName()));
                // String string=new
                // DatabaseHandler(EditAmbienceWall.this).getViewAreaOfProject(GlobalVariables.getProjectName());
                // if(!string.equals("0"))
                // {
                // int projectDrawArea=Integer.parseInt(new
                // DatabaseHandler(EditAmbienceWall.this).getViewAreaOfProject(GlobalVariables.getProjectName()));
                // // LayoutParams param = null;
                // // RelativeLayout.LayoutParams params = null;
                // if(thisDeviceDrawArea==projectDrawArea)
                // {
                LayoutParams param = new LayoutParams(width, height);
                linearLayout.setLayoutParams(param);
                linearLayout.setClickable(false);
                linearLayout.setOnTouchListener(editView.layoutTouched);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        width, height);
                params.leftMargin = left;
                params.topMargin = top;

                AmbienceEditView.LayoutDimensions dim = editView.new LayoutDimensions();
                dim.x = left;
                dim.y = top;
                dim.width = width;
                dim.height = height;
                dim.selectedTile = selectedTileName;
                dim.orientation = orientation;
                dim.rot = rot;
                dim.layRot = layRot;

                linearLayout.setTag(dim);
                linearLayout.setRotation(layRot);
                rel.setClickable(true);
                rel.addView(linearLayout, params);
                linearLayout.bringToFront();

                final String filePath = selectedTileName;
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    Bitmap rBmp = RotateBitmapRight(bitmap, 90 * orientation);
                    // bmp.recycle();

                    // imgV.setImageBitmap(bmp);
                    editView.setSelectedTile(rBmp, filePath, orientation,
                            tileSize);
                    editView.fillTileInLayout(linearLayout, true, rBmp, groovesOn, rot, selectedTileName );
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {

        }
    }

    void getWallInfo() {
        String wall = fileName;
//		String wallCode;

//		float wallWidth = GlobalVariables.getWallWidth();
//		float wallHeight = GlobalVariables.getWallHeight();
//		float wallLength = GlobalVariables.getWallLength();
//		float wallC = GlobalVariables.getWallC();
//		float wallD = GlobalVariables.getWallD();

//		String currentProject = GlobalVariables.getProjectName();

//		float horiDim = wallWidth, vertDim = wallHeight;
//		if (wall.equalsIgnoreCase("front")) {
////			if (!currentProject.startsWith("Rectangle")) {
//				horiDim =  wallC;
////			} else {
////				horiDim =  wallWidth;
////			}
//
//			vertDim = wallHeight;
//		}
//		} else if (wall.equalsIgnoreCase("back")) {
//			horiDim = wallWidth;
//			vertDim = wallHeight;
//
//		} else if (wall.equalsIgnoreCase("left")) {
//			if (!currentProject.startsWith("Rectangle")) {
//				horiDim =  wallD;
//			} else {
//				horiDim =  wallLength;
//			}
//
//			vertDim = wallHeight;
//		} else if (wall.equalsIgnoreCase("right")) {
//			horiDim = wallLength;
//			vertDim = wallHeight;
//
//		} else if (wall.equalsIgnoreCase("top")
//				|| wall.equalsIgnoreCase("bottom")) {
//			horiDim = wallWidth;
//			vertDim = wallLength;
//
//			// calculate area of remaining triangle and calculate tiles
//			// needed for that .then subtract this from total tiles
//
//		} else if (wall.equalsIgnoreCase("frontleft")) {
//			horiDim = (float) (Math.sqrt(Math.pow((wallWidth - wallC), 2)
//					+ Math.pow((wallLength - wallD), 2)));
//			vertDim = wallHeight;
//		}

//		Log.i("wall name", wall + " wall");

        final Dialog dialog = new Dialog(EditAmbienceWall.this);
        dialog.setTitle("Wall Info");
        dialog.setContentView(R.layout.wall_info_dialog);
        TextView widthTextView = (TextView) dialog
                .findViewById(R.id.widthTextView);
        TextView lengthTextView = (TextView) dialog
                .findViewById(R.id.lengthTextView);
        TextView nameTextView = (TextView) dialog
                .findViewById(R.id.wallNameField);

//		wallCode = GlobalVariables.getWallCode(wall, "A");

        nameTextView.setText(fileName);

        widthTextView.setText("Width = " + vertiDim + " mm / "
                + GlobalVariables.mmToFeet(vertiDim) + " feet / "
                + GlobalVariables.mmToInches(vertiDim) + " inches");
        lengthTextView.setText("Length = " + horiDim + " mm / "
                + GlobalVariables.mmToFeet(horiDim) + " feet / "
                + GlobalVariables.mmToInches(horiDim) + " inches");
        dialog.show();

    }

    void intialize() {


        isDrawerOpen = false;
        isOpening = false;
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        patternContent2 = inflater.inflate(R.layout.layout_patters, null);

        // private EditText eText;
        eText = (EditText) patternContent2.findViewById(R.id.textSearch);
        // eText.setText(null);
        filterSearch_button = (Button) patternContent2
                .findViewById(R.id.filterButton);
        filterSearch_button.setOnClickListener(EditAmbienceWall.this);

        Button search = (Button) patternContent2
                .findViewById(R.id.searchButton);

        search.setOnClickListener(EditAmbienceWall.this);
        //
        //
        patternContent = inflater.inflate(R.layout.layout_filter, null);
        filterSearchButton2 = (Button) patternContent
                .findViewById(R.id.filterButton2);

        filterSearchButton2.setOnClickListener(EditAmbienceWall.this);

        patternScrollView = (LinearLayout) findViewById(R.id.patternScrollView);
        main_layout = (RelativeLayout) findViewById(R.id.main_content);
        content_slider = (LinearLayout) findViewById(R.id.content_slider);
        patternHeader = (RelativeLayout) findViewById(R.id.patternHeader);
        // twod_layout = (LinearLayout) findViewById(R.id.twod_layout);
        // buttonLayout = (LinearLayout) findViewById(R.id.leftButtonLayout);
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
        loadTile();
        // twod_layout.bringToFront();
    }

    private Dialog createPreviewDialog() {
        Dialog dialog = new Dialog(EditAmbienceWall.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // dialog.setTitle("New Contact");//sets the title of the dialog box

        dialog.setContentView(R.layout.preview_dialog);// loads the layout we
        // have

        // dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
        // R.layout.custom_title_dialog_box);// sets our custom

		/*
         * Button closeButton=(Button)dialog.findViewById(R.id.dialog_close);
		 * closeButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub dialog.dismiss(); } });
		 */

        ImageView img = (ImageView) dialog.findViewById(R.id.previewImageView);

        img.setScaleType(ScaleType.CENTER_INSIDE);
        mAttacher = new PhotoViewAttacher(img);
        return dialog;

    }

    public void filterSelected() {
        patternScrollView.removeAllViews();
        // LayoutInflater inflater = (LayoutInflater)
        // getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // selectedCompany.clear();
        // selectedBrands.clear();
        // selectedSize.clear();
        // selectedColor.clear();
        // selectedType.clear();
        addCompanyCheckBox(patternContent);
        addbrandCheckBox(patternContent);
        addSizeCheckBox(patternContent);
        addColorCheckBox(patternContent);
        addTypeCheckBox(patternContent);
        patternScrollView.addView(patternContent);

    }

    public void addbrandCheckBox(View patternContent) {
        View v = patternContent;
        ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.brandCheckBox);
        ll.removeAllViews();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        dbResult1 = db.getAllBrand();
        List<String> brandNameList = new ArrayList<String>();

        for (int i = 0; i < dbResult1.size(); i++) {
            Map<String, String> temp1 = dbResult1.get(i);
            brandNameList.add(temp1.get(KEY_BRAND_NAME));
        }

        for (int i = 0; i < brandNameList.size(); i++) {

            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText(brandNameList.get(i));
            cb.setTextColor(Color.BLACK);
            cb.setTag(i);
            cb.setOnCheckedChangeListener(brandCheckListener);
            ll.addView(cb);
        }

    }

    public void addCompanyCheckBox(View patternContent) {
        View v = patternContent;
        ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.companyCheckBox);
        ll.removeAllViews();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        dbResult1 = db.getAllCompany();
        List<String> brandNameList = new ArrayList<String>();

        for (int i = 0; i < dbResult1.size(); i++) {
            Map<String, String> temp1 = dbResult1.get(i);
            if (!temp1.get(KEY_COMPANY).equalsIgnoreCase(""))
                brandNameList.add(temp1.get(KEY_COMPANY));
        }

        for (int i = 0; i < brandNameList.size(); i++) {

            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText(brandNameList.get(i));
            cb.setTextColor(Color.BLACK);
            cb.setTag(i);
            cb.setOnCheckedChangeListener(companyCheckListener);
            ll.addView(cb);
        }

    }

    OnCheckedChangeListener companyCheckListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    int t = (Integer) buttonView.getTag();
                    selectedCompany.add(new Integer(t));
                } catch (Exception e) {
//                    Log.e("company checked item error", "");
                    e.printStackTrace();
                }

            } else {
                selectedCompany.clear();
            }

            if (!isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedCompany.remove(new Integer(t));
            }

        }

    };

    OnCheckedChangeListener brandCheckListener = new OnCheckedChangeListener() {

        @SuppressLint("UseValueOf")
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                try {
                    Integer t = (Integer) buttonView.getTag();
                    selectedBrands.add(t);
                } catch (Exception e) {
//                    Log.e("brand checked item error", "");
                    e.printStackTrace();
                }

            } else {
                selectedBrands.clear();
            }
            if (!isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedBrands.remove(new Integer(t));
            }

        }

    };

    public void addSizeCheckBox(View v) {
        LinearLayout l = (LinearLayout) v.findViewById(R.id.sizeCheckbox);
        l.removeAllViews();
        ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        dbResult2 = db.getDistinctSize();
        List<String> brandNameList = new ArrayList<String>();

        for (int i = 0; i < dbResult2.size(); i++) {
            Map<String, String> temp1 = dbResult2.get(i);
            if (!temp1.get(KEY_DIMEN).equalsIgnoreCase(""))
                brandNameList.add(temp1.get(KEY_DIMEN));
        }

        for (int i = 0; i < brandNameList.size(); i++) {

            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText(brandNameList.get(i));
            cb.setTextColor(Color.BLACK);
            cb.setTag(i);
            cb.setOnCheckedChangeListener(sizeChangeListener);
            l.addView(cb);
        }

    }

    OnCheckedChangeListener sizeChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedSize.add(new Integer(t));
            } else {
                selectedSize.clear();
            }
            if (!isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedSize.remove(new Integer(t));
            }

        }
    };

    public void addColorCheckBox(View v) {
        LinearLayout l = (LinearLayout) v.findViewById(R.id.colorCheckBox);
        l.removeAllViews();
        ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        dbResult2 = db.getDistinctColor();
        List<String> brandNameList = new ArrayList<String>();

        for (int i = 0; i < dbResult2.size(); i++) {
            Map<String, String> temp1 = dbResult2.get(i);
            if (!temp1.get(KEY_COLOR).equalsIgnoreCase(""))
                brandNameList.add(temp1.get(KEY_COLOR));
        }

        for (int i = 0; i < brandNameList.size(); i++) {

            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText(brandNameList.get(i));
            cb.setTextColor(Color.BLACK);
            cb.setTag(i);
            cb.setOnCheckedChangeListener(colorChangeListener);
            l.addView(cb);
        }

    }

    OnCheckedChangeListener colorChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedColor.add(new Integer(t));

            } else {
                selectedColor.clear();
            }
            if (!isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedColor.remove(new Integer(t));
            }

        }
    };

    public void addTypeCheckBox(View v) {
        LinearLayout l = (LinearLayout) v.findViewById(R.id.typeCheckBox);
        l.removeAllViews();
        ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        dbResult2 = db.getDistinctType();
        List<String> brandNameList = new ArrayList<String>();

        for (int i = 0; i < dbResult2.size(); i++) {
            Map<String, String> temp1 = dbResult2.get(i);
            if (!temp1.get(KEY_TYPE).equalsIgnoreCase(""))
                brandNameList.add(temp1.get(KEY_TYPE));
        }

        for (int i = 0; i < brandNameList.size(); i++) {

            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText(brandNameList.get(i));
            cb.setTextColor(Color.BLACK);
            cb.setTag(i);
            cb.setOnCheckedChangeListener(typeChangeListener);
            l.addView(cb);
        }

    }

    OnCheckedChangeListener typeChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            if (isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedType.add(new Integer(t));

            } else {
                selectedType.clear();
            }
            if (!isChecked) {
                int t = (Integer) buttonView.getTag();
                selectedType.remove(new Integer(t));
            }

        }
    };

    public void showPattern(ArrayList<HashMap<String, String>> dbResult) {

        PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
                EditAmbienceWall.this);
        gView.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        // grid.setAdapter(adapter);
        gView.setAdapter(null);
        gView.setAdapter(pgAdapter);

    }

    public void getAllpattern() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        int countRec = db.getCount();
        dbResult = db.getAllPattern();

        PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
                EditAmbienceWall.this);
        gView.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        // grid.setAdapter(adapter);
        gView.setAdapter(null);
        gView.setAdapter(pgAdapter);

    }

    public String getStringFromCheckedList(
            ArrayList<HashMap<String, String>> dbResult,
            List<Integer> selectedBrands2, String ketItem) {
        List<String> itemString = new ArrayList<String>();
        String resString = "";
        if (selectedBrands2.size() == 0) {
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
        if (selectedBrands2.size() == 1) {
            try {
                int index = selectedBrands2.get(0);
                resString = "'" + itemString.get(index) + "'";
            } catch (Exception ex) {
                Log.e("error", ex.getMessage());

            }
        } else {
            resString = "'" + itemString.get(selectedBrands2.get(0)) + "'";
            System.out.println(selectedBrands2.size());
            for (int i = 1; i < selectedBrands2.size(); i++) {
                try {
                    int itemId = selectedBrands2.get(i);
                    resString += ",'" + itemString.get(selectedBrands2.get(i))
                            + "'";
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.e("getStringFromCheckedLis", e.getMessage());
                    return null;

                }
            }
        }
        return resString;

    }

    void donePressed() {
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);

        rel.setDrawingCacheEnabled(true);
        // tiles = calculateCost();
        String currentProject = "Ambience";
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/SmartShowRoom/" + currentProject + "/";

        boolean success = editView.savelayout(rel, fileName, path);
        if (success) {
            bitmap = rel.getDrawingCache();
            // bitmap = getCompressedImage(bitmap);
            Intent i = new Intent();
            String filePath = saveWall(bitmap);
            // i.putExtra("Location", filePath);
            if (tiles == null)
                tiles = new LinkedHashMap<String, Integer>();
            // i.putExtra("tileMap1", tiles);

            setResult(RESULT_OK, i);
            finish();
        }
        // drawView.setSelectedTile(bitmap, filePath, axis);

    }

    private String saveWall(Bitmap bmp) {
        String path;
        String currentProject = "Ambience";

        path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/SmartShowRoom/" + currentProject + "/";
        File directoryPath = new File(path);
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
            GlobalVariables.createNomediafile(path);
        }

        String filePath = path + fileName + ".png";
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

    public Bitmap getCompressedImage(Bitmap inputBitmap) {
        int maxCompression = 10;
        int compression = 90;
        int imageSize = 1000 * 1024;

        while (inputBitmap.getByteCount() > imageSize
                && compression > maxCompression) {
            int inSize = inputBitmap.getByteCount();
            ByteArrayOutputStream baosz = new ByteArrayOutputStream();
            inputBitmap
                    .compress(Bitmap.CompressFormat.JPEG, compression, baosz);
            compression = compression - 10;
            byte[] bitmapdata = baosz.toByteArray();
            inputBitmap = null;
            inputBitmap = BitmapFactory.decodeByteArray(bitmapdata, 0,
                    bitmapdata.length);
        }

        return inputBitmap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);

        if (groovesOn) {
            menu.getItem(0).setTitle("GROOVES ON");
        } else {
            menu.getItem(0).setTitle("GROOVES OFF");
        }
        menu.getItem(2).setVisible(false);
//		menu.getItem(0).setVisible(false);
//		menu.getItem(1).setVisible(false);

        return true;
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.wallInfo:
                getWallInfo();
                break;
            case R.id.actionPen:
                drawToolSelected = true;
                editView.isDrawingSelected(drawToolSelected);
                editView.selectTile(false);
                // Toast.makeText(getApplicationContext(), "pen selected",
                // Toast.LENGTH_SHORT).show();
                break;
            // action with ID action_settings was selected
            /*case R.id.actionHome:
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
                finish();

                break;*/
            case R.id.actionPointer:

                drawToolSelected = false;
                editView.isDrawingSelected(drawToolSelected);
                editView.selectTile(false);
                // Toast.makeText(getApplicationContext(), "pointer selected",
                // Toast.LENGTH_SHORT).show();
                break;
            case R.id.actionSearch:
                addSlider();
                break;
            case R.id.actionDone:
                donePressed();
                break;
            case R.id.actionbarTool:

                RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
                rel.invalidate();
                rel.setDrawingCacheEnabled(true);
                Bitmap bitmap = rel.getDrawingCache();
                // showPreview(bitmap);

                break;
            case R.id.grooveSwap:

                if (groovesOn) {
                    groovesOn = false;
                    GlobalVariables.setGroovesOff();
                    invalidateOptionsMenu();
                } else {
                    groovesOn = true;
                    GlobalVariables.setGroovesOn();
                    invalidateOptionsMenu();
                }

                break;

            default:
                break;
        }

        return true;
    }

    private AnimationListener slideLeftRightAnimationListener = new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            setIsOpening(true);
            content_slider.bringToFront();
            if (!getIsDrawerOpen()) {
                // filterSelected(null);
//				loadTile();
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
//				removeTile();

            }
            setIsOpening(false);
            setIsDrawerOpen();
        }

    };
    private Bitmap bmp;

    private void addSlider() {
        if (!isOpening) {

            Animation animation;
            content_slider.setVisibility(View.VISIBLE);
            if (!isDrawerOpen) {
                animation = new TranslateAnimation(displayWidth, 0, 0, 0);

            } else {
                animation = new TranslateAnimation(0, displayWidth, 0, 0);
            }

            animation.setDuration(500);
            animation.setAnimationListener(slideLeftRightAnimationListener);

            // content_slider.getBackground().setAlpha(1000);

            content_slider.setAnimation(animation);
            content_slider.startAnimation(animation);
            content_slider.requestLayout();
        }

    }

    protected void removeTile() {
        // TODO Auto-generated method stub
        patternScrollView.removeAllViews();
    }

    protected void loadTile() {
        patternScrollView.removeAllViews();

        gView = (GridView) patternContent2.findViewById(R.id.slider_pattern);
        patternScrollView.addView(patternContent2);
        getAllpattern();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.searchButton: {
                tileSearch();
                break;
            }
            case R.id.filterButton: {
                filterSelected();
                break;
            }
            case R.id.filterButton2: {
                applyFilter();
                break;
            }

            default:
                break;
        }

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
                    dbResult, EditAmbienceWall.this);
            gView.refreshDrawableState();
            pgAdapter.notifyDataSetChanged();
            // grid.setAdapter(adapter);
            gView.setAdapter(null);
            gView.setAdapter(pgAdapter);
        } else {
            gView.setAdapter(null);
            Toast.makeText(this, "No Tile found !", Toast.LENGTH_LONG).show();
        }
        // System.out.println("test");
    }

    private void applyFilter() {
        // patternScrollView.removeAllViews();
        // selectedCompany.clear();
        // selectedBrands.clear();
        // selectedSize.clear();
        // selectedColor.clear();
        // selectedType.clear();
        // loadTile();
        String cm_name, br_name, pro_size, pro_color, pro_type;
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> dbResult1 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> dbResult2 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> dbResult3 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> dbResult4 = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> filterResult = new ArrayList<HashMap<String, String>>();
        // String brand, size, color, type;
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        DatabaseHandler db1 = new DatabaseHandler(getApplicationContext());
        DatabaseHandler db2 = new DatabaseHandler(getApplicationContext());
        DatabaseHandler db3 = new DatabaseHandler(getApplicationContext());
        DatabaseHandler db4 = new DatabaseHandler(getApplicationContext());
        dbResult = db.getAllCompany();
        dbResult1 = db1.getAllBrand();
        dbResult2 = db2.getDistinctSize();
        dbResult3 = db3.getDistinctColor();
        dbResult4 = db4.getDistinctType();
        cm_name = getStringFromCheckedList(dbResult, selectedCompany,
                KEY_COMPANY);
        br_name = getStringFromCheckedList(dbResult1, selectedBrands,
                KEY_BRAND_NAME);
        pro_size = getStringFromCheckedList(dbResult2, selectedSize, KEY_DIMEN);
        pro_color = getStringFromCheckedList(dbResult3, selectedColor,
                KEY_COLOR);
        pro_type = getStringFromCheckedList(dbResult4, selectedType, KEY_TYPE);
        DatabaseHandler db5 = new DatabaseHandler(getApplicationContext());

        filterResult = db5.getResultByFilter(cm_name, br_name, pro_size,
                pro_color, pro_type);
        if (filterResult.size() == 0) {
            Toast.makeText(getApplicationContext(), "No tiles found! ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (filterResult.size() > 0) {
            selectedCompany.clear();
            selectedBrands.clear();
            selectedSize.clear();
            selectedColor.clear();
            selectedType.clear();

            // View patternContent3 = inflater.inflate(R.layout.layout_patters,
            // null);

            gView = (GridView) patternContent2
                    .findViewById(R.id.slider_pattern);
            patternScrollView.removeAllViews();
            patternScrollView.addView(patternContent2);
            showPattern(filterResult);
        }

        // System.out.println("test");

    }

    @Override
    public void patternName(String path, final String tileSize, String brand,
                            String type, String tile_id, String tile_type, String price) {
        // System.out.println("hai");
        final String filePath = path;
        bmp = BitmapFactory.decodeFile(filePath);
        drawToolSelected = false;
        editView.isDrawingSelected(drawToolSelected);
        editView.selectTile(true);
        this.tileSize = tileSize;
        final Dialog dialog = new Dialog(EditAmbienceWall.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setTitle("Rotate");// sets the title of the dialog box

        dialog.setContentView(R.layout.tile_rotation_layout);// loads the layout
        // we have

        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_title_dialog_box);// sets our custom
        TextView tileNameView = (TextView) dialog.findViewById(R.id.tileName);
        TextView tileSizeView = (TextView) dialog.findViewById(R.id.tDim);
        TextView tileBrandNameView = (TextView) dialog
                .findViewById(R.id.brandname);
        TextView tileTypeView = (TextView) dialog.findViewById(R.id.tileType);

        tileSizeView.setText(tileSize + " mm");
        tileNameView.setText(new File(path).getName());
        tileBrandNameView.setText(brand);
        tileTypeView.setText(type);

		/*
		 * Button closeButton=(Button)dialog.findViewById(R.id.dialog_close);
		 * closeButton.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub dialog.dismiss(); } });
		 */
        final ImageView imgV = (ImageView) dialog
                .findViewById(R.id.tileImageView);
        imgV.setImageBitmap(bmp);
        Button rightRotButton = (Button) dialog
                .findViewById(R.id.rotRightButton);
        rightRotButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Bitmap rBmp = RotateBitmapRight(bmp, 90);
                axis++;
                // bmp.recycle();
                bmp = rBmp;
                imgV.setImageBitmap(bmp);
                editView.setSelectedTile(bmp, filePath, axis, tileSize);
                // rBmp.recycle();
            }
        });
        Button leftRotButton = (Button) dialog.findViewById(R.id.rotLeftButton);
        leftRotButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Bitmap rBmp = RotateBitmapRight(bmp, -90);
                axis--;
                // bmp.recycle();
                bmp = rBmp;
                imgV.setImageBitmap(bmp);

                editView.setSelectedTile(bmp, filePath, axis, tileSize);
                // rBmp.recycle();
                // imgV.setImageBitmap(RotateBitmapRight(bmp, 90));
            }
        });

        dialog.show();

        editView.setSelectedTile(bmp, filePath, axis, tileSize);
        axis = 0;

    }

    public static Bitmap RotateBitmapRight(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    public void addlayout(int left, int top, int width, int height,
                          String selectedTileName, int orientation, String unit, Float rot, Float layRot) {

        try {
            RelativeLayout rel = (RelativeLayout) findViewById(R.id.main_content);
            {

                LinearLayout linearLayout = new LinearLayout(
                        getApplicationContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                // linearLayout.setBackgroundColor(Color.YELLOW);
                // Random random=new Random();
                Random randomService = new Random();
                StringBuilder sb = new StringBuilder();
                while (sb.length() < 6) {
                    sb.append(Integer.toHexString(randomService.nextInt()));
                }
                sb.setLength(6);
                String s = "#" + sb.toString();
                linearLayout.setBackgroundColor(Color.parseColor(s));
                int thisDeviceDrawArea = GlobalVariables
                        .getDrawArea(EditAmbienceWall.this);
                // int projectDrawArea=Integer.parseInt(new
                // DatabaseHandler(PatternLibraryActivity.this).getViewAreaOfProject(GlobalVariables.getProjectName()));
                String string = new DatabaseHandler(EditAmbienceWall.this)
                        .getViewAreaOfProject(GlobalVariables.getProjectName());
                if (!string.equals("0")) {
                    int projectDrawArea = Integer.parseInt(new DatabaseHandler(
                            EditAmbienceWall.this)
                            .getViewAreaOfProject(GlobalVariables
                                    .getProjectName()));
                    // LayoutParams param = null;
                    // RelativeLayout.LayoutParams params = null;
                    if (thisDeviceDrawArea == projectDrawArea) {
                        LayoutParams param = new LayoutParams(width, height);
                        linearLayout.setLayoutParams(param);
                        linearLayout.setClickable(false);
                        linearLayout.setOnTouchListener(editView.layoutTouched);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                width, height);
                        params.leftMargin = left;
                        params.topMargin = top;

                        AmbienceEditView.LayoutDimensions dim = editView.new LayoutDimensions();
                        dim.x = left;
                        dim.y = top;
                        dim.width = width;
                        dim.height = height;
                        dim.selectedTile = selectedTileName;
                        dim.orientation = orientation;
                        dim.rot = rot;
                        dim.layRot = layRot;

                        linearLayout.setTag(dim);
                        linearLayout.setRotation(layRot);
                        rel.setClickable(true);
                        rel.addView(linearLayout, params);
                        linearLayout.bringToFront();
                    } else if (thisDeviceDrawArea > projectDrawArea) {
                        LayoutParams param = new LayoutParams(width * 2,
                                height * 2);
                        linearLayout.setLayoutParams(param);
                        linearLayout.setClickable(false);
                        linearLayout.setOnTouchListener(editView.layoutTouched);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                width * 2, height * 2);
                        params.leftMargin = left * 2;
                        params.topMargin = top * 2;

                        AmbienceEditView.LayoutDimensions dim = editView.new LayoutDimensions();
                        dim.x = left * 2;
                        dim.y = top * 2;
                        dim.width = width * 2;
                        dim.height = height * 2;
                        dim.selectedTile = selectedTileName;
                        dim.orientation = orientation;
                        dim.rot = rot;
                        dim.layRot = layRot;

                        linearLayout.setTag(dim);
                        linearLayout.setRotation(layRot);
                        rel.setClickable(true);
                        rel.addView(linearLayout, params);
                        linearLayout.bringToFront();
                    } else if (thisDeviceDrawArea < projectDrawArea) {
                        LayoutParams param = new LayoutParams(width / 2,
                                height / 2);
                        linearLayout.setLayoutParams(param);
                        linearLayout.setClickable(false);
                        linearLayout.setOnTouchListener(editView.layoutTouched);
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                width / 2, height / 2);
                        params.leftMargin = left / 2;
                        params.topMargin = top / 2;

                        AmbienceEditView.LayoutDimensions dim = editView.new LayoutDimensions();
                        dim.x = left / 2;
                        dim.y = top / 2;
                        dim.width = width / 2;
                        dim.height = height / 2;
                        dim.selectedTile = selectedTileName;
                        dim.orientation = orientation;
                        dim.rot = rot;
                        dim.layRot = layRot;

                        linearLayout.setTag(dim);
                        linearLayout.setRotation(layRot);
                        rel.setClickable(true);
                        rel.addView(linearLayout, params);
                        linearLayout.bringToFront();
                    }
                } else {
                    LayoutParams param = new LayoutParams(width, height);
                    linearLayout.setLayoutParams(param);
                    linearLayout.setClickable(false);
                    linearLayout.setOnTouchListener(editView.layoutTouched);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            width, height);
                    params.leftMargin = left;
                    params.topMargin = top;

                    AmbienceEditView.LayoutDimensions dim = editView.new LayoutDimensions();
                    dim.x = left;
                    dim.y = top;
                    dim.width = width;
                    dim.height = height;
                    dim.selectedTile = selectedTileName;
                    dim.orientation = orientation;
                    dim.rot = rot;
                    dim.layRot = layRot;

                    linearLayout.setTag(dim);
                    linearLayout.setRotation(layRot);
                    rel.setClickable(true);
                    rel.addView(linearLayout, params);
                    linearLayout.bringToFront();
                }
                // LayoutParams param = new LayoutParams(width, height);
                // linearLayout.setLayoutParams(param);
                // linearLayout.setClickable(false);
                // linearLayout.setOnTouchListener(drawView.layoutTouched);

				/*
				 * ImageView img = new ImageView(context);
				 * img.setBackgroundColor(Color.CYAN);
				 */
                // RelativeLayout.LayoutParams params = null;
                // if(thisDeviceDrawArea==projectDrawArea)
                // {
                // params = new RelativeLayout.LayoutParams(
                // width, height);
                // params.leftMargin = left;
                // params.topMargin = top;
                // }
                // else if(thisDeviceDrawArea>projectDrawArea)
                // {
                // params = new RelativeLayout.LayoutParams(
                // width, height);
                // params.leftMargin = left*2;
                // params.topMargin = top*2;
                // }
                // else if(thisDeviceDrawArea<projectDrawArea)
                // {
                // params = new RelativeLayout.LayoutParams(
                // width, height);
                // params.leftMargin = left/2;
                // params.topMargin = top/2;
                // }
                // RelativeLayout.LayoutParams params = new
                // RelativeLayout.LayoutParams(
                // width, height);
                // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
                // params.leftMargin = left;
                // params.topMargin = top;
                // DrawView.LayoutDimensions dim = drawView.new
                // LayoutDimensions();
                // int
                // thisDeviceDrawArea=GlobalVariables.getDrawArea(PatternLibraryActivity.this);
                // int projectDrawArea=Integer.parseInt(new
                // DatabaseHandler(PatternLibraryActivity.this).getViewAreaOfProject(GlobalVariables.getProjectName()));
                // if(thisDeviceDrawArea==projectDrawArea)
                // {
                // dim.x = left;
                // dim.y = top;
                // dim.width = width;
                // dim.height = height;
                // dim.selectedTile = selectedTileName;
                // dim.orientation = orientation;
                // dim.rot=rot;
                // linearLayout.setTag(dim);
                // linearLayout.setRotation(rot);
                // rel.setClickable(true);
                // rel.addView(linearLayout, params);
                // linearLayout.bringToFront();
                // }
                // else if(thisDeviceDrawArea>projectDrawArea)
                // {
                // dim.x = left*2;
                // dim.y = top*2;
                // dim.width = width*2;
                // dim.height = height*2;
                // dim.selectedTile = selectedTileName;
                // dim.orientation = orientation;
                // dim.rot=rot;
                // linearLayout.setTag(dim);
                // linearLayout.setRotation(rot);
                // rel.setClickable(true);
                // rel.addView(linearLayout, params);
                // linearLayout.bringToFront();
                // }
                // else if(thisDeviceDrawArea<projectDrawArea)
                // {
                // dim.x = left/2;
                // dim.y = top/2;
                // dim.width = width/2;
                // dim.height = height/2;
                // dim.selectedTile = selectedTileName;
                // dim.orientation = orientation;
                // dim.rot=rot;
                // linearLayout.setTag(dim);
                // linearLayout.setRotation(rot);
                // rel.setClickable(true);
                // rel.addView(linearLayout, params);
                // linearLayout.bringToFront();
                // }

                // linearLayout.setTag(dim);
                // linearLayout.setRotation(rot);
                // rel.setClickable(true);
                // rel.addView(linearLayout, params);
                // linearLayout.bringToFront();
                final String filePath = selectedTileName;
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    Bitmap rBmp = RotateBitmapRight(bitmap, 90 * orientation);
                    // bmp.recycle();

                    // imgV.setImageBitmap(bmp);
                    editView.setSelectedTile(rBmp, filePath, orientation,
                            tileSize);
                    editView.fillTileInLayout(linearLayout, true, rBmp, groovesOn, rot, selectedTileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {

        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

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

}
