package com.nagainfomob.app.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nagainfomob.app.R;
import com.nagainfomob.app.adapter.BottomSheetAdapter;
import com.nagainfomob.app.api.ApiClient;
import com.nagainfomob.app.api.ApiInterface;
import com.nagainfomob.app.fragments.HelpFragment;
import com.nagainfomob.app.fragments.LibraryFragment;
import com.nagainfomob.app.fragments.MyDesignsFragment;
import com.nagainfomob.app.fragments.New3dFragment;
import com.nagainfomob.app.fragments.NewPatternFragment;
import com.nagainfomob.app.fragments.NewTileFragment;
import com.nagainfomob.app.fragments.PatternFragment;
import com.nagainfomob.app.helpers.SessionManager;
import com.nagainfomob.app.model.LoadProject.LoadProjectResult;
import com.nagainfomob.app.model.ProjectsModel;
import com.nagainfomob.app.model.SettingsModel;
import com.nagainfomob.app.model.UserModel;
import com.nagainfomob.app.sql.DatabaseManager;
import com.nagainfomob.app.utils.AnimatorUtils;
import com.ogaclejapan.arclayout.ArcLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetAdapter.ItemListener {

    private final AppCompatActivity activity = DashboardActivity.this;
    private Uri mCropImageUri;
    private static int checkFlag = 0;
    private static int menu_anim_flag=0;
    private static int curFragment=1;
    private static int prevFragment=1;

    private static int w_ratio = 1;
    private static int h_ratio = 1;
    private static boolean ratio_flag = false;

    private LinearLayout FrgamentContainer;
    private PopupWindow changeStatusPopUp;

    private New3dFragment new3dFragment;

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView img_user_dropdown;
    private ImageView img_user;

    private TextView txt_username;
    private MediaPlayer mMediaPlayer;
    private SessionManager session;
    private String access_token;

    private BottomSheetBehavior bs_choose_unit;
    private BottomSheetBehavior bs_choose_category;
    private BottomSheetBehavior bs_choose_type;
    private BottomSheetBehavior bs_choose_contry;

    public View bottomSheet_choose_branch;
    public RecyclerView recyclerViewCountry;
    public TextView defaultViewCountry;
    public ProgressBar choose_country_progress;
    public ImageView ImgDownCountry;

    public View bottomSheet_choose_category;
    public RecyclerView recyclerViewCategory;
    public TextView defaultViewCategory;
    public ProgressBar choose_category_progress;
    public ImageView ImgDownCategory;

    public View bottomSheet_choose_type;
    public RecyclerView recyclerViewType;
    public TextView defaultViewType;
    public ProgressBar choose_type_progress;
    public ImageView ImgDownType;

    private Button btn_upgrade;

    private static int isBigScreen = 0;

    Toast toast = null;
    View fab;
    View menuLayout;
    ArcLayout arcLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_dash);

        DisplayMetrics metrics = new DisplayMetrics();
        DashboardActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            isBigScreen = 1;
        }

        initViews();
        initBottomSheet();
        initListeners();
        rotateImages();
