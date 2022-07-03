package com.familheey.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Request.HistoryImages;
import com.familheey.app.R;

import java.util.List;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;

public class HistoryImageAdapter extends RecyclerView.Adapter<HistoryImageAdapter.MyViewHolder> {
    private List<HistoryImages> images;

    public HistoryImageAdapter(Context context, List<HistoryImages> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_history_image, parent, false);

        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(IMAGE_BASE_URL+"history_images/"+images.get(position).getFilename())
                .into(holder.history_image);
        holder.btn_delete.setOnClickListener(v -> {
            images.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView history_image,btn_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            history_image = itemView.findViewById(R.id.history_image);
        }
    }




}
