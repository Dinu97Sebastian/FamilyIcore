package com.familheey.app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.Document;
import com.familheey.app.R;
import com.jsibbold.zoomage.ZoomageView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SliderImageAdapter extends SliderViewAdapter<SliderImageAdapter.SliderAdapterVH> {
    private Context context;
    private ArrayList<Document> documents;

    public SliderImageAdapter(Context context, ArrayList<Document> documents) {
        this.context = context;
        this.documents = documents;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

        Glide.with(viewHolder.itemView)
                .load(documents.get(position).getUrl())
                .into(viewHolder.autoSliderImage);
    }

    public void position() {

    }


    @Override
    public int getCount() {
        return documents.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        @BindView(R.id.iv_auto_image_slider)
        ZoomageView autoSliderImage;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
        }
    }
}
