package com.nagainfomob.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nagainfomob.app.R;
import com.nagainfomob.app.main.PlanActivity;
import com.nagainfomob.app.model.Plan.PlanModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by joy on 27/09/17.
 */

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<PlanModel> mDataset = new ArrayList<>();
    private Context context;
    private Dialog dialog;
    private PlanActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView plan_desc;
        public TextView plan_duration;
        public TextView plan_amount;
        public ImageView thumbnail;
        public CardView card_view;
        public Button plan_subscribe;


        public ViewHolder(View v) {
            super(v);
            plan_desc 		= 	v.findViewById(R.id.plan_desc);
            plan_duration 	= 	v.findViewById(R.id.plan_duration);
            plan_amount 	= 	v.findViewById(R.id.plan_amount);
            thumbnail 		= 	v.findViewById(R.id.thumbnail);
            card_view 		= 	v.findViewById(R.id.card_view);
            plan_subscribe 	= 	v.findViewById(R.id.plan_subscribe);

//            card_view.setPreventCornerOverlap(false);
        }
    }

    public PlanAdapter(Context context, ArrayList<PlanModel> dataset, PlanActivity activity) {
        mDataset.clear();
        mDataset.addAll(dataset);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dmd_plan_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        int img = 0;
        if(position ==0) img = context.getResources().getIdentifier("dmd_plan_basic", "drawable", context.getPackageName());
        else if(position ==1) img = context.getResources().getIdentifier("dmd_plan_premium", "drawable", context.getPackageName());
        else if(position ==2) img = context.getResources().getIdentifier("dmd_plan_ultimate", "drawable", context.getPackageName());
        else img = context.getResources().getIdentifier("dmd_pattern_ph", "drawable", context.getPackageName());

        String duration = "";
        duration = "Duration "+mDataset.get(position).duration+" "+mDataset.get(position).duration_type;

        holder.plan_desc.setText(mDataset.get(position).description);
        holder.plan_duration.setText(duration);
        holder.plan_amount.setText("Rs. "+mDataset.get(position).price);

        Picasso.with(context)
                .load(img)
                .error(R.drawable.dmd_img2)
                .transform(new RoundedTransformation(5, 0))
                .into(holder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {

                    }
                });

        holder.plan_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPay(mDataset.get(position).id, mDataset.get(position).price);
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

    public void requestPay(String plan_id, String price) {
        activity.requestPay(plan_id, price);
    }

    public class RoundedTransformation implements com.squareup.picasso.Transformation {
        private final int radius;
        private final int margin;

        public RoundedTransformation(final int radius, final int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(final Bitmap source) {
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin), radius, radius, paint);

            if (source != output) {
                source.recycle();
            }

            return output;
        }

        @Override
        public String key() {
            return "rounded(radius=" + radius + ", margin=" + margin + ")";
        }
    }
}