//        initSounds();
        session = new SessionManager(activity);
        getDatafromdb();
        getProjects();

        displaySelectedScreen(R.id.ib1);
        fab.performClick();
        menu_anim_flag=1;

    }

    private void initViews() {
        fab = findViewById(R.id.fab);

        FrgamentContainer = findViewById(R.id.FrgamentContainer);

        menuLayout = findViewById(R.id.menu_layout);
        arcLayout = findViewById(R.id.arc_layout);
        imageView1 = findViewById(R.id.ib1);
        imageView2 = findViewById(R.id.ib2);
        imageView3 = findViewById(R.id.ib3);
        imageView4 = findViewById(R.id.ib4);
        imageView5 = findViewById(R.id.ib5);
        img_user_dropdown = findViewById(R.id.img_user_dropdown);
        img_user = findViewById(R.id.img_user);
        btn_upgrade = findViewById(R.id.btn_upgrade);

        txt_username = findViewById(R.id.txt_username);

//        new3dFragment = (New3dFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_id);

        defaultViewCountry = findViewById(R.id.defaultViewCountry);
        bottomSheet_choose_branch = findViewById(R.id.bottom_sheet_choose_country);
        recyclerViewCountry = findViewById(R.id.recyclerViewChooseCountry);
        choose_country_progress = findViewById(R.id.ChooseCountryProgress);
        ImgDownCountry = findViewById(R.id.ImgDownCountry);

        defaultViewCategory = findViewById(R.id.defaultViewCategory);
        bottomSheet_choose_category = findViewById(R.id.bottom_sheet_choose_category);
        recyclerViewCategory = findViewById(R.id.recyclerViewChooseCategory);
        choose_category_progress = findViewById(R.id.ChooseCategoryProgress);
        ImgDownCategory = findViewById(R.id.ImgDownCategory);

        defaultViewType = findViewById(R.id.defaultViewType);
        bottomSheet_choose_type = findViewById(R.id.bottom_sheet_choose_type);
        recyclerViewType = findViewById(R.id.recyclerViewChooseType);
        choose_type_progress = findViewById(R.id.ChooseTypeProgress);
        ImgDownType = findViewById(R.id.ImgDownType);

    }

    private void initListeners() {
        fab.setOnClickListener(this);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView3.setOnClickListener(this);
        imageView4.setOnClickListener(this);
        imageView5.setOnClickListener(this);
        img_user_dropdown.setOnClickListener(this);
        img_user.setOnClickListener(this);
        txt_username.setOnClickListener(this);
        btn_upgrade.setOnClickListener(this);
    }

    private void initSounds(){
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.button_click);
    }


    public void rotateImages(){

        Bitmap myImg1 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_1);
        Matrix matrix1 = new Matrix();
        matrix1.postRotate(-8);
        Bitmap rotated1 = Bitmap.createBitmap(myImg1, 0, 0, myImg1.getWidth(), myImg1.getHeight(),
                matrix1, true);
        imageView1.setImageBitmap(rotated1);

        Bitmap myImg2 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_2);
        Matrix matrix2 = new Matrix();
        matrix2.postRotate(-4);
        Bitmap rotated2 = Bitmap.createBitmap(myImg2, 0, 0, myImg2.getWidth(), myImg2.getHeight(),
                matrix2, true);
        imageView2.setImageBitmap(rotated2);

        Bitmap myImg3 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_3);
        imageView3.setImageBitmap(myImg3);
        if(isBigScreen == 0){
            imageView3.setPadding(8,0,0,0);
        }else{
            imageView3.setPadding(0,0,0,0);
        }

        Bitmap myImg4 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_4);
        Matrix matrix4 = new Matrix();
        matrix4.postRotate(4);
        Bitmap rotated4 = Bitmap.createBitmap(myImg4, 0, 0, myImg4.getWidth(), myImg4.getHeight(),
                matrix4, true);
        imageView4.setImageBitmap(rotated4);

        Bitmap myImg5 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_5);
        Matrix matrix5 = new Matrix();
        matrix5.postRotate(8);
        Bitmap rotated5 = Bitmap.createBitmap(myImg5, 0, 0, myImg5.getWidth(), myImg5.getHeight(),
                matrix5, true);
        imageView5.setImageBitmap(rotated5);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                onFabClick(view);
                break;
            case R.id.ib1:
                curFragment = 1;
                displaySelectedScreen(R.id.ib1);
                break;
            case R.id.ib2:
                curFragment = 2;
                displaySelectedScreen(R.id.ib2);
                break;
            case R.id.ib3:
                curFragment = 3;
                displaySelectedScreen(R.id.ib3);
                break;
            case R.id.ib4:
                curFragment = 4;
                displaySelectedScreen(R.id.ib4);
                break;
            case R.id.ib5:
                curFragment = 5;
                displaySelectedScreen(R.id.ib5);
                break;
            case R.id.img_user_dropdown:
                initMenuPopup(view);
                break;
            case R.id.txt_username:
                initMenuPopup(view);
                break;
            case R.id.img_user:
                initMenuPopup(view);
                break;
            case R.id.btn_upgrade:
                viewPayOptions();
                break;
        }
    }

    private void getDatafromdb(){
        List<UserModel> user;
        List<SettingsModel> settngs;

        user = DatabaseManager.getUser(activity, session.getUserID());
        settngs = DatabaseManager.getSettings(activity);

        if(user.get(0).getName() != null && user.get(0).getMobile_no() != null) {
            txt_username.setText(user.get(0).getName());
        }

        access_token = "Bearer "+settngs.get(0).getAccessToken();



        if(user.get(0).getSubType().equals("2")) {
            btn_upgrade.setVisibility(View.GONE);
        }
    }

    public void initMenuPopup(View view){
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];
        showMenuPopup(DashboardActivity.this, point);
    }

    private void hideMenuPopup(){
        if(changeStatusPopUp.isShowing()){
            changeStatusPopUp.dismiss();
        }
    }

    private void showMenuPopup(final Activity context, Point p) {

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.llSortChangePopup);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_menu, null);

        LinearLayout profileRow=(LinearLayout) layout.findViewById(R.id.ProfileRow);
        profileRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideMenuPopup();
                myProfile();
            }
        });
        LinearLayout changePassRow=(LinearLayout) layout.findViewById(R.id.ChangePassRow);
        changePassRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideMenuPopup();
                changePass();
            }
        });
        LinearLayout sigoutRow=(LinearLayout) layout.findViewById(R.id.SigoutRow);
        sigoutRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Creating the PopupWindow
        changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(350);
