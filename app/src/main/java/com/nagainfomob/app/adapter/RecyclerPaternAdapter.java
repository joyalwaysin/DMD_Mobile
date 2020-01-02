package com.nagainfomob.app.adapter;

import android.content.Context;
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
import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.DisplayMyDesign.PatternLibraryActivity;
import com.nagainfomob.app.R;
import com.nagainfomob.app.fragments.PatternFragment;
import com.nagainfomob.app.helpers.PatternDetailsInterface;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joy on 27/09/17.
 */

public class RecyclerPaternAdapter extends RecyclerView.Adapter<RecyclerPaternAdapter.ViewHolder> {

    //    private String[] mData = new String[0];
    private ArrayList<HashMap<String, String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private PatternFragment fragment;
    public PatternDetailsInterface delegate = null;

    private static final String PATRN_BRAND = "pattern_brand";
    private static final String PATRN_NAME = "pattern_name";
    private static final String PATRN_DIMEN = "pattern_dimen";
    private static final String PATRN_TYPE = "pattern_type";
    private static final String PATRN_CATEGORY = "pattern_cat";
    private static final String PATRN_PRICE = "pattern_price";
    private static final String PATRN_ID = "pattern_id";


    // data is passed into the constructor
    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, PatternFragment fragment) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.fragment = fragment;
    }

    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, PatternLibraryActivity activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceBedroom activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceLivingRoom01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceLivingRoom02 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceBathroom01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceExterior01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    public RecyclerPaternAdapter(Context context, ArrayList<HashMap<String,
            String>> data, AmbienceKitchen01 activity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.delegate = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.dmd_pattern_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(RecyclerPaternAdapter.ViewHolder holder, int position) {

        int img1 = 0;
        img1 = context.getResources().getIdentifier("dmd_pattern_ph", "drawable", context.getPackageName());
        String path = GlobalVariables.getCustomPatternRootPath()
                + mData.get(position).get(PATRN_BRAND) + "/"
                + mData.get(position).get(PATRN_NAME);

        String[] tName = mData.get(position).get(PATRN_NAME).split("/");
        String tname = tName[tName.length - 1].replace(".jpg", "");

        holder.tv.setText(tname);
        holder.brand.setText(mData.get(position).get(PATRN_BRAND));

        Picasso.with(context)
                .load("file:///"+path)
                .error(R.drawable.dmd_pattern_ph)
                .placeholder(R.drawable.dmd_pattern_ph)
                .resize(75, 0)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder.img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
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

                /*String path = GlobalVariables.getPatternRootPath()
                        + mData.get(position).get(PATRN_BRAND) + "/"
                        + mData.get(position).get(PATRN_NAME);*/
                String path = GlobalVariables.getCustomPatternRootPath()
                        + mData.get(position).get(PATRN_BRAND) + "/"
                        + mData.get(position).get(PATRN_NAME);
                String tBrand = mData.get(position).get(PATRN_BRAND);
                String tDim = null;
                if (mData.get(position).get(PATRN_DIMEN) != null) {
                    tDim = mData.get(position).get(PATRN_DIMEN)
                            .replaceAll("Millimeter", "");
                }
                String tType = mData.get(position).get(PATRN_TYPE);
                String tCategory = mData.get(position).get(PATRN_CATEGORY);
                String tname = mData.get(position).get(PATRN_NAME);
                String tPrice = mData.get(position).get(PATRN_PRICE);
                String pID = mData.get(position).get(PATRN_ID);

                callDeligate(path, tname, tDim, tBrand, tType, tCategory, tPrice, pID);

            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        }
    }

    public void callDeligate(String path, String tname, String Dim, String brand, String type,
                             String category, String price, String pat_id ) {
        if(this.fragment != null) {
            fragment.viewPattern(path, tname, Dim, brand, type, category, price);
        }
        else{
            this.delegate.viewPattern(path, Dim, brand, type, pat_id, "P");
        }
    }



    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id).get(PATRN_NAME);
    }

    // allows clicks events to be caught
    public void setClickListener(RecyclerPaternAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
