package com.familheey.app.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.Document;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_SQUARE;

public class AlbumEventAdapter extends RecyclerView.Adapter<AlbumEventAdapter.ViewHolder> {

    private final List<Document> documents;
    private boolean isAdmin;
    private final OnAlbumSelectedListener mListener;

    public AlbumEventAdapter(Context context, OnAlbumSelectedListener mListener, List<Document> albumDocuments, boolean isAdmin) {
        documents = albumDocuments;
        this.mListener = mListener;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.album_events_adatper, parent, false);

        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Document document = documents.get(position);
        if (document.getId() != null && document.getId().equals(0L)) {
            holder.progressBar.setVisibility(View.VISIBLE);
            Glide.with(holder.albumImage.getContext())
                    .load(Uri.parse(document.getoriginalName()))
                    .placeholder(R.drawable.family_dashboard_background)
                    .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                    .into(holder.albumImage);
            holder.imagePlay.setVisibility(View.INVISIBLE);
        } else {
            holder.progressBar.setVisibility(View.INVISIBLE);
            if(document.getUrl()!=null){
                String url=document.getUrl();
                if(!document.getFileType().contains("video"))
                    url=S3_DEV_IMAGE_URL_SQUARE+ document.getUrl();
                Glide.with(holder.albumImage.getContext())
                        .load(url)
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .placeholder(R.drawable.album_default_image)
                        .into(holder.albumImage);
            }else {
                Glide.with(holder.albumImage.getContext())
                        .load(R.drawable.album_default_image)
                        .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                        .into(holder.albumImage);
            }
            if (document.getFileType().contains("video")) {
                holder.imagePlay.setVisibility(View.VISIBLE);
            } else holder.imagePlay.setVisibility(View.INVISIBLE);
        }
        if (document.isLongPressed())
            holder.selectionIndicator.setVisibility(View.VISIBLE);
        else holder.selectionIndicator.setVisibility(View.INVISIBLE);
        holder.itemView.setOnClickListener(v -> {
            if (document.getId().equals(0L))
                return;
            if (isAlreadyInSelectionMode()) {
                document.setLongPressed(!document.isLongPressed());
                handleDeleteButton();
                notifyDataSetChanged();
            } else {
                mListener.onAlbumElementSelected(document, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isAdmin)
                return true;
            if (document.getId().equals(0L))
                return true;
            document.setLongPressed(!document.isLongPressed());
            handleDeleteButton();
            notifyDataSetChanged();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public boolean isAlreadyInSelectionMode() {
        for (Document document : documents) {
            if (document.isLongPressed())
                return true;
        }
        return false;
    }

    public void handleDeleteButton() {
        List<Long> selectedElementIds = new ArrayList<>();
        for (Document document : documents) {
            if (document.isLongPressed())
                selectedElementIds.add(document.getId());
        }
        if (selectedElementIds.size() == 0)
            mListener.onAlbumDeselected();
        else mListener.onAlbumSelectedForDeletion(selectedElementIds);
    }

    public interface OnAlbumSelectedListener {
        void onAlbumElementSelected(Document document, int position);

        void onAlbumSelectedForDeletion(List<Long> selectedElementIds);

        void onAlbumDeselected();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.albumImage)
        ImageView albumImage;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.selectionIndicator)
        ImageView selectionIndicator;
        @BindView(R.id.imagePlay)
        ImageView imagePlay;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setAdminStatus(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}