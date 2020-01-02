package com.nagainfomob.app.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.R;
import com.nagainfomob.app.fragments.LibraryFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joy on 27/09/17.
 */

public class LibraryAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity activity;
    private Context context;
    private Dialog dialog;
    private LibraryFragment fragment;
    private String flag;

    private ArrayList<HashMap<String, String>> dbResult;
    private static  LayoutInflater inflater = null;
    private static final String KEY_BRAND = "pro_brand";
    private static final String KEY_NAME = "pro_name";
    private static final String KEY_DIMEN = "pro_dimen";
    private static final String KEY_TYPE = "pro_type";
    private static final String KEY_CATEGORY = "tile_category";
    private static final String KEY_COLOR = "pro_color";
    private static final String KEY_ID = "pro_id";
    private static final String KEY_TILE_TYPE = "tile_type";
    private static final String KEY_TILE_PRICE = "tile_price";

    private DisplayImageOptions options;


    public LibraryAdapter(Context context, ArrayList<HashMap<String,
            String>> dbResult, LibraryFragment fragment, String isCustom) {

        this.context = context;
        this.dbResult = dbResult;
        this.fragment = fragment;
        this.flag = isCustom;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        initimageloader();
        refresh();

    }

    @Override
    public int getCount() {
        return dbResult.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder
    {
        TextView tv;
        TextView brand;
        ImageView img;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        int img1 = 0;
        img1 = context.getResources().getIdentifier("dmd_img1", "drawable", context.getPackageName());
        String path = GlobalVariables.getPatternRootPath()
                + dbResult.get(position).get(KEY_BRAND) + "/"
                + dbResult.get(position).get(KEY_NAME);


        Holder holder=new Holder();
        View rowView;

        String[] tName = dbResult.get(position).get(KEY_NAME).split("/");
        String tname = tName[tName.length - 1].replace(".jpg", "");
        rowView = inflater.inflate(R.layout.dmd_pattern_item, null);

        holder.tv=(TextView) rowView.findViewById(R.id.titelView);
        holder.brand=(TextView) rowView.findViewById(R.id.brandView);
        holder.img=(ImageView) rowView.findViewById(R.id.picture);

        holder.tv.setText(tname);
        holder.brand.setText(dbResult.get(position).get(KEY_BRAND));
        Picasso.with(context)
                .load("file:///"+path)
                .error(R.drawable.dmd_pattern_ph)
                .placeholder(R.drawable.dmd_pattern_ph)
                .resize(75, 0)
                .into(holder.img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*String projectName = mDataset.get(position).ItemTitle;
                view3D(projectName);*/

                try {
                    String path = GlobalVariables.getPatternRootPath()
                            + dbResult.get(position).get(KEY_BRAND) + "/"
                            + dbResult.get(position).get(KEY_NAME);
                    String tname = dbResult.get(position).get(KEY_NAME);
                    String tBrand = dbResult.get(position).get(KEY_BRAND);
                    String tDim = null;
                    if (dbResult.get(position).get(KEY_DIMEN) != null) {
                        tDim = dbResult.get(position).get(KEY_DIMEN)
                                .replaceAll("Millimeter", "");
                    }
                    String tType = dbResult.get(position).get(KEY_TYPE);
                    String tCategory = dbResult.get(position).get(KEY_CATEGORY);
                    String tColor = dbResult.get(position).get(KEY_COLOR);
                    String tile_id = dbResult.get(position).get(KEY_ID);
                    String tile_type = dbResult.get(position).get(KEY_TILE_TYPE);
                    String tile_price = dbResult.get(position).get(KEY_TILE_PRICE);


                    callDeligate(path, tname, tDim, tBrand, tType, tCategory, tColor, flag, tile_id,
                            tile_type, tile_price);
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }


            }
        });

        return rowView;

    }

    public void callDeligate(String path, String tname, String Dim, String brand, String type,
                             String category, String color, String isCustom, String tile_id,
                             String tile_type, String tile_price) {
        if(isCustom.equals("M"))
            fragment.viewTile(path, tname, Dim, brand, type, category, color, tile_id, tile_type, tile_price);
        else
            fragment.viewTileCustom(path, tname, Dim, brand, type, category, color, tile_id, tile_type, tile_price);
    }

    private void initimageloader() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    void refresh() {

        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /*case R.id.fav_icon :
                Toast.makeText(context, "Favourite Clicked", Toast.LENGTH_SHORT).show();
                break;*/

        }
    }

    public void view3D(String projectName) {
//        activity.view3D(projectName);
    }
}