package com.familheey.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Request.Image;
import com.familheey.app.Models.Request.PostRequest;
import com.familheey.app.Post.CreatePostActivity;
import com.familheey.app.Post.EditPostActivity;
import com.familheey.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;

public class AlbumPostAdapter extends RecyclerView.Adapter<AlbumPostAdapter.ViewHolder>{

    private List<Image> documents;
    private OnAlbumSelectedListener mListener;
    private String edit="";
    public AlbumPostAdapter(OnAlbumSelectedListener mListener, List<Image> albumDocuments) {
        documents = albumDocuments;
        this.mListener = mListener;
    }
    public AlbumPostAdapter(OnAlbumSelectedListener mListener, List<Image> albumDocuments,String edit) {
        documents = albumDocuments;
        this.mListener = mListener;
        this.edit=edit;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.album_post_adatper, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image document = documents.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.imagePlay.setVisibility(View.INVISIBLE);
        /*(25/08/2021)megha-> for disabling switch on uploading image*/
        if(document.isIsuploading()) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
            holder.progressBar.setProgress(document.getPrograss());
        }else{
            holder.progressBar.setVisibility(View.GONE);
            holder.delete.setVisibility(View.VISIBLE);
        }

        if(edit.equals("editPost")) {
            if (EditPostActivity.rating.isChecked()) {
                if (document.isIsuploading()) {
                    EditPostActivity.rating.setClickable(false);
                } else {
                    EditPostActivity.rating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                            if (isChecked){
//
//                            }
//                            if (!isChecked) {
//                                documents.remove(position);
//                                notifyDataSetChanged();
//                                mListener.onAlbumDeleted(position);
//                            }else{
//                                notifyDataSetChanged();
//                            }
                        }
                    });
                    EditPostActivity.rating.setClickable(true);
                }
            } else {
                EditPostActivity.rating.setChecked(false);
            }
        }



        if(document.isVideo()){
            holder.imagePlay.setVisibility(View.VISIBLE);
        }else{
            holder.imagePlay.setVisibility(View.GONE);
        }


        if(document.isDoc()){

            if(document.getfileType().contains("pdf")){
                holder.albumImage.setImageResource(R.drawable.pdf);
            }
            else if(document.getfileType().contains("xls")|| document.getfileType().contains("excel")||document.getfileType().contains("sheet"))
            {
                holder.albumImage.setImageResource(R.drawable.ms_excel);
            }
            else if(document.getfileType().contains("ppt")||document.getfileType().contains("presentation")||document.getfileType().contains("powerpoint"))
            {
                holder.albumImage.setImageResource(R.drawable.ms_powerpoint);
            }
            else if(document.getfileType().contains("doc")||document.getfileType().contains("word")  && (!document.getfileType().contains("presentation") || !document.getfileType().contains("sheet") ||!document.getfileType().contains("xls")))
            {
                holder.albumImage.setImageResource(R.drawable.ms_word);
            }
            else if(document.getfileType().contains("zip")||document.getfileType().contains("rar")||document.getfileType().contains("octet-stream"))
            {
                holder.albumImage.setImageResource(R.drawable.zip);
            }
            else
            {
                holder.albumImage.setImageResource(R.drawable.doc);
            }

        }
        // for audio preview->Dinu
        else if(document.isAudio()){
            holder.albumImage.setImageResource(R.drawable.audio);
        }
        else {

            if (document.isUrl()) {
                Glide.with(holder.albumImage.getContext())
                        .load(IMAGE_BASE_URL + "post/" + document.getmUrl())
                        .placeholder(R.drawable.family_dashboard_background)
                        .into(holder.albumImage);
            } else {
                Glide.with(holder.albumImage.getContext())
                        .load(Uri.parse(document.getmUrl()))
                        .placeholder(R.drawable.family_dashboard_background)
                        .into(holder.albumImage);
            }
        }

        holder.delete.setOnClickListener(v -> {

            if(!document.isIsuploading()){
                if(edit.equals("editPost")) {
                    mListener.onAlbumDeleted(position);
                }
                else{
                    documents.remove(position);
                    notifyDataSetChanged();
                    mListener.onAlbumDeleted(position);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return documents.size();
    }



    public interface OnAlbumSelectedListener {

        void onAlbumDeleted( int position);

    }
    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imagedelete)
        ImageView delete;
        @BindView(R.id.albumImage)
        ImageView albumImage;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.imagePlay)
        ImageView imagePlay;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}