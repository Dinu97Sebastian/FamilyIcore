package com.familheey.app.Post;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Adapters.AlbumPostAdapter;
import com.familheey.app.Models.Request.Image;
import com.familheey.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.familheey.app.Utilities.Constants.ApiPaths.IMAGE_BASE_URL;

public class PostAttachmentAdapter extends RecyclerView.Adapter<PostAttachmentAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    private List<Image> documents;
    private OnAlbumSelectedListener mListener;
    public PostAttachmentAdapter(Context ctx, List<Image> albumDocuments,OnAlbumSelectedListener mListener){

        inflater = LayoutInflater.from(ctx);
        documents = albumDocuments;
        this.mListener = mListener;
    }

    @Override
    public PostAttachmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_post_attachment, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }
    @Override
    public void onBindViewHolder(PostAttachmentAdapter.MyViewHolder holder, int position) {
        Image document = documents.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.imagePlay.setVisibility(View.INVISIBLE);
        /*(25/08/2021)megha-> for disabling switch on uploading image*/
//        if (CreatePostActivity.rating.isChecked()){
//
//            if (document.isIsuploading()){
//                CreatePostActivity.rating.setClickable(false);
//            }else{
//                CreatePostActivity.rating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (!isChecked){
//                            documents.remove(position);
//                            notifyDataSetChanged();
//                          //  mListener.onAlbumDeleted(position);
//                        }
//                    }
//                });
//                CreatePostActivity.rating.setClickable(true);
//            }
//        }else{
//            CreatePostActivity.rating.setChecked(false);
//        }

        if(document.isIsuploading()){
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
            holder.progressBar.setProgress(document.getPrograss());
        }else{
            holder.progressBar.setVisibility(View.GONE);
            holder.delete.setVisibility(View.VISIBLE);
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
                documents.remove(position);
                notifyDataSetChanged();
                mListener.onAlbumDeleted(position);
            }
        });


    }
    @Override
    public int getItemCount() {
        return documents.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imagedelete)
        ImageView delete;
        @BindView(R.id.albumImage)
        ImageView albumImage;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.imagePlay)
        ImageView imagePlay;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }
    public interface OnAlbumSelectedListener {

        void onAlbumDeleted( int position);

    }

}
