package com.nagainfo.smartShowroom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nagainfo.database.DatabaseHandler;
import com.nagainfo.slider.PatternGridAdapter;
import com.nagainfo.smartShowroom.LayeredImageView.Layer;
import com.nagainfo.sromku.polygon.Polygon;
import com.nagainfo.update.PatternimgNameInterface;
import com.nagainfo.utils.FileUtils;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;

public class AmbienceKitchen extends Activity implements OnClickListener,
        PatternimgNameInterface {

    private Layer layerFloor, layerFurniture, layerCenterWall, layerRightWall,
            layerCentreSlab,  layerRightSlab,layerRoof;
    private Layer layerFloorCabin01, layerFloorCabin02, layerFloorCabin03,
            layerFloorCabin04, layerFloorCabin05, layerFloorCabin06,
            layerFloorCabin07, layerFloorCabin08, layerFloorCabin09,
            layerFloorCabin10, layerFloorCabin11;
    private Layer layerWallCabin01, layerWallCabin02, layerWallCabin03,
            layerWallCabin04, layerWallCabin05, layerWallCabin06,
            layerWallCabin07, layerWallCabin08, layerWallCabin09,
            layerWallCabin10;
    private Layer layerMask;
    private Bitmap floorBitmap, furnitureBitmap, centerWallBitmap,
            rightWallBitmap, slabBitmap, roofBitmap;
    private Bitmap wallCabinBitmap01, wallCabinBitmap02, wallCabinBitmap10;

    private Bitmap maskBitmap;

    private Matrix floorCabin1, floorCabin2, floorCabin3, floorCabin4,
            floorCabin5, floorCabin6, floorCabin7, floorCabin8, floorCabin9,
            floorCabin10, floorCabin11;

    private Matrix wallCabin1, wallCabin2, wallCabin3, wallCabin4, wallCabin5,
            wallCabin6, wallCabin7, wallCabin8, wallCabin9, wallCabin10;

    private ProgressDialog fillProgress;
    private LayeredImageView backImg;
    private RelativeLayout layout;
    private DrawerLayout leftDrawer;
    private LayoutInflater inflater;
    private View patternContent2;
    private EditText eText;
    private Button filterSearch_button;
    private View patternContent;
    private Button filterSearchButton2;
    private LinearLayout patternScrollView;
    private RelativeLayout main_layout;
    private LinearLayout content_slider;
    private RelativeLayout patternHeader;
    private Object previewDialog;
    private ActionBarDrawerToggle mDrawerToggle;
    private PhotoViewAttacher mAttacher;

    private float scaleFactor;
    private Float density;

    private Boolean furnitureVisible = true;
    private BitmapDrawable furnitureDrawable, maskDrawable;
    private BitmapDrawable wallCabinDrawable01, wallCabinDrawable02,
            wallCabinDrawable03, wallCabinDrawable04, wallCabinDrawable05,
            wallCabinDrawable06, wallCabinDrawable07, wallCabinDrawable08,
            wallCabinDrawable09, wallCabinDrawable10;
    private BitmapDrawable floorCabinDrawable01, floorCabinDrawable02,
            floorCabinDrawable03, floorCabinDrawable04, floorCabinDrawable05,
            floorCabinDrawable06, floorCabinDrawable07, floorCabinDrawable08,
            floorCabinDrawable09, floorCabinDrawable10, floorCabinDrawable11;
    private BitmapDrawable centreSlabDrawable, rightSlabDrawable;

    private GridView grid;
    private static final String KEY_BRAND_NAME = "pro_brand";
    private static final String KEY_DIMEN = "pro_dimen";
    private static final String KEY_COLOR = "pro_color";
    private static final String KEY_TYPE = "pro_type";
    private static final String KEY_COMPANY = "pro_company";
    private List<Integer> selectedBrands = new ArrayList<Integer>();
    private List<Integer> selectedSize = new ArrayList<Integer>();
    private List<Integer> selectedColor = new ArrayList<Integer>();
    private List<Integer> selectedType = new ArrayList<Integer>();
    private List<Integer> selectedCompany = new ArrayList<Integer>();
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

    private String currentProject;
    private String screenshotLocation;
    private String savedFolderName;
    private String viewType;

    private ProgressDialog dialog;
    private Handler hand = new Handler();

    private Boolean customEdit = false;
    private Boolean groovesOn = true;

    private Bitmap nothing;
    private RelativeLayout viewScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ambience_drawingroom);
        OpenCVLoader.initDebug();

        initialise();
        loadTile();
        clearTempFiles();

        currentProject = "Ambience";// GlobalVariables.getProjectName();
        savedFolderName = "Ambience Saved Views";
        viewType = "Kitchen01";
        screenshotLocation = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + "/SmartShowRoom/"
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
        m.preScale(scaleFactor, scaleFactor);

        int height = dm.heightPixels;
        int width = (int) ((985 / 693) * height * 1.1699);

        layout.setLayoutParams(new RelativeLayout.LayoutParams(width, height
                - (int) (85 * density)));

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
                        new com.nagainfo.sromku.polygon.Point(
                                116 / scaleFactor, 271 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                560 / scaleFactor, 271 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                785 / scaleFactor, 340 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                785 / scaleFactor, 394 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                716 / scaleFactor, 394 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                499 / scaleFactor, 290 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(86 / scaleFactor,
                                288 / scaleFactor)).build();

        final Polygon floorPolygon = Polygon
                .Builder()
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
                                431 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                509 / scaleFactor, 431 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                644 / scaleFactor, 586 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
                                586 / scaleFactor)).build();

        final Polygon centreWallPolygon = Polygon
                .Builder()
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
                                67 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
                                77 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(86 / scaleFactor,
                                77 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                104 / scaleFactor, 85 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                195 / scaleFactor, 85 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                205 / scaleFactor, 97 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                205 / scaleFactor, 210 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                118 / scaleFactor, 210 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                116 / scaleFactor, 271 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                560 / scaleFactor, 271 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                560 / scaleFactor, 214 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                289 / scaleFactor, 210 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                290 / scaleFactor, 86 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                235 / scaleFactor, 88 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                560 / scaleFactor, 77 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                560 / scaleFactor, 67 / scaleFactor)).build();

        final Polygon rightWallPolygon = Polygon
                .Builder()
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                560 / scaleFactor, 214 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                560 / scaleFactor, 271 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                785 / scaleFactor, 388 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                785 / scaleFactor, 233 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                684 / scaleFactor, 223 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                680 / scaleFactor, 192 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                613 / scaleFactor, 192 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                613 / scaleFactor, 215 / scaleFactor)).build();

        final Polygon cabinPolygon01 = Polygon
                .Builder()
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(0 / scaleFactor,
                                85 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                196 / scaleFactor, 94 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                194 / scaleFactor, 202 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(82 / scaleFactor,
                                205 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(82 / scaleFactor,
                                294 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                496 / scaleFactor, 298 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                706 / scaleFactor, 400 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                706 / scaleFactor, 586 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                667 / scaleFactor, 580 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                503 / scaleFactor, 413 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                177 / scaleFactor, 409 / scaleFactor)).build();

        final Polygon cabinPolygon02 = Polygon
                .Builder()
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                295 / scaleFactor, 94 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                534 / scaleFactor, 98 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                784 / scaleFactor, 0 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                784 / scaleFactor, 217 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                691 / scaleFactor, 211 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                692 / scaleFactor, 182 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                608 / scaleFactor, 185 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                610 / scaleFactor, 203 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                542 / scaleFactor, 203 / scaleFactor))
                .addVertex(
                        new com.nagainfo.sromku.polygon.Point(
                                295 / scaleFactor, 202 / scaleFactor)).build();

        layout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                com.nagainfo.sromku.polygon.Point touch = new com.nagainfo.sromku.polygon.Point(
                        event.getX(), event.getY());

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case (MotionEvent.ACTION_DOWN):

                        if (tileSelected) {

                            customEdit = false;

                            if (floorPolygon.contains(touch)) {

                                dialog = ProgressDialog.show(AmbienceKitchen.this,
                                        "", "Loading...", true, false);
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
                                                if (dialog.isShowing())
                                                    dialog.dismiss();

                                            }

                                        });
                                    }

                                });

                                thread.start();

                            } else if (centreWallPolygon.contains(touch)) {

                                dialog = ProgressDialog.show(AmbienceKitchen.this,
                                        "", "Loading...", true, false);
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
                                                if (dialog.isShowing())
                                                    dialog.dismiss();

                                            }

                                        });
                                    }

                                });

                                thread.start();

                            } else if (rightWallPolygon.contains(touch)) {

                                dialog = ProgressDialog.show(AmbienceKitchen.this,
                                        "", "Loading...", true, false);
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
                                                if (dialog.isShowing())
                                                    dialog.dismiss();

                                            }

                                        });
                                    }

                                });

                                thread.start();

                            } else if (slabPolygon.contains(touch)) {

                                dialog = ProgressDialog.show(AmbienceKitchen.this,
                                        "", "Loading...", true, false);
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
                                                if (dialog.isShowing())
                                                    dialog.dismiss();

                                            }

                                        });
                                    }



                                });

                                thread.start();

                            } else if (cabinPolygon01.contains(touch)
                                    || cabinPolygon02.contains(touch)) {
                                replaceCabin();
                            }

                        } else if (customEdit) {
                            // if (doubleClick) {
                            tileSelected = false;
                            if (floorPolygon.contains(touch)) {
                                Intent intent = new Intent(AmbienceKitchen.this,
                                        EditAmbienceWall.class);
                                intent.putExtra("Height", floorHeight + "");
                                intent.putExtra("Width", floorWidth + "");
                                intent.putExtra("FileName", "floor");
                                intent.putExtra("Ratio", "1.5");
                                startActivityForResult(intent, FLOOR_REQUEST_CODE);
                            } else if (centreWallPolygon.contains(touch)) {
                                Intent intent = new Intent(AmbienceKitchen.this,
                                        EditAmbienceWall.class);
                                intent.putExtra("Height", centreWallHeight + "");
                                intent.putExtra("Width", centreWallWidth + "");
                                intent.putExtra("FileName", "centreWall");
                                intent.putExtra("Ratio", "1.5");
                                startActivityForResult(intent,
                                        CENTRE_WALL_REQUEST_CODE);
                            } else if (rightWallPolygon.contains(touch)) {
                                Intent intent = new Intent(AmbienceKitchen.this,
                                        EditAmbienceWall.class);
                                intent.putExtra("Height", rightWallHeight + "");
                                intent.putExtra("Width", rightWallWidth + "");
                                intent.putExtra("FileName", "rightWall");
                                intent.putExtra("Ratio", "1");
                                startActivityForResult(intent,
                                        RIGHT_WALL_REQUEST_CODE);
                            }

                            // }

                        }

                        return true;
                    case (MotionEvent.ACTION_MOVE):

                        return true;
                    case (MotionEvent.ACTION_UP):
                        customEdit = false;
                        return true;
                    case (MotionEvent.ACTION_CANCEL):

                        return true;
                    case (MotionEvent.ACTION_OUTSIDE):

                        return true;

                    default:
                        break;

                }

                return true;
            }
        });



    }

