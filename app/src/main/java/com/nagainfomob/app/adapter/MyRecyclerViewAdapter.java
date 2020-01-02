package com.nagainfomob.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagainfomob.app.DisplayMyDesign.AmbienceBathroom01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceBedroom;
import com.nagainfomob.app.DisplayMyDesign.AmbienceExterior01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceKitchen01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceLivingRoom01;
import com.nagainfomob.app.DisplayMyDesign.AmbienceLivingRoom02;
import com.nagainfomob.app.DisplayMyDesign.CustomPatternActivity;
import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.DisplayMyDesign.PatternLibraryActivity;
import com.nagainfomob.app.DisplayMyDesign.PatternimgNameInterface;
import com.nagainfomob.app.R;
import com.nagainfomob.app.fragments.LibraryFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joy on 20/02/18.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    //    private String[] mData = new String[0];
    private ArrayList<HashMap<String, String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    public PatternimgNameInterface delegate = null;
    private LibraryFragment fragment = null;
    private String isCustom;

    private static final String KEY_BRAND = "pro_brand";
    private static final String KEY_NAME = "pro_name";
    private static final String KEY_DIMEN = "pro_dimen";
    private static final String KEY_TYPE = "pro_type";
    private static final String KEY_CATEGORY = "tile_category";
    private static final String KEY_COLOR = "pro_color";
    private static final String KEY_ID = "pro_id";
    private static final String KEY_TILE_TYPE = "tile_type";
    private static final String KEY_TILE_PRICE = "tile_price";


    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, PatternLibraryActivity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceLivingRoom01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceLivingRoom02 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceBedroom activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, CustomPatternActivity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceBathroom01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceExterior01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceKitchen01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public MyRecyclerViewAdapter(Context context, ArrayList<HashMap<String,
            String>> data, LibraryFragment fragment, String isCustom) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.fragment = fragment;
        this.isCustom = isCustom;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.dmd_pattern_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int img1 = 0;
        img1 = context.getResources().getIdentifier("dmd_pattern_ph", "drawable", context.getPackageName());
        String path = GlobalVariables.getPatternRootPath()
                + mData.get(position).get(KEY_BRAND) + "/"
                + mData.get(position).get(KEY_NAME);

        /*final int THUMBSIZE = 64;
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),
                THUMBSIZE, THUMBSIZE);
        Uri uri = getImageUri(context, ThumbImage);*/

        String[] tName = mData.get(position).get(KEY_NAME).split("/");
        String tname = tName[tName.length - 1].replace(".jpg", "").trim();

        holder.tv.setText(TitleCase(tname));
        holder.brand.setText(mData.get(position).get(KEY_BRAND));

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
    }

    public String TitleCase(String str){
        String[] words = str.toString().split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        String titleCaseValue = sb.toString();

        return titleCaseValue;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv;
        TextView brand;
        ImageView img;

        ViewHolder(View rowView) {
            super(rowView);
            tv=(TextView) rowView.findViewById(R.id.titelView);
            brand=(TextView) rowView.findViewById(R.id.brandView);
            img=(ImageView) rowView.findViewById(R.id.picture);

            rowView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
//            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            try {
                int position = getAdapterPosition();

                String path = GlobalVariables.getPatternRootPath()
                        + mData.get(position).get(KEY_BRAND) + "/"
                        + mData.get(position).get(KEY_NAME);
                String tBrand = mData.get(position).get(KEY_BRAND);
                String tDim = null;
                if (mData.get(position).get(KEY_DIMEN) != null) {
                    tDim = mData.get(position).get(KEY_DIMEN)
                            .replaceAll("Millimeter", "");
                }
                String tType = mData.get(position).get(KEY_TYPE);
                String tCategory = mData.get(position).get(KEY_CATEGORY);
                String tColor = mData.get(position).get(KEY_COLOR);
                String tname = mData.get(position).get(KEY_NAME);
                String tile_id = mData.get(position).get(KEY_ID);
                String tile_type = mData.get(position).get(KEY_TILE_TYPE);
                String tile_price = mData.get(position).get(KEY_TILE_PRICE);

                callDeligate(path, tname, tDim, tBrand, tType, tCategory, tColor, tile_id, tile_type, tile_price);
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
    }

    public void callDeligate(String path, String tname, String Dim, String brand,
                             String type, String category, String color, String tile_id, String tile_type,
                             String price) {
        if(this.fragment != null){
            if(this.isCustom.equals("C")){
                fragment.viewTileCustom(path, tname, Dim, brand, type, category, color, tile_id, tile_type, price);
            }
            else{
                fragment.viewTile(path, tname, Dim, brand, type, category, color, tile_id, tile_type, price);
            }
        }
        else{
            this.delegate.patternName(path, Dim, brand, type, tile_id, tile_type, price);
        }
    }



    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id).get(KEY_NAME);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}