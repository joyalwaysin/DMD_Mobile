package com.nagainfomob.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagainfomob.app.R;
import com.nagainfomob.app.fragments.MyDesignsFragment;
import com.nagainfomob.app.model.MyDesignsModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by joy on 27/09/17.
 */

public class PhotoVisualizerAdapter extends RecyclerView.Adapter<PhotoVisualizerAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<MyDesignsModel> mDataset = new ArrayList<>();
    private Context context;
    private Dialog dialog;
    private MyDesignsFragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemTitle;
        public ImageView itemImg;
        public View clickableView;


        public ViewHolder(View v) {
            super(v);
            itemTitle 		= 	v.findViewById(R.id.titelView);
            itemImg 		= 	v.findViewById(R.id.picture);
            clickableView   =	v.findViewById(R.id.horLinContent);
        }
    }

    public PhotoVisualizerAdapter(Context context, ArrayList<MyDesignsModel> dataset, MyDesignsFragment fragment) {
        mDataset.clear();
        mDataset.addAll(dataset);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public PhotoVisualizerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dmd_photo_design_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        int img = 0;
        if(position ==0) img = context.getResources().getIdentifier("living_room_01_thumb", "drawable", context.getPackageName());
        else if(position ==1) img = context.getResources().getIdentifier("living_room_02_thumb", "drawable", context.getPackageName());
        else if(position ==2) img = context.getResources().getIdentifier("bed_room_01_thumb", "drawable", context.getPackageName());
        else if(position ==3) img = context.getResources().getIdentifier("bathroom_01_thumb", "drawable", context.getPackageName());
        else if(position ==4) img = context.getResources().getIdentifier("exterior_01_thumb", "drawable", context.getPackageName());
        else if(position ==5) img = context.getResources().getIdentifier("kitchen_01_thumb", "drawable", context.getPackageName());
        else img = context.getResources().getIdentifier("dmd_pattern_ph", "drawable", context.getPackageName());

        holder.itemTitle.setText(mDataset.get(position).ItemTitle);

        Picasso.with(context)
                .load(img)
                .error(R.drawable.dmd_img2)
                .into(holder.itemImg, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                    }
                });

        holder.itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view2D(position);
            }
        });

        holder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view2D(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /*case R.id.fav_icon :
                Toast.makeText(context, "Favourite Clicked", Toast.LENGTH_SHORT).show();
                break;*/

        }
    }

    public void view2D(int position) {
        fragment.view2D(position);
    }
}