package com.familheey.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.familheey.app.Models.Response.Document;
import com.familheey.app.Post.VideoActivity;
import com.familheey.app.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_COVER_DETAILED;

public class AlbumSliderAdapter extends SliderViewAdapter<AlbumSliderAdapter.SliderAdapterVH> {
    private final Context context;
    private final ArrayList<Document> documents;

    public AlbumSliderAdapter(Context context, ArrayList<Document> documents) {
        this.context = context;
        this.documents = documents;
    }

    @Override
    public AlbumSliderAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(AlbumSliderAdapter.SliderAdapterVH viewHolder, int position) {
        Document document = documents.get(position);

        if (document.isVideo()) {
            Glide.with(context)
                    .load(document.getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(viewHolder.autoSliderImage);
            viewHolder.playVideo.setVisibility(View.VISIBLE);
        } else {
            viewHolder.playVideo.setVisibility(View.INVISIBLE);
            Glide.with(context)
                    .load(S3_DEV_IMAGE_URL_COVER_DETAILED + document.getUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(viewHolder.autoSliderImage);
        }
        viewHolder.playVideo.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), VideoActivity.class);
            intent.putExtra("URL", document.getUrl());
            intent.putExtra("NAME", document.getFile_name());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getCount() {
        return documents.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        @BindView(R.id.iv_auto_image_slider)
        PhotoView autoSliderImage;
        @BindView(R.id.playVideo)
        ImageView playVideo;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}