//        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = -20;
        int OFFSET_Y = 75;

        //Clear the default translucent background
        changeStatusPopUp.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

    }

    public void displaySelectedScreen(int itemId) {

        Fragment fragment = null;
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        switch (itemId) {
            case R.id.ib1:
                curFragment = 1;
                fragment = new MyDesignsFragment();
                break;
            case R.id.ib2:
                curFragment = 2;
//                fragment = new NewDesignFragment();
                fragment = new New3dFragment();
                break;
            case R.id.ib3:
                curFragment = 3;
                fragment = new LibraryFragment();
                break;
            case R.id.ib4:
                curFragment = 4;
                fragment = new PatternFragment();
                break;
            case R.id.ib5:
                curFragment = 5;
                fragment = new HelpFragment();
                break;
        }

        changeMenuImage(itemId);

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(curFragment > prevFragment){
                ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            }
            if(curFragment < prevFragment){
                ft.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_in_down);
            }
            prevFragment = curFragment;
            ft.replace(R.id.content_frame_id, fragment, "desgin");
            ft.commit();
        }
    }

    private void changeMenuImage(int itemId){

        rotateImages();

        switch (itemId) {
            case R.id.ib1:
                Bitmap myImg1 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_1_active);
                Matrix matrix1 = new Matrix();
                matrix1.postRotate(-8);
                Bitmap rotated1 = Bitmap.createBitmap(myImg1, 0, 0, myImg1.getWidth(), myImg1.getHeight(),
                        matrix1, true);
                imageView1.setImageBitmap(rotated1);
                break;
            case R.id.ib2:
                Bitmap myImg2 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_2_active);
                Matrix matrix2 = new Matrix();
                matrix2.postRotate(-4);
                Bitmap rotated2 = Bitmap.createBitmap(myImg2, 0, 0, myImg2.getWidth(), myImg2.getHeight(),
                        matrix2, true);
                imageView2.setImageBitmap(rotated2);
                break;
            case R.id.ib3:
                Bitmap myImg3 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_3_active);
                Matrix matrix3 = new Matrix();
                matrix3.postRotate(0);
                Bitmap rotated3= Bitmap.createBitmap(myImg3, 0, 0, myImg3.getWidth(), myImg3.getHeight(),
                        matrix3, true);
                imageView3.setImageBitmap(rotated3);
                if(isBigScreen == 0){
                    imageView3.setPadding(8,0,0,0);
                }else{
                    imageView3.setPadding(9,0,0,0);
                }
