package com.nagainfomob.app.helpers;

import android.graphics.Bitmap;

/**
 * Created by joy on 11/01/18.
 */

public class RowItem {

    private Bitmap bitmapImage;
    private String dummyString;

    /*public RowItem(Bitmap bitmapImage) {
        this.bitmapImage =  bitmapImage;
    }*/

    public RowItem(String str) {
        this.dummyString =  str;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }
}