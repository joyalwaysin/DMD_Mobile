package com.nagainfomob.app.DisplayMyDesign;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.nagainfomob.app.R;
import com.nagainfomob.app.database.DatabaseHandler;
import com.nagainfomob.app.helpers.ChangeViewInterface;
import com.nagainfomob.app.helpers.MessageViewInterface;
import com.nagainfomob.app.helpers.MyCountInterface;
import com.nagainfomob.app.helpers.MyDrawingViewInterface;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DrawView extends View implements OnTouchListener {
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
    Boolean isAutoGridSelected = false;
    String wall;
    int leftMoveStart, topMoveStart;
    float leftMoveStart_f, topMoveStart_f;
    String selectedFilePath;
    String selectedFilePathEdit;
    Boolean isTilePatterning = false;
    String unit;
    Float length, width;
    float canvas_H, canvas_W;
    EditText leftText;
    EditText topText;
    EditText widthText;
    EditText heightText;
    EditText rotText,layRotText;
    String origUnit;
    int axis = 0;
    String tile_id;
    String tile_type;
    int viewArea;// = GlobalVariables.getDrawArea(getContext());
    float tileW, tileH;

    Point[] points = new Point[4];
    Point startMovePoint;
    private SweetAlertDialog dDialog;

    public static LinearLayout selectedLayout = null;
    public static LinearLayout prev_selectedLayout = null;
    private MyDrawingViewInterface mInterface = null;
    private MyCountInterface cInterface = null;
    private ChangeViewInterface vInterface = null;
    private MessageViewInterface dInterface = null;
    public static LayoutDimensions dimensions = null;
    public static RelativeLayout selectedRelLay = null;
    public int tot_wall_tiles = 0;

    private static float h_val = 0;
    private static float w_val = 0;
    private static float x_val = 0;
    private static float y_val = 0;

    private static int hh_val = 0;
    private static int ww_val = 0;
    private static int xx_val = 0;
    private static int yy_val = 0;

    private double h_feet = 0;
    private double h_inch = 0;

    private double w_feet = 0;
    private double w_inch = 0;

    private double l_feet = 0;
    private double l_inch = 0;

    private double t_feet = 0;
    private double t_inch = 0;

    private int newFlag = 0;

    long downTime = SystemClock.uptimeMillis();
    long eventTime = SystemClock.uptimeMillis() + 100;
    float xx = 0.0f;
    float yy = 0.0f;
    int metaState = 0;
    MotionEvent motionEvent = MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_UP,
            xx,
            yy,
            metaState
    );

    /**
     * point1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    private ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
    private List<String> cellList = new ArrayList<String>();
    private int balID = 0;
    Canvas canvas;
    static int count_new = 0;

    public DrawView(Context context, String wall) {
        super(context);
        this.context = context;
        this.setOnTouchListener(this);
        this.viewArea = GlobalVariables.getDrawArea(getContext());
        this.mInterface = (MyDrawingViewInterface) context;
        this.cInterface = (MyCountInterface) context;
        this.vInterface = (ChangeViewInterface) context;
        this.dInterface = (MessageViewInterface) context;
        startPoint = endPoint = lastEndPoint = new Point();

        isTileSelected = false;
        isDrawingSelected = false;
        isAutoGridSelected = false;
        this.wall = wall;
        origUnit = GlobalVariables.getUnit();
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

    public void isAutoGridSelected(Boolean select) {
        isAutoGridSelected = select;
    }


    /*public void setViewListener(DrawView interface1) {
        mInterface = interface1;
    }*/

    public void setSelectedTile(Bitmap tile, String filePath, int orientation,
                                String tileSize, String tile_id, String tile_type) {
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
        this.tile_id = tile_id;
        this.tile_type = tile_type;
    }

    @Override
    public void onDraw(Canvas canvas) {

        if(points[3]==null) //point4 null when user did not touch and move on screen.
            return;
        int left, top, right, bottom;
        left = points[0].x;
        top = points[0].y;
        right = points[0].x;
        bottom = points[0].y;
        for (int i = 1; i < points.length; i++) {
            left = left > points[i].x ? points[i].x:left;
            top = top > points[i].y ? points[i].y:top;
            right = right < points[i].x ? points[i].x:right;
            bottom = bottom < points[i].y ? points[i].y:bottom;
        }

        //draw stroke
        paint.setColor(Color.rgb(0, 0, 0));

        paint.setStrokeWidth(2);
        paint.setStyle(Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[] {10,10}, 5));

        canvas.drawRect(
                left + colorballs.get(0).getWidthOfBall() / 2,
                top + colorballs.get(0).getWidthOfBall() / 2,
                right + colorballs.get(2).getWidthOfBall() / 2,
                bottom + colorballs.get(2).getWidthOfBall() / 2, paint);

        //draw the corners
        BitmapDrawable bitmap = new BitmapDrawable();
        // draw the balls on the canvas
        paint.setColor(Color.BLUE);
        paint.setTextSize(14);
        paint.setStrokeWidth(0);
        for (int i =0; i < colorballs.size(); i ++) {
            ColorBall ball = colorballs.get(i);
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                    paint);
        }
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        this.canvas_W = xNew;
        this.canvas_H = yNew;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        this.canvas_H = v.getMeasuredHeight();
        this.canvas_W = v.getMeasuredWidth();

        if (!isDrawingSelected) {
            dInterface.changeMessage("Please select draw tool to begin wall area marking!", 0);
            /*Toast.makeText(getContext(),
                    "Please select the drawing tool to draw !",
                    Toast.LENGTH_SHORT).show();*/
            return false;
        }

        int X = (int) event.getX();
        int Y = (int) event.getY();

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

                startMovePoint = new Point(X,Y);
                if (points[0] == null) {

                    destroyDrawingCache();
                    buildDrawingCache();

                    count_new = 0;
                    //initialize rectangle.
                    points[0] = new Point();
                    points[0].x = X;
                    points[0].y = Y;

                    points[1] = new Point();
                    points[1].x = X;
                    points[1].y = Y ;

                    points[2] = new Point();
                    points[2].x = X ;
                    points[2].y = Y ;

                    points[3] = new Point();
                    points[3].x = X ;
                    points[3].y = Y;

                    balID = 2;
                    groupId = 1;
                    // declare each ball with the ColorBall class
                    for (Point pt : points) {
                        colorballs.add(new ColorBall(getContext(), R.drawable.dmd_ball, pt));
                    }

                } else {
                    //resize rectangle
                    balID = -1;
                    groupId = -1;
                    for (int i = colorballs.size()-1; i>=0; i--) {
                        ColorBall ball = colorballs.get(i);
                        // check if inside the bounds of the ball (circle)
                        // get the center for the ball
                        int centerX = ball.getX() + ball.getWidthOfBall();
                        int centerY = ball.getY() + ball.getHeightOfBall();
                        paint.setColor(Color.CYAN);
                        // calculate the radius from the touch to the center of the
                        // ball
                        double radCircle = Math
                                .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                        * (centerY - Y)));

                        if (radCircle < ball.getWidthOfBall()) {

                            balID = ball.getID();

//                            if(balID > 3) balID = 3;

                            if (balID == 1 || balID == 3) {
                                groupId = 2;
                            } else {
                                groupId = 1;
                            }
                            invalidate();
                            break;
                        }
                        invalidate();
                    }
                }

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                if (balID > -1) {
                    colorballs.get(balID).setX(X);
                    colorballs.get(balID).setY(Y);

                    paint.setColor(Color.CYAN);

                    if (groupId == 1) {
                        colorballs.get(1).setX(colorballs.get(0).getX());
                        colorballs.get(1).setY(colorballs.get(2).getY());
                        colorballs.get(3).setX(colorballs.get(2).getX());
                        colorballs.get(3).setY(colorballs.get(0).getY());
                    } else {
                        colorballs.get(0).setX(colorballs.get(1).getX());
                        colorballs.get(0).setY(colorballs.get(3).getY());
                        colorballs.get(2).setX(colorballs.get(3).getX());
                        colorballs.get(2).setY(colorballs.get(1).getY());
                    }

                    invalidate();
                }else{
                    if (startMovePoint!=null) {
                        paint.setColor(Color.CYAN);
                        int diffX = X - startMovePoint.x;
                        int diffY = Y - startMovePoint.y;
                        startMovePoint.x = X;
                        startMovePoint.y = Y;
                        colorballs.get(0).addX(diffX);
                        colorballs.get(1).addX(diffX);
                        colorballs.get(2).addX(diffX);
                        colorballs.get(3).addX(diffX);
                        colorballs.get(0).addY(diffY);
                        colorballs.get(1).addY(diffY);
                        colorballs.get(2).addY(diffY);
                        colorballs.get(3).addY(diffY);

                        invalidate();
                    }
                }

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
//                    addLayout();
                    vInterface.changeView(true, false);
                }

                setPoints();

                float wallWidth = Math.abs(width);
                float wallLength = Math.abs(length);
                float screenWidth = canvas_W;
                float screenHeight = canvas_H;

                ArrayList<String> size = convertScale(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                        (startPoint.x * (wallWidth / screenWidth)),
                        (startPoint.y * (wallLength / screenHeight)));
                mInterface.setCurrentSelection(size.get(0).toString(), size.get(1).toString(), tot_wall_tiles,
                        Float.parseFloat(size.get(2)),  Float.parseFloat(size.get(3)));

                h_val = Float.parseFloat(size.get(0));
                w_val = Float.parseFloat(size.get(1));
                x_val = Float.parseFloat(size.get(2));
                y_val = Float.parseFloat(size.get(3));

                break;
        }

        invalidate();
        return true;
    }

    public void clearSelection(){

        mInterface.setCurrentSelection("0.0", "0.0", tot_wall_tiles, 0, 0);

        if(prev_selectedLayout != null) {
            if (((LayoutDimensions) prev_selectedLayout.getTag()).selectedTile == null) {
                prev_selectedLayout.setBackgroundColor(Color.parseColor("#bcbbbb"));
                if (Build.VERSION.SDK_INT >= 23) {
                    prev_selectedLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                }
                else{
                    prev_selectedLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                }
            } else {
//                prev_selectedLayout.setBackgroundColor(Color.TRANSPARENT);
                if (Build.VERSION.SDK_INT >= 23) {
                    prev_selectedLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                }
                else{
                    prev_selectedLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                }
            }
        }

        dimensions = null;
        selectedLayout = null;
        prev_selectedLayout = null;
    }

    public void setPoints(){

        if(!colorballs.isEmpty()) {
            startPoint.x = colorballs.get(0).getX() + colorballs.get(0).getWidthOfBall() / 2;
            startPoint.y = colorballs.get(0).getY() + colorballs.get(0).getWidthOfBall() / 2;
            endPoint.x = colorballs.get(2).getX() + colorballs.get(2).getWidthOfBall() / 2;
            endPoint.y = colorballs.get(2).getY() + colorballs.get(2).getWidthOfBall() / 2;
        }

        if(startPoint.x < 0) {
            colorballs.get(0).setX(0 - colorballs.get(0).getWidthOfBall() / 2);
            colorballs.get(1).setX(0- colorballs.get(1).getWidthOfBall() / 2);
            startPoint.x = 0;
            invalidate();
        }
        if(startPoint.y < 0) {
            colorballs.get(0).setY(0 - colorballs.get(0).getWidthOfBall() / 2);
            colorballs.get(3).setY(0 - colorballs.get(3).getWidthOfBall() / 2);
            startPoint.y = 0;
            invalidate();
        }

        if(endPoint.x > this.canvas_W) {
            colorballs.get(2).setX((int) (this.canvas_W - colorballs.get(2).getWidthOfBall() / 2));
            colorballs.get(3).setX((int) (this.canvas_W - colorballs.get(3).getWidthOfBall() / 2));
            endPoint.x = (int) this.canvas_W;
            invalidate();
        }

        if(endPoint.y > this.canvas_H) {
            colorballs.get(2).setY((int) (this.canvas_H - colorballs.get(2).getWidthOfBall() / 2));
            colorballs.get(1).setY((int) (this.canvas_H - colorballs.get(1).getWidthOfBall() / 2));
            endPoint.y = (int) this.canvas_H;
            invalidate();
        }
    }

    public void addLayoutView(){

        if (startPoint.x < endPoint.x && startPoint.y < endPoint.y) {
            addLayout();
        }
        else{
            clearSelection();
        }
    }

    public void fitX(){
        colorballs.get(0).setX(0 - colorballs.get(0).getWidthOfBall() / 2);
        colorballs.get(1).setX(0- colorballs.get(1).getWidthOfBall() / 2);
        startPoint.x = 0;

        colorballs.get(2).setX((int) (this.canvas_W - colorballs.get(2).getWidthOfBall() / 2));
        colorballs.get(3).setX((int) (this.canvas_W - colorballs.get(3).getWidthOfBall() / 2));
        endPoint.x = (int) this.canvas_W;

        ArrayList<String> size = convertScale1(startPoint.x, startPoint.y, endPoint.x, endPoint.y, 0, 0);
        mInterface.setCurrentSelection(size.get(0).toString(), size.get(1).toString(), tot_wall_tiles, 0,
                Float.parseFloat(size.get(3).toString()));
        x_val = 0;
        w_val = Float.parseFloat(size.get(1).toString());

        invalidate();
    }

    public void fitY(){
        colorballs.get(0).setY(0 - colorballs.get(0).getWidthOfBall() / 2);
        colorballs.get(3).setY(0 - colorballs.get(3).getWidthOfBall() / 2);
        startPoint.y = 0;

        colorballs.get(2).setY((int) (this.canvas_H - colorballs.get(2).getWidthOfBall() / 2));
        colorballs.get(1).setY((int) (this.canvas_H - colorballs.get(1).getWidthOfBall() / 2));
        endPoint.y = (int) this.canvas_H;

        ArrayList<String> size = convertScale1(startPoint.x, startPoint.y, endPoint.x, endPoint.y, 0, 0);
        mInterface.setCurrentSelection(size.get(0).toString(), size.get(1).toString(), tot_wall_tiles,
                Float.parseFloat(size.get(2).toString()), 0);
        y_val = 0;
        h_val = Float.parseFloat(size.get(0).toString());

        invalidate();
    }

    public void fitXY(){
        colorballs.get(0).setX(0 - colorballs.get(0).getWidthOfBall() / 2);
        colorballs.get(1).setX(0- colorballs.get(1).getWidthOfBall() / 2);
        startPoint.x = 0;

        colorballs.get(2).setX((int) (this.canvas_W - colorballs.get(2).getWidthOfBall() / 2));
        colorballs.get(3).setX((int) (this.canvas_W - colorballs.get(3).getWidthOfBall() / 2));
        endPoint.x = (int) this.canvas_W;

        colorballs.get(0).setY(0 - colorballs.get(0).getWidthOfBall() / 2);
        colorballs.get(3).setY(0 - colorballs.get(3).getWidthOfBall() / 2);
        startPoint.y = 0;

        colorballs.get(2).setY((int) (this.canvas_H - colorballs.get(2).getWidthOfBall() / 2));
        colorballs.get(1).setY((int) (this.canvas_H - colorballs.get(1).getWidthOfBall() / 2));
        endPoint.y = (int) this.canvas_H;

        ArrayList<String> size = convertScale1(startPoint.x, startPoint.y, endPoint.x, endPoint.y, 0, 0);
        mInterface.setCurrentSelection(size.get(0).toString(), size.get(1).toString(), tot_wall_tiles, 0, 0);

        x_val = 0;
        y_val = 0;
        h_val = Float.parseFloat(size.get(0).toString());
        w_val = Float.parseFloat(size.get(1).toString());

        invalidate();
    }

    public void resetPoint(){
        startPoint = new Point();
        endPoint = new Point();
        this.points = new Point[4];
        groupId = -1;
        balID = 0;
        colorballs = new ArrayList<ColorBall>();
        colorballs.clear();
    }
    public static class ColorBall {

        Bitmap bitmap;
        Context mContext;
        Point point;
        int id;
        static int count = 0;

        public ColorBall(Context context, int resourceId, Point point) {
            this.id = count_new++;
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    resourceId);
            mContext = context;
            this.point = point;
        }

        public int getWidthOfBall() {
            return bitmap.getWidth();
        }

        public int getHeightOfBall() {
            return bitmap.getHeight();
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public int getX() {
            return point.x;
        }

        public int getY() {
            return point.y;
        }

        public int getID() {
            return id;
        }

        public void setX(int x) {
            point.x = x;
        }

        public void setY(int y) {
            point.y = y;
        }

        public void addY(int y){
            point.y = point.y + y;
        }

        public void addX(int x){
            point.x = point.x + x;
        }


    }

    public void addLayout() {
        rel = (RelativeLayout) this.getParent();
        {

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            if (Build.VERSION.SDK_INT >= 23) {
                linearLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg));
            }
            else{
                linearLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
            }

            LayoutParams param = new LayoutParams(Math.abs(startPoint.x
                    - endPoint.x), Math.abs(startPoint.y - endPoint.y));
            linearLayout.setLayoutParams(param);
            linearLayout.setClickable(false);
            linearLayout.setOnTouchListener(layoutTouched);

            ArrayList<String> mm_mes = convertTomm();

            LayoutParams params = new LayoutParams(
                    Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y
                    - endPoint.y));
            // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
            params.leftMargin = startPoint.x;
            params.topMargin = startPoint.y;
            LayoutDimensions dim = new LayoutDimensions();
            dim.x = startPoint.x;
            dim.y = startPoint.y;
            dim.xx = startPoint.x;
            dim.yy = startPoint.y;
            dim.width = Math.abs(startPoint.x - endPoint.x);
            dim.height = Math.abs(startPoint.y - endPoint.y);
            dim.rot = 0;
            dim.layRot = 0;

            dim.w = w_val;
            dim.h = h_val;
            dim.l = x_val;
            dim.t = y_val;

            linearLayout.setTag(dim);

            selectedLayout = linearLayout;

            if(prev_selectedLayout != null && prev_selectedLayout!= selectedLayout) {
//                prev_selectedLayout.setBackgroundColor(Color.parseColor("#FFA300"));
                if(((LayoutDimensions) prev_selectedLayout.getTag()).selectedTile == null) {
                    prev_selectedLayout.setBackgroundColor(Color.parseColor("#bcbbbb"));
                    if (Build.VERSION.SDK_INT >= 23) {
                        prev_selectedLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                    }
                    else{
                        prev_selectedLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                    }
                }
                else {
//                    prev_selectedLayout.setBackgroundColor(Color.TRANSPARENT);
                    if (Build.VERSION.SDK_INT >= 23) {
                        prev_selectedLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                    }
                    else{
                        prev_selectedLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                    }
                }

            }

            prev_selectedLayout = linearLayout;

            rel.setClickable(true);
            rel.addView(linearLayout, params);
            linearLayout.bringToFront();

            linearLayout.dispatchTouchEvent(motionEvent);

            if (!isTilePatterning) {
                if (unit != null) {
                    origUnit = unit;
                    GlobalVariables.setUnit(unit);
                }
                /*showDialog(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                        linearLayout, dim.rot, dim.layRot);*/
            } else {
                /*showTileDialog(startPoint.x, startPoint.y, endPoint.x,
                        endPoint.y, linearLayout, dim.rot, dim.layRot);*/
            }
        }

        vInterface.changeView(false, true);
    }

    public ArrayList<String> convertScale(int left, int top, int right, int bottom, float x_pos, float y_pos ){

        float dd = x_val;
        ArrayList<String> s = new ArrayList<String>();
        float leftString = 0;
        float topString = 0;
        float widthString = 0;
        float heightString = 0;
        String leftStrings, topStrings, widthStrings, heightStrings;
        unit = GlobalVariables.getDesignerUnit();

        final float wallWidth = this.width;
        final float wallHeight = this.length;
        final float wallLength = this.length;
        final float wallC = GlobalVariables.getWallC();
        final float wallD = GlobalVariables.getWallD();

        String currentProject = GlobalVariables.getProjectName();

        width = wallWidth;
        length = wallLength;

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

        float ww = this.canvas_W;
        float hh = this.canvas_H;

        final float screenWidth = this.canvas_W;
        final float screenHeight = this.canvas_H;

        leftString = x_pos;
        topString = y_pos;

        if(hh_val == (Math.abs(top - bottom)) || ww_val == (Math.abs(left - right))){
            heightString = h_val;
            widthString = w_val;
        }
        else{
            heightString = (Math.abs(top - bottom) * ((float) wallLength) / screenHeight);
            widthString = (Math.abs(left - right) * ((float) wallWidth) / screenWidth);

            if (unit.equalsIgnoreCase("Feet")) {
                widthString = widthString / 304.8f;
                widthStrings = String.format("%1.2f", widthString);
                heightString = heightString / 304.8f;
                heightStrings = String.format("%1.2f", heightString);

//                widthStrings = String.valueOf(widthString);
//                heightStrings = String.valueOf(heightString);

            } else if (unit.equalsIgnoreCase("Inches")) {
                widthString = (widthString / 25.4f);
                widthStrings = String.format("%1.2f", widthString);
                heightString = (heightString / 25.4f);
                heightStrings = String.format("%1.2f", heightString);

//                widthStrings = String.valueOf(widthString);
//                heightStrings = String.valueOf(heightString);

            }
        }
        widthStrings = String.format("%1.2f", widthString);
        heightStrings = String.format("%1.2f",heightString);

//        widthStrings = String.valueOf(widthString);
//        heightStrings = String.valueOf(heightString);


        String testUnit = GlobalVariables.getUnit();

        if(yy_val == top || xx_val == left){
            leftString = x_val;
            topString = y_val;
        }
        else{
            if (unit.equalsIgnoreCase("Feet")) {

                leftString = leftString / 304.8f;
                leftStrings = String.format("%1.2f", leftString);
                topString = topString / 304.8f;
                topStrings = String.format("%1.2f", topString);

            } else if (unit.equalsIgnoreCase("Inches")) {

                leftString = (leftString / 25.4f);
                leftStrings = String.format("%1.2f", leftString);
                topString = (topString / 25.4f);
                topStrings = String.format("%1.2f", topString);

            }
        }
        leftStrings = String.format("%1.2f", leftString);
        topStrings =  String.format("%1.2f", topString);

        hh_val = (Math.abs(top - bottom));
        ww_val = (Math.abs(left - right));
        xx_val = left;
        yy_val = top;

        s.add(heightStrings);
        s.add(widthStrings);
        s.add(leftStrings);
        s.add(topStrings);

        return s;

    }

    public ArrayList<String> convertScale1(int left, int top, int right, int bottom, float x_pos, float y_pos ){

        ArrayList<String> s = new ArrayList<String>();
        float leftString = 0, topString = 0, widthString = 0, heightString = 0;
        String leftStrings, topStrings, widthStrings, heightStrings;
        unit = GlobalVariables.getDesignerUnit();

        final float wallWidth = this.width;
        final float wallHeight = this.length;
        final float wallLength = this.length;
        final float wallC = GlobalVariables.getWallC();
        final float wallD = GlobalVariables.getWallD();

        String currentProject = GlobalVariables.getProjectName();

        width = wallWidth;
        length = wallLength;

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

        float ww = this.canvas_W;
        float hh = this.canvas_H;

        final float screenWidth = this.canvas_W;
        final float screenHeight = this.canvas_H;

        leftString = (left * ((float) wallWidth) / screenWidth);
        topString = (top * ((float) wallWidth) / screenWidth);

        widthString = (Math.abs(left - right) * ((float) wallWidth) / screenWidth);
        heightString = (Math.abs(top - bottom) * ((float) wallLength) / screenHeight);

        String testUnit = GlobalVariables.getUnit();


        if (unit.equalsIgnoreCase("Feet")) {

            leftString = leftString / 304.8f;
            leftStrings = String.format("%1.2f", leftString);
            topString = topString / 304.8f;
            topStrings = String.format("%1.2f", topString);
            widthString = widthString / 304.8f;
            widthStrings = String.format("%1.2f", widthString);
            heightString = heightString / 304.8f;
            heightStrings = String.format("%1.2f", heightString);

        } else if (unit.equalsIgnoreCase("Inches")) {

            leftString = (leftString / 25.4f);
            leftStrings = String.format("%1.2f", leftString);
            topString = (topString / 25.4f);
            topStrings = String.format("%1.2f", topString);
            widthString = (widthString / 25.4f);
            widthStrings = String.format("%1.2f", widthString);
            heightString = (heightString / 25.4f);
            heightStrings = String.format("%1.2f", heightString);

        } else {

            leftStrings = String.format("%1.2f", leftString);
            topStrings = String.format("%1.2f", topString);
            widthStrings = String.format("%1.2f", widthString);
            heightStrings = String.format("%1.2f", heightString);

        }

        s.add(heightStrings);
        s.add(widthStrings);
        s.add(leftStrings);
        s.add(topStrings);

        return s;

    }

    public void setConveredMesures(String h, String w, String x, String y){
        h_val = Float.parseFloat(h);
        w_val = Float.parseFloat(w);
        x_val = Float.parseFloat(x);
        y_val = Float.parseFloat(y);
    }

    public void resetMeasures(){
        hh_val = 0;
        ww_val = 0;
        xx_val = 0;
        yy_val = 0;
    }

    public ArrayList<String> convertTomm(){

        ArrayList<String> s = new ArrayList<String>();
        unit = GlobalVariables.getDesignerUnit();
        String mm_h, mm_w, mm_l, mm_t;

        if (unit.equalsIgnoreCase("Feet")) {
            mm_w = w_val * 304.8f + "";
            mm_h = h_val * 304.8f + "";
            mm_l = x_val * 304.8f + "";
            mm_t = y_val * 304.8f + "";

        } else if (unit.equalsIgnoreCase("Inches")) {
            mm_w = w_val * 25.4f + "";
            mm_h = h_val * 25.4f + "";
            mm_l = x_val * 25.4f + "";
            mm_t = y_val * 25.4f + "";

        }
        else{
            mm_w = w_val + "";
            mm_h = h_val + "";
            mm_l = x_val + "";
            mm_t = y_val + "";
        }

        s.add(mm_w);
        s.add(mm_h);
        s.add(mm_l);
        s.add(mm_t);

        h_feet = Double.parseDouble(mm_h) * 0.00328084f;
        h_inch = Double.parseDouble(mm_h) * 0.0393701f;

        w_feet = Double.parseDouble(mm_w) * 0.00328084f;
        w_inch = Double.parseDouble(mm_w) * 0.0393701f;

        l_feet = Double.parseDouble(mm_l) * 0.00328084f;
        l_inch = Double.parseDouble(mm_l) * 0.0393701f;

        t_feet = Double.parseDouble(mm_t) * 0.00328084f;
        t_inch = Double.parseDouble(mm_t) * 0.0393701f;


        return s;

    }

    public ArrayList<String> convertToUnit(float w, float h, float l, float t){

        ArrayList<String> s = new ArrayList<String>();
        unit = GlobalVariables.getDesignerUnit();
        String mm_h, mm_w, mm_l, mm_t;

        if (unit.equalsIgnoreCase("Feet")) {
            mm_w = String.format("%1.2f", (w * 0.00328084f)) + "";
            mm_h = String.format("%1.2f", (h * 0.00328084f)) + "";
            mm_l = String.format("%1.2f", (l * 0.00328084f)) + "";
            mm_t = String.format("%1.2f", (t * 0.00328084f)) + "";

        } else if (unit.equalsIgnoreCase("Inches")) {
            mm_w = String.format("%1.2f", (w * 0.0393701f)) + "";
            mm_h = String.format("%1.2f", (h * 0.0393701f)) + "";
            mm_l = String.format("%1.2f", (l * 0.0393701f)) + "";
            mm_t = String.format("%1.2f", (t * 0.0393701f)) + "";

        }
        else{
            mm_w = String.format("%1.2f", w) + "";
            mm_h = String.format("%1.2f", h) + "";
            mm_l = String.format("%1.2f", l) + "";
            mm_t = String.format("%1.2f", t) + "";
        }

        s.add(mm_w);
        s.add(mm_h);
        s.add(mm_l);
        s.add(mm_t);

        return s;

    }

    OnTouchListener layoutTouched = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            rel = (RelativeLayout) DrawView.this.getParent();
            float wallWidth = Math.abs(width);
            float wallLength = Math.abs(length);
            float screenWidth = canvas_W;
            float screenHeight = canvas_H;
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (!isDrawingSelected && !isAutoGridSelected) {
                        int currentX = startPoint.x - (int) event.getRawX();
                        currentX *= -1;

                        int currentY = startPoint.y - (int) event.getRawY();
                        currentY *= -1;
                        LayoutParams params = (LayoutParams) v
                                .getLayoutParams();

                        Rect rect = new Rect();
                        getHitRect(rect);

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

                            dim.xx = leftMoveStart_f + currentX;
                            dim.yy = topMoveStart_f + currentY;


                            dim.width = Math.round(v.getWidth());
                            dim.height = Math.round(v.getHeight());

                            if(/*x_val != 0 && y_val != 0 &&*/ leftMoveStart_f != dim.xx || topMoveStart_f != dim.yy) {
                                dim.l = dim.xx * ((float) (wallWidth / screenWidth));
                                dim.t = dim.yy * ((float) (wallWidth / screenWidth));
                            }

                            v.setTag(dim);

                            // invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (!isTileSelected && isDrawingSelected) {
                        touchedView = (LinearLayout) v;
                        DrawView.this.onTouch(v, event);

                    } else if ( !isDrawingSelected) {
                        startPoint = new Point((int) event.getRawX(),
                                (int) event.getRawY());
                        LayoutParams params = (LayoutParams) v
                                .getLayoutParams();
                        leftMoveStart = params.leftMargin;
                        topMoveStart = params.topMargin;

                        final LinearLayout layout = (LinearLayout) v;

                        final LayoutDimensions dim = (LayoutDimensions) layout
                                .getTag();

                        leftMoveStart_f = dim.xx;
                        topMoveStart_f = dim.yy;

                        layout.bringToFront();

                        selectedLayout = layout;
                        selectedRelLay = rel;

                        if(prev_selectedLayout == null){
                            h_val = 0;
                            w_val = 0;
                            x_val = 0;
                            y_val = 0;
                        }

                        if(prev_selectedLayout != null && prev_selectedLayout!= selectedLayout) {

                            h_val = 0;
                            w_val = 0;
                            x_val = 0;
                            y_val = 0;

                            if(isAutoGridSelected){
                                /*GradientDrawable border = new GradientDrawable();
                                border.setColor(0xFFFFFFFF); //white background
                                border.setStroke(1, 0x7c000000); //black border with full opacity
                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    prev_selectedLayout.setBackgroundDrawable(border);
                                } else {
                                    prev_selectedLayout.setBackground(border);
                                }
                                dInterface.changeMessage("Choose an option above to select entire row or column", 1);*/

                            } else{
                                if(((LayoutDimensions) prev_selectedLayout.getTag()).selectedTile == null) {
                                    prev_selectedLayout.setBackgroundColor(Color.parseColor("#bcbbbb"));
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        prev_selectedLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                                    }
                                    else{
                                        prev_selectedLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));

                                    }
                                }
                                else {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        prev_selectedLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                                    }
                                    else{
                                        prev_selectedLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                                    }
                                }
                            }
                        }

                        if(!isAutoGridSelected) {
                            prev_selectedLayout = layout;
                        }

                        if(isAutoGridSelected){

                            if(cellList.isEmpty()){
                                cellList.add(dim.id);

                                if(((LayoutDimensions) layout.getTag()).selectedTile != null) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        layout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg));
                                    }
                                    else{
                                        layout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                                    }
                                }
                                else{
                                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        layout.setBackgroundDrawable(context.getResources().
                                                getDrawable(R.drawable.dmd_rect_bg_shadow));
                                    } else {
                                        layout.setBackground(context.getResources().
                                                getDrawable(R.drawable.dmd_rect_bg_shadow));
                                    }
                                }

                            } else{

                                if(cellList.contains(dim.id)){
                                    cellList.remove(dim.id);

                                    if(((LayoutDimensions) layout.getTag()).selectedTile != null) {
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            layout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                                        }
                                        else{
                                            layout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                                        }
                                    }
                                    else{
                                        GradientDrawable border = new GradientDrawable();
                                        border.setColor(0xFFFFFFFF); //white background
                                        border.setStroke(1, 0x7c000000); //black border with full opacity
                                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            layout.setBackgroundDrawable(border);
                                        } else {
                                            layout.setBackground(border);
                                        }
                                    }
                                } else{
                                    cellList.add(dim.id);

                                    if(((LayoutDimensions) layout.getTag()).selectedTile != null) {
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            layout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg));
                                        }
                                        else{
                                            layout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                                        }
                                    }
                                    else{
                                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                            layout.setBackgroundDrawable(context.getResources().
                                                    getDrawable(R.drawable.dmd_rect_bg_shadow));
                                        } else {
                                            layout.setBackground(context.getResources().
                                                    getDrawable(R.drawable.dmd_rect_bg_shadow));
                                        }
                                    }
                                }
                            }
                            dInterface.changeMessage("Choose an option above to select entire row or column", 1);
                        } else{
                            if (Build.VERSION.SDK_INT >= 23) {
                                layout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg));
                            }
                            else{
                                layout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                            }
                        }

                        invalidate();
                    }
                    break;

                // TODO Auto-generated method stub
                case MotionEvent.ACTION_UP: {
                    if (!isTileSelected && isDrawingSelected) {
                        touchedView = (LinearLayout) v;
                        DrawView.this.onTouch(v, event);

                    } else {
                        final LinearLayout layout = (LinearLayout) v;

                        origUnit = unit;
                        GlobalVariables.setUnit(unit);
                        final LayoutDimensions dim = (LayoutDimensions) layout
                                .getTag();

                        dimensions = dim;

                        startPoint = new Point(dim.x, dim.y);
                        endPoint = new Point(dim.x + dim.width, dim.y
                                + dim.height);
                        selectedFilePathEdit = dim.selectedTile;


                        if(h_val == 0 || w_val == 0){
                            ArrayList<String> size = convertScale(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                                    (dim.xx * ((float) (wallWidth / screenWidth))),
                                    (dim.yy * ((float) (wallLength / screenHeight))));
                            mInterface.setCurrentSelection(String.format("%1.2f", dim.h), String.format("%1.2f", dim.w),
                                    tot_wall_tiles, Float.parseFloat(String.format("%1.2f", dim.l)),
                                    Float.parseFloat(String.format("%1.2f", dim.t)));

                            h_val = dim.h;
                            w_val = dim.w;
                            x_val = dim.l;
                            y_val = dim.t;
                        }
                        else{
                            ArrayList<String> size = convertScale(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
                                    (dim.xx * ((float) (wallWidth / screenWidth))),
                                    (dim.yy * ((float) (wallLength / screenHeight))));
                            mInterface.setCurrentSelection(size.get(0).toString(), size.get(1).toString(), tot_wall_tiles,
                                    Float.parseFloat(size.get(2)),  Float.parseFloat(size.get(3)));

                            h_val = Float.parseFloat(size.get(0));
                            w_val = Float.parseFloat(size.get(1));
                            x_val = Float.parseFloat(size.get(2));
                            y_val = Float.parseFloat(size.get(3));
                        }

                        if(!isAutoGridSelected) {
                            vInterface.changeView(false, true);
                        }

                        startPoint = new Point(dim.x, dim.y);
                        endPoint = new Point(dim.x + dim.width, dim.y
                                + dim.height);
                        invalidate();
                        // TODO Auto-generated method stub

                    }
                }
                break;
            }
            return true;

        }
    };

    public void deleteLayout(){
        if(selectedLayout != null) {
            LayoutDimensions dim = (LayoutDimensions) selectedLayout.getTag();
            if(dim.tilesUsed > 0) tot_wall_tiles -= dim.tilesUsed;
            if(selectedRelLay!=null)
                selectedRelLay.removeView(selectedLayout);
            startPoint = new Point();
            endPoint = new Point();
            // isTileSelected = false; 
            invalidate();
            vInterface.changeView(false, false);
            mInterface.setCurrentSelection("", "", tot_wall_tiles, 0 , 0);
        }
        else {
            dInterface.changeMessage("No Layout is available/selected to perform the action!", 0);
        }
    }

    public void checkEditSelection(String width1, String height, float x_pos, float y_pos, String flag){

        float x_pos_f = 0;
        float y_pos_f = 0;
        float screenWidth = canvas_W;
        float screenHeight = canvas_H;

        x_pos_f = (screenWidth / width) * x_pos;
        y_pos_f = (screenHeight / length) * y_pos;

        if(isDrawingSelected){
            if(startPoint.x < endPoint.x && startPoint.y < endPoint.y){
                editSelectionGrid(startPoint.x, startPoint.y, endPoint.x, endPoint.y, width1, height,
                        x_pos, y_pos, flag);
            }
            else{
                dInterface.changeMessage("Invalid values, cannot perform the action!", 0);
            }
        }
        else if(dimensions != null  &&  (Float.parseFloat(width1) > 0.0)  &&  (Float.parseFloat(height) > 0.0) ) {
            editSelection(x_pos_f, y_pos_f,
                    Math.round(x_pos_f) + dimensions.width, Math.round(y_pos_f) + dimensions.height,
                    selectedLayout, dimensions.rot, dimensions.layRot, width1, height, x_pos, y_pos);
        }
        else{
            dInterface.changeMessage("Invalid selection, cannot perform the action!", 0);
        }
    }

    public void checkMoveSelection(String width1, String height, float x_pos, float y_pos, int option){

        LayoutDimensions dim = (LayoutDimensions) selectedLayout.getTag();
        final float screenWidth = this.canvas_W;
        final float screenHeight = this.canvas_H;
        float x_diff, y_diff;
        float s1, s2, s3, c1, c2;
        unit = GlobalVariables.getDesignerUnit();

        s1 = (float) 5;
        s2 = (float) 3;
        s3 = (float) 1;
        c1 = (float) 20;
        c2 = (float) 10;

        /*x_pos = x_val;
        y_pos = y_val;*/
        x_pos = xx_val;
        y_pos = yy_val;
        x_diff = (float) (screenWidth - (x_pos + dim.width));
        y_diff = (float) (screenHeight - (y_pos + dim.height));

        switch (option) {
            case 1:
                if(y_diff > c1)
                    y_pos = y_pos + s1;
                else if(y_diff > c2)
                    y_pos = y_pos + s2;
                else
                    y_pos = y_pos + s3;
                break;
            case 2:
                if(x_diff > c1)
                    x_pos = x_pos + s1;
                else if(x_diff > c2)
                    x_pos = x_pos + s2;
                else
                    x_pos = x_pos + s3;
                break;
            case 3:
                if(x_pos > c1)
                    x_pos = x_pos - s1;
                else if(x_pos > c2)
                    x_pos = x_pos - s2;
                else
                    x_pos = x_pos - s3;
                break;
            case 4:
                if(y_pos > c1)
                    y_pos = y_pos - s1;
                else if(y_pos > c2)
                    y_pos = y_pos - s2;
                else
                    y_pos = y_pos - s3;
                break;
        }

        x_pos = (x_pos * ((float) (width / screenWidth)));
        y_pos = (y_pos * ((float) (width / screenWidth)));

//        x_pos = x_val;
//        y_pos = y_val;

        if(dimensions != null  &&  (Float.parseFloat(width1) > 0.0)  &&  (Float.parseFloat(height) > 0.0)
                && x_pos >= 0 && y_pos >= 0 ) {
            moveSelection(x_pos, y_pos, x_pos
                            + dimensions.width, y_pos + dimensions.height,
                    selectedLayout, dimensions.rot, dimensions.layRot, width1, height);
        }
        else{
            dInterface.changeMessage("Invalid Parameter, cannot perform the action!", 0);
        }
    }

    public void setBgforCurrentLay(String tile_used, RelativeLayout rel){
        resetPoint();
        if(selectedLayout != null) {
//            selectedLayout.setBackgroundColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= 23) {
                selectedLayout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
            }
            else{
                selectedLayout.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
            }
        }

        boolean doBreak = false;
        while (!doBreak) {
            int childCount = rel.getChildCount();
            int i;
            for(i=0; i<childCount; i++) {
                View currentChild = rel.getChildAt(i);
                // Change ImageView with your desired type view
                if (currentChild instanceof LinearLayout) {

                    LayoutDimensions dim = (LayoutDimensions) currentChild.getTag();

                    if (dim != null && (dim.selectedTile == null || dim.selectedTile.equals("null"))) {
                        rel.removeView(currentChild);
                        break;
                    }
                }
            }

            if (i == childCount) {
                doBreak = true;
            }
        }
    }

    public void setOldValues(){

        float n_x_val, n_y_val;
        final float wallWidth = Math.abs(width);
        final float screenWidth = this.canvas_W;

        if (unit.equalsIgnoreCase("Feet")) {

            n_x_val = GlobalVariables.mmToFeet(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToFeet(y_val * ((float) (wallWidth / screenWidth)));


        } else if (unit.equalsIgnoreCase("Inches")) {
            n_x_val = GlobalVariables.mmToInches(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToInches(y_val * ((float) (wallWidth / screenWidth)));
        }
        else{
            n_x_val = x_val;
            n_y_val = y_val;
        }

        mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                Float.parseFloat(String.format("%1.2f", n_x_val)),
                Float.parseFloat(String.format("%1.2f", n_y_val)));
        dInterface.changeMessage("Specified dimensions are not valid!", 0);
    }

    public void editSelectionGrid(float left, float top, int right, int bottom,
                                  String nwidth, String nheight, final float nleft, final float ntop, String flag){

        float leftString = 0, topString = 0, widthString = 0, heightString = 0;
        float ww = 0, hh = 0;
        float n_x_val = 0, n_y_val = 0;
        unit = GlobalVariables.getDesignerUnit();


        final String nw = nwidth;
        final String nh = nheight;

        final float wallWidth = Math.abs(width);
        final float wallLength = Math.abs(length);

        dInterface.changeMessage("", 0);

        final float screenWidth = this.canvas_W;
        final float screenHeight = this.canvas_H;

        leftString = (left * ((float) (wallWidth / screenWidth)));
        topString = (top * ((float) (wallLength / screenHeight)));
        widthString = (Math.abs(left - right) * ((float) (wallWidth / screenWidth)));
        heightString = Math
                .round((Math.abs(top - bottom) * ((float) (wallLength / screenHeight))));

        leftString = nleft;
        topString = ntop;

        if (unit.equalsIgnoreCase("Feet")) {
            if(!nwidth.equalsIgnoreCase(""))
                nwidth = String.format("%1.2f", (Double.parseDouble(nwidth) * 304.8f))+"";
            else
                nwidth = widthString+"";

            if(!nheight.equalsIgnoreCase(""))
                nheight = String.format("%1.2f", (Double.parseDouble(nheight) * 304.8f))+"";
            else
                nheight = heightString+"";

            leftString = leftString * 304.8f;
            topString = topString * 304.8f;

            n_x_val = GlobalVariables.mmToFeet(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToFeet(y_val * ((float) (wallWidth / screenWidth)));

            ww = GlobalVariables.mmToFeet(Float.parseFloat(nwidth));
            hh = GlobalVariables.mmToFeet(Float.parseFloat(nheight));


        } else if (unit.equalsIgnoreCase("Inches")) {
            if(!nwidth.equalsIgnoreCase(""))
                nwidth = String.format("%1.2f", (Double.parseDouble(nwidth) * 25.4f))+"";
            else
                nwidth = widthString+"";

            if(!nheight.equalsIgnoreCase(""))
                nheight = String.format("%1.2f", (Double.parseDouble(nheight) * 25.4f))+"";
            else
                nheight = heightString+"";

            leftString = leftString * 25.4f;
            topString = topString * 25.4f;

            n_x_val = GlobalVariables.mmToInches(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToInches(y_val * ((float) (wallWidth / screenWidth)));

            ww = GlobalVariables.mmToInches(Float.parseFloat(nwidth));
            hh = GlobalVariables.mmToInches(Float.parseFloat(nheight));
        }
        else{
            if(nwidth.equalsIgnoreCase(""))
                nwidth = widthString+"";

            if(nheight.equalsIgnoreCase(""))
                nheight = heightString+"";

            n_x_val = x_val * ((float) (wallWidth / screenWidth));
            n_y_val = y_val * ((float) (wallWidth / screenWidth));

            ww = Float.parseFloat(nwidth);
            hh = Float.parseFloat(nheight);
        }

        float l = 0;
        float t = 0;
        float w = 0;
        float h = 0;
        float r, layRot;
        String ls, ts, ws, hs, rs, layRots;
        String lss, tss, wss, hss, rss, layRotss;
        lss = leftString+"";
        tss = topString+"";
        wss = nwidth+"";
        hss = nheight+"";
        if (lss.length() > 0 && tss.length() > 0 && wss.length() > 0) {
            l = Float.parseFloat(lss);
            t = Float.parseFloat(tss);
            w = Float.parseFloat(nwidth);
            h = Float.parseFloat(nheight);


            l = (float) ((screenWidth / (float) width) * l);
            ls = String.format("%1.2f", l);
            l = Float.parseFloat(ls);
//            Log.d("DrawView", "l2 - "+ l);
            t = (float) ((screenHeight / (float) length) * t);
            ts = String.format("%1.2f", t);
            t = Float.parseFloat(ts);
            w = (float) ((screenWidth / (float) width) * w);
            ws = String.format("%1.2f", w);
            w = Float.parseFloat(ws);
            h = (float) ((screenHeight / (float) length) * h);
            hs = String.format("%1.2f", h);
            h = Float.parseFloat(hs);

            if (l + w > screenWidth || t + h > screenHeight) {

                if(flag.equals("w") && w <= screenWidth){
                    final float finalT = t;
                    final float finalH = h;
                    final float finalWw = ww;
                    final float finalW1 = w;
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Auto adjust")
                            .setContentText("Specified dimensions are not valid.  " +
                                    "Would you like to rearrange left margin of current selection to the start of Canvas?")
                            .setCancelText("Cancel")
                            .setConfirmText("Proceed")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
//                                        setNewVals(nh, nw, nleft, ntop, l, t, w, h);
                                    setNewVals(nh, nw, 0, ntop, 0, finalT, finalW1, finalH);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                                            Float.parseFloat(String.format("%1.2f", x_val)),
                                            Float.parseFloat(String.format("%1.2f", y_val)));

                                    return;
                                }
                            })
                            .show();
                }
                else if(flag.equals("h") && h <= screenHeight){
                    final float finalHh = hh;
                    final float finalH1 = h;
                    final float finalW = w;
                    final float finalL = l;
                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Auto adjust")
                            .setContentText("Specified dimensions are not valid.  " +
                                    "Would you like to rearrange top margin of current selection to the start of Canvas?")
                            .setCancelText("Cancel")
                            .setConfirmText("Proceed")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
//                                        setNewVals(nh, nw, nleft, ntop, l, t, w, h);
                                    setNewVals(nh, nw, nleft, 0, finalL, 0, finalW, finalH1);

                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                                            Float.parseFloat(String.format("%1.2f", x_val)),
                                            Float.parseFloat(String.format("%1.2f", y_val)));

                                    return;
                                }
                            })
                            .show();
                }
                else{
                    mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                            Float.parseFloat(String.format("%1.2f", x_val)),
                            Float.parseFloat(String.format("%1.2f", y_val)));
                    dInterface.changeMessage("Specified dimensions are not valid!", 0);

                    return;
                }
            }
            else{
                h_val = Float.parseFloat(nh);
                w_val = Float.parseFloat(nw);
                x_val = nleft;
                y_val = ntop;

                colorballs.get(0).setX(Math.round(l) - colorballs.get(0).getWidthOfBall() / 2);
                colorballs.get(1).setX(Math.round(l) - colorballs.get(1).getWidthOfBall() / 2);
                startPoint.x = Math.round(l);

                colorballs.get(0).setY(Math.round(t) - colorballs.get(0).getWidthOfBall() / 2);
                colorballs.get(3).setY(Math.round(t) - colorballs.get(3).getWidthOfBall() / 2);
                startPoint.y = 0;

                colorballs.get(2).setX((Math.round(l + w) - colorballs.get(2).getWidthOfBall() / 2));
                colorballs.get(3).setX((Math.round(l + w) - colorballs.get(3).getWidthOfBall() / 2));

                colorballs.get(2).setY((Math.round(t + h) - colorballs.get(2).getWidthOfBall() / 2));
                colorballs.get(1).setY((Math.round(t + h) - colorballs.get(1).getWidthOfBall() / 2));

                endPoint = new Point(Math.round(l + w), Math.round(t + h));

                invalidate();

                dInterface.changeMessage("", 0);
            }


            /*if (l + w > screenWidth || t + h > screenHeight) {
                mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                        Float.parseFloat(String.format("%1.2f", x_val)),
                        Float.parseFloat(String.format("%1.2f", y_val)));
                dInterface.changeMessage("Specified dimensions are not valid!", 0);

                return;
            }

            h_val = Float.parseFloat(nh);
            w_val = Float.parseFloat(nw);
            x_val = nleft;
            y_val = ntop;

            colorballs.get(0).setX(Math.round(l) - colorballs.get(0).getWidthOfBall() / 2);
            colorballs.get(1).setX(Math.round(l) - colorballs.get(1).getWidthOfBall() / 2);
            startPoint.x = Math.round(l);

            colorballs.get(0).setY(Math.round(t) - colorballs.get(0).getWidthOfBall() / 2);
            colorballs.get(3).setY(Math.round(t) - colorballs.get(3).getWidthOfBall() / 2);
            startPoint.y = 0;

            colorballs.get(2).setX((Math.round(l + w) - colorballs.get(2).getWidthOfBall() / 2));
            colorballs.get(3).setX((Math.round(l + w) - colorballs.get(3).getWidthOfBall() / 2));

            colorballs.get(2).setY((Math.round(t + h) - colorballs.get(2).getWidthOfBall() / 2));
            colorballs.get(1).setY((Math.round(t + h) - colorballs.get(1).getWidthOfBall() / 2));

            endPoint = new Point(Math.round(l + w), Math.round(t + h));

            invalidate();

            dInterface.changeMessage("", 0);*/

        } else {
            Toast.makeText(context, "Invalid Parameters; May be negative values!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void setNewVals(String nh, String nw, float nleft, float ntop, float l, float t, float w, float h) {
        h_val = Float.parseFloat(nh);
        w_val = Float.parseFloat(nw);
        x_val = nleft;
        y_val = ntop;

        colorballs.get(0).setX(Math.round(l) - colorballs.get(0).getWidthOfBall() / 2);
        colorballs.get(1).setX(Math.round(l) - colorballs.get(1).getWidthOfBall() / 2);
        startPoint.x = Math.round(l);

        colorballs.get(0).setY(Math.round(t) - colorballs.get(0).getWidthOfBall() / 2);
        colorballs.get(3).setY(Math.round(t) - colorballs.get(3).getWidthOfBall() / 2);
        startPoint.y = 0;

        colorballs.get(2).setX((Math.round(l + w) - colorballs.get(2).getWidthOfBall() / 2));
        colorballs.get(3).setX((Math.round(l + w) - colorballs.get(3).getWidthOfBall() / 2));

        colorballs.get(2).setY((Math.round(t + h) - colorballs.get(2).getWidthOfBall() / 2));
        colorballs.get(1).setY((Math.round(t + h) - colorballs.get(1).getWidthOfBall() / 2));

        endPoint = new Point(Math.round(l + w), Math.round(t + h));

        invalidate();

        mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                Float.parseFloat(String.format("%1.2f", x_val)),
                Float.parseFloat(String.format("%1.2f", y_val)));
        dInterface.changeMessage("", 0);
    }

    public void editSelection(float left, float top, int right, int bottom,
                              final LinearLayout layout, float rot, float layoutRotation,
                              String nwidth, String nheight, float nleft, float ntop){

        float leftString = 0, topString = 0, widthString = 0, heightString = 0;
        float n_x_val = 0, n_y_val = 0;
        unit = GlobalVariables.getDesignerUnit();

        String nw = nwidth;
        String nh = nheight;

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

        dInterface.changeMessage("", 0);

        final float screenWidth = this.canvas_W;
        final float screenHeight = this.canvas_H;

        leftString = (left * ((float) (wallWidth / screenWidth)));
        topString = (top * ((float) (wallLength / screenHeight)));
        widthString = (Math.abs(left - right) * ((float) (wallWidth / screenWidth)));
        heightString = Math
                .round((Math.abs(top - bottom) * ((float) (wallLength / screenHeight))));

        if (unit.equalsIgnoreCase("Feet")) {
            if(!nwidth.equalsIgnoreCase(""))
                nwidth = String.format("%1.2f", (Double.parseDouble(nwidth) * 304.8f))+"";
            else
                nwidth = widthString+"";

            if(!nheight.equalsIgnoreCase(""))
                nheight = String.format("%1.2f", (Double.parseDouble(nheight) * 304.8f))+"";
            else
                nheight = heightString+"";

            leftString = leftString * 304.8f;
            topString = topString * 304.8f;

            n_x_val = GlobalVariables.mmToFeet(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToFeet(y_val * ((float) (wallWidth / screenWidth)));


        } else if (unit.equalsIgnoreCase("Inches")) {
            if(!nwidth.equalsIgnoreCase(""))
                nwidth = String.format("%1.2f", (Double.parseDouble(nwidth) * 25.4f))+"";
            else
                nwidth = widthString+"";

            if(!nheight.equalsIgnoreCase(""))
                nheight = String.format("%1.2f", (Double.parseDouble(nheight) * 25.4f))+"";
            else
                nheight = heightString+"";

            leftString = leftString * 25.4f;
            topString = topString * 25.4f;

            n_x_val = GlobalVariables.mmToInches(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToInches(y_val * ((float) (wallWidth / screenWidth)));
        }
        else{
            if(nwidth.equalsIgnoreCase(""))
                nwidth = widthString+"";

            if(nheight.equalsIgnoreCase(""))
                nheight = heightString+"";

            n_x_val = x_val * ((float) (wallWidth / screenWidth));
            n_y_val = y_val * ((float) (wallWidth / screenWidth));
        }

        float l, t, w, h, r, layRot;
        String ls, ts, ws, hs, rs, layRots;
        String lss, tss, wss, hss, rss, layRotss;
        lss = leftString+"";
        tss = topString+"";
        wss = nwidth+"";
        hss = nheight+"";
        rss = rot+"";
        layRotss = layoutRotation+"";
        if (lss.length() > 0 && tss.length() > 0 && wss.length() > 0
                && hss.length() > 0 && rss.length() > 0 && layRotss.length()>0) {

            l = Float.parseFloat(lss);
            t = Float.parseFloat(tss);
            w = Float.parseFloat(nwidth);
            h = Float.parseFloat(nheight);
            r = Float.parseFloat(rss);

            layRot = Float.parseFloat(layRotss);

            ratio = width / length;
            if (ratio > 1) {
                ratio = 1 / ratio;
                // rwidth = (ratio * viewArea);
                rlength = (ratio * viewArea);
                rwidth = viewArea;
            } else {
                rwidth = (ratio * viewArea);
                rlength = viewArea;
            }

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
            layRots = String.format("%1.2f", layRot);
            layRot = Float.parseFloat(layRots);

            if (l + w > screenWidth || t + h > screenHeight) {
                mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                        Float.parseFloat(String.format("%1.2f", x_val)),
                        Float.parseFloat(String.format("%1.2f", y_val)));
                /*mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                        Float.parseFloat(String.format("%1.2f", (x_val * ((float) (wallWidth / screenWidth))))),
                        Float.parseFloat(String.format("%1.2f", (y_val * ((float) (wallWidth / screenWidth))))));*/
                dInterface.changeMessage("Specified dimensions are not valid!", 0);
                return;
            }
            int position = 0;
            for (int i = 0; i < rel.getChildCount(); i++) {
                if (rel.getChildAt(i).equals(layout)) {
                    position = i;
                }
            }

            dInterface.changeMessage("", 0);

            h_val = Float.parseFloat(nh);
            w_val = Float.parseFloat(nw);
            x_val = nleft;
            y_val = ntop;

            ArrayList<String> mm_mes = convertTomm();

            LayoutParams param = new LayoutParams(Math.round(w), Math
                    .round(h));
            layout.setLayoutParams(param);

            LayoutParams params = new LayoutParams(
                    Math.round(w), Math.round(h));
            // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
            params.leftMargin = Math.round(l);
            params.topMargin = Math.round(t);
            LayoutDimensions dim = (LayoutDimensions) layout.getTag();
            dim.x = Math.round(l);
            dim.y = Math.round(t);
            dim.xx = l;
            dim.yy = t;
            dim.width = Math.round(w);
            dim.height = Math.round(h);
            dim.rot = r;
            dim.layRot = layRot;

            dim.w = w_val;
            dim.h = h_val;
            dim.l = x_val;
            dim.t = y_val;

            layout.setTag(dim);
            layout.setRotation(layRot);
            layout.setOnTouchListener(layoutTouched);
            layout.setLayoutParams(params);



            startPoint = new Point(Math.round(l), Math.round(t));
            endPoint = new Point(Math.round(l + w), Math.round(t + h));
            invalidate();


            if (dim.selectedTile != null && dim.selectedTile.length() > 0) {
                Bitmap bm;//= selectedTile;
                bm = BitmapFactory.decodeFile(dim.selectedTile).copy(Bitmap.Config.ARGB_8888, true);

                fillTileInLayout(layout, true, bm,
                        dim.rot, dim.selectedTile, 1);
            }
        } else {
            Toast.makeText(context, "Invalid Parameters; May be negative values!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void moveSelection(float left, float top, float right, float bottom,
                              final LinearLayout layout, float rot, float layoutRotation,
                              String nwidth, String nheight){

        float leftString = 0, topString = 0, widthString = 0, heightString = 0;
        float n_left= 0, n_top =0, n_x_val = 0, n_y_val =0;
        unit = GlobalVariables.getDesignerUnit();

        String nw = nwidth;
        String nh = nheight;

        final float wallWidth = Math.abs(width);

        final float wallLength = Math.abs(length);

        float ratio = width / length;
        float rlength, rwidth;
        if (ratio > 1) {
            ratio = 1 / ratio;
            rlength = (ratio * viewArea);
            rwidth = viewArea;
        } else {
            rwidth = (ratio * viewArea);
            rlength = viewArea;
        }

        dInterface.changeMessage("", 0);

        final float screenWidth = this.canvas_W;
        final float screenHeight = this.canvas_H;

        leftString = left;
        topString = top;

        widthString = (Math.abs(left - right) * ((float) (wallWidth / screenWidth)));
        heightString = Math
                .round((Math.abs(top - bottom) * ((float) (wallLength / screenHeight))));

        if (unit.equalsIgnoreCase("Feet")) {
            if(!nwidth.equalsIgnoreCase(""))
                nwidth = String.format("%1.2f", (Double.parseDouble(nwidth) * 304.8f))+"";
            else
                nwidth = widthString+"";

            if(!nheight.equalsIgnoreCase(""))
                nheight = String.format("%1.2f", (Double.parseDouble(nheight) * 304.8f))+"";
            else
                nheight = widthString+"";

            n_left = GlobalVariables.mmToFeet(leftString);
            n_top = GlobalVariables.mmToFeet(topString);

            n_x_val = GlobalVariables.mmToFeet(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToFeet(y_val * ((float) (wallWidth / screenWidth)));

        } else if (unit.equalsIgnoreCase("Inches")) {
            if(!nwidth.equalsIgnoreCase(""))
                nwidth = String.format("%1.2f", (Double.parseDouble(nwidth) * 25.4f))+"";
            else
                nwidth = heightString+"";

            if(!nheight.equalsIgnoreCase(""))
                nheight = String.format("%1.2f", (Double.parseDouble(nheight) * 25.4f))+"";
            else
                nheight = heightString+"";

            n_left = GlobalVariables.mmToInches(leftString);
            n_top = GlobalVariables.mmToInches(topString);

            n_x_val = GlobalVariables.mmToInches(x_val * ((float) (wallWidth / screenWidth)));
            n_y_val = GlobalVariables.mmToInches(y_val * ((float) (wallWidth / screenWidth)));

        }
        else{
            if(nwidth.equalsIgnoreCase(""))
                nwidth = heightString+"";

            if(nheight.equalsIgnoreCase(""))
                nheight = heightString+"";

            n_left = leftString;
            n_top = topString;

            n_x_val = x_val * ((float) (wallWidth / screenWidth));
            n_y_val = y_val * ((float) (wallWidth / screenWidth));
        }

        float l, t, w, h, r, layRot;
        String ls, ts, ws, hs, rs, layRots;
        String lss, tss, wss, hss, rss, layRotss;
        lss = leftString+"";
        tss = topString+"";
        wss = nwidth+"";
        hss = nheight+"";
        rss = rot+"";
        layRotss = layoutRotation+"";
        if (lss.length() > 0 && tss.length() > 0 && wss.length() > 0
                && hss.length() > 0 && rss.length() > 0 && layRotss.length()>0) {

            l = Float.parseFloat(lss);
            t = Float.parseFloat(tss);
            w = Float.parseFloat(nwidth);
            h = Float.parseFloat(nheight);
            r = Float.parseFloat(rss);

            layRot = Float.parseFloat(layRotss);

            ratio = width / length;
            if (ratio > 1) {
                ratio = 1 / ratio;
                // rwidth = (ratio * viewArea);
                rlength = (ratio * viewArea);
                rwidth = viewArea;
            } else {
                rwidth = (ratio * viewArea);
                rlength = viewArea;
            }

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
            layRots = String.format("%1.2f", layRot);
            layRot = Float.parseFloat(layRots);

            if (l + w > screenWidth || t + h > screenHeight) {
                mInterface.setCurrentSelection(h_val+"", w_val+"", tot_wall_tiles,
                        Float.parseFloat(String.format("%1.2f", n_x_val)),
                        Float.parseFloat(String.format("%1.2f", n_y_val)));
                return;
            }
            int position = 0;
            for (int i = 0; i < rel.getChildCount(); i++) {
                if (rel.getChildAt(i).equals(layout)) {
                    position = i;
                }
            }

            dInterface.changeMessage("", 0);

            LayoutParams param = new LayoutParams(Math.round(w), Math
                    .round(h));
            layout.setLayoutParams(param);

            LayoutParams params = new LayoutParams(
                    Math.round(w), Math.round(h));
            params.leftMargin = Math.round(l);
            params.topMargin = Math.round(t);
            LayoutDimensions dim = (LayoutDimensions) layout.getTag();
//            Log.d("DrawView", "l3 - "+ Math.round(l));
            dim.x = Math.round(l);
            dim.y = Math.round(t);
            dim.xx = l;
            dim.yy = t;
            dim.width = Math.round(w);
            dim.height = Math.round(h);
            dim.rot = r;
            dim.layRot = layRot;

            dim.h = Float.parseFloat(nh);
            dim.w = Float.parseFloat(nw);
            dim.l = left;
            dim.t = top;

            layout.setTag(dim);
            layout.setRotation(layRot);
            layout.setOnTouchListener(layoutTouched);
            layout.setLayoutParams(params);

            h_val = Float.parseFloat(nh);
            w_val = Float.parseFloat(nw);
            x_val = Math.round(left);
            y_val = Math.round(top);

            xx_val = Math.round(l);
            yy_val = Math.round(t);


            startPoint = new Point(Math.round(l), Math.round(t));
            endPoint = new Point(Math.round(l + w), Math.round(t + h));

            mInterface.setCurrentSelection("", "", tot_wall_tiles, n_left, n_top);

            invalidate();
        } else {
            Toast.makeText(context, "Invalid Parameters!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void setClickableAnimation(final Context context, final View view) {
        final int[] attrs = new int[]{R.attr.selectableItemBackground};
        final TypedArray typedArray = context.obtainStyledAttributes(attrs);
        final int backgroundResource = typedArray.getResourceId(0, 0);
        view.setBackgroundResource(backgroundResource);
        typedArray.recycle();
    }


    public void applyPattern(){
        final LinearLayout layout = (LinearLayout) selectedLayout;
        if(selectedLayout != null) {
            if (isTileSelected) {

                Bitmap bm = selectedTile;
                final LayoutDimensions dim = (LayoutDimensions) layout
                        .getTag();

                selectedFilePathEdit = selectedFilePath;
                if (!isTilePatterning) {
                    fillTileInLayout(layout, true, bm, dim.rot, selectedFilePath, 0);
                } else {
                    fillCustomTileInLayout(layout, true, bm, dim.rot, selectedFilePath);
                }
            }
            else{
                dInterface.changeMessage("No Tile available/selected to perform the action!", 0);
            }
        }
        else{
            dInterface.changeMessage("No Layout is available/selected to perform the action!", 0);
        }
    }

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
//					if(dmd_design_item.equals(""))dmd_design_item="0";
                    currentValue = Double.parseDouble(test);

                    double newValue = currentValue * 3.28084f;
                    leftText.setText(newValue + "");

                    currentValue = Double.parseDouble(topText.getText()
                            .toString());
                    newValue = currentValue * 3.28084f;
                    topText.setText(newValue + "");

                    currentValue = Double.parseDouble(widthText.getText()
                            .toString());
                    newValue = currentValue * 3.28084f;
                    widthText.setText(newValue + "");

                    currentValue = Double.parseDouble(heightText.getText()
                            .toString());
                    newValue = currentValue * 3.28084f;
                    heightText.setText(newValue + "");
                    length *= 3.28084f;
                    width *= 3.28084f;

                } else if (unit.equalsIgnoreCase("Inches")) {
                    currentValue = Double.parseDouble(leftText.getText()
                            .toString());
                    double newValue = currentValue * 0.0833333;
                    leftText.setText(newValue + "");

                    currentValue = Double.parseDouble(topText.getText()
                            .toString());
                    newValue = currentValue * 0.0833333;
                    topText.setText(newValue + "");

                    currentValue = Double.parseDouble(widthText.getText()
                            .toString());
                    newValue = currentValue * 0.0833333;
                    widthText.setText(newValue + "");

                    currentValue = Double.parseDouble(heightText.getText()
                            .toString());
                    newValue = currentValue * 0.0833333;
                    heightText.setText(newValue + "");
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
                    leftText.setText(newValue + "");

                    currentValue = Double.parseDouble(topText.getText()
                            .toString());
                    newValue = currentValue * 0.3048;
                    topText.setText(newValue + "");

                    currentValue = Double.parseDouble(widthText.getText()
                            .toString());
                    newValue = currentValue * 0.3048;
                    widthText.setText(newValue + "");

                    currentValue = Double.parseDouble(heightText.getText()
                            .toString());
                    newValue = currentValue * 0.3048;
                    heightText.setText(newValue + "");
                    length *= 0.3048f;
                    width *= 0.3048f;

                } else if (unit.equalsIgnoreCase("Inches")) {
                    currentValue = Double.parseDouble(leftText.getText()
                            .toString());
                    double newValue = currentValue * 0.0254;
                    leftText.setText(newValue + "");

                    currentValue = Double.parseDouble(topText.getText()
                            .toString());
                    newValue = currentValue * 0.0254;
                    topText.setText(newValue + "");

                    currentValue = Double.parseDouble(widthText.getText()
                            .toString());
                    newValue = currentValue * 0.0254;
                    widthText.setText(newValue + "");

                    currentValue = Double.parseDouble(heightText.getText()
                            .toString());
                    newValue = currentValue * 0.0254;
                    heightText.setText(newValue + "");
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
                    double newValue = currentValue * 12;
                    leftText.setText(newValue + "");

                    currentValue = Double.parseDouble(topText.getText()
                            .toString());
                    newValue = currentValue * 12;
                    topText.setText(newValue + "");

                    currentValue = Double.parseDouble(widthText.getText()
                            .toString());
                    newValue = currentValue * 12;
                    widthText.setText(newValue + "");

                    currentValue = Double.parseDouble(heightText.getText()
                            .toString());
                    newValue = currentValue * 12;
                    heightText.setText(newValue + "");
                    length *= 12f;
                    width *= 12f;

                } else if (unit.equalsIgnoreCase("Meters")) {
                    currentValue = Double.parseDouble(leftText.getText()
                            .toString());
                    double newValue = currentValue * 39.3701;
                    leftText.setText(newValue + "");

                    currentValue = Double.parseDouble(topText.getText()
                            .toString());
                    newValue = currentValue * 39.3701;
                    topText.setText(newValue + "");

                    currentValue = Double.parseDouble(widthText.getText()
                            .toString());
                    newValue = currentValue * 39.3701;
                    widthText.setText(newValue + "");

                    currentValue = Double.parseDouble(heightText.getText()
                            .toString());
                    newValue = currentValue * 39.3701;
                    heightText.setText(newValue + "");
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

    void fillCustomTileInLayout(LinearLayout layout, Boolean shouldFill,
                                Bitmap bitmap,float rotation, String tilePath) {
        if (shouldFill) {

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

            dim.selectedTile = selectedFilePath;
            dim.orientation = axis;
            dim.tile_id = tile_id;
//            dim.tile_type = "P";
            dim.tile_type = tile_type;
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

            if(rotation % 360 == 0){
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
                        BitmapDrawable ob = new BitmapDrawable(getResources(),
                                bitmap);
                        img.setBackground(ob);
                        img.setScaleType(ScaleType.FIT_XY);
                        // img.setImageBitmap(bitmap);
                        img.setImageResource(R.drawable.imgbckgnd);
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
                        BitmapDrawable ob = new BitmapDrawable(getResources(),
                                bitmap);
                        img.setBackground(ob);
                        img.setScaleType(ScaleType.FIT_XY);
                        // img.setImageBitmap(bitmap);
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
                            int newWidth = (int) tileWidth;
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
                            img.setImageResource(R.drawable.imgbckgnd);
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
                            BitmapDrawable ob = new BitmapDrawable(getResources(),
                                    bitmap);
                            img.setBackground(ob);
                            img.setScaleType(ScaleType.FIT_XY);
                            // img.setImageBitmap(bitmap);
                            img.setImageResource(R.drawable.imgbckgnd);
                            img.setLayoutParams(new LayoutParams((int) tileWidth,
                                    (int) tileHeight));
                            img.setClickable(false);
                            linearLayout.addView(img);
                        }
                    }
                }
            }else {

                float ratioLocal = dim.width / dim.height;
                float layWidthmm, layHeightmm;

                if (ratio > 1) {
                    ratio = 1 / ratioLocal;

                    layWidthmm = horiDim
                            * ((float) dim.width / (float) GlobalVariables
                            .getDrawArea(context));
                    layHeightmm = horiDim
                            * ((float) dim.height / (float) GlobalVariables
                            .getDrawArea(context));

                } else {

                    layWidthmm = vertDim
                            * ((float) dim.width / (float) GlobalVariables
                            .getDrawArea(context));
                    layHeightmm = vertDim
                            * ((float) dim.height / (float) GlobalVariables
                            .getDrawArea(context));
                }




                Bitmap bm = GlobalVariables.getRotatedPattern(bitmap,
                        dim.tileWidth, dim.tileHeight, layWidthmm, layHeightmm,
                        dim.rot, context, true);
                ImageView imgV = new ImageView(context);
                imgV.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                imgV.setScaleType(ScaleType.FIT_XY);
                imgV.setImageBitmap(bm);
                layout.addView(imgV);

            }
        }
    }

    void fillTileInLayout(LinearLayout layout, Boolean shouldFill,
                          Bitmap bitmap, float rotation, String tilePath, int flag) {
        if (shouldFill) {

            LayoutDimensions dim = (LayoutDimensions) layout.getTag();
            DatabaseHandler dh = new DatabaseHandler(context);

            String size = "0x0";
            float tH = 0;
            float tW = 0;
            float sH = dim.h;
            float sW = dim.w;


            if (tilePath != null) {
                if(this.tile_type.equals("P")) {
                    size = dh.getPatternSize(this.tile_id);
                }
                else{
                    size = dh.getTileSize(tilePath);
                }

                if (size.equals("")) {
                    size = "0x0";
                }
            }

            tot_wall_tiles -= dim.tilesUsed;

            String[] tileDim = size.split("x");
            if (tileDim.length == 0) {
                tileDim = size.split("X");
            }
            dim.tileWidth = Float.valueOf(tileDim[0]);
            dim.tileHeight = Float.valueOf(tileDim[1]);

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

            float wallWidth = GlobalVariables.getWallWidth();
            float tileWidth = w;
            // float screenHeight = viewArea;
            float wallHeight = GlobalVariables.getWallHeight();
            float wallLength = GlobalVariables.getWallLength();
            float wallC = GlobalVariables.getWallC();
            float wallD = GlobalVariables.getWallD();

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

            if (selectedFilePathEdit != null
                    && !selectedFilePathEdit.equalsIgnoreCase("")
                    && !selectedFilePathEdit.equalsIgnoreCase("null") && flag == 1) {
                dim.selectedTile = selectedFilePathEdit;
            }

            dim.orientation = axis;
            dim.tile_id = tile_id;
            dim.tile_type = tile_type;

            // layout.setTag(dim);
            String currentProject = GlobalVariables.getProjectName();
            int extraTiles = 0;
            float horiDim = wallWidth, vertDim = wallHeight;
            if (wall.equalsIgnoreCase("front")) {
                horiDim = wallWidth;
                vertDim = wallHeight;
            } else if (wall.equalsIgnoreCase("back")) {
                horiDim = wallWidth;
                vertDim = wallHeight;

            } else if (wall.equalsIgnoreCase("left")) {
                horiDim = wallLength;
                vertDim = wallHeight;
            } else if (wall.equalsIgnoreCase("right")) {
                horiDim = wallLength;
                vertDim = wallHeight;

            } else if (wall.equalsIgnoreCase("top")
                    || wall.equalsIgnoreCase("bottom")) {
                horiDim = wallWidth;
                vertDim = wallLength;

                // calculate area of remaining triangle and calculate tiles
                // needed for that .then subtract this from total tiles

            } else if (wall.equalsIgnoreCase("frontleft")) {
                horiDim = (float) (Math.sqrt(Math.pow((wallWidth - wallC), 2)
                        + Math.pow((wallLength - wallD), 2)));
                vertDim = wallHeight;
            }

            float dimW = (dim.width) * horiDim / (float) screenWidth;
            // float dimW = (dim.width);
            String dimWs = String.format("%1.2f", dimW);
            dimW = Float.parseFloat(dimWs);
            float dimH = (dim.height) * vertDim / (float) screenHeight;
            // float dimH = (dim.height);
            String dimHs = String.format("%1.2f", dimH);
            dimH = Float.parseFloat(dimHs);
            long fillCountHori = (int) (dimW / tileWidth);
            Float asd;
            if (dimW % tileWidth > 0) {
                fillCountHori++;
            }

            long fillCountVerti = (int) (dimH / tileHeight);
            Float asdf;
            if (dimH % tileHeight > 0) {
                fillCountVerti++;
            }

            tH = tileHeight;
            tW = tileWidth;

//            Log.d("Disp", "tileWidth * tileHeight "+ dim.width +"_" +dim.height);

            long count = (int) ((dimW * dimH) / (tileWidth * tileHeight));
            float fl;
            if ((dimW * dimH) % (tileWidth * tileHeight) > 0) {
                count++;
                /*if ((fl = (dimW * dimH) % ((tileWidth * tileHeight) * count))
                        / (dimW * dimH) > 0.1
                        || count == 0) {
                    count++;
                }*/
            }
            tileWidth = screenWidth * w / (float) (horiDim);
            String tileWidths = String.format("%.2f", tileWidth);
            tileWidth = Float.parseFloat(tileWidths);

            tileHeight = screenHeight * h / (float) vertDim;
            String tileHeights = String.format("%.2f", tileHeight);
            tileHeight = Float.parseFloat(tileHeights);

            long tilesUsed = 0;
            layout.removeAllViews();

            if (rotation % 360 == 0) {

                for (long i = 0; i < fillCountVerti; i++) {
                    layout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setBackgroundColor(Color.GREEN);
                    linearLayout.setClickable(false);
                    LayoutParams param = new LayoutParams((int) (dim.width),
                            (int) (tileHeight));
                    linearLayout.setLayoutParams(param);
                    layout.addView(linearLayout);
                    for (long j = 0; j < fillCountHori; j++) {
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
                        img.setImageResource(R.drawable.imgbckgnd);
                        img.setLayoutParams(new LayoutParams((int) tileWidth,
                                (int) tileHeight));
                        img.setClickable(false);
                        linearLayout.addView(img);
                        tilesUsed++;
                        ob = null;
                    }
                    if (tileWidth * fillCountHori < viewArea + 10) {
                        float newWidth = tileWidth;
                        float scaleFactor = (float) newWidth / (float) w;
                        int newHeight = (int) (h * scaleFactor);

                        // Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
                        // newWidth,
                        // newHeight, true);

                        BitmapDrawable ob = new BitmapDrawable(getResources(),
                                bitmap);
                        ImageView img = new ImageView(context);
                        img.setBackground(ob);
                        img.setScaleType(ScaleType.FIT_XY);
                        // img.setImageBitmap(bitmap);
                        img.setImageResource(R.drawable.imgbckgnd);
                        img.setLayoutParams(new LayoutParams((int) tileWidth,
                                (int) tileHeight));
                        img.setClickable(false);
                        linearLayout.addView(img);
                        ob = null;
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

                            BitmapDrawable ob = new BitmapDrawable(
                                    getResources(), bitmap);
                            ImageView img = new ImageView(context);
                            img.setBackground(ob);
                            img.setScaleType(ScaleType.FIT_XY);
                            // img.setImageBitmap(bitmap);
                            img.setImageResource(R.drawable.imgbckgnd);
                            img.setLayoutParams(new LayoutParams(
                                    (int) tileWidth, (int) tileHeight));
                            img.setClickable(false);
                            linearLayout.addView(img);
                            ob = null;

                        }
                        if (tileWidth * fillCountHori < viewArea + 10) {
                            float newWidth = tileWidth;
                            float scaleFactor = (float) newWidth / (float) w;
                            float newHeight = (float) (h * scaleFactor);

                            // Bitmap bmp = Bitmap.createScaledBitmap(bitmap,
                            // newWidth,
                            // newHeight, true);

                            BitmapDrawable ob = new BitmapDrawable(
                                    getResources(), bitmap);
                            ImageView img = new ImageView(context);
                            img.setBackground(ob);
                            img.setScaleType(ScaleType.FIT_XY);
                            // img.setImageBitmap(bitmap);
                            img.setImageResource(R.drawable.imgbckgnd);
                            img.setLayoutParams(new LayoutParams(
                                    (int) tileWidth, (int) tileHeight));
                            img.setClickable(false);
                            linearLayout.addView(img);
                            ob = null;
                        }
                    }
                }

            } else {

                float ratioLocal = dim.width / dim.height;
                float layWidthmm, layHeightmm;

                if (ratio > 1) {
                    ratio = 1 / ratioLocal;

                    layWidthmm = horiDim
                            * ((float) dim.width / (float) GlobalVariables
                            .getDrawArea(context));
                    layHeightmm = horiDim
                            * ((float) dim.height / (float) GlobalVariables
                            .getDrawArea(context));

                } else {

                    layWidthmm = vertDim
                            * ((float) dim.width / (float) GlobalVariables
                            .getDrawArea(context));
                    layHeightmm = vertDim
                            * ((float) dim.height / (float) GlobalVariables
                            .getDrawArea(context));
                }

                Bitmap bm = GlobalVariables.getRotatedPattern(bitmap,
                        dim.tileWidth, dim.tileHeight, layWidthmm, layHeightmm,
                        dim.rot, context, true);
                ImageView imgV = new ImageView(context);
                imgV.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                imgV.setScaleType(ScaleType.FIT_XY);
                imgV.setImageBitmap(bm);
                layout.addView(imgV);

                /*if (bm != null && !bm.isRecycled()) {
                    bm.recycle();
                    bm = null;
                }*/
            }

            /*if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }*/

            dim.tilesUsed = (int) count;
            dim.area = dimW * dimH;
            layout.setTag(dim);

            calcTotalTiles(sW, sH, tW, tH, tile_id);

            //tot_wall_tiles += count;
        }

        cInterface.setTotalCount(tot_wall_tiles);
    }

    Map<String,String> tileMap = new HashMap<>();

    private void calcTotalTiles(float dimW, float dimH, float tileWidth, float tileHeight, String tile_id){

        if(tileMap.containsKey(tile_id)){
            String[] tmp = tileMap.get(tile_id).toString().split("#");
            tileMap.put(tile_id, ( Float.parseFloat(tmp[0]) + (float) (dimW * dimH))
                    +"#"+(tileWidth * tileHeight));
        }
        else{
            tileMap.put(tile_id, (dimW * dimH)+"#"+(tileWidth * tileHeight));
        }

        if(tileMap.size() > 0) {
            Iterator it = tileMap.entrySet().iterator();
            tot_wall_tiles = 0;

            while (it.hasNext()) {
                String[] tmp;
                int count = 0;
                Map.Entry pair = (Map.Entry) it.next();

                tmp = pair.getValue().toString().split("#");

                count = (int) (Float.parseFloat(tmp[0]) / Float.parseFloat(tmp[1]));
                float fl;
                if ((Float.parseFloat(tmp[0]) % Float.parseFloat(tmp[1]) ) > 0) {
                    count++;
                }

                tot_wall_tiles += count;
            }
        }
    }

    class LayoutDimensions {
        String id;
        int x, y, height;
        float xx, yy;
        int width;
        float area;
        float rot,layRot;
        String selectedTile;
        String tile_id;
        String tile_type;
        int orientation;
        int tilesUsed;
        float tileWidth, tileHeight;
        float h, w, l, t;
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

                xmlSerializer.startTag(null, "xx");
                xmlSerializer.text(dim.xx + "");
                xmlSerializer.endTag(null, "xx");

                xmlSerializer.startTag(null, "yy");
                xmlSerializer.text(dim.yy + "");
                xmlSerializer.endTag(null, "yy");

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

                xmlSerializer.startTag(null, "tile_id");
                xmlSerializer.text(dim.tile_id + "");
                xmlSerializer.endTag(null, "tile_id");

                xmlSerializer.startTag(null, "tile_type");
                xmlSerializer.text(dim.tile_type + "");
                xmlSerializer.endTag(null, "tile_type");

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

                xmlSerializer.startTag(null, "tilesUsed");
                xmlSerializer.text(dim.tilesUsed + "");
                xmlSerializer.endTag(null, "tilesUsed");

                xmlSerializer.startTag(null, "mmW");
                xmlSerializer.text(dim.w + "");
                xmlSerializer.endTag(null, "mmW");

                xmlSerializer.startTag(null, "mmH");
                xmlSerializer.text(dim.h + "");
                xmlSerializer.endTag(null, "mmH");

                xmlSerializer.startTag(null, "mmL");
                xmlSerializer.text(dim.l + "");
                xmlSerializer.endTag(null, "mmL");

                xmlSerializer.startTag(null, "mmT");
                xmlSerializer.text(dim.t + "");
                xmlSerializer.endTag(null, "mmT");

                xmlSerializer.endTag(null, "layoutData");

            }
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            out.write(dataWrite);
            out.close();
            return true;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Could not save file!",
                    Toast.LENGTH_SHORT).show();
            // Log.e("Save Layout Error", e.printStackTrace());
            e.printStackTrace();
            return false;
        }
    }

    public boolean chkMaxGridSize(float size){
        float wdth = 0, hght = 0;

        if (unit.equalsIgnoreCase("Feet")) {
            wdth = GlobalVariables.mmToFeet(this.width);
            hght = GlobalVariables.mmToFeet(this.length);
        } else if (unit.equalsIgnoreCase("Inches")) {
            wdth = GlobalVariables.mmToInches(this.width);
            hght = GlobalVariables.mmToInches(this.length);
        } else{
            wdth = this.width;
            hght = this.length;
        }

        if(size > wdth || size > hght){
            return false;
        }
        return true;
    }

    public void fillGrid(float sizeW, float sizeH){

        float tmp = 0;
        float sX = 0, eX = 0, sY = 0, eY = 0;
        int row = 1, col = 1, col_end = 0;
        float wdth = 0, hght = 0;

        cellList.clear();

        if (unit.equalsIgnoreCase("Feet")) {
            wdth = GlobalVariables.mmToFeet(this.width);
            hght = GlobalVariables.mmToFeet(this.length);
        } else if (unit.equalsIgnoreCase("Inches")) {
            wdth = GlobalVariables.mmToInches(this.width);
            hght = GlobalVariables.mmToInches(this.length);
        } else{
            wdth = this.width;
            hght = this.length;
        }

        tmp = (hght / sizeH) + 1;

        while(wdth >= eX && row <= tmp){

            if(col == 1) {
                if(row == (int) tmp)
                    eY = (int) hght;
                else
                    eY = eY + sizeH;
                eX = eX + sizeW;
            } else{
                if(wdth - eX >= sizeW){
                    sX = sX+sizeW;
                    eX = eX+sizeW;
                } else{
                    sX = sX+sizeW;
                    eX = (int) wdth;
                    col_end = 1;
                }
            }

            w_val = eX - sX;
            h_val = eY - sY;
            x_val = sX;
            y_val = sY;

            startPoint = new Point(convertPoints(sX), convertPoints(sY));
            endPoint = new Point(convertPoints(eX), convertPoints(eY));
            addLayoutAutoGrid(row+"-"+col);

            if(col_end == 1){
                col = 1;
                sX = 0;
                sY = sY + sizeH;
                eX = 0;

                col_end = 0;
                row++;
            } else{
                col++;
            }
        }
    }

    public int convertPoints(float val){
        float temp;
        int ret;

        if (unit.equalsIgnoreCase("Feet")) {
            val = val * 304.8f;
        } else if (unit.equalsIgnoreCase("Inches")) {
            val = val * 25.4f;
        }
        else{

        }

        temp = (float) ((this.canvas_W / (float) width) * val);
        ret = Math.round(temp);

        return ret;
    }

    public void addLayoutAutoGrid(String id) {
        rel = (RelativeLayout) this.getParent();
        {
            float wallWidth = Math.abs(width);
            float wallLength = Math.abs(length);
            float screenWidth = canvas_W;
            float screenHeight = canvas_H;

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF); //white background
            border.setStroke(1, 0x7c000000); //black border with full opacity
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                linearLayout.setBackgroundDrawable(border);
            } else {
                linearLayout.setBackground(border);
            }

            LayoutParams param = new LayoutParams(Math.abs(startPoint.x
                    - endPoint.x), Math.abs(startPoint.y - endPoint.y));
            linearLayout.setLayoutParams(param);
            linearLayout.setClickable(false);
            linearLayout.setOnTouchListener(layoutTouched);

            ArrayList<String> mm_mes = convertTomm();

            LayoutParams params = new LayoutParams(
                    Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y
                    - endPoint.y));
            // params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,-1);
            params.leftMargin = startPoint.x;
            params.topMargin = startPoint.y;
            LayoutDimensions dim = new LayoutDimensions();
            dim.id = id;
            dim.x = startPoint.x;
            dim.y = startPoint.y;
            dim.xx = startPoint.x;
            dim.yy = startPoint.y;
            dim.width = Math.abs(startPoint.x - endPoint.x);
            dim.height = Math.abs(startPoint.y - endPoint.y);
            dim.rot = 0;
            dim.layRot = 0;

            dim.w = w_val;
            dim.h = h_val;
            dim.l = x_val;
            dim.t = y_val;

            linearLayout.setTag(dim);

