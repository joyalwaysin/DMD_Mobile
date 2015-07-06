package com.nagainfo.smartShowroom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.nagainfo.database.DatabaseHandler;

import java.io.File;
import java.io.IOException;

public class GlobalVariables {

    //public static final int drawArea = 512;

    static String baseUrl = "http://saifeesmartshowroom.com/en/";//live
//	 static String baseUrl = "http://crossrangetalk.com/en/";//test

    static String apiURL = "http://saifeesmartshowroom.com/api/";//live
//	 static String apiURL = "http://crossrangetalk.com/api/";//test

    static String CountryFlagdPath = baseUrl + "assets/images/flags/";
    static String projectName = null;
    static String finalUnit = null;

    static String companyUrlPath = baseUrl + "uploads/companies/";
    static String activateUrl = apiURL + "/appStatus?appId=";

    static String downloadPath = baseUrl + "uploads/companies/";
    static String offerDownloadPath = baseUrl + "uploads/offers/";
    static String imageRoot = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/SmartShowRoom/";

    static final String NOMEDIA = ".nomedia";

    static float wallHeightInMM;
    static float wallWidthInMM;
    static float wallLengthInMM;
    static float wallCInMM;
    static float wallDInMM;

    static Boolean isAmbienceActivated = false;
    static Boolean is3DActivated = false;
    static Boolean isCamClickActivated = false;
    static Boolean isHelpActivated = false;

    static String featureLockedMessage = "Smart Showroom is working on LITE Version. To activate PRO version contact Smart Showroom team.";
    static String helpLockedMessage = "Contact This Application Provider for Help";

    static Boolean actChecked = false;

    static Boolean groovesOn = true;

    // static String downloadPath =
    // "http://192.168.0.110/saifee/uploads/brands/";
    // static final String apiURL = "http://192.168.0.110:80/api/";

    @SuppressLint("NewApi")
    public static int getDrawArea(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = size.y;
        int width = size.x;

        if (height >= 1120 && width >= 1600) {
            return 1024;
        } else {
            return 512;
        }

    }

    public static Bitmap getRotatedPattern(Bitmap tile, float tileWidth,
                                           float tileHeight, float wallWidth, float wallHeight, float rot,
                                           Context context, Boolean groovesOn) {

        // Get View Size
        int viewWidth;
        int viewHeight;

        float ratio = wallWidth / wallHeight;

        if (ratio > 1) {
            ratio = 1 / ratio;

            viewWidth = GlobalVariables.getDrawArea(context);
            viewHeight = (int) (viewWidth * ratio);

        } else {

            viewHeight = GlobalVariables.getDrawArea(context);
            viewWidth = (int) (viewHeight * ratio);
        }


        // Dimension of smallest square containing wall
        int diagonal = (int) Math.sqrt((viewWidth * viewWidth)
                + (viewHeight * viewHeight));

        // Dimension of canvas
        Bitmap outputBitmap = Bitmap.createBitmap((diagonal), (diagonal),
                Bitmap.Config.ARGB_8888);
        float left = 0, top = 0;
        int tileHeightPixels = (int) tileHeight;
        int tileWidthPixels = (int) tileWidth;
        float outerWallWidthPixels = diagonal;
        float outerWallHeightPixels = diagonal;
        float innerWallWidthPixels = viewWidth;
        float innerWallHeigthPixels = viewHeight;

        // Create canvas
        Canvas canvas = new Canvas(outputBitmap);

        // Bitmap bit = Bitmap.createScaledBitmap(tile, tilew, dstHeight,
        // filter)
        canvas.rotate(rot, (int) (diagonal / 2), (int) (diagonal / 2));


        // Orientation correction
        if (tile.getWidth() > tile.getHeight()) {

            if (tileWidth > tileHeight) {
                tileHeightPixels = (int) (((float) tileHeight / wallWidth) * innerWallWidthPixels);
                tileWidthPixels = (int) (((float) tileWidth / wallWidth) * innerWallWidthPixels);
            } else {
                tileHeightPixels = (int) (((float) tileWidth / wallWidth) * innerWallWidthPixels);
                tileWidthPixels = (int) (((float) tileHeight / wallWidth) * innerWallWidthPixels);
            }

        } else {

            if (tileWidth > tileHeight) {

                tileHeightPixels = (int) (((float) tileWidth / wallWidth) * innerWallWidthPixels);
                tileWidthPixels = (int) (((float) tileHeight / wallWidth) * innerWallWidthPixels);

            } else {
                tileHeightPixels = (int) (((float) tileHeight / wallWidth) * innerWallWidthPixels);
                tileWidthPixels = (int) (((float) tileWidth / wallWidth) * innerWallWidthPixels);
            }

        }

        tile = Bitmap.createScaledBitmap(tile, tileWidthPixels,
                tileHeightPixels, true);

        //Grooves
        @SuppressWarnings("deprecation")
        NinePatchDrawable npd = (NinePatchDrawable) context.getResources().getDrawable(
                R.drawable.imgbckgnd);

        // Tile Filling
        while (left < outerWallWidthPixels) {

            while (top < outerWallHeightPixels) {
                canvas.drawBitmap(tile, left, top, null);
                Rect npdBounds = new Rect((int) left, (int) top,
                        (int) (left + tileWidthPixels),
                        (int) (top + tileHeightPixels));
                npd.setBounds(npdBounds);
                if (groovesOn) npd.draw(canvas);

                top += tileHeightPixels;
            }
            left += tileWidthPixels;
            top = 0;
        }

        // Cropping
        int cropWidth, cropHeight, cropLeft, cropTop;

        cropLeft = (int) Math
                .round(((outputBitmap.getWidth() - (innerWallWidthPixels)) / 2));
        cropTop = (int) Math
                .round(((outputBitmap.getHeight() - (innerWallHeigthPixels)) / 2));
        cropWidth = (int) (innerWallWidthPixels);
        cropHeight = (int) (innerWallHeigthPixels);

        // final output
        outputBitmap = Bitmap.createBitmap(outputBitmap, cropLeft, cropTop,
                cropWidth, cropHeight);
        return outputBitmap;
    }

