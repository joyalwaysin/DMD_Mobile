package com.nagainfomob.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagainfomob.app.R;
import com.nagainfomob.app.fragments.ThreeDListFragment;
import com.nagainfomob.app.model.MyDesignsModel;

import java.util.ArrayList;

/**
 * Created by joy on 27/09/17.
 */

public class TDListAdapter extends RecyclerView.Adapter<TDListAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<MyDesignsModel> mDataset = new ArrayList<>();
    private Context context;
    private Dialog dialog;
    private ThreeDListFragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemTitle;
        public ImageView itemImg;
        public TextView itemDateTime;
        public ImageView moreImg;
        public View clickableView;


        public ViewHolder(View v) {
            super(v);
            itemImg 		= 	v.findViewById(R.id.picture);
            itemTitle 		= 	v.findViewById(R.id.titelView);
            itemDateTime 	= 	v.findViewById(R.id.datetimeView);
            moreImg 		    = 	v.findViewById(R.id.menu_ico);
            clickableView   =	v.findViewById(R.id.horLinContent);

        }
    }

    public TDListAdapter(Context context, ArrayList<MyDesignsModel> dataset, ThreeDListFragment fragment) {
        mDataset.clear();
        mDataset.addAll(dataset);

        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public TDListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dmd_td_list_item, parent, false);
        TDListAdapter.ViewHolder vh = new TDListAdapter.ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int img = 0;
        img = context.getResources().getIdentifier("dmd_placeholder", "drawable", context.getPackageName());

        holder.itemTitle.setText(mDataset.get(position).ItemTitle);
        holder.itemDateTime.setText("Created At : " + mDataset.get(position).ItemDateTime);

        final String projectName = mDataset.get(position).ItemTitle;
        final String projectCreated = mDataset.get(position).ItemDateTime;
        final String projectId = mDataset.get(position).projectId;
        final String unit = mDataset.get(position).unit;
        final String width = mDataset.get(position).width;
        final String height = mDataset.get(position).height;
        final String depth = mDataset.get(position).depth;
        final String custName = mDataset.get(position).cust_name;
        final String custMob = mDataset.get(position).cust_mob;

        holder.itemImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view3D(projectName, unit, width, height, depth, projectId, projectCreated, custName, custMob);

            }
        });

        holder.moreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.moreImg);
                popup.getMenuInflater().inflate(R.menu.project_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        deleteProject(projectId);
                        return true;
                    }
                });

                popup.show();
            }
        });
        holder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view3D(projectName, unit, width, height, depth, projectId, projectCreated, custName, custMob);
            }
        });
        holder.itemDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view3D(projectName, unit, width, height, depth, projectId, projectCreated, custName, custMob);
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

    public void view3D(String projectName, String unit, String width, String height,
                       String depth, String projectId, String projectCreated, String custName, String custMob) {
        fragment.view3D(projectName, unit, width, height, depth, projectId, projectCreated, custName, custMob);
    }

    public void deleteProject(String projectId){
        fragment.deleteProject(projectId);

    }
}