//            selectedLayout = linearLayout;

            rel.setClickable(true);
            rel.addView(linearLayout, params);
            linearLayout.bringToFront();

            linearLayout.dispatchTouchEvent(motionEvent);

            if (!isTilePatterning) {
                if (unit != null) {
                    origUnit = unit;
                    GlobalVariables.setUnit(unit);
                }
            } else {
            }
        }

    }

    public void applyPatternGrid(){

        if(!cellList.isEmpty()){

            for (int i = 0; i < rel.getChildCount(); i++) {

                View v = rel.getChildAt(i);
                if (v instanceof LinearLayout) {

                    LayoutDimensions dimV = (LayoutDimensions) v.getTag();
                    if(cellList.contains(dimV.id) /*&& dimV.selectedTile == null*/){

                        selectedLayout = (LinearLayout) v;
                        final LinearLayout layout = (LinearLayout) selectedLayout;
                        if(selectedLayout != null) {

                            if (isTileSelected) {
                                Bitmap bm = selectedTile;
                                final LayoutDimensions dim = (LayoutDimensions) layout
                                        .getTag();

                                selectedFilePathEdit = selectedFilePath;
                                if (!isTilePatterning) {
                                    fillTileInLayout(layout, true, bm, dim.rot, selectedFilePath, 0);
                                } else {
                                    fillCustomTileInLayout(layout, true, bm, dim.rot, selectedFilePath);
                                }

                                if (Build.VERSION.SDK_INT >= 23) {
                                    layout.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                                }
                                else{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        layout.setBackground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                                    }
                                }
                            }
                            else{
                                dInterface.changeMessage("No Tile available/selected to perform the action!", 0);
                            }
                        }
                        else{
                            dInterface.changeMessage("No Layout is available/selected to perform the action!", 0);
                        }
                    }
                }
            }
            cellList.clear();
        } else{
            dInterface.changeMessage("No Layout is available/selected to perform the action!", 0);
        }
    }

    public void clearDrawView(RelativeLayout rel){

        boolean doBreak = false;
        while (!doBreak) {
            int childCount = rel.getChildCount();
            int i;
            for(i=0; i<childCount; i++) {
                View currentChild = rel.getChildAt(i);
                // Change ImageView with your desired type view
                if (currentChild instanceof LinearLayout) {

                    rel.removeView(currentChild);
                    break;
                }
            }

            if (i == childCount) {
                doBreak = true;
            }
        }
    }

    public boolean chkTileSelected(){
        if(isTileSelected){
            return true;
        }
        return false;
    }

    public String getSelectedTile(){
        if(isTileSelected){

            String size = "";
            DatabaseHandler dh = new DatabaseHandler(context);

            if (selectedFilePath != null) {
                if(this.tile_type.equals("P")) {
                    size = dh.getPatternSize(this.tile_id);
                }
                else{
                    size = dh.getTileSize(selectedFilePath);
                }

                if (size.equals("")) {
                    size = "0x0";
                }
            }
            return size;
        }
        return "";
    }

    public void deleteGridTile(){

        if(!cellList.isEmpty()){
            for (int i = 0; i < rel.getChildCount(); i++) {
                View v = rel.getChildAt(i);
                if (v instanceof LinearLayout) {
                    LayoutDimensions dimV = (LayoutDimensions) v.getTag();
                    if(cellList.contains(dimV.id) && dimV.selectedTile != null){

                        ((LinearLayout) v).removeAllViews();

                        if (Build.VERSION.SDK_INT >= 23) {
                            v.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg_transparent));
                        }
                        else{
                            v.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));

                        }

                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            v.setBackgroundDrawable(context.getResources().
                                    getDrawable(R.drawable.dmd_rect_bg_shadow));
                        } else {
                            v.setBackground(context.getResources().
                                    getDrawable(R.drawable.dmd_rect_bg_shadow));
                        }

                        dimV.selectedTile = null;
                        dimV.tilesUsed = 0;
                        dimV.tile_id = null;
                        dimV.tile_type = null;
                        dimV.tileHeight = 0;
                        dimV.tileWidth = 0;

                        invalidate();

                    }
                }
            }

        }
    }

    public void fillCell(String type, RelativeLayout rel){

        if(selectedLayout != null && !cellList.isEmpty()) {
            LayoutDimensions sel = (LayoutDimensions) selectedLayout.getTag();
            String[] selV = sel.id.split("-");

            for (int i = 0; i < rel.getChildCount(); i++) {
                View v = rel.getChildAt(i);
                if (v instanceof LinearLayout) {
                    LayoutDimensions dim = (LayoutDimensions) v.getTag();
                    String[] dimV = dim.id.split("-");

                    if(type == "y"){
                        if(selV[1].equals(dimV[1])){

                            if(((LayoutDimensions) v.getTag()).selectedTile != null) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    v.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg));
                                }
                                else{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        v.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                                    }
                                }
                            }
                            else{
                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    v.setBackgroundDrawable(context.getResources().
                                            getDrawable(R.drawable.dmd_rect_bg_shadow));
                                } else {
                                    v.setBackground(context.getResources().
                                            getDrawable(R.drawable.dmd_rect_bg_shadow));
                                }
                            }

                            insertCellList(dim.id);
                        }
                    }
                    else if(type == "x"){
                        if(selV[0].equals(dimV[0])){

                            if(((LayoutDimensions) v.getTag()).selectedTile != null) {
                                if (Build.VERSION.SDK_INT >= 23) {
                                    v.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg));
                                }
                                else{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        v.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                                    }
                                }
                            }
                            else{
                                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    v.setBackgroundDrawable(context.getResources().
                                            getDrawable(R.drawable.dmd_rect_bg_shadow));
                                } else {
                                    v.setBackground(context.getResources().
                                            getDrawable(R.drawable.dmd_rect_bg_shadow));
                                }
                            }

                            insertCellList(dim.id);
                        }
                    } else{

                        if(((LayoutDimensions) v.getTag()).selectedTile != null) {
                            if (Build.VERSION.SDK_INT >= 23) {
                                v.setForeground(context.getResources().getDrawable(R.drawable.dmd_rect_bg));
                            }
                            else{
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    v.setBackground(context.getResources().getDrawable(R.drawable.border_low_device));
                                }
                            }
                        }
                        else{
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                v.setBackgroundDrawable(context.getResources().
                                        getDrawable(R.drawable.dmd_rect_bg_shadow));
                            } else {
                                v.setBackground(context.getResources().
                                        getDrawable(R.drawable.dmd_rect_bg_shadow));
                            }
                        }

                        insertCellList(dim.id);
                    }
                }
            }

            invalidate();
        }
        else {
            if(type == "xy"){
                for (int i = 0; i < rel.getChildCount(); i++) {
                    View v = rel.getChildAt(i);
                    if (v instanceof LinearLayout) {
                        LayoutDimensions dim = (LayoutDimensions) v.getTag();
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            v.setBackgroundDrawable(context.getResources().
                                    getDrawable(R.drawable.dmd_rect_bg_shadow));
                        } else {
                            v.setBackground(context.getResources().
                                    getDrawable(R.drawable.dmd_rect_bg_shadow));
                        }
                        insertCellList(dim.id);
                    }
                }

            }
            else{
                dInterface.changeMessage("No Layout is available/selected to perform the action!", 0);
            }
        }
    }

    public void insertCellList(String val){
        if(cellList.isEmpty()){
            cellList.add(val);

        } else{
            if(!cellList.contains(val)){
                cellList.add(val);
            }
        }
    }
}