    public static BitmapDrawable bitmapToDrawable(Bitmap bitmap, Resources r) {
        BitmapDrawable drawable = new BitmapDrawable(r, bitmap);

        return drawable;
    }

    public static void setGroovesOn() {
        groovesOn = true;
    }

    public static void setGroovesOff() {
        groovesOn = false;

    }

    public static Boolean getGrooveStatus() {

        return groovesOn;
    }

    public static void saveProject(Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        db.insertProject(projectName, finalUnit, wallLengthInMM + "",
                wallWidthInMM + "", wallHeightInMM + "", wallCInMM + "",
                wallDInMM + "", getDrawArea(context) + "");
    }

    public static void setActChecked() {
        actChecked = true;
    }

    public static Boolean getActCheckStatus() {

        return actChecked;
    }

    public static void createNomediafile(String path) {

        File nomediaFile = new File(path + NOMEDIA);
        if (!nomediaFile.exists()) {
            try {
                nomediaFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    public static void setSettingActivated(String setting, int value) {

        if (setting.equals("ambience")) {
            setAmbienceActivated(value);
        } else if (setting.equals("3d")) {
            set3DActivated(value);
        } else if (setting.equals("camclick")) {
            setCamClickActivated(value);
        } else if (setting.equals("help")) {
            setHelpActivated(value);
        }

    }

    public static void setAmbienceActivated(int value) {
        if (value == 0) {
            isAmbienceActivated = false;
        } else if (value == 1) {
            isAmbienceActivated = true;
        }

    }

    public static void set3DActivated(int value) {
        if (value == 0) {
            is3DActivated = false;
        } else if (value == 1) {
            is3DActivated = true;
        }

    }

    public static void setCamClickActivated(int value) {
        if (value == 0) {
            isCamClickActivated = false;
        } else if (value == 1) {
            isCamClickActivated = true;
        }

    }

    public static void setHelpActivated(int value) {
        if (value == 0) {
            isHelpActivated = false;
        } else if (value == 1) {
            isHelpActivated = true;
        }
    }

    public static Boolean getAmbienceActivationStatus() {

        return isAmbienceActivated;
    }

    public static Boolean get3DActivationStatus() {

        return is3DActivated;
    }

    public static Boolean getCamClickActivationStatus() {

        return isCamClickActivated;
    }

    public static Boolean getHelpActivationStatus() {

        return isHelpActivated;
    }


    public static float getWallHeight() {
        return wallHeightInMM;
    }

    public static float getWallWidth() {
        return wallWidthInMM;
    }

    public static float getWallLength() {
        return wallLengthInMM;
    }

    public static float getWallC() {
        return wallCInMM;
    }

    public static float getWallD() {
        return wallDInMM;
    }

    public static void setWallDim(float h, float w, float l, float c, float d) {
        wallHeightInMM = h;
        wallLengthInMM = l;
        wallWidthInMM = w;
        wallCInMM = c;
        wallDInMM = d;
    }

    public static String getProjectName() {
        return projectName;
    }

    public static void setProjectName(String projectname) {
        projectName = projectname;

    }

    public static void setUnit(String unit) {
        finalUnit = unit;
        // Log.i("Unit set",finalUnit);

    }

    public static String getUnit() {
        return finalUnit;
    }

    public static String getDownloadPath() {
        return downloadPath;

    }

    public static String getApiPath() {
        return apiURL;
    }

    public static String getPatternRootPath() {
        return imageRoot;
    }

    public static String getOfferImagePath() {
        return offerDownloadPath;
    }

    public static String getCompanyImagePath() {
        return companyUrlPath;
    }

    public static String getUniqueId(Context context) {
        String android_id = Secure.getString(context.getContentResolver(),
                Secure.ANDROID_ID);
        return android_id;
    }

    public static String getCountryFlagPath() {
        return CountryFlagdPath;
    }

    public static float mmToFeet(float mm) {
        float feet = mm / 304.8f;
        String feets = String.format("%1.2f", feet);
        feet = Float.parseFloat(feets);
        return feet;
    }

    public static float feetToMm(float value) {

        float mmValue = value * 304.8f;
        String mmValues = String.format("%1.2f", mmValue);
        mmValue = Float.parseFloat(mmValues);

        return mmValue;

    }

    public static float mmToInches(float value) {
        float inches = value / 25.4f;
        String inch = String.format("%1.2f", inches);
        inches = Float.parseFloat(inch);
        return inches;
    }

    public static float inchesToMm(float value) {
        float mmValue = value * 25.4f;
        String mmvalues = String.format("%1.2f", mmValue);
        mmValue = Float.parseFloat(mmvalues);
        return mmValue;

    }

    public static float mmToMeters(float value) {
        float meters = value / 1000;
        String metre = String.format("%1.2f", meters);
        meters = Float.parseFloat(metre);
        return meters;
    }

    public static float metersToMm(float value) {
        float mmValue = value * 1000;
        String mmValues = String.format("%1.2f", mmValue);
        mmValue = Float.parseFloat(mmValues);
        return mmValue;
    }

    public static String getWallCode(String wallName, String currentProject) {
        String wallCode = null;
        if (!currentProject.startsWith("Rectangle")) {
            if (wallName.equalsIgnoreCase("front")) {
                wallCode = "Wall C";
            } else if (wallName.equalsIgnoreCase("back")) {
                wallCode = "Wall B";
            } else if (wallName.equalsIgnoreCase("left")) {
                wallCode = "Wall D";
            } else if (wallName.equalsIgnoreCase("right")) {
                wallCode = "Wall A";
            } else if (wallName.equalsIgnoreCase("frontleft")) {
                wallCode = "Wall E";
            } else if (wallName.equalsIgnoreCase("bottom")) {
                wallCode = "Floor";
            } else if (wallName.equalsIgnoreCase("top")) {
                wallCode = "Ceiling";
            }
        } else {
            if (wallName.equalsIgnoreCase("front")) {
                wallCode = "Wall A";
            } else if (wallName.equalsIgnoreCase("back")) {
                wallCode = "Wall A";
            } else if (wallName.equalsIgnoreCase("left")) {
                wallCode = "Wall B";
            } else if (wallName.equalsIgnoreCase("right")) {
                wallCode = "Wall B";
            } else if (wallName.equalsIgnoreCase("bottom")) {
                wallCode = "Floor";
            } else if (wallName.equalsIgnoreCase("top")) {
                wallCode = "Ceiling";
            }
        }
        return wallCode;
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
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void noNetAvailable(Context context) {
        Toast.makeText(context,
                "Network not available. Kindly check your internet connection!",
                Toast.LENGTH_LONG).show();
    }
}