//                imageView3.setPadding(9,0,0,0);
                break;
            case R.id.ib4:
                Bitmap myImg4 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_4_active);
                Matrix matrix4 = new Matrix();
                matrix4.postRotate(4);
                Bitmap rotated4 = Bitmap.createBitmap(myImg4, 0, 0, myImg4.getWidth(), myImg4.getHeight(),
                        matrix4, true);
                imageView4.setImageBitmap(rotated4);
                break;
            case R.id.ib5:
                Bitmap myImg5 = BitmapFactory.decodeResource(getResources(), R.drawable.dmd_menu_5_active);
                Matrix matrix5 = new Matrix();
                matrix5.postRotate(8);
                Bitmap rotated5 = Bitmap.createBitmap(myImg5, 0, 0, myImg5.getWidth(), myImg5.getHeight(),
                        matrix5, true);
                imageView5.setImageBitmap(rotated5);
                break;
        }
    }

    private void showToast(String str) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Clicked: " + str;
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();

    }

    private void onFabClick(View v) {
        /*if(menu_anim_flag !=0) {
            mMediaPlayer.start();
        }*/

        if (v.isSelected()) {
            hideMenu();
            viewAnimation(230, 90);
        } else {
            showMenu();
            viewAnimation(90, 230);
        }
        v.setSelected(!v.isSelected());
    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        if(menu_anim_flag ==0) return;

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private void viewAnimation(final int Fmargin, final int Tmargin){
        if(menu_anim_flag ==0) return;

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) FrgamentContainer.getLayoutParams();
                params.leftMargin = (int) ((int) (Tmargin - Fmargin) * interpolatedTime + Fmargin);
                FrgamentContainer.setLayoutParams(params);
            }
        };
        a.setDuration(500); // in ms
        FrgamentContainer.startAnimation(a);
    }

    private Animator createShowItemAnimator(View item) {

        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    public void UpgradePlan(){
        Toast.makeText(this, "Upgrade Plan", Toast.LENGTH_LONG).show();
    }

    public void logoutUser() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You are going to Logout from current session!")
                .setCancelText("Cancel")
                .setConfirmText("Logout")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        session.setLogin(false);
                        Intent intent = new Intent(activity, LandingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    public void sessionOut(){
        session.setLogin(false);
        Intent intent = new Intent(activity, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    @Override
    public void onItemClick(String item) {
        String fragId = "";
        fragId = session.getFragmentId();

        if(fragId.equals("tab2")) {
            New3dFragment fragment = (New3dFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_id);
            fragment.selectItem(item);
        }

        if(fragId.equals("tab3.3")) {
            NewTileFragment fragment = (NewTileFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_id);
            fragment.selectItem(item);
        }

        if(fragId.equals("tab4.1")) {
            NewPatternFragment fragment = (NewPatternFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_id);
            fragment.selectItem(item);
        }
    }

    public void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void captureImage(View view, int wr, int hr){
        if(wr > 0 && hr > 0){
            w_ratio = wr;
            h_ratio = hr;
            ratio_flag = true;
        }
        else{
            w_ratio = 1;
            h_ratio = 1;
            ratio_flag = false;
        }
        onSelectImageClick(view);
    }

    /**
     * Start pick image activity with chooser.
     */
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                NewTileFragment.setImage(result);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            }
        }

        // Handle Change Username
        if(requestCode==2)
        {
            String wall_processed = data.getCharSequenceExtra("MESSAGE").toString();
            if(wall_processed != null && !wall_processed.equals("")) {
                txt_username.setText(wall_processed.toString());
            }
        }

        // Handle Payment Response
        /*if(requestCode==3)
        {
            String response = data.getCharSequenceExtra("MESSAGE").toString();
            if(response.equals("success")) {
                Log.d("PaymentLog", "Success");
                displaySelectedScreen(R.id.ib1);

            }
            else{
                Log.d("PaymentLog", "Fail");
            }
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .setAspectRatio(w_ratio, h_ratio)
                .setFixAspectRatio(ratio_flag)
                .start(this);
    }

    public void getProjects() {
        try {

            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<LoadProjectResult> call = apiService.getProjects("2", access_token);
            call.enqueue(new Callback<LoadProjectResult>() {
                @Override
                public void onResponse(Call<LoadProjectResult> call, Response<LoadProjectResult> response) {
                    int statusCode = response.code();
                    if (response.isSuccessful()) {
                        if(statusCode == 200) {
                            if(!response.body().getData().isEmpty()) {

                                saveDB(response);
                            }

                        } else {
                            JSONObject jObjError = null;
                            try {
                                jObjError = new JSONObject(response.errorBody().string());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        JSONObject jObjError = null;
                        try {
                            if (statusCode == 402 || statusCode == 406){
                                sessionOut();
                            }
                            else{
                                jObjError = new JSONObject(response.errorBody().string());
                                if(jObjError.getString("error").equals("invalid_token")){
                                    sessionOut();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoadProjectResult> call, Throwable throwable) {

                    Log.e("Error", throwable.toString());
                }
            });

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void saveDB(Response<LoadProjectResult> response){

        for (int i = 0; i < response.body().getData().size(); i++) {

            if(!DatabaseManager.checkProject(this, response.body().getData().get(i).getId().trim())){

                ProjectsModel project = new ProjectsModel();

                project.setProject_name(response.body().getData().get(i).getName());
                project.setProject_id(response.body().getData().get(i).getId());
                project.setShare_id(response.body().getData().get(i).getShareId());
                project.setUser_id(session.getUserID());
                project.setProject_type("2");
                project.setRoom_type(response.body().getData().get(i).getRooms().getRoomType());
                project.setUnit_id(response.body().getData().get(i).getRooms().getUnit());
                project.setWidth(response.body().getData().get(i).getRooms().getWidth());
                project.setHeight(response.body().getData().get(i).getRooms().getHeight());
                project.setDepth(response.body().getData().get(i).getRooms().getDepth());
                project.setCust_name(response.body().getData().get(i).getCustomerName());
                project.setCust_mob(response.body().getData().get(i).getMobile());
                project.setTime_stamp(response.body().getData().get(i).getCreatedAt());
                project.setStatus("1");

                DatabaseManager.addProjectsInfo(this, project);

            }
        }

        if(checkFlag == 1 && curFragment == 1) {
//            displaySelectedScreen(R.id.ib1);
        }

    }

    private void myProfile(){
        Intent intent = new Intent(DashboardActivity.this, MyProfileActivity.class);
        startActivityForResult(intent, 2);
    }

    private void changePass(){
        Intent intent = new Intent(DashboardActivity.this, ChangePassActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkFlag = 1;

        if (!session.isLoggedIn()) {
            Intent intent = new Intent(activity, LandingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        checkFlag = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkFlag = 0;
    }

    private void initBottomSheet() {

        bs_choose_unit = BottomSheetBehavior.from(bottomSheet_choose_branch);
        bs_choose_category = BottomSheetBehavior.from(bottomSheet_choose_category);
        bs_choose_type = BottomSheetBehavior.from(bottomSheet_choose_type);
        bs_choose_contry = BottomSheetBehavior.from(bottomSheet_choose_branch);

    }

    @Override public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bs_choose_unit.getState()==BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                bottomSheet_choose_branch.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    bs_choose_unit.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (bs_choose_category.getState()==BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                bottomSheet_choose_category.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    bs_choose_category.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (bs_choose_type.getState()==BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                bottomSheet_choose_type.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    bs_choose_type.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void viewPayOptions(){

        Intent intent = new Intent(DashboardActivity.this, PlanActivity.class);
        intent.putExtra("user_mobile",session.getKeyUserMob());
        startActivity(intent);
    }

    private void collapseBottomSheet(){
        if(bs_choose_unit.getState() == 3) {
            bs_choose_unit.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if(bs_choose_category.getState() == 3) {
            bs_choose_category.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if(bs_choose_type.getState() == 3) {
            bs_choose_type.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if(bs_choose_contry.getState() == 3) {
            bs_choose_contry.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onBackPressed()
    {
        String fragId = "";
        fragId = session.getFragmentId();

        if(curFragment == 1) {
            super.onBackPressed();
        }
        else if(curFragment == 2) {
            if(fragId.equals("tab2")) {
                if(bs_choose_contry.getState() == 3) {
                    collapseBottomSheet();
                    return;
                }
                displaySelectedScreen(R.id.ib1);
                return;
            }
        }
        else if(curFragment == 3) {
            if(fragId.equals("tab3.1") || fragId.equals("tab3.2") || fragId.equals("tab3.3")) {
                if(bs_choose_unit.getState() == 3 || bs_choose_category.getState() == 3 ||
                        bs_choose_type.getState() == 3) {
                    collapseBottomSheet();
                    return;
                }

                if(fragId.equals("tab3.3")){
                    session.setFragmentId("tab3");
                    session.setBackStackId("99");
                    NewTileFragment fragment = (NewTileFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame_id);
                    fragment.triggerBackStack();
                    return;
                }

                session.setFragmentId("tab3");
                displaySelectedScreen(R.id.ib3);
                return;
            }
            else{
                displaySelectedScreen(R.id.ib1);
                return;
            }
        }
        else if(curFragment == 4) {
            if(fragId.equals("tab4.1") || fragId.equals("tab4.2")) {
                if(bs_choose_unit.getState() == 3 || bs_choose_category.getState() == 3 ||
                        bs_choose_type.getState() == 3) {
                    collapseBottomSheet();
                    return;
                }
                session.setFragmentId("tab4");
                displaySelectedScreen(R.id.ib4);
                return;
            }
            else{
                displaySelectedScreen(R.id.ib1);
                return;
            }
        }
        else {
            super.onBackPressed();
        }
    }

}