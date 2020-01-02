package com.nagainfomob.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagainfomob.app.DisplayMyDesign.GlobalVariables;
import com.nagainfomob.app.R;
import com.nagainfomob.app.model.TileListModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by joy on 27/09/17.
 */

public class TileRecyclerAdapter extends RecyclerView.Adapter<TileRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<TileListModel> tileList = new ArrayList<>();
    private Context context;
    private Dialog dialog;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tile_name;
        ImageView tile_img;
        TextView tile_brand;
        TextView tile_count;
        TextView tile_rate;
        TextView tile_price;


        public ViewHolder(View v) {
            super(v);

            tile_name = (TextView) v.findViewById(R.id.tile_name);
            tile_img = (ImageView) v.findViewById(R.id.tile_img);
            tile_brand = (TextView) v.findViewById(R.id.tile_brand);
            tile_count = (TextView) v.findViewById(R.id.tile_count);
            tile_rate = (TextView) v.findViewById(R.id.tile_rate);
            tile_price = (TextView) v.findViewById(R.id.tile_price);
        }
    }

    public TileRecyclerAdapter(Context context, ArrayList<TileListModel> dataset) {
        tileList.clear();
        tileList.addAll(dataset);
        this.context = context;
    }

    @Override
    public TileRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        int img = 0;
        String file_path = "";
        if(tileList.get(position).getType().toString().equals("P")){
            file_path = GlobalVariables.getCustomPatternRootPath() + tileList.get(position).getTile_brand().trim() + "/";
        }
        else{
            file_path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/DMD/" + tileList.get(position).getTile_brand().trim() + "/";
        }

        file_path = file_path + tileList.get(position).getTile_name() + ".jpg";

        Picasso.with(context)
                .load("file:///"+file_path)
                .error(R.drawable.dmd_pattern_ph)
                .placeholder(R.drawable.dmd_pattern_ph)
                .resize(75, 0)
                .into(holder.tile_img, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                    }
                });

        String name_desc = tileList.get(position).getTile_name() + " (" +
                tileList.get(position).getTile_dimen() + "mm)";
        holder.tile_name.setText(name_desc);
        holder.tile_brand.setText(tileList.get(position).getTile_brand());
        holder.tile_count.setText(tileList.get(position).getTile_count());
        holder.tile_price.setText("Rs."+tileList.get(position).getTile_price());
        holder.tile_rate.setText("@ "+tileList.get(position).getTile_rate());

    }

    @Override
    public int getItemCount() {
        return tileList.size();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /*case R.id.fav_icon :
                Toast.makeText(context, "Favourite Clicked", Toast.LENGTH_SHORT).show();
                break;*/

        }
    }
}