package com.familheey.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.familheey.app.Models.Response.ListEventAlbumsResponse;
import com.familheey.app.R;
import com.familheey.app.Utilities.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.familheey.app.Utilities.Constants.ApiPaths.S3_DEV_IMAGE_URL_ALBUM;

public class LisAlbumsforEventsAdapter extends RecyclerView.Adapter<LisAlbumsforEventsAdapter.ViewHolder> {
    private boolean isAdmin = false;
    private List<ListEventAlbumsResponse.Datum> listGroupFoldersResponse = new ArrayList<>();
    private FolderAdapterInterface folderAdapterInterface;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
    SimpleDateFormat fetchDisplay = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    public LisAlbumsforEventsAdapter(int type) {

    }

    public void setData(FolderAdapterInterface folderAdapterInterface, List<ListEventAlbumsResponse.Datum> listGroupFoldersResponse) {
        this.listGroupFoldersResponse = listGroupFoldersResponse;
        this.folderAdapterInterface = folderAdapterInterface;
        notifyDataSetChanged();
    }

    public void updateAdminStatus(boolean isAdmin, boolean canCreate, boolean canUpdate, String parentId) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_albums_adapter, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ListEventAlbumsResponse.Datum folder = listGroupFoldersResponse.get(position);
        String docCount = folder.getDocCount();

        holder.albumName.setText(folder.getFolderName());
        holder.txtCount.setText(folder.getDocCount());


        if (docCount != null && !docCount.equals("0")) {
            holder.txtCount.setText(docCount);
        } else {
            holder.cvCount.setVisibility(View.GONE);
        }


        if (folder.getCreatedByName() != null) {
            holder.albumAuthor.setText(folder.getCreatedByName());
            holder.albumAuthor.setVisibility(View.VISIBLE);
        } else {
            holder.albumAuthor.setVisibility(View.INVISIBLE);
        }
        Glide.with(holder.coverPhoto.getContext())
                .load(S3_DEV_IMAGE_URL_ALBUM + folder.getCoverPic())
                .error(R.drawable.default_event_image)
                .transition(withCrossFade(Utilities.getDrawableCrossFadeFactory()))
                .into(holder.coverPhoto);

        try {
            Date date = fetchDisplay.parse(folder.getCreatedAt());
            String formattedDate = dateFormat.format(date);
            holder.createdDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            holder.createdDate.setText(folder.getCreatedAt());
        }

        if (folder.isLongPressed())
            holder.selectionIndicator.setVisibility(View.VISIBLE);
        else holder.selectionIndicator.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (isAlreadyInSelectionMode()) {
                folder.setLongPressed(!folder.isLongPressed());
                handleDeleteButton();
                notifyDataSetChanged();
            } else {
                folderAdapterInterface.onFolderSelected(folder, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (isAdmin || folder.canUpdate(folderAdapterInterface.getParentId())) {
                folder.setLongPressed(!folder.isLongPressed());
                handleDeleteButton();
                notifyDataSetChanged();
                return true;
            } else return false;
        });
    }

    public boolean isAlreadyInSelectionMode() {
        for (ListEventAlbumsResponse.Datum folder : listGroupFoldersResponse) {
            if (folder.isLongPressed())
                return true;
        }
        return false;
    }

    public void handleDeleteButton() {
        List<Long> selectedElementIds = new ArrayList<>();
        for (ListEventAlbumsResponse.Datum folder : listGroupFoldersResponse) {
            if (folder.isLongPressed())
                selectedElementIds.add(folder.getId());
        }
        if (selectedElementIds.size() == 0) {
            folderAdapterInterface.onFolderDeselected();
        } else folderAdapterInterface.onFolderSelectedForDeletion(selectedElementIds);
    }

    public void setAdminStatus(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
    }

    public interface FolderAdapterInterface {

        void onFolderSelected(ListEventAlbumsResponse.Datum folder, int position);

        void onFolderSelectedForDeletion(List<Long> selectedElementIds);

        void onFolderDeselected();

        String getParentId();
    }

    @Override
    public int getItemCount() {
        return listGroupFoldersResponse.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cover_photo)
        ImageView coverPhoto;

        @BindView(R.id.album_name)
        TextView albumName;

        @BindView(R.id.txtCount)
        TextView txtCount;

        @BindView(R.id.album_author)
        TextView albumAuthor;
        @BindView(R.id.created_date)
        TextView createdDate;
        @BindView(R.id.selectionIndicator)
        ImageView selectionIndicator;

        @BindView(R.id.cvCount)
        CardView cvCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
