package com.familheey.app.Need;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.BuildConfig;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.R;

import java.util.ArrayList;

public class ThankYouImageAdapter extends RecyclerView.Adapter<ThankYouImageAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<HistoryImages> thankYouImages;

    ThankYouImageAdapter(Context context, ArrayList<HistoryImages> thankYouImages) {
        this.context = context;
        this.thankYouImages = thankYouImages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thank_you_image, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(BuildConfig.IMAGE_BASE_URL + "post/" + thankYouImages.get(position).getFilename())
                .into(holder.imgBanner);
    }

    @Override
    public int getItemCount() {
        return thankYouImages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgBanner;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imageView);
        }
    }
}