//    class FindViewSize extends AsyncTask<Void, Integer, Integer> {
//
//        RelativeLayout viewLayout;
//
//        public FindViewSize(RelativeLayout viewLayout) {
//            this.viewLayout = viewLayout;
//        }
//
//        @Override
//        protected Integer doInBackground(Void... params) {
//            Integer height = new Integer(viewLayout.getHeight()-getActionBar().getHeight());
//
//
//
//            return height;
//        }
//
//
//        @Override
//        protected void onPostExecute(Integer height) {
//
//            if (height>0) {
//                int heightToSet = backImg.getHeight();
//                int widthToSet = heightToSet * (nothing.getWidth() / nothing.getHeight());
//                layout.setLayoutParams(new RelativeLayout.LayoutParams(widthToSet, heightToSet
//                ));
//                return;
//            } else {
//                doInBackground();
//            }
//        }
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void replaceCabin() {
        // TODO Auto-generated method stub

        dialog = ProgressDialog.show(AmbienceKitchen.this, "", "Loading...",
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
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        clearTempFiles();

        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        layout.setOnTouchListener(null);
        System.gc();
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
            Bitmap wallBitmap = EditAmbienceWall.bitmap;
            BitmapDrawable draw = new BitmapDrawable(getResources(),
                    warpRightWall(wallBitmap));
            wallBitmap.recycle();
            wallBitmap = null;

            layerRightWall = backImg.addLayer(3, draw, rightWall);

        } else if (requestCode == FLOOR_REQUEST_CODE) {
            backImg.removeLayer(layerFloor);
            layerFloor = null;
            Bitmap floorBitmap = EditAmbienceWall.bitmap;
            BitmapDrawable draw = new BitmapDrawable(getResources(),
                    warpFloor(floorBitmap));
            floorBitmap.recycle();
            floorBitmap = null;

            layerFloor = backImg.addLayer(0, draw, floor);

        } else if (requestCode == CENTRE_WALL_REQUEST_CODE) {
            backImg.removeLayer(layerCenterWall);
            layerCenterWall = null;
            Bitmap floorBitmap = EditAmbienceWall.bitmap;
            BitmapDrawable draw = new BitmapDrawable(getResources(),
                    warpCentreWall(floorBitmap));
            floorBitmap.recycle();
            floorBitmap = null;

            layerCenterWall = backImg.addLayer(2, draw, centreWall);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearTempFiles() {
        String currentProject = "Ambience";// GlobalVariables.getProjectName();

        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/SmartShowRoom/" + currentProject + "/";
        DatabaseHandler.deleteAmbienceFiles(path);
    }

//    private void showToast(String string, int length) {
//        Toast.makeText(getApplicationContext(), string, length).show();
//    }

    private void initialise() {

        fillProgress = new ProgressDialog(AmbienceKitchen.this);
        fillProgress.setMessage("Rendering...");
        fillProgress.setCanceledOnTouchOutside(false);

        backImg = (LayeredImageView) findViewById(R.id.imageView);
        layout = (RelativeLayout) findViewById(R.id.viewV);
        viewScreen = (RelativeLayout)findViewById(R.id.viewArea);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        leftDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        patternContent2 = inflater.inflate(R.layout.layout_patters, null);
        eText = (EditText) patternContent2.findViewById(R.id.textSearch);

        filterSearch_button = (Button) patternContent2
                .findViewById(R.id.filterButton);
        filterSearch_button.setOnClickListener(AmbienceKitchen.this);

        Button search = (Button) patternContent2
                .findViewById(R.id.searchButton);

        search.setOnClickListener(AmbienceKitchen.this);

        patternContent = inflater.inflate(R.layout.layout_filter, null);
        filterSearchButton2 = (Button) patternContent
                .findViewById(R.id.filterButton2);

        filterSearchButton2.setOnClickListener(AmbienceKitchen.this);

        patternScrollView = (LinearLayout) findViewById(R.id.patternScrollView);
        main_layout = (RelativeLayout) findViewById(R.id.main_content);
        content_slider = (LinearLayout) findViewById(R.id.content_slider);
        patternHeader = (RelativeLayout) findViewById(R.id.patternHeader);

        previewDialog = createPreviewDialog();

        mDrawerToggle = new ActionBarDrawerToggle(this, leftDrawer,
                R.drawable.ic_drawer, R.string.ambience_drawer_open,
                R.string.ambience_drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("Smart Showroom");
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
                // clearAllViews();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Select Tiles");
                // loadTile();
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()

            }
        };

        leftDrawer.setDrawerListener(mDrawerToggle);
    }

    final float slabHeightFinal = (float) 1.2;
    final float centreSlabWidth = (float) 6;
    final float rightSlabWidth = (float) 4;

    private Bitmap fillTilesOnCabin(Bitmap tile, float height, float width) {
        // included only for example sake

        int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen.this);
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

        int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen.this);
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

        int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen.this);
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

        int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen.this);
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

        int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen.this);
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

        int viewArea = GlobalVariables.getDrawArea(AmbienceKitchen.this);
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

    protected void loadTile() {
        patternScrollView.removeAllViews();

        grid = (GridView) patternContent2.findViewById(R.id.slider_pattern);
        patternScrollView.addView(patternContent2);
        getAllpattern();

    }

    private Dialog createPreviewDialog() {
        Dialog dialog = new Dialog(AmbienceKitchen.this);
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

    private void getAllpattern() {
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        ArrayList<HashMap<String, String>> dbResult = new ArrayList<HashMap<String, String>>();

        dbResult = db.getAllPattern();

        PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
                AmbienceKitchen.this);
        grid.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        // grid.setAdapter(adapter);
        grid.setAdapter(null);
        grid.setAdapter(pgAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Matrix m = new Matrix();

        density = dm.density;
        scaleFactor = (1 / (float) density.intValue());
        m.preScale(scaleFactor, scaleFactor);

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.furnitureVisiblity:
                if (!furnitureVisible) {

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

                    layerWallCabin01 = backImg.addLayer(16, wallCabinDrawable01,
                            wallCabin1);
                    layerWallCabin02 = backImg.addLayer(17, wallCabinDrawable02, m);
                    layerWallCabin03 = backImg.addLayer(18, wallCabinDrawable03,
                            wallCabin3);
                    layerWallCabin04 = backImg.addLayer(19, wallCabinDrawable04,
                            wallCabin4);
                    layerWallCabin05 = backImg.addLayer(20, wallCabinDrawable05,
                            wallCabin5);
                    layerWallCabin06 = backImg.addLayer(21, wallCabinDrawable06,
                            wallCabin6);
                    layerWallCabin07 = backImg.addLayer(22, wallCabinDrawable07,
                            wallCabin7);
                    layerWallCabin08 = backImg.addLayer(23, wallCabinDrawable08,
                            wallCabin8);
                    layerWallCabin09 = backImg.addLayer(24, wallCabinDrawable09,
                            wallCabin9);
                    layerWallCabin10 = backImg.addLayer(25, wallCabinDrawable10, m);

                    layerCentreSlab = backImg.addLayer(26, centreSlabDrawable,
                            centreSlab);
                    layerRightSlab = backImg.addLayer(27, rightSlabDrawable,
                            rightSlab);
                    layerFurniture = backImg.addLayer(28, furnitureDrawable, m);

                    furnitureVisible = true;

                    // item.setTitle("Hide Furniture");
                    invalidateOptionsMenu();
                } else {
                    backImg.removeLayer(layerMask);
                    backImg.removeLayer(layerFurniture);
                    backImg.removeLayer(layerWallCabin01);
                    backImg.removeLayer(layerWallCabin02);
                    backImg.removeLayer(layerWallCabin03);
                    backImg.removeLayer(layerWallCabin04);
                    backImg.removeLayer(layerWallCabin05);
                    backImg.removeLayer(layerWallCabin06);
                    backImg.removeLayer(layerWallCabin07);
                    backImg.removeLayer(layerWallCabin08);
                    backImg.removeLayer(layerWallCabin09);
                    backImg.removeLayer(layerWallCabin10);
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
                    backImg.removeLayer(layerCentreSlab);
                    backImg.removeLayer(layerRightSlab);

                    layerWallCabin01 = null;
                    layerWallCabin02 = null;
                    layerWallCabin03 = null;
                    layerWallCabin04 = null;
                    layerWallCabin05 = null;
                    layerWallCabin06 = null;
                    layerWallCabin07 = null;
                    layerWallCabin08 = null;
                    layerWallCabin09 = null;
                    layerWallCabin10 = null;

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

                    layerMask = null;
                    layerFurniture = null;
                    layerCentreSlab = null;
                    layerRightSlab = null;

                    furnitureVisible = false;
                    // item.setTitle("Show Furniture");
                    invalidateOptionsMenu();
                }
                break;

            case R.id.as_image:

                if (FileUtils.isStorageWritable(AmbienceKitchen.this)) {
                    if (FileUtils.isMemoryAvailable(AmbienceKitchen.this)) {
                        screenShotPreview();
                    }
                }


                break;

            case R.id.custom_edit:

                tileSelected = false;
                customEdit = true;

                break;

            case R.id.grooveSwap:

                if (groovesOn) {
                    groovesOn = false;
                    invalidateOptionsMenu();
                } else {
                    groovesOn = true;
                    invalidateOptionsMenu();
                }

                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ambience_menu, menu);

        if (!furnitureVisible)
            menu.getItem(0).setTitle("Show Furniture");
        else
            menu.getItem(0).setTitle("Hide Furniture");

        if (groovesOn) {
            menu.getItem(2).setTitle("GROOVES ON");
        } else {
            menu.getItem(2).setTitle("GROOVES OFF");
        }

        return true;
    }

    private void screenShotPreview() {

        layout.invalidate();
        layout.setDrawingCacheEnabled(true);
        final Bitmap previewBitmap = layout.getDrawingCache();
        final Dialog previewDialog = new Dialog(AmbienceKitchen.this);
        previewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        previewDialog.setTitle(null);
        previewDialog.setContentView(R.layout.ambience_screenshot_preview);
        Button discard = (Button) previewDialog.findViewById(R.id.discard_shot);
        Button save = (Button) previewDialog.findViewById(R.id.save_shot);
        ImageView preview = (ImageView) previewDialog
                .findViewById(R.id.preview);
        preview.setImageBitmap(previewBitmap);

        discard.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                previewDialog.dismiss();
            }
        });
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                showFileNameDialog(previewBitmap);

                previewDialog.dismiss();

            }
        });
        previewDialog.show();

    }

    private String fileSaveName = "Default";

    private void showFileNameDialog(final Bitmap previewBitmap) {
        final Dialog fileNameDialog = new Dialog(AmbienceKitchen.this);
        fileNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fileNameDialog.setContentView(R.layout.enter_save_name);
        Button ok = (Button) fileNameDialog.findViewById(R.id.ok_button);
        final EditText fileNameText = (EditText) fileNameDialog
                .findViewById(R.id.fileName);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        final String time = sdf.format(new Date());

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                fileSaveName = fileNameText.getText().toString().trim();

                try {

                    if (fileSaveName.length() > 0) {
                        File file = new File(screenshotLocation);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (fileSaveName.equals("Default")) {
                            file = new File(screenshotLocation + "/" + "View at "
                                    + time + ".png");
                        } else {
                            file = new File(screenshotLocation + "/" + fileSaveName
                                    + " " + time + ".png");
                        }

                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        try {
                            FileOutputStream fout = new FileOutputStream(file);
                            previewBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                    fout);
                            fout.flush();
                            fout.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            FileUtils.writeErrorMessageToFile(e, "Kitchen Save Error-IOException");
                        } catch (Exception e) {
                            FileUtils.writeErrorMessageToFile(e, "Kitchen Save Error");
                        }

                        fileNameDialog.dismiss();
                        showLocationDialog(screenshotLocation);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Field cannot be left Blank", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    FileUtils.writeErrorMessageToFile(e, "Kitchen Save Error");
                }
            }
        });
        fileNameDialog.show();
    }

    private void showLocationDialog(String myDir) {
        final Dialog d = new Dialog(AmbienceKitchen.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.saved_location);
        TextView tv = (TextView) d.findViewById(R.id.save_loc);
        Button ok = (Button) d.findViewById(R.id.ok_button);
        tv.setText("View Saved at: " + myDir);

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                d.dismiss();
            }
        });
        d.show();
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
        dbResult = db.searchkeyword(keyString);
        if (dbResult.size() > 0) {
            PatternGridAdapter pgAdapter = new PatternGridAdapter(this,
                    dbResult, AmbienceKitchen.this);
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

    private void filterSelected() {
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

    private void addbrandCheckBox(View patternContent) {
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

    private void addCompanyCheckBox(View patternContent) {
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
                    Log.e("company checkd itm err", "");
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
                    Log.e("brand checked item err", "");
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

    private void addSizeCheckBox(View v) {
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

    private void addColorCheckBox(View v) {
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

    private void addTypeCheckBox(View v) {
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

            grid = (GridView) patternContent2.findViewById(R.id.slider_pattern);
            patternScrollView.removeAllViews();
            patternScrollView.addView(patternContent2);
            showPattern(filterResult);
        }

        // System.out.println("test");

    }

    private void showPattern(ArrayList<HashMap<String, String>> dbResult) {

        PatternGridAdapter pgAdapter = new PatternGridAdapter(this, dbResult,
                AmbienceKitchen.this);
        grid.refreshDrawableState();
        pgAdapter.notifyDataSetChanged();
        // grid.setAdapter(adapter);
        grid.setAdapter(null);
        grid.setAdapter(pgAdapter);

    }

    private String getStringFromCheckedList(
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
                    Log.e("getStringFromCheckedLst", e.getMessage());
                    return null;

                }
            }
        }
        return resString;

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

    @Override
    public void patternName(String path, String tileSize, String brand,
                            String type) {

        if (tileSize != null) {

            String[] tileDim = tileSize.split("x");
            if (tileDim.length == 0) {
                tileDim = tileSize.split("X");
            }
            tileHeight = Float.valueOf(tileDim[0]);
            tileWidth = Float.valueOf(tileDim[1]);

        }
        // TODO Auto-generated method stub

        // System.out.println("hai");
        final String filePath = path;
        bmp = BitmapFactory.decodeFile(filePath);
        // drawToolSelected = false;
        // editView.isDrawingSelected(drawToolSelected);
        // editView.selectTile(true);
        this.tileSize = tileSize;
        final Dialog dialog = new Dialog(AmbienceKitchen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setCanceledOnTouchOutside(false);
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
                bitmap = bmp;
                // editView.setSelectedTile(bmp, filePath, axis, tileSize);
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
                bitmap = bmp;
                // editView.setSelectedTile(bmp, filePath, axis, tileSize);
                // rBmp.recycle();
                // imgV.setImageBitmap(RotateBitmapRight(bmp, 90));
            }
        });

        dialog.show();

        // editView.setSelectedTile(bmp, filePath, axis, tileSize);
        axis = 0;

        bitmap = bmp;
        path = filePath;

        tileSelected = true;

    }

    private static Bitmap RotateBitmapRight(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    String tileSize;
    Bitmap bitmap;
    String path;
    int axis = 0;
    float tileHeight;
    float tileWidth;
    Boolean tileSelected = false;
    Bitmap bmp;

}
